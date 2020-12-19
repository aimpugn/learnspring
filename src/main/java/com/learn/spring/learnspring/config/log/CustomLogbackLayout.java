package com.learn.spring.learnspring.config.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;

/**
 * # LayoutBase
 * - layout의 시작했는지 종료했는지, header, footer, content type data 등 layout 인스턴스에 공통되는 상태(state) 관리
 */
public class CustomLogbackLayout extends LayoutBase<ILoggingEvent>{

    /**
     * # 로그 출력 레이아웃
     * - 자세한 내용은 기본 레이아웃 클래스 참조
     * - ch.qos.logback.classic.PatternLayout
     * - ch.qos.logback.core.pattern.PatternLayoutBase
     */
	@Override
	public String doLayout(ILoggingEvent event) {
		/**
		 * org.springframework.boot.logging.logback.ColorConverter
		 */
		StringBuffer sbuf = new StringBuffer(128);
		/**
		 * # LoggerContextVO
		 * - LoggingEvent에 의해 원격 시스템에 보여지는 제한된 LoggerContext 컨텍스트의 뷰를 제공하며, 이 뷰는 직렬화에 최적화되어 있다
		 * - 원격 시스템에 다른 값이 설정되어 있을 수 있으므로 직렬화에서 LoggerContext 컨텍스트 또는 Logger 속성의 일부(appenders, level 값 등)는 절대로 남아있으면 안 된다.
		 * - LoggerContextVO 오브젝트는 최소한의 원격 시스템과 연관된 속성들만 보여준다
		 *
		 * # getBirthTime()
		 * - 컨텍스트 생성 시 ch.qos.logback.core.ContextBase 클래스의 birthTime 필드에 System.currentTimeMillis() 값이 들어간다
		 */
		sbuf.append(event.getTimeStamp() - event.getLoggerContextVO().getBirthTime());
		sbuf.append(" ");
		sbuf.append(event.getLevel());
		sbuf.append(" [");
		sbuf.append(event.getThreadName());
		sbuf.append("] ");
		sbuf.append(event.getLoggerName());
		sbuf.append(" - ");
		sbuf.append(event.getFormattedMessage());
		sbuf.append(CoreConstants.LINE_SEPARATOR);

		return sbuf.toString();
	}
}