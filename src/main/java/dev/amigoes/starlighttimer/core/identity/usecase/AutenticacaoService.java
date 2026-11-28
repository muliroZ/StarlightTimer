package dev.amigoes.starlighttimer.core.identity.usecase;

import dev.amigoes.starlighttimer.core.identity.domain.UsuarioNaoAutenticadoException;
import dev.amigoes.starlighttimer.core.identity.persistence.IdentidadeEntity;
import dev.amigoes.starlighttimer.core.identity.persistence.IdentidadeRepository;
import dev.amigoes.starlighttimer.core.identity.persistence.RefreshTokenRepository;
import dev.amigoes.starlighttimer.core.identity.persistence.UsuarioRepository;
import dev.amigoes.starlighttimer.enums.identity.ProvedorIdentidade;
import dev.amigoes.starlighttimer.records.identity.LoginRequest;
import dev.amigoes.starlighttimer.records.identity.TokensEmitidos;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class AutenticacaoService {

    private static final String CREDENCIAIS_INVALIDAS = "Credenciais invalidas";
    private static final String REFRESH_INVALIDO = "Refresh token invalido ou expirado";

    private final UsuarioRepository usuarios;
    private final IdentidadeRepository identidades;
    private final RefreshTokenRepository refreshTokens;
    private final PasswordEncoder passwordEncoder;
    private final EmissorTokens emissor;
    private final Clock clock;
    private final String hashFicticio;

    public AutenticacaoService(UsuarioRepository usuarios, IdentidadeRepository identidades,
                               RefreshTokenRepository refreshTokens, PasswordEncoder passwordEncoder,
                               EmissorTokens emissor, Clock clock) {
        this.usuarios = usuarios;
        this.identidades = identidades;
        this.refreshTokens = refreshTokens;
        this.passwordEncoder = passwordEncoder;
        this.emissor = emissor;
        this.clock = clock;
        this.hashFicticio = passwordEncoder.encode("credencial-ficticia");
    }

    @Transactional
    public TokensEmitidos login(LoginRequest request, String userAgent) {
        if (request.senha().getBytes(UTF_8).length > 72) {
            throw new UsuarioNaoAutenticadoException(CREDENCIAIS_INVALIDAS);
        }

        var identidade = usuarios.findByEmail(request.email())
                .filter(usuario -> "ACTIVE".equals(usuario.getStatus()) && usuario.getExcluidoEm() == null)
                .flatMap(usuario -> identidades.findByUsuarioIdAndProvedor(
                        usuario.getId(), ProvedorIdentidade.PASSWORD.name()));
        String hash = identidade.map(IdentidadeEntity::getSenhaHash).orElse(hashFicticio);
        boolean senhaConfere = passwordEncoder.matches(request.senha(), hash);
        if (!senhaConfere || identidade.isEmpty()) {
            throw new UsuarioNaoAutenticadoException(CREDENCIAIS_INVALIDAS);
        }

        return emissor.emitir(identidade.get().getUsuarioId(), userAgent);
    }

    @Transactional
    public TokensEmitidos renovar(String refreshToken, String userAgent) {
        var token = refreshTokens.findByTokenHash(emissor.hash(refreshToken))
                .orElseThrow(() -> new UsuarioNaoAutenticadoException(REFRESH_INVALIDO));
        Instant agora = clock.instant();

        if (!token.estaAtivoEm(agora) || !usuarios.existeAtivoPorId(token.getUsuarioId())
                || refreshTokens.revogarSeAtivo(token.getId(), agora) != 1) {
            throw new UsuarioNaoAutenticadoException(REFRESH_INVALIDO);
        }

        return emissor.emitir(token.getUsuarioId(), userAgent);
    }

    @Transactional
    public void sair(String refreshToken) {
        Instant agora = clock.instant();
        refreshTokens.findByTokenHash(emissor.hash(refreshToken))
                .filter(token -> token.estaAtivoEm(agora))
                .ifPresent(token -> refreshTokens.revogarSeAtivo(token.getId(), agora));
    }
}
