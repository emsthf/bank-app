# Junit Bank App

### JPA LocalDateTime 자동으로 생성하는 법
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

### Security ROLE 프리픽스
[Spring Security Authorize 공식문서](https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html)

해당 공식문서를 들어가면 예시 코드와 설명에서 hasRole() 메서드에서 prefix를 붙여주게 되어있으니 "ROLE_" prefix를 붙이지 말라고 설명이 되어있다.
```java
.requestMatchers("/db/**").access(new WebExpressionAuthorizationManager("hasRole('ADMIN') and hasRole('DBA')"))
```

### PS가 붙은 변수명
DB에서 직접 꺼낸 객체를 보존 데이터란 의미로 persistence의 약자인 PS를 붙여서 사용했음
```java
User userPS = userRepository.findByUserId(userId);
```
