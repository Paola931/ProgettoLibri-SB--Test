package ProgettoLibri_SB.Service.Service;

import ProgettoLibri_SB.DTO.LibroDTO;
import ProgettoLibri_SB.Entity.Libro;
import ProgettoLibri_SB.Entity.TipoUtenza;
import ProgettoLibri_SB.Entity.Utente;
import ProgettoLibri_SB.Exception.LibriException.LibroGiaPresenteInListaException;
import ProgettoLibri_SB.Exception.LibriException.LibroNonTrovatoException;
import ProgettoLibri_SB.Exception.UtenteException.UtenteNonAutorizzatoException;
import ProgettoLibri_SB.Exception.UtenteException.UtenteNonTrovatoException;
import ProgettoLibri_SB.Repository.LibroRepository;
import ProgettoLibri_SB.Repository.UtenteRepository;
import ProgettoLibri_SB.Service.LibroService;
import ProgettoLibri_SB.Service.UploadDownloadService;
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
import static org.mockito.Mockito.*;

class LibroServiceTest {

    private LibroService service;
    private LibroRepository repository;
    private UtenteRepository utenteRepository;
    private UploadDownloadService fileService;
    private ObjectMapper objectMapper;
    private MockMultipartFile copertina;
    private List<Utente> utenti;
    private List<Libro> libroEntity;


    @BeforeEach
    void setUp() throws Exception {
        repository = mock(LibroRepository.class);
        utenteRepository = mock(UtenteRepository.class);
        fileService = mock(UploadDownloadService.class);
        service = new LibroService(repository, utenteRepository, fileService);
        objectMapper = new ObjectMapper();
        copertina = new MockMultipartFile(
                "Copertina",              // nome parametro
                "Odissea.jpeg",                // nome file
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

    @AfterEach
    void tearDown() {
        System.out.println("Fine dei test");
    }


    @Test
    void cercaLibro_okNoCopertina() {
        Libro libro = libroEntity.getFirst();
        when(repository.findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore()))
                .thenReturn(libro);
        when(repository.findLibroByTitoloAndAutoreAndCopertinaIsNotNull(libro.getTitolo(), libro.getAutore()))
                .thenReturn(false);
        String result = service.cercaLibro(libro.getTitolo(), libro.getAutore());
        assertEquals("Il libro che cercavi è: " + libro, result);

        verify(repository, times(1)).findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());
        verify(repository, times(1)).findLibroByTitoloAndAutoreAndCopertinaIsNotNull(libro.getTitolo(), libro.getAutore());
        verify(fileService, never()).getImmagineDaPath("Odissea.jpg");

    }

    @Test
    void cercaLibro_okCopertina() {
        Libro libro = libroEntity.getFirst();
        libro.setCopertina("abc.jpg");
        when(repository.findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore()))
                .thenReturn(libro);
        when(repository.findLibroByTitoloAndAutoreAndCopertinaIsNotNull(libro.getTitolo(), libro.getAutore()))
                .thenReturn(true);
        byte[] fakeCopertina = "Copertina".getBytes();
        when(fileService.getImmagineDaPath("abc.jpg")).thenReturn(fakeCopertina);

        String result = service.cercaLibro(libro.getTitolo(), libro.getAutore());
        assertTrue(result.contains("Copertina libro: "), "Indica che ha trovato la copertina");
        assertTrue(result.contains("Copertina"), "Include la copertina fittizia");

