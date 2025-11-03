package ProgettoLibri_SB.service;

import ProgettoLibri_SB.dto.UtenteDTO;
import ProgettoLibri_SB.entity.Utente;
import ProgettoLibri_SB.exception.UtenteException.CredenzialiErrateException;
import ProgettoLibri_SB.exception.UtenteException.EmailGiaEsistenteException;
import ProgettoLibri_SB.exception.UtenteException.NomeUtenteOccupatoException;
import ProgettoLibri_SB.exception.UtenteException.UtenteNonTrovatoException;
import ProgettoLibri_SB.repository.UtenteRepository;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class UtenteService {

    private final UtenteRepository repository;
    private final UploadDownloadService fileService;

    public UtenteService(UtenteRepository repository, UploadDownloadService fileService) {
        this.repository = repository;
        this.fileService = fileService;
    }

    public String modificaNomeUtente(String nuovoNome, UtenteDTO utente) {
        Optional<Utente> checkUtente = repository.findByEmailAndPassword(utente.getEmail(), utente.getPassword());
        if (checkUtente.isPresent()) {
            Optional<Utente> checkDisponibilitaNomeNuovo = repository.findByNomeUtente(nuovoNome);
            if (checkDisponibilitaNomeNuovo.isEmpty()) {
                Utente utenteEntity = checkUtente.get();
                utenteEntity.setNomeUtente(nuovoNome);
                repository.saveAndFlush(utenteEntity);
                return utenteEntity.getNomeUtente();
            } else {
                throw new NomeUtenteOccupatoException("Nome utente non disponibile");
            }
        } else {
            throw new CredenzialiErrateException("Credenziali errate");
        }
    }

    public String modificaEmail(String nuovaE, UtenteDTO utente) {
        Optional<Utente> checkUtente = repository.findByEmailAndPassword(utente.getEmail(), utente.getPassword());
        if (checkUtente.isPresent()) {
            Optional<Utente> checkEmailNuova = repository.findByEmail(nuovaE);
            if (checkEmailNuova.isEmpty()) {
                Utente utenteEntity = checkUtente.get();
                utenteEntity.setEmail(nuovaE);
                repository.saveAndFlush(utenteEntity);
                return "Email modificata con successo: " + utenteEntity.getEmail();
            } else {
                throw new EmailGiaEsistenteException("Email non disponibile");
            }
        } else {
            throw new CredenzialiErrateException("Credenziali errate");
        }
    }

    public String modificaPassword(String nuovap, UtenteDTO utente) {
        Optional<Utente> checkUtente = repository.findByEmailAndPassword(utente.getEmail(), utente.getPassword());
        if (checkUtente.isPresent()) {
            Utente utenteEntity = checkUtente.get();
            utenteEntity.setPassword(nuovap);
            repository.saveAndFlush(utenteEntity);
            return "Password modificata con successo: " + utenteEntity.getPassword();
        } else {
            throw new CredenzialiErrateException("Credenziali errate");
        }
    }

    @SneakyThrows
    public byte[] modificaImmagine(Long id, MultipartFile file) {
        Optional<Utente> utenteCheck = repository.findById(id);
        if (utenteCheck.isPresent()) {
            Utente utente = utenteCheck.get();
            if (utente.getImmagine_profilo() != null) {
                fileService.remove(utente.getImmagine_profilo().toString());
            }
            String imgP = fileService.upload(file);
            utente.setImmagine_profilo(imgP);
            repository.saveAndFlush(utente);
            return fileService.getImmagineDaPath(imgP);
        } else {
            throw new UtenteNonTrovatoException("Utente non trovato");
        }
    }
}




