package com.marklogic.spring.batch.item.writer;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.eval.EvalResult;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.io.StringHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import org.jdom2.Namespace;
import java.util.List;
import java.util.Map;

public class InvokeModuleItemWriter implements ItemWriter<Map<String, String>> {

    protected String module;
    protected DatabaseClient databaseClient;
    protected ServerEvaluationCall invoker;
    protected List<Namespace> namespaces;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public InvokeModuleItemWriter(DatabaseClient databaseClient, String module) {
        this.module = module;
        this.databaseClient = databaseClient;
        invoker = databaseClient.newServerEval();
        invoker.modulePath(module);
    }

    public InvokeModuleItemWriter(DatabaseClient databaseClient, String module, List<Namespace> namespaces) {
        this(databaseClient, module);
        for (Namespace namespace : namespaces) {
            invoker.addNamespace(namespace.getPrefix(), namespace.getURI());
        }
    }

    @Override
    public void write(List<? extends Map<String, String>> items) throws Exception {
        for (Map<String, String> item : items) {
            for (Map.Entry<String, String> name : item.entrySet()) {
                invoker.addVariable(name.getKey(), name.getValue());
            }
            processResults(invoker.eval());

        }
    }

    public void processResults(EvalResultIterator itr) {
        String item = null;
        while (itr.hasNext()) {
            EvalResult result = itr.next();
            switch (result.getType()) {
                case STRING: {
                    item = result.getAs(String.class);
                    logger.info(item);
                    break;
                }
                default: {
                    StringHandle handle = result.get(new StringHandle());
                    logger.info(handle.get());
                    break;
                }
            }
        }
    }


}
