package ProgettoLibri_SB.Amministratore;

import ProgettoLibri_SB.Libro.Libro;
import ProgettoLibri_SB.Libro.LibroDTO;
import ProgettoLibri_SB.Libro.LibroService;
import ProgettoLibri_SB.Utente.UtenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/utente/admin")
public class AmministratoreController {


    @Autowired
    private LibroService libroService;


    //aggiungi libro V
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "409", description = "Libro già presente"),
            @ApiResponse(responseCode = "401", description = "Utente non autorizzato")
    })
    @Operation(summary = "Questo metodo permette alladmin di aggiungere un libro alla lista")
    @PostMapping("/aggiungiLibro/{id_utente}")
    public ResponseEntity aggiungiLibro(@PathVariable("id_utente") Long id_utente,
                                        @RequestParam String titolo,
                                        @RequestParam String autore,
                                        @RequestParam String ce,
                                        @RequestParam("copertina") MultipartFile copertina) throws IOException {
        return libroService.aggiungiLibro(id_utente,titolo,autore,ce,copertina);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Utente non autorizzato")
    })
    @Operation(summary = "Questo metodo permette alladmin di eliminare un libro dalla lista")
    @DeleteMapping("/eliminaLibro/{id_libro}")
    public ResponseEntity eliminaLibro(@PathVariable Long id_libro ,@RequestParam Long id_utente,String titolo, String autore){
        return  libroService.eliminaLibro(id_libro,id_utente,titolo,autore);
    }
}
