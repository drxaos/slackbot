package ru.ihc.slackbot.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

@Service
public class InfoService implements Command {
    private static final Logger log = LoggerFactory.getLogger(InfoService.class);

    public String execute(List<String> args) {

        if (args.size() == 0) {
            return "Usage: info <panel1> <panel2> ...";
        }

        HashMap<String, String> map = new LinkedHashMap<>();
        map.put("ihc", "https://my.ihc.ru/info/health");
        map.put("relevate", "https://bill.relevate.ru/info/health");
        map.put("devel", "https://devel-x64.ihc-ru.net/info/health");

        StringBuilder sb = new StringBuilder();

        for (String panel : args) {
            String url = map.get("panel");
            if (url == null) {
                sb.append(panel).append(": [no url]\n");
                continue;
            }
            try {
                URLConnection con = new URL(url).openConnection();
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                InputStream in = con.getInputStream();
                String result = new Scanner(in, "UTF-8").useDelimiter("\\A").next().trim();
                sb.append(panel).append(": ").append(result).append("\n");
            } catch (IOException e) {
                sb.append(panel).append(": [error ").append(e).append("]\n");
            }
        }

        String answer = sb.toString();
        return "";
    }

}
