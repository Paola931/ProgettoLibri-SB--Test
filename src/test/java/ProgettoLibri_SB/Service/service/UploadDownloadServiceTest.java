package ProgettoLibri_SB.Service.service;

import ProgettoLibri_SB.dto.UuidGenerator;
import ProgettoLibri_SB.service.UploadDownloadService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UploadDownloadServiceTest {

    private UploadDownloadService service;
    private String fileRepositoryFolder;
    private UuidGenerator uuidGenerator;
    private Path folderPath;
    private MockMultipartFile multipartFile;
    private MockMultipartFile nullFile;
    private String imgPath;


    @BeforeEach
    void setUp() {
        fileRepositoryFolder = "/home/pammaturo/Immagini/";
        uuidGenerator = mock(UuidGenerator.class);
        service = new UploadDownloadService(fileRepositoryFolder,uuidGenerator);
        folderPath = Paths.get(fileRepositoryFolder);
        multipartFile = new MockMultipartFile(
                "file",
                "file.txt",
                "text/plain",
                "Contenuto di prova".getBytes()
        );
        nullFile = null;
        imgPath = "immagine.jpg";

    }

    @AfterEach
    void tearDown() {
        System.out.println("Fine dei test");
    }

    @Test
    void upload_ok() {

        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            when(uuidGenerator.generate()).thenReturn("fakeUUID");
            Path finalDestination = folderPath.resolve("fakeUUID.txt");

            mockedFiles.when(() -> Files.exists(folderPath)).thenReturn(true);
            mockedFiles.when(() -> Files.isDirectory(folderPath)).thenReturn(true);
            mockedFiles.when(() -> Files.exists(finalDestination)).thenReturn(false);

            String result = service.upload(multipartFile);

            assertNotNull(result);
            assertEquals("fakeUUID.txt", result);

            verify(uuidGenerator, times(1)).generate();
            mockedFiles.verify(() -> Files.exists(folderPath), times(1));
            mockedFiles.verify(() -> Files.isDirectory(folderPath), times(1));
            mockedFiles.verify(() -> Files.exists(finalDestination), times(1));
        }
    }

    @Test
    void upload_null() {
            String result = service.upload(nullFile);

            assertNull(result);

            verify(uuidGenerator, never()).generate();
    }

    @Test
    void upload_folderPathNotExists() {

        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            when(uuidGenerator.generate()).thenReturn("fakeUUID");
            Path finalDestination = folderPath.resolve("fakeUUID.txt");

            mockedFiles.when(() -> Files.exists(folderPath)).thenReturn(false);
            mockedFiles.when(() -> Files.isDirectory(folderPath)).thenReturn(true);
            mockedFiles.when(() -> Files.exists(finalDestination)).thenReturn(false);
            mockedFiles.when(() -> Files.copy(
                    any(InputStream.class),
                    eq(finalDestination),
                    eq(StandardCopyOption.REPLACE_EXISTING)
            )).thenReturn(1L);

            IOException e =
                    assertThrows(IOException.class, () -> service.upload(multipartFile));
            assertEquals("La cartella finale non esiste", e.getMessage());


            verify(uuidGenerator, times(1)).generate();
            mockedFiles.verify(() -> Files.exists(folderPath), times(1));
            mockedFiles.verify(() -> Files.isDirectory(folderPath), never());
            mockedFiles.verify(() -> Files.exists(finalDestination), never());
            mockedFiles.verify(() -> Files.copy(any(InputStream.class),
                    eq(finalDestination),
                    eq(StandardCopyOption.REPLACE_EXISTING)), never());
        }
    }

    @Test
    void upload_folderPathIsNotADirectory() {

        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            when(uuidGenerator.generate()).thenReturn("fakeUUID");
            Path finalDestination = folderPath.resolve("fakeUUID.txt");

            mockedFiles.when(() -> Files.exists(folderPath)).thenReturn(true);
            mockedFiles.when(() -> Files.isDirectory(folderPath)).thenReturn(false);
            mockedFiles.when(() -> Files.exists(finalDestination)).thenReturn(false);

            IOException e =
                    assertThrows(IOException.class, () -> service.upload(multipartFile));
            assertEquals("La cartella finale non è una directory", e.getMessage());


            verify(uuidGenerator, times(1)).generate();
            mockedFiles.verify(() -> Files.exists(folderPath), times(1));
            mockedFiles.verify(() -> Files.isDirectory(folderPath), times(1));
            mockedFiles.verify(() -> Files.exists(finalDestination), never());
        }
    }

    @Test
    void upload_fileConflict() {

        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            when(uuidGenerator.generate()).thenReturn("fakeUUID");
            Path finalDestination = folderPath.resolve("fakeUUID.txt");

            mockedFiles.when(() -> Files.exists(folderPath)).thenReturn(true);
            mockedFiles.when(() -> Files.isDirectory(folderPath)).thenReturn(true);
            mockedFiles.when(() -> Files.exists(finalDestination)).thenReturn(true);

            IOException e =
                    assertThrows(IOException.class, () -> service.upload(multipartFile));
            assertEquals("File in conflitto", e.getMessage());


            verify(uuidGenerator, times(1)).generate();
            mockedFiles.verify(() -> Files.exists(folderPath), times(1));
            mockedFiles.verify(() -> Files.isDirectory(folderPath), times(1));
            mockedFiles.verify(() -> Files.exists(finalDestination), times(1));
        }
    }

    @Test
    void remove_ok() {
       String fileName = "file.txt";
       Path path = Paths.get(fileRepositoryFolder,fileName);
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(path)).thenReturn(true);
            mockedFiles.when(()->Files.delete(path)).thenAnswer(invocation->null);

            String result = service.remove(path.getFileName().toString());
            assertEquals("File rimosso correttamente: " + path,result);

            mockedFiles.verify(() -> Files.exists(path), times(1));
            mockedFiles.verify(() -> Files.delete(path), times(1));
        }
    }

    @Test
    void remove_fileNotFound() {
       String fileName = "file.txt";
       Path path = Paths.get(fileRepositoryFolder,fileName);
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(()->Files.exists(path)).thenReturn(false);
            mockedFiles.when(() -> Files.delete(path)).thenAnswer(invocation->null);

            IOException e =
                    assertThrows(IOException.class, () -> service.remove(path.getFileName().toString()));
            assertEquals("File non trovato", e.getMessage());

            mockedFiles.verify(() -> Files.exists(path), times(1));
            mockedFiles.verify(()->Files.delete(path),never());
        }
    }
    @Test
    void remove_fileNotDeleted() throws IOException {
        Path directory = Files.createTempDirectory("indeletableDirectory");
        Path fileFromRepo = Paths.get(fileRepositoryFolder, directory.getFileName().toString());
        Files.createTempFile( directory,"file",".txt");
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class,CALLS_REAL_METHODS)) {
            mockedFiles.when(()->Files.exists(fileFromRepo)).thenReturn(true);

            IOException e =
                    assertThrows(IOException.class, () -> service.remove(directory.getFileName().toString()));
            assertEquals("Impossibile eliminare il file", e.getMessage());

            mockedFiles.verify(() -> Files.exists(fileFromRepo), times(2));
            mockedFiles.verify(()->Files.delete(fileFromRepo),times(1));
        }
    }

    @Test
    void getImmagineDaPath_ok() {
        Path filePath= Paths.get(fileRepositoryFolder, imgPath);
        byte[] fakeimg = "fakeimg".getBytes();
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(()->Files.exists(filePath)).thenReturn(true);
            mockedFiles.when(()->Files.readAllBytes(filePath)).thenReturn(fakeimg);
            mockedFiles.when(()->Files.probeContentType(filePath)).thenReturn("image/jpeg");

            byte[] result = service.getImmagineDaPath(imgPath);

            assertNotNull(result);
            assertArrayEquals(fakeimg,result);

            mockedFiles.verify(()->Files.exists(filePath),times(1));
            mockedFiles.verify(()->Files.readAllBytes(filePath),times(1));
            mockedFiles.verify(()->Files.probeContentType(filePath),times(1));

        }
    }
    @Test
    void getImmagineDaPath_fileNotExists() {
        Path filePath= Paths.get(fileRepositoryFolder, imgPath);
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(()->Files.exists(filePath)).thenReturn(false);
            mockedFiles.when(()->Files.readAllBytes(filePath)).thenReturn(null);
            mockedFiles.when(()->Files.probeContentType(filePath)).thenReturn(null);

            FileNotFoundException e =
                    assertThrows(FileNotFoundException.class,()-> service.getImmagineDaPath(imgPath));
            assertEquals("File non trovato",e.getMessage());

            mockedFiles.verify(()->Files.exists(filePath),times(1));
            mockedFiles.verify(()->Files.readAllBytes(filePath),never());
            mockedFiles.verify(()->Files.probeContentType(filePath),never());
        }
    }
}