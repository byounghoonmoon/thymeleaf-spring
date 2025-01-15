package skcc.arch.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import skcc.arch.app.exception.CustomException;

@Builder
@Getter
public class ApiResponse<T> {

    @JsonIgnore
    private HttpStatus status;
    private boolean success;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ExceptionDto error;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PageInfo pageInfo;

    public static <T> ApiResponse<T> ok(T data){
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK)
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> ok(T data, PageInfo pageInfo){
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK)
                .success(true)
                .data(data)
                .pageInfo(pageInfo)
                .build();
    }

    public static <T> ApiResponse<T> fail(final CustomException e){
        return ApiResponse.<T>builder()
                .status(e.getErrorCode().getStatus())
                .success(false)
                .error(ExceptionDto.of(e.getErrorCode()))
                .build();
    }

}
