package com.dish.board.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dish.board.service.AttachFileService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/file")
@Slf4j
public class AttachFileController {
	
	@Autowired
	private AttachFileService attachFileService;
	
	@PostMapping("/save")
	public ResponseEntity<?> save(
			@RequestParam("files") List<MultipartFile> files,
			HttpServletRequest request
		) {
		try {
			attachFileService.save(files, request);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok("abc");
	}
}
