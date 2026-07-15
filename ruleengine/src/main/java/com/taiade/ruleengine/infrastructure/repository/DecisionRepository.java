package com.taiade.ruleengine.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.taiade.ruleengine.domain.model.DecisionRecord;
import java.util.List;

@Repository
public interface DecisionRepository extends JpaRepository<DecisionRecord, Long> {
    List<DecisionRecord> findByApplicantID(String applicantID);
    List<DecisionRecord> findByRuleSetVersion(String ruleSetVersion);
}
