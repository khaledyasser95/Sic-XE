package Table.Objectcode;

/**
 * Created by ICY on 4/12/2017.
 */
public class Header implements RecordCollector {
    private final String programName;
    private final int startAddress;
    private final int programLength;

    public Header(String programName, int startAddress, int programLength) {
        this.programName = programName;
        this.startAddress = startAddress;
        this.programLength = programLength;
    }

    @Override
    public String toObjectProgram() {
        //%number= which statement to insert
        // $type = type of insertions
        // -6 RIGHT PADDING
        return String.format("H%1$-6s^%2$06X^%3$06X", programName, startAddress, programLength);
    }
}
