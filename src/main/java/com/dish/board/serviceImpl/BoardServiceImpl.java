package com.dish.board.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.dish.board.mapper.BoardMapper;
import com.dish.board.service.BoardService;
import com.dish.board.vo.BoardVO;

@Service
public class BoardServiceImpl implements BoardService {
	
	private final BoardMapper boardMapper;

    public BoardServiceImpl(BoardMapper boardMapper) {
        this.boardMapper = boardMapper;
    }

    @Override
    public List<BoardVO> getAllBoards() {
        return boardMapper.findAll();
    }
    
    @Override
    public List<BoardVO> getBoardsByType(String boardType) {
        return boardMapper.selectBoardsByType(boardType);
    }

    @Override
    public BoardVO getBoard(Long boardNum) {
        return boardMapper.findById(boardNum);
    }

    @Override
    public void createBoard(BoardVO board) {
        boardMapper.insert(board);
    }

    @Override
    public void updateBoard(BoardVO board) {
        boardMapper.update(board);
    }

    @Override
    public void deleteBoard(Long boardNum) {
        boardMapper.delete(boardNum);
    }
    
    @Override
    public List<BoardVO> searchBoardsByTitle(String boardType, String title) {
        return boardMapper.searchBoardsByTitle(boardType, title);
    }
    
    @Override
    public List<BoardVO> getBoardsByUserId(String userId) {
        return boardMapper.selectBoardsByUserId(userId);
    }
    
    
    
    
    
    @Override
    public List<BoardVO> getBoardsByTypeWithPaging(String boardType, int limit, int offset) {
        return boardMapper.selectBoardsByTypeWithPaging(boardType, limit, offset);
    }

    @Override
    public int countBoardsByType(String boardType) {
        return boardMapper.countBoardsByType(boardType);
    }
    
    @Override
    public BoardVO getBoardByNum(int boardNum) {
        return boardMapper.selectBoardByNum(boardNum);
    }
       
    @Override
    public boolean deleteBoardByNum(int boardNum) {
        return boardMapper.deleteBoardByNum(boardNum) > 0;
    }

}