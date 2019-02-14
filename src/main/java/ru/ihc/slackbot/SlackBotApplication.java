package ru.ihc.slackbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"me.ramswaroop.jbot", "ru.ihc.slackbot"})
public class SlackBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(SlackBotApplication.class, args)
    }
}
