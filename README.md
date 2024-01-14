
### Spring Batch 5.0 Migration Guide (spring-projects)

https://github.com/spring-projects/spring-batch/wiki/Spring-Batch-5.0-Migration-Guide

---

program arguments

```
--job.name=validatedParamJob fileName=test.csv
```

---

## Spring Batch Error Collections.

### 1. REGISTERED JOB, STEP, TASKLET BY AS SPRING BEAN. BUT, NOT WORK TASKLET

- Job, Step, Tasklet 이 빈으로 등록되었지만 Job 이 실행되지 않는 문제

https://stackoverflow.com/questions/75287102/spring-batch-5-0-with-spring-boot-tasklet-job-not-starting-automatically

---

### 2. JobParameter 사용 시 null 이 담기는 문제

JobParameter 는 Spring Batch 를 실행할 때 내외부에서 받는 파라미터를 뜻하고, JobScope 또는 StepScope 애너테이션이 필요 

https://oingdaddy.tistory.com/417

<br/>

추가적으로 발생 가능한 원인) Configuration <> Component

https://oingdaddy.tistory.com/417

---

### 3. 배치1 - 성공, 배치2 - 실패 에 대한걸 두번 테스트 하다가 발생한 상황

```
Step already complete or not restartable, so no action to execute
```

위와 같은 내용의 로그가 남았고, 로그 결과로 봤을 때에는 이미 실행되었다는 이유로 Tasklet 이 실행되지 않은걸로 보임<br/>
나중에 Job과 관련된 것과 메타테이블에 대한 것 학습 필요

해당 경우에도 실행되게 하게끔 옵션 추가함
```java
.allowStartIfComplete(true)
```
```java
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
```

https://stackoverflow.com/questions/63694023/springbatch-step-no-longer-executing-step-already-complete-or-not-restartable

---

REFERENCE

https://europani.github.io/spring/2023/06/26/052-spring-batch-version5.html
