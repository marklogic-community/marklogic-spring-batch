package com.marklogic.spring.batch.item.file;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AvroItemReaderTest {
    private AvroItemReader itemReader;
    private Stream<String> expectedOutputStream;

    @Before
    public void setUp() throws Exception {
        Resource sampleResource = new ClassPathResource("avro/sampleAvroSerialized.avro");
        File sampleAvroFile = sampleResource.getFile();

        this.itemReader = new AvroItemReader(sampleAvroFile);

        Resource expectedOutputResource = new ClassPathResource("avro/sampleOutput.txt");
        this.expectedOutputStream = Files.lines(expectedOutputResource.getFile().toPath());
    }

    @Test
    public void testRead() throws Exception {

        this.expectedOutputStream.forEach((String s) -> {
            try {
                assertEquals(itemReader.read(), s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        assertNull(itemReader.read());

    }

    @Test(expected = IOException.class)
    public void testIOException() throws IOException {
        File sampleAvroFile = new File(AvroItemReaderTest.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "DOESNOTEXIST");
        itemReader = new AvroItemReader(sampleAvroFile);
    }

}
