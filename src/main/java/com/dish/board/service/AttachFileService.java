package com.dish.board.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dish.board.mapper.AttachFileMapper;
import com.dish.board.vo.AttachFileDetailVO;
import com.dish.board.vo.AttachFileMasterVO;
import com.dish.board.vo.FileDeleteRequest;
import com.dish.board.vo.MemberVO;
import com.dish.util.UploadFileUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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

	public List<AttachFileDetailVO> save(
			List<MultipartFile> files, 
			HttpServletRequest request,
			Long masterId) throws IOException {
		
		List<AttachFileDetailVO> resultList = new ArrayList<>();
		
		for (MultipartFile file : files) {
			// 1. 파일을 디스크에 일정한 형식으로 저장
			String saveFileName = UploadFileUtil.fileSave(rootLocation.toString(), file);
			log.info(saveFileName);
			
			// 파일 경로 + 파일 이름 / 기준으로 배열 분리
			String[] saveFileNameArray = saveFileName.split("/");
			StringBuffer fileDirString = new StringBuffer();
			
			// 파일 경로만 별도 String으로 분리
			for (int i = 0; i < saveFileNameArray.length; i++) {
				if (i < saveFileNameArray.length - 1) {
					fileDirString.append(saveFileNameArray[i]).append("/");
				}
			}
			
			// 파일명 끝에 /로 끝나면 해당 글자 제거
			if (saveFileName.toCharArray()[0] == '/') {
				saveFileName = saveFileName.substring(1);
			}
			
			// 작성자 ID를 위해 session 값 호출
			HttpSession session = request.getSession();
			MemberVO memberVO = (MemberVO) session.getAttribute("userInfo");
			
			// VO 생성
			AttachFileDetailVO vo = AttachFileDetailVO.builder()
					.fileMasterId(masterId)
					.fileName(saveFileNameArray[saveFileNameArray.length - 1])
					.filePath(rootLocation.toString()
							.replace(File.separatorChar, '/') + 
							File.separator + saveFileName)
					.orgFileName(file.getOriginalFilename())
					.fileExt(UploadFileUtil.getExtension(saveFileName))
					.fileSize(file.getSize())
					.fileMime(UploadFileUtil.getFileMimeType(saveFileName))
					.creator(memberVO.getUserId())
					.modifier(memberVO.getUserId())
					.build();
			log.info(vo.toString());
			
			// DB에 파일 정보 기록
			attachFileMapper.insertFileDetail(vo);
			
			resultList.add(vo);
		}
		
		return resultList;
				
	}
	
	/**
	 * 파일 이름으로 데이터 찾기
	 * @param fileName
	 * @return
	 * @throws BadRequestException 
	 */
	public AttachFileDetailVO findByFileName(String fileName) throws BadRequestException {
		return attachFileMapper.findByFileName(fileName)
				.orElseThrow(() -> new BadRequestException("파일을 찾을 수 없습니다."));

	}

	public Resource loadAsResource(String filePath) {
		
		// 정규식으로 upload\\ 이후 경로만 추출
		String result = filePath.replaceFirst(".*?upload\\\\", "");
		
		Path file = this.rootLocation.resolve(result);
		log.info(file.toUri().toString());
		try {
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public List<AttachFileDetailVO> findFilesByMasterId(Long masterId) {
		
		return attachFileMapper.findFilesByMasterId(masterId);
	}
	
	/**
	 * 첨부파일 마스터 테이블에 row 신규 생성
	 * @param request
	 * @return
	 */
	public AttachFileMasterVO getMasterId(HttpServletRequest request) {
		
		AttachFileMasterVO vo = new AttachFileMasterVO();
		HttpSession session = request.getSession();
        MemberVO memberVO = (MemberVO) session.getAttribute("userInfo");
		vo.setCreator(memberVO.getUserName());
		attachFileMapper.insertFileMaster(vo);
		return vo;
	}
	
	public void deleteFile(FileDeleteRequest fileRDeleteRequest) {
		
		attachFileMapper.deleteFile(fileRDeleteRequest);
	}
}