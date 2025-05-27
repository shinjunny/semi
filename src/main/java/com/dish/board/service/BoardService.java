package com.dish.board.service;

import java.util.List;
import java.util.Map;

import com.dish.board.vo.BoardVO;

public interface BoardService {
	List<BoardVO> getAllBoards();
	List<BoardVO> getBoardsByType(String boardType);
    BoardVO getBoard(Long boardNum);
    void createBoard(BoardVO board);
    void updateBoard(BoardVO board);
    void deleteBoard(Long boardNum);
    List<BoardVO> searchBoardsByTitle(String boardType, String title);
    List<BoardVO> getBoardsByUserId(String userId);
    
    
    List<BoardVO> getBoardsByTypeWithPaging(String boardType, int limit, int offset);
    int countBoardsByType(String boardType);
}