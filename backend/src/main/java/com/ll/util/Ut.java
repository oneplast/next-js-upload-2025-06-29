package com.ll.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.global.app.AppConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Objects;
import io.jsonwebtoken.security.Keys;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

public class Ut {
    public static class str {
        public static boolean isBlank(String str) {
            return str == null || str.trim().isEmpty();
        }

        public static String lcFirst(String str) {
            return Character.toLowerCase(str.charAt(0)) + str.substring(1);
        }
    }

    public static class json {
        private static final ObjectMapper om = AppConfig.getObjectMapper();

        @SneakyThrows
        public static String toString(Object obj) {
            return om.writeValueAsString(obj);
        }
    }

    public static class jwt {
        public static String toString(String secret, long expireSeconds, Map<String, Object> body) {
            Date issuedAt = new Date();
            Date expiration = new Date(issuedAt.getTime() + 1000L * expireSeconds);

            Key secretKey = Keys.hmacShaKeyFor(secret.getBytes());

            return Jwts.builder()
                    .claims(body)
                    .issuedAt(issuedAt)
                    .expiration(expiration)
                    .signWith(secretKey)
                    .compact();
        }

        public static boolean isValid(String secret, String jwtStr) {
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());
            try {
                Jwts
                        .parser()
                        .verifyWith(secretKey)
                        .build()
                        .parse(jwtStr);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        public static Map<String, Object> payload(String secret, String jwtStr) {
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());

            try {
                return (Map<String, Object>) Jwts
                        .parser()
                        .verifyWith(secretKey)
                        .build()
                        .parse(jwtStr)
                        .getPayload();
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static class file {
        private static final String ORIGINAL_FILE_NAME_SEPARATOR = "--originalFileName__";

        private static final Map<String, String> MIME_TYPE_MAP = new LinkedHashMap<>() {{
            put("application/json", "json");
            put("text/plain", "txt");
            put("text/html", "html");
            put("text/css", "css");
            put("application/javascript", "js");
            put("image/jpeg", "jpg");
            put("image/png", "png");
            put("image/gif", "gif");
            put("image/webp", "webp");
            put("image/svg+xml", "svg");
            put("application/pdf", "pdf");
            put("application/xml", "xml");
            put("application/zip", "zip");
            put("application/gzip", "gz");
            put("application/x-tar", "tar");
            put("application/x-7z-compressed", "7z");
            put("application/vnd.rar", "rar");
            put("audio/mpeg", "mp3");
            put("audio/x-m4a", "m4a");
            put("audio/mp4", "m4a");
            put("audio/wav", "wav");
            put("video/quicktime", "mov");
            put("video/mp4", "mp4");
            put("video/webm", "webm");
            put("video/x-msvideo", "avi");
        }};

        @SneakyThrows
        public static String downloadByHttp(String url, String dirPath) {
            return downloadByHttp(url, dirPath, true);
        }

        @SneakyThrows
        public static String downloadByHttp(String url, String dirPath, boolean uniqueFilename) {
            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(Redirect.ALWAYS)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            String tempFilePath = dirPath + "/" + UUID.randomUUID() + ".tmp";

            Ut.file.mkdir(dirPath);

            // 실제 파일 다운로드
            HttpResponse<Path> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofFile(Path.of(tempFilePath))
            );

            // 파일 확장자 추출
            String extension = getExtensionFromResponse(response);

            if (extension.equals("tmp")) {
                extension = getExtensionByTika(tempFilePath);
            }

            // 파일명 추출
            String filename = getFilenameWithoutFromUrl(url);

            filename = uniqueFilename
                    ? UUID.randomUUID() + ORIGINAL_FILE_NAME_SEPARATOR + filename
                    : filename;

            String newFilePath = dirPath + "/" + filename + "." + extension;

            mv(tempFilePath, newFilePath);

            return newFilePath;
        }

        public static String getExtensionByTika(String filePath) {
            String mimeType = AppConfig.getTika().detect(filePath);

            return MIME_TYPE_MAP.getOrDefault(mimeType, "tmp");
        }

        @SneakyThrows
        public static void mv(String oldFilePath, String newFilePath) {
            mkdir(Paths.get(newFilePath).getParent().toString());

            Files.move(Path.of(oldFilePath), Path.of(newFilePath), StandardCopyOption.REPLACE_EXISTING);
        }

        @SneakyThrows
        private static void mkdir(String dirPath) {
            Path path = Path.of(dirPath);

            if (Files.exists(path)) {
                return;
            }

            Files.createDirectories(path);
        }

        private static String getFilenameWithoutFromUrl(String url) {
            try {
                String path = new URI(url).getPath();
                String filename = Path.of(path).getFileName().toString();
                // 확장자 제거
                return filename.contains(".")
                        ? filename.substring(0, filename.lastIndexOf('.'))
                        : filename;
            } catch (URISyntaxException e) {
                // URL 에서 파일명을 추출할 수 없는 경우 타임스탬프 사용
                return "download_" + System.currentTimeMillis();
            }
        }

        private static String getExtensionFromResponse(HttpResponse<?> response) {
            return response.headers()
                    .firstValue("Content-Type")
                    .map(contentType -> MIME_TYPE_MAP.getOrDefault(contentType, "tmp"))
                    .orElse("tmp");
        }

        @SneakyThrows
        public static void delete(String filePath) {
            Files.deleteIfExists(Path.of(filePath));
        }

        public static String getOriginalFileName(String filePath) {
            String originalFileName = Path.of(filePath).getFileName().toString();

            return originalFileName.contains(ORIGINAL_FILE_NAME_SEPARATOR)
                    ? originalFileName.substring(
                    originalFileName.indexOf(ORIGINAL_FILE_NAME_SEPARATOR) + ORIGINAL_FILE_NAME_SEPARATOR.length())
                    : originalFileName;
        }

        public static String getFileExt(String filePath) {
            String filename = getOriginalFileName(filePath);

            return filename.contains(".")
                    ? filename.substring(filename.lastIndexOf('.') + 1)
                    : "";
        }

        @SneakyThrows
        public static int getFileSize(String filePath) {
            return (int) Files.size(Path.of(filePath));
        }

        @SneakyThrows
        public static void rm(String filePath) {
            Path path = Path.of(filePath);

            if (!Files.exists(path)) {
                return;
            }

            if (Files.isRegularFile(path)) {
                // 파일이면 바로 삭제
                Files.delete(path);
            } else {
                // 디렉터리면 내부 파일들 삭제 후 디렉터리 삭제
                Files.walkFileTree(path, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        }

        public static String getFileExtTypeCodeFromFileExt(String ext) {
            return switch (ext) {
                case "jpeg", "jpg", "gif", "png", "svg", "webp" -> "img";
                case "mp4", "avi", "mov" -> "video";
                case "mp3", "m4a" -> "audio";
                default -> "etc";
            };
        }

        public static String getFileExtType2CodeFromFileExt(String ext) {
            return switch (ext) {
                case "jpeg", "jpg" -> "jpg";
                default -> ext;
            };
        }

        public static Map<String, Object> getMetadata(String filePath) {
            String ext = getFileExt(filePath);
            String fileExtTypeCode = getFileExtTypeCodeFromFileExt(ext);

            if (fileExtTypeCode.equals("img")) {
                return getImgMetadata(filePath);
            }

            return Map.of();
        }

        private static Map<String, Object> getImgMetadata(String filePath) {
            Map<String, Object> metadata = new LinkedHashMap<>();

            try (ImageInputStream input = ImageIO.createImageInputStream(new File(filePath))) {
                Iterator<ImageReader> readers = ImageIO.getImageReaders(input);

                if (!readers.hasNext()) {
                    throw new IOException("지원되지 않는 이미지 형식: " + filePath);
                }

                ImageReader reader = readers.next();
                reader.setInput(input);

                int width = reader.getWidth(0);
                int height = reader.getHeight(0);

                metadata.put("width", width);
                metadata.put("height", height);

                reader.dispose();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return metadata;
        }

        @SneakyThrows
        public static String toFile(MultipartFile multipartFile, String dirPath) {
            if (Objects.isEmpty(multipartFile)) {
                return "";
            }

            String filePath = dirPath + "/" + UUID.randomUUID() + ORIGINAL_FILE_NAME_SEPARATOR
                              + multipartFile.getOriginalFilename();

            Ut.file.mkdir(dirPath);
            multipartFile.transferTo(new File(filePath));

            return filePath;
        }

        @SneakyThrows
        public static void copy(String filePath, String newFilePath) {
            Path target = Path.of(newFilePath);
            mkdir(target.getParent().toString());

            Files.copy(
                    Path.of(filePath),
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    public static class cmd {
        public static void runAsync(String cmd) {
            new Thread(() -> run(cmd)).start();
        }

        public static void run(String cmd) {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", cmd);
                processBuilder.inheritIO();
                Process process = processBuilder.start();
                process.waitFor(1, TimeUnit.MINUTES);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class date {
        public static String getCurrentDateFormatted(String pattern) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            return simpleDateFormat.format(new Date());
        }
    }

    public static class url {
        public static String encode(String str) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return str;
            }
        }

        public static String removeDomain(String url) {
            return url.replaceFirst("https?://[^/]+", "");
        }
    }
}
