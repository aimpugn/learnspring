package com.learn.spring.learnspring.config.security;

import java.util.List;

import javax.servlet.Filter;
import javax.sql.DataSource;

import com.learn.spring.learnspring.config.security.filters.CustomHeaderWriterFilter;
import com.learn.spring.learnspring.config.security.filters.CustomSecurityContextPersistenceFilter;
import com.learn.spring.learnspring.config.security.filters.CustomWebAsyncManagerIntegrationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.header.writers.HstsHeaderWriter;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

import lombok.extern.slf4j.Slf4j;

/**
 * SecurityConfig
 */
@Slf4j
// @EnableWebSecurity
@EnableWebSecurity
// ㄴ1. WebSecurityConfigurer에 정의된 시큐리티 구성을 @Configuration 어노테이션 붙은 클래스가 갖도록 한다
// ㄴ2. 또는 WebSecurityConfigurerAdapter 베이스 클래스를 상속하고 개별 메서드를 오버라이딩한다
public class SecurityConfig {

    @Autowired
    private DataSource dataSource;


    @Configuration
    /**
     * # Exception: java.lang.IllegalStateException: @Order on WebSecurityConfigurers must be unique
     * - @Order 지정 안할 경우, 100 Order 이미 AdminAuthenticationConfig 클래스에서 사용하므로 같은 순서를 FilterConfig 클래스에서 사용할 수 없닶는 익셉션 발생
     * - Servlet에서 load-on-startup values와 비슷하다
     * - Order 값은 낮을수록 우선순위가 높다
     */
    @Order(Ordered.LOWEST_PRECEDENCE - 100)
    protected static class FilterConfig extends WebSecurityConfigurerAdapter {
        /**
         * BasicAuthenticationFilter
         */

        /**
         * # springSecurityFilterChain
         * - https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/configuration/WebSecurityConfiguration.html#springSecurityFilterChain--
         * - WebSecurityConfiguration 클래스는 Spring Security에서 웹 기반 보안 수행하는 FilterChainProxy 생성하기 위해서 WebSecurity 사용
         * - WebSecurityConfiguration 클래스의 springSecurityFilterChain() 메서드는 스프링 시큐리티 필터 체인을 생성하고 Filter 타입을 반환
         * - WebSecurity 커스터마이징:
         *      - WebSecurityConfigurerAdapter 상속하여 Configuration으로 사용하여 커스터마이징
         *      - WebSecurityConfigurer 구현하여 Configuration으로 사용하여 커스터마이징
         * - EnableWebSecurity 사용하면 위의 @Configuration 클래스들은 import 된다
         */
        @Autowired
        /**
         * # @Qualifier
         * - autowiring 시 후보가 되는 빈의 한정사(qualifier)로 사용
         * - 여기서 WebSecurityConfiguration 클래스의 springSecurityFilterChain() 메서드가 FilterChainProxy가 아닌 Filter를 반환하므로 @Qualifier 사용
         * - https://www.boraji.com/spring-4-qualifier-annotation-example
         * - https://stackoverflow.com/questions/40830548/spring-autowired-and-qualifier
         */
        @Qualifier("springSecurityFilterChain")
        /**
         * # 필터 체인 목록
         * 1. OncePerRequestFilter implements GenericFilterBean
         * -WebAsyncManagerIntegrationFilter            extends OncePerRequestFilter
         * -HeaderWriterFilter                          extends OncePerRequestFilter
         * -CorsFilter                                  extends OncePerRequestFilter
         * -CsrfFilter                                  extends OncePerRequestFilter
         *
         * 2. GenericFilterBean implements Filter, BeanNameAware, EnvironmentAware, EnvironmentCapable, ServletContextAware, InitializingBean, DisposableBean
         * -SecurityContextPersistenceFilter            extends GenericFilterBean
         * -LogoutFilter                                extends GenericFilterBean
         * -RequestCacheAwareFilter                     extends GenericFilterBean
         * -SecurityContextHolderAwareRequestFilter     extends GenericFilterBean
         * -AnonymousAuthenticationFilter               extends GenericFilterBean
         * -SessionManagementFilter                     extends GenericFilterBean
         * -ExceptionTranslationFilter                  extends GenericFilterBean
         */
        private Filter springSecurityFilterChain;

