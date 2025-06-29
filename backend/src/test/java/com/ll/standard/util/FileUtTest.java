package com.ll.standard.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.ll.global.app.AppConfig;
import com.ll.util.Ut;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FileUtTest {
    @Test
    @DisplayName("downloadByHttp")
    void t1() {
        String newFilePath = Ut.file.downloadByHttp("https://picsum.photos/id/237/200/300", AppConfig.getTempDirPath());

        // newFilepath 의 확장자가 jpg 인지 확인
        assertThat(newFilePath).endsWith(".jpg");

        Ut.file.delete(newFilePath);
    }
}
