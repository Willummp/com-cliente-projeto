package com.cliente.projeto.crudpb.dto;

import com.cliente.projeto.crudpb.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioDTO(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Formato de e-mail inválido.")
        String email
) {
    // Método utilitário para converter o DTO em Entidade
    public Usuario toEntity() {
        return new Usuario(this.nome, this.email);
    }
}