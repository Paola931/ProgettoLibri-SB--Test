package ProgettoLibri_SB.Lettore;

import ProgettoLibri_SB.LibriLetti.LibriLService;
import ProgettoLibri_SB.ListaDesideri.ListaDService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/utente/lettore")
public class LettoreController {

    @Autowired
    private ListaDService listaDService;

    @Autowired
    private LibriLService libriLService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "409", description = "Libro già presente nella lista"),
            @ApiResponse(responseCode = "401", description = "Utente non autorizzato")
    })
    @Operation(summary = "Questo metodo permette di aggiungere un libro a Libri Letti e dare un voto alla lettura che va a modificare la media voti del libro")
    @PatchMapping("/aggiungiLibriLetti/{idLettore}")
    public ResponseEntity aggiungiLibriLetti(@PathVariable Long idLettore, @RequestParam String titolo, String autore, int voto) {
       return libriLService.aggiungiLibriLetti(idLettore, titolo, autore, voto);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "409", description = "Libro già presente nella lista"),
            @ApiResponse(responseCode = "401", description = "Utente non autorizzato")
    })
    @Operation(summary = "Questo metodo permette di aggiungere un libro alla Lista Desideri")
    @PatchMapping("/aggiungiListaDesideri/{idLettore}")
    public ResponseEntity aggiungiListaDesideri(@PathVariable Long idLettore, @RequestParam String titolo, String autore) {
        return listaDService.aggiungiListaDesideri(idLettore, titolo, autore);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Utente non autorizzato"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato")
    })
    @Operation(summary = "Questo metodo permette di visualizzare la Lista Desideri")
    @PatchMapping("/ListaDesideri/{idLettore}")
    public ResponseEntity visualizzaListaD(@PathVariable Long idLettore) {
        return listaDService.visualizzaListaD(idLettore);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Utente non autorizzato"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato")
    })
    @Operation(summary = "Questo metodo permette di visualizzare i Libri Letti")
    @PatchMapping("/LibriLetti/{idLettore}")
    public ResponseEntity visualizzaLibriLetti(@PathVariable Long idLettore) {
        return libriLService.visualizzaLibriLetti(idLettore);
    }



}
