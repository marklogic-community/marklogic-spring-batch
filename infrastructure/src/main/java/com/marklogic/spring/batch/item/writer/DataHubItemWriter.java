package com.marklogic.spring.batch.item.writer;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.ServerTransform;

public class DataHubItemWriter extends MarkLogicItemWriter {

    protected final String TRANSFORM_NAME = "run-flow";
    protected final String ENTITY_PARAM = "entity";
    protected final String FLOW_PARAM = "flow";
    protected final String FLOW_TYPE_PARAM = "flowType";
    protected final String JOB_ID_PARAM = "job-id";

    protected String entity;
    protected String flow;
    protected String jobId;
    protected FlowType flowType;

    public enum FlowType {
        INPUT,
        HARMONIZE
    };

    public DataHubItemWriter(DatabaseClient client, String entity, FlowType flowtype, String flow, String jobId) {
        super(client);
        ServerTransform serverTransform = new ServerTransform(TRANSFORM_NAME);
        serverTransform.addParameter(ENTITY_PARAM, entity);
        serverTransform.addParameter(FLOW_PARAM, flow);
        if (flowType == flowType.INPUT) {
            serverTransform.addParameter(FLOW_TYPE_PARAM, "input");
        } else if (flowType == flowType.HARMONIZE) {
            serverTransform.addParameter(FLOW_TYPE_PARAM, "harmonize");
        }
        serverTransform.addParameter(JOB_ID_PARAM, jobId);
        setServerTransform(serverTransform);
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

    public FlowType getFlowType() {
        return flowType;
    }

    public void setFlowType(FlowType flowType) {
        this.flowType = flowType;
    }


}
