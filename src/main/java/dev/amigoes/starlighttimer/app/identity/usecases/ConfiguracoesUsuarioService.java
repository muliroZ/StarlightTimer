package dev.amigoes.starlighttimer.app.identity.usecases;

import dev.amigoes.starlighttimer.app.identity.ports.in.AtualizarConfiguracoesUsuarioUseCase;
import dev.amigoes.starlighttimer.app.identity.ports.in.ObterConfiguracoesUsuarioUseCase;
import dev.amigoes.starlighttimer.app.identity.ports.out.UsuarioAtualPort;
import dev.amigoes.starlighttimer.app.identity.ports.out.ConfiguracoesUsuarioRepositoryPort;
import dev.amigoes.starlighttimer.domain.shared.exceptions.RecursoNaoEncontradoException;
import dev.amigoes.starlighttimer.domain.identity.entities.ConfiguracoesUsuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ConfiguracoesUsuarioService implements ObterConfiguracoesUsuarioUseCase, AtualizarConfiguracoesUsuarioUseCase {

    private final ConfiguracoesUsuarioRepositoryPort configuracoesRepositoryPort;
    private final UsuarioAtualPort usuarioAtualPort;

    public ConfiguracoesUsuarioService(ConfiguracoesUsuarioRepositoryPort configuracoesRepositoryPort, UsuarioAtualPort usuarioAtualPort) {
        this.configuracoesRepositoryPort = configuracoesRepositoryPort;
        this.usuarioAtualPort = usuarioAtualPort;
    }

    @Override
    @Transactional(readOnly = true)
    public ConfiguracoesUsuario obterConfiguracoes() {
        UUID usuarioId = usuarioAtualPort.getUsuarioId();
        return configuracoesRepositoryPort.buscarPorUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Configurações não encontradas para o usuário informado."));
    }

    @Override
    @Transactional
    public ConfiguracoesUsuario atualizarConfiguracoes(Integer focoEmSegundos, Integer pausaEmSegundos, Boolean iniciarPausaAuto, Boolean somHabilitado) {
        ConfiguracoesUsuario configExistente = obterConfiguracoes();

        int novoFoco = focoEmSegundos != null ? focoEmSegundos : configExistente.getFocoEmSegundos();
        int novaPausa = pausaEmSegundos != null ? pausaEmSegundos : configExistente.getPausaEmSegundos();
        boolean novaAutoPausa = iniciarPausaAuto != null ? iniciarPausaAuto : configExistente.isIniciarPausaAuto();
        boolean novoSom = somHabilitado != null ? somHabilitado : configExistente.isSomHabilitado();

        ConfiguracoesUsuario configAtualizada = new ConfiguracoesUsuario(
                configExistente.getUsuarioId(),
                novoFoco,
                novaPausa,
                novaAutoPausa,
                novoSom
        );

        return configuracoesRepositoryPort.salvar(configAtualizada);
    }
}
