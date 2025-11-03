package ProgettoLibri_SB.controller;


import ProgettoLibri_SB.service.LibroService;
import ProgettoLibri_SB.service.UtenteService;
import ProgettoLibri_SB.dto.UtenteDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/utente")
public class UtenteController {

    private final UtenteService service;
    private final LibroService libroService;

    public UtenteController(UtenteService service, LibroService libroService) {
        this.service = service;
        this.libroService = libroService;
    }

    //cercalibro V
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Libro inesistente")
    })
    @Operation(summary = "Questo metodo permette di cercare un libro tramite titolo e autore")
    @GetMapping("/cercaLibro")
    public ResponseEntity cercaLibro(@RequestParam String titolo, String autore) {
        libroService.cercaLibro(titolo,autore);
        return  ResponseEntity.ok("Ricerca libro completa");
    }

    //modifica nome utente V
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "409", description = "Nome utente non disponibile"),
            @ApiResponse(responseCode = "400", description = "Credenziali errate")
    })
    @Operation(summary = "Questo metodo permette di modificare il nomeUtente")
    @PatchMapping("/modificaNomeUtente")
    public ResponseEntity modificaNomeUtente(@RequestParam String nuovoNome, @RequestBody UtenteDTO utente) {
       service.modificaNomeUtente(nuovoNome,utente);
       return ResponseEntity.ok("Nome utente modificato");
    }
    //modifica email V
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "409", description = "Email non disponibile"),
            @ApiResponse(responseCode = "400", description = "Credenziali errate - Email non disponibile")
    })
    @Operation(summary = "Questo metodo permette di modificare l'email")
    @PatchMapping("/modificaEmail")
    public ResponseEntity modificaEmail(@RequestParam String nuovaE,@RequestBody UtenteDTO utente){
         service.modificaEmail(nuovaE,utente);
        return ResponseEntity.ok("Email modificata");
    }
    //modifica password V
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Credenziali errate")
    })
    @Operation(summary = "Questo metodo permette di modificare la password")
    @PatchMapping("/modificaPassword")
    public ResponseEntity modificaPassword(@RequestParam String nuovap,@RequestBody UtenteDTO utente){
         service.modificaPassword(nuovap,utente);
        return ResponseEntity.ok("Password modificata");
    }

    //modifica immagine profilo V
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato")
    })
    @Operation(summary = "Questo metodo permette di modificare l'immagine del profilo")
    @PatchMapping("/modificaIP/{id_utente}")
    public ResponseEntity<?> modificaImmagineProfilo(@PathVariable Long id_utente, @RequestParam MultipartFile immagine_profilo){
         service.modificaImmagine(id_utente,immagine_profilo);
        return ResponseEntity.ok("Immagine del profilo modificata");
    }

}
