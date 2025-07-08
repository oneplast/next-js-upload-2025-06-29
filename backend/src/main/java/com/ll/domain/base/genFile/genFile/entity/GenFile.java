package com.ll.domain.base.genFile.genFile.entity;

import com.ll.domain.post.genFile.entity.PostGenFile;
import com.ll.global.app.AppConfig;
import com.ll.global.jpa.entity.BaseTime;
import jakarta.persistence.MappedSuperclass;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
public class GenFile extends BaseTime {
    private String typeCode;
    private int fileNo;
    private String originalFileName;
    private String metadata;
    private String fileDateDir;
    private String fileExt;
    private String fileExtTypeCode;
    private String fileExtType2Code;
    private String fileName;
    private int fileSize;

    public String getFilePath() {
        return AppConfig.getGenFileDirPath() + "/" + getModelName() + "/" + typeCode + "/" + fileDateDir + "/"
               + fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (getId() != null) {
            return super.equals(o);
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        PostGenFile that = (PostGenFile) o;
        return fileNo == that.getFileNo() && Objects.equals(typeCode, that.getTypeCode());
    }

    @Override
    public int hashCode() {
        if (getId() != null) {
            return super.hashCode();
        }

        return Objects.hash(super.hashCode(), typeCode, fileNo);
    }
}
