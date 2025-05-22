package com.dish;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@MapperScan("com.dish.board.mapper")
@SpringBootApplication
public class DishApplication {
	
	// 업로드 경로값을 application.properties에서 
	// 읽은 후 uploadPath에 기본값으로 저장
	@Value("${upload.path}")
	private String uploadPath;
	
	// 맴버변수 uploadPath 값을 Bean으로 만들어 둔다.
	@Bean(name = "uploadPath")
	public String uploadPath() {
		return this.uploadPath;
	}

	public static void main(String[] args) {
		SpringApplication.run(DishApplication.class, args);
	}

}