package org.tkit.onecx.chat.bff.rs.controllers;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.openapi.quarkus.onecx_chat_internal_openapi_yaml.api.ChatsInternalApi;
import org.openapi.quarkus.onecx_chat_internal_openapi_yaml.model.Chat;
import org.openapi.quarkus.onecx_chat_internal_openapi_yaml.model.ChatPageResult;
import org.openapi.quarkus.onecx_chat_internal_openapi_yaml.model.Message;
import org.openapi.quarkus.onecx_chat_internal_openapi_yaml.model.Participant;
import org.tkit.onecx.chat.bff.rs.mappers.ChatMapper;
import org.tkit.onecx.chat.bff.rs.mappers.MessageMapper;
import org.tkit.onecx.chat.bff.rs.mappers.ParticipantMapper;

import gen.org.tkit.onecx.chat.bff.rs.internal.ChatsApiService;
import gen.org.tkit.onecx.chat.bff.rs.internal.model.AddParticipantDTO;
import gen.org.tkit.onecx.chat.bff.rs.internal.model.ChatDTO;
import gen.org.tkit.onecx.chat.bff.rs.internal.model.ChatPageResultDTO;
import gen.org.tkit.onecx.chat.bff.rs.internal.model.ChatSearchCriteriaDTO;
import gen.org.tkit.onecx.chat.bff.rs.internal.model.CreateChatDTO;
import gen.org.tkit.onecx.chat.bff.rs.internal.model.CreateMessageDTO;
import gen.org.tkit.onecx.chat.bff.rs.internal.model.MessageDTO;
import gen.org.tkit.onecx.chat.bff.rs.internal.model.ParticipantDTO;
import gen.org.tkit.onecx.chat.bff.rs.internal.model.UpdateChatDTO;

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

    List<ChatDTO> chats = new ArrayList<>();

    @Override
    public Response addParticipant(String chatId, @Valid @NotNull AddParticipantDTO addParticipantDTO) {
        Participant p = this.client.addParticipant(chatId, participantMapper.map(addParticipantDTO));
        return Response.status(200).entity(participantMapper.map(p)).build();
    }

    @Override
    public Response createChat(@Valid @NotNull CreateChatDTO createChatDTO) {
        Chat c = client.createChat(mapper.map(createChatDTO));
        return Response.status(200).entity(mapper.map(c)).build();
    }

    @Override
    public Response createChatMessage(String chatId, @Valid @NotNull CreateMessageDTO createMessageDTO) {
        client.createChatMessage(chatId, messageMapper.map(createMessageDTO));

        MessageDTO mDto = messageMapper.mapToMessage(createMessageDTO);

        try (Response r = getChatParticipants(chatId)) {
            List<ParticipantDTO> l = r.readEntity(ArrayList.class);
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
        Chat c = client.getChatById(id);
        ChatDTO dto = mapper.map(c);
        return Response.status(200).entity(dto).build();
    }

    @Override
    public Response getChatMessages(String chatId) {
        List<Message> m = client.getChatMessages(chatId);
        List<MessageDTO> m2 = messageMapper.map(m);
        return Response.status(200).entity(m2).build();
    }

    @Override
    public Response getChatParticipants(String chatId) {
        List<Participant> p = client.getChatParticipants(chatId);
        List<ParticipantDTO> p2 = participantMapper.map(p);
        return Response.status(200).entity(p2).build();
    }

    @Override
    public Response getChats(Integer pageNumber, Integer pageSize) {
        ChatPageResult result = client.getChats(pageNumber, pageSize);
        ChatPageResultDTO resultDTOs = mapper.map(result);
        return Response.status(200).entity(resultDTOs).build();
    }

    @Override
    public Response searchChats(@Valid @NotNull ChatSearchCriteriaDTO chatSearchCriteriaDTO) {
        ChatPageResult result = client.searchChats(mapper.map(chatSearchCriteriaDTO));
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
        //TODO implement participant removal in SVC
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeParticipant'");
    }

}
