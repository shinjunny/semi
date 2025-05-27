package com.dish.board.controller;

import com.dish.board.service.AttachFileService;
import com.dish.board.vo.AttachFileDetailVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final AttachFileService attachFileService;

    // 📌 파일 미리보기 또는 다운로드 처리
    @GetMapping("/upload/{year}/{month}/{day}/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String year,
            @PathVariable String month,
            @PathVariable String day,
            @PathVariable String fileName,
            HttpServletRequest request) throws Exception {

        // 실제 저장된 파일 경로
        String relativePath = year + "/" + month + "/" + day + "/" + fileName;

        // DB에서 파일 정보 가져오기
        AttachFileDetailVO fileInfo = attachFileService.findByFileName(fileName);

        // 실제 파일 로드
        Resource resource = attachFileService.loadAsResource(fileInfo.getFilePath());

        // 파일이 없다면 null 처리
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        // 다운로드 응답
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileInfo.getOrgFileName() + "\"")
                .body(resource);
    }
}
