# only for local use

docker login

docker buildx create --use
docker buildx inspect --bootstrap
docker buildx build --push --platform linux/amd64,linux/arm64 -t mrsarius/yc-api-gateway:latest ../api-gateway
docker buildx build --push --platform linux/amd64,linux/arm64 -t mrsarius/yc-test-service:latest ../test-service

# kubectl apply -f media-service.yaml
# kubectl apply -f messaging-service.yaml
# kubectl apply -f notification-service.yaml
# kubectl apply -f api-gateway.yaml
