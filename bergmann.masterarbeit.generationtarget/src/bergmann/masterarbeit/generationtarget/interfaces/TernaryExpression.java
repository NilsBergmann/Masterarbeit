package bergmann.masterarbeit.generationtarget.interfaces;

public abstract class TernaryExpression<A extends Object, B extends Object, C extends Object, T extends Object> extends Expression<T>  {
    public Expression<A> a;
    public Expression<B> b;
    public Expression<C> c;

    public TernaryExpression(Expression<A> a, Expression<B> b, Expression<C> c) {
        super();
        if(a == null || b == null || c == null)
        	throw new IllegalArgumentException("null is not a valid subexpression");
        this.a = a;
        this.b = b;
        this.c = c;
    }

}
