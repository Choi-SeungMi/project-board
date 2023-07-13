package com.SeungMi.projectboard.service;

import com.SeungMi.projectboard.domain.Article;
import com.SeungMi.projectboard.domain.type.SearchType;
import com.SeungMi.projectboard.dto.ArticleDto;
import com.SeungMi.projectboard.dto.ArticleUpdateDto;
import com.SeungMi.projectboard.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;



@DisplayName("비즈니스로직 - 게시판")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks private ArticleService sut;

    @Mock private ArticleRepository articleRepository;

    @DisplayName("게시글을 검색하면, 게시글 리스트를 반환한다.")
    @Test
    void givenSearchParameters_whenSearchingArticles_thenReturnsArticleList() {

        //Given

        //When
        Page<ArticleDto> articles = sut.searchArticles(SearchType.TITLE, "search keyword");

        //Then
        assertThat(articles).isNotNull();
    }

    @DisplayName("게시글을 조회하면, 게시글을 반환한다.")
    @Test
    void givenArticleId_whenSearchingArticle_thenReturnsArticle() {

        //Given

        //When
        ArticleDto article = sut.searchArticle(1L);

        //Then
        assertThat(article).isNotNull();
    }

    @Test
    @DisplayName("게시글 정보를 입력하면, 게시글을 생성한다")
    void givenArticleInfo_whenSavingArticle_thenSavesArticle(){
        //Given
        given(articleRepository.save(any(Article.class))).willReturn(null);

        //When
        sut.saveArticle(ArticleDto.of(LocalDateTime.now(), "햄토리", "title", "content", "#java"));

        //Then
        then(articleRepository).should().save(any(Article.class));
    }

    @Test
    @DisplayName("게시글의 ID와 수정 정보를 입력하면, 게시글을 수정한다")
    void givenArticleIdAndModifiedInfo_whenUpdatingArticle_thenUpdatesArticle(){
        //Given
        given(articleRepository.save(any(Article.class))).willReturn(null);

        //When
        sut.updateArticle(1L, ArticleUpdateDto.of("title", "content", "#java"));

        //Then
        then(articleRepository).should().save(any(Article.class));
    }

    @Test
    @DisplayName("게시글의 ID를 입력하면, 게시글을 삭제한다")
    void givenArticleId_whenDeletingArticle_thenDeletesArticle(){
        //Given
        willDoNothing().given(articleRepository).delete(any(Article.class));

        //When
        sut.deleteArticle(1L);

        //Then
        then(articleRepository).should().delete(any(Article.class));
    }
}