        verify(repository, times(1)).findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());
        verify(repository, times(1)).findLibroByTitoloAndAutoreAndCopertinaIsNotNull(libro.getTitolo(), libro.getAutore());
        verify(fileService, times(1)).getImmagineDaPath("abc.jpg");
    }

    @Test
    void cercaLibro_libroNonTrovato() {
        Libro libro = libroEntity.getFirst();
        when(repository.findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore()))
                .thenReturn(null);

        LibroNonTrovatoException e =
                assertThrows(LibroNonTrovatoException.class, () -> service.cercaLibro(libro.getTitolo(), libro.getAutore()));
        assertEquals("Libro non trovato", e.getMessage());

        verify(repository, times(1)).findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());
        verify(repository, never()).findLibroByTitoloAndAutoreAndCopertinaIsNotNull(libro.getTitolo(), libro.getAutore());
        verify(fileService, never()).getImmagineDaPath("Odissea.jpg");
    }

    @Test
    void aggiungiLibro_ok() {
        Utente admin = utenti.getFirst();
        Libro libro = libroEntity.get(1);
        when(utenteRepository.findById(admin.getId()))
                .thenReturn(Optional.of(admin));
        when(utenteRepository.findTipoUtenzaById(admin.getId()))
                .thenReturn(TipoUtenza.AMMINISTRATORE);
        when(repository.findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore()))
                .thenReturn(null);
        when(fileService.upload(copertina))
                .thenReturn("Odissea.jpg");
        when(repository.saveAndFlush(any(Libro.class)))
                .thenAnswer(invocation ->
                {
                    Libro l = invocation.getArgument(0);
                    l.setIdLibro(1L);
                    return l;
                });

        Libro result = service.aggiungiLibro(admin.getId(), libro.getTitolo(), libro.getAutore(), libro.getCe(), copertina);
        assertEquals(libro.getTitolo(), result.getTitolo());
        assertEquals(libro.getAutore(), result.getAutore());
        assertEquals(libro.getIdLibro(), result.getIdLibro());

        verify(utenteRepository, times(1)).findById(admin.getId());
        verify(utenteRepository, times(1)).findTipoUtenzaById(admin.getId());
        verify(repository, times(1)).findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());
        verify(fileService, times(1)).upload(copertina);
        verify(repository, times(1)).saveAndFlush(any(Libro.class));
    }

    @Test
    void aggiungiLibro_libroGiaPresente() {
        Utente admin = utenti.getFirst();
        Libro libro = libroEntity.getFirst();
        when(utenteRepository.findById(admin.getId()))
                .thenReturn(Optional.of(admin));
        when(utenteRepository.findTipoUtenzaById(admin.getId()))
                .thenReturn(TipoUtenza.AMMINISTRATORE);
        when(repository.findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore()))
                .thenReturn(libro);
        LibroGiaPresenteInListaException e =
                assertThrows(LibroGiaPresenteInListaException.class, () -> service.aggiungiLibro(admin.getId(), libro.getTitolo(), libro.getAutore(), libro.getCe(), copertina));
        assertEquals("Libro già presente", e.getMessage());
        verify(utenteRepository, times(1)).findById(admin.getId());
        verify(utenteRepository, times(1)).findTipoUtenzaById(admin.getId());
        verify(repository, times(1)).findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());
        verify(fileService, never()).upload(copertina);
        verify(repository, never()).saveAndFlush(any(Libro.class));
    }

    @Test
    void aggiungiLibro_utenteNonAutorizzato() {
        Utente lettore = utenti.get(1);
        Libro libro = libroEntity.getFirst();
        when(utenteRepository.findById(lettore.getId()))
                .thenReturn(Optional.of(lettore));
        when(utenteRepository.findTipoUtenzaById(lettore.getId()))
                .thenReturn(TipoUtenza.LETTORE);
        UtenteNonAutorizzatoException e =
                assertThrows(UtenteNonAutorizzatoException.class, () -> service.aggiungiLibro(lettore.getId(), libro.getTitolo(), libro.getAutore(), libro.getCe(), copertina));
        assertEquals("Utente non autorizzato", e.getMessage());
        verify(utenteRepository, times(1)).findById(lettore.getId());
        verify(utenteRepository, times(1)).findTipoUtenzaById(lettore.getId());
        verify(repository, never()).findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());
        verify(fileService, never()).upload(copertina);
        verify(repository, never()).saveAndFlush(any(Libro.class));
    }

    @Test
    void aggiungiLibro_utenteNonTrovato() {
        Libro libro = libroEntity.getFirst();
        when(utenteRepository.findById(0L))
                .thenReturn(Optional.empty());
        UtenteNonTrovatoException e =
                assertThrows(UtenteNonTrovatoException.class, () -> service.aggiungiLibro(0L, libro.getTitolo(), libro.getAutore(), libro.getCe(), copertina));
        assertEquals("Utente non trovato", e.getMessage());
        verify(utenteRepository, times(1)).findById(0L);
        verify(utenteRepository, never()).findTipoUtenzaById(0L);
        verify(repository, never()).findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());
        verify(fileService, never()).upload(copertina);
        verify(repository, never()).saveAndFlush(any(Libro.class));
    }

    @Test
    void modificaLibro_ok() {
        Utente admin = utenti.getFirst();
        Libro libro = libroEntity.get(2);
        when(utenteRepository.findById(admin.getId()))
                .thenReturn(Optional.of(admin));
        when(utenteRepository.findTipoUtenzaById(admin.getId()))
                .thenReturn(TipoUtenza.AMMINISTRATORE);
        when(repository.findById(libro.getIdLibro()))
                .thenReturn(Optional.of(libro));
        when(fileService.upload(copertina))
                .thenReturn("Odissea.jpg");
        when(repository.saveAndFlush(any(Libro.class)))
                .thenAnswer(invocation ->
                {
                    Libro l = invocation.getArgument(0);
                    l.setIdLibro(1L);
                    return l;
                });
        String titolo = "titolo", autore = "autore";
        Libro result = service.modificaLibro(admin.getId(), libro.getIdLibro(), titolo, autore, copertina);
        assertEquals(libro, result);
        verify(utenteRepository, times(1)).findById(admin.getId());
        verify(utenteRepository, times(1)).findTipoUtenzaById(admin.getId());
        verify(repository, times(1)).findById(libro.getIdLibro());
        verify(fileService, times(1)).upload(copertina);
        verify(repository, times(1)).saveAndFlush(any(Libro.class));
    }

    @Test
    void modificaLibro_utenteNonTrovato() {
        Libro libro = libroEntity.getFirst();
        when(utenteRepository.findById(0L))
                .thenReturn(Optional.empty());
        UtenteNonTrovatoException e =
                assertThrows(UtenteNonTrovatoException.class, () -> service.modificaLibro(0L, libro.getIdLibro(), libro.getTitolo(), libro.getAutore(), null));
        assertEquals("Utente non trovato", e.getMessage());
        verify(utenteRepository, times(1)).findById(0L);
        verify(utenteRepository, never()).findTipoUtenzaById(0L);
        verify(repository, never()).findById(0L);
        verify(fileService, never()).upload(copertina);
        verify(repository, never()).saveAndFlush(any(Libro.class));
    }

    @Test
    void modificaLibro_utenteNonAutorizzato() {
        Libro libro = libroEntity.get(2);
        Utente lettore = utenti.get(1);
        when(utenteRepository.findById(lettore.getId()))
                .thenReturn(Optional.of(lettore));
        when(utenteRepository.findTipoUtenzaById(lettore.getId()))
                .thenReturn(TipoUtenza.LETTORE);
        String titolo = "titolo";
        String autore = "autore";
        UtenteNonAutorizzatoException e =
                assertThrows(UtenteNonAutorizzatoException.class, () -> service.modificaLibro(lettore.getId(), libro.getIdLibro(), titolo, autore, copertina));
        assertEquals("Utente non autorizzato", e.getMessage());
        verify(utenteRepository, times(1)).findById(lettore.getId());
        verify(utenteRepository, times(1)).findTipoUtenzaById(lettore.getId());
        verify(repository, never()).findById(lettore.getId());
        verify(fileService, never()).upload(copertina);
        verify(repository, never()).saveAndFlush(any(Libro.class));
    }

    @Test
    void modificaLibro_libroNonTrovato() {
        Utente admin = utenti.getFirst();
        Libro libro = libroEntity.getFirst();
        when(utenteRepository.findById(admin.getId()))
                .thenReturn(Optional.of(admin));
        when(utenteRepository.findTipoUtenzaById(admin.getId()))
                .thenReturn(TipoUtenza.AMMINISTRATORE);
        when(repository.findById(0L))
                .thenReturn(Optional.empty());
        LibroNonTrovatoException e =
                assertThrows(LibroNonTrovatoException.class, () -> service.modificaLibro(admin.getId(), 0L, libro.getTitolo(), libro.getAutore(), null));
        assertEquals("Libro non trovato", e.getMessage());
        verify(utenteRepository, times(1)).findById(admin.getId());
        verify(utenteRepository, times(1)).findTipoUtenzaById(admin.getId());
        verify(repository, times(1)).findById(0L);
        verify(fileService, never()).upload(copertina);
        verify(repository, never()).saveAndFlush(any(Libro.class));
    }

    @Test
    void eliminaLibro_ok() {
        Utente admin = utenti.getFirst();
        Libro libro = libroEntity.getFirst();
        when(utenteRepository.findById(admin.getId()))
                .thenReturn(Optional.of(admin));
        when(utenteRepository.findTipoUtenzaById(admin.getId()))
                .thenReturn(TipoUtenza.AMMINISTRATORE);
        when(repository.findIdLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore()))
                .thenReturn(libro.getIdLibro());

        LibroDTO expected = new LibroDTO(
                libro.getIdLibro(),
                libro.getTitolo(),
                libro.getAutore()
        );
        String result = service.eliminaLibro(libro.getIdLibro(), admin.getId(), libro.getTitolo(), libro.getAutore());
        assertEquals("Libro eliminato: " + expected, result);

        verify(utenteRepository, times(1)).findById(admin.getId());
        verify(utenteRepository, times(1)).findTipoUtenzaById(admin.getId());
        verify(repository, times(1)).findIdLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());
        verify(repository, times(1)).deleteById(libro.getIdLibro());
    }

    @Test
    void eliminaLibro_utenteNonTrovato() {
        Libro libro = libroEntity.getFirst();
        when(utenteRepository.findById(0L))
                .thenReturn(Optional.empty());

        UtenteNonTrovatoException e =
                assertThrows(UtenteNonTrovatoException.class, () -> service.eliminaLibro(libro.getIdLibro(), 0L, libro.getTitolo(), libro.getAutore()));
        assertEquals("Utente non trovato", e.getMessage());
        verify(utenteRepository, times(1)).findById(0L);
        verify(utenteRepository, never()).findTipoUtenzaById(0L);
        verify(repository, never()).findIdLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());
        verify(repository, never()).deleteById(libro.getIdLibro());
    }

    @Test
    void eliminaLibro_utenteNonAutorizzato() {
        Utente lettore = utenti.get(1);
        Libro libro = libroEntity.getFirst();
        when(utenteRepository.findById(lettore.getId()))
                .thenReturn(Optional.of(lettore));
        when(utenteRepository.findTipoUtenzaById(lettore.getId()))
                .thenReturn(TipoUtenza.LETTORE);

        UtenteNonAutorizzatoException e =
                assertThrows(UtenteNonAutorizzatoException.class, () -> service.eliminaLibro(libro.getIdLibro(), lettore.getId(), libro.getTitolo(), libro.getAutore()));
        assertEquals("Utente non autorizzato", e.getMessage());
        verify(utenteRepository, times(1)).findById(lettore.getId());
        verify(utenteRepository, times(1)).findTipoUtenzaById(lettore.getId());
        verify(repository, never()).findIdLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());
        verify(repository, never()).deleteById(libro.getIdLibro());
    }

    @Test
    void eliminaLibro_libroNonTrovato() {
        Utente admin = utenti.getFirst();
        Libro libro = libroEntity.getFirst();
        when(utenteRepository.findById(admin.getId()))
                .thenReturn(Optional.of(admin));
        when(utenteRepository.findTipoUtenzaById(admin.getId()))
                .thenReturn(TipoUtenza.AMMINISTRATORE);
        when(repository.findIdLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore()))
                .thenReturn(null);

        LibroNonTrovatoException e =
                assertThrows(LibroNonTrovatoException.class, () -> service.eliminaLibro(libro.getIdLibro(), admin.getId(), libro.getTitolo(), libro.getAutore()));
        assertEquals("Libro non trovato", e.getMessage());
        verify(utenteRepository, times(1)).findById(admin.getId());
        verify(utenteRepository, times(1)).findTipoUtenzaById(admin.getId());
        verify(repository, times(1)).findIdLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());
        verify(repository, never()).deleteById(libro.getIdLibro());
    }
}
