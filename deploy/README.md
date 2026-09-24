# EC2 배포 (단일 환경, SSH: ec2-user)

## 흐름

1. GitHub Actions 에서 Maven 빌드 → jar
2. jar + 설정을 EC2 `~/demo-ci` 로 복사 (scp, `ec2-user` + PEM)
3. SSH 로 `/opt/demo-ci` 에 배치 후 systemd 재시작 (`ci.yml` 안에서 처리)

환경 분리 → `environment.md`

## GitHub Secrets (PEM 연결)

Repository → Settings → Secrets and variables → Actions → New repository secret

| Secret | 넣을 값 |
|--------|---------|
| `EC2_HOST` | EC2 퍼블릭 IP 또는 DNS |
| `EC2_SSH_KEY` | `.pem` 파일 **전체 내용** |
| `EC2_PORT` | (선택) SSH 포트. 없으면 22 |

`EC2_SSH_KEY` 는 파일 경로가 아니라 pem **텍스트 전체**를 붙여 넣습니다.

워크플로 SSH 사용자는 **`ec2-user` 고정** (Amazon Linux 기본 계정).  
`ec2-user` 에 passwordless `sudo` 가 있어야 `/opt` 배치·`systemctl` 이 됩니다.

## EC2에 미리 있어야 할 것

- JDK 21
- systemd 유닛 `demo-ci.service` (`User=ec2-user`, 저장소 `deploy/systemd/demo-ci.service` 참고)
- 디렉터리 `/opt/demo-ci/current`, `/opt/demo-ci/conf` (없으면 배포 시 생성, 소유자 `ec2-user`)

## 배포 시 올라가는 파일

| 파일 | 역할 |
|------|------|
| `app.jar` | 실행 파일 |
| `application.yml` | Spring 설정 |
| `env.conf` | JVM·Spring 환경변수 (`JAVA_TOOL_OPTIONS` 등) |

시작/재시작은 `systemctl restart demo-ci` 로 하면 됩니다. (별도 start.sh 없음)

## 트리거

- PR → 빌드만
- `main` 푸시 → 빌드 + 배포

## 수동 배포

```bash
scp -i my-key.pem \
  release/app.jar \
  release/application.yml \
  release/env.conf \
  ec2-user@{public_ip}:~/demo-ci/

ssh -i my-key.pem ec2-user@{public_ip} <<'EOF'
sudo mkdir -p /opt/demo-ci/current /opt/demo-ci/conf
sudo cp ~/demo-ci/app.jar /opt/demo-ci/current/app.jar
sudo cp ~/demo-ci/application.yml /opt/demo-ci/current/application.yml
sudo cp ~/demo-ci/env.conf /opt/demo-ci/conf/env.conf
sudo chown -R ec2-user:ec2-user /opt/demo-ci
sudo systemctl restart demo-ci.service
EOF
```

## 운영

```bash
sudo systemctl status demo-ci
sudo systemctl restart demo-ci
sudo journalctl -u demo-ci -f
```
