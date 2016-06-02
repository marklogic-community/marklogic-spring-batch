package com.marklogic.spring.batch.deploy;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.mgmt.restapis.RestApiManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DeployMarkLogicJobRepository {

    private static final Log logger = LogFactory.getLog(DeployMarkLogicJobRepository.class);

    private ApplicationContext ctx;

    public static void main(String[] args) {
        new DeployMarkLogicJobRepository().createRestApi();
    }

    public void createRestApi() {
        ManageConfig config = new ManageConfig("oscar", "admin");
        ManageClient client = new ManageClient(config);
        RestApiManager mgr = new RestApiManager(client);
        String apiJson = readFile();
        mgr.createRestApi(readFile());
    }


    private String readFile() {
        try {
            Resource resource = new ClassPathResource("/rest-api.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()),1024);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            br.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
