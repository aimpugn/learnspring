package com.learn.spring.learnspring.errors.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * CommonApiValidationError
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class CommonApiValidationError extends CommonApiSubError {
    private String object;
    private String field;
    private Object rejectedValue;
    private String message;

    CommonApiValidationError(String object, String message) {
        this.object = object;
        this.message = message;
    }
}