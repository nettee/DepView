package me.nettee.depview.model;

import lombok.Getter;

public class Dep<A> {

    @Getter
    private final PlainClass fromClass;

    @Getter
    private final PlainClass toClass;

    @Getter
    private final A attr;

    public Dep(PlainClass fromClass, PlainClass toClass, A attr) {
        this.fromClass = fromClass;
        this.toClass = toClass;
        this.attr = attr;
    }

}

