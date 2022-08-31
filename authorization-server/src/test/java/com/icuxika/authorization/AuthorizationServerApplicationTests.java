package com.icuxika.authorization;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.util.Date;

@SpringBootTest
class AuthorizationServerApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void jwtTest() {
        try {
            // RSA signatures require a public and private RSA key pair, the public key
            // must be made known to the JWS recipient in order to verify the signatures
            RSAKey rsaJWK = new RSAKeyGenerator(2048)
                    .keyID("123")
                    .generate();
            RSAKey rsaPublicJWK = rsaJWK.toPublicJWK();

            // Create RSA-signer with the private key
            JWSSigner signer = new RSASSASigner(rsaJWK);

            // Prepare JWT with claims set
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject("alice")
                    .issuer("https://c2id.com")
                    .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
                    claimsSet);

            // Compute the RSA signature
            signedJWT.sign(signer);

            // To serialize to compact form, produces something like
            // eyJhbGciOiJSUzI1NiJ9.SW4gUlNBIHdlIHRydXN0IQ.IRMQENi4nJyp4er2L
            // mZq3ivwoAjqa1uUkSBKFIX7ATndFF5ivnt-m8uApHO4kfIFOrW7w2Ezmlg3Qd
            // maXlS9DhN0nUk_hGI3amEjkKd0BWYCB8vfUbUv0XGjQip78AI4z1PrFRNidm7
            // -jPDm5Iq0SZnjKjCNS5Q15fokXZc8u0A
            String s = signedJWT.serialize();
            System.out.println(s);

            // On the consumer side, parse the JWS and verify its RSA signature
            signedJWT = SignedJWT.parse(s);

            JWSVerifier verifier = new RSASSAVerifier(rsaPublicJWK);

            System.out.println(signedJWT.verify(verifier));
            System.out.println(signedJWT.getJWTClaimsSet().getSubject());
            System.out.println(signedJWT.getJWTClaimsSet().getIssuer());

        } catch (JOSEException | ParseException e) {
            e.printStackTrace();
        }
    }

}
