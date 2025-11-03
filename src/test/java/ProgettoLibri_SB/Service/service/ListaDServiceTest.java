package ProgettoLibri_SB.Service.service;


import ProgettoLibri_SB.entity.Libro;
import ProgettoLibri_SB.entity.ListaDesideri;
import ProgettoLibri_SB.entity.TipoUtenza;
import ProgettoLibri_SB.entity.Utente;
import ProgettoLibri_SB.exception.LibriException.LibroGiaPresenteInListaException;
import ProgettoLibri_SB.exception.LibriException.LibroNonTrovatoException;
import ProgettoLibri_SB.exception.UtenteException.UtenteNonAutorizzatoException;
import ProgettoLibri_SB.exception.UtenteException.UtenteNonTrovatoException;
import ProgettoLibri_SB.repository.LibroRepository;
import ProgettoLibri_SB.repository.ListaDRepository;
import ProgettoLibri_SB.repository.UtenteRepository;
import ProgettoLibri_SB.service.ListaDService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class ListaDServiceTest {

    private UtenteRepository utenteRepository;
    private LibroRepository libroRepository;
    private ListaDRepository repository;
    private ListaDService service;
    private ObjectMapper objectMapper;
    private List<Utente> utenti;
    private List<ListaDesideri> libri;
    private List<Libro> libroEntity;


    @BeforeEach
    void setUp() throws Exception {
        repository = mock(ListaDRepository.class);
        libroRepository = mock((LibroRepository.class));
        utenteRepository= mock(UtenteRepository.class);
        service = new ListaDService(repository,libroRepository,utenteRepository);
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
                new File("src/test/java/features/ListeLibri/LibriListaD.json"),
                new TypeReference<List<ListaDesideri>>() {

                }
        );
    }

    @AfterEach
    void tearDown() {
        System.out.println("Fine dei test");
    }

    @Test
    void aggiungiListaDesideri_ok() {
        Utente lettore = utenti.get(1);
        Libro libro = libroEntity.get(3);
        when(utenteRepository.findById(lettore.getId()))
                .thenReturn(Optional.of(lettore));
        when(libroRepository.findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore()))
                .thenReturn(libro);

        String result = service.aggiungiListaDesideri(lettore.getId(), libro.getTitolo(), libro.getAutore());
        assertEquals("Libro aggiunto alla lista: " + libro, result);

        verify(utenteRepository,times(1)).findById(lettore.getId());
        verify(libroRepository,times(1)).findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());

    }

    @Test
    void aggiungiListaDesideri_libroGiaPresenteInLista() {
        Libro libro = libroEntity.get(1);
        Utente lettore = utenti.get(1);
        when(utenteRepository.findById(lettore.getId()))
                .thenReturn(Optional.of(lettore));
        when(libroRepository.findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore()))
                .thenReturn(libro);
        when(libroRepository.findIdLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore()))
                .thenReturn(libro.getIdLibro());
        when(repository.findAll())
                .thenReturn(List.of(new ListaDesideri(1L,lettore,libro)));
        LibroGiaPresenteInListaException e =
                assertThrows(LibroGiaPresenteInListaException.class, () -> service.aggiungiListaDesideri(lettore.getId(), libro.getTitolo(), libro.getAutore()));
        assertEquals("Libro già presente nella Lista Desideri", e.getMessage());
        verify(utenteRepository, times(1)).findById(lettore.getId());
        verify(libroRepository, times(1)).findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());
    }

    @Test
    void aggiungiListaDesideri_libroNonTrovato() {
        Utente lettore = utenti.get(1);
        Libro libro = libroEntity.getFirst();
       when(utenteRepository.findById(lettore.getId()))
               .thenReturn(Optional.of(lettore));
       when(libroRepository.findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore()))
               .thenReturn(null);

        LibroNonTrovatoException e =
                assertThrows(LibroNonTrovatoException.class, () -> service.aggiungiListaDesideri(lettore.getId(), libro.getTitolo(), libro.getAutore()));
        assertEquals("Libro non trovato", e.getMessage());

        verify(utenteRepository,times(1)).findById(lettore.getId());
        verify(libroRepository,times(1)).findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());

    }

    @Test
    void aggiungiListaDesideri_utenteNonAutorizzato() {
        Utente admin = utenti.getFirst();
        Libro libro = libroEntity.getFirst();
        when(utenteRepository.findById(admin.getId()))
                .thenReturn(Optional.of(admin));
        UtenteNonAutorizzatoException e =
                assertThrows(UtenteNonAutorizzatoException.class, () -> service.aggiungiListaDesideri(admin.getId(), "titolo","autore"));
        assertEquals("Utente non autorizzato", e.getMessage());
        verify(utenteRepository,times(1)).findById(admin.getId());
        verify(libroRepository,never()).findLibroByTitoloAndAutore(libro.getTitolo(), libro.getAutore());
    }

    @Test
    void visualizzaListaD_ok() {
        Utente lettore = utenti.get(3);
       when(utenteRepository.findById(lettore.getId()))
               .thenReturn(Optional.of(lettore));
       when(utenteRepository.findTipoUtenzaById(lettore.getId()))
               .thenReturn(TipoUtenza.LETTORE);
       when(repository.findByUtenteId(lettore.getId()))
               .thenReturn(libri);
        List<ListaDesideri> listaDesideri = service.visualizzaListaD(lettore.getId());
        assertEquals(libri,listaDesideri);

        verify(utenteRepository,times(1)).findById(lettore.getId());
        verify(utenteRepository,times(1)).findTipoUtenzaById(lettore.getId());
        verify(repository,times(1)).findByUtenteId(lettore.getId());
    }
    @Test
    void visualizzaListaD_utenteNonAutorizzato() {
        Utente admin = utenti.getFirst();
        when(utenteRepository.findById(admin.getId()))
                .thenReturn(Optional.of(admin));
        when(utenteRepository.findTipoUtenzaById(admin.getId()))
                .thenReturn(TipoUtenza.AMMINISTRATORE);

       UtenteNonAutorizzatoException e =
                assertThrows(UtenteNonAutorizzatoException.class, () -> service.visualizzaListaD(admin.getId()));
        assertEquals("Utente non autorizzato", e.getMessage());

        verify(utenteRepository,times(1)).findById(admin.getId());
        verify(utenteRepository,times(1)).findTipoUtenzaById(admin.getId());
        verify(repository,never()).findByUtenteId(admin.getId());
    }
    @Test
    void visualizzaListaD_utenteNonTrovato() {
        when(utenteRepository.findById(0L))
                .thenReturn(Optional.empty());

        UtenteNonTrovatoException e =
                assertThrows(UtenteNonTrovatoException.class, () -> service.visualizzaListaD(0L));
        assertEquals("Utente non trovato", e.getMessage());

        verify(utenteRepository,times(1)).findById(0L);
        verify(utenteRepository,never()).findTipoUtenzaById(0L);
        verify(repository,never()).findByUtenteId(0L);
    }
}