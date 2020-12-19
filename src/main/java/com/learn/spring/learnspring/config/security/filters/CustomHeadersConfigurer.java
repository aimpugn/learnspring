package com.learn.spring.learnspring.config.security.filters;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.writers.CacheControlHeadersWriter;
import org.springframework.security.web.header.writers.ContentSecurityPolicyHeaderWriter;
import org.springframework.security.web.header.writers.FeaturePolicyHeaderWriter;
import org.springframework.security.web.header.writers.HpkpHeaderWriter;
import org.springframework.security.web.header.writers.HstsHeaderWriter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XContentTypeOptionsHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode;
import org.springframework.security.web.util.matcher.RequestMatcher;

import lombok.extern.slf4j.Slf4j;

/**
 * # HeadersConfigurer
 * - 기본 보안 HTTP 헤더를 추가
 * - WebSecurityConfigurerAdapter 기본 생성자 사용하면 헤더는 자동으로 활성화 된다
 *
 * # 기본 헤더
 * # https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control
 * - Cache-Control: no-cache, no-store, max-age=0, must-revalidate
 *
 *
 * # https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Pragma
 * - Pragma: no-cache
 * - 요청/응답 체인에 따라 다양한 영향을 끼치는 implementation-specific 헤더
 * - Cache-Control: no-cache와 같다
 * - 캐시된 복사본을 풀어주기 전에 오리진 서버에 검증 요청
 *
 *
 * # https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Expires
 * - Expires: 0
 * - 이 날짜/시간 이후에는 응답이 오래됐다고 판단
 * - 0 같은 무효한 날짜 -> 과거의 날짜를 나타내며, 리소스가 이미 만료됐음을 의미
 * - Cache-Control: max-age={SECOND} 또는 Cache-Control:s-maxage={SECOND} 있는 경우 무시된다
 *
 *
 * # https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Content-Type-Options
 * - X-Content-Type-Options: nosniff
 * - Content-Type 헤더에 지정된 MIME 타입(https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types)이 변경되지 않아야 함
 * - MIME 타입 sniffing(들여다보기) 방지(https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types#MIME_sniffing)\
 * - Content-Type 제공 -> MIME 타입 sniffing 제외는 최상위 레벨의 문서에 적용 -> Content-Type: text/html 외에는 렌더링되지 않고 다운로드 된다
 *
 *
 * # https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Strict-Transport-Security
 * - Strict-Transport-Security: max-age=31536000 ; includeSubDomains
 * - HSTS(HTTP Strict-Transport-Security) 헤더는 브라우저에게 HTTPS로만 접근해야 함을 말해준다
 *
 *
 * # https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Frame-Options
 * - X-Frame-Options: DENY
 * - <frame>, <iframe>, <embed>, <object> 태그가 있는 페이지 렌더링 허용할 것인지 여부
 * - clickjacking(https://en.wikipedia.org/wiki/Clickjacking) 공격 피하기 위해 사용
 *
 *
 * # https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-XSS-Protection
 * - X-XSS-Protection: 1; mode=block
 * - XSS 필터링을 활성화하고, 브라우저가 페이지 렌더링 중지
 */
@Slf4j
public class CustomHeadersConfigurer<H extends HttpSecurityBuilder<H>> extends AbstractHttpConfigurer<HeadersConfigurer<H>, H>  {
    private List<HeaderWriter> headerWriters = new ArrayList<>();

	/**
     * 기본 헤더 작성자
     */
	private final ContentTypeOptionsConfig contentTypeOptions = new ContentTypeOptionsConfig();
	private final XXssConfig xssProtection = new XXssConfig();
	private final CacheControlConfig cacheControl = new CacheControlConfig();
	private final HstsConfig hsts = new HstsConfig();
	private final FrameOptionsConfig frameOptions = new FrameOptionsConfig();
	private final HpkpConfig hpkp = new HpkpConfig();
	private final ContentSecurityPolicyConfig contentSecurityPolicy = new ContentSecurityPolicyConfig();
	private final ReferrerPolicyConfig referrerPolicy = new ReferrerPolicyConfig();
    private final FeaturePolicyConfig featurePolicy = new FeaturePolicyConfig();


    public CustomHeadersConfigurer(){

    }

    /**************************************************************************************************
     * ContentTypeOptionsConfig
     **************************************************************************************************/
    public final class ContentTypeOptionsConfig {
		private XContentTypeOptionsHeaderWriter writer;

		private ContentTypeOptionsConfig() {
			enable();
		}

