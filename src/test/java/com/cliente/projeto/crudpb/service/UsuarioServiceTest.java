package com.cliente.projeto.crudpb.service;

import com.cliente.projeto.crudpb.exception.ValidacaoException;
import com.cliente.projeto.crudpb.model.Usuario;
import com.cliente.projeto.crudpb.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks 
    private UsuarioService usuarioService;

    @Test
    void deveLancarExcecao_QuandoCriarUsuarioComEmailDuplicado() {
        // 1. Cenário (Given)
        // Simulando que o repositório encontrou um usuário com este e-mail
        when(usuarioRepository.findByEmail("email.repetido@teste.com"))
                .thenReturn(Optional.of(new Usuario()));

        Usuario usuarioNovo = new Usuario("Usuario Novo", "email.repetido@teste.com");

        // 2. Ação e Verificação (When & Then)
        // Verificamos se a exceção de validação de negócio é lançada
        assertThrows(ValidacaoException.class, () -> {
            usuarioService.criarUsuario(usuarioNovo);
        });

        // 3. Verificação Extra
        // Garantir que o "fail early" funcionou e NUNCA tentamos salvar
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecao_QuandoAtualizarUsuarioComEmailDuplicadoDeOutro() {
        // 1. Cenário (Given)
        // O usuário "conflitante" (que já existe no banco)
        Usuario usuarioConflitante = new Usuario("Usuario Antigo", "email.repetido@teste.com");
        usuarioConflitante.setId(99L); // ID diferente

        // O usuário que estamos tentando atualizar
        Usuario usuarioAtualizado = new Usuario("Usuario Alvo", "email.repetido@teste.com");

        // Simulando que o repositório encontrou o e-mail conflitante
        when(usuarioRepository.findByEmail("email.repetido@teste.com"))
                .thenReturn(Optional.of(usuarioConflitante));

        // Simulando a busca do usuário que queremos atualizar (necessário no método 'atualizar')
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(new Usuario("Dummy", "dummy@email.com")));


        // 2. Ação e Verificação (When & Then)
        // Verificamos se a exceção é lançada ao tentar atualizar o usuário de ID 1
        assertThrows(ValidacaoException.class, () -> {
            usuarioService.atualizarUsuario(1L, usuarioAtualizado);
        });

        // 3. Verificação Extra
        // Garantir que NUNCA tentamos salvar
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void devePermitirAtualizar_QuandoEmailDuplicadoEhDoProprioUsuario() {
        // 1. Cenário (Given)
        // O usuário que estamos atualizando (ID 1)
        Usuario usuarioExistente = new Usuario("Usuario Alvo", "email.original@teste.com");
        usuarioExistente.setId(1L);

        // O "novo" e-mail é o mesmo que ele já possui
        Usuario usuarioAtualizado = new Usuario("Novo Nome", "email.original@teste.com");

        // Simulando a busca pelo ID 1
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));

        // Simulando a busca pelo e-mail (encontra o próprio usuário)
        when(usuarioRepository.findByEmail("email.original@teste.com"))
                .thenReturn(Optional.of(usuarioExistente));

        // 2. Ação (When)
        // Esta ação NÃO deve lançar uma ValidacaoException
        usuarioService.atualizarUsuario(1L, usuarioAtualizado);

        // 3. Verificação (Then)
        // Garantir que o método 'save' FOI chamado, pois a validação passou
        verify(usuarioRepository, times(1)).save(usuarioExistente);
    }
}