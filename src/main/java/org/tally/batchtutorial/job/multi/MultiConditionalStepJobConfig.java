package org.tally.batchtutorial.job.multi;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class MultiConditionalStepJobConfig {

    @Bean
    public Job multiConditionalStepJob(
            JobRepository jobRepository,
            Step startStep,
            Step failStep,
            Step allStep,
            Step completeStep) {

        return new JobBuilder("multiConditionalStepJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(startStep)
                    .on("FAILED")
                    .to(failStep)
                .from(startStep)
                    .on("COMPLETED")
                    .to(completeStep)
                .from(startStep)
                    .on("*")
                    .to(allStep)
                .end()
                .build();
    }

    @JobScope
    @Bean
    public Step startStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("startStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("conditional Start Step");
                    return RepeatStatus.FINISHED;
//                    throw new Exception("Exception!!");
                }, transactionManager)
                .build();
    }

    @JobScope
    @Bean
    public Step allStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("allStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("conditional All Step");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @JobScope
    @Bean
    public Step failStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("failStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("conditional Fail Step");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @JobScope
    @Bean
    public Step completeStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("completeStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("conditional Completed Step");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
