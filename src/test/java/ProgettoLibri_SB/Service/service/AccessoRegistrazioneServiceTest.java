package ProgettoLibri_SB.Service.service;

import ProgettoLibri_SB.entity.Utente;
import ProgettoLibri_SB.exception.UtenteException.UtenteGiaRegistratoException;
import ProgettoLibri_SB.exception.UtenteException.UtenteNonTrovatoException;
import ProgettoLibri_SB.repository.UtenteRepository;
import ProgettoLibri_SB.service.AccessoRegistrazioneService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccessoRegistrazioneServiceTest {


    private ObjectMapper objectMapper;
    private UtenteRepository utenteRepository;
    private AccessoRegistrazioneService service;
    private MockHttpSession session;
    private List<Utente> utenti;

    @BeforeEach
    void setUp() throws Exception {
        utenteRepository = mock(UtenteRepository.class);
        service = new AccessoRegistrazioneService(utenteRepository);
        session = new MockHttpSession();
        objectMapper = new ObjectMapper();
        utenti = objectMapper.readValue(
                new File("src/test/java/features/Utente/UtenteEntity.json"),
                new TypeReference<List<Utente>>() {
                }

        );
    }

    @AfterEach
    void tearDown() {
        System.out.println("Fine dei test");
    }

    @Test
    void registrazioneAdmin_ok() {
        Utente admin = utenti.getFirst();
        when(utenteRepository.findByEmail(admin.getEmail()))
                .thenReturn(Optional.empty());
        when(utenteRepository.saveAndFlush(any(Utente.class)))
                .thenAnswer(invocation -> {
                    Utente u = invocation.getArgument(0);
                    u.setId(1L);
                    return u;
                });

        String result = service.registrazioneAdmin(admin);
        assertEquals("Registrazione Amministratore effettuata con successo: "  + admin, result);

        verify(utenteRepository, times(1)).findByEmail(admin.getEmail());
        verify(utenteRepository, times(1)).saveAndFlush(admin);
    }
    @Test
    void registrazioneAdmin_utenteGiaRegistrato() {
        Utente admin = utenti.getFirst();
        when(utenteRepository.findByEmail(admin.getEmail()))
                .thenReturn(Optional.of(admin));
        UtenteGiaRegistratoException e =
                assertThrows(UtenteGiaRegistratoException.class,()->service.registrazioneAdmin(admin));
        assertEquals("Utente già registrato", e.getMessage());

        verify(utenteRepository,times(1)).findByEmail(admin.getEmail());
        verify(utenteRepository, never()).saveAndFlush(null);
    }

    @Test
    void registrazioneLettore_ok() {
        Utente lettore = utenti.get(1);
        when(utenteRepository.findByEmail(lettore.getEmail()))
                .thenReturn(Optional.empty());
        when(utenteRepository.saveAndFlush(any(Utente.class)))
                .thenAnswer(invocation ->
                {
                    Utente u = invocation.getArgument(0);
                    u.setId(1L);
                    return u;
                });

        String result = service.registrazioneLettore(lettore);
        assertEquals("Registrazione Lettore effettuata con successo: "  + lettore, result);

        verify(utenteRepository,times(1)).findByEmail(lettore.getEmail());
        verify(utenteRepository,times(1)).saveAndFlush(lettore);
    }
    @Test
    void registrazioneLettore_utenteGiaRegistrato() {
        Utente lettore = utenti.get(1);
        when(utenteRepository.findByEmail(lettore.getEmail()))
                .thenReturn(Optional.of(lettore));
        UtenteGiaRegistratoException e =
                assertThrows(UtenteGiaRegistratoException.class,()->service.registrazioneLettore(lettore));
        assertEquals("Utente già registrato", e.getMessage());
        verify(utenteRepository,times(1)).findByEmail(lettore.getEmail());
        verify(utenteRepository,never()).saveAndFlush(any(Utente.class));
    }

    @Test
    void accesso_ok() {
        Utente admin = utenti.getFirst();
        when(utenteRepository.findByEmailAndPassword(admin.getEmail(), admin.getPassword()))
                .thenReturn(Optional.of(admin));

        String result = service.accesso(admin.getEmail(),admin.getPassword(),session);

        assertEquals("Benvenuto " + admin.getNomeUtente() + "!", result);
        verify(utenteRepository,times(1)).findByEmailAndPassword(admin.getEmail(), admin.getPassword());
    }
    @Test
    void accesso_utenteNonTrovato(){
        Utente lettore = utenti.get(1);
        when(utenteRepository.findByEmailAndPassword(lettore.getEmail(),lettore.getPassword()))
                .thenReturn(Optional.empty());
        UtenteNonTrovatoException e =
                assertThrows(UtenteNonTrovatoException.class,()->service.accesso(lettore.getEmail(), lettore.getPassword(), session));
        assertEquals("Utente non trovato",e.getMessage());

        verify(utenteRepository,times(1)).findByEmailAndPassword(lettore.getEmail(),lettore.getPassword());
    }

    @Test
    void esci_ok() {
        assertFalse(session.isInvalid());
        String result = service.esci(session);
        assertTrue(session.isInvalid());
        assertEquals("Logout effettuato", result);
    }
}