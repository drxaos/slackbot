package ru.ihc.slackbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ihc.slackbot.commands.Command;
import ru.ihc.slackbot.commands.InfoService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class CommandService {
    private static final Logger log = LoggerFactory.getLogger(CommandService.class);

    @Autowired
    private InfoService infoService;

    private LinkedHashMap<String, Command> commands = new LinkedHashMap<>();

    @PostConstruct
    private void init() {
        commands.put("info", infoService);
    }

    public String handleCommand(String command) {
        List<String> args = new ArrayList<>(Arrays.asList(command.split("[ ]+")));
        if (args.isEmpty()) {
            return "no command";
        }

        String cmd = args.remove(0);
        if (!commands.containsKey(cmd)) {
            return "unknown command";
        }

        return commands.get(cmd).execute(args);
    }

}
