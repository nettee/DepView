package me.nettee.depview.model;

public class PlainClass implements Comparable<PlainClass> {

    private final String qualifiedName;
    private final String projectPackage;

    public PlainClass(PlainClass originClass, String qualifiedName) {
        this.qualifiedName = qualifiedName;
        this.projectPackage = originClass.getProjectPackage();
    }

    public PlainClass(String qualifiedName, String projectPackage) {
        this.qualifiedName = qualifiedName;
        this.projectPackage = projectPackage;
    }

    public boolean isInPackage() {
        return qualifiedName.startsWith(projectPackage);
    }

    public String getSimpleName() {
        int i = qualifiedName.lastIndexOf('.');
        if (i == -1) {
            return qualifiedName;
        } else {
            return qualifiedName.substring(i + 1);
        }
    }

    public String getShortName() {
        int n = projectPackage.length();
        return qualifiedName.substring(n);
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

    public boolean isSamePackageWith(PlainClass that) {
        return this.getPackage().equals(that.getPackage());
    }

    public String getProjectPackage() {
        return projectPackage;
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
