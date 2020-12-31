/*
 * Copyright 2018-2019 the original author or authors.
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

package org.flywaydb.core;

import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.internal.sqlscript.SqlReplacer;

/**
 * {@link Flyway}: Custom
 */
public class CustomFlyway extends Flyway {
	
	/**
	 * {@link SqlReplacer}
	 */
	private final SqlReplacer sqlReplacer;
	
	/**
	 * Constructor
	 * 
	 * @param configuration {@link Configuration}
	 * @param sqlReplacer {@link #sqlReplacer}
	 */
	public CustomFlyway(Configuration configuration, SqlReplacer sqlReplacer) {
		
		super(configuration);
		
		this.sqlReplacer = sqlReplacer;
	}
	
	@Override
	protected <T> T execute(Command<T> command, boolean scannerRequired) {
		
		return super.execute(new CustomCommand<>(command, this.sqlReplacer), scannerRequired);
	}
}
