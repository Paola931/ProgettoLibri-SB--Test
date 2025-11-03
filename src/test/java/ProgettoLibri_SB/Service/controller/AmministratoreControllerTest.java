package ProgettoLibri_SB.Service.controller;

import ProgettoLibri_SB.entity.Libro;
import ProgettoLibri_SB.entity.Utente;
import ProgettoLibri_SB.exception.LibriException.LibroGiaPresenteInListaException;
import ProgettoLibri_SB.exception.LibriException.LibroNonTrovatoException;
import ProgettoLibri_SB.exception.UtenteException.UtenteNonAutorizzatoException;
import ProgettoLibri_SB.exception.UtenteException.UtenteNonTrovatoException;
import ProgettoLibri_SB.service.LibroService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles(value = "test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(locations = "classpath:application-test.yml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AmministratoreControllerTest {

    @MockitoBean
    private LibroService libroService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private MockMultipartFile copertina;
    private List<Utente> utenti;
    private List<Libro> libroEntity;

    @BeforeEach
    void setUp() throws Exception {
        copertina = new MockMultipartFile(
                "copertina",       // nome parametro
                "Odissea.jpg",             // nome file
                "image/jpeg",                  // tipo estensione
                "fakeimg".getBytes()           // contenuto
        );
        utenti = objectMapper.readValue(
                new File("src/test/java/features/Utente/UtenteEntity.json"),
                new TypeReference<List<Utente>>() {
                }

        );
        libroEntity = objectMapper.readValue(
                new File("src/test/java/features/Libro/LibroEntity.json"),
                new TypeReference<List<Libro>>() {

                }
        );
    }

    private MockHttpSession creaSessioneLoggata(Utente utente) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("utentecorrente", utente);
        return session;
    }

    @Test
    void aggiungiLibro_ok() throws Exception {
        Utente admin = utenti.getFirst();
        Libro libro = libroEntity.getFirst();

        when(libroService.aggiungiLibro(admin.getId(), libro.getTitolo(), libro.getAutore(), libro.getCe(), copertina))
                .thenReturn(libro);

        mockMvc.perform(multipart("/utente/admin/aggiungiLibro/"+ admin.getId())
                        .file(copertina)
                .param("titolo", libro.getTitolo())
                .param("autore", libro.getAutore())
                .param("ce",libro.getCe())
                .session(creaSessioneLoggata(admin)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Libro creato"))
                .andDo(print())
                .andReturn();
    }
    @Test
    void aggiungiLibro_libroGiàPresente() throws Exception {
        Utente admin = utenti.getFirst();
        Libro libro = libroEntity.getFirst();

        when(libroService.aggiungiLibro(eq(admin.getId()), eq(libro.getTitolo()), eq(libro.getAutore()), eq(libro.getCe()), eq(copertina)))
                .thenThrow(new LibroGiaPresenteInListaException("Libro già presente"));

        mockMvc.perform(multipart("/utente/admin/aggiungiLibro/"+ admin.getId())
                        .file(copertina)
                        .param("titolo", libro.getTitolo())
                        .param("autore", libro.getAutore())
                        .param("ce",libro.getCe())
                        .session(creaSessioneLoggata(admin)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Libro già presente"))
                .andDo(print())
                .andReturn();
    }
    @Test
    void aggiungiLibro_utenteNonAutorizzato() throws Exception {
        Utente lettore = utenti.get(1);
        Libro libro = libroEntity.getFirst();

        when(libroService.aggiungiLibro(eq(lettore.getId()), eq(libro.getTitolo()), eq(libro.getAutore()), eq(libro.getCe()), eq(copertina)))
                .thenThrow(new UtenteNonAutorizzatoException("Utente non autorizzato"));

        mockMvc.perform(multipart("/utente/admin/aggiungiLibro/"+ lettore.getId())
                        .file(copertina)
                        .param("titolo", libro.getTitolo())
                        .param("autore", libro.getAutore())
                        .param("ce",libro.getCe())
                        .session(creaSessioneLoggata(lettore)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Utente non autorizzato"))
                .andDo(print())
                .andReturn();
    }
    @Test
    void aggiungiLibro_utenteNonTrovato() throws Exception {
        Utente lettore = utenti.get(1);
        Libro libro = libroEntity.getFirst();

        when(libroService.aggiungiLibro(eq(0L), eq(libro.getTitolo()), eq(libro.getAutore()), eq(libro.getCe()), eq(copertina)))
                .thenThrow(new UtenteNonTrovatoException("Utente non trovato"));

        mockMvc.perform(multipart("/utente/admin/aggiungiLibro/0")
                        .file(copertina)
                        .param("titolo", libro.getTitolo())
                        .param("autore", libro.getAutore())
                        .param("ce",libro.getCe())
                        .session(creaSessioneLoggata(lettore)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Utente non trovato"))
                .andDo(print())
                .andReturn();
    }

    @Test
    void modificaLibro_ok() throws Exception {
        Utente admin = utenti.getFirst();
        Libro libro = libroEntity.getFirst();

        when(libroService.modificaLibro(admin.getId(), libro.getIdLibro(), libro.getTitolo(), libro.getAutore(), copertina))
                .thenReturn(libro);

        mockMvc.perform(multipart("/utente/admin/modificaLibro/"+admin.getId())
                        .file(copertina)
                        .with(request -> {request.setMethod("PATCH");
                        return request;})
                .session(creaSessioneLoggata(admin))
                .param("id_libro","1")
                .param("titolo", libro.getTitolo())
                .param("autore", libro.getAutore()))
                .andExpect(status().isOk())
                .andExpect(content().string("Modifiche apportate"))
                .andDo(print())
                .andReturn();
    }
    @Test
    void modificaLibro_libroNonTrovato() throws Exception {
        Utente admin = utenti.getFirst();
        Libro libro = libroEntity.getFirst();

        when(libroService.modificaLibro(eq(admin.getId()), eq(0L), eq(libro.getTitolo()), eq(libro.getAutore()), eq(copertina)))
                .thenThrow(new LibroNonTrovatoException("Libro non trovato"));

        mockMvc.perform(multipart("/utente/admin/modificaLibro/"+admin.getId())
                        .file(copertina)
                        .with(request -> {request.setMethod("PATCH");
                            return request;})
                        .session(creaSessioneLoggata(admin))
                        .param("id_libro","0")
                        .param("titolo", libro.getTitolo())
                        .param("autore", libro.getAutore()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Libro non trovato"))
                .andDo(print())
                .andReturn();
    }
    @Test
    void modificaLibro_utenteNonAutorizzato() throws Exception {
        Utente lettore = utenti.get(1);
        Libro libro = libroEntity.getFirst();

        when(libroService.modificaLibro(eq(lettore.getId()), eq(libro.getIdLibro()), eq(libro.getTitolo()), eq(libro.getAutore()), eq(copertina)))
                .thenThrow(new UtenteNonAutorizzatoException("Utente non autorizzato"));

        mockMvc.perform(multipart("/utente/admin/modificaLibro/"+lettore.getId())
                        .file(copertina)
                        .with(request -> {request.setMethod("PATCH");
                            return request;})
                        .session(creaSessioneLoggata(lettore))
                        .param("id_libro","1")
                        .param("titolo", libro.getTitolo())
                        .param("autore", libro.getAutore()))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Utente non autorizzato"))
                .andDo(print())
                .andReturn();
    }
    @Test
    void modificaLibro_utenteNonTrovato() throws Exception {
        Utente admin = utenti.get(0);
        Libro libro = libroEntity.getFirst();

        when(libroService.modificaLibro(eq(0L), eq(libro.getIdLibro()), eq(libro.getTitolo()), eq(libro.getAutore()), eq(copertina)))
                .thenThrow(new UtenteNonTrovatoException("Utente non trovato"));

        mockMvc.perform(multipart("/utente/admin/modificaLibro/0")
                        .file(copertina)
                        .with(request -> {request.setMethod("PATCH");
                            return request;})
                        .session(creaSessioneLoggata(admin))
                        .param("id_libro","1")
                        .param("titolo", libro.getTitolo())
                        .param("autore", libro.getAutore()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Utente non trovato"))
                .andDo(print())
                .andReturn();
    }

    @Test
    void eliminaLibro_ok() throws Exception {
        Utente admin = utenti.getFirst();
        Libro libro = libroEntity.getFirst();

        when(libroService.eliminaLibro(libro.getIdLibro(), admin.getId(), libro.getTitolo(), libro.getAutore()))
                .thenReturn("Libro eliminato: " + libro);

        mockMvc.perform(delete("/utente/admin/eliminaLibro/"+libro.getIdLibro())
                .session(creaSessioneLoggata(admin))
                .param("id_utente", "1")
                .param("titolo", libro.getTitolo())
                .param("autore", libro.getAutore()))
                .andExpect(status().isGone())
                .andExpect(content().string("Libro eliminato"))
                .andDo(print())
                .andReturn();

    }
    @Test
    void eliminaLibro_libroNonTrovato() throws Exception {
        Utente admin = utenti.getFirst();
        Libro libro = libroEntity.getFirst();

        when(libroService.eliminaLibro(eq(0L), eq(admin.getId()), eq(libro.getTitolo()), eq(libro.getAutore())))
                .thenThrow(new LibroNonTrovatoException("Libro non trovato"));

        mockMvc.perform(delete("/utente/admin/eliminaLibro/0")
                        .session(creaSessioneLoggata(admin))
                        .param("id_utente", "1")
                        .param("titolo", libro.getTitolo())
                        .param("autore", libro.getAutore()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Libro non trovato"))
                .andDo(print())
                .andReturn();

    }
    @Test
    void eliminaLibro_utenteNonAutorizzato() throws Exception {
        Utente lettore = utenti.get(1);
        Libro libro = libroEntity.getFirst();

        when(libroService.eliminaLibro(eq(libro.getIdLibro()), eq(lettore.getId()), eq(libro.getTitolo()), eq(libro.getAutore())))
                .thenThrow(new UtenteNonAutorizzatoException("Utente non autorizzato"));

        mockMvc.perform(delete("/utente/admin/eliminaLibro/" + lettore.getId())
                        .session(creaSessioneLoggata(lettore))
                        .param("id_utente", "1")
                        .param("titolo", libro.getTitolo())
                        .param("autore", libro.getAutore()))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Utente non autorizzato"))
                .andDo(print())
                .andReturn();

    }
    @Test
    void eliminaLibro_utenteNonTrovato() throws Exception {
        Utente admin = utenti.get(0);
        Libro libro = libroEntity.getFirst();

        when(libroService.eliminaLibro(eq(libro.getIdLibro()), eq(0L), eq(libro.getTitolo()), eq(libro.getAutore())))
                .thenThrow(new UtenteNonTrovatoException("Utente non trovato"));

        mockMvc.perform(delete("/utente/admin/eliminaLibro/" + libro.getIdLibro())
                        .session(creaSessioneLoggata(admin))
                        .param("id_utente", "0")
                        .param("titolo", libro.getTitolo())
                        .param("autore", libro.getAutore()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Utente non trovato"))
                .andDo(print())
                .andReturn();

    }
}