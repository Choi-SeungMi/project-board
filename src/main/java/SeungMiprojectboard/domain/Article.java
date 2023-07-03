package SeungMiprojectboard.domain;

import java.time.LocalDateTime;

//게시글
public class Article {
    private Long id;        //게시글 ID
    private String title;   //게시글 제목
    private String content; //게시글 내용
    private String hashtag; //게시글 해시태그

    //공통 메타데이터
    private LocalDateTime createdAt;    //게시글 작성일
    private String createdBy;           //게시글 작성자
    private LocalDateTime modifiedAt;   //게시글 수정일
    private String modifiedBy;          //게시글 수정자
}
