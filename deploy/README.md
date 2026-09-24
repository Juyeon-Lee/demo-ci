# EC2 배포 (단일 환경, SSH: ec2-user)

## 흐름

1. GitHub Actions 에서 Maven 빌드 → jar
2. jar + 설정을 EC2 `~/service/demo-ci` 로 복사 (scp)
3. `systemctl restart demo-ci`

앱 경로: `/home/ec2-user/service/demo-ci`  
(`service` = 설치된 서비스들, `demo-ci` = 이 앱)

환경 분리 → `environment.md`

## GitHub Secrets (PEM 연결)

Repository → Settings → Secrets and variables → Actions → New repository secret

| Secret | 넣을 값 |
|--------|---------|
| `EC2_HOST` | EC2 퍼블릭 IP 또는 DNS |
| `EC2_SSH_KEY` | `.pem` 파일 **전체 내용** |
| `EC2_PORT` | (선택) SSH 포트. 없으면 22 |

워크플로 SSH 사용자는 **`ec2-user` 고정**.  
`systemctl` 용 passwordless `sudo` 만 있으면 됩니다.

## EC2에 미리 있어야 할 것

- JDK 21
- systemd 유닛 `demo-ci.service` (저장소 `deploy/systemd/demo-ci.service` 참고)
- 디렉터리 `~/service/demo-ci` (없으면 첫 scp 전에 `mkdir -p ~/service/demo-ci`)

## 배포 시 올라가는 파일

| 파일 | 역할 |
|------|------|
| `app.jar` | 실행 파일 |
| `application.yml` | Spring 설정 |
| `env.conf` | JVM·Spring 환경변수 |

## 트리거

- PR → 빌드만
- `main` 푸시 → 빌드 + 배포

## 수동 배포

```bash
mkdir -p 는 서버에서 한 번:
ssh -i my-key.pem ec2-user@{public_ip} 'mkdir -p ~/service/demo-ci'

scp -i my-key.pem \
  release/app.jar \
  release/application.yml \
  release/env.conf \
  ec2-user@{public_ip}:~/service/demo-ci/

ssh -i my-key.pem ec2-user@{public_ip} \
  'sudo systemctl restart demo-ci.service'
```

## 운영

```bash
sudo systemctl status demo-ci
sudo systemctl restart demo-ci
sudo journalctl -u demo-ci -f
```
