package com.cliente.projeto.crudpb.service;

import com.cliente.projeto.crudpb.exception.RecursoNaoEncontradoException;
import com.cliente.projeto.crudpb.exception.ValidacaoException;
import com.cliente.projeto.crudpb.model.Usuario;
import com.cliente.projeto.crudpb.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // Injeção de Dependência via Construtor (Clean Code)
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id));
    }

    public Usuario criarUsuario(Usuario usuario) {
        // Regra de negócio (não pode ter e-mail duplicado)
        validarEmailDuplicado(usuario.getEmail(), null);
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) {
        // Busca o usuário (ou falha com 404)
        Usuario usuarioExistente = buscarPorId(id);

        // Validação de e-mail duplicado (ignorando o próprio ID)
        validarEmailDuplicado(usuarioAtualizado.getEmail(), id);

        // Atualiza os dados
        usuarioExistente.setNome(usuarioAtualizado.getNome());
        usuarioExistente.setEmail(usuarioAtualizado.getEmail());

        return usuarioRepository.save(usuarioExistente);
    }

    public void deletarUsuario(Long id) {
        // Verifica se existe antes de deletar (ou falha com 404)
        Usuario usuarioParaDeletar = buscarPorId(id);
        usuarioRepository.delete(usuarioParaDeletar);
    }

    // Método de validação privado (Clean Code)
    private void validarEmailDuplicado(String email, Long idExcecao) {
        Optional<Usuario> conflito = usuarioRepository.findByEmail(email);

        // Se encontrou um usuário com o mesmo e-mail E
        // (se o ID desse usuário for diferente do ID que estamos atualizando)
        if (conflito.isPresent() && (idExcecao == null || !conflito.get().getId().equals(idExcecao))) {
            throw new ValidacaoException("O e-mail '" + email + "' já está em uso por outro usuário.");
        }
    }
}