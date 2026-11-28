package dev.amigoes.starlighttimer.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;

@Configuration(proxyBeanMethods = false)
public class JwtConfig {

    private static final Pattern UUID_CANONICO = Pattern.compile(
            "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", Pattern.CASE_INSENSITIVE);

    @Bean
    SecretKey chaveJwt(@Value("${jwt.secret}") String segredo,
                       @Value("${jwt.validity_time}") Duration validade) {
        byte[] bytes = segredo.getBytes(StandardCharsets.UTF_8);
        if (segredo.isBlank() || bytes.length < 32) {
            throw new IllegalArgumentException("jwt.secret deve ter pelo menos 32 bytes em UTF-8");
        }
        if (validade == null || validade.isZero() || validade.isNegative()) {
            throw new IllegalArgumentException("jwt.validity_time deve ser positivo");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    JwtDecoder jwtDecoder(SecretKey chaveJwt, Clock clock) {
        var decoder = NimbusJwtDecoder.withSecretKey(chaveJwt).macAlgorithm(MacAlgorithm.HS256).build();
        var datas = new JwtTimestampValidator();
        datas.setClock(clock);
        datas.setAllowEmptyExpiryClaim(false);
        var sujeito = new JwtClaimValidator<String>(JwtClaimNames.SUB,
                valor -> valor != null && UUID_CANONICO.matcher(valor).matches());
        decoder.setJwtValidator(JwtValidators.createDefaultWithValidators(List.of(datas, sujeito)));
        return decoder;
    }

    @Bean
    JwtEncoder jwtEncoder(SecretKey chaveJwt) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(chaveJwt));
    }
}
