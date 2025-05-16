package com.dish.board.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dish.board.service.BoardService;
import com.dish.board.vo.BoardVO;

@Controller
public class HomeController {
	
	private final BoardService boardService;

    public HomeController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }
    
    @GetMapping("/search")
    public String search(@RequestParam String boardType, @RequestParam String title, Model model) {
    	List<BoardVO> boards = boardService.searchBoardsByTitle(boardType, title);

    	if (boards.isEmpty()) {
            model.addAttribute("message", "존재하지 않는 게시글입니다.");
        } else {
            model.addAttribute("boards", boards);
        }
        model.addAttribute("boardType", boardType);
        return "board/list";
    }
}