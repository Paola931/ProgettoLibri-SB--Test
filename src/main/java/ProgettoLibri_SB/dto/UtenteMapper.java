package ProgettoLibri_SB.dto;

import ProgettoLibri_SB.entity.Utente;

public class UtenteMapper {

    public static Utente toEntity(UtenteDTO dto, Utente entity) {
        if (entity == null) entity = new Utente();
        entity.setNomeUtente(dto.getNomeUtente());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        return entity;
    }

    public static UtenteDTO toDTO(Utente entity) {
        if (entity == null) return null;
        return new UtenteDTO(
                entity.getNomeUtente(),
                entity.getEmail(),
                entity.getPassword()
        );
    }
}
