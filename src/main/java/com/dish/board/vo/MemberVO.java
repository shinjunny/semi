package com.dish.board.vo;

import java.time.LocalDateTime;
import java.util.List;

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
public class MemberVO {
	private String userId;
	private String userPw;
	private String userName;
	private String userPhone1;
	private String userPhone2;
	private String userPhone3;
	private String userAddr1;
	private String userAddr2;
	private String Email;
	private String creator;
    private LocalDateTime createTime;
    private String modifier;
    private LocalDateTime modifiedTime;
    
    private List<BoardVO> boardList;
}