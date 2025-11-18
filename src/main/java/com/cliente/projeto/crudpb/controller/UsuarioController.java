package com.cliente.projeto.crudpb.controller;

import com.cliente.projeto.crudpb.dto.UsuarioDTO;
import com.cliente.projeto.crudpb.exception.ValidacaoException;
import com.cliente.projeto.crudpb.model.Usuario;
import com.cliente.projeto.crudpb.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios") // Novo endpoint: /usuarios
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Injeção via construtor
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // READ (Listagem)
    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "lista-usuarios"; // Novo HTML: 'lista-usuarios.html'
    }

    // CREATE (Mostrar formulário)
    @GetMapping("/novo")
    public String mostrarFormularioNovo(Model model) {
        model.addAttribute("usuarioDTO", new UsuarioDTO("", ""));
        model.addAttribute("pageTitle", "Novo Usuário");
        return "form-usuario"; // Novo HTML: 'form-usuario.html'
    }

    // CREATE (Salvar)
    @PostMapping
    public String salvarUsuario(@Valid @ModelAttribute("usuarioDTO") UsuarioDTO usuarioDTO,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Novo Usuário");
            return "form-usuario";
        }

        try {
            usuarioService.criarUsuario(usuarioDTO.toEntity());
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Usuário criado com sucesso!");
            return "redirect:/usuarios";

        } catch (ValidacaoException ex) {
            model.addAttribute("pageTitle", "Novo Usuário");
            model.addAttribute("mensagemErro", ex.getMessage());
            return "form-usuario";
        }
    }

    // UPDATE (Mostrar formulário de edição)
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id); // Já lida com 404
        UsuarioDTO dto = new UsuarioDTO(usuario.getNome(), usuario.getEmail());

        model.addAttribute("usuarioDTO", dto);
        model.addAttribute("usuarioId", id);
        model.addAttribute("pageTitle", "Editar Usuário");
        return "form-usuario";
    }

    // UPDATE (Atualizar)
    @PostMapping("/{id}")
    public String atualizarUsuario(@PathVariable Long id,
                                     @Valid @ModelAttribute("usuarioDTO") UsuarioDTO usuarioDTO,
                                     BindingResult bindingResult,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Editar Usuário");
            model.addAttribute("usuarioId", id);
            return "form-usuario";
        }

        try {
            usuarioService.atualizarUsuario(id, usuarioDTO.toEntity());
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Usuário atualizado com sucesso!");
            return "redirect:/usuarios";

        } catch (ValidacaoException ex) {
            model.addAttribute("pageTitle", "Editar Usuário");
            model.addAttribute("usuarioId", id);
            model.addAttribute("mensagemErro", ex.getMessage());
            return "form-usuario";
        }
    }

    // DELETE
    @GetMapping("/deletar/{id}")
    public String deletarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        usuarioService.deletarUsuario(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Usuário deletado com sucesso!");
        return "redirect:/usuarios";
    }
}