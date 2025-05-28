package com.dish.board.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.dish.board.vo.AttachFileDetailVO;
import com.dish.board.vo.AttachFileMasterVO;
import com.dish.board.vo.FileDeleteRequest;

@Mapper
public interface AttachFileMapper {

	void insertFileDetail(AttachFileDetailVO vo);
	
	Optional<AttachFileDetailVO> findByFileName(String fileName);

	void insertFileMaster(AttachFileMasterVO vo);
	
	List<AttachFileDetailVO> findFilesByMasterId(Long fileMasterId);

	void deleteFile(FileDeleteRequest fileRDeleteRequest);
}