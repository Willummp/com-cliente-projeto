package test.java.com.cliente.projeto.crudpb.cobertura;

import com.cliente.projeto.crudpb.controller.EventoController;
import com.cliente.projeto.crudpb.controller.UsuarioController;
import com.cliente.projeto.crudpb.dto.ErroDTO;
import com.cliente.projeto.crudpb.dto.EventoDTO;
import com.cliente.projeto.crudpb.dto.UsuarioDTO;
import com.cliente.projeto.crudpb.exception.GlobalExceptionHandler;
import com.cliente.projeto.crudpb.exception.RecursoNaoEncontradoException;
import com.cliente.projeto.crudpb.exception.ValidacaoException;
import com.cliente.projeto.crudpb.model.Evento;
import com.cliente.projeto.crudpb.model.Usuario;
import com.cliente.projeto.crudpb.repository.EventoRepository;
import com.cliente.projeto.crudpb.repository.UsuarioRepository;
import com.cliente.projeto.crudpb.service.EventoService;
import com.cliente.projeto.crudpb.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings; // Importante
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // <--- A SOLUÇÃO MÁGICA: Permite stubs não usados
class CoberturaTotalTest {

    @Mock EventoRepository eventoRepository;
    @Mock UsuarioRepository usuarioRepository;
    @Mock BindingResult bindingResult;
    @Mock HttpServletRequest request;
    @Mock MethodArgumentNotValidException methodArgumentNotValidException;

    EventoService eventoService;
    UsuarioService usuarioService;
    EventoController eventoController;
    UsuarioController usuarioController;
    GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setup() {
        usuarioService = new UsuarioService(usuarioRepository);
        eventoService = new EventoService(eventoRepository, usuarioService);
        eventoController = new EventoController(eventoService, usuarioService);
        usuarioController = new UsuarioController(usuarioService);
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testEventoController_FluxoCompleto() {
        Model model = new ConcurrentModel();
        RedirectAttributes redirect = new RedirectAttributesModelMap();
        
        Usuario usuarioMock = new Usuario("User Teste", "user@teste.com");
        usuarioMock.setId(1L);
        
        Evento eventoMock = new Evento("Evento Teste", "Descricao");
        eventoMock.setId(10L);
        eventoMock.setUsuario(usuarioMock);

        // Mocks necessários
        when(eventoRepository.findAll()).thenReturn(List.of(eventoMock));
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioMock));
        when(eventoRepository.findById(10L)).thenReturn(Optional.of(eventoMock));
        
        // Testar LISTAR
        String viewListar = eventoController.listarEventos(model);
        assertEquals("lista-eventos", viewListar);

        // Testar NOVO
        String viewNovo = eventoController.mostrarFormularioNovo(model);
        assertEquals("form-evento", viewNovo);

        // Testar EDITAR
        String viewEditar = eventoController.mostrarFormularioEditar(10L, model);
        assertEquals("form-evento", viewEditar);

