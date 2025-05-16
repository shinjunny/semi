package com.dish.board.controller;

import com.dish.board.vo.BoardVO;

import lombok.extern.slf4j.Slf4j;

import com.dish.board.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/board")
public class BoardController {
	
	private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    // 게시글 목록
    @GetMapping
    public String list(Model model) {
        List<BoardVO> boards = boardService.getAllBoards();
        model.addAttribute("boards", boards);
        return "board/list";
    }
    
    // 게시판 유형별 목록
    @GetMapping("/type/{boardType}")
    public String listByType(@PathVariable String boardType, Model model) {
        List<BoardVO> boards = boardService.getBoardsByType(boardType);
        model.addAttribute("boards", boards);
        model.addAttribute("boardType", boardType);
        // log.info(boards.toString());       
        return "board/list";
    }

    // 게시글 상세
    @GetMapping("/type/{boardType}/{boardNum}")
    public String detail(@PathVariable String boardType,
    					 @PathVariable Long boardNum, 
    					 Model model) {
    	BoardVO board = boardService.getBoard(boardNum);
    	model.addAttribute("boardType", boardType);
    	model.addAttribute("board", board);
        return "board/detail";
    }

    // 글쓰기 폼
    @GetMapping("/type/{boardType}/write")
    public String writeForm(@PathVariable String boardType, Model model) {
        BoardVO board = new BoardVO();
        board.setBoardType(boardType); // 유형 지정
        model.addAttribute("board", board);
        model.addAttribute("boardType", boardType);
        return "board/write";
    }

    // 글 등록
    @PostMapping("/type/{boardType}/write")
    public String write(@PathVariable String boardType,
                        @ModelAttribute BoardVO board) {
        board.setBoardType(boardType); // URL에서 받은 유형 적용
        board.setCreator("admin"); // 실제 로그인 사용자 정보로 대체
        board.setModifier("admin");
        boardService.createBoard(board);
        return "redirect:/board/type/" + boardType;
    }

    // 수정 폼
    @GetMapping("/type/{boardType}/edit/{boardNum}")
    public String editForm(@PathVariable String boardType,
                           @PathVariable Long boardNum,
                           Model model) {
        BoardVO board = boardService.getBoard(boardNum);
        model.addAttribute("board", board);
        model.addAttribute("boardType", boardType);
        return "board/edit";
    }

    // 수정
    @PostMapping("/type/{boardType}/edit")
    public String edit(@PathVariable String boardType,
                       @ModelAttribute BoardVO board) {
        board.setModifier("admin"); // 실제 로그인 사용자 정보로 대체
        boardService.updateBoard(board);
        return "redirect:/board/type/" + boardType + "/" + board.getBoardNum();
    }

    // 삭제
    @GetMapping("/type/{boardType}/delete/{boardNum}")
    public String delete(@PathVariable String boardType,
                         @PathVariable Long boardNum) {
        boardService.deleteBoard(boardNum);
        return "redirect:/board/type/" + boardType;
    }
}