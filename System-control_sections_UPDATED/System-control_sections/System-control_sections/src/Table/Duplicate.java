package Table;

/**
 * Created by ICY on 4/10/2017.
 */
class Duplicate extends Exception {
    public Duplicate(Statement statement) {
        super("Duplicate symbol found: " + statement.label());
    }
}
