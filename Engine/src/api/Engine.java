package api;


import impl.Range;

import java.io.IOException;
import java.util.Map;

public interface Engine {
    void loadFile(String filePath) throws IOException;
    DTO getSheetDTO();
    boolean isCellInBounds(int row, int col);
    void updateCellValue(String cellIdentity, CellValue value, String originalValue);
    DTO getCellDTO(String cellIdentity);
    void checkForLoadedFile();
    Map<Integer,DTO> getSheetsPreviousVersionsDTO();
    void saveSheetToFile(String filePath) throws IOException;
    void loadPreviousSheetFromFile(String filePath) throws IOException, ClassNotFoundException;
    void setNewRowsWidth(int width);
    void setNewColsWidth(int width);
    void addNewRange(String topLeftCell, String bottomRightCell, String rangeName);
    Range getRangeFromSheet(String rangeName);
}
