package Table;

/**
 * Created by ICY on 3/13/2017.
 */
public class OPERATION {
    private String mnemonic;
    private String opcode;
    private String format;

    public OPERATION(String mnemonic, String format, String opcode) {
        this.mnemonic = mnemonic;
        this.opcode = opcode;
        this.format = format;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public String getOpcode() {
        return opcode;
    }

    public String getFormat() {
        return format;
    }

}
