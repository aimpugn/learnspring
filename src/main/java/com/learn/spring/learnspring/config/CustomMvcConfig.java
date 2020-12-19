package com.learn.spring.learnspring.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.UrlBasedViewResolverRegistration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.groovy.GroovyMarkupConfigurer;
import org.springframework.web.servlet.view.groovy.GroovyMarkupViewResolver;

/**
 * # CustomMvcConfig
 * - @EnablceWebMvc로 import되는 구성을 커스터마이징
 * - WebMvcConfigurerAdapter 상속하고 커스터마이징 하려는 메서드 override
 * - WebMvcConfigurerAdapter 상속 후 커스터마이징한 메서드는 구성 스테이지 동안 WebMvcConfigurationSupport에 의해 콜백 호출된다
 * - 단, Spring 5.0 이후부터 WebMvcConfigurerAdapter는 deprecated 됐으니 WebMvcConfigurer 인터페이스 구현해서 사용

 * # @EnableWebMvc
 * - https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto-switch-off-default-mvc-configuration
 * - MVC 구성을 제어하는 가장 쉬운 방법은 사용자정의 @Configuration 클래스에 @EnableWebMvc 어노테이션을 함께 사용하는 것
 * - https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/servlet/config/annotation/EnableWebMvc.html
 * - WebMvcAutoConfiguration 기본 구성 사용
 * - Srping Web MVC 구성 import하기 위해 하나의 @Configuration에서만 @EnableWebMvc를 갖는다
 * - 하지만 Srping Web MVC 구성을 커스터마이징하기 위해 여러번 WebMvcConfigurer 구현할 수 있다
 */
@EnableWebMvc
public class CustomMvcConfig {

    /** # Resource 설정 관련 config */
    @Configuration
    protected static class ResourceConfig implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            /**
             * URL 경로 패턴(/resources/**)의 정적 자원 제공하기 위해 호출되어야 하는 ResourceHandler가 생성되어 반환된다
             */
            ResourceHandlerRegistration rhr = registry.addResourceHandler("/resources/**");
            /**
             * /resources 경로 하위 "/" 또는 "/META-INF/public-web-resources/" 정적 자원 제공하도록 위치 추가
             */
            rhr.addResourceLocations("/", "classpath:/META-INF/public-web-resources/");
            /**
             * 0이면 캐싱하지 않음. 1 이상인 경우 해당 초만큼 캐싱
             */
            rhr.setCachePeriod(300);
        }


        /**
     * 전역 CORS 구성
     * https://docs.spring.io/spring/docs/5.2.2.RELEASE/spring-framework-reference/web.html#mvc-cors-global
     */
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/api/**");
        }
    }


    /** # Groovy 템플릿 사용 위한 config 클래스 */
    @Configuration
    protected static class GroovyConfig implements WebMvcConfigurer {
        /**
     * GroovyMarkupConfigurer: Groovy의 TemplateConfiguration의 확장이자 MarkupTemplateEngine 생성위한 Spring MVC의 GroovyMarkupConfig 인터페이스 구현
     *
     * 기본적으로,
     * 1. Groovy 템플릿과 그 참조자들을 로딩하기 위한 부모 ClassLoader
     * 2. 베이스 TemplateConfiguration 클래스의 기본 구성
     * 3. 템플릿 파일 리졸브하기 위한 TemplateResolver
     * 와 함께 MarkupTemplateEngine을 생성
     */
        @Bean
        public GroovyMarkupConfigurer groovyMarkupConfigurer() {
            GroovyMarkupConfigurer configurer = new GroovyMarkupConfigurer();
            configurer.setResourceLoaderPath("classpath:templates/");
            return configurer;
        }


        @Override
        public void configureViewResolvers(ViewResolverRegistry registry) {
            /**
             * Groovy 템플릿 등록 후 registration으로 캐시, view 등 설정
             * - registry 내부 GroovyMarkupRegistration 클래스에서 GroovyMarkupViewResolver를 resolver로 UrlBasedViewResolverRegistration를 생성한 후
             * - getViewResolver().setSuffix(".tpl");로 설정되며,
             *
             * 1. ModelAndView에서 view 이름을 설정하면 해당 view로 Model 객체가 값을 가지고 전달되며
             * 2. 또는 return "{PATH/TO/VIEW}";을 사용하면 {PATH/TO/VIEW}에 해당하는 뷰를 classpath:templates/ 하위에서 찾아서 리졸브한다
             */
            UrlBasedViewResolverRegistration registration = registry.groovy();
        }
    }

    /**
     * # REST 관련 config 클래스
    */
    @Configuration
    protected static class RestConfig implements WebMvcConfigurer {
        /**
         * # configureMessageConverters
         * - 요청 또는 응답의 바디를 읽고 쓰는 데 사용하는 HttpMessageConverters 컨버터 구성
         * - 컨버터가 추가되지 않으면, 기본 컨버터 목록이 등록된다
         * - 리스트에 컨버터 추가하면, 기본 컨버터 목록이 등록되지 않는다. 영향 주고 싶지 않다면 extendMessageConverters(java.util.List) 사용
         */
        // @Override
        // public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {}

        /**
         * # extendMessageConverters()
         * - converters 구성 완료된 후에 컨버터 목록을 확장하거나 수정하기 위한 훅(hook)
         * - 기본 컨버터와 사용자정의 컨버터 모두 사용할 때 유용
         */
        @Override
        public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
            /**
             * # MappingJackson2HttpMessageConverter
             * - Spring MVC에서는 기본적으로 HttpMessageConverters 컨버터 사용하여 @ResponseBody 어노테이션이 붙은 메서드의 결과를 렌더링한다
             * - JSON을 읽고 쓸 수 있는 Jackson 2.x's ObjectMapper 사용하여 org.springframework.http.converter.HttpMessageConverter 구현한 converter
             * - 타입 있는 빈 또는 타입 없는 HashMap 인스턴스에 바인드할 때 사용
             * - application/json, application/*+json with UTF-8 지원하며, supportedMediaTypes 속성을 오버라이딩 해서 바꿀 수 있다
             * - 기본 생성자는 Jackson2ObjectMapperBuilder 빌더가 제공하는 기본 구성을 사용한다
             */
            converters.add(new MappingJackson2HttpMessageConverter());
        }
    }

}