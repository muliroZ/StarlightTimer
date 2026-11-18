package dev.amigoes.starlighttimer.app.progression.usecases;

import dev.amigoes.starlighttimer.app.identity.ports.out.PerfilRepositoryPort;
import dev.amigoes.starlighttimer.app.identity.ports.out.UsuarioAtualPort;
import dev.amigoes.starlighttimer.app.progression.dto.ResumoDiaDto;
import dev.amigoes.starlighttimer.app.progression.ports.in.ObterResumoDiaUseCase;
import dev.amigoes.starlighttimer.app.progression.ports.out.LerResumoDiaPort;
import dev.amigoes.starlighttimer.domain.identity.entities.Perfil;
import dev.amigoes.starlighttimer.domain.shared.exceptions.DomainValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ObterResumoDiaService implements ObterResumoDiaUseCase {

    private final UsuarioAtualPort usuarioAtualPort;
    private final PerfilRepositoryPort perfilRepositoryPort;
    private final LerResumoDiaPort lerResumoDiaPort;
    private final Clock clock;

    public ObterResumoDiaService(UsuarioAtualPort usuarioAtualPort, PerfilRepositoryPort perfilRepositoryPort,
                                 LerResumoDiaPort lerResumoDiaPort, Clock clock) {
        this.usuarioAtualPort = usuarioAtualPort;
        this.perfilRepositoryPort = perfilRepositoryPort;
        this.lerResumoDiaPort = lerResumoDiaPort;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public ResumoDiaDto obterResumoDia() {
        UUID usuarioId = usuarioAtualPort.getUsuarioId();

        Perfil perfil = perfilRepositoryPort.buscarPorUsuarioId(usuarioId)
                .orElseThrow(() -> new DomainValidationException("Perfil não encontrado para o usuário."));

        ZoneId fusoUsuario = ZoneId.of(perfil.getFusoHorario());
        LocalDate diaLogicoAtual = ZonedDateTime.now(clock)
                .withZoneSameInstant(fusoUsuario)
                .minusHours(4)
                .toLocalDate();

        LerResumoDiaPort.EstatisticasHoje estatisticas = lerResumoDiaPort.obterEstatisticasDoDia(usuarioId, diaLogicoAtual);
        List<LocalDate> diasAtivos = lerResumoDiaPort.obterDiasComFocoConcluido(usuarioId);

        int orbitaAtual = calcularOrbita(diaLogicoAtual, diasAtivos);

        return new ResumoDiaDto(
                diaLogicoAtual,
                estatisticas.estrelasHoje(),
                estatisticas.tempoFocoHojeEmSegundos(),
                orbitaAtual
        );
    }

    private int calcularOrbita(LocalDate diaLogicoAtual, List<LocalDate> diasComFoco) {
        if (diasComFoco == null || diasComFoco.isEmpty()) {
            return 0;
        }

        Set<LocalDate> conjuntoDias = new HashSet<>(diasComFoco);
        LocalDate cursor = diaLogicoAtual;

        if (!conjuntoDias.contains(cursor)) {
            cursor = cursor.minusDays(1);
        }

        int streak = 0;
        while (conjuntoDias.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }

        return streak;
    }
}