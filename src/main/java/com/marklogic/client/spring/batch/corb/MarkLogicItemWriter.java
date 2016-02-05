package com.marklogic.client.spring.batch.corb;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.helper.LoggingObject;

public class MarkLogicItemWriter<T> extends LoggingObject implements ItemWriter<T> {

    private DatabaseClient client;
    private String processModule;

    public MarkLogicItemWriter(DatabaseClient client, String processModule) {
        this.client = client;
        this.processModule = processModule;
    }

    @Override
    public void write(List<? extends T> items) throws Exception {
        for (T uri : items) {
            ServerEvaluationCall invokeProcessModule = client.newServerEval();
            invokeProcessModule.modulePath(processModule);
            invokeProcessModule.addVariable("URI", uri.toString());
            logger.info(invokeProcessModule.evalAs(String.class));
        }
    }
}
