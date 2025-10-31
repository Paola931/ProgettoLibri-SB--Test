package ProgettoLibri_SB.Service;
import ProgettoLibri_SB.DTO.UuidGenerator;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Service
public class UploadDownloadService implements WebMvcConfigurer {
    private final String fileRepositoryFolder;
    private final UuidGenerator uuidGenerator;
    public UploadDownloadService(
            @Value("${fileRepositoryFolder}") String fileRepositoryFolder,
            UuidGenerator uuidGenerator
    ) {
        this.fileRepositoryFolder = fileRepositoryFolder;
        this.uuidGenerator = uuidGenerator;
    }

    @SneakyThrows
    public String upload (MultipartFile file){
        if(file == null){
            return null;
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String newFileName = uuidGenerator.generate();
        String completeFileName = newFileName + "." + extension;

        Path folderPath = Paths.get(fileRepositoryFolder);
        if(!Files.exists(folderPath))throw new IOException("La cartella finale non esiste");
        if(!Files.isDirectory(folderPath))throw  new IOException("La cartella finale non è una directory");
        Path finalDestination = folderPath.resolve(completeFileName);
        if(Files.exists(finalDestination)) throw new IOException("File in conflitto");

        Files.copy(file.getInputStream(),finalDestination, StandardCopyOption.REPLACE_EXISTING);
        return  completeFileName;
    }

    @SneakyThrows
    public String remove (String fileName){
        Path fileFromRepo = Paths.get(fileRepositoryFolder ,fileName);
        if(!Files.exists(fileFromRepo)){
            throw new IOException("File non trovato");
        }
        try{
            Files.delete(fileFromRepo);
            return "File rimosso correttamente: " + fileFromRepo;
        }catch (IOException e){
            throw  new IOException("Impossibile eliminare il file",e);
        }
    }

    @SneakyThrows
    public byte[] getImmagineDaPath(String imgPath) {
       Path filePath = Paths.get(fileRepositoryFolder,imgPath);

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File non trovato");
        }

        byte[] imageBytes = Files.readAllBytes(filePath);

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "image/jpeg";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK).getBody();
    }
}
