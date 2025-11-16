# Water Ball Platform - Production Deployment Guide

## Overview

This guide covers deploying the Water Ball Platform to production using Docker Compose on a cloud platform (AWS, GCP, or Azure) with SSL/HTTPS support.

## Prerequisites

- Cloud VM/Instance:
  - **Minimum**: 2 vCPU, 4GB RAM
  - **Recommended**: 4 vCPU, 8GB RAM
  - **OS**: Ubuntu 22.04 LTS or similar
- Domain name with DNS access
- SSL certificate and private key
- docker and docker compose installed on server

## Architecture

```
Internet → HTTPS (443) → Nginx Reverse Proxy
                          ├→ Next.js Frontend (port 3000)
                          └→ Spring Boot Backend (port 8080)
                               ├→ PostgreSQL Database
                               └→ Redis Cache
```

## Step 1: Provision Cloud Infrastructure

### AWS EC2

```bash
# Launch t3.medium instance
# AMI: Ubuntu 22.04 LTS
# Security Group: Allow ports 22, 80, 443
# EBS: 30GB minimum
```

### GCP Compute Engine

```bash
# Machine type: e2-medium
# OS: Ubuntu 22.04 LTS
# Firewall: Allow HTTP, HTTPS, SSH
# Disk: 30GB minimum
```

### Azure VM

```bash
# Size: B2s or higher
# Image: Ubuntu Server 22.04 LTS
# NSG: Allow ports 22, 80, 443
# Disk: 30GB minimum
```

## Step 2: Server Setup

### Install Docker

```bash
# Update system
sudo apt-get update
sudo apt-get upgrade -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Add user to docker group
sudo usermod -aG docker $USER

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Verify installation
docker --version
docker-compose --version
```

### Configure Firewall

```bash
# UFW firewall
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
```

## Step 3: DNS Configuration

Point your domain A record to your server's public IP:

```
Type: A
Name: @ (or www)
Value: <SERVER_PUBLIC_IP>
TTL: 300
```

Verify DNS propagation:

```bash
dig yourdomain.com
nslookup yourdomain.com
```

## Step 4: Prepare Application Files

### Clone Repository

```bash
cd /opt
sudo git clone <your-repo-url> waterball
cd waterball
sudo chown -R $USER:$USER /opt/waterball
```

### Create Production Environment File

```bash
cp .env.production.example .env.production
nano .env.production
```

Fill in all values (see `.env.production.example` for required variables).

### Generate Secrets

```bash
# Generate JWT secret
openssl rand -base64 64

# Generate encryption key
openssl rand -base64 32

# Generate NextAuth secret
openssl rand -base64 32
```

## Step 5: SSL Certificate Setup

### Option A: Using Existing Certificate

```bash
# Upload your certificate files to server
scp /path/to/cert.pem user@server:/opt/waterball/ssl/
scp /path/to/key.pem user@server:/opt/waterball/ssl/

# Set permissions
chmod 600 /opt/waterball/ssl/key.pem
chmod 644 /opt/waterball/ssl/cert.pem
```

Update `.env.production`:

```
SSL_CERT_PATH=/opt/waterball/ssl/cert.pem
SSL_KEY_PATH=/opt/waterball/ssl/key.pem
```

### Option B: Using Let's Encrypt

```bash
# Install certbot
sudo apt-get install certbot

# Stop any services on port 80
sudo docker-compose down

# Get certificate
sudo certbot certonly --standalone -d yourdomain.com

# Certificates will be in /etc/letsencrypt/live/yourdomain.com/
```

Update `.env.production`:

```
SSL_CERT_PATH=/etc/letsencrypt/live/yourdomain.com/fullchain.pem
SSL_KEY_PATH=/etc/letsencrypt/live/yourdomain.com/privkey.pem
```

## Step 6: Deploy Application

### Build and Start Services

```bash
cd /opt/waterball

# Pull latest code
git pull origin main

# Build and start all services
docker-compose -f docker-compose.prod.yml up -d --build

# View logs
docker-compose -f docker-compose.prod.yml logs -f
```

### Verify Services

```bash
# Check all containers are running
docker-compose -f docker-compose.prod.yml ps

# Check health
curl http://localhost/actuator/health
```

## Step 7: Database Migration

Database migrations run automatically via Flyway when the backend starts.

### Verify Migration

```bash
# Connect to database
docker exec -it waterball-db-prod psql -U waterball_user -d waterball

# Check migration version
SELECT version FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 1;

# Should show version 7 (role management)
```

## Step 8: Create First Admin User

```bash
# Find your user ID
docker exec waterball-db-prod psql -U waterball_user -d waterball \
  -c "SELECT user_id, nickname, email, role FROM users;"

# Upgrade first user to ADMIN
docker exec waterball-db-prod psql -U waterball_user -d waterball \
  -c "UPDATE users SET role = 'ADMIN' WHERE user_id = 1;"

# Verify
docker exec waterball-db-prod psql -U waterball_user -d waterball \
  -c "SELECT user_id, nickname, role FROM users WHERE role = 'ADMIN';"
```

## Step 9: Verification & Testing

### SSL/HTTPS Check

1. Visit `https://yourdomain.com` - should show green padlock
2. HTTP should redirect to HTTPS
3. Check certificate validity: `openssl s_client -connect yourdomain.com:443`

### Application Testing

