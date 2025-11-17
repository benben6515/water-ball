# CI/CD Setup Guide

This guide explains how to set up Continuous Integration and Continuous Deployment (CI/CD) for the Water Ball project using GitHub Actions.

## Overview

The CI/CD pipeline automatically:
1. **Tests** - Runs backend and frontend tests on every push
2. **Deploys** - Automatically deploys to AWS EC2 when tests pass
3. **Verifies** - Checks that all services are running correctly

## Workflow Triggers

- **Automatic**: Triggers on push to `001-member-system` branch
- **Manual**: Can be triggered manually from GitHub Actions UI

## Setup Instructions

### 1. Generate SSH Key for GitHub Actions

On your **local machine** or **AWS server**, generate a new SSH key pair for GitHub Actions:

```bash
# Generate new SSH key (no passphrase)
ssh-keygen -t ed25519 -C "github-actions" -f ~/.ssh/github-actions-waterball

# This creates two files:
# - ~/.ssh/github-actions-waterball (private key)
# - ~/.ssh/github-actions-waterball.pub (public key)
```

### 2. Add Public Key to AWS Server

Copy the public key to your AWS server:

```bash
# On your local machine
cat ~/.ssh/github-actions-waterball.pub

# Copy the output, then SSH to your AWS server
ssh ubuntu@your-server-ip

# On AWS server
echo "PUBLIC_KEY_HERE" >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys
```

Test the connection:
```bash
# On your local machine
ssh -i ~/.ssh/github-actions-waterball ubuntu@your-server-ip
```

### 3. Configure GitHub Secrets

Go to your GitHub repository:
1. Click **Settings** → **Secrets and variables** → **Actions**
2. Click **New repository secret**

Add these secrets:

#### `AWS_HOST`
- **Value**: Your AWS EC2 public IP or domain
- **Example**: `3.25.123.45` or `water-ball.benben.me`

#### `AWS_USERNAME`
- **Value**: Your SSH username (usually `ubuntu` for Ubuntu EC2)
- **Example**: `ubuntu`

#### `AWS_SSH_KEY`
- **Value**: Contents of your **private key** file
- Get it with:
  ```bash
  cat ~/.ssh/github-actions-waterball
  ```
- Copy the **entire content** including:
  ```
  -----BEGIN OPENSSH PRIVATE KEY-----
  ...all the key content...
  -----END OPENSSH PRIVATE KEY-----
  ```

### 4. Verify GitHub Actions Setup

1. Go to **Actions** tab in your GitHub repository
2. You should see the "Deploy to AWS EC2" workflow
3. Make a test commit and push to `001-member-system` branch
4. Watch the workflow run in the Actions tab

## Workflow Stages

### Stage 1: Test (Runs Always)
- ✅ Checkout code
- ✅ Set up Java 17
- ✅ Run backend tests with Maven
- ✅ Set up Node.js 20
- ✅ Install frontend dependencies
- ✅ Run frontend tests
- ✅ Lint frontend code

### Stage 2: Deploy (Runs Only if Tests Pass)
- ✅ SSH to AWS server
- ✅ Pull latest code
- ✅ Stop running services
- ✅ Rebuild Docker images
- ✅ Start services
- ✅ Verify services are healthy

### Stage 3: Verify (Runs After Deployment)
- ✅ Check all containers are running
- ✅ Send notification about deployment status

## Manual Deployment

You can manually trigger deployment from GitHub:
1. Go to **Actions** tab
2. Select "Deploy to AWS EC2" workflow
3. Click **Run workflow**
4. Select branch and click **Run workflow**

## Monitoring Deployments

### View Deployment Logs
1. Go to **Actions** tab
2. Click on the workflow run
3. Click on job name (Test or Deploy)
4. Expand steps to see detailed logs

### Check Deployment Status on Server

SSH to your server and run:
```bash
cd /opt/waterball
docker compose -f docker-compose.prod.yml ps
docker compose -f docker-compose.prod.yml logs --tail=100
```

## Rollback Procedure

If a deployment fails, you can rollback:

```bash
# SSH to your AWS server
ssh ubuntu@your-server-ip

cd /opt/waterball

# View recent commits
git log --oneline -10

# Rollback to a specific commit
git reset --hard COMMIT_SHA

# Rebuild and restart
docker compose -f docker-compose.prod.yml down
docker compose -f docker-compose.prod.yml --env-file .env.production.local build
docker compose -f docker-compose.prod.yml --env-file .env.production.local up -d
```

## Troubleshooting

### Deployment fails with "Permission denied"
- Check that the public key is added to `~/.ssh/authorized_keys` on AWS
- Verify the private key in GitHub Secrets is correct
- Ensure SSH port 22 is open in AWS Security Group

### Tests fail but work locally
- Check that all dependencies are specified in package.json
- Ensure test environment variables are set if needed
- Review test logs in GitHub Actions

### Services don't start after deployment
- SSH to server and check logs:
  ```bash
  docker compose -f docker-compose.prod.yml logs
  ```
- Verify `.env.production.local` file exists and has correct values
- Check if ports are already in use

### Workflow doesn't trigger
- Ensure you pushed to `001-member-system` branch
- Check workflow file syntax (YAML is indent-sensitive)
- Go to Actions → select workflow → check for errors

## Best Practices

1. **Always test locally first** before pushing
2. **Use pull requests** for code review before merging to deployment branch
3. **Monitor the first deployment** to ensure everything works
4. **Keep secrets secure** - never commit them to the repository
5. **Use manual trigger** for production deployments to have more control

## Next Steps

Consider adding:
- 🔔 Slack/Discord notifications for deployment status
- 🧪 More comprehensive test coverage
- 🚀 Staging environment deployment
- 📊 Performance monitoring
- 🔍 Automated security scanning
- 📦 Docker image caching for faster builds
