package me.nettee.depview.main;

public class Env {

    private final String projectPackage;

    private Env(String projectPackage) {
        this.projectPackage = projectPackage;
    }

    public static Env newInstance(TestSubject testSubject) {
        return new Env(testSubject.getProjectPackage());
    }

    public String getProjectPackage() {
        return projectPackage;
    }
}
