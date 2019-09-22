package bergmann.masterarbeit.generationtarget.interfaces;

public abstract class BinaryExpression<A, B, T> implements Expression<T> {
    public Expression<A> left;
    public Expression<B> right;

    public BinaryExpression(Expression<A> left, Expression<B> right) {
        super();
        this.right = right;
        this.left = left;
    }

}