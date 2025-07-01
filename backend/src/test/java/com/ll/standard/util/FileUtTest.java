package com.ll.standard.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.ll.global.app.AppConfig;
import com.ll.util.Ut;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class FileUtTest {
    @Test
    @DisplayName("downloadByHttp")
    void t1() {
        String newFilePath = Ut.file.downloadByHttp("https://picsum.photos/id/237/200/300", AppConfig.getTempDirPath(),
                false);

        // newFilepath 의 확장자가 jpg 인지 확인
        assertThat(newFilePath).endsWith(".jpg");

        Ut.file.delete(newFilePath);
    }

    @Test
    @DisplayName("getExtensionByTika")
    void t2() {
        String newFilePath = Ut.file.downloadByHttp("https://picsum.photos/id/237/200/300", AppConfig.getTempDirPath(),
                false);

        String ext = Ut.file.getExtensionByTika(newFilePath);
        assertThat(ext).isEqualTo("jpg");

        Ut.file.delete(newFilePath);
    }
}
