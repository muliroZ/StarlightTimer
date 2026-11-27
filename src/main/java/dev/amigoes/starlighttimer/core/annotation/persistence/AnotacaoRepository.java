package dev.amigoes.starlighttimer.core.annotation.persistence;

import dev.amigoes.starlighttimer.core.annotation.domain.Anotacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnotacaoRepository extends JpaRepository<AnotacaoEntity, UUID> {
    List<AnotacaoEntity> findByUsuarioIdAndSessaoId(UUID usuarioId, UUID sessaoId);
    List<AnotacaoEntity> findByUsuarioIdAndDataAnotacao(UUID usuarioId, LocalDate dataAnotacao);

    default Anotacao salvar(Anotacao anotacao) {
        AnotacaoEntity entidade = AnotacaoMapper.paraEntidade(anotacao);
        AnotacaoEntity salva = save(entidade);
        return AnotacaoMapper.paraDominio(salva);
    }

    default Optional<Anotacao> buscarPorId(UUID id) {
        return findById(id).map(AnotacaoMapper::paraDominio);
    }

    default List<Anotacao> buscarPorUsuarioESessao(UUID usuarioId, UUID sessaoId) {
        return findByUsuarioIdAndSessaoId(usuarioId, sessaoId)
                .stream()
                .map(AnotacaoMapper::paraDominio)
                .toList();
    }

    default List<Anotacao> buscarPorUsuarioEData(UUID usuarioId, LocalDate dataAnotacao) {
        return findByUsuarioIdAndDataAnotacao(usuarioId, dataAnotacao)
                .stream()
                .map(AnotacaoMapper::paraDominio)
                .toList();
    }

    default void remover(UUID id) {
        deleteById(id);
    }
}
