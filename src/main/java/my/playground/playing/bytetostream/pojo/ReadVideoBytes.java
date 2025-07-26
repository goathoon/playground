package my.playground.playing.bytetostream.pojo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class ReadVideoBytes {
    public static void main(String[] args) throws Exception {
        Path path = Paths.get("2point7mega.mp4");

        // 원본 바이트
        byte[] videoBytes = Files.readAllBytes(path);
        int originalSize = videoBytes.length;

        // Base64 인코딩
        String base64Encoded = Base64.getEncoder().encodeToString(videoBytes);
        int base64SizeInBytes = base64Encoded.getBytes("UTF-8").length;

        // 출력
        System.out.println("원본 파일 크기: " + originalSize + " bytes");
        System.out.println("Base64 인코딩 크기: " + base64SizeInBytes + " bytes");

        double ratio = (double) base64SizeInBytes / originalSize;
        System.out.printf("⚠증가 비율: %.2fx (%.2f%% 증가)%n", ratio, (ratio - 1) * 100);
        Thread.sleep(10000000);
    }
}