		/**
		 * X-Content-Type-Options: nosniff 헤더 제거
		 *
		 * @return {@link HeadersConfigurer} for additional customization.
		 */
		public CustomHeadersConfigurer<H> disable() {
			writer = null;
			return and();
		}

		/**
		 * 추가 커스터마이징 허용
		 */
		public CustomHeadersConfigurer<H> and() {
			return CustomHeadersConfigurer.this;
		}

		/**
		 * X-Content-Type-Options: nosniff 헤더 쓰기
		 */
		private ContentTypeOptionsConfig enable() {
			if (writer == null) {
				writer = new XContentTypeOptionsHeaderWriter();
			}
			return this;
		}
	}





    /**************************************************************************************************
     * XXssConfig
     **************************************************************************************************/
    /**
	 * - 포괄적인 XSS 보호가 아니다
     * - X-XSS-Protection: 1; mode=block 추가하는 XXssProtectionHeaderWriter 커스터마이징 허용
	 */
	public XXssConfig xssProtection() {
		return xssProtection.enable();
	}

	/**
	 * - 포괄적인 XSS 보호가 아니다
     * - X-XSS-Protection: 1; mode=block 추가하는 XXssProtectionHeaderWriter 커스터마이징 허용
	 */
	public CustomHeadersConfigurer<H> xssProtection(Customizer<XXssConfig> xssCustomizer) {
		xssCustomizer.customize(xssProtection.enable());
		return CustomHeadersConfigurer.this;
    }

	public final class XXssConfig {
		private XXssProtectionHeaderWriter writer;

		private XXssConfig() {
			enable();
		}

		/**
		 * - block(false): mode=block 제외되고, 이 경우 모든 컨텐츠를 수정하려고 시도
         * - block(true): 컨텐츠가 "#"으로 치환된다
		 */
		public XXssConfig block(boolean enabled) {
			writer.setBlock(enabled);
			return this;
		}

		/**
		 * - xssProtectionEnabled(true) -> X-XSS-Protection: 1
         * - xssProtectionEnabled(false) -> X-XSS-Protection: 0
         * - XXssProtectionHeaderWriter.setBlock(true) -> X-XSS-Protection: 1; mode=block
		 */
		public XXssConfig xssProtectionEnabled(boolean enabled) {
			writer.setEnabled(enabled);
			return this;
		}

		/**
		 * - X-XSS-Protection 헤더 제외
		 */
		public CustomHeadersConfigurer<H> disable() {
			writer = null;
			return and();
		}

		/**
		 * 추가 커스터마이징 허용
		 */
		public CustomHeadersConfigurer<H> and() {
			return CustomHeadersConfigurer.this;
		}

		/**
		 * - X-XSS-Protection 헤더가 활성화 됐는지 확인
         * - XXssProtectionHeaderWriter 작성자 추가
         * Ensures the X-XSS-Protection header is enabled if it is not already.
		 *
		 * @return the {@link XXssConfig} for additional customization
		 */
		private XXssConfig enable() {
			if (writer == null) {
				writer = new XXssProtectionHeaderWriter();
			}
			return this;
		}
	}




    /**************************************************************************************************
     * CacheControlConfig
     **************************************************************************************************/
	/**
	 * - CacheControlHeadersWriter 커스터마이징 허용
     * - 다음 헤더 추가
     * Cache-Control: no-cache, no-store, max-age=0, must-revalidate
     * Pragma: no-cache
     * Expires: 0
	 */
	public CacheControlConfig cacheControl() {
		return cacheControl.enable();
	}

	/**
	 * - CacheControlHeadersWriter 커스터마이징 허용
     * - 다음 헤더 추가
     * Cache-Control: no-cache, no-store, max-age=0, must-revalidate
     * Pragma: no-cache
     * Expires: 0
	 */
	public CustomHeadersConfigurer<H> cacheControl(Customizer<CacheControlConfig> cacheControlCustomizer) {
		cacheControlCustomizer.customize(cacheControl.enable());
		return CustomHeadersConfigurer.this;
	}

	public final class CacheControlConfig {
		private CacheControlHeadersWriter writer;

		private CacheControlConfig() {
			enable();
		}

		/**
		 * - Cache-Control 헤더 제외
		 */
		public CustomHeadersConfigurer<H> disable() {
			writer = null;
			return CustomHeadersConfigurer.this;
		}

		/**
		 * - 추가 커스터마이징 허용
		 */
		public CustomHeadersConfigurer<H> and() {
			return CustomHeadersConfigurer.this;
		}

