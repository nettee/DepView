package me.nettee.depview.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestSubject {

    private String name;
    private List<File> sources;
    private List<File> classes;
    private final List<File> jars;

    public TestSubject() {
        jars = new ArrayList<>();
    }

    public void addJars(Collection<File> jars) {
        this.jars.addAll(jars);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<File> getSources() {
        return sources;
    }

    public void setSources(List<File> sources) {
        this.sources = sources;
    }

    public List<File> getClasses() {
        return classes;
    }

    public void setClasses(List<File> classes) {
        this.classes = classes;
    }

    public List<File> getJars() {
        return jars;
    }
}
