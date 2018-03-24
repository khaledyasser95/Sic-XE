//latest
package Table;
//just a first comment to try commits

import Table.Objectcode.*;

import java.io.*;
import java.util.*;

/**
 * Created by ICY on 3/14/2017.
 */
public class Assembler {
    private final Map<String, OPERATION> opTable;
    private final Map<String, Integer> registerTable;
    //Q:is making symbol table final a correct move ? this means it can only be intialized in the constructor once
    //if we are going to do that then we need to put the constructor in pass 1
    // private final Map<String, Integer> symbolTable;
    private final Map<String, Character> type;
    private final Map<String, String> repo;
    ArrayList<ControlSection> controlSections;
    ControlSection currentCS;
    ControlSection currentCS2;
    int add;
    int res = 0;
    private int location;
    private int old_loc;
    private int startAddress;
    private int CSstartAddress;
    private int firstExecAddress;
    private int Length;
    private int baseAddress = 0;

    public Assembler() {
        //Pointing the optable to the operation in the instruction class
        opTable = Instruc.getOPERATIONTable();
        //Pointing the registertable to the register inside the instruction class
        registerTable = Instruc.getRegisterTable();
        // symbolTable = new HashMap<>();
        type = new HashMap<>();
        repo = new HashMap<>();
        //Initializing the symbol table
        //symbolTable.put(null, 0);
        type.put(null, ' ');
        repo.put(null, null);
        currentCS = new ControlSection();
        currentCS2 = new ControlSection();
        controlSections = new ArrayList<>(5);
        controlSections.add(currentCS);

    }

