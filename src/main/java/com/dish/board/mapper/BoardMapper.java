package com.dish.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dish.board.vo.BoardVO;

@Mapper
public interface BoardMapper {
	List<BoardVO> findAll();
	List<BoardVO> selectBoardsByType(String boardType);
	BoardVO findById(Long boardNum);
    void insert(BoardVO board);
    void update(BoardVO board);
    void delete(Long boardNum);
    List<BoardVO> searchBoardsByTitle(@Param("boardType") String boardType, @Param("title") String title);
}