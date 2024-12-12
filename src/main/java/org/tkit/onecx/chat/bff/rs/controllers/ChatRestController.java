package org.tkit.onecx.chat.bff.rs.controllers;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.chat.bff.rs.mappers.*;

import gen.org.tkit.onecx.chat.bff.clients.api.ChatsInternalApi;
import gen.org.tkit.onecx.chat.bff.clients.model.*;
import gen.org.tkit.onecx.chat.bff.rs.internal.ChatsApiService;
import gen.org.tkit.onecx.chat.bff.rs.internal.model.*;

@ApplicationScoped
public class ChatRestController implements ChatsApiService {

    @Inject
    @RestClient
    ChatsInternalApi client;

    @Inject
    ChatMapper mapper;

    @Inject
    ParticipantMapper participantMapper;

    @Inject
    MessageMapper messageMapper;

    @Inject
    ChatSocket socket;

    @Inject
    ExceptionMapper exceptionMapper;

    List<ChatDTO> chats = new ArrayList<>();

    @Override
    public Response addParticipant(String chatId, @Valid @NotNull AddParticipantDTO addParticipantDTO) {
        try (Response response = this.client.addParticipant(chatId, participantMapper.map(addParticipantDTO))) {
            Participant p = response.readEntity(Participant.class);
            return Response.status(200).entity(participantMapper.map(p)).build();
        }
    }

    @Override
    public Response createChat(@Valid @NotNull CreateChatDTO createChatDTO) {
        Chat c = client.createChat(mapper.map(createChatDTO)).readEntity(Chat.class);
        return Response.status(200).entity(mapper.map(c)).build();
    }

    @Override
    public Response createChatMessage(String chatId, @Valid @NotNull CreateMessageDTO createMessageDTO) {
        client.createChatMessage(chatId, messageMapper.map(createMessageDTO));

        MessageDTO mDto = messageMapper.mapToMessage(createMessageDTO);

        try (Response r = getChatParticipants(chatId)) {
            List<ParticipantDTO> l = r.readEntity(new GenericType<List<ParticipantDTO>>() {
            });
            List<String> userNames = new ArrayList<>();

            l.forEach(p -> {
                userNames.add(p.getUserName());
            });
            this.socket.sendMessage(userNames, chatId, mDto);
        }

        return Response.status(200).entity(mDto).build();
    }

    @Override
    public Response deleteChat(String id) {
        try (Response response = client.deleteChat(id)) {
            return Response.status(response.getStatus()).build();
        }
    }

    @Override
    public Response getChatById(String id) {
        Chat c = client.getChatById(id).readEntity(Chat.class);
        ChatDTO dto = mapper.map(c);
        return Response.status(200).entity(dto).build();
    }

    @Override
    public Response getChatMessages(String chatId) {
        List<Message> m = client.getChatMessages(chatId).readEntity(new GenericType<List<Message>>() {
        });
        List<MessageDTO> m2 = messageMapper.map(m);
        return Response.status(200).entity(m2).build();
    }

    @Override
    public Response getChatParticipants(String chatId) {
        List<Participant> p = client.getChatParticipants(chatId).readEntity(new GenericType<List<Participant>>() {
        });
        List<ParticipantDTO> p2 = participantMapper.map(p);
        return Response.status(200).entity(p2).build();
    }

    @Override
    public Response getChats(Integer pageNumber, Integer pageSize) {
        ChatPageResult result = client.getChats(pageNumber, pageSize)
                .readEntity(new GenericType<ChatPageResult>() {
                });
        ChatPageResultDTO resultDTOs = mapper.map(result);
        return Response.status(200).entity(resultDTOs).build();
    }

    @Override
    public Response searchChats(@Valid @NotNull ChatSearchCriteriaDTO chatSearchCriteriaDTO) {
        ChatPageResult result = client.searchChats(mapper.map(chatSearchCriteriaDTO))
                .readEntity(new GenericType<ChatPageResult>() {
                });
        ChatPageResultDTO resultDTO = mapper.map(result);
        return Response.status(200).entity(resultDTO).build();
    }

    @Override
    public Response updateChat(String id, @Valid @NotNull UpdateChatDTO updateChatDTO) {
        try (Response response = client.updateChat(id, mapper.map(updateChatDTO))) {
            Chat chat = response.readEntity(Chat.class);
            ChatDTO chatDTO = mapper.map(chat);
            return Response.status(response.getStatus()).entity(chatDTO).build();
        }
    }

    @Override
    public Response removeParticipant(String chatId, String participantId) {
        // TODO implement participant removal in SVC
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeParticipant'");
    }

    @ServerExceptionMapper
    public Response exception(ClientWebApplicationException ex) {
        return exceptionMapper.clientException(ex);
    }

}
