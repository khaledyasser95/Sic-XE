package Table.Objectcode;

import Table.ControlSection;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by Kareem on 21-May-17.
 */
public class Refer implements RecordCollector {
    private ControlSection controlSection;

    public Refer(ControlSection controlSection) {
        this.controlSection = controlSection;
    }

    @Override
    public String toObjectProgram() {
        return "R_"+controlSection.externalReferences.stream().collect(Collectors.joining("_"));
    }
}
