package dev.amigoes.starlighttimer.core.social.usecase;

import dev.amigoes.starlighttimer.core.identity.usecase.ConsultarIdentidadeService;
import dev.amigoes.starlighttimer.core.social.domain.RegraAmizadeVioladaExcecao;
import dev.amigoes.starlighttimer.core.social.persistence.AmizadeRepository;
import dev.amigoes.starlighttimer.core.social.persistence.BloqueioRepository;
import dev.amigoes.starlighttimer.core.social.persistence.PedidoAmizadeRepository;
import dev.amigoes.starlighttimer.exceptions.RecursoNaoEncontradoException;
import dev.amigoes.starlighttimer.records.identity.ResumoPerfilPublico;
import dev.amigoes.starlighttimer.records.social.Amizade;
import dev.amigoes.starlighttimer.records.social.PedidosPendentes;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

@Service
public class GerenciarCirculoSocialService {
    private final ConsultarIdentidadeService identidade;
    private final AmizadeRepository amizades;
    private final PedidoAmizadeRepository pedidos;
    private final BloqueioRepository bloqueios;
    private final Clock relogio;

    public GerenciarCirculoSocialService(ConsultarIdentidadeService identidade, AmizadeRepository amizades,
                                        PedidoAmizadeRepository pedidos, BloqueioRepository bloqueios,
                                        Clock relogio) {
        this.identidade = identidade;
        this.amizades = amizades;
        this.pedidos = pedidos;
        this.bloqueios = bloqueios;
        this.relogio = relogio;
    }

    @Transactional(readOnly = true)
    public List<ResumoPerfilPublico> listarAmigos() {
        UUID usuarioId = identidade.obterUsuarioAtual();
        return amizades.listarPorUsuario(usuarioId).stream()
                .map(amizade -> identidade.obterPerfilPublico(outroUsuario(amizade, usuarioId)))
                .toList();
    }

    @Transactional(readOnly = true)
    public PedidosPendentes listarPedidos() {
        UUID usuarioId = identidade.obterUsuarioAtual();
        return new PedidosPendentes(pedidos.listarPendentesRecebidos(usuarioId),
                pedidos.listarPendentesEnviados(usuarioId));
    }

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
