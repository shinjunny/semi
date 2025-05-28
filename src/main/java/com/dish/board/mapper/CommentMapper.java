package com.dish.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dish.board.vo.CommentVO;

@Mapper
public interface CommentMapper {
	
	void insert(CommentVO comment);
    List<CommentVO> selectByBoardNum(Long boardNum);
    
    CommentVO getCommentById(Long commentId);
    void deleteComment(Long commentId);
    void updateComment(CommentVO comment);
}