package com.dish.board.service;

import java.util.List;
import com.dish.board.vo.BoardVO;

public interface BoardService {
	List<BoardVO> getAllBoards();
	List<BoardVO> getBoardsByType(String boardType);
    BoardVO getBoard(Long boardNum);
    void createBoard(BoardVO board);
    void updateBoard(BoardVO board);
    void deleteBoard(Long boardNum);
    List<BoardVO> searchBoardsByTitle(String boardType, String title);
}