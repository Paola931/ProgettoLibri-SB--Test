package ProgettoLibri_SB.Service.controller;

import ProgettoLibri_SB.entity.LibriLetti;
import ProgettoLibri_SB.entity.Libro;
import ProgettoLibri_SB.entity.ListaDesideri;
import ProgettoLibri_SB.entity.Utente;
import ProgettoLibri_SB.exception.UtenteException.UtenteNonAutorizzatoException;
import ProgettoLibri_SB.exception.UtenteException.UtenteNonTrovatoException;
import ProgettoLibri_SB.service.LibriLService;
import ProgettoLibri_SB.service.LibroService;
import ProgettoLibri_SB.service.ListaDService;
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
import java.util.ArrayList;
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
class LettoreControllerTest {


    @MockitoBean
    private LibroService libroService;
    @MockitoBean
    private LibriLService libriLService;
    @MockitoBean
    private ListaDService listaDService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private MockMultipartFile copertina;
    private List<Utente> utenti;
    private List<Libro> libroEntity;
    private List<ListaDesideri> libriListaD;
    private List<LibriLetti> libriLetti;
    @BeforeEach
    void setUp() throws Exception {
        libriLetti = new ArrayList<>();
        libriListaD = new ArrayList<>();
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
    void aggiungiLibriLetti_ok() throws Exception {
        Utente admin = utenti.getFirst();
        Utente lettore = utenti.get(3);
        Libro libro = libroEntity.getFirst();

        when(libroService.aggiungiLibro(admin.getId(), libro.getTitolo(), libro.getAutore(), libro.getCe(), copertina))
                .thenReturn(libro);

        mockMvc.perform(multipart("/utente/admin/aggiungiLibro/"+ admin.getId())
                        .file(copertina)
                .param("titolo","Odissea")
                .param("autore","Omero")
                .param("ce","ce")
                        .session(creaSessioneLoggata(admin)))
                        .andExpect(status().isCreated())
                        .andExpect(content().string("Libro creato"))
                .andDo(print())
                .andReturn();

        when(libriLService.aggiungiLibriLetti(lettore.getId(), libro.getTitolo(), libro.getAutore(), 5))
                .thenReturn("Libro aggiunto alla lista: " + libro);

        mockMvc.perform(post("/utente/lettore/aggiungiLibriLetti/" + lettore.getId())
                .param("titolo","Odissea")
                .param("autore","Omero")
                .param("voto", "5")
                .session(creaSessioneLoggata(lettore)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .string("Libro aggiunto correttamente alla tua lista di Libri Letti"))
                .andDo(print())
                .andReturn();
    }
    @Test
    void aggiungiLibriLetti_utenteNonAutorizzato() throws Exception {
        Utente admin = utenti.getFirst();
        Libro libro = libroEntity.getFirst();

        when(libriLService.aggiungiLibriLetti(eq(admin.getId()), eq(libro.getTitolo()), eq(libro.getAutore()),eq(5)))
                .thenThrow(new UtenteNonAutorizzatoException("Utente non autorizzato"));

        mockMvc.perform(post("/utente/lettore/aggiungiLibriLetti/" + admin.getId())
                        .param("titolo","Odissea")
                        .param("autore","Omero")
                        .param("voto", "5")
                        .session(creaSessioneLoggata(admin)))
                .andExpect(status().isForbidden())
                .andExpect(content()
                        .string("Utente non autorizzato"))
                .andDo(print())
                .andReturn();
    }
    @Test
    void aggiungiLibriLetti_utenteNonTrovato() throws Exception {
        Utente lettore = utenti.get(1);
        when(libriLService.aggiungiLibriLetti(eq(0l),eq("titolo"),eq("autore"),eq(5)))
                .thenThrow(new UtenteNonTrovatoException("Utente non trovato"));

        mockMvc.perform(post("/utente/lettore/aggiungiLibriLetti/0")
                .param("titolo","titolo")
                .param("autore","autore")
                        .param("voto","5")
                .session(creaSessioneLoggata(lettore)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Utente non trovato"))
                .andDo(print())
                .andReturn();
    }

    @Test
    void aggiungiListaDesideri() throws Exception {
        Utente admin = utenti.getFirst();
        Utente lettore = utenti.get(3);
        Libro libro = libroEntity.getFirst();

        when(libroService.aggiungiLibro(admin.getId(), libro.getTitolo(), libro.getAutore(), libro.getCe(), copertina))
                .thenReturn(libro);

        mockMvc.perform(multipart("/utente/admin/aggiungiLibro/"+ admin.getId())
                        .file(copertina)
                        .param("titolo","Odissea")
                        .param("autore","Omero")
                        .param("ce","ce")
                        .session(creaSessioneLoggata(admin)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Libro creato"))
                .andDo(print())
                .andReturn();

        when(listaDService.aggiungiListaDesideri(lettore.getId(), libro.getTitolo(), libro.getAutore()))
                .thenReturn("Libro aggiunto alla lista: " + libro);

        mockMvc.perform(post("/utente/lettore/aggiungiListaDesideri/" + lettore.getId())
                        .param("titolo","Odissea")
                        .param("autore","Omero")
                        .session(creaSessioneLoggata(lettore)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .string("Libro aggiunto correttamente alla tua Lista Desideri"))
                .andDo(print())
                .andReturn();
    }
    @Test
    void aggiungiListaDesideri_utenteNonAutorizzato() throws Exception {
        Utente admin = utenti.getFirst();
        Libro libro = libroEntity.getFirst();

        when(listaDService.aggiungiListaDesideri(eq(admin.getId()), eq(libro.getTitolo()), eq(libro.getAutore())))
                .thenThrow(new UtenteNonAutorizzatoException("Utente non autorizzato"));

        mockMvc.perform(post("/utente/lettore/aggiungiListaDesideri/" + admin.getId())
                        .param("titolo","Odissea")
                        .param("autore","Omero")
                        .session(creaSessioneLoggata(admin)))
                .andExpect(status().isForbidden())
                .andExpect(content()
                        .string("Utente non autorizzato"))
                .andDo(print())
                .andReturn();
    }
    @Test
    void aggiungiListaDesideri_utenteNonTrovato() throws Exception {
        Utente lettore = utenti.get(1);
        when(listaDService.aggiungiListaDesideri(eq(0l),eq("titolo"),eq("autore")))
                .thenThrow(new UtenteNonTrovatoException("Utente non trovato"));
        mockMvc.perform(post("/utente/lettore/aggiungiListaDesideri/0")
                .param("titolo","titolo")
                .param("autore","autore")
                .session(creaSessioneLoggata(lettore)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Utente non trovato"))
                .andDo(print())
                .andReturn();
    }

    @Test
    void visualizzaListaD_ok() throws Exception {
        Utente lettore = utenti.get(1);

        when(listaDService.visualizzaListaD(lettore.getId()))
                .thenReturn(libriListaD);

        mockMvc.perform(get("/utente/lettore/ListaDesideri/" + lettore.getId())
                        .session(creaSessioneLoggata(lettore)))
                .andExpect(status().isOk())
                .andExpect(content().string("Lista Desideri"))
                .andDo(print())
                .andReturn();
    }
    @Test
    void visualizzaListaD_utenteNonTrovato() throws Exception {
        Utente lettore = utenti.get(1);
        when(listaDService.visualizzaListaD(0L))
                .thenThrow(new UtenteNonTrovatoException("Utente non trovato"));

        mockMvc.perform(get("/utente/lettore/ListaDesideri/0")
                        .session(creaSessioneLoggata(lettore)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Utente non trovato"))
                .andDo(print())
                .andReturn();
    }
    @Test
    void visualizzaListaD_utenteNonAutorizzato() throws Exception {
        Utente admin = utenti.get(0);
        when(listaDService.visualizzaListaD(admin.getId()))
                .thenThrow(new UtenteNonAutorizzatoException("Utente non autorizzato"));

        mockMvc.perform(get("/utente/lettore/ListaDesideri/" + admin.getId())
                        .session(creaSessioneLoggata(admin)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Utente non autorizzato"))
                .andDo(print())
                .andReturn();
    }

    @Test
    void visualizzaLibriLetti_ok() throws Exception {
        Utente lettore = utenti.get(1);

        when(libriLService.visualizzaLibriLetti(lettore.getId()))
                .thenReturn(libriLetti);

        mockMvc.perform(get("/utente/lettore/LibriLetti/"+lettore.getId())
                .session(creaSessioneLoggata(lettore)))
                .andExpect(status().isOk())
                .andExpect(content().string("Lista Libri Letti"))
                .andDo(print())
                .andReturn();
    }
    @Test
    void visualizzaLibriLetti_utenteNonTrovato() throws Exception {
       Utente lettore = utenti.get(1);
        when(libriLService.visualizzaLibriLetti(0L))
                .thenThrow(new UtenteNonTrovatoException("Utente non trovato"));

        mockMvc.perform(get("/utente/lettore/LibriLetti/0")
                        .session(creaSessioneLoggata(lettore)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Utente non trovato"))
                .andDo(print())
                .andReturn();
    }
    @Test
    void visualizzaLibriLetti_utenteNonAutorizzato() throws Exception {
        Utente admin = utenti.get(0);
        when(libriLService.visualizzaLibriLetti(admin.getId()))
                .thenThrow(new UtenteNonAutorizzatoException("Utente non autorizzato"));

        mockMvc.perform(get("/utente/lettore/LibriLetti/" + admin.getId())
                        .session(creaSessioneLoggata(admin)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Utente non autorizzato"))
                .andDo(print())
                .andReturn();
    }
}