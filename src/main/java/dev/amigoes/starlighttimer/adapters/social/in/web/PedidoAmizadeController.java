package dev.amigoes.starlighttimer.adapters.social.in.web;

import dev.amigoes.starlighttimer.app.social.ports.in.GerenciarPedidosAmizadePort;
import dev.amigoes.starlighttimer.app.social.ports.in.ResultadoPedidoAmizade;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/amizades/pedidos")
public class PedidoAmizadeController {
    private final GerenciarPedidosAmizadePort pedidos;

    public PedidoAmizadeController(GerenciarPedidosAmizadePort pedidos) {
        this.pedidos = pedidos;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoAmizadeResponse enviar(@Valid @RequestBody EnviarPedidoAmizadeRequest request) {
        return PedidoAmizadeResponse.de(pedidos.enviar(request.destinatarioId()));
    }

    @PatchMapping("/{id}")
    public Object responder(@PathVariable("id") UUID id, @Valid @RequestBody ResponderPedidoAmizadeRequest request) {
        return switch (pedidos.responder(id, request.acao())) {
            case ResultadoPedidoAmizade.Aceito aceito -> AmizadeResponse.de(aceito.amizade());
            case ResultadoPedidoAmizade.Respondido respondido -> PedidoAmizadeResponse.de(respondido.pedido());
        };
    }
}
