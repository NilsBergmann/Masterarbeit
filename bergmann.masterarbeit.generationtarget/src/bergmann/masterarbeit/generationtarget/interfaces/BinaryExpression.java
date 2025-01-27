package bergmann.masterarbeit.generationtarget.interfaces;

public abstract class BinaryExpression<A, B, T> extends Expression<T> {
    public Expression<A> left;
    public Expression<B> right;

    public BinaryExpression(Expression<A> left, Expression<B> right) {
        super();
        if(left == null || right == null)
        	throw new IllegalArgumentException("null is not a valid subexpression");
        this.right = right;
        this.left = left;
    }

}