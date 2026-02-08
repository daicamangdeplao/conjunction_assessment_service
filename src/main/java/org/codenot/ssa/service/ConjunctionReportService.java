package org.codenot.ssa.service;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import org.codenot.ssa.domain.ConjunctionReportJPAEntity;
import org.codenot.ssa.repository.ConjunctionReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ConjunctionReportService {

    private final ConjunctionReportRepository conjunctionReportRepository;
    // If you use the LangChain4j Anthropic Spring Boot Starter, it auto-registers a ChatModel bean for Claude.
    // So you do not need a separate @Bean in your configuration class for Claude, i.e., the starter handles it.
    private final ChatModel chatModel;

    public ConjunctionReportService(ConjunctionReportRepository conjunctionReportRepository, ChatModel chatModel) {
        this.conjunctionReportRepository = conjunctionReportRepository;
        this.chatModel = chatModel;
    }

    public String generateReport() {
        // 1) Retrieve context, the context should be collected from Vector DB
        /**
         *      List<VectorStoreRetriever.Hit> hits = retriever.retrieve(question);
         *
         *      StringBuilder context = new StringBuilder();
         *      hits.stream().limit(5)
         *      .forEach(hit -> context.append(hit.text()).append("\n"));
         * */
        String context = """
                LangChain4j is a Java library for building LLM-powered applications.
                It supports RAG, agents, tools, and memory.
                """;

        // 2) Build a prompt
        String prompt = """
                You are a helpful assistant.
                Use the following context to answer the question.
                
                Context:
                {{context}}
                
                Question:
                {{question}}
                """;

        PromptTemplate template = PromptTemplate.from(prompt);

        Prompt finalPrompt = template.apply(Map.of(
                "context", context,
                "question", "Tell me more about the Situational Space Awareness"
        ));

        // 3) Call Claude
        return chatModel.chat(finalPrompt.text());
    }

    public List<ConjunctionReportJPAEntity> searchSimilarReports(float[] embedding) {
        return conjunctionReportRepository.findMostSimilar(embedding);
    }

    public ConjunctionReportJPAEntity saveReport(Long a, Long b, String text, float[] embedding) {
        ConjunctionReportJPAEntity report = new ConjunctionReportJPAEntity();
        report.setObjectAId(a);
        report.setObjectBId(b);
        report.setReportText(text);
        report.setEmbedding(embedding);
        return conjunctionReportRepository.save(report);
    }
}
