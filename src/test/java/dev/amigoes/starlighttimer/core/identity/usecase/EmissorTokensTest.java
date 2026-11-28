package dev.amigoes.starlighttimer.core.identity.usecase;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Clock;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class EmissorTokensTest {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"PT0S", "-PT1S"})
    void rejeitaValidadeNulaOuNaoPositiva(String validade) {
        Duration validadeRefresh = validade == null ? null : Duration.parse(validade);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new EmissorTokens(null, null, Clock.systemUTC(), Duration.ofHours(1), validadeRefresh))
                .withMessage("jwt.refresh_validity_time deve ser positivo");
    }
}
