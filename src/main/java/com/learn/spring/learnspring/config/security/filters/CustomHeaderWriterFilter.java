package com.learn.spring.learnspring.config.security.filters;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.util.OnCommittedResponseWrapper;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * CustomHeaderWriterFilter
 */
@Slf4j
public class CustomHeaderWriterFilter extends OncePerRequestFilter {

    /**
	 * # HeaderWriter
     * - 응답 헤더 작성
     *
     * # CompositeHeaderWriter
     * - 다른 HeaderWriter 클래스에 위임하는 HeaderWriter 클래스
	 */
	private final List<HeaderWriter> headerWriters;

	/**
	 * # shouldWriteHeadersEagerly
     * - 요청의 시작 시 헤더를 작성할 것인지 지시
	 */
    private boolean shouldWriteHeadersEagerly = false;

	public CustomHeaderWriterFilter(List<HeaderWriter> headerWriters) {
        this.headerWriters = headerWriters;
        headerWriters.forEach(headerWriter -> log.info(headerWriter.getClass().getName()));
	}

    @Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		if (this.shouldWriteHeadersEagerly) {
			doHeadersBefore(request, response, filterChain);
		} else {
			doHeadersAfter(request, response, filterChain);
		}
    }


    private void doHeadersBefore(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		writeHeaders(request, response);
		filterChain.doFilter(request, response);
	}

	private void doHeadersAfter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		HeaderWriterResponse headerWriterResponse = new HeaderWriterResponse(request, response);
		HeaderWriterRequest headerWriterRequest = new HeaderWriterRequest(request, headerWriterResponse);
		try {
			filterChain.doFilter(headerWriterRequest, headerWriterResponse);
		} finally {
			headerWriterResponse.writeHeaders();
		}
    }



    void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
		for (HeaderWriter writer : this.headerWriters) {
			writer.writeHeaders(request, response);
		}
	}

	/**
	 * Allow writing headers at the beginning of the request.
	 *
	 * @param shouldWriteHeadersEagerly boolean to allow writing headers at the beginning of the request.
	 * @author Ankur Pathak
	 * @since 5.2
	 */
	public void setShouldWriteHeadersEagerly(boolean shouldWriteHeadersEagerly) {
		this.shouldWriteHeadersEagerly = shouldWriteHeadersEagerly;
	}




    class HeaderWriterResponse extends OnCommittedResponseWrapper {
		private final HttpServletRequest request;

		HeaderWriterResponse(HttpServletRequest request, HttpServletResponse response) {
			super(response);
			this.request = request;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.springframework.security.web.util.OnCommittedResponseWrapper#
		 * onResponseCommitted()
		 */
		@Override
		protected void onResponseCommitted() {
			writeHeaders();
			this.disableOnResponseCommitted();
		}

		protected void writeHeaders() {
			if (isDisableOnResponseCommitted()) {
				return;
			}
			CustomHeaderWriterFilter.this.writeHeaders(this.request, getHttpResponse());
		}

		private HttpServletResponse getHttpResponse() {
			return (HttpServletResponse) getResponse();
		}
	}

	static class HeaderWriterRequest extends HttpServletRequestWrapper {
		private final HeaderWriterResponse response;

		HeaderWriterRequest(HttpServletRequest request, HeaderWriterResponse response) {
			super(request);
			this.response = response;
		}

		@Override
		public RequestDispatcher getRequestDispatcher(String path) {
			return new HeaderWriterRequestDispatcher(super.getRequestDispatcher(path), this.response);
		}
	}

	static class HeaderWriterRequestDispatcher implements RequestDispatcher {
		private final RequestDispatcher delegate;
		private final HeaderWriterResponse response;

		HeaderWriterRequestDispatcher(RequestDispatcher delegate, HeaderWriterResponse response) {
			this.delegate = delegate;
			this.response = response;
		}

		@Override
		public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
			this.delegate.forward(request, response);
		}

		@Override
		public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
			this.response.onResponseCommitted();
			this.delegate.include(request, response);
		}
	}
}