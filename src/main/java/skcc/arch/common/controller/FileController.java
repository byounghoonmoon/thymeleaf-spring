package skcc.arch.common.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;
import skcc.arch.app.dto.ApiResponse;
import skcc.arch.app.dto.ExceptionDto;
import skcc.arch.app.file.DownloadFile;
import skcc.arch.app.file.FileService;
import skcc.arch.app.file.UploadFile;
import skcc.arch.common.controller.request.FileDownloadRequest;

import java.io.IOException;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;

    /**
     * 단일 파일 업로드 API
     * @param file 업로드할 파일
     * @param policyKey 업로드 정책 키 (예: "imagePolicy")
     * @return 업로드된 파일 정보
     */
    @PostMapping("/upload")
    public ApiResponse<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("policyKey") String policyKey) {

        UploadFile storeFile = null;
        try {
            storeFile = fileService.storeFile(file, policyKey);
        } catch (IOException e) {
            return ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionDto.builder().message("파일 저장 중 오류가 발생 하였습니다.").build() );
        }
        return ApiResponse.ok(storeFile);
    }

    /**
     * 다중 파일 업로드 API
     * @param files 업로드할 파일 리스트
     * @param policyKey 업로드 정책 키 (예: "documentPolicy")
     * @return 업로드된 파일들의 정보
     */
    @PostMapping("/upload/multiple")
    public ApiResponse<?> uploadMultipleFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("policyKey") String policyKey) {

        List<UploadFile> storedFiles = null;
        try {
            storedFiles = fileService.storeFiles(files, policyKey);
        } catch (IOException e) {
            return ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionDto.builder().message("파일 저장 중 오류가 발생 하였습니다.").build() );
        }
        return ApiResponse.ok(storedFiles);
    }

    /**
     * 파일 다운로드 API
     * @param fileDownloadRequest
     * @return 파일 Resource
     */

    @PostMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestBody FileDownloadRequest fileDownloadRequest) throws IOException {

        DownloadFile download = fileService.getDownloadFileByFilepath(fileDownloadRequest.getFilePath());
        if (download != null) {

            // URL 인코딩된 한글 파일명
            String encodedFileName = UriUtils.encode(download.getFileName(), UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.add(CONTENT_DISPOSITION, String.format("attachment; filename=%s" , encodedFileName));
            headers.add(CONTENT_TYPE, APPLICATION_OCTET_STREAM_VALUE);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(download.getResource());
        }
        return null;
    }

}
