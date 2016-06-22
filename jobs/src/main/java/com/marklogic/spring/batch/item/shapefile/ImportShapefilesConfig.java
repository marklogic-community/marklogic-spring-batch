package com.marklogic.spring.batch.item.shapefile;

import com.marklogic.spring.batch.config.AbstractMarkLogicBatchConfig;
import com.marklogic.spring.batch.item.DirectoryReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.io.File;

public class ImportShapefilesConfig extends AbstractMarkLogicBatchConfig {

    @Bean
    public Job job(@Qualifier("step1") Step step1) {
        return jobBuilderFactory.get("importShapefilesJob").start(step1).build();
    }

    @Bean
    @JobScope
    protected Step step1(
            @Value("#{jobParameters['input_file_path']}") String inputFilePath,
            @Value("#{jobParameters['ogre_url']}") String ogreUrl,
            @Value("#{jobParameters['output_collections']}") String[] collections,
            @Value("#{jobParameters['output_permissions']}") String permissions) {

        DirectoryReader reader = new DirectoryReader(new File(inputFilePath));

        ShapefileProcessor processor = new ShapefileProcessor();
        if (ogreUrl != null) {
            processor.setUrl(ogreUrl);
        }

        ShapefileAndJsonWriter writer = new ShapefileAndJsonWriter(getDatabaseClient());
        writer.setPermissions(permissions);
        writer.setCollections(collections);

        return stepBuilderFactory.get("step1")
                .<File, ShapefileAndJson>chunk(getChunkSize())
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}