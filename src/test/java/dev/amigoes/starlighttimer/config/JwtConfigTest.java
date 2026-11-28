package dev.amigoes.starlighttimer.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtConfigTest {

    private static final Instant AGORA = Instant.parse("2000-01-01T00:00:00Z");

    private final ApplicationContextRunner contexto = new ApplicationContextRunner()
            .withInitializer(ctx -> ctx.getBeanFactory().setConversionService(new ApplicationConversionService()))
            .withBean(Clock.class, () -> Clock.fixed(AGORA, ZoneOffset.UTC))
            .withUserConfiguration(JwtConfig.class);

    @Test
    void rejeitaSegredoComMenosDe32Bytes() {
        contexto.withPropertyValues("jwt.secret=" + "é".repeat(15), "jwt.validity_time=3600000")
                .run(ctx -> assertThat(ctx.getStartupFailure())
                        .hasRootCauseInstanceOf(IllegalArgumentException.class)
                        .hasRootCauseMessage("jwt.secret deve ter pelo menos 32 bytes em UTF-8"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "0", "-1"})
    void rejeitaValidadeAusenteOuNaoPositiva(String validade) {
        contexto.withPropertyValues("jwt.secret=" + "é".repeat(16), "jwt.validity_time=" + validade)
                .run(ctx -> assertThat(ctx.getStartupFailure())
                        .hasRootCauseInstanceOf(IllegalArgumentException.class)
                        .hasRootCauseMessage("jwt.validity_time deve ser positivo"));
    }

    @Test
    void aceitaValidadeNumericaEChaveDe32BytesUtf8() {
        contexto.withPropertyValues("jwt.secret=" + "é".repeat(16), "jwt.validity_time=3600000")
                .run(ctx -> {
                    assertThat(ctx).hasSingleBean(SecretKey.class)
                            .hasSingleBean(JwtEncoder.class)
                            .hasSingleBean(JwtDecoder.class);
                    assertThat(ctx.getBean(SecretKey.class).getEncoded()).hasSize(32);
                });
    }

    @Test
    void validaDatasPeloRelogioInjetado() {
        String sujeito = UUID.randomUUID().toString();
        contexto.withPropertyValues("jwt.secret=" + "é".repeat(16), "jwt.validity_time=3600000")
                .run(ctx -> {
                    var claims = JwtClaimsSet.builder()
                            .subject(sujeito)
                            .issuedAt(AGORA)
                            .expiresAt(AGORA.plusSeconds(300))
                            .build();
                    var cabecalho = JwsHeader.with(MacAlgorithm.HS256).build();
                    var token = ctx.getBean(JwtEncoder.class).encode(JwtEncoderParameters.from(cabecalho, claims));

                    assertThat(ctx.getBean(JwtDecoder.class).decode(token.getTokenValue()).getSubject())
                            .isEqualTo(sujeito);
                });
    }
}
