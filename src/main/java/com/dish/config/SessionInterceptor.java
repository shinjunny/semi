package com.dish.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.dish.board.vo.MemberVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SessionInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		log.info("Session Interceptor");
		
		// 1. 세션 체크 (세션이 없으면 null 반환)
		HttpSession session = request.getSession(false);
		
		// 2. 세션이 존재하고, 특정한 키의 속성이 있으면 로그인 된 상태로 판단
		if (session != null) {
			MemberVO userInfo = (MemberVO) session.getAttribute("userInfo");
			if (userInfo != null) {
				return true;
			}
		}
		
		// 3. 세션이 없는 경우 : 빈(empty) 세션을 생성 후 로그인 페이지로 이동시키기
		String requestedUrl = request.getRequestURI();
		session = request.getSession();
		session.setAttribute("redirectUrl", requestedUrl);
		
		// 4. 로그인 페이지로 이동
		response.sendRedirect(request.getContextPath() + "/member/login");
		return false;
	}
}
