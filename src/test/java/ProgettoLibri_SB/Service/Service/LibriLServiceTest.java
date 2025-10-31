package ProgettoLibri_SB.Service.Service;

import ProgettoLibri_SB.Entity.LibriLetti;
import ProgettoLibri_SB.Entity.Libro;
import ProgettoLibri_SB.Entity.TipoUtenza;
import ProgettoLibri_SB.Entity.Utente;
import ProgettoLibri_SB.Exception.LibriException.LibroGiaPresenteInListaException;
import ProgettoLibri_SB.Exception.LibriException.LibroNonTrovatoException;
import ProgettoLibri_SB.Exception.UtenteException.UtenteNonAutorizzatoException;
import ProgettoLibri_SB.Exception.UtenteException.UtenteNonTrovatoException;
import ProgettoLibri_SB.Repository.LibriLRepository;
import ProgettoLibri_SB.Repository.LibroRepository;
import ProgettoLibri_SB.Repository.UtenteRepository;
import ProgettoLibri_SB.Service.LibriLService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


class LibriLServiceTest {

    private ObjectMapper objectMapper;
    private LibriLRepository repository;
    private LibroRepository libroRepository;
    private UtenteRepository utenteRepository;
    private LibriLService service;
    private List<Utente> utenti;
    private List<Libro> libroEntity;
    private List<LibriLetti> libri;


    @BeforeEach
    void setUp() throws Exception {
        repository = mock(LibriLRepository.class);
        libroRepository = mock(LibroRepository.class);
        utenteRepository = mock(UtenteRepository.class);
        service = new LibriLService(repository,libroRepository,utenteRepository);
        objectMapper = new ObjectMapper();
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
        libri = objectMapper.readValue(
                new File("src/test/java/features/ListeLibri/ListaLibriL.json"),
                new TypeReference<List<LibriLetti>>() {

                }
        );
    }

    @AfterEach
    void tearDown() {
        System.out.println("Fine dei test");
    }

