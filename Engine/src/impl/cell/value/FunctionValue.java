package impl.cell.value;

import api.CellValue;
import exception.WrongParenthesesOrderException;
import impl.EngineImpl;
import impl.cell.Cell;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class FunctionValue implements CellValue {
    private final FunctionType functionType;
    private final List<CellValue> arguments = new ArrayList<>();
    private Object effectiveValue;
    private Cell activatingCell;

    public FunctionValue(String functionDefinition) {
        List<String> argsStr = extractArguments(functionDefinition);
        functionType = parseFunctionType(argsStr.getFirst().toUpperCase());
        for (String argument : argsStr.subList(1, argsStr.size())) {
            CellValue value = EngineImpl.convertStringToCellValue(argument);
            value.setActivatingCell(activatingCell);
            arguments.add(value);
        }
    }

    public void calculateAndSetEffectiveValue(){
        try{
            effectiveValue = eval();
        }
        catch(ArithmeticException e){
            effectiveValue = "NaN";
        }
    }

    public void setActivatingCell(Cell cell) {
        this.activatingCell = cell;
        for (CellValue argument : arguments) {
            argument.setActivatingCell(activatingCell);
        }
    }

    public static List<String> extractArguments(String input) {
        List<String> arguments = new ArrayList<>();
        int level = 0;
        int start = 0;
        boolean insideArgument = false;

        for (int i = 0; i < input.length() && level >= 0; i++) {
            char c = input.charAt(i);

            if (c == '{') {
                if (level == 0) {
                    start = i;
                }
                level++;
            }
            else if (c == '}') {
                level--;
                if (level == 0) {
                    if(start != i)
                        arguments.add(input.substring(start, i));
                    insideArgument = false;
                }
                else{
                    insideArgument = true;
                }
            }
            else if (c == ',' && level == 1) {
                if (insideArgument) {
                    arguments.add(input.substring(start, i));
                }
                start = i + 1;
                insideArgument = false;
            } else if (level == 1 && !insideArgument) {
                start = i;
                insideArgument = true;
            }
        }

        if (insideArgument) {
            arguments.add(input.substring(start));
        }

        if(level != 0)
        {
            throw new WrongParenthesesOrderException();
        }

        return arguments;
    }

    @Override
    public Object eval() {
        switch (functionType) {
            case PLUS:
            case MINUS:
            case TIMES:
            case DIVIDE:
            case MOD:
            case POW:
                try {
                    checkNumOfArguments(2, "2 arguments");
                    double arg1 = (double) arguments.get(0).eval();
                    double arg2 = (double) arguments.get(1).eval();
                    return functionType.apply(arg1, arg2);
                }
                catch (ClassCastException e) {
                    throw new RuntimeException(String.format("Error: One or more arguments are not valid. Ensure that all inputs for this function are numeric, e.g. {%s,4,5}.", functionType.name()));
                }
            case ABS:
                try {
                    checkNumOfArguments(1, "1 argument");
                    double arg = (double) arguments.getFirst().eval();
                    return functionType.apply(arg);
                }
                catch (ClassCastException e) {
                    throw new RuntimeException("Error: argument is not valid. Ensure that the input argument is numeric, e.g. {ABS,3}.");
                }
            case CONCAT:
                checkNumOfArguments(2, "2 arguments");
                try {
                    String str1 = (String) arguments.get(0).eval();
                    String str2 = (String) arguments.get(1).eval();
                    return functionType.apply(str1, str2);
                }
                catch (ClassCastException e) {
                    throw new RuntimeException("Error: One or more arguments are not valid. Ensure that all arguments are correctly formatted as text, e.g. {CONCAT,HELLO,WORLD}.");
                }
            case SUB:
                checkNumOfArguments(3, "3 arguments");
                try {
                    String str = (String) arguments.get(0).eval();
                    int idx1 =  ((Double) arguments.get(1).eval()).intValue();
                    int idx2 =  ((Double) arguments.get(2).eval()).intValue();
                    return functionType.apply(str,idx1,idx2);
                }
                catch (ClassCastException e) {
                    throw new RuntimeException("Error: One or more arguments are not valid. Ensure that all arguments are correctly formatted as (source-text, start-index, end-index)  e.g. {SUB,HELLO,1,3}.");
                }

            case REF:
                try {
                    String str = (String) arguments.getFirst().eval();
                    return functionType.apply(str, activatingCell).eval();
                }
                catch (ClassCastException e) {
                        throw new RuntimeException("Error: argument is not valid. Ensure that the input argument is a cell identity, e.g. {REF,A4}.");
                    }
        }
        return null;
    }

    public enum FunctionType {
        PLUS {
            @Override
            public double apply(double arg1, double arg2) {
                return arg1 + arg2;
            }
        },
        MINUS {
            @Override
            public double apply(double arg1, double arg2) {
                return arg1 - arg2;
            }
        },
        TIMES {
            @Override
            public double apply(double arg1, double arg2) {
                return arg1 * arg2;
            }

        },
        MOD{
            @Override
            public double apply(double arg1, double arg2) {
                return arg1 % arg2;
            }
        },
        DIVIDE {
            @Override
            public double apply(double arg1, double arg2) {
                if (arg2 == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return arg1 / arg2;
            }
        },
        POW {
            @Override
            public double apply(double arg1, double arg2) {
                if(arg1 == 0 && arg2 < 0){
                    throw new ArithmeticException("Division by zero");
                }
                return Math.pow(arg1, arg2);
            }
        },
        ABS{
            @Override
            public double apply(double arg) {
                return Math.abs(arg);
            }
        },
        CONCAT {
            @Override
            public String apply(String str1, String str2) {
                return str1 + str2;
            }
        },
        SUB {
            @Override
            public String apply(String source, int startIndex, int endIndex) {
                if (startIndex < 0 || endIndex >= source.length() || startIndex > endIndex) {
                    return "!UNDEFINED!";
                }
                return source.substring(startIndex, endIndex + 1);
            }
        },
        REF {
            @Override
            public CellValue apply(String cellId, Cell activatingCell){

                if(!isStringInCellIdentityFormat(cellId)){
                    throw new IllegalStateException("Error: " + cellId + " is not a cell identity. Ensure that the argument is in the right format (e.g., {REF,A4}).");
                }

                Cell referancedCell = activatingCell.getSheet().getCell(cellId.toUpperCase());

                if(referancedCell == null)
                {
                    throw new NullPointerException("Error: Cell " + cellId + " cannot be referenced. Please ensure the cell is within the sheet boundaries and contains a value.");
                }

                activatingCell.getCellsImDependentOn().add(referancedCell);
                referancedCell.getCellsImInfluencing().add(activatingCell);

                try {
                    activatingCell.getSheet().detectCycleByDFS();
                }
                catch (IllegalStateException e)
                {
                    throw new IllegalStateException(e.getMessage());
                }

                return referancedCell.getEffectiveValue();
            }
        };

        public double apply(double arg1, double arg2) {
            throw new UnsupportedOperationException("Error: This function does not support numeric operations");
        }

        public String apply(String str1, String str2) {
            throw new UnsupportedOperationException("Error: This function does not support string concatenation");
        }

        public String apply(String source, int startIndex, int endIndex) {
            throw new UnsupportedOperationException("Error: This function does not support substring operations");
        }

        public double apply(double arg) {
            throw new UnsupportedOperationException("Error: This function does not support numeric operations");
        }

        public CellValue apply(String cellId, Cell activatingCell) {
            throw new UnsupportedOperationException("Error: This function does not support referring operations");
        }
    }

    private static boolean isStringInCellIdentityFormat(String str){
        if (str == null || str.length() < 2) {
            return false;
        }

        char firstChar = str.charAt(0);
        String restOfString = str.substring(1);

        if (!Character.isLetter(firstChar)) {
            return false;
        }

        try {
            Integer.parseInt(restOfString);
        } catch (NumberFormatException e) {
            return false; // Not a valid number
        }

        return true;
    }

    private void checkNumOfArguments(int numOfArgumentsExp, String numArgsStr) throws IllegalArgumentException {
        if (arguments.size() != numOfArgumentsExp) {
            throw new IllegalArgumentException("Error: Function " + functionType.name() + " expected " + numArgsStr + ", got " + arguments.size() + ".");
        }
    }

    @Override
    public Object getEffectiveValue() {
        if(effectiveValue instanceof Double num)
        {
            return convertToIntIfWholeNumber(num);
        }

        return effectiveValue;
    }

    public String convertToIntIfWholeNumber(Double value) {
        if (value % 1 == 0) {
            return String.format("%,d", value.longValue());
        } else {
            return String.format("%,.2f", value);
        }
    }

    private FunctionType parseFunctionType(String functionName) {
        try {
            return FunctionType.valueOf(functionName);
        }
        catch (IllegalArgumentException e) {
            throw new RuntimeException("Error: Invalid function definition: " + functionName);
        }
    }

    @Override
    public Cell getActivatingCell() {
        return activatingCell;
    }

    @Override
    public FunctionValue clone(){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);

            out.writeObject(this);
            out.flush();
            out.close();

            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream in = new ObjectInputStream(bis);

            return (FunctionValue) in.readObject();
        }
        catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Clone failed", e);
        }
    }
}
