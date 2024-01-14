
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

REFERENCE

https://europani.github.io/spring/2023/06/26/052-spring-batch-version5.html
