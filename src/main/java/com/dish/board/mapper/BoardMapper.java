package com.dish.board.mapper;

import java.util.List;
import java.util.Map;

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
    List<BoardVO> selectBoardsByUserId(String userId);
    
    
    List<BoardVO> selectBoardsByTypeWithPaging(@Param("boardType") String boardType,
            @Param("limit") int limit,
            @Param("offset") int offset);

    int countBoardsByType(@Param("boardType") String boardType);
    
    BoardVO selectBoardByNum(int boardNum);
    
    int deleteBoardByNum(@Param("boardNum") int boardNum);
}