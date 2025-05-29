package com.dish.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dish.board.vo.MemberVO;

@Mapper
public interface MemberMapper {
	
	void insert(MemberVO member);
	
	// 아이디 중복 화인
	int countByUserId(String userId);
	
	// 아이디와 비밀번호로 회원 조회
	MemberVO selectByIdAndPassword(MemberVO member);
	
	// 내 정보
	MemberVO findByUserId(String userId);
	
	// 회원삭제
	void DeleteMember(String userId);
	
	void updateMember(MemberVO member);
	
	// 비번변경
	// 비밀번호 찾기 신준 2025-05-22
	void updatePassword(@Param("userId") String userId, @Param("userPw") String userPw);
	
	List<MemberVO> selectAllMembers();
	
	MemberVO selectMemberByUserId(String userId);
	
	void deleteByUserId(String userId);
}
