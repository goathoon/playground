package my.playground.playing.bytetostream;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.CipherOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/upload")
@Slf4j
public class BinaryUploadController {

    @PostMapping(value = "/bytes-only", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<String> bytesOnly(@RequestBody byte[] data) {
        File encryptedFile = new File("tmp/encrypted-byte.text");

        try {
            byte[] encrypted = AesEncryptor.encrypt(data);
            log.info("암호화된 바이트 크기: {}", encrypted.length);

            try (FileOutputStream fos = new FileOutputStream(encryptedFile)) {
                fos.write(encrypted);
            }

            log.info("저장된 파일 경로: {}", encryptedFile.getAbsolutePath());
            log.info("저장된 파일 크기: {} bytes", encryptedFile.length());

            return ResponseEntity.ok("Encrypted and saved: " + encryptedFile.length() + " bytes");

        } catch (Exception e) {
            log.error("암호화 또는 저장 실패", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("암호화 실패: " + e.getMessage());
        }
    }

    @PostMapping(value = "/bytes-to-stream")
    public ResponseEntity<String> bytesToStream (HttpServletRequest request) throws IOException {
        File encryptedFile = new File("tmp/encrypted-stream.text");

        try (
                ServletInputStream inputStream = request.getInputStream();
                FileOutputStream fos = new FileOutputStream(encryptedFile);
                CipherOutputStream cos = new CipherOutputStream(fos, AesEncryptor.initEncryptCipher())
        ) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalRead = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                // 암호화된 조각을 바로 출력 스트림에 씀
                cos.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
            }

            cos.flush(); // 마지막 flush
            log.info("저장된 파일 경로: {}", encryptedFile.getAbsolutePath());
            log.info("저장된 파일 크기: {} bytes", encryptedFile.length());


            return ResponseEntity.ok("Encrypted and saved: " + encryptedFile.length() + " bytes");
        } catch (Exception e) {
            log.error("암호화 또는 저장 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("암호화 실패: " + e.getMessage());
        }
    }


}
