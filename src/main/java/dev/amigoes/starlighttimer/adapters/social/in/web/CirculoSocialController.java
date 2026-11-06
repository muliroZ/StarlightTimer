package dev.amigoes.starlighttimer.adapters.social.in.web;

import dev.amigoes.starlighttimer.app.social.ports.in.GerenciarCirculoSocialUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class CirculoSocialController {
    private final GerenciarCirculoSocialUseCase circulo;

    public CirculoSocialController(GerenciarCirculoSocialUseCase circulo) {
        this.circulo = circulo;
    }

    @GetMapping("/me/amigos")
    public List<AmigoResponse> listarAmigos() {
        return circulo.listarAmigos().stream().map(AmigoResponse::de).toList();
    }

    @GetMapping("/me/pedidos")
    public PedidosPendentesResponse listarPedidos() {
        return PedidosPendentesResponse.de(circulo.listarPedidos());
    }

    @DeleteMapping("/amizades/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desfazerAmizade(@PathVariable UUID id) {
        circulo.desfazerAmizade(id);
    }

    @PostMapping("/me/bloqueios/{usuarioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void bloquear(@PathVariable UUID usuarioId) {
        circulo.bloquear(usuarioId);
    }

    @DeleteMapping("/me/bloqueios/{usuarioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desbloquear(@PathVariable UUID usuarioId) {
        circulo.desbloquear(usuarioId);
    }
}
