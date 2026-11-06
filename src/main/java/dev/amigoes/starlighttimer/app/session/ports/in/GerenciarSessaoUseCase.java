package dev.amigoes.starlighttimer.app.session.ports.in;

import dev.amigoes.starlighttimer.domain.session.entities.Sessao;
import dev.amigoes.starlighttimer.domain.session.enums.TipoSessao;
import dev.amigoes.starlighttimer.domain.session.events.SessaoConcluida;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GerenciarSessaoUseCase {
    Sessao iniciar(TipoSessao tipo, Integer duracaoPlanejada, OffsetDateTime reportadoEm);

    ResultadoTransicao transicionar(UUID id, AcaoSessao acao, OffsetDateTime reportadoEm);

    Optional<Sessao> buscarAtiva();

    List<Sessao> listarHistorico(LocalDate dataInicio, LocalDate dataFim, int limite, int deslocamento);

    record ResultadoTransicao(Sessao sessao, SessaoConcluida.Recompensa recompensa) {}
}
