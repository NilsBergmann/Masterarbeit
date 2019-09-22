package bergmann.masterarbeit.generationtarget.interfaces;

public abstract class UnaryExpression<S, T> implements Expression<T> {
    public Expression<S> expr;

    public UnaryExpression(Expression<S> expr) {
        super();
        this.expr = expr;
    }

}