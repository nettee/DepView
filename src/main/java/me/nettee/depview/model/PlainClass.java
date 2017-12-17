package me.nettee.depview.model;

public class PlainClass implements Comparable<PlainClass> {

    private final String qualifiedName;

    public PlainClass(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public boolean isInPackage(String package_) {
        return qualifiedName.startsWith(package_);
    }

    public String getSimpleName() {
        int i = qualifiedName.lastIndexOf('.');
        if (i == -1) {
            return qualifiedName;
        } else {
            return qualifiedName.substring(i + 1);
        }
    }

    public String getShortName(String package_) {
        int n = package_.length();
        return qualifiedName.substring(n);
    }

    public String getName() {
        return qualifiedName;
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
