package org.tally.batchtutorial.job.multi;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class MultiStepJobConfig {

    @Bean
    public Job multiStepJob(
            JobRepository jobRepository,
            Step mStep1,
            Step mStep2,
            Step mStep3
    ) {
        return new JobBuilder("multiStepJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(mStep1)
                .next(mStep2)
                .next(mStep3)
                .build();
    }

    @Bean
    @JobScope
    public Step mStep1(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        return new StepBuilder("mStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("-> step1");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    @JobScope
    public Step mStep2(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        return new StepBuilder("mStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("->-> step2");

                    ExecutionContext executionContext = chunkContext
                            .getStepContext()
                            .getStepExecution()
                            .getJobExecution()
                            .getExecutionContext();

                    executionContext.put("someKey", "hello!");

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    @JobScope
    public Step mStep3(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        return new StepBuilder("mStep3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("->->-> step3");

                    ExecutionContext executionContext = chunkContext
                            .getStepContext()
                            .getStepExecution()
                            .getJobExecution()
                            .getExecutionContext();

                    executionContext.get("someKey");

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
