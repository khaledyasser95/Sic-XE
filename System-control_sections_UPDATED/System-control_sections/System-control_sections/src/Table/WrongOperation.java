package Table;

/**
 * Created by ICY on 4/10/2017.
 */
class WrongOperation extends Exception {
    public WrongOperation(Statement statement) {
        super("Wrong operation : " + statement.operation());
    }
}
