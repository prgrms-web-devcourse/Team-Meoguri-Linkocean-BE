package com.meoguri.linkocean.test.support.logging.p6spy;

import java.util.Locale;

import org.hibernate.engine.jdbc.internal.FormatStyle;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

public class SqlFormatPretty implements MessageFormattingStrategy {

	@Override
	public String formatMessage(final int connectionId, final String now, final long elapsed, final String category,
		final String prepared, final String sql, final String url) {

		String sql1 = sql;
		if (sql1 == null || sql1.trim().equals("")) {
			return sql1;
		}

		// Only format Statement, distinguish DDL And DML
		if (Category.STATEMENT.getName().equals(category)) {
			String tmpsql = sql1.trim().toLowerCase(Locale.ROOT);
			if (tmpsql.startsWith("create") || tmpsql.startsWith("alter") || tmpsql.startsWith("comment")) {
				sql1 = FormatStyle.DDL.getFormatter().format(sql1);
			} else {
				sql1 = FormatStyle.BASIC.getFormatter().format(sql1);
			}
		}
		return sql1;
	}

}