		/**
		 * - CacheControlHeadersWriter 작성자 없으면 추가
		 */
		private CacheControlConfig enable() {
			if (writer == null) {
				writer = new CacheControlHeadersWriter();
			}
			return this;
		}
	}





    /**************************************************************************************************
     * HstsConfig
     **************************************************************************************************/
	/**
     * # https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Strict-Transport-Security
     * - HTTP Strict Transport Security(HSTS) 지원하는 HstsHeaderWriter 작성자 커스터마이징 허용
	 */
	public HstsConfig httpStrictTransportSecurity() {
		return hsts.enable();
	}

	/**
     * # https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Strict-Transport-Security
     * - HTTP Strict Transport Security(HSTS) 지원하는 HstsHeaderWriter 작성자 커스터마이징 허용
     * - hstsCustomizer -> HstsConfig 구성 클래스에 대한 추가 옵션 위한 Customizer 클래스
	 */
	public CustomHeadersConfigurer<H> httpStrictTransportSecurity(Customizer<HstsConfig> hstsCustomizer) {
		hstsCustomizer.customize(hsts.enable());
		return CustomHeadersConfigurer.this;
	}

	public final class HstsConfig {
		private HstsHeaderWriter writer;

		private HstsConfig() {
			enable();
		}

		/**
         * #
         * - 초 단위로 max-age 지시자 설정
         * - 기본은 1년
         *
         * # https://tools.ietf.org/html/rfc6797#section-6.1.1
         * - 브라우저로 하여금 얼마 동아 이 도메인을 알고 있는 HSTS 호스트로 기억하게 할 것인지 지정
		 */
		public HstsConfig maxAgeInSeconds(long maxAgeInSeconds) {
			writer.setMaxAgeInSeconds(maxAgeInSeconds);
			return this;
		}

		/**
		 * # RequestMatcher
         * - "Strict-Transport-Security"가 추가되어야 하는지 결정하기 위해 사용
         * - true면 헤더 추가하고, 그렇지 않으면 추가하지 않는다
         * - 기본적으로 HttpServletRequest.isSecure() 메서드가 true 반환하면 추가된다
		 */
		public HstsConfig requestMatcher(RequestMatcher requestMatcher) {
			writer.setRequestMatcher(requestMatcher);
			return this;
		}

		/**
		 * # https://tools.ietf.org/html/rfc6797#section-6.1.2
         * - true면 하위 도메인(subdomains)도 HSTS로 고려된다
         * - 기본값은 true
		 */
		public HstsConfig includeSubDomains(boolean includeSubDomains) {
			writer.setIncludeSubDomains(includeSubDomains);
			return this;
		}

		/**
		 * # https://hstspreload.org/
         * - true면 HSTS 헤더에 preload가 포함된다
         * - 기본값은 false
		 */
		public HstsConfig preload(boolean preload) {
			writer.setPreload(preload);
			return this;
		}

		/**
		 * - HSTS 비활성화
		 */
		public CustomHeadersConfigurer<H> disable() {
			writer = null;
			return CustomHeadersConfigurer.this;
		}

		/**
		 * - 추가 커스터마이징 허용
		 */
		public CustomHeadersConfigurer<H> and() {
			return CustomHeadersConfigurer.this;
		}

		/**
		 * - 앞서 HSTS가 활성화되어 있지 않다면, 활성화
		 */
		private HstsConfig enable() {
			if (writer == null) {
				writer = new HstsHeaderWriter();
			}
			return this;
		}
	}


    /**************************************************************************************************
     * FrameOptionsConfig
     **************************************************************************************************/
	/**
	 * - XFrameOptionsHeaderWriter 커스터마이징 허용
	 */
	public FrameOptionsConfig frameOptions() {
		return frameOptions.enable();
	}

    /**
	 * - XFrameOptionsHeaderWriter 커스터마이징 허용
     * - frameOptionsCustomizer -> FrameOptionsConfig 구성 클래스에 대한 추가 옵션 위한 Customizer 클래스
	 */
	public CustomHeadersConfigurer<H> frameOptions(Customizer<FrameOptionsConfig> frameOptionsCustomizer) {
		frameOptionsCustomizer.customize(frameOptions.enable());
		return CustomHeadersConfigurer.this;
	}

	public final class FrameOptionsConfig {
		private XFrameOptionsHeaderWriter writer;

		private FrameOptionsConfig() {
			enable();
		}

