package me.nettee.depview.model;

public class Inheritation extends DepAttr {

    private enum Type {
        CLASS_EXTENDS,
        CLASS_IMPLEMENTS,
        INTERFACE_EXTENDS,
    }

    private final Type type;

    private Inheritation(Type type) {
        this.type = type;
    }

    public static Inheritation classExtends() {
        return new Inheritation(Type.CLASS_EXTENDS);
    }

    public static Inheritation classImplements() {
        return new Inheritation(Type.CLASS_IMPLEMENTS);
    }

    public static Inheritation interfaceExtends() {
        return new Inheritation(Type.INTERFACE_EXTENDS);
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
