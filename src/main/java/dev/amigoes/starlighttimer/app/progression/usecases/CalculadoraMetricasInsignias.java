package dev.amigoes.starlighttimer.app.progression.usecases;

import dev.amigoes.starlighttimer.app.progression.dto.HistoricoProgressao;
import dev.amigoes.starlighttimer.app.progression.dto.HistoricoProgressao.Ciclo;
import dev.amigoes.starlighttimer.app.progression.dto.HistoricoProgressao.Nota;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** Métricas de D-9; os alvos e recompensas continuam pertencendo ao catálogo. */
final class CalculadoraMetricasInsignias {
    Map<String, Integer> calcular(HistoricoProgressao historico) {
        var focos = historico.ciclos().stream().filter(Ciclo::foco).toList();
        var descansos = historico.ciclos().stream().filter(Ciclo::descanso).toList();
        var dias = focos.stream().map(Ciclo::diaLocal).collect(Collectors.toCollection(HashSet::new));
        var diasOrdenados = dias.stream().sorted().toList();
        int maiorOrbit = maiorSequencia(diasOrdenados);
        int descansosVinculados = (int) descansos.stream()
                .filter(descanso -> focos.stream().anyMatch(foco -> vinculado(foco, descanso)))
                .count();

        Map<String, Integer> metricas = new HashMap<>();
        for (String codigo : List.of("FIRST_LIGHT", "IGNITION_SEQUENCE", "SUPERNOVA",
                "STELLAR_FORGE", "A_THOUSAND_SUNS")) {
            metricas.put("BADGE_" + codigo, focos.size());
        }
        metricas.put("BADGE_SILENT_CORE", (int) focos.stream()
                .filter(foco -> foco.segundosPausa() == 0).count());
        metricas.put("BADGE_NIGHT_WATCH", contarJanela(focos, historico, 0, 4));
        metricas.put("BADGE_DAWNBRINGER", contarJanela(focos, historico, 5, 8));
        metricas.put("BADGE_MERIDIAN", contarJanela(focos, historico, 11, 14));
        int minutos = Math.toIntExact(focos.stream().mapToLong(Ciclo::segundosFoco).sum() / 60);
        metricas.put("BADGE_DEEP_FIELD", minutos);
        metricas.put("BADGE_EVENT_HORIZON", minutos);
        metricas.put("BADGE_FIRST_ORBIT", maiorOrbit);
        metricas.put("BADGE_STEADY_ORBIT", maiorOrbit);
        metricas.put("BADGE_UNBROKEN_ORBIT", maiorOrbit);
        metricas.put("BADGE_STELLAR_LEGACY", dias.size());
        metricas.put("BADGE_COOLING_NEBULA", descansosVinculados);

        Set<UUID> idsConcluidos = historico.ciclos().stream()
                .map(Ciclo::id).collect(Collectors.toSet());
        metricas.put("BADGE_CELESTIAL_SCRIBE", (int) historico.notas().stream()
                .map(Nota::sessaoId).filter(idsConcluidos::contains).count());
        metricas.put("BADGE_ASTRAL_CHRONICLE", (int) historico.notas().stream()
                .map(n -> n.criadoEm().atZone(historico.fuso()).minusHours(4).toLocalDate())
                .distinct().count());
        metricas.put("BADGE_CELESTIAL_CLOCKWORK", maiorJanelaDeTrintaDias(diasOrdenados));
        metricas.put("BADGE_STABLE_FUSION", diasFusaoEstavel(focos, descansos));
        metricas.put("BADGE_REIGNITION", teveRetorno(diasOrdenados) ? 1 : 0);
        metricas.put("BADGE_PHOENIX_FLAME", orbitAposPerda(diasOrdenados));
        return metricas;
    }

    private int contarJanela(List<Ciclo> focos, HistoricoProgressao historico, int inicio, int fim) {
        return (int) focos.stream().filter(foco -> {
            LocalTime hora = foco.iniciadoEm().atZone(historico.fuso()).toLocalTime();
            return hora.compareTo(LocalTime.of(inicio, 0)) >= 0
                    && hora.compareTo(LocalTime.of(fim, 0)) < 0;
        }).count();
    }

    private boolean vinculado(Ciclo foco, Ciclo descanso) {
        long segundos = Duration.between(foco.terminadoEm(), descanso.iniciadoEm()).getSeconds();
        return segundos >= 0 && segundos <= 120;
    }

    private int maiorSequencia(List<LocalDate> dias) {
        int atual = 0, maior = 0;
        LocalDate anterior = null;
        for (LocalDate dia : dias) {
            atual = anterior != null && anterior.plusDays(1).equals(dia) ? atual + 1 : 1;
            maior = Math.max(maior, atual);
            anterior = dia;
        }
        return maior;
    }

    private int maiorJanelaDeTrintaDias(List<LocalDate> dias) {
        int inicio = 0, maior = 0;
        for (int fim = 0; fim < dias.size(); fim++) {
            while (dias.get(inicio).isBefore(dias.get(fim).minusDays(29))) inicio++;
            maior = Math.max(maior, fim - inicio + 1);
        }
        return maior;
    }

    private int diasFusaoEstavel(List<Ciclo> focos, List<Ciclo> descansos) {
        Map<LocalDate, List<Ciclo>> porDia = focos.stream()
                .collect(Collectors.groupingBy(Ciclo::diaLocal));
        return (int) porDia.values().stream().filter(grupo -> grupo.size() >= 4
                && grupo.stream().allMatch(foco -> descansos.stream()
                .anyMatch(descanso -> vinculado(foco, descanso)))).count();
    }

    private boolean teveRetorno(List<LocalDate> dias) {
        for (int i = 1; i < dias.size(); i++) {
            if (!dias.get(i - 1).plusDays(15).isAfter(dias.get(i))) return true;
        }
        return false;
    }

    private int orbitAposPerda(List<LocalDate> dias) {
        int atual = 0;
        boolean orbitPerdida = false;
        LocalDate anterior = null;
        for (LocalDate dia : dias) {
            if (anterior != null && !anterior.plusDays(1).equals(dia)) {
                if (atual >= 7) orbitPerdida = true;
                atual = 0;
            }
            atual++;
            if (orbitPerdida && atual >= 7) return atual;
            anterior = dia;
        }
        return 0;
    }
}
