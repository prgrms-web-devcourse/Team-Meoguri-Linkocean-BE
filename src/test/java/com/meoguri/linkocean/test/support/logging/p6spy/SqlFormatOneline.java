package com.meoguri.linkocean.test.support.logging.p6spy;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

public class SqlFormatOneline implements MessageFormattingStrategy {

	@Override
	public String formatMessage(final int connectionId, final String now, final long elapsed, final String category,
		final String prepared, final String sql, final String url) {
		return sql;
	}
}
