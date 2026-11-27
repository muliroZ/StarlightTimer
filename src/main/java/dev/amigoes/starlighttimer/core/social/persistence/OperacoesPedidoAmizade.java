package dev.amigoes.starlighttimer.core.social.persistence;

import dev.amigoes.starlighttimer.core.social.domain.PedidoAmizade;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

interface OperacoesPedidoAmizade {
    @Transactional(propagation = Propagation.MANDATORY)
    void bloquearPar(UUID primeiro, UUID segundo);

    @Transactional(readOnly = true)
    Optional<PedidoAmizade> buscarPorId(UUID id);

    @Transactional(propagation = Propagation.MANDATORY)
    void criar(PedidoAmizade pedido);
}
