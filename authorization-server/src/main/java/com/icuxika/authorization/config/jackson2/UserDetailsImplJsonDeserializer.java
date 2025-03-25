package com.icuxika.authorization.config.jackson2;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icuxika.authorization.config.UserDetailsImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDetailsImplJsonDeserializer extends JsonDeserializer<UserDetailsImpl> {

    private final TypeReference<SimpleGrantedAuthority> typeReference = new TypeReference<>() {
    };

    @Override
    public UserDetailsImpl deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectMapper objectMapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode jsonNode = objectMapper.readTree(jsonParser);
        final JsonNode authoritiesNode = jsonNode.get("authorities");
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        if (authoritiesNode.isArray()) {
            for (JsonNode objNode : authoritiesNode) {
                if (objNode.isArray()) {
                    for (JsonNode authorityNode : objNode) {
                        SimpleGrantedAuthority simpleGrantedAuthority = objectMapper.readValue(authorityNode.toString(), typeReference);
                        authorityList.add(simpleGrantedAuthority);
                    }
                }
            }
        }
        Long id = jsonNode.get("id").asLong();
        String username = jsonNode.get("username").asText();
        JsonNode passwordNode = jsonNode.get("password");
        String password = passwordNode.asText("");
        boolean enabled = Optional.ofNullable(jsonNode.get("enabled")).map(JsonNode::asBoolean).orElse(true);
        boolean accountNonExpired = Optional.ofNullable(jsonNode.get("accountNonExpired")).map(JsonNode::asBoolean).orElse(true);
        boolean accountNonLocked = Optional.ofNullable(jsonNode.get("accountNonLocked")).map(JsonNode::asBoolean).orElse(true);
        boolean credentialsNonExpired = Optional.ofNullable(jsonNode.get("credentialsNonExpired")).map(JsonNode::asBoolean).orElse(true);

        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setAuthorities(authorityList);
        userDetails.setId(id);
        userDetails.setUsername(username);
        userDetails.setPassword(password);
        userDetails.setEnabled(enabled);
        userDetails.setAccountNonExpired(accountNonExpired);
        userDetails.setAccountNonLocked(accountNonLocked);
        userDetails.setCredentialsNonExpired(credentialsNonExpired);
        return userDetails;
    }

}
