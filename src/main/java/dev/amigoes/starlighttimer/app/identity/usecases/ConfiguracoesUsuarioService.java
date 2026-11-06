package dev.amigoes.starlighttimer.app.identity.usecases;

import dev.amigoes.starlighttimer.app.identity.ports.in.AtualizarConfiguracoesUsuarioUseCase;
import dev.amigoes.starlighttimer.app.identity.ports.in.ObterConfiguracoesUsuarioUseCase;
import dev.amigoes.starlighttimer.app.identity.ports.in.UsuarioAtualPort;
import dev.amigoes.starlighttimer.app.identity.ports.out.ConfiguracoesUsuarioRepositoryPort;
import dev.amigoes.starlighttimer.domain.shared.exceptions.DomainValidationException;
import dev.amigoes.starlighttimer.domain.social.entities.ConfiguracoesUsuario;
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
        UUID usuarioId = usuarioAtualPort.obterUsuarioIdAtual();
        return configuracoesRepositoryPort.buscarPorUsuarioId(usuarioId)
                .orElseThrow(() -> new DomainValidationException("Configurações não encontradas para o usuário informado."));
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