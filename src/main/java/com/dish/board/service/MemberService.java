package com.dish.board.service;

import org.apache.ibatis.annotations.Param;

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
	
	// 임시비번
	// 비밀번호 찾기 신준 2025-05-22
	void sendTempPassword(String to, String tempPassword);

	// 비번재설정
	// 비밀번호 찾기 신준 2025-05-22
	void updatePassword(@Param("userId") String userId, @Param("userPw") String userPw);
}