		/**
		 * # https://developer.mozilla.org/ko/docs/Web/HTTP/Headers/X-Frame-Options
         * - 이 애플리케이션에서 프레임 사용 DENY
         * - DENY하면 같은 사이트 내에서 frame 통한 접근도 막는다
         * - 하지만 sameorigin 명시할 경우 frame에 포함된 페이지가 페이지를 제공하는 사이트와 동일할 경우 계속 사용할 수 있다
		 */
		public CustomHeadersConfigurer<H> deny() {
			writer = new XFrameOptionsHeaderWriter(XFrameOptionsMode.DENY);
			return and();
		}

		/**
         * # https://developer.mozilla.org/ko/docs/Web/HTTP/Headers/X-Frame-Options
         * - 이 애플리케이션을 프레임으로 만들기 위한 같은 출처(origin)의 오는 모든 요청 허용
         * - 예를 들어, 애플리케이션이 example.com에 호스트된 경우, example.com은 그 애플리케이션을 프레임으로 만들 수 있다
         * - 하지만 evil.com은 이 애플리케이션을 프레임으로 만들 수 없다
		 */
		public CustomHeadersConfigurer<H> sameOrigin() {
			writer = new XFrameOptionsHeaderWriter(XFrameOptionsMode.SAMEORIGIN);
			return and();
		}

		/**
		 * - X-Frame-Option 헤더 추가하지 않는다
		 *
		 * @return the {@link HeadersConfigurer} for additional configuration.
		 */
		public CustomHeadersConfigurer<H> disable() {
			writer = null;
			return and();
		}

		/**
		 * - 추가 커스터마이징 허용
		 */
		public CustomHeadersConfigurer<H> and() {
			return CustomHeadersConfigurer.this;
		}

		/**
		 * - FrameOptionsConfig 구성 클래스가 활성화되지 않았다면 활성화
		 */
		private FrameOptionsConfig enable() {
			if (writer == null) {
				writer = new XFrameOptionsHeaderWriter(XFrameOptionsMode.DENY);
			}
			return this;
		}
	}


    /**************************************************************************************************
     * HpkpConfig
     **************************************************************************************************/
    /**
     * # https://tools.ietf.org/html/rfc7469
     * # https://developer.mozilla.org/en-US/docs/Web/HTTP/Public_Key_Pinning
     * - 이 기능은 더이상 추천하지 않는다.
     * - 브라우저에서 이 기능을 지원하더라도, 이미 관련된 웹 표준에서 제거됐거나, 제중일 것이며, 호환성 때문에 유지될 뿐
     * - 사용하는 것을 피하고, 가능하면 기존 코드르 수정한다
     * - HPKP 메커니즘은 인증 투명성(https://developer.mozilla.org/en-US/docs/Web/Security/Certificate_Transparency)과
     *   Expect-CT 헤더를 지지하게 되면서 deprecated 됐다
     *
     *
     * # 인증 투명성(https://letsencrypt.org/ko/docs/ct-logs/)
     * # https://www.certificate-transparency.org/what-is-ct
     * - 공개 프레임워크로, 잘못된 인증 발행을 방지하고 TLS 인증서 발급에 대한 로깅 및 모니터링하기 위해 디자인되었다.
     * - 인증서가 실수로 발행되거나 잘못된 인증기관이 인증서를 발행하는 등의 경우에는 오히려 인증서가 악용될 수 있기 때문
     *
     *
     * # Expect-CT
     * - 사이트가 인증서 투명성 요구사항의 보고 그리고/또는 시행(enforcement)에 참여하도록 함
     * - 사이트에 잘못 발행된 인증서 사용을 방지한다
     * - CT 요구사항은 다음 메커니즘 중 하나를 통해 만족될 수 있다(http://www.certificate-transparency.org/how-ct-works)
     *  1. 개별 로그들로 발행되는 사인된 인증서 타임스탬프(SCT) 포함을 허용하는 X.509v3 인증서 확장
     *  2. 핸드셰이크 동안 signed_certificate_timestamp 타입을 전송하는 TLS 확장
     *  3. OCSP 스테플링, 즉, status_request TLS 확장을 지원하고 SignedCertificateTimestampList 제공
     *      ㄴ(https://en.wikipedia.org/wiki/Online_Certificate_Status_Protocol)
     *
     *
	 * # https://tools.ietf.org/html/rfc7469
     * # https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Public-Key-Pins
     * # https://developer.mozilla.org/en-US/docs/Web/HTTP/Public_Key_Pinning
     * - HTTP Public Key Pinning (HPKP) 지원하는 HpkpHeaderWriter 작성자 커스터마이징 허용
     *
     * # 기타
     * - https://rsec.kr/?p=346
	 */
	public HpkpConfig httpPublicKeyPinning() {
		return hpkp.enable();
	}


