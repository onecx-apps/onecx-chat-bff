package org.tkit.onecx.chat.bff.rs.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openapi.quarkus.onecx_chat_internal_openapi_yaml.model.*;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.chat.bff.rs.internal.model.ChatDTO;
import gen.org.tkit.onecx.chat.bff.rs.internal.model.ChatPageResultDTO;
import gen.org.tkit.onecx.chat.bff.rs.internal.model.ChatSearchCriteriaDTO;
import gen.org.tkit.onecx.chat.bff.rs.internal.model.CreateChatDTO;
import gen.org.tkit.onecx.chat.bff.rs.internal.model.UpdateChatDTO;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ChatMapper {
    CreateChat map(CreateChatDTO createChatDTO);

    @Mapping(target = "removeParticipantsItem", ignore = true)
    CreateChatDTO map(CreateChat createChat);

    Chat map(ChatDTO chatDTO);

    @Mapping(target = "removeParticipantsItem", ignore = true)
    ChatDTO map(Chat chat);

    @Mapping(target = "removeParticipantsItem", ignore = true)
    UpdateChatDTO map(UpdateChat updateChat);

    UpdateChat map(UpdateChatDTO updateChatDTO);

    ChatSearchCriteriaDTO map(ChatSearchCriteria chatSearchCriteria);

    ChatSearchCriteria map(ChatSearchCriteriaDTO chatSearchCriteriaDTO);

    @Mapping(target = "removeStreamItem", ignore = true)
    List<ChatPageResultDTO> map(List<ChatPageResult> results);

    @Mapping(target = "removeStreamItem", ignore = true)
    ChatPageResultDTO map(ChatPageResult chatPageResult);

    ChatPageResult map(ChatPageResultDTO chatPageResultDTO);
}