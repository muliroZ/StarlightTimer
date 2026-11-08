package dev.amigoes.starlighttimer.app.progression.dto;

import java.time.LocalDate;

public record ResumoDiaDto(LocalDate diaLocal, int estrelasHoje, int tempoFocoHojeEmSegundos, int orbitAtual) {}
