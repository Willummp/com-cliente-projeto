package com.cliente.projeto.crudpb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    @Column(nullable = false, length = 100)
    private String nome;

    @Size(max = 255, message = "A descrição não pode exceder 255 caracteres.")
    private String descricao;

    /*
    ==================== INTEGRAÇÃO (Req 2) ====================
    Aqui definimos o relacionamento: Muitos Eventos (Many)
    pertencem a Um Usuário (ToOne).
    
    @NotNull: Garante que um evento não pode ser criado sem um usuário.
    @JoinColumn: Especifica que a coluna no BD se chamará 'usuario_id'.
    ============================================================
    */
    @ManyToOne(fetch = FetchType.LAZY) // LAZY = Boa prática de performance
    @JoinColumn(name = "usuario_id", nullable = false)
    @NotNull(message = "O criador (usuário) é obrigatório.")
    private Usuario usuario;

    // Construtores
    public Evento() {}

    public Evento(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    // Get/Set para o relacionamento de Usuário
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}