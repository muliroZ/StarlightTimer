package dev.amigoes.starlighttimer.app.identity.usecases;

import dev.amigoes.starlighttimer.app.identity.dto.CadastroUsuarioComando;
import dev.amigoes.starlighttimer.app.identity.ports.in.CadastrarUsuarioPort;
import dev.amigoes.starlighttimer.app.identity.ports.out.UsuarioRepositoryPort;
import dev.amigoes.starlighttimer.domain.identity.entities.ConfiguracoesUsuario;
import dev.amigoes.starlighttimer.domain.identity.entities.Perfil;
import dev.amigoes.starlighttimer.domain.identity.entities.Usuario;
import dev.amigoes.starlighttimer.domain.identity.exceptions.EmailJaCadastradoException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.util.Objects;
import java.util.UUID;

@Service
public class CadastrarUsuarioService implements CadastrarUsuarioPort {
    private final UsuarioRepositoryPort usuarios;
    private final Clock relogio;
    private final Validator validator;

    public CadastrarUsuarioService(UsuarioRepositoryPort usuarios, Clock relogio, Validator validator) {
        this.usuarios = usuarios;
        this.relogio = relogio;
        this.validator = validator;
    }

    @Override
    @Transactional
    public Usuario cadastrar(CadastroUsuarioComando comando) {
        Objects.requireNonNull(comando, "O comando de cadastro e obrigatorio");
        var violacoes = validator.validate(comando);
        if (!violacoes.isEmpty()) {
            throw new ConstraintViolationException(violacoes);
        }

        UUID id = UUID.randomUUID();
        // O construtor do dominio da #18 valida o fuso antes de qualquer INSERT.
        Perfil perfil = new Perfil(id, comando.nomeExibido(), null, null,
                comando.fusoHorario(), 0);
        ConfiguracoesUsuario configuracoes = new ConfiguracoesUsuario(
                id, 1500, 300, true, true);

        if (usuarios.existePorEmail(comando.email())) {
            throw new EmailJaCadastradoException();
        }

        Usuario usuario = new Usuario(id, comando.email(), "ACTIVE",
                relogio.instant(), null, perfil, configuracoes);
        usuarios.criar(usuario);
        return usuario;
    }
}
