# Deploy Kafka with a Statefulset to Kubernetes using [Strimzi](https://strimzi.io)
```
kubectl create namespace kafka
```
```
kubectl create -f 'https://strimzi.io/install/latest?namespace=kafka' -n kafka
```

Wait for pod to be ready.
```
kubectl get pod -n kafka --watch
```
```
kubectl apply -f https://strimzi.io/examples/latest/kafka/kafka-persistent-single.yaml -n kafka
```

Wait for kafka cluster to be ready.
```
kubectl wait kafka/my-cluster --for=condition=Ready --timeout=300s -n kafka
```