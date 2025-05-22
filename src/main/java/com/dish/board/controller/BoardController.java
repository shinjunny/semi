package com.dish.board.controller;

import com.dish.board.vo.BoardVO;
import com.dish.board.vo.MemberVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import com.dish.board.service.BoardService;
import com.dish.board.service.MemberService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/board")
public class BoardController {
	
	private final BoardService boardService;
	private final MemberService memberService;

    public BoardController(BoardService boardService, MemberService memberService) {
        this.boardService = boardService;
        this.memberService = memberService;
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
    					 @RequestParam(value = "fromInfo", required = false) boolean fromInfo,
    					 HttpServletRequest request,
    					 Model model) {
    	BoardVO board = boardService.getBoard(boardNum);
    	
    	// 로그인 사용자 정보 가져오기
    	HttpSession session = request.getSession();
    	MemberVO loginUser = (MemberVO) session.getAttribute("userInfo");
    	
    	model.addAttribute("boardType", boardType);
    	model.addAttribute("board", board);
    	model.addAttribute("infoView", fromInfo);
    	
    	// 로그인한 유저의 ID를 모델에 추가
        if (loginUser != null) {
            model.addAttribute("loginUserId", loginUser.getUserId());
        }
    	
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
                        @ModelAttribute BoardVO board,
                        HttpServletRequest request) {
    	
    	HttpSession session = request.getSession();
    	MemberVO member = (MemberVO) session.getAttribute("userInfo");
    	
        board.setBoardType(boardType);
        board.setCreator(member.getUserId());
        board.setModifier(member.getUserId());
        boardService.createBoard(board);
        return "redirect:/board/type/" + boardType;
    }

    // 수정 폼
    @GetMapping("/type/{boardType}/edit/{boardNum}")
    public String editForm(@PathVariable String boardType,
                           @PathVariable Long boardNum,
                           @RequestParam(value = "fromInfo", required = false, defaultValue = "false") boolean fromInfo,
                           Model model) {
        BoardVO board = boardService.getBoard(boardNum);
        model.addAttribute("board", board);
        model.addAttribute("boardType", boardType);
        model.addAttribute("fromInfo", fromInfo);
        return "board/edit";
    }

    // 수정
    @PostMapping("/type/{boardType}/edit")
    public String edit(@PathVariable String boardType,
                       @ModelAttribute BoardVO board,
                       @RequestParam(value = "fromInfo", required = false, defaultValue = "false") boolean fromInfo,
                       HttpSession session) {
        // 실제 로그인 사용자 정보로 수정자 설정
        MemberVO loginUser = (MemberVO) session.getAttribute("userInfo");
        if (loginUser != null) {
            board.setModifier(loginUser.getUserId());
        } else {
            board.setModifier("anonymous");
        }

        boardService.updateBoard(board);

        // info에서 왔다면 해당 유저 정보 페이지로 리다이렉트
        if (fromInfo && loginUser != null) {
            return "redirect:/member/info/" + loginUser.getUserId();
        }

        return "redirect:/board/type/" + boardType + "/" + board.getBoardNum();
    }
    
    // 삭제
    @GetMapping("/type/{boardType}/delete/{boardNum}")
    public String delete(@PathVariable String boardType,
                         @PathVariable Long boardNum,
                         @RequestParam(value = "fromInfo", required = false, defaultValue = "false") boolean fromInfo,
                         HttpSession session) {
        boardService.deleteBoard(boardNum);

        // 세션에서 userId 가져오기
        MemberVO loginUser = (MemberVO) session.getAttribute("userInfo");
        
        // info에서 왔다면 해당 유저 정보 페이지로 리다이렉트
        if (fromInfo && loginUser != null) {
            return "redirect:/member/info/" + loginUser.getUserId();
        }

        return "redirect:/board/type/" + boardType;
    }
}