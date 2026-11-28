package dev.amigoes.starlighttimer.records.identity;

public record TokensResponse(String accessToken, String refreshToken, String tipoToken, long expiraEmSegundos) {
    @Override
    public String toString() {
        return "TokensResponse[tipoToken=" + tipoToken + ", expiraEmSegundos=" + expiraEmSegundos + "]";
    }
}
