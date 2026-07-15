package com.taiade.ruleengine.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.taiade.ruleengine.domain.model.CaseData;

@Repository
public interface CaseDataRepository extends JpaRepository<CaseData, Long> {
    CaseData findByApplicantID(String applicantID);
}
