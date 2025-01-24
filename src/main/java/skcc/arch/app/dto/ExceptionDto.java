package skcc.arch.app.dto;

import lombok.Getter;
import skcc.arch.app.exception.ErrorCode;

@Getter
public class ExceptionDto {

    private final Integer code;
    private final String message;

    public ExceptionDto(Exception e) {
        this.code = e.getClass().getSimpleName().hashCode();
        this.message = e.getMessage();
    }

    public ExceptionDto(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public static ExceptionDto of(ErrorCode errorCode) {
        return new ExceptionDto(errorCode);
    }

}
