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
        boolean enabled = jsonNode.get("enabled").asBoolean();
        boolean accountNonExpired = jsonNode.get("accountNonExpired").asBoolean();
        boolean accountNonLocked = jsonNode.get("accountNonLocked").asBoolean();
        boolean credentialsNonExpired = jsonNode.get("credentialsNonExpired").asBoolean();

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
