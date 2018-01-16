package com.marklogic.spring.batch.item.writer;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.ExtensionLibrariesManager;
import com.marklogic.client.ext.DatabaseClientConfig;
import com.marklogic.client.ext.helper.DatabaseClientProvider;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.ext.spring.SimpleDatabaseClientProvider;
import com.marklogic.junit.ClientTestHelper;
import com.marklogic.spring.batch.test.AbstractSpringBatchTest;
import com.marklogic.spring.batch.test.SpringBatchNamespaceProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContextConfiguration(classes = {com.marklogic.spring.batch.config.MarkLogicConfiguration.class})
public class InvokeModuleItemWriterTest extends AbstractSpringBatchTest {

    final String MODULE_PATH = "/ext/test.xqy";
    InvokeModuleItemWriter itemWriter;
    ExtensionLibrariesManager libMgr;
    ClientTestHelper clientTestHelper;

    @Autowired
    public void setClientTestHelper(DatabaseClientConfig batchDatabaseClientConfig) {
        clientTestHelper = new ClientTestHelper();
        DatabaseClientProvider databaseClientProvider = new SimpleDatabaseClientProvider(batchDatabaseClientConfig);
        clientTestHelper.setDatabaseClientProvider(databaseClientProvider);
        clientTestHelper.setNamespaceProvider(new SpringBatchNamespaceProvider());
        return;
    }

    @Before
    public void setup() {
        DatabaseClient client = getClient();
        libMgr = client.newServerConfigManager().newExtensionLibrariesManager();
        StringHandle handle = new StringHandle("xdmp:document-insert('hello.xml', <hello />, (), 'test')").withFormat(Format.TEXT);
        libMgr.writeAs(MODULE_PATH, handle);
        itemWriter = new InvokeModuleItemWriter(client, MODULE_PATH);
    }

    @Test
    public void invokeModuleItemWriterTest() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
        maps.add(map);
        itemWriter.write(maps);
        clientTestHelper.assertInCollections("hello.xml", "test");

    }

    @After
    public void teardown() {
        libMgr.delete(MODULE_PATH);
    }
}
