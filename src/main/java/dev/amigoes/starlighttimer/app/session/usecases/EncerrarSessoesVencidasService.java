package dev.amigoes.starlighttimer.app.session.usecases;

import dev.amigoes.starlighttimer.app.session.ports.out.SessaoRepositoryPort;
import dev.amigoes.starlighttimer.domain.session.enums.StatusSessao;
import dev.amigoes.starlighttimer.domain.session.exceptions.ConclusaoAntecipadaException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
public class EncerrarSessoesVencidasService {
    private final SessaoRepositoryPort sessoes;
    private final TransicoesSessaoService transicoes;
    private final GerenciarSessaoService gerenciar;

    public EncerrarSessoesVencidasService(SessaoRepositoryPort sessoes,
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
