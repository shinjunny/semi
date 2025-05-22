package com.dish.board.vo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AttachFileDetailVO {
	
	private Long fileId;
	private Long fileMasterId;
	
	@JsonIgnore
	private String filePath;
	
	private String fileName;
	private String orgFileName;
	private String fileExt;
	private String fileMime;
	private Long fileSize;
	private String creator;
    private LocalDateTime createTime;
    private String modifier;
    private LocalDateTime modifiedTime;
    
    @Builder
    public AttachFileDetailVO(Long fileId, Long fileMasterId, String filePath, String fileName, String orgFileName,
			String fileExt, String fileMime, Long fileSize, String creator, LocalDateTime createTime, String modifier,
			LocalDateTime modifiedTime) {
		super();
		this.fileId = fileId;
		this.fileMasterId = fileMasterId;
		this.filePath = filePath;
		this.fileName = fileName;
		this.orgFileName = orgFileName;
		this.fileExt = fileExt;
		this.fileMime = fileMime;
		this.fileSize = fileSize;
		this.creator = creator;
		this.createTime = createTime;
		this.modifier = modifier;
		this.modifiedTime = modifiedTime;
	}
}