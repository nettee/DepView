package me.nettee.depview.model;

public class Dep<A> {

    private final PlainClass fromClass;
    private final PlainClass toClass;
    private final A attr;

    public Dep(PlainClass fromClass, PlainClass toClass, A attr) {
        this.fromClass = fromClass;
        this.toClass = toClass;
        this.attr = attr;
    }

    public PlainClass getFromClass() {
        return fromClass;
    }

    public PlainClass getToClass() {
        return toClass;
    }

    public A getAttr() {
        return attr;
    }

}

