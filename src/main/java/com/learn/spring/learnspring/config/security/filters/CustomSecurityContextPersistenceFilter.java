package com.learn.spring.learnspring.config.security.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * # SecurityContextPersistenceFilter
 * - request에 앞서 설정된 SecurityContextRepository에서 얻은 정보로 SecurityContextHolder 생성
 * - request가 완료되면 respository에 저장하고 컨텍스트 홀더를 정리한다
 * - 기본적으로 HttpSessionSecurityContextRepository 사용
 * - 서블릿 컨테이너 비호환성(특히 Weblogic)을 해결하기 위해 요청당 한번씩 실행
 * - 이 필터는 반드시 인증(authentication) 처리 메커니즘 전에 실행되어야 한다
 * - 인증 처리 메커니즘(BASIC, CAS 처리 필터 등)은 해당 메커니즘이 실행될 때 SecurityContextHolder가 유효한 SecurityContext를 가지고 있을 것이라 예상한다
 * - 본질적으로 저장 이슈(storage issue)를 별도의 전략(separate strategy)에 위임하기 위한 HttpSessionContextIntegrationFilter의 리팩토링
 * - forceEagerSessionCreation 속성 사용하여 픽터 체인 실행 전에 언제나 세션 사용 가능하도록 보장할 수 있다(기본값은 false이며, 자원집약적이라 추천하지 않는다)
 */
@Slf4j
public class CustomSecurityContextPersistenceFilter extends SecurityContextPersistenceFilter {

	static final String FILTER_APPLIED = "__spring_security_scpf_applied";

	private SecurityContextRepository repo;

	private boolean forceEagerSessionCreation = false;

	public CustomSecurityContextPersistenceFilter() {
        this(new HttpSessionSecurityContextRepository());
	}

	public CustomSecurityContextPersistenceFilter(SecurityContextRepository repo) {
		this.repo = repo;
	}

    /**
     * # req
     * # res
     * # chain
     * - chain.getClass().getName(): org.springframework.security.web.FilterChainProxy$VirtualFilterChain
     */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        log.info("doFilter");
		HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;


		if (request.getAttribute(FILTER_APPLIED) != null) {
			// ensure that filter is only applied once per request
			chain.doFilter(request, response);
			return;
		}

		final boolean debug = logger.isDebugEnabled();

		request.setAttribute(FILTER_APPLIED, Boolean.TRUE);

		if (forceEagerSessionCreation) {
			HttpSession session = request.getSession();

			if (debug && session.isNew()) {
				logger.debug("Eagerly created session: " + session.getId());
			}
		}

		HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
		SecurityContext contextBeforeChainExecution = repo.loadContext(holder);

		try {
			SecurityContextHolder.setContext(contextBeforeChainExecution);

			chain.doFilter(holder.getRequest(), holder.getResponse());

		}
		finally {
			SecurityContext contextAfterChainExecution = SecurityContextHolder.getContext();
			// Crucial removal of SecurityContextHolder contents - do this before anything else.
			SecurityContextHolder.clearContext();
			repo.saveContext(contextAfterChainExecution, holder.getRequest(), holder.getResponse());
			request.removeAttribute(FILTER_APPLIED);

			if (debug) {
				logger.debug("SecurityContextHolder now cleared, as request processing completed");
			}
		}
	}

	public void setForceEagerSessionCreation(boolean forceEagerSessionCreation) {
		this.forceEagerSessionCreation = forceEagerSessionCreation;
	}
}