package dev.amigoes.starlighttimer.core.session.usecase;

import dev.amigoes.starlighttimer.core.session.domain.ConclusaoAntecipadaException;
import dev.amigoes.starlighttimer.core.session.persistence.SessaoRepository;
import dev.amigoes.starlighttimer.enums.session.StatusSessao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
public class EncerrarSessoesVencidasService {
    private final SessaoRepository sessoes;
    private final TransicoesSessaoService transicoes;
    private final GerenciarSessaoService gerenciar;

    public EncerrarSessoesVencidasService(SessaoRepository sessoes,
                                         TransicoesSessaoService transicoes, GerenciarSessaoService gerenciar) {
        this.sessoes = sessoes;
        this.transicoes = transicoes;
        this.gerenciar = gerenciar;
    }

    @Transactional
    public void encerrar() {
        for (var sessao : sessoes.listarAtivas()) {
            if (sessao.getStatus() == StatusSessao.PAUSED
                    && sessao.getPausaIniciadaEm() != null
                    && sessao.getTempoDePausa() + Duration.between(
                    sessao.getPausaIniciadaEm(), transicoes.horarioServidor()).getSeconds() > 600) {
                sessoes.salvarComEvento(sessao, transicoes.abandonarPorAgendador(sessao, "Pausa excedida"));
            } else if (sessao.getStatus() == StatusSessao.RUNNING
                    && Duration.between(sessao.getIniciadoEm(), transicoes.horarioServidor()).getSeconds()
                    - sessao.getTempoDePausa() >= sessao.getDuracaoPlanejada()) {
                try {
                    var evento = transicoes.concluirPorAgendador(sessao);
                    sessoes.salvarComEvento(sessao, evento);
                    gerenciar.publicarConclusao(sessao);
                } catch (ConclusaoAntecipadaException ignorada) {
                }
            }
        }
    }
}
