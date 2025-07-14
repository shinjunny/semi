package com.dish.board.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FileDeleteRequest {
	private Long fileId;
	private String fileName;
	private Long fileMasterId;
}