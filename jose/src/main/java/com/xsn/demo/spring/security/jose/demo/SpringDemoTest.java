package com.xsn.demo.spring.security.jose.demo;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import java.time.Instant;

public class SpringDemoTest {

    @Test
    public void rs256() throws JOSEException, InterruptedException {
        RSAKey rsaKey = new RSAKeyGenerator(2048).generate();
        JwtEncoder encoder = new NimbusJwtEncoder(
                new ImmutableJWKSet<>(
                        new JWKSet(rsaKey)
                )
        );
        Jwt jwt = encoder.encode(JwtEncoderParameters.from(
                        JwsHeader.with(SignatureAlgorithm.RS256)
                                // .jwk(new HashMap<>() {{ put("publicKey", rsaKey.toPublicKey()); }})
                                .build()
                        , JwtClaimsSet.builder()
                                .expiresAt(Instant.now().plusSeconds(5))
                                .subject("xsn")
                                .claim("content", "hello spring jwt")
                                .build()
                )
        );
        String tokenValue = jwt.getTokenValue();

        // TimeUnit.SECONDS.sleep(6);

        DefaultJWTProcessor<SecurityContext> processor = new DefaultJWTProcessor<>();
        processor.setJWSKeySelector(new JWSVerificationKeySelector<SecurityContext>(
                JWSAlgorithm.RS256
                , new ImmutableJWKSet<>(new JWKSet(rsaKey.toPublicJWK()))
        ));
        JwtDecoder decoder = new NimbusJwtDecoder(processor);
        Jwt decode = decoder.decode(tokenValue);
        System.out.println(decode.getClaim(JwtClaimNames.SUB).toString());
        System.out.println(decode.getClaim("content").toString());
    }

}
