package com.marklogic.spring.batch.item.tasklet;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.eval.ServerEvaluationCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class EvaluateServerSideStringTasklet implements Tasklet {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private DatabaseClient databaseClient;
    private String evalString;
    private ServerSideLanguage language;

    public enum ServerSideLanguage { JAVASCRIPT, XQUERY }

    public EvaluateServerSideStringTasklet(
            DatabaseClient databaseClient,
            ServerSideLanguage language,
            String evalString) {
        this.databaseClient = databaseClient;
        this.evalString = evalString;
        this.language = language;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        ServerEvaluationCall theCall = databaseClient.newServerEval();
        if (language.equals(ServerSideLanguage.JAVASCRIPT)) {
            theCall.javascript(evalString);
        } else if (language.equals(ServerSideLanguage.XQUERY)) {
            theCall.xquery(evalString);
        } else {
            throw new IllegalArgumentException("Server side language not recognized");
        }
        String response = theCall.evalAs(String.class);
        logger.info(evalString);
        logger.info(response);
        return RepeatStatus.FINISHED;
    }

}
