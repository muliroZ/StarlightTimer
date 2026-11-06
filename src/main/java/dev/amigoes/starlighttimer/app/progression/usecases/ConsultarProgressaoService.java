package dev.amigoes.starlighttimer.app.progression.usecases;

import dev.amigoes.starlighttimer.app.progression.dto.DadosPerfilProgressao;
import dev.amigoes.starlighttimer.app.progression.dto.HistoricoProgressao.Ciclo;
import dev.amigoes.starlighttimer.app.progression.dto.ResumoProgressao;
import dev.amigoes.starlighttimer.app.progression.ports.in.ConsultarProgressaoUseCase;
import dev.amigoes.starlighttimer.app.progression.ports.out.LerCatalogoProgressaoPort;
import dev.amigoes.starlighttimer.app.progression.ports.out.LerDadosPerfilProgressaoPort;
import dev.amigoes.starlighttimer.app.progression.ports.out.LerHistoricoProgressaoPort;
import dev.amigoes.starlighttimer.domain.progression.services.CurvaDeNiveis;
import dev.amigoes.starlighttimer.domain.shared.exceptions.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ConsultarProgressaoService implements ConsultarProgressaoUseCase {
    private final LerDadosPerfilProgressaoPort perfil;
    private final LerHistoricoProgressaoPort historico;
    private final LerCatalogoProgressaoPort catalogo;
    private final Clock relogio;
    private final CalculadoraMetricasInsignias metricas = new CalculadoraMetricasInsignias();

    public ConsultarProgressaoService(LerDadosPerfilProgressaoPort perfil,
            LerHistoricoProgressaoPort historico, LerCatalogoProgressaoPort catalogo, Clock relogio) {
        this.perfil = perfil;
        this.historico = historico;
        this.catalogo = catalogo;
        this.relogio = relogio;
    }

    @Override
    @Transactional(readOnly = true)
    public ResumoProgressao consultar(UUID usuarioId) {
        DadosPerfilProgressao dados = perfil.carregar(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil nao encontrado"));
        var historicoUsuario = historico.carregar(usuarioId);
        var focos = historicoUsuario.ciclos().stream().filter(ciclo -> ciclo.foco()).toList();
        long estrelas = focos.size();
        long segundos = focos.stream().mapToLong(ciclo -> ciclo.segundosFoco()).sum();
        var limites = catalogo.buscarLimites();
        var resultado = new CurvaDeNiveis(limites, catalogo.buscarPatentes()).calcular(dados.xpTotal());
        int inicioNivel = limites.get(resultado.nivel() - 1).xpNecessario();
        Integer proximoNivel = resultado.xpDoProximoNivel().isPresent()
                ? resultado.xpDoProximoNivel().getAsInt() : null;
        var progressoPersistido = dados.insignias().stream().collect(Collectors.toMap(
                DadosPerfilProgressao.InsigniaUsuario::id, Function.identity()));
        Map<String, Integer> valores = metricas.calcular(historicoUsuario);
        var insignias = catalogo.buscarInsignias().stream().map(insignia -> {
            var salvo = progressoPersistido.get(insignia.id());
            int progresso = Math.max(valores.getOrDefault(insignia.codigo(), 0),
                    salvo == null ? 0 : salvo.contagem());
            var desbloqueadoEm = salvo == null ? null : salvo.desbloqueadoEm();
            return new ResumoProgressao.Insignia(insignia.codigo(), insignia.nome(),
                    insignia.descricao(), insignia.icone(), progresso, insignia.contagemAlvo(),
                    desbloqueadoEm != null, desbloqueadoEm);
        }).toList();
        var desbloqueios = dados.constelacoes().stream().collect(Collectors.toMap(
                DadosPerfilProgressao.ConstelacaoUsuario::id, Function.identity()));
        var constelacoes = catalogo.buscarConstelacoes().stream().map(constelacao -> {
            int conquistadas = (int) constelacao.insignias().stream()
                    .filter(id -> progressoPersistido.containsKey(id)
                            && progressoPersistido.get(id).desbloqueadoEm() != null).count();
            var salvo = desbloqueios.get(constelacao.id());
            var desbloqueadoEm = salvo == null ? null : salvo.desbloqueadoEm();
            return new ResumoProgressao.Constelacao(constelacao.codigo(), constelacao.nome(),
                    constelacao.descricao(), constelacao.assetSvg(), estrelas,
                    constelacao.qtdEstrelas(), conquistadas, constelacao.qtdInsigniasRequeridas(),
                    desbloqueadoEm != null, desbloqueadoEm);
        }).toList();
        return new ResumoProgressao(resultado.nivel(), new ResumoProgressao.Patente(
                resultado.patente().codigo(), resultado.patente().nome(),
                resultado.patente().imagemTitulo()), dados.xpTotal(), dados.xpTotal() - inicioNivel,
                proximoNivel, proximoNivel == null ? null : proximoNivel - dados.xpTotal(),
                estrelas, segundos, BigDecimal.valueOf(segundos)
                .divide(BigDecimal.valueOf(3600), 2, RoundingMode.HALF_UP),
                orbitAtual(focos, dados, relogio), insignias, constelacoes);
    }

    private int orbitAtual(List<Ciclo> focos,
                           DadosPerfilProgressao dados, Clock relogio) {
        var dias = focos.stream().map(ciclo -> ciclo.diaLocal()).distinct()
                .sorted(Comparator.reverseOrder()).toList();
        if (dias.isEmpty()) return 0;
        LocalDate hoje = relogio.instant().atZone(dados.fuso()).toLocalDateTime()
                .minusHours(4).toLocalDate();
        LocalDate ultimo = dias.getFirst();
        if (!ultimo.equals(hoje) && !ultimo.equals(hoje.minusDays(1))) return 0;
        int sequencia = 0;
        LocalDate esperado = ultimo;
        for (LocalDate dia : dias) {
            if (!dia.equals(esperado)) break;
            sequencia++;
            esperado = esperado.minusDays(1);
        }
        return sequencia;
    }
}
