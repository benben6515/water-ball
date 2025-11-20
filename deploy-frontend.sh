#!/bin/bash

# Deploy Frontend Script
# This script rebuilds the frontend with production environment variables

echo "🚀 Starting frontend deployment..."

# Stop the frontend container
echo "📦 Stopping frontend container..."
docker compose stop frontend

# Rebuild the frontend with production env vars
echo "🔨 Rebuilding frontend..."
docker compose build --no-cache frontend

# Start the frontend container
echo "▶️  Starting frontend container..."
docker compose up -d frontend

# Wait for container to be healthy
echo "⏳ Waiting for frontend to be ready..."
sleep 10

# Check if frontend is running
if docker compose ps frontend | grep -q "Up"; then
  echo "✅ Frontend deployed successfully!"
  echo "🌐 Frontend is available at: https://water-ball.benben.me"
else
  echo "❌ Frontend deployment failed!"
  echo "📋 Checking logs..."
  docker compose logs frontend --tail 50
  exit 1
fi

echo "✨ Deployment complete!"
