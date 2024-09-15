package com.tally.batch.config.launcher;

import com.tally.batch.entity.AfterEntity;
import com.tally.batch.entity.BeforeEntity;
import com.tally.batch.repository.AfterRepository;
import com.tally.batch.repository.BeforeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class FirstBatch {

    private static final String FIRST_JOB = "firstJob";
    private static final String FIRST_STEP = "firstStep";
    private static final String BEFORE_READER = "beforeReader";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final BeforeRepository beforeRepository;
    private final AfterRepository afterRepository;

    @Bean
    public Job firstJob() {

        return new JobBuilder(FIRST_JOB, jobRepository) // name은 메서드 이름과 동일하게
                .start(firstStep())
                .build();
    }

    @Bean
    public Step firstStep() {
        return new StepBuilder(FIRST_STEP, jobRepository)
                .<BeforeEntity, AfterEntity> chunk(10, platformTransactionManager)
                .reader(beforeReader())
                .processor(middleProcessor())
                .writer(afterWriter())
                .build();
    }

    /**
     * 실제 쿼리가 어떻게 나가는지 확인
     * findAll + order by DESC + limit 10 으로 나가는건가,,!?
     * 아니면 List<BeforeEntity> findAll() 이렇게 나가는건가,,!?
     */
    @Bean
    public ItemReader<BeforeEntity> beforeReader() {
        return new RepositoryItemReaderBuilder<BeforeEntity>()
                .name(BEFORE_READER)
                .repository(beforeRepository)
                .methodName("findAll")
                .sorts(Map.of("id", Sort.Direction.ASC))
                .pageSize(10)
                .build();
    }

    @Bean
    public ItemProcessor<BeforeEntity, AfterEntity> middleProcessor() {
        return item -> AfterEntity.created(item);
    }

    @Bean
    public ItemWriter<AfterEntity> afterWriter() {
        return new RepositoryItemWriterBuilder<AfterEntity>()
                .repository(afterRepository)
                .methodName("save")
                .build();
    }
}
