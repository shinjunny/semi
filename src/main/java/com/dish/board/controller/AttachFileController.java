package com.dish.board.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dish.board.service.AttachFileService;
import com.dish.board.vo.AttachFileDetailVO;
import com.dish.board.vo.FileDeleteRequest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/file")
@Slf4j
public class AttachFileController {
	
	@Autowired
	private AttachFileService attachFileService;
	
	@GetMapping("/getMasterId")
	public ResponseEntity<?> getMasterId(HttpServletRequest request) {
		
		return ResponseEntity.ok(attachFileService.getMasterId(request));
	}
	
	@PostMapping("/save")
	public ResponseEntity<?> save(
			@RequestParam("files") List<MultipartFile> files,
			HttpServletRequest request,
			@RequestParam("masterId") Long masterId
		) throws IOException {
		
		return ResponseEntity.ok(attachFileService.save(files, request));
	}
	
	// api/file/view/fileName/파일명
	@GetMapping("/view/fileName/{fileName}")
	public ResponseEntity<?> viewByFileName(
			@PathVariable String fileName) throws BadRequestException {
		// DB에 row를 찾아서 객체에 담는다.
		AttachFileDetailVO vo = attachFileService.findByFileName(fileName);
		if (vo == null) return ResponseEntity.badRequest().build();
		
		// ResponseEntity에 HttpHeaders 설정하고 body에 이미지를 담는다.
		HttpHeaders headers = new HttpHeaders();
		String resultFileName = vo.getFileName();
		headers.add(
			HttpHeaders.CONTENT_DISPOSITION, 
			"attachment; fileName=\"" + 
			new String(
				resultFileName.getBytes(StandardCharsets.UTF_8),
				StandardCharsets.ISO_8859_1
			) + 
			"\""
		);
		headers.setContentType(MediaType.valueOf(vo.getFileMime()));
		
		// body에 이미지를 담을때는 찾은 객체를 디스크에서 찾는다.
		Resource resource = attachFileService.loadAsResource(vo.getFilePath());
		return ResponseEntity.ok().headers(headers).body(resource);
	}
	
	@PostMapping("/delete")
	public ResponseEntity<?> deleteFile(@RequestBody FileDeleteRequest fileDeleteRequest) {
	    log.info(fileDeleteRequest.toString());
	    attachFileService.deleteFile(fileDeleteRequest);

	    Map<String, Object> result = new HashMap<>();
	    result.put("status", "success");
	    return ResponseEntity.ok(result);
	}
}