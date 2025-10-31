package ProgettoLibri_SB.Service;

import ProgettoLibri_SB.Entity.*;
import ProgettoLibri_SB.Exception.LibriException.LibroGiaPresenteInListaException;
import ProgettoLibri_SB.Exception.LibriException.LibroNonTrovatoException;
import ProgettoLibri_SB.Exception.UtenteException.UtenteNonAutorizzatoException;
import ProgettoLibri_SB.Exception.UtenteException.UtenteNonTrovatoException;
import ProgettoLibri_SB.Repository.LibriLRepository;
import ProgettoLibri_SB.Repository.LibroRepository;
import ProgettoLibri_SB.Repository.UtenteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibriLService {

    private final LibriLRepository libriLRepository;
    private final LibroRepository libroRepository;
    private final UtenteRepository utenteRepository;

    public LibriLService(LibriLRepository libriLRepository, LibroRepository libroRepository, UtenteRepository utenteRepository) {
        this.libriLRepository = libriLRepository;
        this.libroRepository = libroRepository;
        this.utenteRepository = utenteRepository;
    }

    public Long libroInLibriLetti(Long idLibro, Long idLettore){
        List<LibriLetti> libriletti = libriLRepository.findAll();
        if(libriletti.isEmpty()){
            return null;
        }else {
            for (LibriLetti ll : libriletti){
                if(ll.getLibro().getIdLibro().equals(idLibro) && ll.getUtente().getId().equals(idLettore)){
                    return ll.getLibro().getIdLibro();
                }
            }
        }
        return null;
    }


    public String aggiungiLibriLetti(Long idLettore, String titolo, String autore, int voto) {
        Optional<Utente> letttore = utenteRepository.findById(idLettore);
        if (letttore.isPresent()) {
            TipoUtenza checkLettore = utenteRepository.findTipoUtenzaById(idLettore);
            if (checkLettore.equals(TipoUtenza.LETTORE)) {
                Libro libro = libroRepository.findLibroByTitoloAndAutore(titolo, autore);
                if (libro == null) {
                    throw new LibroNonTrovatoException("Libro non trovato");
                } else {
                    Long idLibro = libroRepository.findIdLibroByTitoloAndAutore(titolo, autore);
                    Long libroPresente = libroInLibriLetti(idLibro, idLettore);
                    if (libroPresente != null && libroPresente > 0) {
                        throw new LibroGiaPresenteInListaException("Libro già presente nella lista Libri Letti");
                    } else {
                        LibriLetti ll = new LibriLetti();
                        ll.setUtente(letttore.get());
                        ll.setLibro(libro);
                        ll.setVoto(voto);
                        double media = libroRepository.findAverageVotoByIdLibro(idLibro);
                        libro.setMedia_voti(media);
                        return "Libro aggiunto alla lista: " + libro;
                    }
                }
            } else {
                throw new UtenteNonAutorizzatoException("Utente non autorizzato");
            }
        } else {
            throw new UtenteNonTrovatoException("Utente non trovato");
        }
    }

    public List<LibriLetti> visualizzaLibriLetti(Long idUtente) {
        Optional<Utente> checkUtente = utenteRepository.findById(idUtente);
        if (checkUtente.isPresent()) {
            TipoUtenza checkUtenza = utenteRepository.findTipoUtenzaById(idUtente);
            if (checkUtenza.equals(TipoUtenza.LETTORE)) {
                List<LibriLetti> libri = libriLRepository.findByUtenteId(idUtente);

                return libri;
            } else {
                throw new UtenteNonAutorizzatoException("Utente non autorizzato");
            }
        } else {
            throw new UtenteNonTrovatoException("Utente non trovato");
        }
    }
}
