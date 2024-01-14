package org.tally.batchtutorial.job.dbMigration;

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
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.*;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;
import org.tally.batchtutorial.job.core.domain.account.Account;
import org.tally.batchtutorial.job.core.domain.account.AccountRepository;
import org.tally.batchtutorial.job.core.domain.order.Order;
import org.tally.batchtutorial.job.core.domain.order.OrderRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 주문 테이블 -> 정산 테이블 데이터 이관
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class TrMigrationConfig {

    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;

    @Bean
    public Job trMigrationJob(
            JobRepository jobRepository,
            Step trMigrationStep
    ) {
        return new JobBuilder("trMigrationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(trMigrationStep)
                .build();
    }

    /**
     * -----------------------
     *  Request ( Given )
     * -----------------------
     * new StepBuilder("trMigrationStep", jobRepository)
     *       .<Order, Order> chunk(5, transactionManager) // 한 번에 처리할 데이터의 개수
     *       .reader(orderReader)
     *       .writer(chunk -> {
     *           final List<Order> items = chunk.getItems();
     *           items.forEach(System.out::println);
     *       })
     *       .build();
     *
     * -----------------------
     *  Execute Result
     * -----------------------
     * Hibernate:
     *     select
     *         o1_0.id,
     *         o1_0.order_date,
     *         o1_0.order_item,
     *         o1_0.price
     *     from
     *         orders o1_0
     *     order by
     *         o1_0.id
     *     limit
     *         ?, ?
     * Hibernate:
     *     select
     *         count(o1_0.id)
     *     from
     *         orders o1_0
     * Order(id=1, orderItem=카카오 선물, price=15000, orderDate=2022-03-01)
     * Order(id=2, orderItem=배달주문, price=18000, orderDate=2022-03-01)
     * Order(id=3, orderItem=교보문고, price=14000, orderDate=2022-03-02)
     * Order(id=4, orderItem=아이스크림, price=3800, orderDate=2022-03-03)
     * Order(id=5, orderItem=치킨, price=21000, orderDate=2022-03-04)
     */
    @Bean
    @JobScope
    public Step trMigrationStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader orderReader,
            ItemProcessor orderToAccountProcessor,
            ItemWriter accountWriter
//           , ItemWriter itemWriter
    ) {
        return new StepBuilder("trMigrationStep", jobRepository)
                .<Order, Account> chunk(5, transactionManager) // 한 번에 처리할 데이터의 개수
                .reader(orderReader)
                .processor(orderToAccountProcessor) // order -> account 변환
                .writer(accountWriter)    // RepositoryItemWriter - 변환된 정산(account)을 DB 에 마이그레이션
//                .writer(itemWriter())       // ItemWriter - 변환된 정산(account)을 DB 에 마이그레이션

//                .writer(chunk -> { // Iterate Println
//                    final List<Order> items = chunk.getItems();
//                    items.forEach(System.out::println);
//                })
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<Order> orderReader() {
        return new RepositoryItemReaderBuilder<Order>()
                .name("orderReader")
                .repository(orderRepository)
                .methodName("findAll")
                .pageSize(5) // chunk size 와 page size 를 동일
                .arguments(Arrays.asList())
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Order, Account> orderToAccountProcessor() {
        return order -> new Account(order);
    }

    @Bean
    @StepScope
    public RepositoryItemWriter<Account> accountWriter() {
        return new RepositoryItemWriterBuilder<Account>()
                .repository(accountRepository)
                .methodName("save")
                .build();
    }

    public ItemWriter<Account> itemWriter() {
        return chunk -> accountRepository.saveAll(chunk.getItems());
    }

}
