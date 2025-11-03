package ProgettoLibri_SB.controller;

import ProgettoLibri_SB.service.LibroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/utente/admin")
public class AmministratoreController {

    private final LibroService libroService;

    public AmministratoreController(LibroService libroService) {
        this.libroService = libroService;
    }


    //aggiungi libro V
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Libro creato"),
            @ApiResponse(responseCode = "409", description = "Libro già presente"),
            @ApiResponse(responseCode = "401", description = "Utente non autorizzato")
    })
    @Operation(summary = "Questo metodo permette all'admin di aggiungere un libro alla lista")
    @PostMapping("/aggiungiLibro/{id_utente}")
    public ResponseEntity aggiungiLibro(@PathVariable("id_utente") Long id_utente,
                                        @RequestParam String titolo,
                                        @RequestParam String autore,
                                        @RequestParam String ce,
                                        @RequestParam("copertina") MultipartFile copertina) {
         libroService.aggiungiLibro(id_utente,titolo,autore,ce,copertina);
         return  ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Libro creato");
    }

    // modifica libri V
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Libro inesistente"),
            @ApiResponse(responseCode = "401", description = "Utente non autorizzato")
    })
    @Operation(summary = "Questo metodo permette all'admin di modificare un libro")
    @PatchMapping("/modificaLibro/{id_utente}")
    public ResponseEntity modificaLibro(@PathVariable Long id_utente, @RequestParam Long id_libro,
                                                                      @RequestParam (required = false)String titolo,
                                                                      @RequestParam (required = false)String autore,
                                                                      @RequestParam(required = false) MultipartFile copertina){
        libroService.modificaLibro(id_utente,id_libro,titolo,autore,copertina);
        return ResponseEntity.ok("Modifiche apportate");
    }

    //elimina libri V
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Utente non autorizzato")
    })
    @Operation(summary = "Questo metodo permette all'admin di eliminare un libro dalla lista")
    @DeleteMapping("/eliminaLibro/{id_libro}")
    public ResponseEntity eliminaLibro(@PathVariable Long id_libro ,@RequestParam Long id_utente,String titolo, String autore){
         libroService.eliminaLibro(id_libro,id_utente,titolo,autore);
         return ResponseEntity.status(HttpStatus.GONE).body("Libro eliminato");
    }
}
