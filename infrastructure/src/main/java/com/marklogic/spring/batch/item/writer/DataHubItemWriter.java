package com.marklogic.spring.batch.item.writer;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.ServerTransform;

import java.util.Map;

public class DataHubItemWriter extends MarkLogicItemWriter {

    protected final String TRANSFORM_NAME = "run-flow";
    protected final String ENTITY_PARAM = "entity-name";
    protected final String FLOW_PARAM = "flow-name";
    protected final String JOB_ID_PARAM = "job-id";

    protected String entity;
    protected String flow;
    protected String jobId;
    protected ServerTransform serverTransform;

    public DataHubItemWriter(DatabaseClient client, String entity, String flow, String jobId) {
    	 	this(client, entity, flow, jobId, null);
    }

    public DataHubItemWriter(DatabaseClient client, String entity, String flow, String jobId, Map<String, String> transformParams) {
        super(client);
        this.entity = entity;
        this.flow = flow;
        ServerTransform serverTransform = new ServerTransform(TRANSFORM_NAME);
        serverTransform.addParameter(ENTITY_PARAM, entity);
        serverTransform.addParameter(FLOW_PARAM, flow);
        serverTransform.addParameter(JOB_ID_PARAM, jobId);
        this.setServerTransform(serverTransform);
        if (transformParams != null) {
        		for (String key : transformParams.keySet()) {
        			serverTransform.addParameter(key, transformParams.get(key));
        		}
        }
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

}
