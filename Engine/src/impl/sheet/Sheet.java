package impl.sheet;

import api.CellValue;
import generated.STLCell;
import impl.EngineImpl;
import impl.cell.Cell;

import java.io.Serializable;
import java.util.*;

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


    @Override
    public Sheet clone(){
        Sheet sheet = new Sheet();
        sheet.name = name;
        sheet.version = version;
        sheet.numOfRows = numOfRows;
        sheet.numOfCols = numOfCols;
        sheet.rowHeight = rowHeight;
        sheet.colWidth = colWidth;
        for (Map.Entry<String, Cell> entry : activeCells.entrySet()) {
            String copiedKey = entry.getKey();
            Cell copiedValue = new Cell(sheet, entry.getValue());
            sheet.activeCells.put(copiedKey, copiedValue);
        }
        return sheet;
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
}
