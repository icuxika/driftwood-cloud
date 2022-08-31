package com.icuxika.authorization.config.jackson2;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.icuxika.authorization.config.phone.PhoneVerificationAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.List;

/**
 * Copy from org.springframework.security.jackson2.UsernamePasswordAuthenticationTokenDeserializer
 */
public class PhoneVerificationAuthenticationTokenJsonDeserializer extends JsonDeserializer<PhoneVerificationAuthenticationToken> {

    private static final TypeReference<List<GrantedAuthority>> GRANTED_AUTHORITY_LIST = new TypeReference<>() {
    };

    private static final TypeReference<Object> OBJECT = new TypeReference<>() {
    };

    @Override
    public PhoneVerificationAuthenticationToken deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectMapper objectMapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode jsonNode = objectMapper.readTree(jsonParser);
        Boolean authenticated = readJsonNode(jsonNode, "authenticated").asBoolean();
        JsonNode principalNode = readJsonNode(jsonNode, "principal");
        Object principal = getPrincipal(objectMapper, principalNode);
        JsonNode credentialsNode = readJsonNode(jsonNode, "credentials");
        Object credentials = getCredentials(credentialsNode);
        List<GrantedAuthority> authorities = objectMapper.readValue(readJsonNode(jsonNode, "authorities").traverse(objectMapper),
                GRANTED_AUTHORITY_LIST);

        PhoneVerificationAuthenticationToken token = (!authenticated)
                ? new PhoneVerificationAuthenticationToken(principal, credentials)
                : new PhoneVerificationAuthenticationToken(principal, credentials, authorities);
        JsonNode detailsNode = readJsonNode(jsonNode, "details");
        if (detailsNode.isNull() || detailsNode.isMissingNode()) {
            token.setDetails(null);
        } else {
            Object details = objectMapper.readValue(detailsNode.toString(), OBJECT);
            token.setDetails(details);
        }
        return token;
    }

    private Object getCredentials(JsonNode credentialsNode) {
        if (credentialsNode.isNull() || credentialsNode.isMissingNode()) {
            return null;
        }
        return credentialsNode.asText();
    }

    private Object getPrincipal(ObjectMapper mapper, JsonNode principalNode)
            throws IOException {
        if (principalNode.isObject()) {
            return mapper.readValue(principalNode.traverse(mapper), Object.class);
        }
        return principalNode.asText();
    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }
}
