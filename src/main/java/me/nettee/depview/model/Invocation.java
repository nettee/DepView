package me.nettee.depview.model;

public class Invocation {

    private final String expression;
    private final String invocationName;
    private final String qualifiedType;

    public Invocation(String expression, String invocationName, String qualifiedType) {
        this.expression = expression;
        this.invocationName = invocationName;
        this.qualifiedType = qualifiedType;
    }

    public String getQualifiedType() {
        return qualifiedType;
    }

    public String getInvocationString() {
        return String.format("%s.%s()", expression, invocationName);
    }

    @Override
    public String toString() {
        return String.format("%s.%s(), type = %s", expression, invocationName, qualifiedType);
    }
}
