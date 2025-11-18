package com.cliente.projeto.crudpb.service;

import com.cliente.projeto.crudpb.exception.RecursoNaoEncontradoException;
import com.cliente.projeto.crudpb.exception.ValidacaoException;
import com.cliente.projeto.crudpb.model.Evento;
import com.cliente.projeto.crudpb.model.Usuario;
import com.cliente.projeto.crudpb.repository.EventoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Habilita o Mockito
class EventoServiceTest {

    @Mock 
    private EventoRepository eventoRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private EventoService eventoService;

    @Test
    void deveLancarExcecao_QuandoCriarEventoComNomeDuplicado() {
        // 1. Cenário (Given)
        Evento eventoNovo = new Evento("Evento Repetido", "Descricao");
        Long usuarioId = 1L;

        // Simula a validação de nome duplicado
        when(eventoRepository.findByNome("Evento Repetido")).thenReturn(Optional.of(new Evento()));

        // 2. Ação e Verificação (When & Then)
        // Verificamos se a exceção de validação é lançada
        assertThrows(ValidacaoException.class, () -> {
            eventoService.criarEvento(eventoNovo, usuarioId); // Assinatura atualizada
        });

        // 3. Verificação Extra
        // Garantir que NUNCA tentamos buscar o usuário ou salvar
        verify(usuarioService, never()).buscarPorId(anyLong());
        verify(eventoRepository, never()).save(any());
    }

    @Test
    void deveCriarEventoComSucesso_EAssociarUsuarioCorretamente() {
        Evento eventoNovo = new Evento("Evento de Lançamento", "Descricao");
        Long usuarioId = 5L;

        Usuario usuarioCriador = new Usuario("Admin", "admin@email.com");
        usuarioCriador.setId(usuarioId);

        // Simulando que o nome NÃO está duplicado
        when(eventoRepository.findByNome("Evento de Lançamento")).thenReturn(Optional.empty());

        // Simulando a busca do usuário (Integração)
        when(usuarioService.buscarPorId(usuarioId)).thenReturn(usuarioCriador);
        
        // Simulando o 'save' (apenas retornando o evento que foi passado)
        when(eventoRepository.save(any(Evento.class))).thenReturn(eventoNovo);

        Evento eventoSalvo = eventoService.criarEvento(eventoNovo, usuarioId);

        // 3. Verificação (Then)
        // O evento foi salvo
        verify(eventoRepository, times(1)).save(eventoNovo);}}
