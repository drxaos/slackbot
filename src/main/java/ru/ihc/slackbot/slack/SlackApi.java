package ru.ihc.slackbot.slack;

import com.ullink.slack.simpleslackapi.*;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.replies.SlackMessageReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ihc.slackbot.CommandService;
import ru.ihc.slackbot.config.Config;

import javax.annotation.PostConstruct;
import java.net.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SlackApi {
    private static final Logger log = LoggerFactory.getLogger(SlackApi.class);

    @Autowired
    private CommandService commandService;

    @Autowired
    private Config config;

    private SlackSession session;

    @PostConstruct
    public void start() {
        final String token = config.getSlackToken();

        log.info("Using token: " + token);
        session = SlackSessionFactory.getSlackSessionBuilder(token)
                .withAutoreconnectOnDisconnection(true)
                .withConnectionHeartbeat(5, TimeUnit.SECONDS)
                .withProxy(Proxy.Type.HTTP, "127.0.0.1", 8080)
                .build();

        try {
            session.connect();
        } catch (Exception e) {
            log.error("connect error with token " + token, e);
            throw new RuntimeException(e);
        }

        session.addMessagePostedListener((event, session1) -> {
            final String selfId = session1.sessionPersona().getId();

            if (selfId.equals(event.getSender().getId())) {
                // avoid self messages
                return;
            }

            String text = event.getMessageContent();
            if (!text.contains("<@" + selfId + ">")) {
                // no mention
                return;
            }

            text = text.replace("<@" + selfId + ">", "").trim();

            session1.sendTyping(event.getChannel());

            try {
                String answer = commandService.handleCommand(text);

                session1.sendMessage(event.getChannel(),
                        "# " + text + "\n" +
                                Stream.of(answer.split("\n"))
                                        .map(elem -> "> " + elem + "\n")
                                        .collect(Collectors.joining()));

            } catch (Throwable e) {
                session1.sendMessage(event.getChannel(), "" + e);
            }

        });

        send(config.getKey(), "bots", null, null, "Bot started");

        Runtime.getRuntime().addShutdownHook(new Thread(this::tearDown));
    }

    public void tearDown() {
        send(config.getKey(), "bots", null, null, "Bot shutdown");
    }

    public String send(String key, String channel, String username, String icon, String content) {
        JsonMessage message = new JsonMessage();
        message.setText(content);
        message.setUsername(username);
        message.setIcon(icon);

        return sendJson(key, channel, message);
    }

    public String sendJson(String key, String channel, final JsonMessage content) {
        if (!key.equals(config.getKey())) {
            return "wrong key";
        }

        SlackChannel ch = session.findChannelByName(channel);
        if (ch == null) {
            return "wrong channel";
        }

        if (content.getIcon() == null || content.getIcon().isEmpty()) {
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put("ihc", "https://my.ihc.ru/images/avatar/ihc.jpg");
            map.put("ihc.ru", "https://my.ihc.ru/images/avatar/ihc.jpg");
            map.put("my.ihc.ru", "https://my.ihc.ru/images/avatar/ihc.jpg");
            map.put("ихц", "https://my.ihc.ru/images/avatar/ihc.jpg");
            map.put("интернет хостинг центр", "https://my.ihc.ru/images/avatar/ihc.jpg");
            map.put("relevate", "https://bill.relevate.ru/images/avatar/rlv.jpg");
            map.put("relevate.ru", "https://bill.relevate.ru/images/avatar/rlv.jpg");
            map.put("bill.relevate.ru", "https://bill.relevate.ru/images/avatar/rlv.jpg");
            map.put("релевейт", "https://bill.relevate.ru/images/avatar/rlv.jpg");
            map.put("devel", "https://my.ihc.ru/images/avatar/devel.png");
            map.put("devel-x64.ihc-ru.net", "https://my.ihc.ru/images/avatar/devel.png");
            map.put("девел", "https://my.ihc.ru/images/avatar/devel.png");

            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (content.getUsername().toLowerCase().startsWith(entry.getKey())) {
                    content.setIcon(entry.getValue());
                }
            }
        }

        // дефолтные настройки
        SlackChatConfiguration user = SlackChatConfiguration.getConfiguration().asUser();
        if (content.getUsername() == null || content.getUsername().isEmpty()) {
            user = user.withName(content.getUsername());
        }
        if (content.getIcon() == null || content.getIcon().isEmpty()) {
            user = user.withIcon(content.getIcon());
        }


        SlackPreparedMessage.Builder builder = new SlackPreparedMessage.Builder();
        if (content.getText() != null && !content.getText().isEmpty()) {
            builder.withMessage(content.getText());
        }
        if (content.getAttachments() != null && !content.getAttachments().isEmpty()) {
            builder.withAttachments(content.getAttachments());
        }
        SlackPreparedMessage msg = builder.build();


        // https://api.slack.com/docs/messages/builder
        SlackMessageHandle<SlackMessageReply> message = session.sendMessage(ch, msg, user);

        String ts = message.getReply().getTimestamp();

        if (content.getReaction() != null && !content.getReaction().isEmpty()) {
            session.addReactionToMessage(ch, ts, content.getReaction());
        }

        return "ok: " + ts;
    }
}
