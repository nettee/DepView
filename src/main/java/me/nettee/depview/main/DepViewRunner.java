package me.nettee.depview.main;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DepViewRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        if (args.length < 1) {
            throw new IllegalArgumentException("No config file specified");
        }
        String filename = args[0];

        File testSubjectConfigFile = new File(filename);

        Config conf = ConfigFactory.parseFile(testSubjectConfigFile);
        Config config = conf.getConfig("testSubject");

        TestSubject testSubject = TestSubject.fromConfig(config);

        DepView depView = new DepView(testSubject);
        depView.view();
    }
}
