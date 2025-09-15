#!/bin/bash

echo "🚀 Setting up IoT Smart Home Dashboard development environment..."

# Compile the project
echo "📦 Compiling Maven project..."
mvn clean compile

# Start DynamoDB Local
echo "🗄️ Starting DynamoDB Local..."
docker run -d \
  --name dynamodb-local \
  -p 8000:8000 \
  amazon/dynamodb-local:latest \
  -jar DynamoDBLocal.jar -sharedDb -inMemory

# Wait for DynamoDB to be ready
echo "⏳ Waiting for DynamoDB Local to start..."
sleep 10

# Verify DynamoDB is running
for i in {1..10}; do
  if curl -s http://localhost:8000/ > /dev/null 2>&1; then
    echo "✅ DynamoDB Local is running on port 8000"
    break
  fi
  echo "Waiting for DynamoDB... ($i/10)"
  sleep 3
done

# Configure AWS CLI for local development
echo "🔧 Configuring AWS CLI for DynamoDB Local..."
aws configure set aws_access_key_id test --profile default
aws configure set aws_secret_access_key test --profile default  
aws configure set region us-east-1 --profile default

# Test connection
echo "🧪 Testing DynamoDB Local connection..."
aws dynamodb list-tables --endpoint-url http://localhost:8000 --region us-east-1 > /dev/null 2>&1
if [ $? -eq 0 ]; then
  echo "✅ DynamoDB Local connection successful!"
else
  echo "⚠️ DynamoDB Local connection test failed, but continuing..."
fi

# Show useful information
echo ""
echo "🎉 Setup completed!"
echo "📋 What's available:"
echo "  🏠 Your app: Port 5555 (will auto-forward when running)"
echo "  🗄️ DynamoDB Local: Port 8000 (already running)"
echo ""
echo "🌍 CLOUD READY - Access from anywhere!"
echo "  • Ports are PUBLIC and auto-forwarded"
echo "  • You'll get URLs like: https://xyz-5555.app.github.dev"
echo ""
echo "🚀 Quick commands:"
echo "  mvn clean package    # Build JAR"
echo "  java -jar target/iot-smarthome-dashboard-1.0-SNAPSHOT.jar    # Run app"
echo "  docker-compose up    # Run full stack"
echo ""
echo "✨ Ready! Run your app and get public URLs for global access!"
