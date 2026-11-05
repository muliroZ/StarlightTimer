package dev.amigoes.starlighttimer.adapters.annotation.out.persistence;

import dev.amigoes.starlighttimer.app.annotation.ports.out.AnotacaoRepositoryPort;
import dev.amigoes.starlighttimer.domain.annotation.Anotacao;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AnotacaoPersistenceAdapter implements AnotacaoRepositoryPort {

    private final AnotacaoJpaRepository repositorioJpa;

    public AnotacaoPersistenceAdapter(AnotacaoJpaRepository repositorioJpa) {
        this.repositorioJpa = repositorioJpa;
    }

    @Override
    public Anotacao salvar(Anotacao anotacao) {
        AnotacaoEntity entidade = AnotacaoMapper.paraEntidade(anotacao);
        AnotacaoEntity salva = repositorioJpa.save(entidade);
        return AnotacaoMapper.paraDominio(salva);
    }

    @Override
    public Optional<Anotacao> buscarPorId(UUID id) {
        return repositorioJpa.findById(id).map(AnotacaoMapper::paraDominio);
    }

    @Override
    public List<Anotacao> buscarPorUsuarioESessao(UUID usuarioId, UUID sessaoId) {
        return repositorioJpa.findByUsuarioIdAndSessaoId(usuarioId, sessaoId)
                .stream()
                .map(AnotacaoMapper::paraDominio)
                .toList();
    }

    @Override
    public List<Anotacao> buscarPorUsuarioEData(UUID usuarioId, LocalDate dataAnotacao) {
        return repositorioJpa.findByUsuarioIdAndDataAnotacao(usuarioId, dataAnotacao)
                .stream()
                .map(AnotacaoMapper::paraDominio)
                .toList();
    }
}