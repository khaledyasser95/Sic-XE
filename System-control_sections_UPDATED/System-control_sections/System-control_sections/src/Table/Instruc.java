package Table;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ICY on 3/13/2017.
 */

public class Instruc {
    private static final Map<String, OPERATION> OPERATIONTable;
    private static final Map<String, Integer> registerTable;

    static {
        //Operations can be done
        OPERATIONTable = new HashMap<>();
        registerTable = new HashMap<>();
        //operation arguments: mnemonic,format,opcode
        //Q: why not put 3/4 format operations as two different ones in map with same key?
        OPERATIONTable.put("ADD", new OPERATION("ADD", "3", "18"));
        OPERATIONTable.put("ADDF", new OPERATION("ADDF", "3/4", "58"));
        OPERATIONTable.put("ADDR", new OPERATION("ADDR", "2", "90"));
        OPERATIONTable.put("AND", new OPERATION("AND", "3/4", "40"));
        OPERATIONTable.put("DIV", new OPERATION("DIV", "3/4", "24"));
        OPERATIONTable.put("DIVF", new OPERATION("DIVF", "3/4", "64"));
        OPERATIONTable.put("DIVR", new OPERATION("DIVR", "2", "9C"));
        OPERATIONTable.put("FIX", new OPERATION("FIX", "1", "C4"));
        OPERATIONTable.put("FLOAT", new OPERATION("FLOAT", "1", "C0"));
        OPERATIONTable.put("HIO", new OPERATION("HIO", "1", "F4"));
        OPERATIONTable.put("JGT", new OPERATION("JGT", "3/4", "34"));
        OPERATIONTable.put("CLEAR", new OPERATION("CLEAR", "2", "B4"));
        OPERATIONTable.put("COMP", new OPERATION("COMP", "3/4", "28"));
        OPERATIONTable.put("COMPR", new OPERATION("COMPR", "2", "A0"));
        OPERATIONTable.put("J", new OPERATION("J", "3/4", "3C"));
        OPERATIONTable.put("JEQ", new OPERATION("JEQ", "3/4", "30"));
        OPERATIONTable.put("JLT", new OPERATION("JLT", "3/4", "38"));
        OPERATIONTable.put("JSUB", new OPERATION("JSUB", "3/4", "48"));
        OPERATIONTable.put("LDA", new OPERATION("LDA", "3/4", "00"));
        OPERATIONTable.put("LDB", new OPERATION("LDB", "3/4", "68"));
        OPERATIONTable.put("LDCH", new OPERATION("LDCH", "3/4", "50"));
        OPERATIONTable.put("LDF", new OPERATION("LDF", "3/4", "70"));
        OPERATIONTable.put("LDL", new OPERATION("LDL", "3/4", "08"));
        OPERATIONTable.put("LDS", new OPERATION("LDS", "3/4", "6C"));
        OPERATIONTable.put("LDT", new OPERATION("LDT", "3/4", "74"));
        OPERATIONTable.put("LDX", new OPERATION("LDX", "3/4", "04"));
        OPERATIONTable.put("LPS", new OPERATION("LPS", "3/4", "D0"));
        OPERATIONTable.put("MUL", new OPERATION("MUL", "3/4", "20"));
        OPERATIONTable.put("MULF", new OPERATION("MULF", "3/4", "60"));
        OPERATIONTable.put("MULR", new OPERATION("MULR", "2", "68"));
        OPERATIONTable.put("NORM", new OPERATION("NORM", "1", "C8"));
        OPERATIONTable.put("OR", new OPERATION("OR", "1", "44"));
        OPERATIONTable.put("RD", new OPERATION("RD", "3/4", "D8"));
        OPERATIONTable.put("RMO", new OPERATION("RMO", "2", "AC"));
        OPERATIONTable.put("RSUB", new OPERATION("RSUB", "3/4", "4C"));
        OPERATIONTable.put("SHIFTL", new OPERATION("SHIFTL", "2", "A4"));
        OPERATIONTable.put("SHIFTR", new OPERATION("SHIFTR", "2", "A8"));
        OPERATIONTable.put("SIO", new OPERATION("SIO", "1", "F0"));
        OPERATIONTable.put("SSK", new OPERATION("SSK", "3/4", "EC"));
        OPERATIONTable.put("STA", new OPERATION("STA", "3/4", "0C"));
        OPERATIONTable.put("STB", new OPERATION("STB", "3/4", "78"));
        OPERATIONTable.put("STCH", new OPERATION("STCH", "3/4", "54"));
        OPERATIONTable.put("STF", new OPERATION("STF", "3/4", "80"));
        OPERATIONTable.put("STI", new OPERATION("STI", "3/4", "D4"));
        OPERATIONTable.put("STL", new OPERATION("STL", "3/4", "14"));
        OPERATIONTable.put("STX", new OPERATION("STX", "3/4", "10"));
        OPERATIONTable.put("STS", new OPERATION("STS", "3/4", "7C"));
        OPERATIONTable.put("STSW", new OPERATION("STSW", "3/4", "E8"));
        OPERATIONTable.put("SUB", new OPERATION("SUB", "3/4", "1C"));
        OPERATIONTable.put("SUBF", new OPERATION("SUBF", "3/4", "5C"));
        OPERATIONTable.put("SUBR", new OPERATION("SUBR", "2", "94"));
        OPERATIONTable.put("SVC", new OPERATION("SVC", "2", "B0"));
        OPERATIONTable.put("STT", new OPERATION("STT", "3/4", "84"));
        OPERATIONTable.put("TD", new OPERATION("TD", "3/4", "E0"));
        OPERATIONTable.put("TIO", new OPERATION("TIO", "1", "F8"));
        OPERATIONTable.put("TIX", new OPERATION("TIX", "3/4", "2C"));
        OPERATIONTable.put("TIXR", new OPERATION("TIXR", "2", "B8"));
        OPERATIONTable.put("WD", new OPERATION("WD", "3/4", "DC"));
        //SIC REGISTERS WITH NUMBER TO IDENTIFY
        //Q: why did we put null at first one?
        registerTable.put(null, 0);
        registerTable.put("A", 0);
        registerTable.put("X", 1);
        registerTable.put("L", 2);
        registerTable.put("B", 3);
        registerTable.put("S", 4);
        registerTable.put("T", 5);
        registerTable.put("F", 6);
        registerTable.put("SW", 9);
    }

    public static Map<String, OPERATION> getOPERATIONTable() {
        return OPERATIONTable;
    }

    public static Map<String, Integer> getRegisterTable() {
        return registerTable;
    }
}
