package com.cliente.projeto.crudpb.controller;

import com.cliente.projeto.crudpb.dto.EventoDTO;
import com.cliente.projeto.crudpb.exception.ValidacaoException;
import com.cliente.projeto.crudpb.model.Evento;
import com.cliente.projeto.crudpb.service.EventoService;
import com.cliente.projeto.crudpb.service.UsuarioService; // Importado
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService eventoService;
    private final UsuarioService usuarioService; // Injetado para a integração

    public EventoController(EventoService eventoService, UsuarioService usuarioService) {
        this.eventoService = eventoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listarEventos(Model model) {
        model.addAttribute("eventos", eventoService.listarTodos());
        return "lista-eventos"; // Renderiza 'lista-eventos.html'
    }

    @GetMapping("/novo")
    public String mostrarFormularioNovo(Model model) {
        // Passa o DTO vazio e a lista de usuários para o dropdown
        model.addAttribute("eventoDTO", new EventoDTO("", "", null));
        model.addAttribute("todosUsuarios", usuarioService.listarTodos()); // INTEGRAÇÃO
        model.addAttribute("pageTitle", "Novo Evento");
        return "form-evento"; // Renderiza 'form-evento.html'
    }

    @PostMapping
    public String salvarEvento(@Valid @ModelAttribute("eventoDTO") EventoDTO eventoDTO,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Novo Evento");
            model.addAttribute("todosUsuarios", usuarioService.listarTodos()); // Devolve a lista em caso de erro
            return "form-evento";
        }

        try {
            // Passa o DTO e o ID do usuário para o serviço
            eventoService.criarEvento(eventoDTO.toEntity(), eventoDTO.usuarioId()); // INTEGRAÇÃO

            redirectAttributes.addFlashAttribute("mensagemSucesso", "Evento criado com sucesso!");
            return "redirect:/eventos";

        } catch (ValidacaoException ex) {
            model.addAttribute("pageTitle", "Novo Evento");
            model.addAttribute("todosUsuarios", usuarioService.listarTodos()); // Devolve a lista em caso de erro
            model.addAttribute("mensagemErro", ex.getMessage());
            return "form-evento";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Evento evento = eventoService.buscarPorId(id); // Já lida com 404
        
        // Cria o DTO com os dados existentes, incluindo o ID do usuário
        EventoDTO dto = new EventoDTO(
                evento.getNome(), 
                evento.getDescricao(), 
                evento.getUsuario().getId() // INTEGRAÇÃO
        );

        model.addAttribute("eventoDTO", dto);
        model.addAttribute("todosUsuarios", usuarioService.listarTodos()); // Passa a lista de usuários
        model.addAttribute("eventoId", id);
        model.addAttribute("pageTitle", "Editar Evento");
        return "form-evento";
    }

    @PostMapping("/{id}")
    public String atualizarEvento(@PathVariable Long id,
                                    @Valid @ModelAttribute("eventoDTO") EventoDTO eventoDTO,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Editar Evento");
            model.addAttribute("todosUsuarios", usuarioService.listarTodos()); // Devolve a lista em caso de erro
            model.addAttribute("eventoId", id);
            return "form-evento";
        }

        try {
            // Passa o DTO e o ID do usuário para o serviço
            eventoService.atualizarEvento(id, eventoDTO.toEntity(), eventoDTO.usuarioId()); // INTEGRAÇÃO
            
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Evento atualizado com sucesso!");
            return "redirect:/eventos";

        } catch (ValidacaoException ex) {
            model.addAttribute("pageTitle", "Editar Evento");
            model.addAttribute("todosUsuarios", usuarioService.listarTodos()); // Devolve a lista em caso de erro
            model.addAttribute("eventoId", id);
            model.addAttribute("mensagemErro", ex.getMessage());
            return "form-evento";
        }
    }

    @GetMapping("/deletar/{id}")
    public String deletarEvento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        eventoService.deletarEvento(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Evento deletado com sucesso!");
        return "redirect:/eventos";
    }
}