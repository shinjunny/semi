package com.dish.board.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dish.board.mapper.AttachFileMapper;
import com.dish.util.UploadFileUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AttachFileService {
	
	private final Path rootLocation;
	private final AttachFileMapper attachFileMapper;
	
	/**
	 * Bean으로 설정한 uploadPath 값을 rootLocation에 대입한다.
	 * @param uploadPath
	 * @param attachFileMapper
	 */
	public AttachFileService(String uploadPath, AttachFileMapper attachFileMapper) {
		this.rootLocation = Paths.get(uploadPath);
		this.attachFileMapper = attachFileMapper;
	}
	
	public void save(
			List<MultipartFile> files, 
			HttpServletRequest request) throws IOException {
		
		for (MultipartFile file : files) {
			// 1. 파일을 디스크에 일정한 형식으로 저장
			UploadFileUtil.fileSave(rootLocation.toString(), file);
		}
				
		for (MultipartFile item : files) {
			log.info(item.getContentType());
			log.info(item.getOriginalFilename());
			log.info(String.valueOf(item.getSize()));
		}
	}
}
