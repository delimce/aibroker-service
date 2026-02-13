package com.delimce.aibroker.infrastructure.adapters.springai;

import com.delimce.aibroker.domain.dto.requests.llm.ModelMessageRequest;
import com.delimce.aibroker.domain.dto.responses.llm.ModelChatResponse;
import com.delimce.aibroker.domain.mappers.llm.ChatResponseMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeepSeekAdapterTest {

    @Mock
    private ChatResponseMapper chatResponseMapper;

    @Test
    void mergeMessages_shouldReturnEmptyList_whenNullOrEmptyInput() {
        DeepSeekAdapter adapter = new DeepSeekAdapter(chatResponseMapper);

        List<Message> fromNull = adapter.mergeMessages(null);
        List<Message> fromEmpty = adapter.mergeMessages(new ModelMessageRequest[0]);

        assertNotNull(fromNull);
        assertNotNull(fromEmpty);
        assertTrue(fromNull.isEmpty());
        assertTrue(fromEmpty.isEmpty());
    }

    @Test
    void mergeMessages_shouldMapRolesToMessageTypes_andSkipNulls() {
        DeepSeekAdapter adapter = new DeepSeekAdapter(chatResponseMapper);

        ModelMessageRequest system = new ModelMessageRequest("system", "system-content");
        ModelMessageRequest assistant = new ModelMessageRequest("assistant", "assistant-content");
        ModelMessageRequest user = new ModelMessageRequest("user", "user-content");
        ModelMessageRequest defaultRole = new ModelMessageRequest(null, "default-content");

        List<Message> messages = adapter.mergeMessages(new ModelMessageRequest[] {
                system,
                null,
                assistant,
                user,
                defaultRole
        });

        assertEquals(4, messages.size());
        assertInstanceOf(SystemMessage.class, messages.get(0));
        assertInstanceOf(AssistantMessage.class, messages.get(1));
        assertInstanceOf(UserMessage.class, messages.get(2));
        assertInstanceOf(UserMessage.class, messages.get(3));

        assertEquals("system-content", messageContent(messages.get(0)));
        assertEquals("assistant-content", messageContent(messages.get(1)));
        assertEquals("user-content", messageContent(messages.get(2)));
        assertEquals("default-content", messageContent(messages.get(3)));
    }

    @Test
    void mapToModelChatResponse_shouldDelegateToMapper() {
        DeepSeekAdapter adapter = new DeepSeekAdapter(chatResponseMapper);
        ChatResponse response = mock(ChatResponse.class);
        ModelChatResponse expected = new ModelChatResponse();

        when(chatResponseMapper.toModelChatResponse(response)).thenReturn(expected);

        ModelChatResponse actual = adapter.mapToModelChatResponse(response);

        assertSame(expected, actual);
        verify(chatResponseMapper).toModelChatResponse(response);
    }

    private static String messageContent(Message message) {
        try {
            Method getText = message.getClass().getMethod("getText");
            return (String) getText.invoke(message);
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new IllegalStateException("Failed to read message text", ex);
        }

        try {
            Method getContent = message.getClass().getMethod("getContent");
            return (String) getContent.invoke(message);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException("Message content accessor not found", ex);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new IllegalStateException("Failed to read message content", ex);
        }
    }
}
