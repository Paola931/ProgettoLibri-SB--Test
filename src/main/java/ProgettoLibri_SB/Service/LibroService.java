package ProgettoLibri_SB.Service;

import ProgettoLibri_SB.DTO.LibroDTO;
import ProgettoLibri_SB.Entity.Libro;
import ProgettoLibri_SB.Exception.LibriException.LibroGiaPresenteInListaException;
import ProgettoLibri_SB.Exception.LibriException.LibroNonTrovatoException;
import ProgettoLibri_SB.Exception.UtenteException.UtenteNonAutorizzatoException;
import ProgettoLibri_SB.Exception.UtenteException.UtenteNonTrovatoException;
import ProgettoLibri_SB.Repository.LibroRepository;
import ProgettoLibri_SB.Entity.TipoUtenza;
import ProgettoLibri_SB.Entity.Utente;
import ProgettoLibri_SB.Repository.UtenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;


@Service
public class LibroService {

    private final LibroRepository repository;
    private final UtenteRepository utenteRepository;
    private final UploadDownloadService fileService;

    public LibroService(LibroRepository repository, UtenteRepository utenteRepository, UploadDownloadService fileService) {
        this.repository = repository;
        this.utenteRepository = utenteRepository;
        this.fileService = fileService;
    }

    public String cercaLibro(String titolo, String autore){
        Libro libro = repository.findLibroByTitoloAndAutore(titolo, autore);
        if (libro == null) {
            throw new LibroNonTrovatoException("Libro non trovato");
        } else {
            boolean copertinaLibro = repository.findLibroByTitoloAndAutoreAndCopertinaIsNotNull(titolo,autore);
            if(copertinaLibro){
                return "Copertina libro: " + fileService.getImmagineDaPath(libro.getCopertina());
            }else{
                return "Il libro che cercavi è: " + libro;
            }
        }
    }

    public Libro aggiungiLibro(Long id_utente, String titolo, String autore, String ce, MultipartFile copertina) {
        Optional<Utente> checkUtente = utenteRepository.findById(id_utente);
        if (checkUtente.isPresent()){
            if(utenteRepository.findTipoUtenzaById(id_utente).equals(TipoUtenza.AMMINISTRATORE)) {

                Libro checkLibro = repository.findLibroByTitoloAndAutore(titolo, autore);
                if (checkLibro == null) {
                    Libro libro = new Libro();
                    libro.setTitolo(titolo);
                    libro.setAutore(autore);
                    libro.setCe(ce);
                    String fileCopertina = fileService.upload(copertina);
                    libro.setCopertina(fileCopertina);
                    libro.setUtente(checkUtente.get());
                    repository.saveAndFlush(libro);

                    return libro;
                } else {
                    throw new LibroGiaPresenteInListaException("Libro già presente");
                }
            }else{
                throw new UtenteNonAutorizzatoException("Utente non autorizzato");
            }
        } else {
           throw  new UtenteNonTrovatoException("Utente non trovato");
        }
    }

    public Libro modificaLibro(Long id_utente,Long id_libro,String titolo, String autore, MultipartFile copertina){
        Optional<Utente> checkUtente = utenteRepository.findById(id_utente);
        if (checkUtente.isPresent()){
            if(utenteRepository.findTipoUtenzaById(id_utente).equals(TipoUtenza.AMMINISTRATORE)) {

                Optional<Libro> checkLibro = repository.findById(id_libro);
                if (checkLibro.isEmpty()) {
                    throw new LibroNonTrovatoException("Libro non trovato");
                } else {
                    Libro libro = checkLibro.get();
                    libro.setTitolo(titolo);
                    libro.setAutore(autore);
                    libro.setCopertina(fileService.upload(copertina));
                    repository.saveAndFlush(libro);
                    return libro;
                }
            }else {
                throw new UtenteNonAutorizzatoException("Utente non autorizzato");
            }
        } else {
            throw  new UtenteNonTrovatoException("Utente non trovato");
        }
    }

    public String eliminaLibro(Long idLibro,Long idAdmin, String titolo, String autore) {
        Optional<Utente> checkUtente = utenteRepository.findById(idAdmin);
        if (checkUtente.isPresent()) {
            boolean amministratore  = utenteRepository.findTipoUtenzaById(idAdmin).equals(TipoUtenza.AMMINISTRATORE);
            if(amministratore) {
                Long libro = repository.findIdLibroByTitoloAndAutore(titolo, autore);
                if (idLibro.equals(libro)) {
                    repository.deleteById(idLibro);
                    LibroDTO libroEliminato = new LibroDTO(idLibro, titolo, autore);
                    return "Libro eliminato: " + libroEliminato;
                } else {
                    throw new LibroNonTrovatoException("Libro non trovato");
                }
            }else{
                throw new UtenteNonAutorizzatoException("Utente non autorizzato");
            }
        } else {
            throw  new UtenteNonTrovatoException("Utente non trovato");
        }
    }
}
