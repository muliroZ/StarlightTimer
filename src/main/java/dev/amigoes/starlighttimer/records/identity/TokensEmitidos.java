package dev.amigoes.starlighttimer.records.identity;

public record TokensEmitidos(String accessToken, String refreshToken, long expiraEmSegundos) {
    @Override
    public String toString() {
        return "TokensEmitidos[expiraEmSegundos=" + expiraEmSegundos + "]";
    }
}
