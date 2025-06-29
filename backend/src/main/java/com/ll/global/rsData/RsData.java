package com.ll.global.rsData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ll.global.dto.Empty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.NonNull;

@JsonInclude(Include.NON_NULL)
@AllArgsConstructor
@Getter
public class RsData<T> {
    public static final RsData<Empty> OK = new RsData<>("200-1", "OK", new Empty());

    @NonNull
    private String resultCode;

    @NonNull
    private String msg;

    @NonNull
    private T data;

    public RsData(String resultCode, String msg) {
        this(resultCode, msg, (T) new Empty());
    }

    @JsonIgnore
    public int getStatusCode() {
        return Integer.parseInt(resultCode.split("-")[0]);
    }

    @JsonIgnore
    public boolean isSuccess() {
        return getStatusCode() < 400;
    }

    @JsonIgnore
    public boolean isFail() {
        return !isSuccess();
    }
}
