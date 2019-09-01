package me.nettee.depview.main;

import lombok.Getter;
import me.nettee.depview.model.DepGraph;

public class Env {

    @Getter
    private final String projectPackage;

    private DepGraph depGraph; // TODO thread safety

    private Env(String projectPackage) {
        this.projectPackage = projectPackage;
    }

    public static Env newInstance(TestSubject testSubject) {
        return new Env(testSubject.getProjectPackage());
    }

    public DepGraph getDepGraph() {
        if (depGraph == null) {
            depGraph = new DepGraph(this);
        }
        return depGraph;
    }

}
