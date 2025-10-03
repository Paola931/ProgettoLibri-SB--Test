package ProgettoLibri_SB.Libro;


public class LibroDTO {
    String titolo;
    String autore;
    String copertinaUrl;
    String ce;

    public LibroDTO(String titolo, String autore, String copertinaUrl, String ce) {
        this.titolo = titolo;
        this.autore = autore;
        this.copertinaUrl = copertinaUrl;
        this.ce = ce;
    }

    public LibroDTO() {
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getAutore() {
        return autore;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

    public String getCe() {
        return ce;
    }

    public void setCe(String ce) {
        this.ce = ce;
    }

    public String getCopertinaUrl() {
        return copertinaUrl;
    }
    public void setCopertinaUrl(String copertinaUrl) {
        this.copertinaUrl = copertinaUrl;
    }
}