    public static void main(String args[]) {
        try {
            Assembler assembler = new Assembler();
            //  File assembly = new File ("copy.asm");
            assembler.run(new File("input_fibonacci.txt"), new File("Listing.txt"), new File("symb.txt"), new File("HTMI.o"));

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static boolean isNumeric(String str) {
        try {
            Integer d = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * THIS METHOD READ THE INPUT FILE AND SEND IT TO BE PASSED TO PASS 1 AND PASS2 THEN RETURN THE OUTPUT INTO FILES
     *
     * @param input   ASSEMBLY FILE
     * @param output  LISTING FILE
     * @param output2 SYMBOL TABLE FILE
     * @param output3 HTMI FILE
     * @throws IOException            CHECK IF FILE ERROR
     * @throws ClassNotFoundException IF CLASS IS NOT AVAILABLE
     */
    void run(File input, File output, File output2, File output3) throws IOException, ClassNotFoundException {
        //creating temporary file to take the output from pass 1 to be translated to object code in pass 2
        File intermediateFile = new File(".assembler.tmp");
        try {
            intermediateFile.createNewFile();
            pass1(input, output, output2, intermediateFile);

            pass2(intermediateFile, output3);
        } finally {
            intermediateFile.delete();
        }
    }

    /**
     * PASS 1: READ EACH STATEMENT IN FILE AND DETECT TYPE OF STATEMENT
     * IF LABEL: ITS ADDED TO SYMBOL TABLE WITH ITS LOCATION AND CHECK IF ITS NOT DUPLICATED  AND PRINT THE VALUES INSIDE THE SYMBOLTABLE FILE
     * IF COMMENT: THEN ITS IGNORED
     * OPERATION : CHECK THE OPERATION IF AVAILABLE THEN IF NORMAL OPERATION (DEFAULT) THEN IT CHECKS THE FORMAT
     * IT PRINT INSIDE THE LISTING FILE
     * RETURN OBJECT INTO INTERMEDIATE FILE
     *
     * @param input   ASSEMBLY FILE
     * @param output  LISTING FILE
     * @param output2 SYMBOL TABLE
     * @param output3 INTERMEDIATE FILE
     * @throws IOException FILE ERROR
     */


    void pass1(File input, File output, File output2, File output3) throws IOException {

        try (Scanner scanner = new Scanner(input);
             FileOutputStream ostream = new FileOutputStream(output);
             FileOutputStream ostream3 = new FileOutputStream(output3);
             //OBJECT OUTPUT must get from a Serializable class
             //OOS is for writing an object into a file
             ObjectOutputStream objOutputStream = new ObjectOutputStream(ostream3);

             FileOutputStream ostream2 = new FileOutputStream(output2);
             PrintWriter y = new PrintWriter(ostream2);
             PrintWriter x = new PrintWriter(ostream)


        )//all past are parameters for this try block
        //try block:
        {
            CSstartAddress = location = startAddress = old_loc = 0000;
            firstExecAddress = -1;
            //while not end of file
            while (scanner.hasNext()) {
                try {//try reading lines from file
                    //read each line and parse it into a statement
                    Statement statement = Statement.parse(scanner.nextLine(), location);

                    statement.setCS(currentCS);

                    if (statement.isComment()) {
                        continue;
                    }
                    statement.setLocation(location);
                    //check Duplication of label
                    if (statement.label() != null) {
                        if (currentCS.symbolTable.containsKey(statement.label()) && !statement.operation().equals("CSECT")) {
                            throw new Duplicate(statement);
                        } else {
                            /**
                             * EQUATE IS HERE TO CHANGE IN SYMBOL TABLE
                             */

                           /* if (statement.operation().equals("CSECT")) {
                                update(y);
                                System.out.println("=====================csect=======================");
                                CSstartAddress = location = 0;
                                firstExecAddress = -1;
                                statement.setLocation(location);
                                //make a new control section with the name as the label
                                currentCS = new ControlSection(statement.label());
                                controlSections.add(currentCS);
                                //todo not sure
                                statement.setCS(currentCS);

                            }*/


                            if (statement.operation().equals("EQU")) {
                                String Lablval = repo.get(statement.label());

                                if (statement.isExpression()) {
                                    String typex = Expression(statement);
                                    if (typex.equals("A")) {
                                        statement.chracter = 'A';
                                        add = res;
                                        currentCS.symbolTable.put(statement.label(), add);
                                        currentCS.symbolTable.put(Lablval, add);
                                        type.put(Lablval, statement.chracter);
                                        type.put(statement.label(), statement.chracter);
                                    } else if (typex.equals("R")) {
                                        statement.chracter = 'R';
                                        add = res;
                                        currentCS.symbolTable.put(statement.label(), add);
                                        currentCS.symbolTable.put(Lablval, add);
                                        type.put(Lablval, statement.chracter);
                                        type.put(statement.label(), statement.chracter);
                                    } else
                                        throw new WrongOperation(statement);
                                } else if (!isNumeric(statement.operand1()) && statement.operand1().charAt(0) != '*') {
                                    //getting address of the label from symbol table
                                    try {
                                        add = currentCS.symbolTable.get(statement.operand1());
                                    } catch (Exception F) {
                                        throw new Forward(statement);
                                    }
                                    currentCS.symbolTable.put(statement.label(), add);
                                    //symbolTable.put(Lablval, add);
                                    type.put(Lablval, statement.chracter);
                                    type.put(statement.label(), statement.chracter);
                                } else if (statement.operand1().charAt(0) == '*') {
                                    // operand = *
                                    add = location;
                                    currentCS.symbolTable.put(statement.label(), add);
                                    currentCS.symbolTable.put(Lablval, add);
                                    type.put(Lablval, statement.chracter);
                                    type.put(statement.label(), statement.chracter);
                                } else if (isNumeric(statement.operand1())) {
                                    statement.chracter = 'A';
                                    add = Integer.parseInt(statement.operand1());
                                    currentCS.symbolTable.put(statement.label(), add);
                                    currentCS.symbolTable.put(Lablval, add);
                                    type.put(Lablval, statement.chracter);
                                    type.put(statement.label(), statement.chracter);
                                }
                            } else
                                currentCS.symbolTable.put(statement.label(), location);
                            type.put(statement.label(), statement.chracter);
                            //made it print hexa:
                            repo.put(statement.operand1(), statement.label());

                            if (statement.chracter == 'A' && !statement.operation().equals("EQU")) {
                                String xx = String.format("%-10s  %-10s %s", statement.label(), statement.operand1(), statement.chracter);
                                y.println(xx);

                            } else if (statement.operation().equals("EQU")) {
                                String xx = null;
                                if (statement.chracter == 'A')
                                    xx = String.format("%-10s  %-10s %s", statement.label(), add, statement.chracter);
                                else {
                                    xx = String.format("%-10s  %-10s %s", statement.label(), Integer.toHexString(add).toUpperCase(), statement.chracter);

                                }
                                y.println(xx);
                            } else {
                                String xx = String.format("%-10s  %-10s %s", statement.label(), Integer.toHexString(location).toUpperCase(), statement.chracter);
                                y.println(xx);
                            }
                        }
                    }
                    //check operation
                    boolean error = false;
                    switch (statement.operation()) {

                        case "START"://TODO : integer after start is hexa decimal not decimal
                            //K made radix =16 because it is a hexa decimal number
                            //startAddress = Integer.parseInt(statement.operand1(), 16);
                            CSstartAddress = startAddress = Integer.parseInt(statement.operand1(), 16);
                            currentCS.setName(statement.label());
                            statement.setLocation(location = startAddress);
                            //System.out.println("===================start=========================");
                            break;
                        case "BYTE":
                            String s = statement.operand1();

                            switch (s.charAt(0)) {
                                case 'C':
                                    location += (s.length() - 3); // C'EOF' -> EOF -> 3 bytes

                                    break;
                                case 'X':
                                    location += (s.length() - 3) / 2; // X'05' -> 05 -> 2 half bytes

                                    break;
                            }
                            break;

                        case "WORD":
                            location += 3;

                            break;
                        case "RESW":

                            for (int i = 0; i < statement.operand1().length(); i++) {
                                if (!Character.isLetterOrDigit(statement.operand1().charAt(i)))
                                    System.out.println("can't use this symbol   " + statement.operand1().charAt(i) + "  in operation: " + statement.operand1());
                                error = true;
                            }
                            if (!error) {
                                location += 3 * Integer.parseInt(statement.operand1());

                            } else {
                                location += 3;

                            }
                            break;
                        case "RESB":

                            for (int i = 0; i < statement.operand1().length(); i++) {

                                if (!Character.isLetterOrDigit(statement.operand1().charAt(i)))
                                    System.out.println("can't use this symbol   " + statement.operand1().charAt(i) + "  in operation: " + statement.operand1());
                                error = true;
                            }

                            location += Integer.parseInt(statement.operand1());

                            break;


                        case "END":
                            currentCS.length = location;
                            break;
                        case "CSECT":
                            //control section names are concidered as external symbols
                            //   currentCS.externallyDefined.add(statement.label());
                            //set location of new control section to zero
                            currentCS.length = location;
                            update(y);
                         //   System.out.println("=====================csect=======================");
                            CSstartAddress = location = 0;
                            firstExecAddress = -1;
                            statement.setLocation(location);
                            //make a new control section with the name as the label

                            currentCS = new ControlSection(statement.label());
                            controlSections.add(currentCS);
                            //todo not sure
                            statement.setCS(currentCS);
                            currentCS.symbolTable.put(statement.label(),0);

                            break;
                        case "EXTDEF":

                            //statement.setLocation(location);
                            int i = 0;

                            try {
                                while (i < 8 && !statement.Symbols[i].equalsIgnoreCase("kiko") && statement.Symbols[i] != null) {
                                    currentCS.externallyDefined.add(statement.Symbols[i]);
                                    i++;
                                }
                            } catch (Exception xx) {
                               // System.out.println(xx.getMessage());
                            }


                            break;
                        case "EXTREF":

                            //statement.setLocation(location);
                            //TODO: check if they are externally defined in a control section befor adding them to symtab
                            //why did  I start i at 2 ?


                            int l = 0;
                            String operand = statement.Symbols[l];
                            //loop all control sections to check if each label beside extrefrence
                            //is externally defined in a control section
                            //if so add it to symbol tabel of control section
                            try {
                                while (l < 8 && !operand.equalsIgnoreCase("kiko") && operand != null) {

                                    //give it value zero as address
                                    //  System.out.println("putting "+operand+" in table currnent csect is "+currentCS.name);
                                    //I put location instead of 0 as second argument
                                    currentCS.symbolTable.put(operand, location);
                                    currentCS.externalReferences.add(operand);
                                    l++;
                                    operand = statement.Symbols[l];
                                }
                            } catch (Exception e) {
                              //  System.out.println(e.getMessage());
                            }

                            //TODO cancel this check bec if not yet defined CS
                              /*  for (ControlSection controlSection : controlSections) {

                                    cs = controlSection;

                                    if (cs.searchExtDef(operand) != null && !Objects.equals(operand, "kiko") && operand != null) {
                                        //give it value zero as address
                                        System.out.println("putting "+operand+" in table currnent csect is "+currentCS);
                                        currentCS.symbolTable.put(operand, 0);
                                    }
                                }
                              */


                            break;


                        case "BASE":
                            break;
                        case "NOBASE":
                            break;
                        case "EQU":
                            break;
                        case "ORG":

                            if (isNumeric(statement.operand1())) {
                                old_loc = location;
                                location = Integer.parseInt(statement.operand1(), 16);
                                statement.setLocation(location);
                            } else if (statement.operand1() == null) {
                                location = old_loc;
                            } else System.out.println("ERROR");
                            break;

                        //not a directive
                        default:
                            if (opTable.containsKey(statement.operation())) {
                                //if first Executable address is not set yet , set it
                                if (firstExecAddress < 0) {
                                    firstExecAddress = location;
                                }

                                switch (opTable.get(statement.operation()).getFormat()) {
                                    case "1":
                                        location += 1;
                                        break;
                                    case "2":
                                        location += 2;
                                        break;
                                    case "3/4":
                                        //if e=1 then Format 4 then 3+1=4 BYTE
                                        location += 3 + (statement.isExtended() ? 1 : 0);
                                        break;
                                }
                            } else {
                                throw new WrongOperation(statement);
                            }
                    }

                     /* if(statement.operation().compareTo("CSECT")==0)
                    {
                        break;
                    }*/

                    x.println(statement);
                    objOutputStream.writeObject(statement);
                } catch (Duplicate | WrongOperation | Forward e) {
                    x.println(e.getMessage());
                    y.println(e.getMessage());
                }
                //Length = location - startAddress;
                Length = location - CSstartAddress;

            }
            y.println("-----------------------UPDATED SYMBOL TABLE WITH EQU----------------------");

            for (Map.Entry<String, Integer> entry : currentCS.symbolTable.entrySet()) {
                String key = entry.getKey();
                int value = entry.getValue();
                String print = null;
                try {
                    if (type.get(key) == 'A') {
                        //  y.println(key +"    " + value+ " A");
                        print = String.format("%-10s  %-10s %s", key, value, "A");
                    } else
                        print = String.format("%-10s  %-10s %s", key, Integer.toHexString(value).toUpperCase() + " H", "R");
                    y.println(print);
                    //  y.println(key +"    " + Integer.toHexString(value).toUpperCase()+ " R");
                    //System.out.println ("Key: " + key + " Value: " + value);
                    //y.println(key +"    " + value);
                } catch (Exception xx) {
                  //  System.out.println("" + xx.getMessage());
                }

            }


        }//end try
    }

    public void update(PrintWriter y) {
        y.println("-----------------------UPDATED SYMBOL TABLE WITH EQU----------------------");

        for (Map.Entry<String, Integer> entry : currentCS.symbolTable.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();
            String print = null;
            try {
                if (type.get(key) == 'A') {
                    //  y.println(key +"    " + value+ " A");
                    print = String.format("%-10s  %-10s %s", key, value, "A");
                } else
                    print = String.format("%-10s  %-10s %s", key, Integer.toHexString(value).toUpperCase() + " H", "R");
                y.println(print);
                //  y.println(key +"    " + Integer.toHexString(value).toUpperCase()+ " R");
                //System.out.println ("Key: " + key + " Value: " + value);
                //y.println(key +"    " + value);
            } catch (Exception xx) {
              //  System.out.println("" + xx.getMessage());
            }

        }
    }

    /**
     * @param input  READ THE ASSEMBLY FILE
     * @param output THE HTMI FILE
     * @throws IOException
     * @throws ClassNotFoundException
     */
    //TODO : null pointer exception is because of "BASE" instn

    void pass2(File input, File output) throws IOException, ClassNotFoundException {
        //input file here is intermediate file which is listing file which is one that saves statement objects
        //output file is the HTME file
        /*for(int i=0;i<controlSections.size();i++)
        {
            currentCS=controlSections.get(i);
            for (String s:currentCS.symbolTable.keySet()) {
                System.out.printf("%s:%s->%X\n",currentCS.name,s,currentCS.symbolTable.get(s));
            }

        }*/
        try (FileInputStream istream = new FileInputStream(input);
             ObjectInputStream objInputStream = new ObjectInputStream(istream);
             FileWriter objectProgram = new FileWriter(output)

        )

        {

            List<RecordCollector> mRecords = new ArrayList<>();
            /*TEXT textRecord = new TEXT(startAddress);
            int lastRecordAddress = startAddress;*/
            TEXT textRecord = new TEXT(CSstartAddress);
            int lastRecordAddress = CSstartAddress;

            while (istream.available() > 0) {
                Statement statement = (Statement) objInputStream.readObject();

                //System.out.println("inside pass2 "+statement);
                String csName = statement.getCS().name;
                currentCS = searchCSsByName(csName);

                if (statement.isComment()) {
                    continue;
                }
                //compare operation of statement object to "START"
                //new code for control sections added

                if (statement.compareTo("CSECT") == 0) {
                    for (RecordCollector r : mRecords) {
                        objectProgram.write(r.toObjectProgram() + '\n');
                    }
                    mRecords.clear();

                   // System.out.println("==================new sect ============================");
                    objectProgram.write(new Header(statement.label(), CSstartAddress, currentCS.length).toObjectProgram() + '\n');
                } else if (statement.compareTo("START") == 0) {
                   // System.out.println("==================new sect start ============================");
                    try {
                        objectProgram.write(new Header(statement.label(), CSstartAddress, currentCS.length).toObjectProgram() + '\n');
                    } catch (Exception q) {
                  //      System.out.println(q.getMessage());
                    }
                }
                //K: try to make end record here and add CSECT to it too(it should make a new end record each time it sees one of them)
                else if (statement.compareTo("END") == 0) {
                    break;
                } else if (statement.compareTo("EXTREF") == 0) {
                    objectProgram.write((new Refer(currentCS)).toObjectProgram() + "\n");
                } else if (statement.compareTo("EXTDEF") == 0) {
                    objectProgram.write((new Define(currentCS)).toObjectProgram() + "\n");

                } else {
                    //gets object code of instruction
                    String objectCode = Instruction(statement);

                    // If it is format 4 and not immediate value
                    if (statement.isExtended() && currentCS.symbolTable.containsKey(statement.operand1())) {
                     //   System.out.println("adding a record current cs->" + currentCS.name + " label ->" + statement.operand1());
                        mRecords.add(new Modification(statement.location() + 1, 5, '+', currentCS, statement.operand1()));


                    }
//                    Uncomment next line to show the instruction and corresponding object code
//                    System.out.println(statement + "\t\t" + objectCode);

                    if (statement.location() - lastRecordAddress >= 0x1000 || !textRecord.add(objectCode)) {
                        objectProgram.write(textRecord.toObjectProgram() + '\n');

                        textRecord = new TEXT(statement.location());
                        textRecord.add(objectCode);
                    }

                    lastRecordAddress = statement.location();
                }
            }

            objectProgram.write(textRecord.toObjectProgram() + '\n');

            for (RecordCollector r : mRecords) {
                objectProgram.write(r.toObjectProgram() + '\n');
            }

            objectProgram.write(new END(firstExecAddress).toObjectProgram() + '\n');
        }
    }

    private ControlSection searchCSsByName(String csName) {
        for (int i = 0; i < controlSections.size(); i++) {
            if (controlSections.get(i).name.equalsIgnoreCase(csName)) {
                return controlSections.get(i);
            }
        }
        return null;
    }

    private String Expression(Statement statement) {
        int evodd = statement.size - 1;
        int index = 0;
        int counter = 1;
        int freq = 1;
        String prev = null;
        String term = null;
        String typ;
        //   int res = 0;
        //counts terms of expression
        res = 0;
        while (index != evodd) {
            term = statement.symbout[index];
            if (!term.equals("-") && !term.equals("+") && !term.equals("*")) {
                counter++;
            } else {
                if (term.equals("-"))
                    freq--;
                else if (term.equals("+"))
                    freq++;
            }
            index++;
        }
        if (freq == 0) {
            typ = "A";
        } else if (freq == 1) {
            typ = "R";
        } else
            typ = "X";

        for (String curr : statement.symbout) {
            try {
                if (curr.equals("+") || curr.equals("-")) {
                    prev = curr;
                    continue;
                }else if (curr.equals("*")){
                    return "X";
                }
                int sign = prev == null || prev.equals("+") ? 1 : -1;
                if (curr.matches("[0-9]+")) {
                    res += sign * Integer.parseInt(curr);
                } else
                    res += sign * currentCS.symbolTable.get(curr);
                prev = curr;
            } catch (Exception x) {
           //     System.out.println(x.getMessage());
            }

        }
        return typ;

    }


    /**
     * @param statement
     * @return
     */
    private String Instruction(Statement statement) throws IOException {
        String objCode = "";
        //if operation of statement is a valid operation
        if (opTable.containsKey(statement.operation())) {//cases of format of operation
            switch (opTable.get(statement.operation()).getFormat()) {
                case "1":
                    objCode = "^"+opTable.get(statement.operation()).getOpcode();

                    break;
                case "2":
                    objCode = "^"+opTable.get(statement.operation()).getOpcode();
                    try {
                        objCode += Integer.toHexString(registerTable.get(statement.operand1())).toUpperCase();
                        objCode += Integer.toHexString(registerTable.get(statement.operand2())).toUpperCase();
                    } catch (Exception x) {
                        System.out.println("WRONG REGISTER " + x.getMessage());
                    }

                    break;

                case "3/4":
                    final int n = 1 << 5;// n=100000 in binary
                    final int i = 1 << 4;
                    final int x = 1 << 3;
                    final int b = 1 << 2;
                    final int p = 1 << 1;
                    final int e = 1;
                    //The radix parameter is used to specify which numeral system to be used, for example, a radix of 16 (hexadecimal)
                    // indicates that the number in the string should be parsed from a hexadecimal number to a decimal number.
                    int code = Integer.parseInt(opTable.get(statement.operation()).getOpcode(), 16) << 4;
                    String operand = statement.operand1();
                    String label = statement.operation();
                    //if no operand
                    if (operand == null) {
                        //put zeros in displacement
                        code = (code | n | i) << 12; // for RSUB, NOBASE
                    } else {
                        //TODO : problem with oring is that opcode may already have contained n and i =1
                        //like ending with 3 so if immediate  and n place already had1 it won:t change
                        //so it isnot realistic in case of immediate or indirect code and will probably may produce wrong
                        //object code
                        //we can in "code: initialization up shift right by 2 then left by 6
                        //It'llsolve the problem isA
                        switch (operand.charAt(0)) {
                            case '#': // immediate addressing
                                code |= i;

                                operand = operand.substring(1);
                                break;
                            case '@': // indirect addressing
                                code |= n;

                                operand = operand.substring(1);
                                break;
                            default: // simple/direct addressing
                                code |= n | i;


                                if (statement.operand2() != null) {
                                    code |= x;
                                }
                        }

                        try {

                            int disp;
                            //if operand is not a label
                            //currentCS.symbolTable.keySet().forEach(System.out::println);
                            //  System.out.println("trying to find "+operand+"in CS "+currentCS.name);
                            if (currentCS.symbolTable.get(operand) == null) {
                                disp = Integer.parseInt(operand);
                              //  System.out.println("not found " + operand + " in sym table");

                            } else {

                                //GETS LOCATION OF THE OPERAND INSIDE SYMBOL TABLE IN DECIMAL
                                int targetAddress = currentCS.symbolTable.get(operand);
                           //     System.out.println("found " + operand + " in sym table");

                                disp = targetAddress;

                                if (!statement.isExtended()) {
                                    //System.out.println(statement.location() + " HEY " + label);
                                    disp -= statement.location() + 3;
                                    //if pc relative
                                    if (disp >= -2048 && disp <= 2047) {
                                        code |= p;
                                    }
                                    //else if out of base relative range

                                    else if ((targetAddress - baseAddress) >= 4096) {
                                        if (baseAddress == 0) {
                                            System.out.println("Base Address not set");
                                        } else {
                                            code |= b;
                                            disp = targetAddress - baseAddress;
                                        }
                                        System.out.println("Error at instrucion  " + statement.operation() + "can't be base or pc relative");
                                        //System.out.println("but object code handled as if base relative");

                                    }
                                    //else if base relative

                                }
                            }

                            if (statement.isExtended()) {
                                code |= e;

                                code = (code << 20) | (disp & 0xFFFFF);
                            } else {
                                code = (code << 12) | (disp & 0xFFF);
                            }
                        } catch (Exception a) {
                            System.out.println("ERROR FORWARD " + a.getMessage().toUpperCase());
                        }
                    }
                    //assign 8 or 6 hexa decimal digits
                    objCode = String.format(statement.isExtended() ? "^%08X" : "^%06X", code);

                    break;
            }
        } else if (statement.compareTo("BYTE") == 0) {
            String s = statement.operand1();
            char type = s.charAt(0);

            s = s.substring(s.indexOf('\'') + 1, s.lastIndexOf('\''));

            switch (type) {
                case 'C':
                    for (char ch : s.toCharArray()) {
                        objCode += Integer.toHexString(ch).toUpperCase();
                    }

                    break;
                case 'X':
                    objCode = s;

                    break;
            }
        } else if (statement.compareTo("WORD") == 0) {

            //2nd change made it integer+parse int
            objCode = String.format("^%06X", Integer.parseInt(statement.operand1()));
        } else if (statement.compareTo("BASE") == 0) {
            baseAddress = currentCS.symbolTable.get(statement.operand1());
        } else if (statement.compareTo("NOBASE") == 0) {
            baseAddress = 0;
        }

        return objCode;
    }
}

