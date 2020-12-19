package com.learn.spring.learnspring.config.security;

import javax.servlet.ServletContext;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

/**
 * SecurityInitializer
 */
public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {
	public SecurityInitializer() {
		super(SecurityConfig.class);
	}

	/**
	 * # AbstractSecurityWebApplicationInitializer.onStartup
	 * - Configure the given ServletContext with any servlets, filters, listeners context-params and attributes necessary for initializing this web application. See examples above.
	 */

	/**
	 * # beforeSpringSecurityFilterChain()
	 * - 스프링 시큐리티 필터체인이 등록되기 전에 호출되는 메서드
	 */
	@Override
	protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {

	}

	/**
	 * # enableHttpSessionEventPublisher()
	 * - HttpSessionEventPublisher 클래스가 리스너로 등로되어야 한다면 오버라이딩
	 * - 최대 세션 수를 지정했다면, true여야 한다
	 * - javax.servlet.http.HttpSessionListener.sessionCreated() 를 HttpSessionCreatedEvent 와 매핑
	 * - javax.servlet.http.HttpSessionListener.sessionDestroyed() 를 HttpSessionDestroyedEvent 에 매핑
	 * This should be true, if session management has specified a maximum number of
	 * sessions.
	 */
	/*@Override
	protected boolean enableHttpSessionEventPublisher() {
		return false;
	}*/

}