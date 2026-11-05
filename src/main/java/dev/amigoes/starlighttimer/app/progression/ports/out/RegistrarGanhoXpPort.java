package dev.amigoes.starlighttimer.app.progression.ports.out;

import dev.amigoes.starlighttimer.domain.progression.entities.GanhoXp;

public interface RegistrarGanhoXpPort {
    /**
     * Registro de ganho de xp no ledger, com retorno idempotente
     *
     * @param ganho Entidade de domínio
     * @return true, se for registrado, false se já tiver sido concedido
     * */
    boolean registrarGanhoXp(GanhoXp ganho);
}
