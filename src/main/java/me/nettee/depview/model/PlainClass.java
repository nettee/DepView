package me.nettee.depview.model;

import me.nettee.depview.main.Env;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class PlainClass implements Comparable<PlainClass> {

    private final String qualifiedName;

    public PlainClass(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public PlainClass(ITypeBinding typeBinding) {
        this(typeBinding.getQualifiedName());
    }

    public boolean isInPackage(String packageName) {
        return qualifiedName.startsWith(packageName);
    }

    public String getSimpleName() {
        int i = qualifiedName.lastIndexOf('.');
        if (i == -1) {
            return qualifiedName;
        } else {
            return qualifiedName.substring(i + 1);
        }
    }

    private String getShortName(String packageName) {
        if (qualifiedName.startsWith(packageName)) {
            int n = packageName.length();
            return qualifiedName.substring(n);
        } else {
            return qualifiedName;
        }
    }

    public String getShortName(Env env) {
        return getShortName(env.getProjectPackage());
    }

    public String getName() {
        return qualifiedName;
    }

    public String getPackage() {
        int i = qualifiedName.lastIndexOf('.');
        if (i == -1) {
            return qualifiedName;
        } else {
            return qualifiedName.substring(0, i);
        }
    }

    @Override
    public int compareTo(PlainClass that) {
        return this.qualifiedName.compareTo(that.qualifiedName);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlainClass) {
            PlainClass that = (PlainClass) obj;
            return this.qualifiedName.equals(that.qualifiedName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return qualifiedName.hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }
}
