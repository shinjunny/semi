package com.dish.board.vo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BoardVO {
	private String boardType;
    private Long boardNum;
    private String boardTitle;
    private String boardComment;
    private String creator;
    private LocalDateTime createTime;
    private String modifier;
    private LocalDateTime modifiedTime;
    private String writer;
    
    private Long fileMasterId;
}