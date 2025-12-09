package com.cliente.projeto.crudpb.controller;

import com.cliente.projeto.crudpb.dto.UsuarioDTO;
import com.cliente.projeto.crudpb.exception.ValidacaoException;
import com.cliente.projeto.crudpb.model.Usuario;
import com.cliente.projeto.crudpb.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UsuarioService usuarioService;

  @Test
  void listarUsuarios_DeveRetornarViewComLista() throws Exception {
    when(usuarioService.listarTodos()).thenReturn(List.of(new Usuario("Teste", "teste@email.com")));

    mockMvc.perform(get("/usuarios"))
        .andExpect(status().isOk())
        .andExpect(view().name("lista-usuarios"))
        .andExpect(model().attributeExists("usuarios"));
  }

  @Test
  void mostrarFormularioNovo_DeveRetornarViewCorreta() throws Exception {
    mockMvc.perform(get("/usuarios/novo"))
        .andExpect(status().isOk())
        .andExpect(view().name("form-usuario"))
        .andExpect(model().attributeExists("usuarioDTO"));
  }

  @Test
  void salvarUsuario_ComDadosValidos_DeveRedirecionar() throws Exception {
    mockMvc.perform(post("/usuarios")
        .param("nome", "Novo Usuario")
        .param("email", "novo@email.com"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/usuarios"))
        .andExpect(flash().attributeExists("mensagemSucesso"));

    verify(usuarioService).criarUsuario(any(Usuario.class));
  }

  @Test
  void salvarUsuario_ComErroValidacao_DeveRetornarFormulario() throws Exception {
    mockMvc.perform(post("/usuarios")
        .param("nome", "") // Nome vazio inválido
        .param("email", "email"))
        .andExpect(status().isOk())
        .andExpect(view().name("form-usuario"));

    verify(usuarioService, never()).criarUsuario(any(Usuario.class));
  }

  @Test
  void salvarUsuario_ComExcecaoServico_DeveRetornarFormularioComErro() throws Exception {
    doThrow(new ValidacaoException("Email duplicado")).when(usuarioService).criarUsuario(any(Usuario.class));

    mockMvc.perform(post("/usuarios")
        .param("nome", "Usuario Duplicado")
        .param("email", "duplicado@email.com"))
        .andExpect(status().isOk())
        .andExpect(view().name("form-usuario"))
        .andExpect(model().attributeExists("mensagemErro"));
  }

  @Test
  void mostrarFormularioEditar_DeveRetornarViewPreenchida() throws Exception {
    Usuario usuario = new Usuario("Edit", "edit@email.com");
    when(usuarioService.buscarPorId(1L)).thenReturn(usuario);

    mockMvc.perform(get("/usuarios/editar/1"))
        .andExpect(status().isOk())
        .andExpect(view().name("form-usuario"))
        .andExpect(model().attribute("usuarioId", 1L))
        .andExpect(model().attributeExists("usuarioDTO"));
  }

  @Test
  void atualizarUsuario_ComDadosValidos_DeveRedirecionar() throws Exception {
    mockMvc.perform(post("/usuarios/1")
        .param("nome", "Usuario Atualizado")
        .param("email", "atualizado@email.com"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/usuarios"))
        .andExpect(flash().attributeExists("mensagemSucesso"));

    verify(usuarioService).atualizarUsuario(eq(1L), any(Usuario.class));
  }

  @Test
  void atualizarUsuario_ComErroValidacao_DeveRetornarFormulario() throws Exception {
    mockMvc.perform(post("/usuarios/1")
        .param("nome", "") // Inválido
        .param("email", "email"))
        .andExpect(status().isOk())
        .andExpect(view().name("form-usuario"))
        .andExpect(model().attribute("usuarioId", 1L));
  }

  @Test
  void atualizarUsuario_ComExcecaoServico_DeveRetornarFormularioComErro() throws Exception {
    doThrow(new ValidacaoException("Email duplicado")).when(usuarioService).atualizarUsuario(eq(1L),
        any(Usuario.class));

    mockMvc.perform(post("/usuarios/1")
        .param("nome", "Usuario Atualizado")
        .param("email", "duplicado@email.com"))
        .andExpect(status().isOk())
        .andExpect(view().name("form-usuario"))
        .andExpect(model().attributeExists("mensagemErro"));
  }

  @Test
  void deletarUsuario_DeveRedirecionar() throws Exception {
    mockMvc.perform(get("/usuarios/deletar/1"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/usuarios"))
        .andExpect(flash().attributeExists("mensagemSucesso"));

    verify(usuarioService).deletarUsuario(1L);
  }
}
