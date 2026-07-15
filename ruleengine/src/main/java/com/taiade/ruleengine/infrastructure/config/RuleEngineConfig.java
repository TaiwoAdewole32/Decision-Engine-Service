package com.taiade.ruleengine.infrastructure.config;

import com.taiade.ruleengine.domain.rule.RuleEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RuleEngineConfig {

    @Autowired
    private RuleLoader ruleLoader;

    @Bean
    public RuleEngine ruleEngine() throws Exception {
        return new RuleEngine(ruleLoader.loadRules("rules.json"));
    }
}
