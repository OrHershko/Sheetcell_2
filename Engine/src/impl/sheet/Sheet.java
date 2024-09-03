package impl.sheet;

import api.CellValue;
import generated.STLCell;
import generated.STLRange;
import generated.STLRanges;
import impl.EngineImpl;
import impl.Range;
import impl.cell.Cell;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Sheet implements Serializable {
    private String name;
    private int version = 1;
    private final Map<String, Cell> activeCells = new HashMap<>();
    private int numOfRows;
    private int numOfCols;
    private int rowHeight;
    private int colWidth;
    private int changedCellsCount = 0;
    private final static Map<Integer,Sheet> previousVersions = new HashMap<>();
    private final Map<String, Range> ranges = new HashMap<>();


    @Override
    public Sheet clone(){
        Sheet newSheet = new Sheet();
        newSheet.name = name;
        newSheet.version = version;
        newSheet.numOfRows = numOfRows;
        newSheet.numOfCols = numOfCols;
        newSheet.rowHeight = rowHeight;
        newSheet.colWidth = colWidth;
        cloneActiveCellsMap(newSheet);
        cloneRangesMap(newSheet);
        return newSheet;
    }

    private void cloneActiveCellsMap(Sheet newSheet) {
        for (Map.Entry<String, Cell> entry : activeCells.entrySet()) {
            String copiedKey = entry.getKey();
            Cell copiedValue = new Cell(newSheet, entry.getValue());
            newSheet.activeCells.put(copiedKey, copiedValue);
        }
    }

    private void cloneRangesMap(Sheet newSheet) {
        for (Map.Entry<String, Range> entry : ranges.entrySet()) {
            String copiedKey = entry.getKey();
            Range copiedValue = new Range(copiedKey, entry.getValue().getTopLeft(), entry.getValue().getBottomRight(),newSheet);
            newSheet.ranges.put(copiedKey, copiedValue);
        }
    }

    public int getChangedCellsCount() {
        return changedCellsCount;
    }

    public Map<Integer,Sheet> getPreviousVersions() {
        return previousVersions;
    }

    public Map<String,Cell> getActiveCells() {
        return activeCells;
    }

    public Cell getCell(String cellIdentity){
        return activeCells.get(cellIdentity);
    }

    public void updateOrCreateCell(String cellIdentity, CellValue value, String originalValue, boolean isFromFile) {
        Cell cell = getCell(cellIdentity);
        //If cell is not in active cells
        if (cell == null){
            createNewCell(cellIdentity, value, originalValue, isFromFile);
        }
        else{
            cell.updateValues(value,originalValue,isFromFile);
        }
        version++;
    }

    private void createNewCell(String cellIdentity, CellValue value, String originalValue, boolean isFromFile) {
        Cell cell = new Cell(this, cellIdentity);
        activeCells.put(cellIdentity, cell);
        cell.updateValues(value, originalValue, isFromFile);
    }



    public int getNumOfRows() {
        return numOfRows;
    }

    public void setNumOfRows(int numOfRows) {
        this.numOfRows = numOfRows;
    }

    public int getNumOfCols() {
        return numOfCols;
    }

    public void setNumOfCols(int numOfCols) {
        this.numOfCols = numOfCols;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getVersion(){
        return version;
    }

    public int getColWidth() {
        return colWidth;
    }

    public void setColWidth(int colWidth) {
        this.colWidth = colWidth;
    }

    public int getRowHeight() {
        return rowHeight;
    }

    public void setRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
    }

    public void setActiveCells(List<STLCell> stlCellsList) {
        for (STLCell stlCell : stlCellsList) {
            String cellIdentity = stlCell.getColumn().toUpperCase() + stlCell.getRow();
            String orgValue = stlCell.getSTLOriginalValue();
            createNewCell(cellIdentity, EngineImpl.convertStringToCellValue(orgValue), orgValue, true);
            changedCellsCount++;
        }
        List<Cell> topologicalOrder = sortActiveCellsTopologicallyByDFS();
        recalculateByTopologicalOrder(topologicalOrder);
    }

    public void detectCycleByDFS(){
        sortActiveCellsTopologicallyByDFS();
    }

    public List<Cell> sortActiveCellsTopologicallyByDFS() {
        List<Cell> topologicalOrder = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> inStack = new HashSet<>();
        Stack<Cell> stack = new Stack<>();

        for (String cellId : activeCells.keySet()) {
            if (!visited.contains(cellId)) {
                if (!iterativeDFS(activeCells.get(cellId), visited, inStack, stack, topologicalOrder)) {
                    throw new IllegalStateException("Error: Circular reference loop detected in spreadsheet.");
                }
            }
        }

        return topologicalOrder;
    }



    private boolean iterativeDFS(Cell startNode, Set<String> visited, Set<String> inStack, Stack<Cell> stack, List<Cell> topologicalOrder) {
        stack.push(startNode);

        while (!stack.isEmpty()) {
            Cell currentNode = stack.peek();

            if (!visited.contains(currentNode.getIdentity())) {
                visited.add(currentNode.getIdentity());
                inStack.add(currentNode.getIdentity());
            }

            boolean hasUnvisitedDependency = false;
            Cell currentCell = activeCells.get(currentNode.getIdentity());
            if (currentCell != null) {
                for (Cell dependency : currentCell.getCellsImDependentOn()) {
                    if (inStack.contains(dependency.getIdentity())) {
                        return false; //cycle
                    }

                    if (!visited.contains(dependency.getIdentity())) {
                        stack.push(dependency);
                        hasUnvisitedDependency = true;
                    }
                }
            }

            if (!hasUnvisitedDependency) {
                inStack.remove(currentNode.getIdentity());
                stack.pop();
                topologicalOrder.add(currentNode);
            }
        }

        return true;
    }

    public void recalculateByTopologicalOrder(List<Cell> topologicalOrder) {

        topologicalOrder.forEach(Cell::clearDependenciesLists);
        topologicalOrder.forEach(Cell::calculateEffectiveValue);
    }

    public static void addToPreviousVersions(Sheet sheet) {
        previousVersions.put(sheet.getVersion(),sheet);
    }


    public static void clearPreviousVersions() {
        previousVersions.clear();
    }

    public void calculateChangedCells(Cell updatedCell) {

        calculateCellsImInfluencing(updatedCell);
        changedCellsCount++;
    }

    public void calculateCellsImInfluencing(Cell updatedCell){

        if(updatedCell.getCellsImInfluencing().isEmpty()){
            return;
        }

        for(Cell cell : updatedCell.getCellsImInfluencing()) {
            changedCellsCount++;
            calculateCellsImInfluencing(cell);
        }
    }

    public List<Cell> getCellsInRange(String topLeft, String bottomRight) {

        int topLeftCol = topLeft.charAt(0) - 'A' + 1;
        int topLeftRow = Integer.parseInt(topLeft.substring(1));

        int bottomRightCol = bottomRight.charAt(0) - 'A' + 1;
        int bottomRightRow = Integer.parseInt(bottomRight.substring(1));

        checkIfRangeInBoundaries(topLeftRow, topLeftCol, bottomRightRow, bottomRightCol);

        return activeCells.entrySet().stream()
                .filter(entry -> {
                    String cellIdentity = entry.getKey();
                    int col = cellIdentity.charAt(0) - 'A' + 1;
                    int row = Integer.parseInt(cellIdentity.substring(1));

                    return (row >= topLeftRow && row <= bottomRightRow) &&
                            (col >= topLeftCol && col <= bottomRightCol);
                })
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    private void checkIfRangeInBoundaries(int topLeftRow, int topLeftCol, int bottomRightRow, int bottomRightCol) {
        if(topLeftRow > numOfRows || topLeftCol > numOfCols || bottomRightRow > numOfRows || bottomRightCol > numOfCols
           || topLeftCol <= 0 || bottomRightCol <= 0 || topLeftRow <= 0 || bottomRightRow <= 0){
            throw new RuntimeException("Error: A defined range falls outside the sheet boundaries. Please ensure all ranges are within the valid sheet area.");
        }
    }

    public void addRange(Range range) {
        ranges.put(range.getName(), range);
    }

    public void setRanges(STLRanges stlRanges) {

        for(STLRange stlRange: stlRanges.getSTLRange()){
            if(ranges.containsKey(stlRange.getName()))
            {
                throw new RuntimeException("Error: At least two ranges have been found with the same name. Each range must have a unique identifier.");
            }
            ranges.put(stlRange.getName(),new Range(stlRange.getName(),stlRange.getSTLBoundaries().getFrom(),stlRange.getSTLBoundaries().getTo(),this));
        }
    }
}
