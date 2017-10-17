package com.marklogic.spring.batch.samples.geonames;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.ext.helper.DatabaseClientProvider;
import com.marklogic.spring.batch.item.writer.MarkLogicItemWriter;
import org.geonames.Geoname;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;


@EnableBatchProcessing
@Import(value = {com.marklogic.spring.batch.config.MarkLogicBatchConfiguration.class})
public class IngestGeonamesToMarkLogicJobConfig {

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, @Qualifier("ingestGeonamesStep1") Step step1) {
        return jobBuilderFactory.get("ingestGeonames").start(step1).build();
    }

    /**
     * TODO Plenty to add here, such as all the options we'd like on a writer - e.g. output collections and
     * permissions, URI options,e tc.
     *
     * @param inputFilePath
     * @return
     */
    @Bean
    @JobScope
    protected Step ingestGeonamesStep1(
            StepBuilderFactory stepBuilderFactory,
            DatabaseClientProvider databaseClientProvider,
            @Value("#{jobParameters['input_file_path']}") String inputFilePath) {
        FlatFileItemReader<Geoname> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(inputFilePath));
        DefaultLineMapper<Geoname> mapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
        tokenizer.setQuoteCharacter('{');
        mapper.setLineTokenizer(tokenizer);
        mapper.setFieldSetMapper(new GeonameFieldSetMapper());
        reader.setLineMapper(mapper);

        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(2);

        return stepBuilderFactory.get("step1")
                .<Geoname, DocumentWriteOperation>chunk(10)
                .reader(reader)
                .processor(new GeonamesItemProcessor())
                .writer(new MarkLogicItemWriter(databaseClientProvider.getDatabaseClient()))
                .taskExecutor(taskExecutor)
                .build();
    }
}