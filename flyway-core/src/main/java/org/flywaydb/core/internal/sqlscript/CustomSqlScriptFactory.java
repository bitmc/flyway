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

import java.lang.reflect.Field;

import org.flywaydb.core.api.ResourceProvider;
import org.flywaydb.core.api.resource.LoadableResource;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * {@link SqlScriptFactory}: Custom
 */
@RequiredArgsConstructor
public class CustomSqlScriptFactory implements SqlScriptFactory {
	
	/**
	 * {@link SqlScriptExecutorFactory}
	 */
	@NonNull
	private final SqlScriptFactory factory;
	
	/**
	 * {@link SqlReplacer}
	 */
	@NonNull
	private final SqlReplacer replacer;
	
	@SuppressWarnings("resource")
	@Override
	public SqlScript createSqlScript(LoadableResource resource, boolean mixed, ResourceProvider resourceProvider) {
		
		SqlScript script = this.factory.createSqlScript(resource, mixed, resourceProvider);
		
		script.getSqlStatements().forEachRemaining(target -> {
			
			try {
				
				ParsedSqlStatement statement = (ParsedSqlStatement) target;
				
				Field field = ParsedSqlStatement.class.getDeclaredField("sql");
				field.setAccessible(true);
				field.set(statement, this.replacer.replace((String) field.get(statement)));
			}
			catch (ClassCastException e) {
				
				throw new IllegalStateException("Unsupported statement class", e);
			}
			catch (ReflectiveOperationException e) {
				
				throw new IllegalStateException("Failed to access 'sql' field", e);
			}
		});
		
		return script;
	}
}
