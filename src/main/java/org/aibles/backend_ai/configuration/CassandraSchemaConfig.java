package org.aibles.backend_ai.configuration;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CassandraSchemaConfig {

    private final CqlSession cqlSession;

    public CassandraSchemaConfig(CqlSession cqlSession) {
        this.cqlSession = cqlSession;
    }

    @Bean
    public InitializingBean initializingCassandraSchema() {
        return () -> {

            cqlSession.execute(
                    """
                          CREATE KEYSPACE IF NOT EXISTS backend_ai
                          WITH replication = {'class' : 'SimpleStrategy', 'replication_factor' : 1};
                          """
            );

            cqlSession.execute(
                    "USE backend_ai"
            );

            cqlSession.execute(
                    """
                          CREATE TABLE IF NOT EXISTS conversation(
                          month_bucket VARCHAR,
                          id VARCHAR,
                          title TEXT,
                          created_at TIMESTAMP,
                          PRIMARY KEY ((month_bucket), created_at)
                          ) WITH CLUSTERING ORDER BY (created_at DESC)
                          """
            );

            cqlSession.execute(
                    """
                            CREATE TABLE IF NOT EXISTS conversation_metadata(
                            conversation_id VARCHAR PRIMARY KEY,
                            model VARCHAR
                            )
                            """
            );

            cqlSession.execute(
                    """
                          CREATE TABLE IF NOT EXISTS conversation_message(
                          id VARCHAR,
                          conversation_id VARCHAR,
                          question TEXT,
                          answer TEXT,
                          created_at TIMESTAMP,
                          PRIMARY KEY ((conversation_id), created_at)
                          ) WITH CLUSTERING ORDER BY (created_at DESC)
                          """
            );
        };
    }
}
