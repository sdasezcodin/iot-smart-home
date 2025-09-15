#!/bin/bash

set -e

echo "🚀 Setting up IoT Smart Home Dashboard development environment..."

# Update package list
sudo apt-get update -y

# Install additional tools
sudo apt-get install -y curl wget unzip tree

# Install DynamoDB Local (for development)
echo "📦 Installing DynamoDB Local..."
mkdir -p ~/.aws/dynamodb-local
cd ~/.aws/dynamodb-local
if [ ! -f "DynamoDBLocal.jar" ]; then
    wget -q https://d1ni2b6xgvw0s0.cloudfront.net/v2.x/dynamodb_local_latest.zip
    unzip -q dynamodb_local_latest.zip
    rm dynamodb_local_latest.zip
fi

# Create alias for DynamoDB Local
echo 'alias dynamodb-local="java -Djava.library.path=~/.aws/dynamodb-local/DynamoDBLocal_lib -jar ~/.aws/dynamodb-local/DynamoDBLocal.jar -sharedDb"' >> ~/.bashrc

# Set up AWS CLI with test credentials for local development
echo "🔑 Setting up local AWS credentials for testing..."
aws configure set aws_access_key_id test --profile local-dev
aws configure set aws_secret_access_key test --profile local-dev
aws configure set region us-east-1 --profile local-dev

# Cache Maven dependencies
echo "📚 Caching Maven dependencies..."
if [ -f "pom.xml" ]; then
    mvn dependency:resolve -q
fi

# Create useful aliases
echo "⚡ Creating helpful aliases..."
cat >> ~/.bashrc << 'EOF'

# IoT Smart Home Dashboard aliases
alias build="mvn clean package"
alias test="mvn test"
alias run="java -jar target/iot-smarthome-dashboard-1.0-SNAPSHOT.jar"
alias docker-build="docker build -t iot-smarthome-dashboard ."
alias start-dynamodb="java -Djava.library.path=~/.aws/dynamodb-local/DynamoDBLocal_lib -jar ~/.aws/dynamodb-local/DynamoDBLocal.jar -sharedDb -port 8000"

# Useful development shortcuts
alias ll="ls -alF"
alias la="ls -A"
alias l="ls -CF"
alias ..="cd .."
alias ...="cd ../.."

EOF

# Make the script executable
chmod +x ~/.bashrc

echo "✅ Development environment setup complete!"
echo ""
echo "🎯 Quick start commands:"
echo "  mvn clean compile  - Compile the project"
echo "  mvn test           - Run tests"
echo "  mvn clean package  - Build JAR"
echo "  start-dynamodb     - Start DynamoDB Local"
echo "  build              - Alias for mvn clean package"
echo "  run                - Run the application JAR"
echo ""
echo "🌐 Port forwards:"
echo "  - 5555: Smart Home Server"  
echo "  - 8000: DynamoDB Local"