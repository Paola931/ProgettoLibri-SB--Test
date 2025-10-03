package ProgettoLibri_SB.Libro;

public class LibroMapping {
    public static Libro toEntity(LibroDTO dto, Libro entity) {
        if (entity == null) entity = new Libro();
        entity.setTitolo(dto.getTitolo());
        entity.setAutore(dto.getAutore());
        entity.setCe(dto.getCe());
        return entity;
    }

    public static LibroDTO toDTO(Libro entity) {
        if (entity == null) return null;
        return new LibroDTO(
                entity.getTitolo(),
                entity.getAutore(),
                entity.getCopertina().toString(),
                entity.getCe()
        );
    }
}
