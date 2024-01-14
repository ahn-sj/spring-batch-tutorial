package org.tally.batchtutorial.job.validatedParam;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.tally.batchtutorial.job.validatedParam.validator.FileParamValidator;

import java.util.Arrays;

/**
 * 파일 이름 파라미터 전달 및 검증
 * run: --job.name=validatedParamJob -fileName=test.csv
 */
@Configuration
public class ValidatedParamJobConfig {

    @Bean
    public Job ValidatedParamJob(
            JobRepository jobRepository,
            Step validatedParamStep
    ) {
        return new JobBuilder("validatedParamJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .validator(compositeValidator())
                .start(validatedParamStep)
                .build();
    }

    private CompositeJobParametersValidator compositeValidator() {
        final CompositeJobParametersValidator compositeValidator = new CompositeJobParametersValidator();
        compositeValidator.setValidators(Arrays.asList(new FileParamValidator()));

        return compositeValidator;
    }

    @Bean
    @JobScope
    public Step validatedParamStep(
            JobRepository jobRepository,
            Tasklet validatedParamTasklet,
            PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("validatedParamStep", jobRepository)
                .tasklet(validatedParamTasklet, transactionManager) // or .chunk(chunkSize, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet validatedParamTasklet(@Value("#{jobParameters['fileName']}") String fileName) {
        return (contribution, chunkContext) -> {
            System.out.println("fileName = " + fileName);
            System.out.println("Validated Param Spring Batch.");
            return RepeatStatus.FINISHED;
        };
    }

}
