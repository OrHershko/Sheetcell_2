package dto;

import api.DTO;
import api.DTOFactory;
import impl.cell.Cell;
import impl.sheet.Sheet;

public class DTOFactoryImpl implements DTOFactory {
    @Override
    public DTO createSheetDTO(Sheet sheet) {
        return new SheetDTO(sheet);
    }

    @Override
    public DTO createCellDTO(Cell cell) {
        return new CellDTO(cell);
    }

    @Override
    public DTO createEmptyCellDTO() {
        return new CellDTO();
    }
}