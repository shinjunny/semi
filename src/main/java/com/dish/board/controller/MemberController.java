package com.dish.board.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dish.board.mapper.MemberMapper;
import com.dish.board.service.BoardService;
import com.dish.board.service.MemberService;
import com.dish.board.vo.BoardVO;
import com.dish.board.vo.MemberVO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/member")
public class MemberController {
	
	private final MemberService memberService;
	private final BoardService boardService;
ss
    public MemberController(MemberService memberService, BoardService boardService) {
    	this.memberService = memberService;
    	this.boardService = boardService;
    }
    
    // 로그인 폼
    @GetMapping("/login")
    public String loginFrom(Model model) {
    	MemberVO member = new MemberVO();   
        model.addAttribute("member", member);
    	return "member/login";
    }
    
    // 로그인
    @PostMapping("/login")
    public String login(@ModelAttribute MemberVO member, HttpSession session, Model model) {
        MemberVO loginMember = memberService.login(member);

        if (loginMember != null) {
            session.setAttribute("userInfo", loginMember);
            return "redirect:/";
        } else {
            model.addAttribute("error", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "member/login";
        }
    }
	
	// 회원가입 폼
    @GetMapping("/signup")
    public String signupForm(Model model) {
        MemberVO member = new MemberVO();   
        model.addAttribute("member", member);      
        return "member/signup";
    }

    // 회원가입
    @PostMapping("/signup")
    public String signup(@ModelAttribute MemberVO member, Model model, RedirectAttributes redirectAttributes) {
        if (memberService.isUserIdExists(member.getUserId())) {
            model.addAttribute("member", member);
            model.addAttribute("error", "이미 존재하는 아이디입니다.");
            return "member/signup";
        }

        member.setCreator(member.getUserId());
        member.setModifier(member.getUserId());

        memberService.createMember(member);
        
        redirectAttributes.addFlashAttribute("welcomeMessage", "환영합니다, " + member.getUserId() + "님!");

        return "redirect:/member/login";
    }
    
    // 아이디 중복 확인
    @GetMapping("/checkId")
    @ResponseBody
    public Map<String, Boolean> checkDuplicateId(@RequestParam String userId) {
        boolean exists = memberService.isUserIdExists(userId);
        return Collections.singletonMap("exists", exists);
    }
    
    // 내 정보
    @GetMapping("/info/{userId}")
    public String viewMyInfo(@PathVariable String userId, Model model, HttpSession session) {
        MemberVO sessionUser = (MemberVO) session.getAttribute("userInfo");

        // 다른 사용자의 정보에 접근하지 못하도록
        if (sessionUser == null || !sessionUser.getUserId().equals(userId)) {
            return "redirect:/";
        }

        MemberVO member = memberService.findByUserId(userId);        
        // 내 게시글 조회
        List<BoardVO> myBoards = boardService.getBoardsByUserId(userId);
        
        model.addAttribute("member", member);
        model.addAttribute("boards", myBoards);

        return "member/info";
    }
}
