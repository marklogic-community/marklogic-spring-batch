package com.marklogic.spring.batch.item.writer;


import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.MarkLogicWriteHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit.ClientTestHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { com.marklogic.spring.batch.config.MarkLogicApplicationContext.class })
public class MarkLogicItemWriterTest extends ClientTestHelper {

    @Autowired
    private DatabaseClientProvider databaseClientProvider;

    private MarkLogicItemWriter itemWriter;
    List<DocumentWriteOperation> handles;

    @Before
    public void setup() {
        setDatabaseClientProvider(databaseClientProvider);
        DatabaseClient databaseClient = databaseClientProvider.getDatabaseClient();
        itemWriter = new MarkLogicItemWriter(databaseClient);
        handles = new ArrayList<DocumentWriteOperation>();

        MarkLogicWriteHandle handle = new MarkLogicWriteHandle();
        handle.setUri("hello.xml");
        handle.setMetadataHandle(new DocumentMetadataHandle().withCollections("raw"));
        handle.setHandle(new StringHandle("<hello />"));
        handles.add(handle);

        MarkLogicWriteHandle handle2 = new MarkLogicWriteHandle();
        handle.setUri("hello2.xml");
        handle.setMetadataHandle(new DocumentMetadataHandle().withCollections("raw"));
        handle.setHandle(new StringHandle("<hello2 />"));
        handles.add(handle2);
    }

    @Test
    public void test() throws Exception {
        itemWriter.write(handles);
        assertInCollections("hello.xml", "raw");
        assertCollectionSize("Expecting two items in raw collection", "raw", 2);
    }
}
