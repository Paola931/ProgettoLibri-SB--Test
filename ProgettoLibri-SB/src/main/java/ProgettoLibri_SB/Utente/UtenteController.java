package ProgettoLibri_SB.Utente;


import ProgettoLibri_SB.Libro.LibroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/utente")
public class UtenteController {

    @Autowired
    private UtenteService service;

    @Autowired
    private LibroService libroService;

    //modifica nome utente V
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato")
    })
    @Operation(summary = "Questo metodo permette di modificare il nomeUtente")
    @PatchMapping("/modificaNomeUtente")
    public ResponseEntity modificaNomeUtente(@RequestParam String nuovoNome, @RequestBody UtenteDTO utente) {
        return service.modificaNomeUtente(nuovoNome,utente);
    }
    //modifica email V
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Credenziali errate - Email non disponibile")
    })
    @Operation(summary = "Questo metodo permette di modificare l'email")
    @PatchMapping("/modificaEmail")
    public ResponseEntity modificaEmail(@RequestParam String nuovaE,@RequestBody UtenteDTO utente){
        return service.modificaEmail(nuovaE,utente);
    }
    //modifica password V
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Credenziali errate")
    })
    @Operation(summary = "Questo metodo permette di modificare la password")
    @PatchMapping("/modificaPassword/{id}")
    public ResponseEntity modificaPassword(@PathVariable Long id,@RequestParam String nuovap,@RequestBody UtenteDTO utente){
        return service.modificaPassword(id,nuovap,utente);
    }
    //cercalibro V
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Libro non presente")
    })
    @Operation(summary = "Questo metodo permette di cercare un libro tramite titolo e autore")
    @GetMapping("/cercaLibro")
    public ResponseEntity cercaLibro(@RequestParam String titolo, String autore) throws UnsupportedEncodingException {
        return libroService.cercaLibro(titolo,autore);
    }

    //modifica immagine profilo V
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato")
    })
    @Operation(summary = "Questo metodo permette di modificare l'immagine del profilo")
    @PatchMapping("/modificaIP/{id_utente}")
    public ResponseEntity modificaImmagineProfilo(@PathVariable Long id_utente, @RequestParam MultipartFile immagine_profilo){
        return service.modificaImmagine(id_utente,immagine_profilo);
    }

}
