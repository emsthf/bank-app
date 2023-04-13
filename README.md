# Junit Bank App

## 기술 스택
- Springboot 2.7.10
- JPA
- JWT
- Spring Security
- H2(테스트)
- MySQL(프로덕션)
- AOP
- JUnit5



### JPA LocalDateTime 자동으로 생성
- @EnableJpaAuditing (Main 클래스)
- @EntityListeners(AuditingEntityListener.class) (Entity 클래스)
```java
@CreatedDate
@Column(nullable = false)
private LocalDateTime createdAt;

@LastModifiedDate
@Column(nullable = false)
private LocalDateTime updatedAt;
```

## Security
```java
AuthenticationManager 의존이 무한으로 의존하는 이슈가 있었다고 한다.
그래서 시큐리티 설정은 이제 WebSecurityConfigurerAdapter를 상속하지 않고 
@Configuration 클래스안에 @Bean으로 설정한다.
그리고 필터 설정은 전부 내부 클래스를 만들어서 AuthenticationManager를 주입받아서 필터를 설정한다.
```

### Security ROLE 프리픽스
[Spring Security Authorize 공식문서](https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html)

해당 공식문서를 들어가면 예시 코드와 설명에서 hasRole() 메서드에서 prefix를 붙여주게 되어있으니 "ROLE_" prefix를 붙이지 말라고 설명이 되어있다.
```java
.requestMatchers("/db/**").access(new WebExpressionAuthorizationManager("hasRole('ADMIN') and hasRole('DBA')"))
```

### CORS 정책 변경 공식 문서
예전엔 디폴 값이어서 생략 가능했지만 지금은 꼭 넣어줘야 하는 CorsConfigurationSource의 옵션
```java
configuration.addExposedHeader("Authorization");
```
[CORS-safelisted response header](https://developer.mozilla.org/en-US/docs/Glossary/CORS-safelisted_response_header)


### PS가 붙은 변수명
DB에서 직접 꺼낸 객체를 보존 데이터(영속화)란 의미로 persistence의 약자인 PS를 붙여서 사용했음
```java
User userPS = userRepository.findByUserId(userId);
```

## 통합(컨트롤러) 테스트 기본 어노테이션 세팅
테스트용 프로퍼티 profile을 잡고, PK를 초기화 시켜주는 SQL문을 실행. 나머진 Mock으로 환경을 구성
```java
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
```

## 레포지토리 테스트 기본 어노테이션 세팅
```java
@ActiveProfiles("test")
@DataJpaTest
```
QueryDSL을 사용한다면 빈 추가(@Import(QueryDSLConfig.class))

## 서비스 테스트 기본 어노테이션 세팅
```java
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
```

## JUnit 테스트 시 주의점
Lombok 어노테이션 사용하지 않기. Lombok은 compileOnly이기 때문에 runtime시에 작동 안한다.

### 통합테스트 MockUser 주입
```java
@WithUserDetails(value = "{주입할 username}", setupBefore = TestExecutionEvent.TEST_EXECUTION)
```
default로 TestExecutionListener.beforeTestMethod로 설정되어 있다. 이렇게 되면
@BeforeAll, @BeforeEach 실행전에 WithUserDetails가 실행되어서, DB에 User가 생기기전에 실행됨

### 서비스 테스트 시 주의점
stub 실행시점은 service 메서드 동작시점이기 때문에, read일 때는 stub이 한개만 있어도 되지만, write일 때는 stub을 단계별로 만들고 깊은 복사를 해야 한다.

### 서비스 테스트 시 사용 어노테이션들
```java
@Mock         // 진짜 객체를 추상화된 가짜 객체로 만들어서 Mockito환경에 주입함.
@InjectMocks  // Mock된 가짜 객체를 진짜 객체 UserService를 만들어서 주입함 
@MockBean     //Mock객체들을 스프링 ApplicationContext에 주입함. (IoC컨테이너 주입)
@Spy          //진짜 객체를 만들어서 Mockito환경에 주입함.
@SpyBean      // Spay객체들을 스프링 ApplicationContext에 주입함. (IoC컨테이너 주입)
```

### Security와 JWT를 함께 사용시
JWT 인증, 인가 테스트를 따로 한다.
통합 테스트에서 인증 체크는 세션값을 확인.