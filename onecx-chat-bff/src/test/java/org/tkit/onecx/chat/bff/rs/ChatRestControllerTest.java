package org.tkit.onecx.chat.bff.rs;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.HttpHeaders;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.Header;
import org.mockserver.model.JsonBody;
import org.openapi.quarkus.onecx_chat_internal_openapi_yaml.model.*;
import org.tkit.onecx.chat.bff.rs.controllers.ChatRestController;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.permission.model.ProblemDetailResponse;
import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkiverse.mockserver.test.MockServerTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;

@QuarkusTest
@LogService
@TestHTTPEndpoint(ChatRestController.class)
@QuarkusTestResource(MockServerTestResource.class)
public class ChatRestControllerTest extends AbstractTest {

    @InjectMockServerClient
    public MockServerClient mockServerClient;

    KeycloakTestClient keycloakTestClient = new KeycloakTestClient();

    static final String mockId = "MOCK";

    @BeforeEach
    public void resetExpectation() {
        try {
            mockServerClient.clear(mockId);
        } catch (Exception e) {
            // mockid not existing
        }
    }

    @Test
    public void getChatChatByIdTest() {
        var chatId = "id";
        String result = "{\r\n" +
                "  \"version\": 0,\r\n" +
                "  \"creationDate\": \"2022-03-10T12:15:50-04:00\",\r\n" +
                "  \"creationUser\": \"string\",\r\n" +
                "  \"modificationDate\": \"2022-03-10T12:15:50-04:00\",\r\n" +
                "  \"modificationUser\": \"string\",\r\n" +
                "  \"id\": \"id\",\r\n" +
                "  \"type\": \"HUMAN_CHAT\",\r\n" +
                "  \"topic\": \"string\",\r\n" +
                "  \"summary\": \"string\",\r\n" +
                "  \"appId\": \"string\",\r\n" +
                "  \"participants\": [\r\n" +
                "    {\r\n" +
                "      \"version\": 0,\r\n" +
                "      \"creationDate\": \"2022-03-10T12:15:50-04:00\",\r\n" +
                "      \"creationUser\": \"string\",\r\n" +
                "      \"modificationDate\": \"2022-03-10T12:15:50-04:00\",\r\n" +
                "      \"modificationUser\": \"string\",\r\n" +
                "      \"id\": \"string\",\r\n" +
                "      \"type\": \"HUMAN\",\r\n" +
                "      \"userId\": \"string\",\r\n" +
                "      \"userName\": \"string\",\r\n" +
                "      \"email\": \"string\"\r\n" +
                "    }\r\n" +
                "  ]\r\n" +
                "}";

        mockServerClient.when(request()
                .withPath("/internal/chats/" + chatId)
                .withMethod(HttpMethod.GET))
                .withId(mockId)
                .respond(httpRequest -> response().withStatusCode(OK.getStatusCode())
                        .withHeaders(
                                new Header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON))
                        .withBody(result));

        var res = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("id", chatId)
                .get()
                .then()
                .statusCode(OK.getStatusCode())
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                .extract().body().asString();

