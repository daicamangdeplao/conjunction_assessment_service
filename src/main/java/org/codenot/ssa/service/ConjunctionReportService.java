package org.codenot.ssa.service;

import org.codenot.ssa.domain.ConjunctionReportJPAEntity;
import org.codenot.ssa.repository.ConjunctionReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConjunctionReportService {

    private final ConjunctionReportRepository conjunctionReportRepository;

    public ConjunctionReportService(ConjunctionReportRepository conjunctionReportRepository) {
        this.conjunctionReportRepository = conjunctionReportRepository;
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
