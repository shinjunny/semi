package com.dish.board.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dish.board.mapper.CommentMapper;
import com.dish.board.service.CommentService;
import com.dish.board.vo.CommentVO;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    @Override
    public void addComment(CommentVO comment) {
        commentMapper.insert(comment);
    }

    @Override
    public List<CommentVO> getComments(Long boardNum) {
        return commentMapper.selectByBoardNum(boardNum);
    }
    
    @Override
    public CommentVO getCommentById(Long commentId) {
        return commentMapper.getCommentById(commentId);
    }
    
    @Override
    public void updateComment(CommentVO comment) {
    	commentMapper.updateComment(comment);
    }
    
    @Override
    public void deleteComment(Long commentId, String userId) {
        CommentVO comment = commentMapper.getCommentById(commentId);
        if (comment != null && comment.getWriter().equals(userId)) {
        	commentMapper.deleteComment(commentId);
        }
    }
}