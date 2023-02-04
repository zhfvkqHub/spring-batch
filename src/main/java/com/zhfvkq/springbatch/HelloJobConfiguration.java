package com.zhfvkq.springbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class HelloJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job helloJob(@Qualifier("helloStep1") Step helloStep1,
                        @Qualifier("helloStep2") Step helloStep2){
        return jobBuilderFactory.get("helloJob")
                .start(helloStep1)
                .next(helloStep2)
                .build();

    }

    @Bean
    public Step helloStep1() {
        return stepBuilderFactory.get("helloStep1")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(" =========================== ");
                    System.out.println(" Hello Spring Batch!! ");
                    System.out.println(" =========================== ");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step helloStep2() {
        return stepBuilderFactory.get("helloStep2")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(" =========================== ");
                    System.out.println(" Hello Spring Batch2!! ");
                    System.out.println(" =========================== ");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
