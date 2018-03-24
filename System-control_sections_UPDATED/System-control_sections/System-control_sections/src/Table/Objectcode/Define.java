package Table.Objectcode;

import Table.ControlSection;

import java.util.ArrayList;

/**
 * Created by Kareem on 21-May-17.
 */
public class Define implements RecordCollector {
    private ControlSection controlSection;
    public Define(ControlSection controlSection)
    {
        this.controlSection = controlSection;
    }


    @Override
    public String toObjectProgram() {
        StringBuilder builder = new StringBuilder();
        builder.append("D_");
        for (String label:controlSection.externallyDefined) {
            builder.append(String.format("%-6s_%06X_",label,controlSection.symbolTable.get(label)));
        }
        return builder.toString();
    }
}
