package Table;

/**
 * Created by ICY on 5/13/2017.
 */
class Forward extends  Exception {
    public Forward(Statement statement) {
        super("Forward symbol found: " + statement.operand1());
    }
}
