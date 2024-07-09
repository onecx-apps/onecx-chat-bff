package org.tkit.onecx.chat.bff.rs.helper;

import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import gen.org.tkit.onecx.chat.bff.rs.internal.model.WebsocketHelperDTO;

public class MessageEncoder implements Encoder.Text<WebsocketHelperDTO> {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public String encode(WebsocketHelperDTO object) throws EncodeException {
        try {
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.registerModule(new JavaTimeModule());
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }
}
