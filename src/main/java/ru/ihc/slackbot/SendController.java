package ru.ihc.slackbot;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.ihc.slackbot.slack.JsonMessage;
import ru.ihc.slackbot.slack.SlackApi;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;

@Controller
public class SendController {

    private static final Logger log = LoggerFactory.getLogger(SendController.class);

    @Autowired
    private SlackApi slackApi;

    @ResponseBody
    @RequestMapping(value = "/send/{channel}", method = RequestMethod.GET)
    public String simpleSend(@RequestParam(name = "key", required = false) String key,
                             @PathVariable String channel,
                             @RequestParam(required = false, defaultValue = "info") String username,
                             @RequestParam(required = false) String icon,
                             @RequestParam(name = "text", required = false, defaultValue = "test") String text) {
        return slackApi.send(key, channel, username, icon, text);
    }

}
