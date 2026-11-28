package dev.amigoes.starlighttimer.core.identity.usecase;

import dev.amigoes.starlighttimer.core.identity.domain.Usuario;
import dev.amigoes.starlighttimer.core.identity.persistence.IdentidadeEntity;
import dev.amigoes.starlighttimer.core.identity.persistence.IdentidadeRepository;
import dev.amigoes.starlighttimer.enums.identity.ProvedorIdentidade;
import dev.amigoes.starlighttimer.exceptions.RegraDeNegocioException;
import dev.amigoes.starlighttimer.records.identity.CadastroContaRequest;
import dev.amigoes.starlighttimer.records.identity.CadastroUsuarioComando;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class CadastrarContaService {

    private final CadastrarUsuarioService cadastro;
    private final IdentidadeRepository identidades;
    private final PasswordEncoder passwordEncoder;

    public CadastrarContaService(CadastrarUsuarioService cadastro, IdentidadeRepository identidades,
                                 PasswordEncoder passwordEncoder) {
        this.cadastro = cadastro;
        this.identidades = identidades;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario cadastrar(CadastroContaRequest request) {
        if (request.senha().getBytes(UTF_8).length > 72) {
            throw new RegraDeNegocioException("Dados de cadastro invalidos");
        }

        String senhaHash = passwordEncoder.encode(request.senha());
        Usuario usuario = cadastro.cadastrar(new CadastroUsuarioComando(
                request.email(), request.nomeExibido(), request.fusoHorario()));
        identidades.save(new IdentidadeEntity(UUID.randomUUID(), usuario.getId(),
                ProvedorIdentidade.PASSWORD.name(), null, senhaHash));
        return usuario;
    }
}