        assertThat(res).isNotNull().isNotEmpty();
    }

    @Test
    public void getChatById_shouldReturnBadRequest() {
        var chatId = "id";

        ProblemDetailResponse problemDetailResponse = new ProblemDetailResponse();
        problemDetailResponse.setErrorCode(String.valueOf(BAD_REQUEST));
        problemDetailResponse.setDetail("Bad request");

        mockServerClient.when(request()
                .withPath("/internal/chats/" + chatId)
                .withMethod(HttpMethod.GET))
                .withId(mockId)
                .respond(httpRequest -> response().withStatusCode(BAD_REQUEST.getStatusCode())
                        .withBody(JsonBody.json(problemDetailResponse)));

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("id", chatId)
                .get()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    public void addParticipantTest() {
        var chatId = "id";
        AddParticipant addParticipant = new AddParticipant();
        addParticipant.setUserId("userId");
        addParticipant.setType(ParticipantType.HUMAN);

        Participant participant = new Participant();
        participant.setUserId("ParticipantId");
        participant.setType(ParticipantType.HUMAN);

        mockServerClient.when(request()
                .withPath("/internal/chats/" + chatId + "/participants")
                .withMethod(HttpMethod.POST))
                .withId(mockId)
                .respond(httpRequest -> response().withStatusCode(OK.getStatusCode())
                        .withBody(JsonBody.json(participant)));

        var res = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("id", chatId)
                .post()
                .then()
                .statusCode(OK.getStatusCode())
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                .extract().body().asString();

        assertThat(res).isNotNull().isNotEmpty();
    }

    @Test
    public void createChatTest() {
        CreateChat createChat = new CreateChat();
        createChat.setType(ChatType.AI_CHAT);

        Chat chat = new Chat();
        chat.setType(ChatType.AI_CHAT);

        mockServerClient.when(request()
                .withPath("/internal/chats")
                .withMethod(HttpMethod.POST))
                .withId(mockId)
                .respond(httpRequest -> response().withStatusCode(OK.getStatusCode())
                        .withBody(JsonBody.json(chat)));

        var res = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .post()
                .then()
                .statusCode(OK.getStatusCode())
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                .extract().body().asString();

        assertThat(res).isNotNull().isNotEmpty();
    }

    @Test
    public void createChatMessageTest() {
        var chatId = "id";

        CreateMessage createMessage = new CreateMessage();
        createMessage.setType(MessageType.HUMAN);

        Message message = new Message();
        message.setType(MessageType.HUMAN);

        mockServerClient.when(request()
                .withPath("/internal/chats/" + chatId + "/messages")
                .withMethod(HttpMethod.POST))
                .withId(mockId)
                .respond(httpRequest -> response().withStatusCode(OK.getStatusCode())
                        .withBody(JsonBody.json(message)));

        var res = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .post()
                .then()
                .statusCode(OK.getStatusCode())
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                .extract().body().asString();

        assertThat(res).isNotNull().isNotEmpty();
    }

    @Test
    public void deleteChatTest() {
        var chatId = "id";

        mockServerClient.when(request()
                .withPath("/internal/chats/" + chatId)
                .withMethod(HttpMethod.DELETE))
                .withId(mockId)
                .respond(httpRequest -> response().withStatusCode(OK.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .pathParam("id", chatId)
                .delete()
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    public void getChatMessagesTest() {
        var chatId = "id";

        Message message = new Message();
        message.setType(MessageType.HUMAN);

        List<Message> messages = new ArrayList<>();
        messages.add(message);

        mockServerClient.when(request()
                .withPath("/internal/chats/" + chatId + "/messages")
                .withMethod(HttpMethod.GET))
                .withId(mockId)
                .respond(httpRequest -> response().withStatusCode(OK.getStatusCode())
                        .withBody(JsonBody.json(messages)));

        var res = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .pathParam("id", chatId)
                .get()
                .then()
                .statusCode(OK.getStatusCode())
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                .extract().body().asString();

        assertThat(res).isNotNull().isNotEmpty();
    }

    @Test
    public void getChatParticipantsTest() {
        var chatId = "id";

        Participant participant = new Participant();
        participant.setType(ParticipantType.HUMAN);

        List<Participant> participants = new ArrayList<>();
        participants.add(participant);

        mockServerClient.when(request()
                .withPath("/internal/chats/" + chatId + "/participants")
                .withMethod(HttpMethod.GET))
                .withId(mockId)
                .respond(httpRequest -> response().withStatusCode(OK.getStatusCode())
                        .withBody(JsonBody.json(participants)));

        var res = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .pathParam("id", chatId)
                .get()
                .then()
                .statusCode(OK.getStatusCode())
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                .extract().body().asString();

        assertThat(res).isNotNull().isNotEmpty();
    }

    @Test
    public void getChatsTest() {
        //TODO
    }

    @Test
    public void searchChatsTest() {
        //TODO
    }

    @Test
    public void updateChatTest() {
        var chatId = "id";

        Chat chat = new Chat();
        chat.setType(ChatType.HUMAN_CHAT);

        mockServerClient.when(request()
                .withPath("/internal/chats/" + chatId)
                .withMethod(HttpMethod.PUT))
                .withId(mockId)
                .respond(httpRequest -> response().withStatusCode(OK.getStatusCode())
                        .withBody(JsonBody.json(chat)));

        var res = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .pathParam("id", chatId)
                .put()
                .then()
                .statusCode(OK.getStatusCode())
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                .extract().body().asString();

        assertThat(res).isNotNull().isNotEmpty();
    }

    @Test
    public void removeParticipant() {
        //TODO
    }

}
