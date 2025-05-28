package com.dish.board.controller;

import com.dish.board.vo.BoardVO;
import com.dish.board.vo.CommentVO;
import com.dish.board.vo.MemberVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import com.dish.board.service.BoardService;
import com.dish.board.service.CommentService;
import com.dish.board.service.MemberService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/board")
public class BoardController {
	
	private final BoardService boardService;
	private final MemberService memberService;
	private final CommentService commentService;

    public BoardController(BoardService boardService, MemberService memberService, CommentService commentService) {
        this.boardService = boardService;
        this.memberService = memberService;
        this.commentService = commentService;
    }

    // 게시글 목록
    @GetMapping
    public String list(Model model) {
        List<BoardVO> boards = boardService.getAllBoards();
        model.addAttribute("boards", boards);
        return "board/list";
    }
    
//    // 게시판 유형별 목록
//    @GetMapping("/type/{boardType}")
//    public String listByType(@PathVariable String boardType, Model model) {
//        List<BoardVO> boards = boardService.getBoardsByType(boardType);
//        model.addAttribute("boards", boards);
//        model.addAttribute("boardType", boardType);
//        // log.info(boards.toString());   
//        return "board/list";
//    }
    @GetMapping("/type/{boardType}")
    public String listByType(
        @PathVariable String boardType,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        Model model) {

        if ("05".equals(boardType)) {
            List<BoardVO> lunchMenus = boardService.getBoardsByType(boardType);
            model.addAttribute("recommendations", lunchMenus);
            return "board/recommend"; // → recommend.html
        }

        int offset = (page - 1) * size;
        int limit = size;

        // 서비스 호출 시 인자 3개로 변경
        List<BoardVO> boards = boardService.getBoardsByTypeWithPaging(boardType, limit, offset);

        int totalCount = boardService.countBoardsByType(boardType);

        model.addAttribute("boards", boards);
        model.addAttribute("boardType", boardType);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalCount", totalCount);
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
    	
    	// 댓글 목록 조회 2025-05-23 10:30
        List<CommentVO> comments = commentService.getComments(boardNum);
        
        log.info(fromInfo ? "1111" : "222222");
    	model.addAttribute("boardType", boardType);
    	model.addAttribute("board", board);
    	model.addAttribute("infoView", fromInfo ? "t": "f");
    	model.addAttribute("comments", comments);
    	
    	// 로그인한 유저의 ID를 모델에 추가
        if (loginUser != null) {
            model.addAttribute("loginUserId", loginUser.getUserId());
        }
        
        return "board/detail";
    }
    
    // 댓글 작성
    @PostMapping("/type/{boardType}/{boardNum}/comment")
    public String writeComment(@PathVariable String boardType,
                               @PathVariable Long boardNum,
                               @ModelAttribute CommentVO comment,
                               @RequestParam(value = "fromInfo", defaultValue = "f") String fromInfo,
                               HttpSession session) {

        MemberVO loginUser = (MemberVO) session.getAttribute("userInfo");

        log.info(fromInfo);
        if (loginUser != null) {
            comment.setBoardNum(boardNum);
            comment.setWriter(loginUser.getUserId());
            commentService.addComment(comment);
        }
        
        fromInfo = fromInfo.equals("t") ? "?fromInfo=true" : "";

        return "redirect:/board/type/" + boardType + "/" + boardNum + fromInfo;
    }
    
    // 댓글 수정 폼
    @GetMapping("/type/{boardType}/{boardNum}/comment/edit/{commentId}")
    public String editCommentForm(@PathVariable String boardType,
                                  @PathVariable Long boardNum,
                                  @PathVariable Long commentId,
                                  @RequestParam(value = "fromInfo", defaultValue = "f") String fromInfo,
                                  Model model,
                                  HttpSession session) {
        MemberVO loginUser = (MemberVO) session.getAttribute("userInfo");
        CommentVO comment = commentService.getCommentById(commentId);

        if (comment != null && loginUser.getUserId().equals(comment.getWriter())) {
            model.addAttribute("comment", comment);
            model.addAttribute("boardType", boardType);
            model.addAttribute("boardNum", boardNum);
            model.addAttribute("fromInfo", fromInfo);
            return "member/editcomment";
        }
        
        fromInfo = fromInfo.equals("t") ? "?fromInfo=true" : "";

        return "redirect:/board/type/" + boardType + "/" + boardNum + fromInfo;
    }

    // 댓글 수정 처리
    @PostMapping("/type/{boardType}/{boardNum}/comment/edit")
    public String editComment(@PathVariable String boardType,
                              @PathVariable Long boardNum,
                              @ModelAttribute CommentVO comment,
                              @RequestParam(value = "fromInfo", defaultValue = "f") String fromInfo,
                              HttpSession session) {
        MemberVO loginUser = (MemberVO) session.getAttribute("userInfo");

        if (loginUser != null && loginUser.getUserId().equals(comment.getWriter())) {
            commentService.updateComment(comment);
        }
        
        fromInfo = fromInfo.equals("t") ? "?fromInfo=true" : "";

        return "redirect:/board/type/" + boardType + "/" + boardNum + fromInfo;
    }
    
    // 댓글 삭제
    @GetMapping("/type/{boardType}/{boardNum}/comment/delete/{commentId}")
    public String deleteComment(@PathVariable String boardType,
                                @PathVariable Long boardNum,
                                @PathVariable Long commentId,
                                @RequestParam(value = "fromInfo", defaultValue = "f") String fromInfo,
                                Model model,
                                HttpSession session) {
        MemberVO loginUser = (MemberVO) session.getAttribute("userInfo");
        CommentVO comment = commentService.getCommentById(commentId);
        
        if (comment != null && loginUser.getUserId().equals(comment.getWriter())) {
            model.addAttribute("fromInfo", fromInfo);
        }
        
        if (loginUser != null) {
            commentService.deleteComment(commentId, loginUser.getUserId());
        }
        
        fromInfo = fromInfo.equals("t") ? "?fromInfo=true" : "";

        return "redirect:/board/type/" + boardType + "/" + boardNum + fromInfo;
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
                           @RequestParam(value = "fromInfo", required = false, defaultValue = "f") String fromInfo,
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
                       @RequestParam(value = "fromInfo", required = false, defaultValue = "f") String fromInfo,
                       HttpSession session) {
        MemberVO loginUser = (MemberVO) session.getAttribute("userInfo");
        if (loginUser != null) {
            board.setModifier(loginUser.getUserId());
        } else {
            board.setModifier("anonymous");
        }

        boardService.updateBoard(board);

        if (fromInfo.equals("t") && loginUser != null) {
            return "redirect:/member/info/" + loginUser.getUserId();
        }

        return "redirect:/board/type/" + boardType + "/" + board.getBoardNum();
    }

    // 삭제
    @GetMapping("/type/{boardType}/delete/{boardNum}")
    public String delete(@PathVariable String boardType,
                         @PathVariable Long boardNum,
                         @RequestParam(value = "fromInfo", required = false, defaultValue = "f") String fromInfo,
                         HttpSession session) {
        boardService.deleteBoard(boardNum);
        MemberVO loginUser = (MemberVO) session.getAttribute("userInfo");

        if (fromInfo.equals("t") && loginUser != null) {
            return "redirect:/member/info/" + loginUser.getUserId();
        }

        return "redirect:/board/type/" + boardType;
    }

}