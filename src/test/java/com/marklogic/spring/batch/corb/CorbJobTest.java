package com.marklogic.spring.batch.corb;

import org.junit.Test;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;

import com.marklogic.spring.batch.AbstractSpringBatchTest;
import com.marklogic.spring.batch.item.MarkLogicItemReader;

public class CorbJobTest extends AbstractSpringBatchTest {

    private ItemReader<String> reader;
    private ItemWriter<String> writer;

    @Test
    public void corbTest() {
        givenACorbReaderAndWriter();
        whenTheJobIsRun();

        // TODO Should add some assertions based on what the process module does.
    }

    private void givenACorbReaderAndWriter() {
        reader = new MarkLogicItemReader<>(getClient(), "/ext/corb/uris.xqy");
        writer = new CorbWriter<>(getClient(), "/ext/corb/process.xqy");
    }

    private void whenTheJobIsRun() {
        launchJobWithStep(
                stepBuilderFactory.get("testStep").<String, String> chunk(10).reader(reader).writer(writer).build());
    }
}
