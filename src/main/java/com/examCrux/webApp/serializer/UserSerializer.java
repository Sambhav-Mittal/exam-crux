package com.examCrux.webApp.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.examCrux.webApp.entities.User;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class UserSerializer extends StdSerializer<User> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public UserSerializer() {
        this(null);
    }

    public UserSerializer(Class<User> t) {
        super(t);
    }

    @Override
    public void serialize(User user, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        gen.writeNumberField("id", user.getId());
        gen.writeStringField("email", user.getUsername());
        gen.writeStringField("profileImage", user.getProfileImage());
        gen.writeStringField("role", user.getRole().getRoleName());
        gen.writeStringField("createdAt", user.getCreatedAt().format(FORMATTER));
        gen.writeObjectField("status", user.getActive());
        gen.writeStringField("updatedAt", user.getUpdatedAt().format(FORMATTER));
        gen.writeEndObject();
    }
}
