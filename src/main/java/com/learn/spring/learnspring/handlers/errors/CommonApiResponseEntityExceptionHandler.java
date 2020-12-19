package com.learn.spring.learnspring.handlers.errors;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * CommonApiResponseEntityExceptionHandler
 * # 참조 링크
 * 1. https://stackoverflow.com/questions/38117717/what-is-the-best-way-to-return-different-types-of-responseentity-in-spring-mvc-o
 */

/**
 * # @ControllerAdvice 사용하면 세 가지 타입의 메서드가 지원된다
 * 1. @ExceptionHandler 어노테이션 붙은 익셉션 핸들링 메서드
 * 2. @ModelAttribute 어노테이션 붙은 Model 향상 메서드. 단, 여기의 attributes는 익셉션 핸들링 뷰에서는 사용할 수 없다
 * 3. @InitBinder 어노테이션 붙은, form-handling 구성하는 데 사용하는 바인더 초기화 메서드
 */
// @ControllerAdvice

/**
 * # @RestControllerAdvice
 * - @ControllerAdvice 와 @ResponseBody 가 함께 어노테이션 된다.
 * - 즉, @ExceptionHandler 메서드가 메시지 변환(message conversion)을 거치면서 응답 바디(response body)로 렌더링된다
 */
@RestControllerAdvice
public class CommonApiResponseEntityExceptionHandler extends ResponseEntityExceptionHandler{

}