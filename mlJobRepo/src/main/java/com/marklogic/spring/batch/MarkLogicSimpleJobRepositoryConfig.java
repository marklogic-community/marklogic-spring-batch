package com.marklogic.spring.batch;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.restapi.RestApi;
import com.marklogic.mgmt.api.security.Permission;
import com.marklogic.mgmt.api.security.ProtectedCollection;
import com.marklogic.mgmt.api.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MarkLogicSimpleJobRepositoryConfig {

    protected Logger logger = LoggerFactory.getLogger(MarkLogicSimpleJobRepositoryConfig.class);

    private API api;
    private ManageClient manageClient;

    public MarkLogicSimpleJobRepositoryConfig(ManageClient manageClient) {
        this.manageClient = manageClient;
        this.api = new API(manageClient);
    }

    public ManageClient getManageClient() {
        return manageClient;
    }

    public RestApi getRestApi(String name, int port, String group) {
        RestApi restApi = api.restApi(name, port);
        restApi.setGroup(group);
        restApi.setDatabase(name + "-content");
        restApi.setModulesDatabase(name + "-modules");
        restApi.setXdbcEnabled(true);
        restApi.setForestsPerHost(1);
        restApi.setErrorFormat("json");
        return restApi;
    }

    public String getDatabase(String name) {
        InputStream in = getClass().getResourceAsStream("/content-database.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String DATABASE_JSON = reader.lines().collect(Collectors.joining());
        return String.format(DATABASE_JSON, name + "-content");
    }

    public ProtectedCollection getProtectedCollection() {
        ProtectedCollection coll = api.protectedCollection("spring-batch");
        Permission restAdminRead = new Permission();
        restAdminRead.setRoleName("rest-admin");
        restAdminRead.setCapability("read");

        Permission restAdminUpdate = new Permission();
        restAdminUpdate.setRoleName("rest-admin");
        restAdminUpdate.setCapability("update");
        coll.addPermission(restAdminRead);
        coll.addPermission(restAdminUpdate);
        return coll;
    }

    //Lazy - just using the JSON strings instead of building the Role objects
    public List<String> getRoles() {
        List roles = new ArrayList<String>();

        String springBatchReaderRole = "{  \"role-name\": \"spring-batch-reader\",  \"description\": \"Allow users to view Spring Batch job execution metrics\",  \"role\": [\"rest-reader\"],  \"collection\": [ \"spring-batch\" ],  \"privilege\": [    {      \"privilege-name\": \"xdbc-invoke\",      \"action\": \"http://marklogic.com/xdmp/privileges/xdbc-invoke\",      \"kind\": \"execute\"    }  ]}";

        String springBatchAdminRole = "{  \"role-name\": \"spring-batch-admin\",  \"description\": \"Allow users to view Spring Batch job execution metrics\",  \"role\": [\"spring-batch-reader\", \"rest-admin\", \"rest-extension-user\"],  \"privilege\": [    {      \"privilege-name\": \"any-uri\",      \"action\": \"http://marklogic.com/xdmp/privileges/any-uri\",      \"kind\": \"execute\"    },    {      \"privilege-name\": \"any-collection\",      \"action\": \"http://marklogic.com/xdmp/privileges/any-collection\",      \"kind\": \"execute\"    },    {      \"privilege-name\": \"xdbc:insert-in\",      \"action\": \"http://marklogic.com/xdmp/privileges/xdbc-insert-in\",      \"kind\": \"execute\"    },    {      \"privilege-name\": \"xdbc:insert\",      \"action\": \"http://marklogic.com/xdmp/privileges/xdbc-insert\",      \"kind\": \"execute\"    }  ],  \"permission\": [     {       \"role-name\": \"rest-admin\",       \"capability\": \"update\"     },     {       \"role-name\": \"rest-admin\",       \"capability\": \"read\"     },     {       \"role-name\": \"rest-extension-user\",       \"capability\": \"execute\"     }   ]}";

        roles.add(springBatchReaderRole);
        roles.add(springBatchAdminRole);

        return roles;
    }

    public List<User> getUsers() {
        User springBatchAdmin = api.user("spring-batch-admin");
        springBatchAdmin.setDescription("Admin user for Spring Batch application");
        springBatchAdmin.setPassword("password");
        springBatchAdmin.addRole("spring-batch-admin");

        List<User> users = new ArrayList<User>();
        users.add(springBatchAdmin);
        return users;
    }

    public String getSpringBatchOptions() {
        InputStream in = getClass().getResourceAsStream("/options.xml");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String options = reader.lines().collect(Collectors.joining());
        logger.info(options);
        return options;
    }

}
