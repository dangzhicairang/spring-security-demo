package com.xsn.demo.spring.security.jose.demo;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NimbusDemoTest {

    @Test
    public void jwkMatcher() throws JOSEException {
        JWK jwk1 = new RSAKey.Builder(new RSAKeyGenerator(2048).generate())
                .keyID("id1")
                .keyOperations(new HashSet<>() {{ add(KeyOperation.SIGN); }})
                .build();
        JWK jwk2 = new RSAKey.Builder(new RSAKeyGenerator(2048).generate())
                .keyID("id1")
                .keyOperations(new HashSet<>() {{ add(KeyOperation.ENCRYPT); }})
                .build();
        JWK jwk3 = new RSAKey.Builder(new RSAKeyGenerator(2048).generate())
                .keyID("id3")
                .keyOperations(new HashSet<>() {{ add(KeyOperation.ENCRYPT); }})
                .build();
        JWKSet jwkSet = new JWKSet(new ArrayList<>() {{
            add(jwk1);
            add(jwk2);
            add(jwk3);
        }});

        JWKMatcher matcher1 = new JWKMatcher.Builder()
                .keyID("id1")
                .build();
        JWKMatcher matcher2 = new JWKMatcher.Builder()
                .keyOperations(KeyOperation.SIGN)
                .build();

        List<JWK> select1 = new JWKSelector(matcher1).select(jwkSet);
        List<JWK> select2 = new JWKSelector(matcher2).select(jwkSet);
        select1.forEach(System.out::println);
        System.out.println("----");
        select2.forEach(System.out::println);
    }

    @Test
    public void rs256() throws JOSEException, ParseException, InterruptedException {
        RSAKey rsaKey = new RSAKeyGenerator(2048)
                .generate();
        RSAKey publicJWK = rsaKey.toPublicJWK();
        String publicJWKJson = publicJWK.toJSONString();

        JWSSigner signer = new RSASSASigner(rsaKey);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .expirationTime(new Date(new Date().getTime() + 5 * 1000))
                .audience("xsn")
                .build();
        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.RS256)
                , jwtClaimsSet
        );
        signedJWT.sign(signer);
        String jwt = signedJWT.serialize();

        TimeUnit.SECONDS.sleep(6);

        SignedJWT parse = SignedJWT.parse(jwt);
        JWSVerifier verifier = new RSASSAVerifier(
                RSAKey.parse(publicJWKJson)
        );
        boolean verify = parse.verify(verifier);

        if (verify) {
            System.out.println(parse.getJWTClaimsSet().getAudience());
        }
    }

    @Test
    public void hs256() throws JOSEException, ParseException {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        JWSSigner signer = new MACSigner(bytes);
        JWSObject jwt = new JWSObject(
                new JWSHeader(JWSAlgorithm.HS256)
                , new Payload("hello jwt")
        );
        jwt.sign(signer);

        String serialize = jwt.serialize();

        JWSObject parse = JWSObject.parse(serialize);
        JWSVerifier verifier = new MACVerifier(bytes);
        boolean verify = parse.verify(verifier);
        if (verify) {
            System.out.println(parse.getPayload());
        }
    }
}
