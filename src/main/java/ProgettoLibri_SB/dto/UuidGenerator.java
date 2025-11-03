package ProgettoLibri_SB.dto;

import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
public class UuidGenerator {
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
