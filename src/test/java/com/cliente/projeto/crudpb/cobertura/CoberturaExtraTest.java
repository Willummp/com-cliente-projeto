package test.java.com.cliente.projeto.crudpb.cobertura;

import com.cliente.projeto.crudpb.dto.ErroDTO;
import com.cliente.projeto.crudpb.dto.EventoDTO;
import com.cliente.projeto.crudpb.dto.UsuarioDTO;
import com.cliente.projeto.crudpb.exception.RecursoNaoEncontradoException;
import com.cliente.projeto.crudpb.exception.ValidacaoException;
import com.cliente.projeto.crudpb.model.Evento;
import com.cliente.projeto.crudpb.model.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 * TP5 - Requisito Não Funcional
 * "A cobertura de testes mínima deve ser de 90%..."
 * Esta classe de teste garante a cobertura de métodos simples (Getters/Setters/Construtores)
 * em Models, DTOs e Exceptions, blindando o código contra regressões estruturais.
 */
class CoberturaExtraTest {

    @Test
    void testarModelUsuario() {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setNome("Teste");
        u.setEmail("teste@email.com");

        assertEquals(1L, u.getId());
        assertEquals("Teste", u.getNome());
        assertEquals("teste@email.com", u.getEmail());

        Usuario u2 = new Usuario("Nome", "Email");
        assertNotNull(u2);
    }

    @Test
    void testarModelEvento() {
        Evento e = new Evento();
        e.setId(1L);
        e.setNome("Evento");
        e.setDescricao("Desc");
        Usuario u = new Usuario();
        e.setUsuario(u);

        assertEquals(1L, e.getId());
        assertEquals("Evento", e.getNome());
        assertEquals("Desc", e.getDescricao());
        assertEquals(u, e.getUsuario());

        Evento e2 = new Evento("Nome", "Desc");
        assertNotNull(e2);
    }

    @Test
    void testarDTOs() {
        // TP5 - Req 1: Testando imutabilidade dos Records (DTOs)
        EventoDTO eDto = new EventoDTO("Nome", "Desc", 1L);
        assertEquals("Nome", eDto.nome());
        assertEquals("Desc", eDto.descricao());
        assertEquals(1L, eDto.usuarioId());
        assertNotNull(eDto.toEntity());

        UsuarioDTO uDto = new UsuarioDTO("User", "email@email.com");
        assertEquals("User", uDto.nome());
        assertNotNull(uDto.toEntity());

        ErroDTO erro = new ErroDTO("time", 400, "Bad Request", "Msg", "/path");
        assertEquals("Msg", erro.message());
        assertEquals(400, erro.status());
    }

    @Test
    void testarExceptions() {
        // Garante que as exceções personalizadas estão repassando mensagens corretamente
        RecursoNaoEncontradoException ex1 = new RecursoNaoEncontradoException("Não achou");
        assertEquals("Não achou", ex1.getMessage());

        ValidacaoException ex2 = new ValidacaoException("Erro validação");
        assertEquals("Erro validação", ex2.getMessage());
    }
}