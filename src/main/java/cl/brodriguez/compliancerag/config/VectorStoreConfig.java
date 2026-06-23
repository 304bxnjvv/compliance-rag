package cl.brodriguez.compliancerag.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Vector store en memoria, usado por defecto y en tests.
 * Solo se activa cuando el perfil "pg" NO está presente. Con el perfil "pg",
 * Spring AI autoconfigura un PgVectorStore y este bean no se crea, de modo que
 * la lógica de negocio nunca depende del backend concreto del vector store.
 */
@Configuration
@Profile("!pg")
public class VectorStoreConfig {

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
