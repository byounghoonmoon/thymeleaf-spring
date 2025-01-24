package skcc.arch.code.service.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import skcc.arch.code.domain.Code;
import skcc.arch.code.domain.CodeSearchCondition;
import skcc.arch.code.service.dto.CodeDto;

import java.util.Optional;

public interface CodeRepository {
    Code save(Code code);
    Optional<Code> findById(Long id);
    Optional<CodeDto> findByIdWithChild(Long id);
    Optional<Code> findByCode(String code);
    Optional<CodeDto> findByCodeWithChild(CodeSearchCondition condition);
    Page<Code> findAll(Pageable pageable);
}
