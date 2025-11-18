package com.cliente.projeto.crudpb.service;

import com.cliente.projeto.crudpb.exception.RecursoNaoEncontradoException;
import com.cliente.projeto.crudpb.exception.ValidacaoException;
import com.cliente.projeto.crudpb.model.Evento;
import com.cliente.projeto.crudpb.model.Usuario;
import com.cliente.projeto.crudpb.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /*
     * TP5 - Requisito 1: Refatoração e Organização do Código
     * "Aplicar o Princípio da Imutabilidade, separando consultas de modificadores..."
     * @Transactional(readOnly = true) garante otimização de performance para leituras.
     */
    @Transactional(readOnly = true)
    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Evento buscarPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento não encontrado com ID: " + id));
    }

    /*
     * TP5 - Requisito 1: Refatoração e Organização do Código
     * "Simplificar condicionais complexas, substituindo por cláusulas de guarda..."
     * A validação foi movida para o início (fail-fast) antes de processar a lógica.
     */
    @Transactional
    public Evento criarEvento(Evento evento, Long usuarioId) {
        // Cláusula de Guarda: Valida regra de negócio antes de prosseguir
        validarNomeDuplicado(evento.getNome(), null);

        // TP5 - Requisito 1: "Reorganizar métodos... eliminando redundâncias"
        // Reutilização do serviço de usuário centralizado
        Usuario criador = usuarioService.buscarPorId(usuarioId);
        evento.setUsuario(criador);

        return eventoRepository.save(evento);
    }

    @Transactional
    public Evento atualizarEvento(Long id, Evento eventoAtualizado, Long usuarioId) {
        // Cláusula de Guarda
        validarNomeDuplicado(eventoAtualizado.getNome(), id);

        Evento eventoExistente = buscarPorId(id);
        Usuario criador = usuarioService.buscarPorId(usuarioId);

        // TP5 - Requisito 1: "Garantir modularidade e coesão"
        // Atualização explícita mantendo a consistência da entidade gerenciada
        eventoExistente.setUsuario(criador);
        eventoExistente.setNome(eventoAtualizado.getNome());
        eventoExistente.setDescricao(eventoAtualizado.getDescricao());

        return eventoRepository.save(eventoExistente);
    }

    @Transactional
    public void deletarEvento(Long id) {
        // Cláusula de Guarda para verificação de existência
        if (!eventoRepository.existsById(id)) {
             throw new RecursoNaoEncontradoException("Evento não encontrado para deletar.");
        }
        eventoRepository.deleteById(id);
    }

    /*
     * TP5 - Requisito 1: Refatoração
     * "Eliminar redundâncias e... otimizar trechos redundantes"
     * Método privado encapsulando a lógica de verificação de duplicidade.
     */
    private void validarNomeDuplicado(String nome, Long idExcecao) {
        Optional<Evento> conflito = eventoRepository.findByNome(nome);
        
        if (conflito.isPresent()) {
            // Lógica simplificada para verificar se o ID é diferente
            if (idExcecao == null || !conflito.get().getId().equals(idExcecao)) {
                throw new ValidacaoException("O nome '" + nome + "' já está em uso por outro evento.");
            }
        }
    }
}