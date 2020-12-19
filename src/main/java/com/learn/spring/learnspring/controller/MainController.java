package com.learn.spring.learnspring.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * MainController
 */

/**
 * # @RestController
 * - @Controller 어노테이션과 @ResponseBody 어노테이션 조합
 * - 따라서 매핑별로 public @ResponseBody ResponseEntity<List<JSONObject>> getAll() 같이 @ResponseBody 선언하지 않아도 된다
 * - 이 어노테이션을 가진 타입은, @RequestMapping 메서드가 기본적으로 @ResponseBody 구문을 가정하는 컨트롤러로 다뤄진다
 * - 적절한 HandlerMapping 과 HandlerAdapter 쌍이 구성되면 처리된다(ex: RequestMappingHandlerMapping 와 RequestMappingHandlerAdapter 쌍)
 */
@Slf4j
@RestController
public class MainController {

    @GetMapping(path = "/home")
    public ResponseEntity<?> home() {
        log.info("Lombok log test");

        return ResponseEntity.accepted().body("ACCEPTED");
    }
}