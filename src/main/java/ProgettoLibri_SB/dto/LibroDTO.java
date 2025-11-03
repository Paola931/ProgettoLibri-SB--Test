package ProgettoLibri_SB.dto;

public class LibroDTO {
    Long id;
    String titolo;
    String autore;

    public LibroDTO(Long id, String titolo, String autore) {
        this.id = id;
        this.titolo = titolo;
        this.autore = autore;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "LibroDTO{" +
                "id=" + id +
                ", titolo='" + titolo + '\'' +
                ", autore='" + autore + '\'' +
                '}';
    }
}
