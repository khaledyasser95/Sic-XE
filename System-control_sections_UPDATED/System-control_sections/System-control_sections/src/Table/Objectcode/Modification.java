package Table.Objectcode;

import Table.ControlSection;

/**
 * Created by ICY on 4/12/2017.
 */
public class Modification implements RecordCollector {
    private int location;
    private int length;
    private char sign;
    private ControlSection currentCS;
    private String label;

    public Modification(int location, int length) {
        this.location = location;
        this.length = length;
        this.sign = '+';

    }

    public Modification(int location, int length, char sign, ControlSection currentCS, String label) {
        this.location = location;
        this.length = length;
        this.sign = sign;
        this.currentCS = currentCS;
        this.label = label;
    }

    @Override
    public String toObjectProgram() {

        return String.format("M%06X%02X%c%s", location, length, sign,
                currentCS.externalReferences.contains(label) ? label : currentCS.name);
        //if this label is an external ref put its name in the m record else put the name of the current  cs
        //in the record
    }

}
