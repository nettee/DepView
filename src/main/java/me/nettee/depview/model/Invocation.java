package me.nettee.depview.model;

public class Invocation extends DepAttr {

    private final String expression;
    private final String invocationName;

    public Invocation(String expression, String invocationName) {
        this.expression = expression;
        this.invocationName = invocationName;
    }

    @Override
    public String toString() {
        return String.format("%s.%s()", expression, invocationName);
    }
}
