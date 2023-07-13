package com.SeungMi.projectboard.service;

import com.SeungMi.projectboard.domain.Article;
import com.SeungMi.projectboard.dto.ArticleCommentDto;
import com.SeungMi.projectboard.repository.ArticleCommentRepository;
import com.SeungMi.projectboard.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ArticleCommentService {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    @Transactional(readOnly = true)
    public List<ArticleCommentDto> searchArticleComment(long articleId) {
        return List.of();
    }

    public void saveArticleComment(ArticleCommentDto dto) {
    }
}
