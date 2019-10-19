package bergmann.masterarbeit.generationtarget.interfaces;

public abstract class UnaryExpression<S, T> extends Expression<T> {
    public Expression<S> expr;

    public UnaryExpression(Expression<S> expr) {
        super();
        if(expr == null)
        	throw new IllegalArgumentException("null is not a valid subexpression");
        this.expr = expr;
    }

}