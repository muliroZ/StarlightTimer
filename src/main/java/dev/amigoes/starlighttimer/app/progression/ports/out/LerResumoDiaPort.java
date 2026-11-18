package dev.amigoes.starlighttimer.app.progression.ports.out;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LerResumoDiaPort {

    EstatisticasHoje obterEstatisticasDoDia (UUID usuarioId, LocalDate dataAtual);
    List<LocalDate> obterDiasComFocoConcluido(UUID usuarioId);
    record EstatisticasHoje (int estrelasHoje, int tempoFocoHojeEmSegundos){}
}
