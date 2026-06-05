package me.catst0day.Eclipse.Mail;

import java.util.UUID;

public class MailTemplate {
    private final int id;
    private final UUID sender;
    private final UUID recipient;
    private final String message;
    private final long timestamp;
    private boolean read;

    public MailTemplate(int id, UUID sender, UUID recipient, String message, long timestamp, boolean read) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.timestamp = timestamp;
        this.read = read;
    }

    public int getId() {
        return id;
    }

    public UUID getSender() {
        return sender;
    }

    public UUID getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
