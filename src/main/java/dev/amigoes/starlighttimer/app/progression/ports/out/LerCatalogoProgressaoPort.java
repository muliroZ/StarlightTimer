package dev.amigoes.starlighttimer.app.progression.ports.out;

import dev.amigoes.starlighttimer.domain.progression.entities.LimiteNivel;
import dev.amigoes.starlighttimer.domain.progression.entities.Patente;

import java.util.List;

public interface LerCatalogoProgressaoPort {
    List<LimiteNivel> buscarLimites();
    List<Patente> buscarPatentes();
}
