# Flyway

[![github actions](https://img.shields.io/badge/github_actions-flyway-brightgreen.svg)](https://github.com/bitmc/flyway/actions)
[![maven central](https://img.shields.io/badge/maven_central-flyway-blue.svg)](https://mvnrepository.com/artifact/com.github.bitmc/flyway)
[![javadoc](https://img.shields.io/badge/javadoc-flyway-blue.svg)](https://www.javadoc.io/doc/com.github.bitmc/flyway)

* Custom implementation of [Flyway](https://github.com/flyway/flyway)

## Usage

1. Add a dependency in your project.

	```xml
	<dependency>
	    <groupId>com.github.bitmc</groupId>
	    <artifactId>flyway-core</artifactId>
	    <version>7.1.1.0</version>
	</dependency>
	```

1. Implement `SqlReplacer`. You can also use `MysqlH2SqlReplacer`.

	```java
	package my.project;
	
	import org.flywaydb.core.internal.sqlscript.SqlReplacer;
	
	public class MySqlReplacer implements SqlReplacer {
	    
	    public String replace(String sql) {
	        ......
	    }
	}
	```

1. Setup `CustomFlyway` instance.

	```java
	import org.flywaydb.core.CustomFlyway;
	import org.flywaydb.core.api.configuration.FluentConfiguration;
	import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
	import my.project.MySqlReplacer;
	
	FluentConfiguration config = Flyway.configure()
	    .locations("classpath:/data/migration")
	    .schemas("example")
	    .sqlMigrationPrefix("")
	    .sqlMigrationSeparator("-")
	    .dataSource(DataSourceBuilder.create().url("jdbc:h2:mem:test;MODE=MySQL;DATABASE_TO_UPPER=FALSE").build());
	
	CustomFlyway flyway = new CustomFlyway(config, new MySqlReplacer());
	flyway.clean();
	flyway.migrate();
	```

## License

* Apache License 2.0.
