package org.tally.batchtutorial.job.jobListener;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.tally.batchtutorial.job.jobListener.listener.JobLoggerListener;

@Configuration
public class JobListenerConfig {

    @Bean
    public Job jobListenerJob(
            JobRepository jobRepository,
            Step jobListenerStep
    ) {
        return new JobBuilder("jobListenerJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(new JobLoggerListener())
                .start(jobListenerStep)
                .build();
    }

    @Bean
    public Step jobListenerStep(
            JobRepository jobRepository,
            Tasklet jobListenerTasklet,
            PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("jobListenerStep", jobRepository)
                .tasklet(jobListenerTasklet, transactionManager) // or .chunk(chunkSize, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Tasklet jobListenerTasklet() {
        return (contribution, chunkContext) -> {
//            throw new Exception("Invoke Exception"); // EXCEPTION CASE (BatchStatus == FAILED)
            return RepeatStatus.FINISHED;
        };
    }

}