        // Testar DELETAR
        when(eventoRepository.existsById(10L)).thenReturn(true);
        String viewDelete = eventoController.deletarEvento(10L, redirect);
        assertEquals("redirect:/eventos", viewDelete);
    }

    @Test
    void testEventoController_Salvar_Cenarios() {
        Model model = new ConcurrentModel();
        RedirectAttributes redirect = new RedirectAttributesModelMap();
        EventoDTO dto = new EventoDTO("Nome", "Desc", 1L);
        
        // Cenário 1: Erro de Validação
        when(bindingResult.hasErrors()).thenReturn(true);
        String viewErro = eventoController.salvarEvento(dto, bindingResult, model, redirect);
        assertEquals("form-evento", viewErro);

        // Cenário 2: Sucesso
        when(bindingResult.hasErrors()).thenReturn(false);
        Usuario u = new Usuario(); u.setId(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        when(eventoRepository.save(any())).thenReturn(new Evento());
        
        String viewSucesso = eventoController.salvarEvento(dto, bindingResult, model, redirect);
        assertEquals("redirect:/eventos", viewSucesso);

        // Cenário 3: Exceção de Negócio
        when(eventoRepository.findByNome("Nome")).thenReturn(Optional.of(new Evento())); 
        
        String viewException = eventoController.salvarEvento(dto, bindingResult, model, redirect);
        assertEquals("form-evento", viewException);
    }

    @Test
    void testEventoController_Atualizar_Cenarios() {
        Model model = new ConcurrentModel();
        RedirectAttributes redirect = new RedirectAttributesModelMap();
        EventoDTO dto = new EventoDTO("Nome", "Desc", 1L);
        Long idEvento = 10L;

        // Cenário 1: Erro de Validação
        when(bindingResult.hasErrors()).thenReturn(true);
        String viewErro = eventoController.atualizarEvento(idEvento, dto, bindingResult, model, redirect);
        assertEquals("form-evento", viewErro);

        // Cenário 2: Sucesso
        when(bindingResult.hasErrors()).thenReturn(false);
        Evento eventoExistente = new Evento(); eventoExistente.setId(idEvento);
        Usuario u = new Usuario(); u.setId(1L);
        
        when(eventoRepository.findById(idEvento)).thenReturn(Optional.of(eventoExistente));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        when(eventoRepository.save(any())).thenReturn(eventoExistente);

        String viewSucesso = eventoController.atualizarEvento(idEvento, dto, bindingResult, model, redirect);
        assertEquals("redirect:/eventos", viewSucesso);

        // Cenário 3: Exceção de Negócio
        Evento outroEvento = new Evento(); outroEvento.setId(99L);
        when(eventoRepository.findByNome("Nome")).thenReturn(Optional.of(outroEvento));
        
        String viewException = eventoController.atualizarEvento(idEvento, dto, bindingResult, model, redirect);
        assertEquals("form-evento", viewException);
    }

    // --- TESTES DE COBERTURA GERAL ---

    @Test
    void testUsuarioController_Completo() {
        Model model = new ConcurrentModel();
        RedirectAttributes redirect = new RedirectAttributesModelMap();
        UsuarioDTO dto = new UsuarioDTO("User", "email@email.com");
        Usuario u = new Usuario(); u.setId(1L);

        when(usuarioRepository.findAll()).thenReturn(List.of(u));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        when(usuarioRepository.save(any())).thenReturn(u);

        usuarioController.listarUsuarios(model);
        usuarioController.mostrarFormularioNovo(model);
        usuarioController.mostrarFormularioEditar(1L, model);
        usuarioController.deletarUsuario(1L, redirect);

        when(bindingResult.hasErrors()).thenReturn(false);
        usuarioController.salvarUsuario(dto, bindingResult, model, redirect);
        
        when(bindingResult.hasErrors()).thenReturn(true);
        usuarioController.salvarUsuario(dto, bindingResult, model, redirect);
    }

    @Test
    void testGlobalExceptionHandler() {
        when(request.getRequestURI()).thenReturn("/teste");
        Exception ex = new Exception("Erro Genérico");
        ResponseEntity<ErroDTO> response = exceptionHandler.handleGenericException(ex, request);
        assertEquals(500, response.getStatusCode().value());

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("obj", "campo", "msg")));
        
        ResponseEntity<ErroDTO> responseValid = exceptionHandler.handleValidationErrors(methodArgumentNotValidException, request);
        assertEquals(400, responseValid.getStatusCode().value());
    }
    
    @Test
    void testServices_Calls() {
        // Garante cobertura dos métodos transacionais dos services
        Usuario u = new Usuario(); u.setId(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        
        usuarioService.listarTodos();
        assertThrows(RecursoNaoEncontradoException.class, () -> usuarioService.buscarPorId(99L));
        
        when(eventoRepository.existsById(99L)).thenReturn(false);
        assertThrows(RecursoNaoEncontradoException.class, () -> eventoService.deletarEvento(99L));
    }
}