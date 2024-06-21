package org.tkit.onecx.chat.bff.rs.controllers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import gen.org.tkit.onecx.chat.bff.rs.internal.model.MessageDTO;

@ServerEndpoint("/chats/socket/{userName}")
@ApplicationScoped
public class ChatSocket {

    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userName") String userName) {
        sessions.put(userName, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("userName") String userName) {
        sessions.remove(userName);
    }

    public void sendMessage(List<String> userNames, String chatId, MessageDTO messageDTO) {
        WebsocketHelperDTO helperDTO = new WebsocketHelperDTO(chatId, messageDTO);
        for (String userName : userNames) {
            Session session = sessions.get(userName);
            if (session != null) {
                session.getAsyncRemote().sendObject(helperDTO);
            }
        }
    }

    private class WebsocketHelperDTO {
        private String chatId;
        private MessageDTO messageDTO;

        WebsocketHelperDTO(String chatId, MessageDTO messageDTO) {
            this.chatId = chatId;
            this.messageDTO = messageDTO;
        }

    }
}
