package ProgettoLibri_SB.Service.controller;

import ProgettoLibri_SB.controller.UtenteController;
import ProgettoLibri_SB.dto.UtenteDTO;
import ProgettoLibri_SB.entity.Libro;
import ProgettoLibri_SB.entity.Utente;
import ProgettoLibri_SB.exception.LibriException.LibroNonTrovatoException;
import ProgettoLibri_SB.exception.UtenteException.CredenzialiErrateException;
import ProgettoLibri_SB.exception.UtenteException.EmailGiaEsistenteException;
import ProgettoLibri_SB.exception.UtenteException.NomeUtenteOccupatoException;
import ProgettoLibri_SB.exception.UtenteException.UtenteNonTrovatoException;
import ProgettoLibri_SB.service.LibroService;
import ProgettoLibri_SB.service.AccessoRegistrazioneService;
import ProgettoLibri_SB.service.UtenteService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
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
class UtenteControllerTest {

    @Autowired
    private UtenteController controller;
    @MockitoBean
    private AccessoRegistrazioneService accessoRegistrazioneService;
    @MockitoBean
    private LibroService libroService;
    @MockitoBean
    private UtenteService service;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private MockMultipartFile immagineP;
    private MockMultipartFile copertina;
    private List<Utente> utenti;
    private List<UtenteDTO> dtoutente;
    private List<Libro> libroEntity;

    @BeforeEach
    void setUp() throws Exception {
        immagineP = new MockMultipartFile(
                "immagine_profilo",       // nome parametro
                "fotoprofilo.jpg",             // nome file
                "image/jpeg",                  // tipo estensione
                "fakeimg".getBytes()           // contenuto
        );
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
        dtoutente = objectMapper.readValue(
                new File("src/test/java/features/Utente/UtenteDTO.json"),
                new TypeReference<List<UtenteDTO>>() {
                }

        );
    }

