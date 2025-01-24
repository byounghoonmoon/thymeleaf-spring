package skcc.arch.code.infrastructure.jpa;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import skcc.arch.code.domain.Code;
import skcc.arch.code.service.dto.CodeDto;
import skcc.arch.common.infrastructure.jpa.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "codes")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class CodeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키

    @Column(nullable = false, unique = true, length = 50)
    private String code; // 고유한 코드 값

    @Column(nullable = false, length = 100)
    private String codeName; // 코드 이름

    @ManyToOne(fetch = FetchType.LAZY) // 단방향 관계: 부모 코드만 참조
    @JoinColumn(name = "parent_code_id", referencedColumnName = "id")
    private CodeEntity parentCode; // 부모 코드

    @OneToMany(mappedBy = "parentCode", cascade = CascadeType.ALL, orphanRemoval = true) // 양방향 관계
    @Builder.Default
    @BatchSize(size = 100)
    private List<CodeEntity> child = new ArrayList<>();

    @Column(nullable = false)
    private Integer seq; // 정렬 순서

    @Column(length = 255)
    private String description; // 설명

    @Column(nullable = false)
    private boolean delYn;

//    public void addChild(CodeEntity entity) {
//        child.add(entity);
//        entity.setParentCode(this);
//    }

    public static CodeEntity from(Code code, CodeEntity parentCode) {
        return CodeEntity.builder()
                .id(code.getId())
                .code(code.getCode())
                .codeName(code.getCodeName())
                .parentCode(parentCode)
                .seq(code.getSeq())
                .description(code.getDescription())
                .build();
    }

    public Code toModel() {
        return Code.builder()
                .id(id)
                .code(code)
                .codeName(codeName)
                .parentCodeId(parentCode == null ? null : parentCode.getId())
                .seq(seq)
                .description(description)
                .delYn(delYn)
                .createdDate(super.getCreatedDate())
                .lastModifiedDate(super.getLastModifiedDate())
                .build();
    }

    public CodeDto toDto() {
        return CodeDto.builder()
                .id(id)
                .code(code)
                .codeName(codeName)
                .child(child.stream().map(CodeEntity::toDto).toList())
                .parentCodeId(parentCode == null ? null : parentCode.getId())
                .seq(seq)
                .description(description)
                .delYn(delYn)
                .createdDate(super.getCreatedDate())
                .lastModifiedDate(super.getLastModifiedDate())
                .build();
    }
}
