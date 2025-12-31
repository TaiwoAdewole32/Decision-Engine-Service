package com.taiade.ruleengine.domain.condition;
import com.taiade.ruleengine.domain.model.CaseData;
public interface Condition {

    boolean evaluate(CaseData data);
    String explain (CaseData data);

}