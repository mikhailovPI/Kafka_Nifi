docker exec -it kafka-1 kafka-console-consumer `
  --topic nifi-topic `
  --bootstrap-server localhost:9093 `
  --from-beginning