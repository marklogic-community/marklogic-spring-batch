package com.marklogic.client.spring.batch.corb;

import org.junit.Test;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;

import com.marklogic.client.spring.batch.AbstractBatchTest;

public class CorbJobTest extends AbstractBatchTest {

    private ItemReader<String> reader;
    private ItemWriter<String> writer;

    @Test
    public void corbTest() {
        givenACorbReaderAndWriter();
        whenTheJobIsRun();

        // TODO Should add some assertions based on what the process module does.
    }

    private void givenACorbReaderAndWriter() {
        reader = new MarkLogicItemReader<String>(getClient(), "/ext/corb/uris.xqy");
        writer = new MarkLogicItemWriter<String>(getClient(), "/ext/corb/process.xqy");
    }

    private void whenTheJobIsRun() {
        launchJobWithStep(
                stepBuilderFactory.get("testStep").<String, String> chunk(10).reader(reader).writer(writer).build());
    }
}
