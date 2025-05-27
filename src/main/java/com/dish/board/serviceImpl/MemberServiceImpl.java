package com.dish.board.serviceImpl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.dish.board.mapper.MemberMapper;
import com.dish.board.service.MemberService;
import com.dish.board.vo.MemberVO;

@Service
public class MemberServiceImpl implements MemberService {

	private final MemberMapper memberMapper;
	private final JavaMailSender mailSender;

    public MemberServiceImpl(MemberMapper memberMapper, JavaMailSender mailSender) {
        this.memberMapper = memberMapper;
        this.mailSender = mailSender;
    }
    
    @Override
    public void createMember(MemberVO member) {
    	memberMapper.insert(member);
    }
    
    @Override
    public boolean isUserIdExists(String userId) {
        return memberMapper.countByUserId(userId) > 0;
    }

	@Override
	public MemberVO login(MemberVO member) {
		return memberMapper.selectByIdAndPassword(member);
	}
	
	// 내 정보
	@Override
    public MemberVO findByUserId(String userId) {
        return memberMapper.findByUserId(userId);
    }
	
	// 회원삭제
	@Override
	public void deleteMember(String userId) {
		memberMapper.DeleteMember(userId);
	}
	
	@Override
    public void updateMember(MemberVO member) {
        memberMapper.updateMember(member);
    }
	
	// 비밀번호 찾기 신준 2025-05-22
	@Override
	public void sendTempPassword(String to, String tempPassword) {
		String subject = "임시 비밀번호 발급";
		String content = "임시 비밀번호는 다음과 같습니다: " + tempPassword;

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(content);

		mailSender.send(message);
	}

	// 비밀번호 찾기 신준 2025-05-22
	@Override
	public void updatePassword(String userId, String userPw) {
		memberMapper.updatePassword(userId, userPw);
	}
}