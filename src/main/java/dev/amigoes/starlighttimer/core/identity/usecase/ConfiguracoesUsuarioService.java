package dev.amigoes.starlighttimer.core.identity.usecase;

import dev.amigoes.starlighttimer.core.identity.currentuser.UsuarioAtualPort;
import dev.amigoes.starlighttimer.core.identity.domain.ConfiguracoesUsuario;
import dev.amigoes.starlighttimer.core.identity.persistence.ConfiguracoesUsuarioMapper;
import dev.amigoes.starlighttimer.core.identity.persistence.ConfiguracoesUsuarioRepository;
import dev.amigoes.starlighttimer.exceptions.RecursoNaoEncontradoException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ConfiguracoesUsuarioService {

    private final ConfiguracoesUsuarioRepository configuracoes;
    private final UsuarioAtualPort usuarioAtualPort;
    private final ConfiguracoesUsuarioMapper mapper;

    public ConfiguracoesUsuarioService(ConfiguracoesUsuarioRepository configuracoes, UsuarioAtualPort usuarioAtualPort,
                                       ConfiguracoesUsuarioMapper mapper) {
        this.configuracoes = configuracoes;
        this.usuarioAtualPort = usuarioAtualPort;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public ConfiguracoesUsuario obterConfiguracoes() {
        UUID usuarioId = usuarioAtualPort.getUsuarioId();
        return configuracoes.findById(usuarioId).map(mapper::paraDominio)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Configurações não encontradas para o usuário informado."));
    }

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

        return mapper.paraDominio(configuracoes.save(mapper.paraEntity(configAtualizada)));
    }
}
