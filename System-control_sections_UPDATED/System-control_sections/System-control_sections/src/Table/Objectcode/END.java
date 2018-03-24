package Table.Objectcode;

/**
 * Created by ICY on 4/12/2017.
 */
public class END implements RecordCollector {
    private final int startAddress;

    public END(int startAddress) {
        this.startAddress = startAddress;
    }

    @Override
    public String toObjectProgram() {
        return String.format("E%1$06X", startAddress);
    }

}
