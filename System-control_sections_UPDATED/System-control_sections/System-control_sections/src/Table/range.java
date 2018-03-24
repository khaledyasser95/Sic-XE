package Table;

/**
 * Created by ICY on 4/19/2017.
 */
class range extends Exception {
    public range(Statement statement) {
        super("out of range : " + statement.location());
    }
}
