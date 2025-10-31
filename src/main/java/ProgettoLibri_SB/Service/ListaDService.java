package ProgettoLibri_SB.Service;

import ProgettoLibri_SB.Entity.Libro;
import ProgettoLibri_SB.Entity.ListaDesideri;
import ProgettoLibri_SB.Exception.LibriException.LibroGiaPresenteInListaException;
import ProgettoLibri_SB.Exception.LibriException.LibroNonTrovatoException;
import ProgettoLibri_SB.Exception.UtenteException.UtenteNonAutorizzatoException;
import ProgettoLibri_SB.Exception.UtenteException.UtenteNonTrovatoException;
import ProgettoLibri_SB.Repository.LibroRepository;
import ProgettoLibri_SB.Repository.ListaDRepository;
import ProgettoLibri_SB.Entity.TipoUtenza;
import ProgettoLibri_SB.Entity.Utente;
import ProgettoLibri_SB.Repository.UtenteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ListaDService {

    private final ListaDRepository listaDRepository;
    private final LibroRepository libroRepository;
    private final UtenteRepository utenteRepository;

    public ListaDService(ListaDRepository listaDRepository, LibroRepository libroRepository, UtenteRepository utenteRepository) {
        this.listaDRepository = listaDRepository;
        this.libroRepository = libroRepository;
        this.utenteRepository = utenteRepository;
    }

 public Long libroInListaDesideri (Long idLibro,Long idLettore){
        List<ListaDesideri> listaDesideri = listaDRepository.findAll();
        if(listaDesideri.isEmpty()){
            return null;
        }else {
           for (ListaDesideri ld : listaDesideri){
              if(ld.getLibro().getIdLibro().equals(idLibro) && ld.getUtente().getId().equals(idLettore)){
                  return ld.getLibro().getIdLibro();
              }
           }
        }
        return null;
 }

    public String aggiungiListaDesideri(Long idLettore, String titolo, String autore) {
       Optional<Utente> lettore = utenteRepository.findById(idLettore);
        if (lettore.get().getTipo_utenza().equals(TipoUtenza.LETTORE)) {
            Libro libro = libroRepository.findLibroByTitoloAndAutore(titolo, autore);
            if (libro == null) {
                throw new LibroNonTrovatoException("Libro non trovato");
            } else {
                Long idLibro = libroRepository.findIdLibroByTitoloAndAutore(titolo, autore);
                Long libroPresente = libroInListaDesideri(idLibro,idLettore);
                if (libroPresente != null && libroPresente > 0) {
                    throw new LibroGiaPresenteInListaException("Libro già presente nella Lista Desideri");
                } else {
                    ListaDesideri ld = new ListaDesideri();
                    ld.setUtente(lettore.get());
                    ld.setLibro(libro);
                    return "Libro aggiunto alla lista: " + libro;
                }
            }
        } else {
           throw new UtenteNonAutorizzatoException("Utente non autorizzato");
        }
    }

    public List<ListaDesideri> visualizzaListaD(Long idUtente){
        Optional<Utente> utente = utenteRepository.findById(idUtente);
        if(utente.isPresent()){
            TipoUtenza checkUtenza = utenteRepository.findTipoUtenzaById(idUtente);
            if(checkUtenza.equals(TipoUtenza.LETTORE)) {
                List<ListaDesideri> lista = listaDRepository.findByUtenteId(idUtente);
                return lista;
            }else {
                throw new UtenteNonAutorizzatoException("Utente non autorizzato");
            }
        }else{
           throw new UtenteNonTrovatoException("Utente non trovato");
        }
    }
}
