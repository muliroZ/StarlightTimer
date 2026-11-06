package dev.amigoes.starlighttimer.domain.social.entities;

import dev.amigoes.starlighttimer.domain.shared.exceptions.RegraDeNegocioException;

import java.util.UUID;

public class ConfiguracoesUsuario {
    private final UUID usuarioId;
    private int focoEmSegundos;
    private int pausaEmSegundos;
    private boolean iniciarPausaAuto;
    private boolean somHabilitado;

    public ConfiguracoesUsuario(UUID usuarioId, int focoEmSegundos, int pausaEmSegundos, boolean iniciarPausaAuto, boolean somHabilitado) {
        this.usuarioId = usuarioId;
        setFocoEmSegundos(focoEmSegundos);
        setPausaEmSegundos(pausaEmSegundos);
        this.iniciarPausaAuto = iniciarPausaAuto;
        this.somHabilitado = somHabilitado;
    }

    public void setFocoEmSegundos(int focoEmSegundos) {
        int tempoMinimo = 900;
        int tempoMaximo = 5400;
        int tempoMultiplo = 300;
        if (focoEmSegundos < tempoMinimo || focoEmSegundos > tempoMaximo || (focoEmSegundos % tempoMultiplo != 0)) {
            throw new RegraDeNegocioException("Tempo invalido detectado");
        }
        this.focoEmSegundos = focoEmSegundos;
    }

    public void setPausaEmSegundos(int pausaEmSegundos) {
        int tempoMinimo = 180;
        int tempoMaximo = 900;
        if (pausaEmSegundos < tempoMinimo || pausaEmSegundos > tempoMaximo) {
            throw new RegraDeNegocioException("Tempo invalido detectado");
        }
        this.pausaEmSegundos = pausaEmSegundos;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public int getFocoEmSegundos() {
        return focoEmSegundos;
    }

    public int getPausaEmSegundos() {
        return pausaEmSegundos;
    }

    public boolean isIniciarPausaAuto() {
        return iniciarPausaAuto;
    }

    public boolean isSomHabilitado() {
        return somHabilitado;
    }
}