1. **Homepage**: `https://yourdomain.com`
2. **Backend Health**: `https://yourdomain.com/actuator/health`
3. **OAuth Login**: Test Google/Facebook login
4. **Admin UI**: `https://yourdomain.com/admin/users` (requires ADMIN role)
5. **Role Management**: Test changing user roles

### Performance Check

```bash
# Check resource usage
docker stats

# Check logs for errors
docker-compose -f docker-compose.prod.yml logs --tail=100
```

## Step 10: Post-Deployment

### Set Up Automated Backups

#### Database Backup Script

```bash
# Create backup script
sudo nano /opt/waterball/scripts/backup-db.sh
```

```bash
#!/bin/bash
BACKUP_DIR="/opt/waterball/backups"
DATE=$(date +%Y%m%d_%H%M%S)
mkdir -p $BACKUP_DIR

docker exec waterball-db-prod pg_dump -U waterball_user waterball | \
  gzip > $BACKUP_DIR/waterball_$DATE.sql.gz

# Keep only last 30 days
find $BACKUP_DIR -name "*.sql.gz" -mtime +30 -delete
```

```bash
# Make executable
chmod +x /opt/waterball/scripts/backup-db.sh

# Add to crontab (daily at 2 AM)
crontab -e
0 2 * * * /opt/waterball/scripts/backup-db.sh
```

### Monitoring Setup

#### Log Rotation

```bash
# Configure Docker log rotation
sudo nano /etc/docker/daemon.json
```

```json
{
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "10m",
    "max-file": "3"
  }
}
```

```bash
# Restart Docker
sudo systemctl restart docker
```

### Update OAuth Redirect URIs

Update your OAuth app configurations:

**Google Console**: https://console.cloud.google.com/apis/credentials

- Authorized redirect URIs: `https://yourdomain.com/login/oauth2/code/google`

**Facebook Developer**: https://developers.facebook.com/apps

- Valid OAuth Redirect URIs: `https://yourdomain.com/login/oauth2/code/facebook`

## Maintenance Commands

### View Logs

```bash
# All services
docker-compose -f docker-compose.prod.yml logs -f

# Specific service
docker-compose -f docker-compose.prod.yml logs -f backend

# Last 100 lines
docker-compose -f docker-compose.prod.yml logs --tail=100
```

### Restart Services

```bash
# All services
docker-compose -f docker-compose.prod.yml restart

# Specific service
docker-compose -f docker-compose.prod.yml restart backend
```

### Update Application

```bash
cd /opt/waterball
git pull origin main
docker-compose -f docker-compose.prod.yml up -d --build
```

### Database Operations

```bash
# Connect to PostgreSQL
docker exec -it waterball-db-prod psql -U waterball_user -d waterball

# Backup database
docker exec waterball-db-prod pg_dump -U waterball_user waterball > backup.sql

# Restore database
cat backup.sql | docker exec -i waterball-db-prod psql -U waterball_user -d waterball
```

## Troubleshooting

### Services Won't Start

```bash
# Check Docker daemon
sudo systemctl status docker

# Check logs
docker-compose -f docker-compose.prod.yml logs

# Check disk space
df -h

# Check memory
free -m
```

### SSL Issues

```bash
# Test certificate
openssl s_client -connect yourdomain.com:443

# Check nginx logs
docker-compose -f docker-compose.prod.yml logs nginx

# Verify certificate paths in .env.production
cat .env.production | grep SSL
```

### Database Connection Issues

```bash
# Check database container
docker-compose -f docker-compose.prod.yml ps db

# Check database logs
docker-compose -f docker-compose.prod.yml logs db

# Test connection
docker exec waterball-db-prod pg_isready -U waterball_user
```

### OAuth Login Fails

1. Verify redirect URIs in OAuth console match production domain
2. Check CORS settings in backend
3. Verify OAuth credentials in `.env.production`
4. Check browser console for errors

## Security Checklist

- [ ] Strong passwords in `.env.production`
- [ ] SSL certificate valid and auto-renewing
- [ ] Firewall configured (only 22, 80, 443 open)
- [ ] Database not exposed to public
- [ ] Redis password set
- [ ] Regular backups configured
- [ ] Docker log rotation enabled
- [ ] SSH key-based authentication only
- [ ] UFW/firewall enabled
- [ ] Regular security updates: `sudo apt-get update && sudo apt-get upgrade`

## Performance Tuning

### Database

```sql
-- Connect to database
\c waterball

-- Analyze query performance
EXPLAIN ANALYZE SELECT * FROM users WHERE role = 'ADMIN';

-- Vacuum database
VACUUM ANALYZE;
```

### Backend

- Adjust JVM memory in `docker-compose.prod.yml` if needed
- Monitor `/actuator/metrics` endpoint
- Consider adding connection pooling adjustments

### Nginx

- Adjust worker connections in `nginx/nginx.conf`
- Enable additional caching if needed
- Consider CDN for static assets

## Support

For issues or questions:

1. Check logs: `docker-compose -f docker-compose.prod.yml logs`
2. Verify configuration: `.env.production`
3. Review this guide
4. Check application health: `https://yourdomain.com/actuator/health`

## Next Steps

- [ ] Set up monitoring (Grafana, Prometheus)
- [ ] Configure CDN for static assets
- [ ] Set up CI/CD pipeline
- [ ] Implement multiple roles (see CLAUDE.md for migration plan)
- [ ] Add automated testing in production
- [ ] Set up alerting for critical errors
