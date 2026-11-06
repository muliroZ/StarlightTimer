package dev.amigoes.starlighttimer.adapters.session.in.jobs;

import dev.amigoes.starlighttimer.app.session.usecases.EncerrarSessoesVencidasService;
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
