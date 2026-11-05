package dev.amigoes.starlighttimer.app.progression.usecases;

import dev.amigoes.starlighttimer.app.progression.ports.out.LerCatalogoProgressaoPort;
import dev.amigoes.starlighttimer.domain.progression.entities.LimiteNivel;
import dev.amigoes.starlighttimer.domain.progression.entities.Patente;

import java.util.List;

public class LerCatalogoProgressaoService {

    private final LerCatalogoProgressaoPort lerCatalogoProgressao;

    public LerCatalogoProgressaoService(LerCatalogoProgressaoPort lerCatalogoProgressao) {
        this.lerCatalogoProgressao = lerCatalogoProgressao;
    }

    public List<LimiteNivel> buscarLimites() {
        return lerCatalogoProgressao.buscarLimites();
    }

    public List<Patente> buscarPatentes() {
        return lerCatalogoProgressao.buscarPatentes();
    }
}
