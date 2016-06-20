package com.marklogic.spring.batch.config;

import com.marklogic.client.helper.LoggingObject;
import com.marklogic.spring.batch.item.DirectoryReader;
import com.marklogic.spring.batch.item.shapefile.ShapefileAndJson;
import com.marklogic.spring.batch.item.shapefile.ShapefileAndJsonWriter;
import com.marklogic.spring.batch.item.shapefile.ShapefileProcessor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.util.List;

public class ImportShapefilesTest extends AbstractJobsJobTest {

    @Test
    public void test() {
        runJob(ImportShapefilesConfig.class, "--input_file_path", "build/shapefiles");
    }

    public static class ImportShapefilesConfig extends AbstractMarkLogicBatchConfig {

        @Bean
        public Job job(@Qualifier("step1") Step step1) {
            return jobBuilderFactory.get("importShapefilesJob").start(step1).build();
        }

        @Bean
        @JobScope
        protected Step step1(
                @Value("#{jobParameters['input_file_path']}") String inputFilePath) {

            DirectoryReader reader = new DirectoryReader(new File(inputFilePath));

            return stepBuilderFactory.get("step1")
                    .<File, ShapefileAndJson>chunk(10)
                    .reader(reader)
                    .processor(new ShapefileProcessor())
                    .writer(new ShapefileAndJsonWriter(getDatabaseClient()))
                    .build();
        }
    }
}
