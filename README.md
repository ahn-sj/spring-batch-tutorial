
### Spring Batch 5.0 Migration Guide (spring-projects)

```html
JobBuilderFactory and StepBuilderFactory bean exposure/configuration
JobBuilderFactory and StepBuilderFactory are not exposed as beans in the application context anymore, and are now deprecated for removal in v5.2 in favor of using the respective builders they create.
```

https://github.com/spring-projects/spring-batch/wiki/Spring-Batch-5.0-Migration-Guide

---

### REGISTERED JOB, STEP, TASKLET BY AS SPRING BEAN. <br/>
BUT, NOT WORK TASKLET

https://stackoverflow.com/questions/75287102/spring-batch-5-0-with-spring-boot-tasklet-job-not-starting-automatically

---

<img width="553" alt="image" src="https://github.com/ahn-sj/spring-batch-tutorial/assets/64416833/1c8bee21-4efa-4910-9aed-58bf4bfc9919">

---

REFERENCE

https://europani.github.io/spring/2023/06/26/052-spring-batch-version5.html
