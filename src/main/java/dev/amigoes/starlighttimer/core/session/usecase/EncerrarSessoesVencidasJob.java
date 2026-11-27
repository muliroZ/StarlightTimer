package dev.amigoes.starlighttimer.core.session.usecase;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EncerrarSessoesVencidasJob {
    private final EncerrarSessoesVencidasService servico;

    public EncerrarSessoesVencidasJob(EncerrarSessoesVencidasService servico) {
        this.servico = servico;
    }

    @Scheduled(fixedDelayString = "${starlight.sessoes.intervalo-job-ms:30000}")
    public void executar() {
        servico.encerrar();
    }
}
