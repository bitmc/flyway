/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.flywaydb.core.internal.sqlscript;

import java.util.regex.Pattern;

import org.flywaydb.core.api.logging.Log;
import org.flywaydb.core.api.logging.LogFactory;
import org.flywaydb.core.internal.util.StringUtils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * {@link SqlReplacer}: MySQL-H2
 */
public class MysqlH2SqlReplacer implements SqlReplacer {
	
	/**
	 * {@link Log}
	 */
	private static final Log logger = LogFactory.getLog(MysqlH2SqlReplacer.class);
	
	/**
	 * LF
	 */
	private static final String LF = "\n";
	
	@Override
	public String replace(String sql) {
		
		InternalSqlReplacer replacer = new InternalSqlReplacer(sql);
		
		// Remove 'SET ...@...'
		replacer.remove("(?m)^SET(?:\\s+)(?:.*?)@(?:.*?)$");
		
		// Remove 'LOCK' and 'UNLOCK'
		replacer.remove("(?m)^LOCK(?:\\s+)TABLES(?:\\s+)(?:.*?)$");
		replacer.remove("(?m)^UNLOCK(?:\\s+)TABLES(?:.*?)$");
		
		// Replace 'RENAME /* CONSTRAINT */ INDEX' to 'RENAME CONSTRAINT'
		replacer.replace("RENAME(?:\\s*)/\\*(?:\\s*)CONSTRAINT(?:\\s*)\\*/(?:\\s*)(INDEX|KEY)", "RENAME CONSTRAINT");
		
		// Replace 'DROP /* CONSTRAINT */ INDEX' to 'DROP CONSTRAINT'
		replacer.replace("DROP(?:\\s*)/\\*(?:\\s*)CONSTRAINT(?:\\s*)\\*/(?:\\s*)(INDEX|KEY)", "DROP CONSTRAINT");
		
		// Remove 'AFTER'
		replacer.replace("(?:\\s+)AFTER(?:\\s+)(?:[^;]+)", "");
		
		// Remove 'CHARACTER SET' in column
		replacer.remove("(?:\\s*)CHARACTER(?:\\s+)SET(?:\\s+)'(?:.*?)'(?:\\s+)COLLATE(?:\\s+)'(?:.*?)'");
		replacer.remove("(?:\\s*)CHARACTER(?:\\s+)SET(?:\\s+)'(?:.*?)'");
		
		// Remove comment
		replacer.remove("(?m)^--(?:.*?)$");
		
		// Remove newlines
		replacer.replace("\r\n", LF);
		replacer.replace("\r", LF);
		replacer.replace("\n\n\n+", LF + LF);
		replacer.replace("^\n+", LF);
		replacer.replace("\n+$", LF);
		
		String result = replacer.toString();
		
		logger.debug(String.format("SQL: %s", result));
		
		return result;
	}
	
	/**
	 * Internal SQL replacer
	 */
	@RequiredArgsConstructor
	protected static class InternalSqlReplacer {
		
		/**
		 * SQL
		 */
		@NonNull
		private String sql;
		
		/**
		 * Remove pattern in SQL
		 * 
		 * @param pattern pattern
		 * @return {@link InternalSqlReplacer}
		 */
		public InternalSqlReplacer remove(String pattern) {
			
			return this.replace(pattern, "");
		}
		
		/**
		 * Replace pattern in SQL
		 * 
		 * @param pattern pattern
		 * @param replacement replacement
		 * @return {@link InternalSqlReplacer}
		 */
		public InternalSqlReplacer replace(String pattern, String replacement) {
			
			if (StringUtils.hasText(this.sql)) {
				
				this.sql = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE)
				/* @formatter:off */
					.matcher(this.sql)
					.replaceAll(replacement);
					/* @formatter:on */
			}
			
			return this;
		}
		
		@Override
		public String toString() {
			
			return this.sql;
		}
	}
}
