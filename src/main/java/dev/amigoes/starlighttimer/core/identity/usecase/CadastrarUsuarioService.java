package dev.amigoes.starlighttimer.core.identity.usecase;

import dev.amigoes.starlighttimer.core.identity.domain.ConfiguracoesUsuario;
import dev.amigoes.starlighttimer.core.identity.domain.EmailJaCadastradoException;
import dev.amigoes.starlighttimer.core.identity.domain.Perfil;
import dev.amigoes.starlighttimer.core.identity.domain.Usuario;
import dev.amigoes.starlighttimer.core.identity.persistence.UsuarioRepository;
import dev.amigoes.starlighttimer.records.identity.CadastroUsuarioComando;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Objects;
import java.util.UUID;

@Service
public class CadastrarUsuarioService {
    private final UsuarioRepository usuarios;
    private final Clock relogio;
    private final Validator validator;

    public CadastrarUsuarioService(UsuarioRepository usuarios, Clock relogio, Validator validator) {
        this.usuarios = usuarios;
        this.relogio = relogio;
        this.validator = validator;
    }

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
