# 환경(test / production) 분리 시 반영할 것들 — 학습용 메모

지금은 단일 환경만 다룬다. 나중에 test/production 을 나눌 때
아래를 손보면 된다.

---

## 1. 설정 파일 자체를 환경별로 둔다

예시 구조:

```
deploy/
  environments/
    test/
      env.conf           # JAVA_TOOL_OPTIONS=-Xmx128m
      application.yml    # 로깅 DEBUG 등
    production/
      env.conf           # JAVA_TOOL_OPTIONS=-Xmx256m
      application.yml    # 로깅 INFO/WARN 등
```

차이 나기 쉬운 항목:

| 항목 | test 예시 | production 예시 |
|------|-----------|-----------------|
| JVM 힙 (`JAVA_TOOL_OPTIONS`) | `-Xmx128m` | `-Xmx256m` |
| Spring profile | `test` | `production` |
| 로그 레벨 | DEBUG | INFO / WARN |
| DB·외부 API URL | 테스트용 | 실제 |
| 서버 대수 / EC2 호스트 | 테스트 인스턴스 | 운영 인스턴스 |

`env.conf` → systemd `EnvironmentFile` (메모리 등 JVM 옵션)
`application.yml` → Spring 외부 설정 (포트, 로깅, 연동 정보)

---

## 2. CI/CD 워크플로에서 환경을 고르게 한다

- `workflow_dispatch` input 으로 `test` / `production` 선택
- 또는 브랜치 규칙: `develop` → test, `main` → production
- 배포 전에 `deploy/environments/${ENV}/` 쪽 파일을 패키지에 넣기

---

## 3. GitHub Environments + Secrets 분리

Environment 이름: `test`, `production`

환경마다 다른 Secrets 예시:

- `EC2_HOST` (서버가 다르면)
- `EC2_USER`, `EC2_SSH_KEY`
- (필요 시) DB 비밀번호 등

job 에 `environment: test` 처럼 지정하면 해당 Secrets 가 주입된다.
production 에는 required reviewers(승인 후 배포) 를 걸 수도 있다.

---

## 4. EC2 / systemd 쪽

단일 서버에 환경 하나만 돌리면: 지금처럼 `/home/ec2-user/service/demo-ci` + `env.conf` 교체만으로도 충분.

서버를 둘로 나누면:

- test EC2, production EC2 각각 동일 디렉터리 구조
- CD 가 골라서 scp/ssh

한 서버에 두 환경을 같이 올릴 경우(비권장·학습용):

- 유닛 이름 분리: `demo-ci-test.service`, `demo-ci-prod.service`
- 경로·포트 분리: `/home/ec2-user/service/demo-ci-test`, 포트 8080 / 8081

---

## 5. 지금 코드에서 건드릴 위치 (체크리스트)

- [ ] `deploy/environments/<env>/env.conf`, `application.yml` 추가
- [ ] `.github/workflows/ci.yml` — 환경 선택 + 해당 폴더 파일 복사
- [ ] GitHub Settings → Environments 생성 및 Secrets 분리
- [ ] (선택) production 배포 전 승인 규칙
