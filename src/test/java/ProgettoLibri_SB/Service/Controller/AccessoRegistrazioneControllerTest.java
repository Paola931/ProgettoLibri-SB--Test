package ProgettoLibri_SB.Service.Controller;

import ProgettoLibri_SB.Entity.Utente;
import ProgettoLibri_SB.Exception.UtenteException.UtenteGiaRegistratoException;
import ProgettoLibri_SB.Exception.UtenteException.UtenteNonTrovatoException;
import ProgettoLibri_SB.Service.AccessoRegistrazioneService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(locations = "classpath:application-test.yml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AccessoRegistrazioneControllerTest {
    @MockitoBean
    private AccessoRegistrazioneService service;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private List<Utente> utenti;


    @BeforeEach
    void setUp() throws Exception {
        utenti = objectMapper.readValue(
                new File("src/test/java/features/Utente/UtenteEntity.json"),
                new TypeReference<List<Utente>>() {
                }

        );
    }

    private MockHttpSession creaSessioneLoggata(Utente utente) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("utentecorrente", utente);
        return session;
    }

    @Test
    void registrazioneAdmin_ok() throws Exception {
        Utente admin = utenti.get(0);
        String adminJson = objectMapper.writeValueAsString(admin);
        when(service.registrazioneAdmin(admin))
                .thenReturn("Registrazione Amministratore effettuata con successo: " + admin);

        mockMvc.perform(post("/account/registraAdmin")
                        .content(adminJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string("Admin creato"))
                .andDo(print())
                .andReturn();
    }

    @Test
    void registrazioneAdmin_utenteGiaRegistrato() throws Exception {
        Utente admin = utenti.get(0);
        String adminJson = objectMapper.writeValueAsString(admin);
        when(service.registrazioneAdmin(admin))
                .thenThrow(new UtenteGiaRegistratoException("Utente già registrato"));
        mockMvc.perform(post("/account/registraAdmin")
                        .content(adminJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string("Utente già registrato"))
                .andDo(print())
                .andReturn();
    }

    @Test
    void registrazioneLettore_ok() throws Exception {
        Utente lettore = utenti.get(1);
        String lettJson = objectMapper.writeValueAsString(lettore);
        when(service.registrazioneLettore(lettore))
                .thenReturn("Registrazione Lettore effettuata con successo: " + lettore);
        mockMvc.perform(post("/account/registraLettore")
                        .content(lettJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string("Lettore creato"))
                .andDo(print())
                .andReturn();
    }

    @Test
    void registrazioneLettore_utenteGiaRegistrato() throws Exception {
        Utente lettore = utenti.get(1);
        String lettJson = objectMapper.writeValueAsString(lettore);
        when(service.registrazioneLettore(lettore))
                .thenThrow(new UtenteGiaRegistratoException("Utente già registrato"));
        mockMvc.perform(post("/account/registraLettore")
                        .content(lettJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string("Utente già registrato"))
                .andDo(print())
                .andReturn();
    }

    @Test
    void accesso_ok() throws Exception {
        Utente admin = utenti.getFirst();
        when(service.accesso(eq(admin.getEmail()), eq(admin.getPassword()), any(HttpSession.class)))
                .thenReturn("Benvenuto " + admin.getNomeUtente() + "!");

        mockMvc.perform(post("/account/accesso")
                        .param("email", admin.getEmail())
                        .param("password", admin.getPassword())
                        .session(creaSessioneLoggata(admin)))
                .andExpect(status().isOk())
                .andExpect(content().string("Accesso effettuato"))
                .andDo(print())
                .andReturn();
    }

    @Test
    void accesso_utenteNonTrovato() throws Exception {
        when(service.accesso(anyString(), anyString(), any(HttpSession.class)))
                .thenThrow(new UtenteNonTrovatoException("Utente non trovato"));

        mockMvc.perform(post("/account/accesso")
                        .param("email", "email")
                        .param("password", "password"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Utente non trovato"))
                .andDo(print())
                .andReturn();
    }

    @Test
    void esci() throws Exception {
        Utente admin = utenti.getFirst();
        when(service.esci(any(HttpSession.class)))
                .thenReturn("Sessione chiusa");
        mockMvc.perform(post("/account/esci")
                        .session(creaSessioneLoggata(admin)))
                .andExpect(status().isOk())
                .andExpect(content().string("Sessione chiusa"))
                .andDo(print())
                .andReturn();
    }
}