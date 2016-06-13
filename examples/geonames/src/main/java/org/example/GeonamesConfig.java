package org.example;

import org.springframework.batch.item.file.LineMapper;

@Configuration
public class GeonamesConfig {

    @Bean
    public Job job(@Qualifier("step1") Step step1) {
        return jobBuilderFactory.get("corbJob").start(step1).build();
    }

    @Bean
    @JobScope
    protected Step step1() {
        return stepBuilderFactory.get("step1")
            .<String, String>chunk(10)
            .reader(new MarkLogicItemReader<>(getDatabaseClient(), urisModule))
            .processor(new PassThroughItemProcessor<>())
            .writer(new CorbWriter<>(databaseClientProvider.getDatabaseClient(), transformModule))
            .build();
    }

    @Bean
    protected FlatFileItemReader<Geoname> itemReader() {
        Resource resource = new FileSystemResource("resources/trades.csv");
        FlatFileItemReader<Geoname> reader = new FlatFileItemReader<Geoname>();
        reader.setLineMapper(lineMapper());
        return reader;

    }

    private LineMapper<Geoname> lineMapper() {
        return new LineMapper<Geoname>() {
            @Overrides
            public Geoname mapLine(String line, int lineNumber) {
                return new Geoname();
            }
        };
    }





}