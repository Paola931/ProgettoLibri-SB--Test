package ProgettoLibri_SB.exception;

import ProgettoLibri_SB.exception.LibriException.LibroGiaPresenteInListaException;
import ProgettoLibri_SB.exception.LibriException.LibroNonTrovatoException;
import ProgettoLibri_SB.exception.UtenteException.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(LibroNonTrovatoException.class)
    public ResponseEntity<String> handleLibroNonTrovato(LibroNonTrovatoException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LibroGiaPresenteInListaException.class)
    public ResponseEntity<String> handleLibroGiaPresenteInLista(LibroGiaPresenteInListaException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UtenteNonAutorizzatoException.class)
    public ResponseEntity<String> handleUtenteNonAutorizzato(UtenteNonAutorizzatoException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CredenzialiErrateException.class)
    public ResponseEntity<String> handleCredenzialiErrate(CredenzialiErrateException ex){
        return new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UtenteGiaRegistratoException.class)
    public ResponseEntity<String> handleUtenteGiaRegistrato(UtenteGiaRegistratoException ex){
        return new ResponseEntity<>(ex.getMessage(),HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NomeUtenteOccupatoException.class)
    public ResponseEntity<String> handleNomeUtenteOccupato(NomeUtenteOccupatoException ex){
        return new ResponseEntity<>(ex.getMessage(),HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailGiaEsistenteException.class)
    public ResponseEntity<String> handleEmailGiaEsistente(EmailGiaEsistenteException ex){
        return new ResponseEntity<>(ex.getMessage(),HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UtenteNonTrovatoException.class)
    public ResponseEntity<String> handleUtenteNonTrovato(UtenteNonTrovatoException ex){
        return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        return new ResponseEntity<>("Errore interno del server: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
