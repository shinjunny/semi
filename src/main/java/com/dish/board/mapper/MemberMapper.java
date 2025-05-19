package com.dish.board.mapper;

import org.apache.ibatis.annotations.Mapper;

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

	// 탈퇴
	void DeleteMember(String userId);
}
