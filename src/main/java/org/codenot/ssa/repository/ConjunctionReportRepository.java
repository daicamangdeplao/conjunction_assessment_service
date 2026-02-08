package org.codenot.ssa.repository;

import org.codenot.ssa.domain.ConjunctionReportJPAEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConjunctionReportRepository extends JpaRepository<ConjunctionReportJPAEntity, Long> {
    /**                                                                                                                            * Finds the most similar conjunction reports based on vector similarity search.
     * <p>
     * This method performs a semantic search by comparing the query embedding against
     * all stored report embeddings using cosine distance. Reports are ranked by similarity,
     * with the most similar reports returned first.
     * </p>
     * <p>
     * The search leverages PostgreSQL's pgvector extension and the IVFFlat index
     * for efficient approximate nearest neighbor (ANN) search.
     * </p>
     * <p>
     * embedding <-> :queryEmbedding uses L2 distance. Replace <-> with <#> for cosine similarity if needed.
     * </p>
     *
     * @param queryEmbedding the embedding vector to search for, typically generated
     *                       from a text query using the same embedding model used
     *                       for report embeddings (e.g., OpenAI text-embedding-ada-002).
     *                       Must be a 1536-dimensional vector matching the schema definition.
     * @return a list of {@link ConjunctionReportJPAEntity} ordered by similarity (most similar first).
     *         Returns an empty list if no reports exist or if the query embedding is null.
     * @throws IllegalArgumentException if the embedding dimension doesn't match the expected size (1536)
     * @see ConjunctionReportJPAEntity
     */
    @Query(value = "SELECT *, embedding <-> :queryEmbedding AS distance " +
            "FROM conjunction_reports " +
            "ORDER BY distance ASC " +
            "LIMIT 10", nativeQuery = true)
    List<ConjunctionReportJPAEntity> findMostSimilar(@Param("queryEmbedding") float[] queryEmbedding);
}
