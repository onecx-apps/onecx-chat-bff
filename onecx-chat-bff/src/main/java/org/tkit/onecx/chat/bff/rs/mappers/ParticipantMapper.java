package org.tkit.onecx.chat.bff.rs.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.openapi.quarkus.onecx_chat_internal_openapi_yaml.model.AddParticipant;
import org.openapi.quarkus.onecx_chat_internal_openapi_yaml.model.Participant;

import gen.org.tkit.onecx.chat.bff.rs.internal.model.AddParticipantDTO;
import gen.org.tkit.onecx.chat.bff.rs.internal.model.ParticipantDTO;

@Mapper
public interface ParticipantMapper {
    ParticipantDTO map(Participant participant);

    Participant map(ParticipantDTO participantDTO);

    AddParticipantDTO map(AddParticipant addParticipant);

    AddParticipant map(AddParticipantDTO addParticipantDTO);

    List<ParticipantDTO> map(List<Participant> participants);
}
