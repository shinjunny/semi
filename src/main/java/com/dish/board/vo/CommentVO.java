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
public class CommentVO {

	private Long commentId;
    private Long boardNum;
    private String writer;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;
}