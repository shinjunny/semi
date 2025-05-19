package com.dish.board.serviceImpl;

import org.springframework.stereotype.Service;

import com.dish.board.mapper.MemberMapper;
import com.dish.board.service.MemberService;
import com.dish.board.vo.MemberVO;

@Service
public class MemberServiceImpl implements MemberService {

	private final MemberMapper memberMapper;

	public MemberServiceImpl(MemberMapper memberMapper) {
		this.memberMapper = memberMapper;
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

}