package me.nettee.depview.main;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DepViewApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DepViewApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }
}
