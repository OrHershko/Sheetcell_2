package impl;

import api.*;
import exception.CellOutOfBoundsException;
import exception.FileNotXMLException;
import exception.InvalidSheetSizeException;
import exception.RangeUsedInFunctionException;
import generated.STLCell;
import generated.STLSheet;
import impl.cell.Cell;
import impl.cell.value.*;
import impl.sheet.Sheet;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EngineImpl implements Engine {
    private static Sheet currentSheet;
    private final DTOFactory DTOFactory;
    private final String JAXB_XML_PACKAGE_NAME = "generated";
    private final int MAX_NUM_OF_ROWS = 50;
    private final int MAX_NUM_OF_COLUMNS = 20;

    public EngineImpl(DTOFactory DTOFactory) {
        this.DTOFactory = DTOFactory;
    }

    @Override
    public void loadFile(String filePath) throws IOException {
        STLSheet currentSTLSheet;
        checkIfFilePathValid(filePath);
        try{
            currentSTLSheet = buildSTLSheetFromXML(filePath);
        }
        catch (JAXBException e){
            throw new RuntimeException("Error: The file is not in the correct format.");
        }
        buildSheetFromSTLSheet(currentSTLSheet);
        Sheet.clearPreviousVersions();
    }

    private void checkIfFilePathValid(String filePath) throws FileNotFoundException, FileNotXMLException {
        File file = new File(filePath);
        if(!file.exists()){
            throw new FileNotFoundException("Error: File is not found in the file path " + filePath);
        }
        if(!file.getName().endsWith(".xml")){
            throw new FileNotXMLException();
        }
    }

    private STLSheet buildSTLSheetFromXML(String filePath)throws IOException, JAXBException{
        InputStream inputStream = new FileInputStream(filePath);
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_PACKAGE_NAME);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        return (STLSheet) unmarshaller.unmarshal(inputStream);
    }


    private void buildSheetFromSTLSheet(STLSheet currentSTLSheet) {
        checkDataValidity(currentSTLSheet);
        currentSheet = new Sheet();
        currentSheet.setName(currentSTLSheet.getName());
        currentSheet.setNumOfCols(currentSTLSheet.getSTLLayout().getColumns());
        currentSheet.setNumOfRows(currentSTLSheet.getSTLLayout().getRows());
        currentSheet.setColWidth(currentSTLSheet.getSTLLayout().getSTLSize().getColumnWidthUnits());
        currentSheet.setRowHeight(currentSTLSheet.getSTLLayout().getSTLSize().getRowsHeightUnits());
        currentSheet.setActiveCells(currentSTLSheet.getSTLCells().getSTLCell());
        currentSheet.setRangesFromFile(currentSTLSheet.getSTLRanges());
    }

    @Override
    public void addNewRange(String topLeftCell, String bottomRightCell, String rangeName) {
        int topLeftCellRow = Cell.getRowFromCellID(topLeftCell);
        int topLeftCellColumn = Cell.getColumnFromCellID(topLeftCell);
        int bottomRightCellRow = Cell.getRowFromCellID(bottomRightCell);
        int bottomRightCellColumn = Cell.getColumnFromCellID(bottomRightCell);
        currentSheet.checkIfRangeInBoundaries(topLeftCellRow, topLeftCellColumn, bottomRightCellRow, bottomRightCellColumn);
        Range newRange = new Range(rangeName, topLeftCell, bottomRightCell, currentSheet);
        currentSheet.addRange(newRange);
    }

    private void checkDataValidity(STLSheet currentSTLSheet) {
        checkSheetSize(currentSTLSheet.getSTLLayout().getRows(), currentSTLSheet.getSTLLayout().getColumns());
        checkCellsWithinBounds(currentSTLSheet);
    }

    private void checkSheetSize(int rows, int columns) {
        if (rows < 1 || rows > MAX_NUM_OF_ROWS || columns < 1 || columns > MAX_NUM_OF_COLUMNS) {
            throw new InvalidSheetSizeException("Error: The sheet size is not valid," +
                    String.format(" make sure that the number of rows is between 1 and %d and the number of columns is between 1 and %d.", MAX_NUM_OF_ROWS, MAX_NUM_OF_COLUMNS));
        }
    }

    private void checkCellsWithinBounds(STLSheet sheet) {
        int rowCount = sheet.getSTLLayout().getRows();
        int columnCount = sheet.getSTLLayout().getColumns();

        List<STLCell> cells = sheet.getSTLCells().getSTLCell();

        for (STLCell cell : cells) {
            int row = cell.getRow();
            String columnLetter = cell.getColumn();

            int column = convertColumnLetterToNumber(columnLetter);

            if (row < 1 || row > rowCount || column < 1 || column > columnCount) {
                throw new CellOutOfBoundsException("Error: A cell is defined outside the sheet boundaries: (" + row + ", " + columnLetter + ").");
            }
        }
    }

    private int convertColumnLetterToNumber(String columnLetter) {
        if (columnLetter == null || columnLetter.length() != 1 || !Character.isLetter(columnLetter.charAt(0))) {
            throw new IllegalArgumentException("Error: Invalid column letter: " + columnLetter);
        }

        return columnLetter.toUpperCase().charAt(0) - 'A' + 1;
    }

    @Override
    public DTO getSheetDTO() {
        checkForLoadedFile();
        return DTOFactory.createSheetDTO(currentSheet);
    }

    @Override
    public void checkForLoadedFile(){
        if(currentSheet == null)
        {
            throw new NullPointerException("Error: You must load a file to the system before performing this action.");
        }
    }

    @Override
    public DTO getCellDTO(String cellIdentity) {
        checkForLoadedFile();
        Cell currentCell = currentSheet.getCell(cellIdentity);
        if(currentCell == null)
            return DTOFactory.createEmptyCellDTO(cellIdentity);
        return DTOFactory.createCellDTO(currentSheet.getCell(cellIdentity));
    }

    @Override
    public boolean isCellInBounds(int row, int col) {
        checkForLoadedFile();
        return(row >= 0 && row < currentSheet.getNumOfRows() && col >= 0 && col < currentSheet.getNumOfCols());
    }

    @Override
    public void updateCellValue(String cellIdentity, CellValue value, String originalValue) {
        checkForLoadedFile();
        Sheet alternativeSheet = currentSheet.clone();
        List<Cell> topologicalOrder = alternativeSheet.sortActiveCellsTopologicallyByDFS();
        alternativeSheet.updateOrCreateCell(cellIdentity, value, originalValue, false);
        Cell updatedCell = alternativeSheet.getCell(cellIdentity);

        if(!topologicalOrder.contains(updatedCell))
            topologicalOrder.addLast(updatedCell);

        alternativeSheet.recalculateByTopologicalOrder(topologicalOrder);
        alternativeSheet.calculateChangedCells(updatedCell);
        Sheet.addToPreviousVersions(currentSheet);
        currentSheet = alternativeSheet;
    }

    public static CellValue convertStringToCellValue(String newValue) {
        CellValue cellValue;
        newValue = newValue.trim();

        // Check for Boolean
        if (newValue.equalsIgnoreCase("TRUE") || newValue.equalsIgnoreCase("FALSE")) {
            cellValue = new BooleanValue(Boolean.parseBoolean(newValue));
        }
        // Check for Numerical
        else if (newValue.matches("-?\\d+(\\.\\d+)?")) {
            try {
                double numericValue = Double.parseDouble(newValue);
                cellValue = new NumericValue(numericValue);
            }
            catch (NumberFormatException e) {
                throw new NumberFormatException("Error: Invalid numeric value.");
            }
        }
        // Check for Function
        else if (newValue.matches("\\{[A-Za-z]+(,([^,]*)?)*\\}")) {
            cellValue = new FunctionValue(newValue);
        }
        // Otherwise, treat as String
        else {
            cellValue = new StringValue(newValue);
        }

        return cellValue;
    }


    @Override
    public Map<Integer, DTO> getSheetsPreviousVersionsDTO() {
        checkForLoadedFile();
        Map<Integer,Sheet> previousVersions = currentSheet.getPreviousVersions();

        if(previousVersions.isEmpty()){
            throw new RuntimeException("There are no previous versions to look back at.");
        }

        Map<Integer, DTO> previousVersionsDTO = new TreeMap<>();

        for(Map.Entry<Integer,Sheet> entry : previousVersions.entrySet()) {
            previousVersionsDTO.put(entry.getKey(), DTOFactory.createSheetDTO(entry.getValue()));
        }

        return previousVersionsDTO;
    }

    private void checkIfFilePathIsDir(String filePath) throws IOException {
        File file = new File(filePath);

        if (file.isDirectory()) {
            throw new IOException("Error: The provided path is a directory, not a valid file path: " + filePath +
                    ". Make sure that the file path contains the file name.");
        }
    }

    @Override
    public void saveSheetToFile(String filePath) throws IOException {

        checkIfFilePathIsDir(filePath);

        if (!filePath.endsWith(".ser")) {
            filePath = filePath + ".ser";
        }
        try( FileOutputStream fileOut = new FileOutputStream(filePath);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)){
            out.writeObject(currentSheet);
        }
        catch (IOException e) {
            throw new IOException("Error: File cannot be saved in: " + filePath);
        }
    }

    @Override
    public void loadPreviousSheetFromFile(String filePath) throws IOException, ClassNotFoundException {

        checkIfFilePathIsDir(filePath);

        if (!filePath.endsWith(".ser")) {
            filePath = filePath + ".ser";
        }
        try (FileInputStream fileIn = new FileInputStream(filePath);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            currentSheet = (Sheet) in.readObject();
        }
        catch (IOException e) {
            throw new FileNotFoundException("Error: File not found in: " + filePath);
        }
        catch (ClassNotFoundException e){
            throw new ClassNotFoundException("Error: File failed to load from: " + filePath + ". Please make sure that the file is in the correct format.");
        }
    }

    @Override
    public void setNewRowsWidth(int width) {
        currentSheet.setRowHeight(width);
    }

    @Override
    public void setNewColsWidth(int width) {
        currentSheet.setColWidth(width);
    }

    @Override
    public DTO getRangeDTOFromSheet(String rangeName) {
        return DTOFactory.createRangeDTO(currentSheet.getRange(rangeName));
    }

    @Override
    public void deleteRangeFromSheet(String rangeName) {
        for(Cell cell : currentSheet.getActiveCells().values()) {
            if(cell.getEffectiveValue() instanceof FunctionValue functionValue) {
                if(functionValue.getFunctionType().equals(FunctionValue.FunctionType.AVERAGE) || functionValue.getFunctionType().equals(FunctionValue.FunctionType.SUM))
                {
                    if(functionValue.getArguments().getFirst().getEffectiveValue().equals(rangeName))
                    {
                        throw new RangeUsedInFunctionException("The range '" + rangeName + "cannot be deleted because it is used in a function.");
                    }
                }
            }
        }

        currentSheet.deleteRange(rangeName);

    }

    @Override
    public DTO getSortedSheetDTO(List<String> columnsToSortBy, String topLeft, String bottomRight) {
        checkForLoadedFile(); // Make sure that a sheet is loaded

        // 1. Create a list of rows where each row is a map entry (row number -> list of cells)
        List<Map.Entry<Integer, List<Cell>>> rowsList = new ArrayList<>();

        int topLeftRow = Cell.getRowFromCellID(topLeft);
        int topLeftCol = Cell.getColumnFromCellID(topLeft);
        int bottomRightRow = Cell.getRowFromCellID(bottomRight);
        int bottomRightCol = Cell.getColumnFromCellID(bottomRight);

        // Populate the list with row entries
        for (int row = topLeftRow; row <= bottomRightRow; row++) {
            List<Cell> cellsInRow = new ArrayList<>();
            for (int col = topLeftCol; col <= bottomRightCol; col++) {
                String cellID = Cell.getCellIDFromRowCol(row, col);
                Cell cell = currentSheet.getCell(cellID);
                if (cell != null) {
                    cellsInRow.add(cell);
                }
            }
            rowsList.add(Map.entry(row, cellsInRow));
        }

        // 2. Sort the rows by columns in reverse order (starting from the last column in columnsToSortBy)
        for (int i = columnsToSortBy.size() - 1; i >= 0; i--) {
            String columnToSortBy = columnsToSortBy.get(i).replace("Column ", "").trim();
            int colToSortBy = Cell.getColumnFromCellID(columnToSortBy + "1"); // Get column index

            // Sort the list of rows based on the current column
            rowsList.sort((entry1, entry2) -> {
                // Get the effective values for the column to sort by
                Cell cell1 = entry1.getValue().get(colToSortBy - topLeftCol);
                Cell cell2 = entry2.getValue().get(colToSortBy - topLeftCol);

                Comparable value1 = (Comparable) cell1.getEffectiveValue().getEffectiveValue();
                Comparable value2 = (Comparable) cell2.getEffectiveValue().getEffectiveValue();

                // Handle null values, ensuring nulls go to the end
                if (value1 == null && value2 == null) return 0;
                if (value1 == null) return 1; // Place nulls at the end
                if (value2 == null) return -1;

                // Perform the comparison
                return value1.compareTo(value2);
            });
        }

        // 3. Create the sorted DTO
        Sheet sortedSheet = currentSheet.clone(); // Create a copy of the current sheet

        int currentRow = topLeftRow;

        for (Map.Entry<Integer, List<Cell>> entry : rowsList) {
            List<Cell> cellsInRow = entry.getValue();
            int currentCol = topLeftCol;

            for (Cell cell : cellsInRow) {
                String newCellID = Cell.getCellIDFromRowCol(currentRow, currentCol);
                sortedSheet.updateOrCreateCell(newCellID, cell.getEffectiveValue(), cell.getOriginalValue(), false);

                currentCol++;
            }

            currentRow++;
        }
        // Return the sorted sheet as a DTO
        return DTOFactory.createSheetDTO(sortedSheet);
    }


}
