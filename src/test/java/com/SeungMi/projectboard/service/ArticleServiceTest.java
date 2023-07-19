package com.SeungMi.projectboard.service;

import com.SeungMi.projectboard.domain.Article;
import com.SeungMi.projectboard.domain.UserAccount;
import com.SeungMi.projectboard.domain.type.SearchType;
import com.SeungMi.projectboard.dto.ArticleDto;
import com.SeungMi.projectboard.dto.ArticleWithCommentsDto;
import com.SeungMi.projectboard.dto.UserAccountDto;
import com.SeungMi.projectboard.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@DisplayName("비즈니스로직 - 게시글")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks private ArticleService sut;

    @Mock private ArticleRepository articleRepository;

    @DisplayName("검색어 없이 게시글을 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void givenNoSearchParameters_whenSearchingArticles_thenReturnsArticlePage() {

        //Given
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findAll(pageable)).willReturn(Page.empty());

        //When
        Page<ArticleDto> articles = sut.searchArticles(null, null, pageable);

        //Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(pageable);
    }
    @DisplayName("검색어 없이 게시글을 해시태그 검색하면, 빈 페이지를 반환한다.")
    @Test
    void givenSearchParameters_whenSearchingArticlesViaHashtag_thenReturnsEmptyPage() {
        // Given
        Pageable pageable = Pageable.ofSize(20);
        //페이지의 사이즈를 20으로 설정하고
        // When
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(null, pageable);
        //ArticleService의 searchArticlesViaHashtag() 메서드를 통해서 검색어가 null 인 경우 일때

        // Then
        assertThat(articles).isEqualTo(Page.empty(pageable));
        //articles의 pageable이 비어있는지를 확인하고
        then(articleRepository).shouldHaveNoInteractions();
        //빈 검색어를 입력했으니 DB를 건드릴 일이 없으므로 repository에 대해 아무일도 하지 않는다.
    }

    @DisplayName("검색어와 함께 게시글을 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void givenSearchParameters_whenSearchingArticles_thenReturnsArticlePage() {
        // Given
        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByTitleContaining(searchKeyword, pageable)).willReturn(Page.empty());

        // When
        Page<ArticleDto> articles = sut.searchArticles(searchType, searchKeyword, pageable);

        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findByTitleContaining(searchKeyword, pageable);
    }

    @DisplayName("게시글을 해시태그 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void givenHashtag_whenSearchingArticlesViaHashtag_thenReturnsArticlesPage() {
        // Given
        String hashtag = "#java";
        //해시태그 내용을 하나 넣어주고
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByHashtag(hashtag, pageable)).willReturn(Page.empty(pageable));
        //이미 구현되어 있는 findByHashtag 메서드를 이용해서 페이지를 반환
        //어떤 페이지인지는 중요하지 않기 때문에 empty 페이지로 작성
        // When
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(hashtag, pageable);
        //ArticleService의 searchArticlesViaHashtag() 메서드를 통해서 검색어가 있는경우 일때

        // Then
        assertThat(articles).isEqualTo(Page.empty(pageable));
        //어떤 페이지를 반환하고 (큰 상관이 없어서 빈페이지를 반환하는 것을 확인)
        then(articleRepository).should().findByHashtag(hashtag, pageable);
        //articleRepository가 findByHashtag를 하는지를 검사
    }

    @DisplayName("게시글을 조회하면, 게시글을 반환한다.")
    @Test
    void givenArticleId_whenSearchingArticle_thenReturnsArticle() {

        //Given
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        //When
        ArticleWithCommentsDto dto = sut.getArticle(articleId);

        //Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("없는 게시글을 조회하면, 예외를 던진다.")
    @Test
    void givenNonexistentArticleId_whenSearchingArticle_thenThrowsException() {
        // Given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // When
        Throwable t = catchThrowable(() -> sut.getArticle(articleId));

        // Then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글이 없습니다 - articleId: " + articleId);
        then(articleRepository).should().findById(articleId);
    }

    @Test
    @DisplayName("게시글 정보를 입력하면, 게시글을 생성한다")
    void givenArticleInfo_whenSavingArticle_thenSavesArticle(){
        //Given
        ArticleDto dto = createArticleDto();
        given(articleRepository.save(any(Article.class))).willReturn(createArticle());

        //When
        sut.saveArticle(dto);

        //Then
        then(articleRepository).should().save(any(Article.class));
    }

    @Test
    @DisplayName("게시글의 수정 정보를 입력하면, 게시글을 수정한다.")
    void givenModifiedArticleInfo_whenUpdatingArticle_thenUpdatesArticle() {
        //Given
        Article article = createArticle();
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용", "#springboot");
        given(articleRepository.getReferenceById(dto.id())).willReturn(article);

        //When
        sut.updateArticle(dto);

        //Then
        assertThat(article)
                .hasFieldOrPropertyWithValue("title", dto.title())
                .hasFieldOrPropertyWithValue("content", dto.content())
                .hasFieldOrPropertyWithValue("hashtag", dto.hashtag());
        then(articleRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("없는 게시글의 수정 정보를 입력하면, 경고 로그를 찍고 아무 것도 하지 않는다.")
    @Test
    void givenNonexistentArticleInfo_whenUpdatingArticle_thenLogsWarningAndDoesNothing() {
        // Given
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용", "#springboot");
        given(articleRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        // When
        sut.updateArticle(dto);

        // Then
        then(articleRepository).should().getReferenceById(dto.id());
    }

    @Test
    @DisplayName("게시글의 ID를 입력하면, 게시글을 삭제한다")
    void givenArticleId_whenDeletingArticle_thenDeletesArticle(){
        //Given
        Long articleId = 1L;
        willDoNothing().given(articleRepository).deleteById(articleId);

        //When
        sut.deleteArticle(1L);

        //Then
        then(articleRepository).should().deleteById(articleId);
    }

    @DisplayName("게시글 수를 조회하면, 게시글 수를 반환한다")
    @Test
    void givenNothing_whenCountingArticles_thenReturnsArticleCount() {
        // Given
        long expected = 0L;
        given(articleRepository.count()).willReturn(expected);

        // When
        long actual = sut.getArticleCount();

        // Then
        assertThat(actual).isEqualTo(expected);
        then(articleRepository).should().count();
    }

    @DisplayName("해시태그를 조회하면, 유니크 해시태그 리스트를 반환한다")
    @Test
    void givenNothing_whenCalling_thenReturnsHashtags() {
        // Given
        List<String> expectedHashtags = List.of("#java", "#spring", "#boot");
        //원하는 모양의 해시태그 리스트를 먼저 만든다.
        given(articleRepository.findAllDistinctHashtags()).willReturn(expectedHashtags);
        //articleRepository에서 findAllDistinctHashtags() 메서드를 이용하면
        //기대하는 해시태그 리스트가 나와야 한다.

        // When
        List<String> actualHashtags = sut.getHashtags();
        //articleService의 getHashtags() 메서드를 사용할 때

        // Then
        assertThat(actualHashtags).isEqualTo(expectedHashtags);
        //기대하는 모양의 해시태그 리스트와
        //실제로 getHashtags() 메서드를 통해 얻은 해시태그 리스트가 같은지를 확인
        then(articleRepository).should().findAllDistinctHashtags();
        //articleRepository에서 findAllDistinctHashtags() 메서드를 호출했는지 확인
    }

    private UserAccount createUserAccount() {
        return UserAccount.of(
                "uno",
                "password",
                "uno@email.com",
                "Uno",
                null
        );
    }

    private Article createArticle() {
        return Article.of(
                createUserAccount(),
                "title",
                "content",
                "#java"
        );
    }

    private ArticleDto createArticleDto() {
        return createArticleDto("title", "content", "#java");
    }

    private ArticleDto createArticleDto(String title, String content, String hashtag) {
        return ArticleDto.of(1L,
                createUserAccountDto(),
                title,
                content,
                hashtag,
                LocalDateTime.now(),
                "Uno",
                LocalDateTime.now(),
                "Uno");
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                1L,
                "uno",
                "password",
                "uno@mail.com",
                "Uno",
                "This is memo",
                LocalDateTime.now(),
                "uno",
                LocalDateTime.now(),
                "uno"
        );
    }
}