    @Test
    void aggiungiLibriLetti_ok() {
        Utente lettore = utenti.get(1);
        Libro libro = libroEntity.getFirst();
        when(utenteRepository.findById(lettore.getId()))
                .thenReturn(Optional.of(lettore));
        when(utenteRepository.findTipoUtenzaById(lettore.getId()))
                .thenReturn(TipoUtenza.LETTORE);
        when(libroRepository.findLibroByTitoloAndAutore(eq(libro.getTitolo()), eq(libro.getAutore())))
                .thenReturn(libro);
        when(libroRepository.findIdLibroByTitoloAndAutore(eq(libro.getTitolo()), eq(libro.getAutore())))
                .thenReturn(libro.getIdLibro());
        when(libroRepository.findAverageVotoByIdLibro(libro.getIdLibro()))
                .thenReturn(5.0);

        String result = service.aggiungiLibriLetti(lettore.getId(), libro.getTitolo(), libro.getAutore(), 5);

        assertEquals("Libro aggiunto alla lista: " + libro, result);

        verify(utenteRepository,times(1)).findById(lettore.getId());
        verify(utenteRepository,times(1)).findTipoUtenzaById(lettore.getId());
        verify(libroRepository,times(1)).findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());
        verify(libroRepository,times(1)).findIdLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());
        verify(libroRepository,times(1)).findAverageVotoByIdLibro(libro.getIdLibro());

    }

    @Test
    void aggiungiLibriLetti_utenteNonTrovato() {
        Libro libro = libroEntity.getFirst();
        when(utenteRepository.findById(0L))
                .thenReturn(Optional.empty());
        UtenteNonTrovatoException e =
                assertThrows(UtenteNonTrovatoException.class, () -> service.aggiungiLibriLetti(0L, libro.getTitolo(), libro.getAutore(), 5));
        assertEquals("Utente non trovato", e.getMessage());

        verify(utenteRepository,times(1)).findById(0L);
        verify(utenteRepository,never()).findTipoUtenzaById(0L);
        verify(libroRepository,never()).findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());
        verify(libroRepository,never()).findIdLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());
        verify(libroRepository,never()).findAverageVotoByIdLibro(libro.getIdLibro());
    }

    @Test
    void aggiungiLibriLetti_utenteNonAutorizzato() {
        Utente admin = utenti.getFirst();
        Libro libro = libroEntity.getFirst();
        when(utenteRepository.findById(admin.getId()))
                .thenReturn(Optional.of(admin));
       when(utenteRepository.findTipoUtenzaById(admin.getId()))
               .thenReturn(TipoUtenza.AMMINISTRATORE);

        UtenteNonAutorizzatoException e =
                assertThrows(UtenteNonAutorizzatoException.class, () -> service.aggiungiLibriLetti(admin.getId(),libro.getTitolo(), libro.getAutore(), 5));
        assertEquals("Utente non autorizzato", e.getMessage());

        verify(utenteRepository,times(1)).findById(admin.getId());
        verify(utenteRepository,times(1)).findTipoUtenzaById(admin.getId());
        verify(libroRepository,never()).findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());
        verify(libroRepository,never()).findIdLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());
        verify(libroRepository,never()).findAverageVotoByIdLibro(libro.getIdLibro());
    }

    @Test
    void aggiungiLibriLetti_libroNonTrovato() {
        Utente lettore = utenti.get(1);
        Libro libro = libroEntity.getFirst();
        when(utenteRepository.findById(lettore.getId()))
                .thenReturn(Optional.of(lettore));
        when(utenteRepository.findTipoUtenzaById(lettore.getId()))
                .thenReturn(TipoUtenza.LETTORE);
        when(libroRepository.findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore()))
                .thenReturn(null);

        LibroNonTrovatoException e =
                assertThrows(LibroNonTrovatoException.class, () -> service.aggiungiLibriLetti(lettore.getId(), libro.getTitolo(), libro.getAutore(), 5));
        assertEquals("Libro non trovato", e.getMessage());

        verify(utenteRepository,times(1)).findById(lettore.getId());
        verify(utenteRepository,times(1)).findTipoUtenzaById(lettore.getId());
        verify(libroRepository,times(1)).findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());
        verify(libroRepository,never()).findIdLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());
        verify(libroRepository,never()).findAverageVotoByIdLibro(0L);
    }

    @Test
    void aggiungiLibriLetti_libroGiaPresente() {
        Libro libro = libroEntity.getFirst();
        Utente lettore = utenti.get(1);
        when(utenteRepository.findById(lettore.getId()))
                .thenReturn(Optional.of(lettore));
        when(utenteRepository.findTipoUtenzaById(lettore.getId()))
                .thenReturn(TipoUtenza.LETTORE);
        when(libroRepository.findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore()))
                .thenReturn(libro);
        when(libroRepository.findIdLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore()))
                .thenReturn(libro.getIdLibro());
                when(repository.findAll())
                        .thenReturn(List.of(new LibriLetti(1L,5,lettore,libro)));

        LibroGiaPresenteInListaException e =
                assertThrows(LibroGiaPresenteInListaException.class, () -> service.aggiungiLibriLetti(lettore.getId(), libro.getTitolo(), libro.getAutore(), 5));
        assertEquals("Libro già presente nella lista Libri Letti", e.getMessage());


        verify(utenteRepository,times(1)).findTipoUtenzaById(lettore.getId());
        verify(libroRepository,times(1)).findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());
        verify(libroRepository,times(1)).findIdLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());
        verify(libroRepository,never()).findAverageVotoByIdLibro(libro.getIdLibro());
    }

    @Test
    void visualizzaLibriLetti_ok() {
        Utente lettore = utenti.get(1);
        when(utenteRepository.findById(lettore.getId()))
                .thenReturn(Optional.of(lettore));
        when(utenteRepository.findTipoUtenzaById(lettore.getId()))
                .thenReturn(TipoUtenza.LETTORE);

        when(repository.findByUtenteId(lettore.getId()))
                .thenReturn(libri);

        List<LibriLetti> result = service.visualizzaLibriLetti(lettore.getId());

        assertNotNull(result);
        assertEquals(libri, result);

        verify(utenteRepository,times(1)).findById(lettore.getId());
        verify(utenteRepository,times(1)).findTipoUtenzaById(lettore.getId());
        verify(repository,times(1)).findByUtenteId(lettore.getId());
    }

    @Test
    void visualizzaLibriLetti_utenteNonTrovato() {
        when(utenteRepository.findById(0L))
                .thenReturn(Optional.empty());
        UtenteNonTrovatoException e =
                assertThrows(UtenteNonTrovatoException.class, () -> service.visualizzaLibriLetti(0L));
        assertEquals("Utente non trovato", e.getMessage());
        verify(utenteRepository,times(1)).findById(0L);
        verify(utenteRepository,never()).findTipoUtenzaById(0L);
        verify(repository,never()).findByUtenteId(0L);
    }

    @Test
    void visualizzaLibriLetti_utenteNonAutorizzato() {
        Utente admin = utenti.getFirst();
       when(utenteRepository.findById(admin.getId()))
               .thenReturn(Optional.of(admin));
       when(utenteRepository.findTipoUtenzaById(admin.getId()))
               .thenReturn(TipoUtenza.AMMINISTRATORE);
        UtenteNonAutorizzatoException e =
                assertThrows(UtenteNonAutorizzatoException.class, () -> service.visualizzaLibriLetti(admin.getId()));
        assertEquals("Utente non autorizzato", e.getMessage());
        verify(utenteRepository,times(1)).findById(admin.getId());
        verify(utenteRepository,times(1)).findTipoUtenzaById(admin.getId());
        verify(repository,never()).findByUtenteId(admin.getId());
    }
}