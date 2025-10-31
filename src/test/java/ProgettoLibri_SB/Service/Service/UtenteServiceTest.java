package ProgettoLibri_SB.Service.Service;

import ProgettoLibri_SB.DTO.UtenteDTO;
import ProgettoLibri_SB.Entity.Utente;
import ProgettoLibri_SB.Exception.UtenteException.*;
import ProgettoLibri_SB.Repository.UtenteRepository;
import ProgettoLibri_SB.Service.UploadDownloadService;
import ProgettoLibri_SB.Service.UtenteService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class UtenteServiceTest {

    private UtenteRepository repository;
    private UploadDownloadService fileService;
    private UtenteService service;
    private ObjectMapper objectMapper;
    private MockMultipartFile imgProfilo;
    private List<Utente> utenti;
    private List<UtenteDTO> dtoutente;

    @BeforeEach
    void setUp() throws Exception {
        repository = mock(UtenteRepository.class);
        fileService = mock(UploadDownloadService.class);
        service = new UtenteService(repository,fileService);
        objectMapper = new ObjectMapper();
        imgProfilo = new MockMultipartFile(
                "immagine_profilo",      // nome parametro
                "fotoprofilo.jpg",             // nome file
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

    @AfterEach
    void tearDown() {
        System.out.println("Fine dei test");
    }


    @Test
    void modificaNomeUtente_Ok() {
        Utente admin = utenti.getFirst();
        String nuovoNome = "Rebecca";
       when(repository.findByEmailAndPassword(admin.getEmail(), admin.getPassword()))
               .thenReturn(Optional.of(admin));
       when(repository.findByNomeUtente(nuovoNome))
               .thenReturn(Optional.empty());
       when(repository.saveAndFlush(any(Utente.class)))
               .thenAnswer(invocation -> {
                   Utente u = invocation.getArgument(0);
                   u.setNomeUtente(nuovoNome);
                   return u;
               });
        service.modificaNomeUtente(nuovoNome, dtoutente.getFirst());
        assertEquals(nuovoNome, admin.getNomeUtente());

        verify(repository,times(1)).findByEmailAndPassword(admin.getEmail(), admin.getPassword());
        verify(repository,times(1)).findByNomeUtente(nuovoNome);
        verify(repository,times(1)).saveAndFlush(any(Utente.class));
    }

    @Test
    void modificaNomeUtente_nomeUtenteNonDisponibile() {
        Utente admin = utenti.getFirst();
        String nuovoNome = "Tonia";
        when(repository.findByEmailAndPassword(admin.getEmail(), admin.getPassword()))
                .thenReturn(Optional.of(admin));
        when(repository.findByNomeUtente(nuovoNome))
                .thenReturn(Optional.of(admin));

        NomeUtenteOccupatoException e =
                assertThrows(NomeUtenteOccupatoException.class, () -> service.modificaNomeUtente(nuovoNome, dtoutente.getFirst()));
        assertEquals("Nome utente non disponibile", e.getMessage());
        verify(repository,times(1)).findByEmailAndPassword(admin.getEmail(), admin.getPassword());
        verify(repository,times(1)).findByNomeUtente(nuovoNome);
        verify(repository,never()).saveAndFlush(any(Utente.class));
    }

    @Test
    void modificaNomeUtente_credenzialiErrate() {
        String nuovoNome = "Rebecca";
        UtenteDTO dto = dtoutente.get(1);
        when(repository.findByEmailAndPassword(dto.getEmail(), dto.getPassword()))
                .thenReturn(Optional.empty());
        CredenzialiErrateException e =
                assertThrows(CredenzialiErrateException.class, () -> service.modificaNomeUtente(nuovoNome, dto));
        assertEquals("Credenziali errate", e.getMessage());
        verify(repository,times(1)).findByEmailAndPassword(dto.getEmail(), dto.getPassword());
        verify(repository,never()).findByNomeUtente(nuovoNome);
        verify(repository,never()).saveAndFlush(any(Utente.class));
    }

    @Test
    void modificaEmail_Ok() {
        Utente admin = utenti.getFirst();
        String nuovaEmail = "cla@mail.com";
        String vecchiaEmail= admin.getEmail();
        when(repository.findByEmailAndPassword(vecchiaEmail, admin.getPassword()))
                .thenReturn(Optional.of(admin));
        when(repository.findByEmail(nuovaEmail))
                .thenReturn(Optional.empty());
        when(repository.saveAndFlush(any(Utente.class)))
                .thenAnswer(invocation->{
                    Utente u = invocation.getArgument(0);
                    u.setEmail(nuovaEmail);
                    return u;
                });

        service.modificaEmail(nuovaEmail, dtoutente.getFirst());
        assertEquals(nuovaEmail, admin.getEmail());

        verify(repository,times(1)).findByEmailAndPassword(vecchiaEmail, admin.getPassword());
        verify(repository,times(1)).findByEmail(nuovaEmail);
        verify(repository,times(1)).saveAndFlush(admin);

    }

    @Test
    void modificaEmail_emailGiaEsistente() {
        Utente admin = utenti.getFirst();
        Utente lettore = utenti.get(1);
        String nuovaEmail = "tonia@mail.com";
        when(repository.findByEmailAndPassword(admin.getEmail(), admin.getPassword()))
                .thenReturn(Optional.of(admin));
        when(repository.findByEmail(nuovaEmail))
                .thenReturn(Optional.of(lettore));

        EmailGiaEsistenteException e =
                assertThrows(EmailGiaEsistenteException.class, () -> service.modificaEmail(nuovaEmail, dtoutente.getFirst()));
        assertEquals("Email non disponibile", e.getMessage());
        verify(repository,times(1)).findByEmailAndPassword(admin.getEmail(), admin.getPassword());
        verify(repository,times(1)).findByEmail(nuovaEmail);
        verify(repository,never()).saveAndFlush(any(Utente.class));

    }

    @Test
    void modificaPassword_ok() {
        Utente admin = utenti.getFirst();
        UtenteDTO dto = dtoutente.getFirst();
        String nuovaPass = "newpassword";
        String vecchiaPass = admin.getPassword();
        when(repository.findByEmailAndPassword(admin.getEmail(), vecchiaPass))
                .thenReturn(Optional.of(admin));
        when(repository.saveAndFlush(any(Utente.class)))
                .thenAnswer(invocation ->
                {Utente u = invocation.getArgument(0);
                u.setPassword(nuovaPass);
                return u;}
                );
        service.modificaPassword(nuovaPass,dto);
        assertEquals(nuovaPass, admin.getPassword());

        verify(repository,times(1)).findByEmailAndPassword(admin.getEmail(), vecchiaPass);
        verify(repository,times(1)).saveAndFlush(admin);
    }


    @Test
    void modificaPassword_credenzialiErrate() {
        UtenteDTO dto = dtoutente.get(1);
        String password = "newpassword";
        when((repository.findByEmailAndPassword(dto.getEmail(), dto.getPassword())))
                .thenReturn(Optional.empty());

        CredenzialiErrateException e =
                assertThrows(CredenzialiErrateException.class, () -> service.modificaPassword( password, dto));
        assertEquals("Credenziali errate", e.getMessage());
        verify(repository,times(1)).findByEmailAndPassword(dto.getEmail(),dto.getPassword());
        verify(repository,never()).saveAndFlush(any(Utente.class));
    }

    @Test
    void modificaImmagine_ok(){
        Utente lettore = utenti.get(1);
        when(repository.findById(lettore.getId()))
                .thenReturn(Optional.of(lettore));
        when(fileService.upload(imgProfilo))
                .thenReturn("fotoprofilo.jpg");
        when(repository.saveAndFlush(any(Utente.class)))
                .thenReturn(lettore);
        byte[] nuovaImgProfilo = "fotoprofilo.jpg".getBytes();
        when(fileService.getImmagineDaPath("fotoprofilo.jpg"))
                .thenReturn(nuovaImgProfilo);
        byte[] result = service.modificaImmagine(lettore.getId(), imgProfilo);
        assertEquals(nuovaImgProfilo,result);

        verify(repository,times(1)).findById(lettore.getId());
        verify(fileService,times(1)).upload(imgProfilo);
        verify(repository,times(1)).saveAndFlush(lettore);
        verify(fileService,times(1)).getImmagineDaPath("fotoprofilo.jpg");
    }

    @Test
    void modificaImmagine_utenteNonTrovato() {
        when(repository.findById(0L))
                .thenReturn(Optional.empty());
        UtenteNonTrovatoException e =
        assertThrows(UtenteNonTrovatoException.class,()-> service.modificaImmagine(0L,imgProfilo));
        assertEquals("Utente non trovato", e.getMessage());
        verify(repository,times(1)).findById(0L);
        verify(fileService,never()).upload(imgProfilo);
        verify(repository,never()).saveAndFlush(any(Utente.class));
        verify(fileService,never()).getImmagineDaPath("abc.jpg");
    }
}