    /**
	 *
     * - HTTP Public Key Pinning (HPKP) 지원하는 HpkpHeaderWriter 작성자 커스터마이징 허용
     * - hpkpCustomizer -> HpkpConfig 구성 클래스에 대한 추가 옵션 위한 Customizer 클래스
	 */
	public CustomHeadersConfigurer<H> httpPublicKeyPinning(Customizer<HpkpConfig> hpkpCustomizer) {
		hpkpCustomizer.customize(hpkp.enable());
		return CustomHeadersConfigurer.this;
	}

	public final class HpkpConfig {
		private HpkpHeaderWriter writer;

		private HpkpConfig() {}

		/**
		 * # https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Public-Key-Pins
         * # https://tools.ietf.org/html/rfc7469#section-2.1.1
         * - Public-Key-Pins 헤더의 "pin-" 지시자의 값 설정
         * - 웹 호스트 운영자에게 해당 웹 호스트에 바인드되어야 할 암호 id를 알려준다
		 */
		public HpkpConfig withPins(Map<String, String> pins) {
			writer.setPins(pins);
			return this;
		}

		/**
         * - pins는 base64로 인코딩된 SPKI(Subject Public Key Information, 공개키) fingerprint 목록
         * - SHA-256으로 해시되고 base64로 인코딩된 pins을 넘긴다
         * - hpkpHeaderWriter.addSha256Pins("d6qzRu9zOECb90Uez27xWltNsj0e1Md7GkYYkVoZWmM", "E9CZ9INDbd+2eRQozYqqbQ2yXLVKB9+xcprMF+44U1g=", ...);
         * - 인증서에서 공개 키 가져와서 base64 인코딩하기
         *   1. 인증서에서 공개 키 가져오기
         *      a. openssl rsa -in my-key-file.key -outform der -pubout \
         *          | openssl dgst -sha256 -binary \
         *          | openssl enc -base64 \
         *      b. openssl req -in my-signing-request.csr -pubkey -noout \
         *          | openssl rsa -pubin -outform der \
         *          | openssl dgst -sha256 -binary \
         *          | openssl enc -base64 \
         *      c. openssl x509 -in my-certificate.crt -pubkey -noout \
         *          | openssl rsa -pubin -outform der \
         *          | openssl dgst -sha256 -binary \
         *          | openssl enc -base64 \
         *  2. 사이트에 대한 Base64 인코딩 정보 추출
         *      a. openssl s_client -servername www.example.com -connect www.example.com:443 \
         *          | openssl x509 -pubkey -noout \
         *          | openssl rsa -pubin -outform der \
         *          | openssl dgst -sha256 -binary | openssl enc -base64 \
		 */
		public HpkpConfig addSha256Pins(String ... pins) {
			writer.addSha256Pins(pins);
			return this;
		}

		/**
		 * # max-age
         * - Public-Key-Pins 헤더에 초단위로 max-age 지시자의 값 설정
         * - 기본값: 60일
		 */
		public HpkpConfig maxAgeInSeconds(long maxAgeInSeconds) {
			writer.setMaxAgeInSeconds(maxAgeInSeconds);
			return this;
		}

		/**
		 * # includeSubDomains
         * - 이 옵션 파라미터 추가하면 같은 규칙이 해당 사이트의 모든 서브 도메인에도 적용된다
         * - 기본값: false
		 */
		public HpkpConfig includeSubDomains(boolean includeSubDomains) {
			writer.setIncludeSubDomains(includeSubDomains);
			return this;
		}

		/**
		 * # Public-Key-Pins-Report-Only(https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Public-Key-Pins-Report-Only)
         * - true면 Public-Key-Pins 대신 Public-Key-Pins-Report-Only 헤더 넣는다
         * - 기본값: true
         * - 브라우저는 서버와 연결을 종료하면 안된다
		 */
		public HpkpConfig reportOnly(boolean reportOnly) {
			writer.setReportOnly(reportOnly);
			return this;
		}

		/**
		 * # report-uri
         * - pin 검증이 실패하면 해당 URL로 보고된다
         * @param reportUri URI 타입의 보고할 URL 주소
		 */
		public HpkpConfig reportUri(URI reportUri) {
			writer.setReportUri(reportUri);
			return this;
		}

