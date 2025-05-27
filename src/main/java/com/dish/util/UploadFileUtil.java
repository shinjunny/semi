package com.dish.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UploadFileUtil {
	
	/**
	 * 파일을 저장한다.
	 * @param uploadPath
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String fileSave(String uploadPath,
			MultipartFile file) throws IOException {
		
		if (file.getInputStream() != null) {
			// 파일 경로 체크
			File uploadPathDir = new File(uploadPath);
			// 파일의 경로가 없을 경우 경로를 생성한다.
			if (!uploadPathDir.exists()) uploadPathDir.mkdirs();
			
			// abc.jpg >> 9sc-sdsadsa2aa44 >> 9sc-sdsadsa2aa44.jpg
			// 파일명을 랜덤하게 변경 (중복 방지)
			String genId = UUID.randomUUID().toString();
			log.info("생성된 파일명 : {}", genId);
			// 원본 파일명
			String originalFileName = file.getOriginalFilename();
			// 확장자 추출
			String fileExtension = getExtension(originalFileName);
			log.info("확장자 : {}", fileExtension);
			// 파일명 병합
			String saveFileName = genId + "." + fileExtension;
			log.info("최종 파일명 : {}", saveFileName);
			
			// 파일의 저장 경로 생성 후 파일 저장
			String savePath = calcPath(uploadPath);
			
			// 파일 복사
			File target = new File(uploadPath + savePath, saveFileName);
			FileCopyUtils.copy(file.getBytes(), target);
			
			// 리턴 남았다.
			// /2025/05/21/asffsafsafsa.ext
			String result = makeFilePath(uploadPath, savePath, saveFileName);
			return result;
		} else {
			throw new BadRequestException("파일을 업로드 할 수 없습니다.");
		}
	}
	
	/**
	 * 파일 경로 문자열 생성
	 * @param uploadPath
	 * @param path
	 * @param fileName
	 * @return
	 */
	public static String makeFilePath(
			String uploadPath, 
			String path, 
			String fileName) {
		
		// C:\\WAS_DATA\\upload \2025/05/21/파일명
		String filePath = uploadPath + path + File.separator + fileName;
		return filePath.substring(uploadPath.length()).replace(File.separatorChar, '/');
	}
	
	/**
	 * 폴더명을 생성한다.
	 * yyyy > mm > dd 기준으로 생성
	 * @param uploadPath
	 * @return
	 */
	public static String calcPath(String uploadPath) {
		// 연월일은 날짜 관련 클래스를 사용한다.
		Calendar cal = Calendar.getInstance();
		
		// 경로의 맨 처음에는 / 로 시작해야한다.
		// File.separator 를 사용하면 OS 환경에 따라 / 또는 \를 알아서 처리한다.
		String yearPath = File.separator + cal.get(Calendar.YEAR);
		// 2025/
		String monthPath = yearPath + 
				File.separator + new DecimalFormat("00")
				.format(cal.get(Calendar.MONTH) + 1);
		// 2025/05/21
		String datePath = monthPath + 
				File.separator + new DecimalFormat("00")
				.format(cal.get(Calendar.DATE));
		log.info("파일 경로명 : {}", datePath);
		// 경로 기준 폴더를 생성
		makeDir(uploadPath, yearPath, monthPath, datePath);
		
		return datePath;
	}
	
	/**
	 * 디스크에 폴더 생성
	 * @param uploadPath
	 * @param paths
	 */
	public static void makeDir(String uploadPath, String... paths) {
	    File dir = new File(uploadPath); // 시작 경로

	    for (String path : paths) {
	        dir = new File(dir, path); // 상대 경로로 누적
	        if (!dir.exists()) {
	            dir.mkdir();
	        }
	    }
	}
	
	/**
	 * 파라미터로 파일명을 던지면 확장자가 리턴된다.
	 * @param fileName
	 * @return
	 */
	public static String getExtension(String fileName) {
		// 파일이름 21sd.12d1dssss.asfasf2.sa2fafsaf.2342.jpg
		// 파일 이름 sdfsdfsa >> -1 .이 없으면 dotPostion에 -1 출력
		int dotPostion = fileName.lastIndexOf('.');
		if (-1 != dotPostion && fileName.length() - 1 > dotPostion) {
			return fileName.substring(dotPostion + 1);
		} else {
			return "";
		}
	}
	
	/**
	 * 파일 MIME
	 * MIME (Multipurpose Internet Mail Extension)
	 * 타입/서브타입 image/jpg, image/png, image/gif
	 * @param filePath
	 * @return
	 */
	public static String getFileMimeType(String filePath) {
		File file = new File(filePath);
		try {
			return Files.probeContentType(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
