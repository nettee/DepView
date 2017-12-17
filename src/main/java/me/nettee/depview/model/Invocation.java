package me.nettee.depview.model;

public class Invocation {

    private final String expression;
    private final String invocationName;
    private final PlainClass type;

    public Invocation(String expression, String invocationName, PlainClass type) {
        this.expression = expression;
        this.invocationName = invocationName;
        this.type = type;
    }

    public PlainClass getType() {
        return type;
    }

    public String getInvocationString() {
        return String.format("%s.%s()", expression, invocationName);
    }

    @Override
    public String toString() {
        return String.format("%s.%s(), type = %s", expression, invocationName, type);
    }
}