		/**
		 * # report-uri
         * - pin 검증이 실패하면 해당 URL로 보고된다
         * @param reportUri String 타입의 보고할 URL 주소
		 */
		public HpkpConfig reportUri(String reportUri) {
			writer.setReportUri(reportUri);
			return this;
		}

		/**
		 * - Public-Key-Pins 헤더 비활성화
		 */
		public CustomHeadersConfigurer<H> disable() {
			writer = null;
			return and();
		}

		/**
		 * - 추가 커스터마이징 허용
		 */
		public CustomHeadersConfigurer<H> and() {
			return CustomHeadersConfigurer.this;
		}

		/**
		 *  - Public-Key-Pins 또는 Public-Key-Pins-Report-Only 비활성화 상태면, 활성화
		 */
		private HpkpConfig enable() {
			if (writer == null) {
				writer = new HpkpHeaderWriter();
			}
			return this;
		}
	}







    /**************************************************************************************************
     * ContentSecurityPolicyConfig
     **************************************************************************************************/
	/**
     * # Content-Security-Policy(https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP)
     * # https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy
     * - XSS와 데이터 인젝션 공격을 포함한 특정 타입의 공격을 감지하거나 완화하는 데 도움을 주는 추가 보안 계층
     * - 이러한 공격들은 데이터 도용에서부터 사이트 손상(defacement), 악성 코드 배포까지 다양하게 사용된다
     * - CSP는 완벽하게 구버전에 호환(backward compatible) 되도록 디자인 된 반면, CSP 버전2는 구버전이 호환되지 않음을 명시했다
     * - 사이트가 CSP 사용하지 않으면, 브라우저는 마찬가지로 표준 same-origin 정책(https://developer.mozilla.org/en-US/docs/Web/Security/Same-origin_policy) 사용
     *
     * # Cross Site Scripting(XSS) 공격 완화
     * - CSP의 주된 목표는 XSS 공격 위협 경감 및 보고다
     * - XSS 공격은 서버로부터 받은 컨텐츠에 대한 브라우저의 신뢰를 악용
     * - CSP 헤더 설정하여 서버 관리자는 XSS 공격 발생할 수 있는 vector를 줄이거나 제거할 수 있다
     * - 브라우저가 스크립트를 실행할 수 있는 유효한 소스로 도메인을 지정
     * - 브라우저는 위의 허용된 도메인으로부터 수신한 소스 파일에서 로드된 스크립트 실행하고, 그렇지 않은 스크립트는 무시한다
     * - 완벽한 형태의 보호로, 전체적으로 스크립트 실행을 허용하지 않을 수 있다.
     *
     *
     * # https://www.w3.org/TR/CSP2/
     * - CSP 레벨 2 설정 허용
     * - 전달된 policyDirectives 지시자 사용하여 Content-Security-Policy 헤더 추가
     * - Content-Security-Policy 또는 Content-Security-Policy-Report-Only 헤더 지원
	 */
	public ContentSecurityPolicyConfig contentSecurityPolicy(String policyDirectives) {
		this.contentSecurityPolicy.writer = new ContentSecurityPolicyHeaderWriter(policyDirectives);
		return contentSecurityPolicy;
	}

	/**
     * # https://www.w3.org/TR/CSP2/
     * - CSP 레벨 2 설정 허용
     * - 전달된 policyDirectives 지시자 사용하여 Content-Security-Policy 헤더 추가
     * - Content-Security-Policy 또는 Content-Security-Policy-Report-Only 헤더 지원
     * - contentSecurityCustomizer 매개변수는 ContentSecurityPolicyConfig 구성 클래스 커스터마이징
	 */
	public CustomHeadersConfigurer<H> contentSecurityPolicy(Customizer<ContentSecurityPolicyConfig> contentSecurityCustomizer) {
		this.contentSecurityPolicy.writer = new ContentSecurityPolicyHeaderWriter();
		contentSecurityCustomizer.customize(this.contentSecurityPolicy);

		return CustomHeadersConfigurer.this;
	}

	public final class ContentSecurityPolicyConfig {
		private ContentSecurityPolicyHeaderWriter writer;

		private ContentSecurityPolicyConfig() {
		}

		/**
         * # 정책 지시자: 사용할 보안 정책 지시자들 설정
         * - https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy 참조
		 */
		public ContentSecurityPolicyConfig policyDirectives(String policyDirectives) {
			this.writer.setPolicyDirectives(policyDirectives);
			return this;
		}

		/**
		 * - Content-Security-Policy-Report-Only 헤더 활성화
		 */
		public ContentSecurityPolicyConfig reportOnly() {
			this.writer.setReportOnly(true);
			return this;
		}