    private MockHttpSession creaSessioneLoggata(Utente utente) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("utentecorrente", utente);
        return session;
    }

    @Test
    void cercaLibro_okNoCopertina() throws Exception {
        Utente admin = utenti.getFirst();

        when(libroService.cercaLibro("Odissea", "Omero"))
                .thenReturn("Il libro che cercavi è: " + libroEntity);

        mockMvc.perform(get("/utente/cercaLibro")
                        .param("titolo", "Odissea")
                        .param("autore", "Omero")
                        .session(creaSessioneLoggata(admin)))
                .andExpect(status().isOk()).andExpect(content()
                        .string(containsString("Ricerca libro completa")))
                .andDo(print())
                .andReturn();
    }

    @Test
    void cercaLibro_okConCopertina() throws Exception {
        Utente admin = utenti.getFirst();

        when(libroService.cercaLibro("Odissea", "Omero"))
                .thenReturn("Copertina libro: Odissea.jpg");

        mockMvc.perform(multipart("/utente/cercaLibro")
                        .file(copertina)
                        .with(request ->  {request.setMethod("GET");
                            return request;
                        })
                        .param("titolo", "Odissea")
                        .param("autore", "Omero")
                        .session(creaSessioneLoggata(admin)))
                .andExpect(status().isOk()).andExpect(content()
                        .string(containsString("Ricerca libro completa")))
                .andDo(print())
                .andReturn();
    }

    @Test
    void cercaLibro_libroNonPresente() throws Exception {
        Utente admin = utenti.getFirst();

        when(libroService.cercaLibro("niente", "niente"))
                .thenThrow(new LibroNonTrovatoException("Libro non trovato"));

        mockMvc.perform(get("/utente/cercaLibro")
                        .param("titolo", "niente")
                        .param("autore", "niente")
                        .session(creaSessioneLoggata(admin)))
                .andExpect(status().isNotFound()).andExpect(content()
                        .string("Libro non trovato"))
                .andDo(print())
                .andReturn();
    }

    @Test
    void modificaNomeUtente() throws Exception {
        Utente admin = utenti.getFirst();
        UtenteDTO dto = dtoutente.getFirst();
        when(service.modificaNomeUtente("Clara", dto)).
                thenReturn("Nome utente modificato con successo: " + admin.getNomeUtente());
        String dtoJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(patch("/utente/modificaNomeUtente")
                        .param("nuovoNome", "Clara")
                        .session(creaSessioneLoggata(admin))
                        .content(dtoJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content()
                        .string(containsString("Nome utente modificato")))
                .andDo(print())
                .andReturn();
    }

    @Test
    void modificaNomeUtente_credenzialiErrate() throws Exception {
        Utente admin = utenti.getFirst();
        UtenteDTO dto = dtoutente.get(1);
        when(service.modificaNomeUtente(eq("Clara"), any(UtenteDTO.class))).
                thenThrow(new CredenzialiErrateException("Credenziali errate"));

        String dtoJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(patch("/utente/modificaNomeUtente")
                        .param("nuovoNome", "Clara")
                        .session(creaSessioneLoggata(admin))
                        .content(dtoJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andExpect(content()
                        .string("Credenziali errate"))
                .andDo(print())
                .andReturn();
    }

    @Test
    void modificaNomeUtente_nomeUtenteNonDisponibile() throws Exception {
        Utente admin = utenti.getFirst();
        UtenteDTO dto = dtoutente.getFirst();

        when(service.modificaNomeUtente(eq("Clara"), any(UtenteDTO.class))).
                thenThrow(new NomeUtenteOccupatoException("Nome utente non disponibile"));

        String dtoJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(patch("/utente/modificaNomeUtente")
                        .param("nuovoNome", "Clara")
                        .session(creaSessioneLoggata(admin))
                        .content(dtoJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict()).andExpect(content()
                        .string("Nome utente non disponibile"))
                .andDo(print())
                .andReturn();
    }

    @Test
    void modificaEmail_ok() throws Exception {
        Utente admin = utenti.getFirst();
        UtenteDTO dto = dtoutente.getFirst();

        when(service.modificaEmail("nuova@mail.com", dto))
                .thenReturn("Email modificata con successo: " + admin.getEmail());

        String dtoJson = objectMapper.writeValueAsString(dto);
        mockMvc.perform(patch("/utente/modificaEmail")
                        .param("nuovaE", "nuova@mail.com")
                        .session(creaSessioneLoggata(admin))
                        .content(dtoJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Email modificata"))
                .andDo(print())
                .andReturn();
    }

    @Test
    void modificaEmail_utenteNonTrovato() throws Exception {
        Utente admin = utenti.getFirst();
        UtenteDTO dto = dtoutente.getFirst();

        when(service.modificaEmail(eq("nuova@mail.com"), any(UtenteDTO.class)))
                .thenThrow(new UtenteNonTrovatoException("Utente non trovato"));

        String dtoJson = objectMapper.writeValueAsString(dto);
        mockMvc.perform(patch("/utente/modificaEmail")
                        .param("nuovaE", "nuova@mail.com")
                        .session(creaSessioneLoggata(admin))
                        .content(dtoJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Utente non trovato"))
                .andDo(print())
                .andReturn();
    }

    @Test
    void modificaEmail_emailNonDisponibile() throws Exception {
        Utente admin = utenti.getFirst();
        UtenteDTO dto = dtoutente.getFirst();

        when(service.modificaEmail(eq("nonDisp@mail.com"), any(UtenteDTO.class)))
                .thenThrow(new EmailGiaEsistenteException("Email non disponibile"));

        String dtoJson = objectMapper.writeValueAsString(dto);
        mockMvc.perform(patch("/utente/modificaEmail")
                        .param("nuovaE", "nonDisp@mail.com")
                        .session(creaSessioneLoggata(admin))
                        .content(dtoJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string("Email non disponibile"))
                .andDo(print())
                .andReturn();
    }

    @Test
    void modificaPassword_ok() throws Exception {
        Utente admin = utenti.getFirst();
        UtenteDTO dto = dtoutente.getFirst();

        when(service.modificaPassword( "newpass", dto))
                .thenReturn("Password modificata con successo: " + admin.getPassword());

        String dtoJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(patch("/utente/modificaPassword")
                        .param("nuovap", "newpass")
                        .session(creaSessioneLoggata(admin))
                        .content(dtoJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Password modificata"))
                .andDo(print())
                .andReturn();
    }

    @Test
    void modificaPassword_utenteNonTrovato() throws Exception {
        Utente admin = utenti.getFirst();
        UtenteDTO dto = dtoutente.getFirst();
        String dtoJson = objectMapper.writeValueAsString(dto);
        when(service.modificaPassword( eq("newpass"), any(UtenteDTO.class)))
                .thenThrow(new UtenteNonTrovatoException("Utente non trovato"));

        mockMvc.perform(patch("/utente/modificaPassword")
                        .param("nuovap", "newpass")
                        .session(creaSessioneLoggata(admin))
                        .content(dtoJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Utente non trovato"))
                .andDo(print())
                .andReturn();


    }

    @Test
    void modificaPassword_credenzialiErrate() throws Exception {
        Utente admin = utenti.getFirst();
        UtenteDTO dto = dtoutente.getFirst();
        String dtoJson = objectMapper.writeValueAsString(dto);

        when(service.modificaPassword( eq("newpass"), any(UtenteDTO.class)))
                .thenThrow(new CredenzialiErrateException("Credenziali errate"));

        mockMvc.perform(patch("/utente/modificaPassword")
                        .param("nuovap", "newpass")
                        .session(creaSessioneLoggata(admin))
                        .content(dtoJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Credenziali errate"))
                .andDo(print())
                .andReturn();
    }

    @Test
    void modificaImmagineProfilo_ok() throws Exception {
        Utente admin = utenti.getFirst();

        when(service.modificaImmagine(admin.getId(), immagineP)).thenReturn(immagineP.getBytes());

        mockMvc.perform(multipart("/utente/modificaIP/" + admin.getId())
                        .file(immagineP)
                        .with(request -> {request.setMethod("PATCH");
                            return request;
                        })
                        .session(creaSessioneLoggata(admin)))
                .andExpect(status().isOk())
                .andExpect(content().string("Immagine del profilo modificata"))
                .andDo(print())
                .andReturn();
    }
    @Test
    void modificaImmaginProfilo_utenteNonTrovato() throws Exception {
        Utente admin = utenti.getFirst();
        when(service.modificaImmagine(eq(0L),any(MultipartFile.class)))
                .thenThrow(new UtenteNonTrovatoException("Utente non trovato"));

        mockMvc.perform(multipart("/utente/modificaIP/" + 0L)
                .file(immagineP)
                .with(request -> {request.setMethod("PATCH");
        return request;
                })
                .session(creaSessioneLoggata(admin)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Utente non trovato"))
                .andDo(print())
                .andReturn();
    }
}