package skcc.arch.code.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import skcc.arch.app.dto.ApiResponse;
import skcc.arch.app.dto.PageInfo;
import skcc.arch.code.controller.port.CodeServicePort;
import skcc.arch.code.controller.request.CodeCreateRequest;
import skcc.arch.code.controller.request.CodeSearchRequest;
import skcc.arch.code.controller.request.CodeUpdateRequest;
import skcc.arch.code.controller.response.CodeResponse;
import skcc.arch.code.domain.Code;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/codes")
public class CodeRestController {

    private final CodeServicePort codeServicePort;
    
    @PostMapping
    public ApiResponse<CodeResponse> createCode(@RequestBody CodeCreateRequest codeCreateRequest) {
        return ApiResponse.ok(CodeResponse.from(codeServicePort.save(codeCreateRequest)));
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ApiResponse<CodeResponse> getCode(@PathVariable Long id
            , @RequestParam(required = false, defaultValue = "false") boolean withChild) {

        if (withChild) {
            return ApiResponse.ok(CodeResponse.from(codeServicePort.findByIdWithChild(id)));
        } else {
            return ApiResponse.ok(CodeResponse.from(codeServicePort.findById(id)));
        }
    }

    // 다건 조회
    @GetMapping
    public ApiResponse<List<CodeResponse>> getCodeList(Pageable pageable, CodeSearchRequest codeSearchRequest
            , @RequestParam(required = false, defaultValue = "false") boolean withChild) {

        Page<Code> result;
        if(withChild) {
            result = codeServicePort.findByConditionWithChild(pageable, codeSearchRequest);
        }else {
            result = codeServicePort.findByCode(pageable, codeSearchRequest);
        }
        return ApiResponse.ok(
            result.getContent()
                  .stream()
                  .map(CodeResponse::from)
                  .toList(),
            PageInfo.fromPage(result)
        );
    }

    @PatchMapping
    public ApiResponse<CodeResponse> updateCode(@RequestBody @Valid CodeUpdateRequest codeUpdateRequest) {
        return ApiResponse.ok(CodeResponse.from(codeServicePort.update(codeUpdateRequest)));
    }

    @GetMapping("/cache/{parentCodeName}")
    public ApiResponse<CodeResponse> getCode(@PathVariable String parentCodeName) {
        Code result = codeServicePort.findByCode(CodeSearchRequest.builder().code(parentCodeName).build());
        return ApiResponse.ok(CodeResponse.from(result));
    }

}