		/**
		 * - 추가 커스터마이징 헝요
		 */
		public CustomHeadersConfigurer<H> and() {
			return CustomHeadersConfigurer.this;
		}
	}

	/**
	 * - 기본 헤더 비활성화하고, 나중에 다시 추가할 수 있다.
     * - 캐시 제어만 사용하고 싶다면, http.headers().defaultsDisabled().cacheControl();
	 */
	public CustomHeadersConfigurer<H> defaultsDisabled() {
		contentTypeOptions.disable();
		xssProtection.disable();
		cacheControl.disable();
		hsts.disable();
		frameOptions.disable();
		return this;
	}

	/**
     * # List<HeaderWriter> writers = getHeaderWriters(); 로 Null이 아닌 헤더 작성자(writer) 목록을 가져온다
     * - contentTypeOptions.writer, xssProtection, cacheControl, hsts, frameOptions, contentSecurityPolicy, referrerPolicy, featurePolicy
     * - private 필드인 headerwriters 목록이 있으면 추가
     *
     * # headersFilter = postProcess(headersFilter);
     * - SecurityConfigurerAdapter 클래스의 내부 private static final class CompositeObjectPostProcessor 클래스에서 postProcess(Object object) 후처리
     * - ObjectPostProcessor 클래스 목록은 SecurityConfigurerAdapter 클래스의 addObjectPostProcessor() 메서드 사용해서 추가
	 */
	private CustomHeaderWriterFilter createHeaderWriterFilter() {
		List<HeaderWriter> writers = getHeaderWriters();
		if (writers.isEmpty()) {
			throw new IllegalStateException(
					"Headers security is enabled, but no headers will be added. Either add headers or disable headers security");
		}
		CustomHeaderWriterFilter headersFilter = new CustomHeaderWriterFilter(writers);
		headersFilter = postProcess(headersFilter);
		return headersFilter;
	}

	/**
	 * Gets the {@link HeaderWriter} instances and possibly initializes with the defaults.
	 *
	 * @return
	 */
	private List<HeaderWriter> getHeaderWriters() {
		List<HeaderWriter> writers = new ArrayList<>();
		addIfNotNull(writers, contentTypeOptions.writer);
		addIfNotNull(writers, xssProtection.writer);
		addIfNotNull(writers, cacheControl.writer);
		addIfNotNull(writers, hsts.writer);
		addIfNotNull(writers, frameOptions.writer);
		addIfNotNull(writers, hpkp.writer);
		addIfNotNull(writers, contentSecurityPolicy.writer);
		addIfNotNull(writers, referrerPolicy.writer);
		addIfNotNull(writers, featurePolicy.writer);
		writers.addAll(headerWriters);
		return writers;
	}

	private <T> void addIfNotNull(List<T> values, T value) {
		if (value != null) {
			values.add(value);
		}
    }





    /**************************************************************************************************
     * ContentSecurityPolicyConfig
     **************************************************************************************************/
	/**
	 * # https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Referrer-Policy
     * # 참조자 정보: https://developer.mozilla.org/en-US/docs/Web/Security/Referer_header:_privacy_and_security_concerns
     * # 링크 관계: https://developer.mozilla.org/en-US/docs/Web/HTML/Link_types
     * # https://www.w3.org/TR/referrer-policy/
     *
     * # Referrer-Policy 헤더
     * - 요청에 얼마나 많은 참조자(referrer) 정보가 포함되어야 하는지 제어하는 HTTP 헤더
     * - 기본값: Referrer-Policy: no-referrer
     * - ReferrerPolicyHeaderWriter 작성자 생성하여 설정
     *
     * - 지시자 목록
     *  ㄴ no-referrer
     *  ㄴ no-referrer-when-downgrade (default)
     *  ㄴ origin
     *  ㄴ origin-when-cross-origin
     *  ㄴ same-origin
     *  ㄴ strict-origin
     *  ㄴ strict-origin-when-cross-origin
     *  ㄴ unsafe-url
     *
     * - HTML 내에도 설정할 수 있다
     *  ㄴ <meta name="referrer" content="origin">
     *  ㄴ <a href="http://example.com" referrerpolicy="origin">
     *  ㄴ <area referrerpolicy="origin"></area>
     *  ㄴ <iframe referrerpolicy="origin"></iframe>
     *  ㄴ <script referrerpolicy="origin"></script>
     *  ㄴ <link referrerpolicy="origin"></link>
     *
     * - <a>, <area>, <link> HTML 태그 요소에서 noreferrer 링크 관계 설정 가능
     *  ㄴ <a href="http://example.com" rel="noreferrer">
     *
     * - stylesheet에서 참조된 자원을 CSS가 가져올 수 있는데, 이 자원들도 참조자 정책을 따른다
     *  ㄴ CSS 스타일 시트 응답 헤더에서 Referrer-Policy를 덮어쓰지 않는 한, 외부 스타일시트은 no-referrer-when-downgrade 기본 정책을 따른다
     *  ㄴ <style> 태그 요소 또는 style 속성은 소유자 도큐먼트의 참조자 정책이 적용된다
	 */
	public ReferrerPolicyConfig referrerPolicy() {
		this.referrerPolicy.writer = new ReferrerPolicyHeaderWriter();
		return this.referrerPolicy;
    }

