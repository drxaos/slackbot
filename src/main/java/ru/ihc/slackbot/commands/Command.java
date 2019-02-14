package ru.ihc.slackbot.commands;

import java.util.List;

public interface Command {

    String execute(List<String> args);

}
