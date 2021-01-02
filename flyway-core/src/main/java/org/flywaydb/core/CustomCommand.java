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

package org.flywaydb.core;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Optional;

import org.flywaydb.core.Flyway.Command;
import org.flywaydb.core.api.resolver.MigrationResolver;
import org.flywaydb.core.internal.callback.CallbackExecutor;
import org.flywaydb.core.internal.database.base.Database;
import org.flywaydb.core.internal.database.base.Schema;
import org.flywaydb.core.internal.jdbc.StatementInterceptor;
import org.flywaydb.core.internal.resolver.CompositeMigrationResolver;
import org.flywaydb.core.internal.resolver.sql.SqlMigrationResolver;
import org.flywaydb.core.internal.schemahistory.SchemaHistory;
import org.flywaydb.core.internal.sqlscript.CustomSqlScriptFactory;
import org.flywaydb.core.internal.sqlscript.SqlReplacer;
import org.flywaydb.core.internal.sqlscript.SqlScriptFactory;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * {@link Command}: Custom
 * 
 * @param <T> result type
 */
@RequiredArgsConstructor
public class CustomCommand<T> implements Command<T> {
	
	/**
	 * Delegate
	 */
	@NonNull
	private final Command<T> delegate;
	
	/**
	 * {@link SqlReplacer}
	 */
	private final SqlReplacer sqlReplacer;
	
	@Override
	public T execute(MigrationResolver migrationResolver, SchemaHistory schemaHistory,
		@SuppressWarnings("rawtypes") Database database, @SuppressWarnings("rawtypes") Schema[] schemas,
		CallbackExecutor callbackExecutor, StatementInterceptor statementInterceptor) {
		
		Optional.ofNullable(this.sqlReplacer).ifPresent(sqlReplacer -> {
			
			this.injectSqlReplacer(migrationResolver, sqlReplacer);
		});
		
		return this.delegate.execute(migrationResolver, schemaHistory, database, schemas, callbackExecutor,
			statementInterceptor);
	}
	
	/**
	 * Inject {@link SqlReplacer}
	 * 
	 * @param migrationResolver {@link MigrationResolver}
	 * @param sqlReplacer {@link SqlReplacer}
	 * @throws IllegalStateException if failed
	 */
	protected void injectSqlReplacer(MigrationResolver migrationResolver, SqlReplacer sqlReplacer)
		throws IllegalStateException {
		
		// Extract child resolvers
		if (migrationResolver instanceof CompositeMigrationResolver) {
			
			try {
				
				Field field = CompositeMigrationResolver.class.getDeclaredField("migrationResolvers");
				field.setAccessible(true);
				
				@SuppressWarnings("unchecked")
				Collection<MigrationResolver> childResolvers = (Collection<MigrationResolver>) field
					.get(migrationResolver);
				
				// Recursive call
				for (MigrationResolver childResolver : childResolvers) {
					
					this.injectSqlReplacer(childResolver, this.sqlReplacer);
				}
			}
			catch (ReflectiveOperationException e) {
				
				throw new IllegalStateException("Failed to access 'migrationResolvers' field", e);
			}
		}
		
		// Replace
		else if (migrationResolver instanceof SqlMigrationResolver) {
			
			try {
				
				Field field = SqlMigrationResolver.class.getDeclaredField("sqlScriptFactory");
				field.setAccessible(true);
				
				SqlScriptFactory value = (SqlScriptFactory) field.get(migrationResolver);
				
				field.set(migrationResolver, new CustomSqlScriptFactory(value, this.sqlReplacer));
			}
			catch (ReflectiveOperationException e) {
				
				throw new IllegalStateException("Failed to access 'sqlScriptFactory' field", e);
			}
		}
	}
}
