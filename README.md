# Weiter server

Weiter의 Java 25·Spring Boot 4 백엔드입니다. 현재 저장소에는 애플리케이션 시작 코드와 테스트·개발 규칙이 있으며, 도메인 기능은 아직 구현 전입니다.

## 시작하기

Java 25가 필요합니다. Windows에서는 `gradlew.bat`, macOS·Linux에서는 `./gradlew`를 사용합니다.

```powershell
.\gradlew.bat bootRun
.\gradlew.bat check
.\gradlew.bat pitest
```

`check`는 테스트와 JaCoCo 라인 커버리지 80% 검증을 실행합니다. `pitest`는 변이 테스트를 실행하며 기준은 90%입니다. 현재는 변이 테스트 대상인 도메인 코드가 없어 PIT 작업이 건너뛰어집니다. JaCoCo 보고서는 `build/reports/jacoco/test/html/index.html`, PIT 보고서는 `build/reports/pitest/index.html`에서 확인할 수 있습니다.

## 설계 기준

[설계 원칙](docs/engineering-principles.md)은 Elegant Objects와 Clean Code를 우선하고, 기능별 계층은 Bibbidi의 구조를 따릅니다. 기능 패키지는 필요할 때 `controller`, `service`, `repository`, `persistence`, `domain`으로 나눕니다. 도메인 객체는 Spring과 JPA에 의존하지 않습니다.

## 테스트 기준

[테스트 전략](.agents/skills/weiter-test-strategy/SKILL.md)은 **각 클래스의 책임과 그 클래스가 속한 계층의 계약을 모두** 확인합니다. 같은 규칙을 계층마다 반복해서 검증하지 않습니다.

| 확인할 책임 | 실행 방식 |
| --- | --- |
| 도메인 규칙과 값 객체 | 객체를 직접 생성 |
| 서비스의 판단·협력·결과 | 서비스를 직접 생성하고 저장소·외부 포트에 손으로 작성한 Fake 사용 |
| JPA 매핑·쿼리·제약·페치 | 실제 JPA가 동작하는 테스트 슬라이스와 적합한 데이터베이스 사용 |
| HTTP 경로·바인딩·검증·응답 | MVC 테스트 컨텍스트와 서비스 경계의 Fake 사용 |
| 빈 설정·보안 필터·트랜잭션 프록시·핵심 사용자 흐름 | 검증할 동작에 필요한 범위의 Spring 컨텍스트 사용 |

객체를 `new`로 만들었을 때도 검증할 결과가 성립한다면 먼저 컨텍스트 없이 테스트합니다. Spring의 매핑, 프록시, 필터, 빈 구성, JPA 쿼리처럼 프레임워크 실행 자체가 검증 대상이면 가장 작은 컨텍스트를 사용합니다. Fake는 실제 경계 인터페이스를 구현하고 결과와 상태를 검증할 수 있어야 합니다. JaCoCo와 PIT 수치를 맞추기 위한 무의미한 테스트는 추가하지 않습니다.

## 에이전트 스킬

Codex와 Google Antigravity는 `.agents/skills/`의 원본 스킬을 사용합니다. Claude Code는 `.claude/skills/`의 연결 스킬에서 같은 원본을 읽습니다. [AGENTS.md](AGENTS.md)와 [CLAUDE.md](CLAUDE.md)가 공통 규칙을 안내합니다.

| 스킬 | 역할 |
| --- | --- |
| `java-coding-standards` | 객체 설계·이름·응집도와 계층 배치 |
| `java-springboot` | 설정·의존성 주입·REST 경계 |
| `jpa-patterns` | 엔티티·쿼리·트랜잭션·페치 전략 |
| `java-junit` | JUnit Jupiter 테스트 작성 방식 |
| `weiter-test-strategy` | 클래스·계층 책임에 따른 테스트 선택과 Fake/컨텍스트 구분 |
| `humanize-korean` | 요청받은 한국어 원고 윤문 |
| `techwriting-reviewer` | 기술 문서 작성·수정 후 18개 항목 검토 |

스킬 원본을 수정하면 Claude 연결 스킬의 설명도 함께 확인합니다. `humanize-korean`의 검증 스크립트를 실행하려면 Python 3가 필요합니다. 스킬의 출처와 적용 라이선스는 각 `SKILL.md`와 `third_party/licenses/`에 기록되어 있습니다.
