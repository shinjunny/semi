package com.dish.board.service;

import java.util.List;

import com.dish.board.vo.CommentVO;

public interface CommentService {

	void addComment(CommentVO comment);
    List<CommentVO> getComments(Long boardNum);
    
    CommentVO getCommentById(Long commentId);
    void updateComment(CommentVO comment);
    void deleteComment(Long commentId, String userId);
}