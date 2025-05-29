package com.dish.board.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dish.board.service.BoardService;
import com.dish.board.service.MemberService;
import com.dish.board.vo.BoardVO;
import com.dish.board.vo.MemberVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final MemberService memberService;
    private final BoardService boardService;

    public AdminController(MemberService memberService, BoardService boardService) {
        this.memberService = memberService;
        this.boardService = boardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(
            HttpSession session,
            Model model,
            @RequestParam(value = "userId", required = false) String userId) {

        Boolean isAdmin = (Boolean) session.getAttribute("admin");
        if (isAdmin == null || !isAdmin) {
            return "redirect:/member/admin/login";
        }

        // 전체 회원 목록은 항상 모델에 추가
        List<MemberVO> allMembers = memberService.getAllMembers();
        model.addAttribute("allMembers", allMembers);

        if (userId == null || userId.trim().isEmpty()) {
            model.addAttribute("members", List.of());
            model.addAttribute("memberBoardsMap", Map.of());
            model.addAttribute("searchUserId", "");
            return "admin/dashboard";
        }

        MemberVO member = memberService.getMemberByUserId(userId.trim());
        if (member == null) {
            model.addAttribute("members", List.of());
            model.addAttribute("memberBoardsMap", Map.of());
            model.addAttribute("notFound", true);
            model.addAttribute("searchUserId", userId.trim());
            return "admin/dashboard";
        }

        List<BoardVO> boards = boardService.getBoardsByUserId(userId.trim());
        model.addAttribute("members", List.of(member));
        model.addAttribute("memberBoardsMap", Map.of(userId.trim(), boards));
        model.addAttribute("searchUserId", userId.trim());

        return "admin/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("admin");
        return "redirect:/";
    }
    
    // 회원 삭제
    @PostMapping("/deleteMember")
    public String deleteMember(@RequestParam("userId") String userId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        Boolean isAdmin = (Boolean) session.getAttribute("admin");
        if (isAdmin == null || !isAdmin) {
            return "redirect:/member/admin/login";
        }

        boolean deleted = memberService.deleteMemberByUserId(userId);
        if (deleted) {
            redirectAttributes.addFlashAttribute("message", "회원이 삭제되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "회원 삭제에 실패했습니다.");
        }

        return "redirect:/admin/dashboard";
    }
    
    // 회원 상세 정보
    @GetMapping("/memberInfo")
    public String memberInfo(@RequestParam("userId") String userId,
                             HttpSession session,
                             Model model) {

        Boolean isAdmin = (Boolean) session.getAttribute("admin");
        if (isAdmin == null || !isAdmin) {
            return "redirect:/member/admin/login";
        }

        MemberVO member = memberService.getMemberByUserId(userId);
        if (member == null) {
            model.addAttribute("error", "해당 회원을 찾을 수 없습니다.");
            return "admin/adminuserinfo";
        }

        model.addAttribute("member", member);
        return "admin/adminuserinfo";
    }
    
    // 작성자 boardDetail
    @GetMapping("/boardDetail")
    public String boardDetail(@RequestParam("boardNum") int boardNum, Model model, HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("admin");
        if (isAdmin == null || !isAdmin) {
            return "redirect:/member/admin/login";
        }

        BoardVO board = boardService.getBoardByNum(boardNum);
        if (board == null) {
            model.addAttribute("error", "해당 게시글을 찾을 수 없습니다.");
            return "admin/adminboarddetail";
        }

        model.addAttribute("board", board);
        return "admin/adminboarddetail";
    }
    
    // 상세 게시물에서 삭제
    @DeleteMapping("/deleteBoard/{boardNum}")
    @ResponseBody
    public ResponseEntity<?> deleteBoard(@PathVariable int boardNum, HttpSession session) {	
        Boolean isAdmin = (Boolean) session.getAttribute("admin");
        
        if (isAdmin == null || !isAdmin) {
            return ResponseEntity.status(401).build();
        }

        boolean deleted = boardService.deleteBoardByNum(boardNum);
        
        return ResponseEntity.ok(Map.of("success", deleted));
    }
}