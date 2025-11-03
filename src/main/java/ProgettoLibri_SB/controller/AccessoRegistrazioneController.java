package ProgettoLibri_SB.controller;


import ProgettoLibri_SB.entity.Utente;
import ProgettoLibri_SB.service.AccessoRegistrazioneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccessoRegistrazioneController {

     private final AccessoRegistrazioneService accessoRegistrazioneService;

    public AccessoRegistrazioneController(AccessoRegistrazioneService accessoRegistrazioneService) {
        this.accessoRegistrazioneService = accessoRegistrazioneService;
    }

    // registrazione admin V
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utente creato"),
            @ApiResponse(responseCode = "409", description = "Utente già registrato")
    })
    @Operation(summary = "Questo metodo permette di regitrare un utente admin")
    @PostMapping("/registraAdmin")
    public ResponseEntity registrazioneAdmin (@RequestBody Utente utente){
         accessoRegistrazioneService.registrazioneAdmin(utente);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Admin creato");
    }

    // registrazione lettore V
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utente creato"),
            @ApiResponse(responseCode = "409", description = "Utente già registrato")
    })
    @Operation(summary = "Questo metodo permette di regitrare un utente admin")
    @PostMapping("/registraLettore")
    public ResponseEntity registrazioneLettore (@RequestBody Utente utente){
       accessoRegistrazioneService.registrazioneLettore(utente);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Lettore creato");
    }

    // accesso V
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato")
    })
    @Operation(summary = "Questo metodo permette di accedere ai metodi dei vari controller in base al tipo di utenza")
    @PostMapping("/accesso")
    public ResponseEntity accesso (@RequestParam String email, String password, HttpSession session){
         accessoRegistrazioneService.accesso(email,password,session);
         return ResponseEntity.ok("Accesso effettuato");
    }

    // esci V
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @Operation(summary = "Questo metodo permette di uscre dal programma")
    @PostMapping("/esci")
    public ResponseEntity esci (HttpSession session){
         accessoRegistrazioneService.esci(session);
         return ResponseEntity.ok("Sessione chiusa");
    }
}
