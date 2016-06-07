package com.marklogic.spring.batch.core.repository;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.restapi.RestApi;

public class MarkLogicSimpleJobRepositoryConfig {

    private API api;

    public ManageClient getManageClient() {
        return manageClient;
    }

    private ManageClient manageClient;

    public String getName() {
        return NAME;
    }

    private final String NAME = "spring-batch";
    private final String DATABASE_JSON = "{\"database-name\": \"spring-batch-content\",  \"maintain-last-modified\": true,  \"uri-lexicon\": true,  \"collection-lexicon\": true,   \"triple-index\": true,    \"range-element-index\": [{\"scalar-type\": \"dateTime\",\"namespace-uri\": \"http://marklogic.com/spring-batch\",\"localname\": \"createDateTime\",\"collation\": \"\",\"range-value-positions\": false,\"invalid-values\": \"reject\"}, {\"scalar-type\": \"unsignedLong\",\"namespace-uri\": \"http://marklogic.com/spring-batch\",\"localname\": \"id\",\"collation\": \"\",\"range-value-positions\": false,\"invalid-values\": \"reject\"  }, {\"scalar-type\": \"string\",\"namespace-uri\": \"http://marklogic.com/spring-batch\",\"localname\": \"jobName\",\"collation\": \"http://marklogic.com/collation/codepoint\",\"range-value-positions\": false,\"invalid-values\": \"reject\"  }  ],  \"path-namespace\": [{\"prefix\": \"msb\",\"namespace-uri\": \"http://marklogic.com/spring-batch\"}],  \"range-path-index\": [{\"scalar-type\": \"unsignedLong\",\"collation\": \"\",\"path-expression\": \"/msb:mlJobInstance/msb:jobExecutions/msb:jobExecution/msb:id\",\"range-value-positions\": false,\"invalid-values\": \"reject\"}]}";

    public RestApi getRestApi(int port) {
        API api = new API(manageClient);
        RestApi restApi = api.restApi(NAME);
        restApi.setGroup("Default");
        restApi.setPort(port);
        restApi.setDatabase(NAME + "-content");
        restApi.setModulesDatabase(NAME + "-modules");
        restApi.setXdbcEnabled(true);
        restApi.setForestsPerHost(1);
        restApi.setErrorFormat("json");
        return restApi;
    }

    public String getDatabase() {
        return DATABASE_JSON;
    }

    public MarkLogicSimpleJobRepositoryConfig(ManageClient manageClient) {
        this.manageClient = manageClient;
        this.api = new API(manageClient);
    }

}
