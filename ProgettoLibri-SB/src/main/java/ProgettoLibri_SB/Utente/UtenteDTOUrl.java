package ProgettoLibri_SB.Utente;



public class UtenteDTOUrl {
    String nomeUtente;
    String email;
    String password;
    String immagineProfiloUrl;

    public UtenteDTOUrl(String nomeUtente, String email, String password, String immagineProfiloUrl) {
        this.nomeUtente = nomeUtente;
        this.email = email;
        this.password = password;
        this.immagineProfiloUrl = immagineProfiloUrl;
    }

    public String getNomeUtente() {
        return nomeUtente;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImmagineProfilo() {
        return immagineProfiloUrl;
    }
    public void setImmagineProfilo(String immagineProfilo) {
        this.immagineProfiloUrl = immagineProfilo;
    }
}

