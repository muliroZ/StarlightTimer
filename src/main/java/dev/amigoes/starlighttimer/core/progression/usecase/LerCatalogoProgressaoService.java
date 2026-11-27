package dev.amigoes.starlighttimer.core.progression.usecase;

import dev.amigoes.starlighttimer.core.progression.persistence.CatalogoProgressaoRepository;
import dev.amigoes.starlighttimer.records.progression.Constelacao;
import dev.amigoes.starlighttimer.records.progression.Insignia;
import dev.amigoes.starlighttimer.records.progression.LimiteNivel;
import dev.amigoes.starlighttimer.records.progression.Patente;

import java.util.List;

public class LerCatalogoProgressaoService {

    private final CatalogoProgressaoRepository lerCatalogoProgressao;

    public LerCatalogoProgressaoService(CatalogoProgressaoRepository lerCatalogoProgressao) {
        this.lerCatalogoProgressao = lerCatalogoProgressao;
    }

    public List<LimiteNivel> buscarLimites() {
        return lerCatalogoProgressao.buscarLimites();
    }

    public List<Patente> buscarPatentes() {
        return lerCatalogoProgressao.buscarPatentes();
    }

    public List<Insignia> buscarInsignias() {
        return lerCatalogoProgressao.buscarInsignias();
    }

    public List<Constelacao> buscarConstelacoes() {
        return lerCatalogoProgressao.buscarConstelacoes();
    }
}
