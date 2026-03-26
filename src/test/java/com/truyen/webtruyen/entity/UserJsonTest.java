package com.truyen.webtruyen.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class UserJsonTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serialize_doesNotExposeEmailOrPassword() throws Exception {
        User u = new User();
        u.setId(1L);
        u.setUsername("alice");
        u.setEmail("alice@example.com");
        u.setPassword("secret");

        String json = mapper.writeValueAsString(u);

        assertFalse(json.contains("\"email\""), json);
        assertFalse(json.contains("\"password\""), json);
    }
}

