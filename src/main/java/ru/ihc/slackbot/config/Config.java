package ru.ihc.slackbot.config;

import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Config {
    private static final Logger log = LoggerFactory.getLogger(Config.class);

    private static Configuration configuration = null;

    @Value("${slackToken}")
    private String slackToken;

    @Value("${key}")
    private String key;

    public String getSlackToken() {
        return configuration != null ? configuration.getString("slackToken", slackToken) : slackToken;
    }

    public String getKey() {
        return configuration != null ? configuration.getString("key", key) : key;
    }

}
