package api;

import impl.cell.Cell;
import impl.sheet.Sheet;

public interface DTOFactory {
    DTO createSheetDTO(Sheet sheet);
    DTO createCellDTO(Cell cell);
    DTO createEmptyCellDTO();
}
