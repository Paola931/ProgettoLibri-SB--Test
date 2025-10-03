package ProgettoLibri_SB.Utente;

import ProgettoLibri_SB.Libro.LibroRepository;
import ProgettoLibri_SB.UploadDownload.UploadDownloadService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class UtenteService {
    @Autowired
    private UtenteRepository repository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private UploadDownloadService fileService;

    //modifica nome utente
    public ResponseEntity modificaNomeUtente(String nuovoNome, UtenteDTO utente) {
        Optional<Utente> checkUtente = repository.findByNomeUtente(utente.getNomeUtente());
        Optional<Utente> checkDisponibilitaNomeNuovo = repository.findByNomeUtente(nuovoNome);
        if (checkUtente.isPresent() && checkDisponibilitaNomeNuovo.isEmpty()) {
            Utente utenteEntity = checkUtente.get();
            utenteEntity.setNome_utente(nuovoNome);
            repository.saveAndFlush(utenteEntity);
            return ResponseEntity.ok(utenteEntity);
        } else {
            return ResponseEntity.status(404).body("Utente non trovato");
        }
    }

    //modifica email
    public ResponseEntity modificaEmail(String nuovaE, UtenteDTO utente) {
        System.out.println(utente.getEmail());
        Optional<Utente> checkEmailEsistente = repository.findByEmail(utente.getEmail());
        System.out.println(checkEmailEsistente);
        Optional<Utente> checkEmailNuova = repository.findByEmail(nuovaE);
        System.out.println(checkEmailNuova);
        if (checkEmailEsistente.isPresent() && checkEmailNuova.isEmpty()) {
            Utente utenteEntity = checkEmailEsistente.get();
            utenteEntity.setEmail(nuovaE);
            repository.saveAndFlush(utenteEntity);
            return ResponseEntity.ok(utenteEntity);
        } else {
            return ResponseEntity.status(400).body("Credenziali errate - Email non disponibile");
        }
    }

    //modifica password
    public ResponseEntity modificaPassword(Long id, String nuovap, UtenteDTO utente) {
        System.out.println(id);
        Optional<Utente> checkUtente = repository.findById(id);
        System.out.println(checkUtente);
        if (checkUtente.isPresent()) {
            Utente utenteEntity = checkUtente.get();
            utenteEntity.setPassword(nuovap);
            repository.saveAndFlush(utenteEntity);
            return ResponseEntity.ok(utenteEntity);
        } else {
            return ResponseEntity.status(400).body("Credenziali errate");
        }
    }

   @SneakyThrows
    public ResponseEntity modificaImmagine(Long id, MultipartFile file) {
        System.out.println(id);
        Optional<Utente> utenteCheck = repository.findById(id);
        System.out.println(utenteCheck);
        if (utenteCheck.isPresent()) {
            Utente utente = utenteCheck.get();
            if (utente.getImmagine_profilo() != null) {
                fileService.remove(utente.getImmagine_profilo().toString());
            }

            utente.setImmagine_profilo(fileService.upload(file));
            repository.saveAndFlush(utente);

            return ResponseEntity.ok(utente);
        } else {
            return ResponseEntity.status(404).body("Utente non presente");
        }
    }


}




