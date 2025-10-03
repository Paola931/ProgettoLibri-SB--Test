package ProgettoLibri_SB.UploadDownload;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class UploadDownloadService implements WebMvcConfigurer {
    @Value("${fileRepositoryFolder}")
    private String fileRepositoryFolder;

    @SneakyThrows
    public String upload (MultipartFile file){
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String newFileName = UUID.randomUUID().toString();
        String completeFileName = newFileName + "." + extension;

        File finalFolder = new File(fileRepositoryFolder);
        if(!finalFolder.exists())throw new IOException("La cartella finale non esiste");
        if(!finalFolder.isDirectory())throw  new IOException("La cartella finale non è una directory");
        File finalDestination = new File(fileRepositoryFolder + "\\" +completeFileName);
        if(finalDestination.exists()) throw new IOException("File in conflitto");

        file.transferTo(finalDestination);
        return  completeFileName;
    }

    @SneakyThrows
    public void remove (String fileName){
        File fileFromRepo = new File(fileRepositoryFolder + "\\" + fileName);
        if(!fileFromRepo.exists()) return;
        boolean deleteResult = fileFromRepo.delete();
        if(deleteResult == false) throw  new Exception("Impossibile eliminare il file");
    }

    public String generaUrlCopertina(Long id, HttpServletRequest request) {
        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();

        return baseUrl + "/libri/" + id + "/copertina";
    }

    public String generaUrlImmagineProfilo(Long id, HttpServletRequest request) {
        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();

        return baseUrl + "/utente/" + id + "/immagine_profilo";
    }
}
