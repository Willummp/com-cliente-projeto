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

        eventoService.criarEvento(eventoNovo, usuarioId);

        // 3. Verificação (Then)
        // O evento foi salvo
        verify(eventoRepository, times(1)).save(eventoNovo);
    }

    @Test
    void deveListarTodosOsEventos() {
        eventoService.listarTodos();
        verify(eventoRepository).findAll();
    }

    @Test
    void deveBuscarPorId_ComSucesso() {
        Evento evento = new Evento("Evento", "Desc");
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));

        Evento resultado = eventoService.buscarPorId(1L);
        assertEquals(evento, resultado);
    }

    @Test
    void deveLancarExcecao_QuandoBuscarPorIdNaoExistente() {
        when(eventoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> eventoService.buscarPorId(1L));
    }

    @Test
    void deveAtualizarEvento_ComSucesso() {
        Long id = 1L;
        Long usuarioId = 2L;
        Evento eventoAtualizado = new Evento("Novo Nome", "Nova Desc");
        Evento eventoExistente = new Evento("Antigo Nome", "Antiga Desc");
        eventoExistente.setId(id);
        Usuario usuario = new Usuario("User", "email");

        when(eventoRepository.findByNome("Novo Nome")).thenReturn(Optional.empty());
        when(eventoRepository.findById(id)).thenReturn(Optional.of(eventoExistente));
        when(usuarioService.buscarPorId(usuarioId)).thenReturn(usuario);
        when(eventoRepository.save(any(Evento.class))).thenAnswer(i -> i.getArgument(0));

        Evento resultado = eventoService.atualizarEvento(id, eventoAtualizado, usuarioId);

        assertEquals("Novo Nome", resultado.getNome());
        assertEquals("Nova Desc", resultado.getDescricao());
        assertEquals(usuario, resultado.getUsuario());
    }

    @Test
    void deveLancarExcecao_QuandoAtualizarComNomeDuplicadoDeOutroEvento() {
        Long id = 1L;
        Evento eventoAtualizado = new Evento("Nome Duplicado", "Desc");
        Evento outroEvento = new Evento("Nome Duplicado", "Desc");
        outroEvento.setId(2L); // ID diferente

        when(eventoRepository.findByNome("Nome Duplicado")).thenReturn(Optional.of(outroEvento));

        assertThrows(ValidacaoException.class, () -> eventoService.atualizarEvento(id, eventoAtualizado, 1L));
    }

    @Test
    void devePermitirAtualizar_ComMesmoNomeDoProprioEvento() {
        Long id = 1L;
        Long usuarioId = 2L;
        Evento eventoAtualizado = new Evento("Mesmo Nome", "Desc");
        Evento eventoExistente = new Evento("Mesmo Nome", "Desc");
        eventoExistente.setId(id);
        Usuario usuario = new Usuario("User", "email");

        when(eventoRepository.findByNome("Mesmo Nome")).thenReturn(Optional.of(eventoExistente));
        when(eventoRepository.findById(id)).thenReturn(Optional.of(eventoExistente));
        when(usuarioService.buscarPorId(usuarioId)).thenReturn(usuario);
        when(eventoRepository.save(any(Evento.class))).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() -> eventoService.atualizarEvento(id, eventoAtualizado, usuarioId));
    }

    @Test
    void deveDeletarEvento_ComSucesso() {
        when(eventoRepository.existsById(1L)).thenReturn(true);

        eventoService.deletarEvento(1L);

        verify(eventoRepository).deleteById(1L);
    }

    @Test
    void deveLancarExcecao_QuandoDeletarEventoInexistente() {
        when(eventoRepository.existsById(1L)).thenReturn(false);

        assertThrows(RecursoNaoEncontradoException.class, () -> eventoService.deletarEvento(1L));
    }
}
