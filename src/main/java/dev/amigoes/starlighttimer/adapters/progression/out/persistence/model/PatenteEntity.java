package dev.amigoes.starlighttimer.adapters.progression.out.persistence.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "patente")
public class PatenteEntity {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false, length = 35)
    private String codigo;

    @Column(unique = true, nullable = false, length = 35)
    private String nome;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nivel_minimo", referencedColumnName = "nivel", nullable = false)
    private LimiteNivelEntity nivelMinimo;

    @Column(name = "imagem_titulo", columnDefinition = "TEXT")
    private String imagemTitulo;
}
