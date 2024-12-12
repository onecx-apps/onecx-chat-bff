package org.tkit.onecx.chat.bff.rs.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import gen.org.tkit.onecx.chat.bff.clients.model.AddParticipant;
import gen.org.tkit.onecx.chat.bff.clients.model.Participant;
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
