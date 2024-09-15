## 요구 사항
- 테이블 -> 테이블 배치
- 엑셀 -> 테이블 배치
- 테이블 -> 엑셀 배치
- 웹API -> 테이블 배치

---

## 프로젝트 세팅
- Spring Batch 5.1.2
- MySQL 8.3.0
- JDBC API
- Spring Data JPA
- Lombok

---

### 스프링 배치 모식도

![img.png](img.png)

- JobRepository: Job과 Step의 메타데이터를 저장하는 레포지토리

### (1) 테이블 마이그레이션 - 동일 DB 내 테이블 to 데이터

- Read : BeforeEntity 테이블에서 읽어오는 Reader
- Process : 읽어온 데이터를 처리하는 Process
- Write : AfterEntity에 처리한 결과를 저장하는 Writer

```java
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
```

### (2) 실행 및 스케줄 - API or Schedule

- 방법1: 요청을 Handler로 받아서 실행하는 방법
- 방법2: 스케줄러를 통해 주기적으로 실행하는 방법

자동 실행 방지되도록 설정했기 때문에 별도 트리거가 되게 설정해야 함
```yaml
spring:
  batch:
    job:
      enabled: false # 배치 자동 실행 방지 - 애플리케이션이 시작될 때 배치가 실행되지 않도록 하기 위함
```

- JobLauncher: Job을 실행하는 인터페이스
- JobRegistry: Job을 등록하고 조회하는 인터페이스

