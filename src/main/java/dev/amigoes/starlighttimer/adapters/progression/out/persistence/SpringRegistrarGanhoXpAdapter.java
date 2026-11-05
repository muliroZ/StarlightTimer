package dev.amigoes.starlighttimer.adapters.progression.out.persistence;

import dev.amigoes.starlighttimer.adapters.progression.out.persistence.repository.GanhoXpJpaRepository;
import dev.amigoes.starlighttimer.app.progression.ports.out.RegistrarGanhoXpPort;
import dev.amigoes.starlighttimer.domain.progression.entities.GanhoXp;
import org.springframework.stereotype.Component;

@Component
public class SpringRegistrarGanhoXpAdapter implements RegistrarGanhoXpPort {

    private final GanhoXpJpaRepository ganhoXpJpaRepository;

    public SpringRegistrarGanhoXpAdapter(GanhoXpJpaRepository ganhoXpJpaRepository) {
        this.ganhoXpJpaRepository = ganhoXpJpaRepository;
    }

    @Override
    public boolean registrarGanhoXp(GanhoXp ganho) {
        int linhasInseridas = 0;

        if (ganho.origemSessao() != null) {
            linhasInseridas = ganhoXpJpaRepository.inserirPorSessao(
                    ganho.id(),
                    ganho.usuarioId(),
                    ganho.quantidade(),
                    ganho.origemSessao(),
                    ganho.atribuidoEm()
            );
        } else if (ganho.origemConstelacao() != null) {
            linhasInseridas = ganhoXpJpaRepository.inserirPorConstelacao(
                    ganho.id(),
                    ganho.usuarioId(),
                    ganho.quantidade(),
                    ganho.origemConstelacao(),
                    ganho.atribuidoEm()
            );
        } else if (ganho.origemInsignia() != null) {
            linhasInseridas = ganhoXpJpaRepository.inserirPorInsignia(
                    ganho.id(),
                    ganho.usuarioId(),
                    ganho.quantidade(),
                    ganho.origemInsignia(),
                    ganho.atribuidoEm()
            );
        }

        return linhasInseridas == 1;
    }
}
