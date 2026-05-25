package ru.ship.ShipHub.services.chat;

public class ChatWebSocketMessage {
    public String type;
    public Long claimId;
    public Long messageId;
    public Long senderId;
    public String senderName;
    public String text;
    public String dateCreated;
    public String error;
    public String info;

    public ChatWebSocketMessage() {
    }

    public ChatWebSocketMessage(String type) {
        this.type = type;
    }

    public ChatWebSocketMessage(String type, Long claimId, Long messageId, Long senderId, String senderName, String text, String dateCreated, String error, String info) {
        this.type = type;
        this.claimId = claimId;
        this.messageId = messageId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.text = text;
        this.dateCreated = dateCreated;
        this.error = error;
        this.info = info;
    }
}
