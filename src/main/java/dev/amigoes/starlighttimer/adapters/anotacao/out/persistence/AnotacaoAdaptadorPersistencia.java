package dev.amigoes.starlighttimer.adapters.anotacao.out.persistence;

import dev.amigoes.starlighttimer.app.anotacao.ports.out.AnotacaoRepositorioPorta;
import dev.amigoes.starlighttimer.domain.anotacao.Anotacao;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AnotacaoAdaptadorPersistencia implements AnotacaoRepositorioPorta {

    private final AnotacaoRepositorioJpa repositorioJpa;

    public AnotacaoAdaptadorPersistencia(AnotacaoRepositorioJpa repositorioJpa) {
        this.repositorioJpa = repositorioJpa;
    }

    @Override
    public Anotacao salvar(Anotacao anotacao) {
        AnotacaoEntidade entidade = AnotacaoMapeador.paraEntidade(anotacao);
        AnotacaoEntidade salva = repositorioJpa.save(entidade);
        return AnotacaoMapeador.paraDominio(salva);
    }

    @Override
    public Optional<Anotacao> buscarPorId(UUID id) {
        return repositorioJpa.findById(id).map(AnotacaoMapeador::paraDominio);
    }

    @Override
    public List<Anotacao> buscarPorUsuarioESessao(UUID usuarioId, UUID sessaoId) {
        return repositorioJpa.findByUsuarioIdAndSessaoId(usuarioId, sessaoId)
                .stream()
                .map(AnotacaoMapeador::paraDominio)
                .toList();
    }

    @Override
    public List<Anotacao> buscarPorUsuarioEData(UUID usuarioId, LocalDate dataAnotacao) {
        return repositorioJpa.findByUsuarioIdAndDataAnotacao(usuarioId, dataAnotacao)
                .stream()
                .map(AnotacaoMapeador::paraDominio)
                .toList();
    }
}