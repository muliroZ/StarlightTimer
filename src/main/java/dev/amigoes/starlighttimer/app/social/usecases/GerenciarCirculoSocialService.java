package dev.amigoes.starlighttimer.app.social.usecases;

import dev.amigoes.starlighttimer.app.identity.ports.in.ConsultarIdentidadePort;
import dev.amigoes.starlighttimer.app.identity.ports.in.ResumoPerfilPublico;
import dev.amigoes.starlighttimer.app.social.ports.in.GerenciarCirculoSocialUseCase;
import dev.amigoes.starlighttimer.app.social.ports.out.AmizadeRepositoryPort;
import dev.amigoes.starlighttimer.app.social.ports.out.BloqueioRepositoryPort;
import dev.amigoes.starlighttimer.app.social.ports.out.PedidoAmizadeRepositoryPort;
import dev.amigoes.starlighttimer.domain.shared.exceptions.RecursoNaoEncontradoException;
import dev.amigoes.starlighttimer.domain.social.entities.Amizade;
import dev.amigoes.starlighttimer.domain.social.exceptions.RegraAmizadeVioladaExcecao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

@Service
public class GerenciarCirculoSocialService implements GerenciarCirculoSocialUseCase {
    private final ConsultarIdentidadePort identidade;
    private final AmizadeRepositoryPort amizades;
    private final PedidoAmizadeRepositoryPort pedidos;
    private final BloqueioRepositoryPort bloqueios;
    private final Clock relogio;

    public GerenciarCirculoSocialService(ConsultarIdentidadePort identidade, AmizadeRepositoryPort amizades,
                                        PedidoAmizadeRepositoryPort pedidos, BloqueioRepositoryPort bloqueios,
                                        Clock relogio) {
        this.identidade = identidade;
        this.amizades = amizades;
        this.pedidos = pedidos;
        this.bloqueios = bloqueios;
        this.relogio = relogio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumoPerfilPublico> listarAmigos() {
        UUID usuarioId = identidade.obterUsuarioAtual();
        return amizades.listarPorUsuario(usuarioId).stream()
                .map(amizade -> identidade.obterPerfilPublico(outroUsuario(amizade, usuarioId)))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PedidosPendentes listarPedidos() {
        UUID usuarioId = identidade.obterUsuarioAtual();
        return new PedidosPendentes(pedidos.listarPendentesRecebidos(usuarioId),
                pedidos.listarPendentesEnviados(usuarioId));
    }

    @Override
    @Transactional
    public void desfazerAmizade(UUID amizadeId) {
        UUID usuarioId = identidade.obterUsuarioAtual();
        Amizade amizade = amizades.buscarPorId(amizadeId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Amizade nao encontrada"));
        UUID outroId = outroUsuario(amizade, usuarioId);
        pedidos.bloquearPar(usuarioId, outroId);
        Amizade atual = amizades.buscarPorId(amizadeId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Amizade nao encontrada"));
        outroUsuario(atual, usuarioId);
        amizades.remover(amizadeId);
    }

    @Override
    @Transactional
    public void bloquear(UUID usuarioId) {
        UUID atualId = identidade.obterUsuarioAtual();
        if (atualId.equals(usuarioId)) {
            throw new RegraAmizadeVioladaExcecao("Nao e permitido bloquear a si mesmo");
        }
        if (!identidade.existeUsuarioAtivo(usuarioId)) {
            throw new RecursoNaoEncontradoException("Usuario nao encontrado");
        }
        pedidos.bloquearPar(atualId, usuarioId);
        bloqueios.criar(atualId, usuarioId);
        amizades.removerEntre(atualId, usuarioId);
        pedidos.cancelarPendentesEntre(atualId, usuarioId, relogio.instant());
    }

    @Override
    @Transactional
    public void desbloquear(UUID usuarioId) {
        UUID atualId = identidade.obterUsuarioAtual();
        if (atualId.equals(usuarioId)) {
            throw new RegraAmizadeVioladaExcecao("Nao e permitido desbloquear a si mesmo");
        }
        pedidos.bloquearPar(atualId, usuarioId);
        bloqueios.remover(atualId, usuarioId);
    }

    private UUID outroUsuario(Amizade amizade, UUID usuarioId) {
        if (amizade.usuario1Id().equals(usuarioId)) {
            return amizade.usuario2Id();
        }
        if (amizade.usuario2Id().equals(usuarioId)) {
            return amizade.usuario1Id();
        }
        throw new RecursoNaoEncontradoException("Amizade nao encontrada");
    }
}
