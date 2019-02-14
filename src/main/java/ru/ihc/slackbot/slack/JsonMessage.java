package ru.ihc.slackbot.slack;

import com.ullink.slack.simpleslackapi.SlackAttachment;

import java.io.Serializable;
import java.util.List;

public class JsonMessage implements Serializable {

    private String username;
    private String icon;
    private String text;
    private List<SlackAttachment> attachments;
    private String reaction;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<SlackAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<SlackAttachment> attachments) {
        this.attachments = attachments;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

}
