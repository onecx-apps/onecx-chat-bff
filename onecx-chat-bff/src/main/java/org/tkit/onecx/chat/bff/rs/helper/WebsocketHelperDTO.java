package org.tkit.onecx.chat.bff.rs.helper;

import gen.org.tkit.onecx.chat.bff.rs.internal.model.MessageDTO;

public class WebsocketHelperDTO {
    private String chatId;
    private MessageDTO messageDTO;

    public WebsocketHelperDTO(String chatId, MessageDTO messageDTO) {
        this.chatId = chatId;
        this.messageDTO = messageDTO;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public MessageDTO getMessageDTO() {
        return messageDTO;
    }

    public void setMessageDTO(MessageDTO messageDTO) {
        this.messageDTO = messageDTO;
    }

    @Override
    public String toString() {
        return "WebsocketHelperDTO [chatId=" + chatId + ", messageDTO=" + messageDTO + "]";
    }

}
