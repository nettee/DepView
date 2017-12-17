package me.nettee.depview.model;

public class InvDep {

    private final PlainClass thisClass;
    private final PlainClass targetClass;
    private final Invocation invocation;

    public InvDep(PlainClass thisClass, PlainClass targetClass, Invocation invocation) {
        this.thisClass = thisClass;
        this.targetClass = targetClass;
        this.invocation = invocation;
    }

    public PlainClass getThisClass() {
        return thisClass;
    }

    public PlainClass getTargetClass() {
        return targetClass;
    }

    public Invocation getInvocation() {
        return invocation;
    }
}
