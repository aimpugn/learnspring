package com.learn.spring.learnspring.errors.api;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * CommonApiError
 */
@Data
public class CommonApiError {
	private HttpStatus status;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private LocalDateTime timestamp;
	private String message;
	private String debugMessage;
	private List<?> subErrors;


	CommonApiError(HttpStatus status) {
		this(status, null, null);
	}

	CommonApiError(HttpStatus status, Throwable ex) {
		this(status, null, ex);
	}

	CommonApiError(HttpStatus status, String message, Throwable ex) {
		this.timestamp = LocalDateTime.now();
		this.status = status;
		this.message = message;
		this.debugMessage = ex.getLocalizedMessage();
  }
}