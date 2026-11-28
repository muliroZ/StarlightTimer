package dev.amigoes.starlighttimer.core.identity.usecase;

import dev.amigoes.starlighttimer.core.identity.persistence.RefreshTokenEntity;
import dev.amigoes.starlighttimer.core.identity.persistence.RefreshTokenRepository;
import dev.amigoes.starlighttimer.records.identity.TokensEmitidos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class EmissorTokens {

    private final JwtEncoder jwtEncoder;
    private final RefreshTokenRepository refreshTokens;
    private final Clock clock;
    private final Duration validadeAccess;
    private final Duration validadeRefresh;
    private final SecureRandom random = new SecureRandom();

    public EmissorTokens(JwtEncoder jwtEncoder, RefreshTokenRepository refreshTokens, Clock clock,
                         @Value("${jwt.validity_time}") Duration validadeAccess,
                         @Value("${jwt.refresh_validity_time}") Duration validadeRefresh) {
        if (validadeRefresh == null || validadeRefresh.isZero() || validadeRefresh.isNegative()) {
            throw new IllegalArgumentException("jwt.refresh_validity_time deve ser positivo");
        }

        this.jwtEncoder = jwtEncoder;
        this.refreshTokens = refreshTokens;
        this.clock = clock;
        this.validadeAccess = validadeAccess;
        this.validadeRefresh = validadeRefresh;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public TokensEmitidos emitir(UUID usuarioId, String userAgent) {
        Instant agora = clock.instant();
        var claims = JwtClaimsSet.builder()
                .subject(usuarioId.toString())
                .issuedAt(agora)
                .expiresAt(agora.plus(validadeAccess))
                .id(UUID.randomUUID().toString())
                .build();
        var cabecalho = JwsHeader.with(MacAlgorithm.HS256).build();
        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(cabecalho, claims)).getTokenValue();

        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String refreshToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        refreshTokens.save(new RefreshTokenEntity(UUID.randomUUID(), usuarioId, hash(refreshToken),
                agora.plus(validadeRefresh), agora, null, userAgent));
        return new TokensEmitidos(accessToken, refreshToken, validadeAccess.toSeconds());
    }

    public String hash(String token) {
        try {
            var sha256 = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(sha256.digest(token.getBytes(UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponivel", e);
        }
    }
}
