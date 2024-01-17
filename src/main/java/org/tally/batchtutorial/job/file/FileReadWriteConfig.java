package org.tally.batchtutorial.job.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.tally.batchtutorial.job.file.dto.Player;
import org.tally.batchtutorial.job.file.dto.PlayerYears;
import org.tally.batchtutorial.job.file.mapper.PlayerFieldMapper;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FileReadWriteConfig {

    @Bean
    public Job fileReadWriteJob(
            JobRepository jobRepository,
            Step fileReadWriteJobStep
    ) {
        return new JobBuilder("fileReadWriteJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(fileReadWriteJobStep)
                .build();
    }

    @Bean
    @JobScope
    public Step fileReadWriteJobStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader playerItemReader,
            ItemProcessor playerItemProcessor,
            ItemWriter playerItemWriter

    ) {
        return new StepBuilder("fileReadWriteJobStep", jobRepository)
                .<Player, PlayerYears> chunk(5, transactionManager) // 한 번에 처리할 데이터의 개수
                .reader(playerItemReader)
                .writer(chunk -> chunk.getItems().forEach(System.out::println))
                .processor(playerItemProcessor)
                .writer(playerItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<PlayerYears> playerItemWriter() {
        final BeanWrapperFieldExtractor<PlayerYears> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"id", "lastName", "position", "debutYear", "yearsExperience"});
        fieldExtractor.afterPropertiesSet();

        DelimitedLineAggregator<PlayerYears> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        FileSystemResource outputResource = new FileSystemResource("src/main/resources/static/players_output.txt");

        log.info("success");

        return new FlatFileItemWriterBuilder<PlayerYears>()
                .name("playerItemWriter")
                .resource(outputResource)
                .lineAggregator(lineAggregator)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Player, PlayerYears> playerItemProcessor() {
        return PlayerYears::new;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Player> playerItemReader() {
        return new FlatFileItemReaderBuilder<Player>()
                .name("playerItemReader")
                .resource(new FileSystemResource("src/main/resources/static/player.csv"))
                .lineTokenizer(new DelimitedLineTokenizer())
                .fieldSetMapper(new PlayerFieldMapper())
                .linesToSkip(1)
                .build();
    }

}
