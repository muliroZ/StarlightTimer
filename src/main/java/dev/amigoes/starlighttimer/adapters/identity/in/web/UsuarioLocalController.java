package dev.amigoes.starlighttimer.adapters.identity.in.web;

import dev.amigoes.starlighttimer.app.identity.dto.CadastroUsuarioComando;
import dev.amigoes.starlighttimer.app.identity.ports.in.CadastrarUsuarioPort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Profile("local")
@RequestMapping("/api/v1/usuarios/local")
public class UsuarioLocalController {
    private final CadastrarUsuarioPort cadastro;

    public UsuarioLocalController(CadastrarUsuarioPort cadastro) {
        this.cadastro = cadastro;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioLocalResponse criar(@Valid @RequestBody CriarUsuarioLocalRequest request) {
        var usuario = cadastro.cadastrar(new CadastroUsuarioComando(
                request.email(), request.nomeExibido(), request.fusoHorario()));
        return new UsuarioLocalResponse(usuario.getId(), usuario.getEmail(),
                usuario.getPerfil().getNomeExibido(), usuario.getPerfil().getFusoHorario());
    }

    public record CriarUsuarioLocalRequest(
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(max = 50) String nomeExibido,
            @NotBlank @Size(max = 40) String fusoHorario) {}

    public record UsuarioLocalResponse(UUID id, String email, String nomeExibido, String fusoHorario) {}
}
