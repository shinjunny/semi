package com.dish.board.service;

import com.dish.board.vo.MemberVO;

public interface MemberService {

	void createMember(MemberVO member);
	
	// 아이디 중복 확인
	boolean isUserIdExists(String userId);
	
	// 로그인
	MemberVO login(MemberVO member);
	
	// 내 정보
	MemberVO findByUserId(String userId);
	
	// 회원삭제
	void deleteMember(String userId);
	
	void updateMember(MemberVO member);
}
