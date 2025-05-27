package com.dish.board.mapper;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.dish.board.vo.AttachFileDetailVO;

@Mapper
public interface AttachFileMapper {

	void insertFileDetail(AttachFileDetailVO vo);
	
	Optional<AttachFileDetailVO> findByFileName(String fileName);
}