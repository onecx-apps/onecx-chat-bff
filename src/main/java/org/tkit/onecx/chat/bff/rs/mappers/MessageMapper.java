package org.tkit.onecx.chat.bff.rs.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import gen.org.tkit.onecx.chat.bff.rs.internal.model.CreateMessageDTO;
import gen.org.tkit.onecx.chat.bff.rs.internal.model.MessageDTO;
import gen.org.tkit.onecx.chat.clients.model.CreateMessage;
import gen.org.tkit.onecx.chat.clients.model.Message;

@Mapper
public interface MessageMapper {
    MessageDTO map(Message message);

    Message map(MessageDTO messageDTO);

    CreateMessageDTO map(CreateMessage createMessage);

    CreateMessage map(CreateMessageDTO createMessageDTO);

    MessageDTO mapToMessage(CreateMessageDTO createMessageDTO);

    List<MessageDTO> map(List<Message> messages);
}
