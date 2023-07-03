package SeungMiprojectboard.domain;

import java.time.LocalDateTime;

//댓글
public class ArticleComment {
    private Long id;            //댓글 ID
    private Article article;    //게시글 (ID)
    private String content;     //댓글 내용

    //공통 메타데이터
    private LocalDateTime createdAt;    //댓글 작성일
    private String createdBy;           //댓글 작성자
    private LocalDateTime modifiedAt;   //댓글 수정일
    private String modifiedBy;          //댓글 수정자
}