	/**
	 * # https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Referrer-Policy
     * # https://www.w3.org/TR/referrer-policy/
     * # Referrer-Policy 헤더
     * - 기본값: Referrer-Policy: no-referrer
     * - ReferrerPolicyHeaderWriter 작성자 생성하여 설정
     * - ReferrerPolicy
	 */
	public ReferrerPolicyConfig referrerPolicy(ReferrerPolicy policy) {
		this.referrerPolicy.writer = new ReferrerPolicyHeaderWriter(policy);
		return this.referrerPolicy;
	}

	/**
	 * <p>
	 * Allows configuration for <a href="https://www.w3.org/TR/referrer-policy/">Referrer Policy</a>.
	 * </p>
	 *
	 * <p>
	 * Configuration is provided to the {@link ReferrerPolicyHeaderWriter} which support the writing
	 * of the header as detailed in the W3C Technical Report:
	 * </p>
	 * <ul>
	 *  <li>Referrer-Policy</li>
	 * </ul>
	 *
	 * @see ReferrerPolicyHeaderWriter
	 * @param referrerPolicyCustomizer the {@link Customizer} to provide more options for
	 * the {@link ReferrerPolicyConfig}
	 * @return the {@link HeadersConfigurer} for additional customizations
	 */
	public CustomHeadersConfigurer<H> referrerPolicy(Customizer<ReferrerPolicyConfig> referrerPolicyCustomizer) {
		this.referrerPolicy.writer = new ReferrerPolicyHeaderWriter();
		referrerPolicyCustomizer.customize(this.referrerPolicy);
		return CustomHeadersConfigurer.this;
	}

	public final class ReferrerPolicyConfig {

		private ReferrerPolicyHeaderWriter writer;

		private ReferrerPolicyConfig() {
		}

		/**
		 * Sets the policy to be used in the response header.
		 *
		 * @param policy a referrer policy
		 * @return the {@link ReferrerPolicyConfig} for additional configuration
		 * @throws IllegalArgumentException if policy is null
		 */
		public ReferrerPolicyConfig policy(ReferrerPolicy policy) {
			this.writer.setPolicy(policy);
			return this;
		}

		public CustomHeadersConfigurer<H> and() {
			return CustomHeadersConfigurer.this;
		}

	}

	/**
     * # https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Feature-Policy
     * # Feature-Policy
     * - 자신의 프레임, 그리고 도큐먼트의 iframe 엘리먼트 내의 컨텐츠에서 브라우저 기능 사용을 허용/거부하는 메커니즘 제공
     * - 이 헤더는 아직 실험중이며, 언제든 바뀔 수 있으므로 웹사이트에 적용하는 것에 주의
     *
     *
	 * # https://wicg.github.io/feature-policy/
     * - policyDirectives 지시자와 함께 Feature-Policy 헤더 추가
     * - 구성은 FeaturePolicyHeaderWriter 작성자에게 전달되며 작성에 책임을 진다
	 */
	public FeaturePolicyConfig featurePolicy(String policyDirectives) {
		this.featurePolicy.writer = new FeaturePolicyHeaderWriter(policyDirectives);
		return featurePolicy;
	}

	public final class FeaturePolicyConfig {

		private FeaturePolicyHeaderWriter writer;

		private FeaturePolicyConfig() {
		}

		/**
		 * Allows completing configuration of Feature Policy and continuing configuration
		 * of headers.
		 *
		 * @return the {@link HeadersConfigurer} for additional configuration
		 */
		public CustomHeadersConfigurer<H> and() {
			return CustomHeadersConfigurer.this;
		}

    }

    /**
     * TODO Expect-CT 헤더 작성 클래스 만들기
     *
     */

}