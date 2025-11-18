package com.cliente.projeto.crudpb.service;

import com.cliente.projeto.crudpb.exception.RecursoNaoEncontradoException;
import com.cliente.projeto.crudpb.exception.ValidacaoException;
import com.cliente.projeto.crudpb.model.Evento;
import com.cliente.projeto.crudpb.model.Usuario;
import com.cliente.projeto.crudpb.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventoService {
    private final EventoRepository eventoRepository;
    private final UsuarioService usuarioService;

    public EventoService(EventoRepository eventoRepository, UsuarioService usuarioService) {
        this.eventoRepository = eventoRepository;
        this.usuarioService = usuarioService; 
    }

    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }

    public Evento buscarPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento não encontrado com ID: " + id));
    }

    public Evento criarEvento(Evento evento, Long usuarioId) {
        // 1. Validação de regra de negócio (nome duplicado)
        validarNomeDuplicado(evento.getNome(), null);

        // 2. Busca e associa o usuário (Integração)
        Usuario criador = usuarioService.buscarPorId(usuarioId);
        evento.setUsuario(criador);

        // 3. Salva o evento
        return eventoRepository.save(evento);
    }

    public Evento atualizarEvento(Long id, Evento eventoAtualizado, Long usuarioId) {
        // 1. Busca o evento (ou falha com 404)
        Evento eventoExistente = buscarPorId(id);

        // 2. Validação de nome duplicado (ignorando o próprio ID)
        validarNomeDuplicado(eventoAtualizado.getNome(), id);
        
        // 3. Busca e associa o novo usuário (ou o mesmo)
        Usuario criador = usuarioService.buscarPorId(usuarioId);
        eventoExistente.setUsuario(criador);

        // 4. Atualiza os dados
        eventoExistente.setNome(eventoAtualizado.getNome());
        eventoExistente.setDescricao(eventoAtualizado.getDescricao());

        return eventoRepository.save(eventoExistente);
    }

    public void deletarEvento(Long id) {
        Evento eventoParaDeletar = buscarPorId(id);
        eventoRepository.delete(eventoParaDeletar);
    }
    
    private void validarNomeDuplicado(String nome, Long idExcecao) {
        Optional<Evento> conflito = eventoRepository.findByNome(nome);
        if (conflito.isPresent() && (idExcecao == null || !conflito.get().getId().equals(idExcecao))) {
            throw new ValidacaoException("O nome '" + nome + "' já está em uso por outro evento.");
        }
    }
}