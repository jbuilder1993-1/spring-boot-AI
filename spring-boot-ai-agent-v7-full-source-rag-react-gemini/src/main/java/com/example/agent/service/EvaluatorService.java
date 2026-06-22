package com.example.agent.service;

import com.example.agent.dto.ReActTrace;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EvaluatorService {
    private final ChatClient evaluator;
    public EvaluatorService(@Qualifier("evaluatorClient") ChatClient evaluator) { this.evaluator = evaluator; }
    public String evaluate(String request, String answer, List<ReActTrace> trace) { return evaluator.prompt().user("Request:" + request + "\nAnswer:" + answer + "\nTrace:" + trace + "\nEvaluate.").call().content(); }
}
