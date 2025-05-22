package com.dish.board.vo;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AttachFileMasterVO {

	private Long fileId;
	private String creator;
    private LocalDateTime createTime;
    
    @Builder
    public AttachFileMasterVO(Long fileId, String creator, LocalDateTime createTime) {
		super();
		this.fileId = fileId;
		this.creator = creator;
		this.createTime = createTime;
	}
}