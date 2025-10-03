package ProgettoLibri_SB.Utente;

public class UtenteDTO {
    String nomeUtente;
    String email;
    String password;

    public UtenteDTO(String nomeUtente, String email, String password) {
        this.nomeUtente = nomeUtente;
        this.email = email;
        this.password = password;
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
}
