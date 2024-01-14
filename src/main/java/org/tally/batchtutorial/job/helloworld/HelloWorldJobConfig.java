package org.tally.batchtutorial.job.helloworld;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class HelloWorldJobConfig {

    private final Environment env;

    @Bean
    public Job helloWorldJob(
            JobRepository jobRepository,
            Step helloWorldStep
    ) {
//        final String property = env.getProperty("job.name");
//        System.out.println("property = " + property);
        return new JobBuilder("helloWorldJob", jobRepository)
                .start(helloWorldStep)
                .build();
    }

    @Bean
    public Step helloWorldStep(
            JobRepository jobRepository,
            Tasklet helloWorldTasklet,
            PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("helloWorldStep", jobRepository)
                .tasklet(helloWorldTasklet, transactionManager) // or .chunk(chunkSize, transactionManager)
                .build();
    }

    @Bean
    public Tasklet helloWorldTasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("Hello World Spring Batch.");
            return RepeatStatus.FINISHED;
        };
    }

}
