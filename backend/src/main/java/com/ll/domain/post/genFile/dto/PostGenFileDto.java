package com.ll.domain.post.genFile.dto;

import com.ll.domain.post.genFile.entity.PostGenFile;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class PostGenFileDto {
    @NonNull
    private long id;
    @NonNull
    private LocalDateTime createDate;
    @NonNull
    private LocalDateTime modifyDate;
    @NonNull
    private long postId;
    @NonNull
    private String fileName;
    @NonNull
    private PostGenFile.TypeCode typeCode;
    @NonNull
    private String fileExtTypeCode;
    @NonNull
    private String fileExtType2Code;
    @NonNull
    private long fileSize;
    @NonNull
    private long fileNo;
    @NonNull
    private String fileExt;
    @NonNull
    private String fileDateDir;
    @NonNull
    private String originalFileName;
    @NonNull
    private String downloadUrl;
    @NonNull
    private String publicUrl;

    public PostGenFileDto(PostGenFile postGenFile) {
        this.id = postGenFile.getId();
        this.createDate = postGenFile.getCreateDate();
        this.modifyDate = postGenFile.getModifyDate();
        this.postId = postGenFile.getPost().getId();
        this.fileName = postGenFile.getFileName();
        this.typeCode = postGenFile.getTypeCode();
        this.fileExtTypeCode = postGenFile.getFileExtTypeCode();
        this.fileExtType2Code = postGenFile.getFileExtType2Code();
        this.fileSize = postGenFile.getFileSize();
        this.fileNo = postGenFile.getFileNo();
        this.fileExt = postGenFile.getFileExt();
        this.fileDateDir = postGenFile.getFileDateDir();
        this.originalFileName = postGenFile.getOriginalFileName();
        this.downloadUrl = postGenFile.getDownloadUrl();
        this.publicUrl = postGenFile.getPublicUrl();
    }
}
