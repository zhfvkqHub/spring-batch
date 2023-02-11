package com.zhfvkq.springbatch.repeat;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.exception.ExceptionHandler;
import org.springframework.batch.repeat.exception.SimpleLimitExceptionHandler;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class RepeatConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job(){
        return this.jobBuilderFactory.get("repeatJob")
                .start(step1())
                .next(step2())
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<String, String>chunk(5)
                .reader(new ListItemReader<>(Arrays.asList("item1", "item2", "item3", "item4", "item5")))
                .processor((ItemProcessor<String, String>) item -> {

                    RepeatTemplate repeatTemplate = new RepeatTemplate();

                    // 3번까지만 반복함
                    repeatTemplate.setCompletionPolicy(new SimpleCompletionPolicy(3));
                    repeatTemplate.iterate(context -> {
                        System.out.println("repeatTemplate is testing");
                        return RepeatStatus.CONTINUABLE;
                    });
                    // 3번의 예외까지 재 실행함
                    repeatTemplate.setExceptionHandler(simpleLimitExceptionHandler(3));

                    System.out.println("item = " + item);
                    return item;
                })
                .writer(items -> {
                    System.out.println("items = " + items);
                })
                .build();
    }

    private ExceptionHandler simpleLimitExceptionHandler(int cnt) {
        return new SimpleLimitExceptionHandler(cnt);
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step2 was executed");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

}