        /* public void getFilters() {
            FilterChainProxy filterChainProxy = (FilterChainProxy) springSecurityFilterChain;
            List<SecurityFilterChain> list = filterChainProxy.getFilterChains();
            list.stream()
              .flatMap(chain -> chain.getFilters().stream())
              .forEach(filter -> log.info(filter.getClass().getName()));
        } */

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
            /**
             * # addFilter(https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/HttpSecurityBuilder.html#addFilter-javax.servlet.Filter-)
             * - 필터 인스턴스 또는 필터중 하나를 상속하는 필터 추가
             * - 필터의 순서를 자동으로 관리
             * - SwitchUserFilter 클래스가 가장 마지막 순서
             */
            .addFilterAfter(new CustomWebAsyncManagerIntegrationFilter(), SwitchUserFilter.class)
            .addFilterAfter(new CustomSecurityContextPersistenceFilter(), CustomWebAsyncManagerIntegrationFilter.class);
        }
    }


    @Configuration
    @Order(Ordered.LOWEST_PRECEDENCE - 90)
    protected static class AdminAuthenticationConfig extends WebSecurityConfigurerAdapter implements EnvironmentAware {
        private Environment environment;

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }

        /**
         * # authenticationManagerBean()
         * - configure(AuthenticationManagerBuilder) 에서 AuthenticationManager 빈을 드러내고 싶은 경우 override
         */
        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        /**
         * # HttpSecurity
         * - Spring Security XML 파일의 <http> 태그와 비슷하다
         * - 특정 http 요청에 대한 웹 기반 보안 설정(web based security) 돕는다
         * - 기본적으로 모든 요청에 적용되지만, requestMatcher(RequestMatcher) 또는 비슷한 메서드 사용해서 제한할 수 있다
         */
        @Override
        protected void configure(HttpSecurity http) throws Exception {
                configureHeaders(http.headers());
                http.csrf()
                .ignoringAntMatchers("/resources/static/*", "/resources/templates/*").and()
                /**
                 * # .cors()
                 * - cors() 안에서 CsrfConfigurer 인스턴스가 새로 생성된다
                 * - 생성된 CsrfConfigurer AbstractConfiguredSecurityBuilder 빌더에서 config에 등록된다
                 */
                .cors().and()
                .exceptionHandling().and()
                .sessionManagement();
                // .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)).and()
                // .authenticationEntryPoint(authenticationEntryPoint()).and()
                // .sessionManagement()
                // .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // .authorizeRequests()
                // .antMatchers("/admin/**").authenticated()
                // .antMatchers(HttpMethod.HEAD).permitAll()
                // .antMatchers(HttpMethod.GET, "/admin/tcm/").permitAll()
                // .antMatchers(HttpMethod.POST, "/admin/tcm/users", "/admin/tcm/login").permitAll()
                // .antMatchers(HttpMethod.GET, "/articles/**", "/profiles/**", "/tags").permitAll()
                // .anyRequest().authenticated();
        }


        private AuthenticationEntryPoint authenticationEntryPoint() {
                LoginUrlAuthenticationEntryPoint entryPoint = new LoginUrlAuthenticationEntryPoint("/signin");
                entryPoint.setForceHttps(false);

                return entryPoint;
        }


        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            // User 클래스 내부 클래스 UserBuilder 생성 후 반환
                auth.authenticationProvider(authenticationProviderImpl());
        }


        public AuthenticationProviderImpl authenticationProviderImpl() {
                return new AuthenticationProviderImpl();
        }
    }


    private static void configureHeaders(HeadersConfigurer<?> headers) throws Exception {
        // false: 서브도메인 포함 여부
        HstsHeaderWriter writer = new HstsHeaderWriter(false);
        writer.setRequestMatcher(AnyRequestMatcher.INSTANCE);
        /**
         * # contentTypeOptions()
         * - X-Content-Type-Options: nosniff 헤더 값을 작성하는 XContentTypeOptionsHeaderWriter 사용 여부
         * - MIME type이 없거나 정확하지 않으면 브라우저가 MIME type을 추측하기 위해 MIME sniffing을 수행하는데,
         *   이 옵션으로 내용을 들여다보는 것을 막을 수 있다.
         * - https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Content-Type-Options
         * - https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types#MIME_sniffing
         * - https://fetch.spec.whatwg.org/#should-response-to-request-be-blocked-due-to-nosniff?
         * */
        headers.contentTypeOptions().and()
        /**
         * # xssProtection()
         * - X-XSS-Protection: 0                  XSS 필터링 비활성화
         * - X-XSS-Protection: 1                  XSS 필터링 사용. 스크립팅 공격 감지되면 브라우저는 안전하지 않은 영역 제거 후 렌더링
         * - X-XSS-Protection: 1; mode=block      XSS 필터링 사용. 스크립팅 공격 감지되면 페이지 렌더링 중단
         * - X-XSS-Protection: 1; report={URI}    XSS 필터링 사용. Chromium에서만 사용 가능. 렌더링 차단 후 위반 사항 보고
         * */
        .xssProtection().and()
        .cacheControl().and()
        .addHeaderWriter(writer)
        .frameOptions();
    }
}