package ProgettoLibri_SB.controller;

import ProgettoLibri_SB.service.LibriLService;
import ProgettoLibri_SB.service.ListaDService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/utente/lettore")
public class LettoreController {

    private final ListaDService listaDService;
    private final LibriLService libriLService;

    public LettoreController(ListaDService listaDService, LibriLService libriLService) {
        this.listaDService = listaDService;
        this.libriLService = libriLService;
    }

    //aggiungi libri letti V
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Libro inesistente"),
            @ApiResponse(responseCode = "409", description = "Libro già presente nella lista"),
            @ApiResponse(responseCode = "401", description = "Utente non autorizzato")
    })
    @Operation(summary = "Questo metodo permette di aggiungere un libro a Libri Letti e dare un voto alla lettura che va a modificare la media voti del libro")
    @PostMapping("/aggiungiLibriLetti/{idLettore}")
    public ResponseEntity aggiungiLibriLetti(@PathVariable Long idLettore, @RequestParam String titolo, String autore, int voto) {
       libriLService.aggiungiLibriLetti(idLettore, titolo, autore, voto);
       return ResponseEntity.ok("Libro aggiunto correttamente alla tua lista di Libri Letti");
    }

    //aggiungi lista desideri V
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "409", description = "Libro già presente nella lista"),
            @ApiResponse(responseCode = "401", description = "Utente non autorizzato")
    })
    @Operation(summary = "Questo metodo permette di aggiungere un libro alla Lista Desideri")
    @PostMapping("/aggiungiListaDesideri/{idLettore}")
    public ResponseEntity aggiungiListaDesideri(@PathVariable Long idLettore, @RequestParam String titolo, String autore) {
       listaDService.aggiungiListaDesideri(idLettore, titolo, autore);
        return ResponseEntity.ok("Libro aggiunto correttamente alla tua Lista Desideri");
    }

    // visualizza lista desideri V
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Utente non autorizzato"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato")
    })
    @Operation(summary = "Questo metodo permette di visualizzare la Lista Desideri")
    @GetMapping("/ListaDesideri/{idLettore}")
    public ResponseEntity visualizzaListaD(@PathVariable Long idLettore) {
         listaDService.visualizzaListaD(idLettore);
        return ResponseEntity.ok("Lista Desideri");
    }

    //visualizza libri letti V
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Utente non autorizzato"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato")
    })
    @Operation(summary = "Questo metodo permette di visualizzare i Libri Letti")
    @GetMapping("/LibriLetti/{idLettore}")
    public ResponseEntity visualizzaLibriLetti(@PathVariable Long idLettore) {
        libriLService.visualizzaLibriLetti(idLettore);
        return ResponseEntity.ok("Lista Libri Letti");
    }
}
