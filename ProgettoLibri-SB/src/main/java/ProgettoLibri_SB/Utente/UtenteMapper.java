package ProgettoLibri_SB.Utente;

public class UtenteMapper {

    public static Utente toEntity(UtenteDTOUrl dto, Utente entity) {
        if (entity == null) entity = new Utente();
        entity.setNome_utente(dto.getNomeUtente());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        return entity;
    }

    public static UtenteDTO toDTO(Utente entity) {
        if (entity == null) return null;
        return new UtenteDTO(
                entity.getNome_utente(),
                entity.getEmail(),
                entity.getPassword()
        );
    }
}
