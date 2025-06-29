package com.dish.board.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
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

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/member")
public class MemberController {
	
	private final MemberService memberService;
	private final BoardService boardService;

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
        
        // 관리자 계정 체크
    	String userId = member.getUserId();
        String userPw = member.getUserPw();
        
        if ("scott".equals(userId) && "tiger".equals(userPw)) {
            session.setAttribute("admin", true);
            return "redirect:/admin/dashboard";
        }

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
    
    @PostMapping("/delete")
    public String deleteMember(HttpSession session, RedirectAttributes redirectAttributes) {
       MemberVO sessionUser = (MemberVO) session.getAttribute("userInfo");
       if (sessionUser == null) {
          return "redirect:/member/login";
       }

       String userId = sessionUser.getUserId();

       try {
          memberService.deleteMember(userId);
          session.invalidate(); // 세션 종료 (로그아웃 처리)
          redirectAttributes.addFlashAttribute("message", "회원 탈퇴가 완료되었습니다.");
       } catch (Exception e) {
          redirectAttributes.addFlashAttribute("error", "탈퇴 처리 중 오류가 발생했습니다.");
          return "redirect:/member/info/" + userId;
       }

       return "redirect:/";
    }
    
    @GetMapping("/info/{userId}/edit")
    public String editFormFromInfo(@PathVariable String userId, HttpSession session, Model model) {
        MemberVO sessionUser = (MemberVO) session.getAttribute("userInfo");

        if (sessionUser == null || !sessionUser.getUserId().equals(userId)) {
            return "redirect:/";
        }

        MemberVO member = memberService.findByUserId(userId);
        model.addAttribute("member", member);

        return "member/editInfo";
    }
    
    @PostMapping("/info/{userId}/edit")
    public String updateMemberInfo(@PathVariable String userId,
                                    @ModelAttribute MemberVO updatedMember,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        MemberVO sessionUser = (MemberVO) session.getAttribute("userInfo");

        if (sessionUser == null || !sessionUser.getUserId().equals(userId)) {
            return "redirect:/";
        }

        try {
            updatedMember.setUserId(userId);
            updatedMember.setModifier(userId);

            memberService.updateMember(updatedMember);

            // 세션 정보도 업데이트
            session.setAttribute("userInfo", updatedMember);

            redirectAttributes.addFlashAttribute("message", "정보가 성공적으로 수정되었습니다.");
            return "redirect:/member/info/" + userId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "정보 수정 중 오류가 발생했습니다.");
            return "redirect:/member/info/" + userId + "/edit";
        }
    }
    
    @PostMapping("/password-reset-direct")
	public ResponseEntity<String> resetPasswordDirectly(@RequestParam String userId) {
		MemberVO member = memberService.findByUserId(userId);
		if (member == null) {
			return ResponseEntity.badRequest().body("존재하지 않는 사용자입니다.");
		}

		// 임시 비밀번호 생성
		String tempPassword = UUID.randomUUID().toString().substring(0, 8);

		// 비밀번호 암호화 후 DB에 저장
		memberService.updatePassword(userId, tempPassword);

		// 이메일 전송
		memberService.sendTempPassword(member.getEmail(), tempPassword);

		return ResponseEntity.ok("임시 비밀번호가 이메일로 전송되었습니다.");
	}

	// 비밀번호 찾기 신준 2025-05-22
	@GetMapping("/find-password")
	public String showFindPasswordPage() {
		return "member/find-password";
	}

	// 비밀번호 찾기 신준 2025-05-22
	@PostMapping("/find-password")
	public String findPassword(@RequestParam("userId") String userId, @RequestParam("email") String email,
			Model model) {

		log.info(email);
		log.info(userId);

		MemberVO member = memberService.findByUserId(userId);

		if (member == null || !member.getEmail().equals(email)) {
			model.addAttribute("error", "아이디 또는 이메일이 일치하지 않습니다.");
			return "member/find-password";
		}

		// 임시 비밀번호 생성
		String tempPassword = UUID.randomUUID().toString().substring(0, 8);
		memberService.updatePassword(userId, tempPassword); // DB에 반영
		memberService.sendTempPassword(email, tempPassword); // 이메일 전송

		model.addAttribute("message", "임시 비밀번호가 이메일로 전송되었습니다.");
		return "member/find-password";
	}

	// 비밀번호 재설정 신준 2025-05-26
	@GetMapping("/update-password")
	public String showUpdatePasswordPage(@RequestParam(required = false) String userId, Model model) {
		model.addAttribute("userId", userId);
		return "member/update-password"; // templates/member/updatePassword.html
	}

	@PostMapping("/update-password")
	public String updatePassword(@RequestParam String userId, @RequestParam String newPassword, Model model) {
		try {
			memberService.updatePassword(userId, newPassword);
			// model.addAttribute("message", "비밀번호가 성공적으로 변경되었습니다.");
			return "redirect:/member/update-password?success=true"; 
		} catch (Exception e) {
			// model.addAttribute("message", "비밀번호 변경 중 오류 발생.");
			return "redirect:/member/update-password?success=false";
		}
		// return "member/update-password";
	}
}
