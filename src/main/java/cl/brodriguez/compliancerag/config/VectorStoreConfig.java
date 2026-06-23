package cl.brodriguez.compliancerag.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Vector store en memoria, usado por defecto y en tests.
 * Si el perfil "pg" está activo, Spring AI autoconfigura un PgVectorStore y
 * este bean NO se crea (gracias a {@link ConditionalOnMissingBean}). Así la
 * lógica de negocio nunca depende del backend concreto del vector store.
 */
@Configuration
public class VectorStoreConfig {

    @Bean
    @ConditionalOnMissingBean(VectorStore.class)
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
