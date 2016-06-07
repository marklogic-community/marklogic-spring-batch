package com.marklogic.spring.batch.core.repository;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.restapi.RestApi;
import com.marklogic.mgmt.api.security.Role;
import com.marklogic.mgmt.api.security.RolePrivilege;

import java.util.ArrayList;
import java.util.List;

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
        RestApi restApi = api.restApi(NAME, port);
        restApi.setGroup("Default");
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

    public List<String> getRoles() {
        List roles = new ArrayList<String>();

        String springBatchReaderRole = "{  \"role-name\": \"spring-batch-reader\",  \"description\": \"Allow users to view Spring Batch job execution metrics\",  \"role\": [\"rest-reader\"],  \"collection\": [ \"spring-batch\" ],  \"privilege\": [    {      \"privilege-name\": \"xdbc-invoke\",      \"action\": \"http://marklogic.com/xdmp/privileges/xdbc-invoke\",      \"kind\": \"execute\"    }  ]}";

        String springBatchAdminRole = "{  \"role-name\": \"spring-batch-admin\",  \"description\": \"Allow users to view Spring Batch job execution metrics\",  \"role\": [\"spring-batch-reader\", \"rest-admin\", \"rest-extension-user\"],  \"privilege\": [    {      \"privilege-name\": \"any-uri\",      \"action\": \"http://marklogic.com/xdmp/privileges/any-uri\",      \"kind\": \"execute\"    },    {      \"privilege-name\": \"any-collection\",      \"action\": \"http://marklogic.com/xdmp/privileges/any-collection\",      \"kind\": \"execute\"    },    {      \"privilege-name\": \"xdbc:insert-in\",      \"action\": \"http://marklogic.com/xdmp/privileges/xdbc-insert-in\",      \"kind\": \"execute\"    },    {      \"privilege-name\": \"xdbc:insert\",      \"action\": \"http://marklogic.com/xdmp/privileges/xdbc-insert\",      \"kind\": \"execute\"    }  ],  \"permission\": [     {       \"role-name\": \"rest-admin\",       \"capability\": \"update\"     },     {       \"role-name\": \"rest-admin\",       \"capability\": \"read\"     },     {       \"role-name\": \"rest-extension-user\",       \"capability\": \"execute\"     }   ]}";

        String springBatchTestRole = "{  \"role-name\" : \"spring-batch-test\",  \"role\" : [ \"spring-batch-admin\"],  \"privilege\": [    {      \"privilege-name\": \"xdbc:eval-in\",      \"action\": \"http://marklogic.com/xdmp/privileges/xdbc-eval-in\",      \"kind\": \"execute\"    },    {      \"privilege-name\": \"xdbc:eval\",      \"action\": \"http://marklogic.com/xdmp/privileges/xdbc-eval\",      \"kind\": \"execute\"    },    {      \"privilege-name\": \"unprotected-collections\",      \"action\": \"http://marklogic.com/xdmp/privileges/unprotected-collections\",      \"kind\": \"execute\"    },    {      \"privilege-name\": \"xdbc:invoke\",      \"action\": \"http://marklogic.com/xdmp/privileges/xdbc-invoke\",      \"kind\": \"execute\"    },    {      \"privilege-name\": \"xdbc:invoke-in\",      \"action\": \"http://marklogic.com/xdmp/privileges/xdbc-invoke-in\",      \"kind\": \"execute\"    }  ]}";

        roles.add(springBatchReaderRole);
        roles.add(springBatchAdminRole);
        roles.add(springBatchTestRole);

        return roles;
    }

}
