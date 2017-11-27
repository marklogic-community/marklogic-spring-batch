import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;

import java.util.List;

@EnableBatchProcessing
public class YourTwoStepJobConfig {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    //Rename this private variable
    private final String JOB_NAME = "twoStepJob";

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step1, Step step2) {
        return jobBuilderFactory.get(JOB_NAME)
                .start(step1)
                .next(step2)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    @JobScope
    public Step step1(StepBuilderFactory stepBuilderFactory) {

        ItemReader<String> reader = new ItemReader<String>() {
            int i = 0;

            @Override
            public String read() throws Exception {
                i++;
                return i == 10 ? "hello" : null;
            }
        };

        ItemWriter<String> writer = new ItemWriter<String>() {

            private StepExecution stepExecution;

            @Override
            public void write(List<? extends String> items) throws Exception {

                for (String item : items) {
                    logger.info("step1: " + item);
                }
            }

            @BeforeStep
            public void beforeStep(StepExecution stepExecution) {
                logger.info("step1: beforeStep");
                this.stepExecution = stepExecution;
                ExecutionContext stepContext = this.stepExecution.getExecutionContext();
                stepContext.putString("someKey", "key-123");
            }

        };


        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[]{"someKey"});

        return stepBuilderFactory.get("step1")
                .<String, String>chunk(10)
                .reader(reader)
                .writer(writer)
                .listener(listener)
                .listener(writer)
                .build();
    }

    @Bean
    @JobScope
    public Step step2(StepBuilderFactory stepBuilderFactory) {
        ItemReader<String> reader = new ItemReader<String>() {
            int i = 0;

            @Override
            public String read() throws Exception {
                i++;
                return i == 1 ? "hello" : null;
            }
        };


        ItemWriter<String> writer = new ItemWriter<String>() {
            String someString;

            @BeforeStep
            public void beforeStep(StepExecution stepExecution) {
                JobExecution jobExecution = stepExecution.getJobExecution();
                ExecutionContext jobContext = jobExecution.getExecutionContext();
                this.someString = jobContext.getString("someKey");
            }

            @Override
            public void write(List<? extends String> items) throws Exception {
                logger.info("Step 2: " + someString);
                for (String item : items) {
                    logger.info("step2: " + item);
                }
            }

        };


        return stepBuilderFactory.get("step2")
                .<String, String>chunk(10)
                .reader(reader)
                .writer(writer)
                .listener(writer)
                .build();
    }


}
