package com.cliente.projeto.crudpb.controller;

import com.cliente.projeto.crudpb.model.Usuario;
import com.cliente.projeto.crudpb.service.EventoService;
import com.cliente.projeto.crudpb.service.UsuarioService; 
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when; 
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(EventoController.class) 
class EventoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc; 

    @MockBean
    private EventoService eventoService; 

    @MockBean
    private UsuarioService usuarioService;

    /**
     * Exemplo de Teste Parametrizado
     * Testa múltiplas entradas inválidas no formulário de criação.
     */
    @ParameterizedTest
    @CsvSource({
            // Nome inválido
            "'', 'Descrição válida', 1, 'O nome é obrigatório.'",
            // Nome inválido
            "'OK', 'Descrição válida', 1, 'O nome deve ter entre 3 e 100 caracteres.'",
            // ID do Usuário inválido (nulo)
            "'Nome Válido', 'Desc Válida', , 'O usuário criador é obrigatório.'"
    })
    void deveRetornarErroNoFormulario_ParaEntradasInvalidas(String nome, String descricao, String usuarioId, String mensagemErroEsperada) throws Exception {
        when(usuarioService.listarTodos()).thenReturn(Collections.singletonList(new Usuario("Teste", "teste@email.com")));

        mockMvc.perform(post("/eventos")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("nome", nome)
                        .param("descricao", descricao)
                        .param("usuarioId", usuarioId)
                )
                .andExpect(status().isOk()) // Espera HTTP 200 (OK), pois re-renderiza o form
                .andExpect(view().name("form-evento")) // Confirma que voltou para o formulário
                .andExpect(model().hasErrors()) // Confirma que há erros de validação
                .andExpect(content().string(org.hamcrest.Matchers.containsString(mensagemErroEsperada))); // Verifica a mensagem
    }

    @Test
    void deveCriarEventoComSucesso_ParaEntradaValida() throws Exception {
        mockMvc.perform(post("/eventos")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("nome", "Evento de Teste Válido")
                        .param("descricao", "Uma descrição completa")
                        .param("usuarioId", "1") // Parâmetro de integração obrigatório
                )
                .andExpect(status().is3xxRedirection()) // Espera HTTP 302 (Redirecionamento)
                .andExpect(view().name("redirect:/eventos"))
                .andExpect(redirectedUrl("/eventos"))
                .andExpect(flash().attributeExists("mensagemSucesso"));
    }
}