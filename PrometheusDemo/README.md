# Prometheus Demo

- Run spring boot application

```
$ mvn clean package
$ java -jar target/PrometheusDemo-0.0.1-SNAPSHOT.jar
```

- Prometheus matrices

```
$ curl http://localhost:8080/actuator/prometheus
```

- Increment car orders

```
$ ab -n 100 localhost:8080/order/sports
$ ab -n 100 localhost:8080/order/suv
$ ab -n 100 localhost:8080/order/van
```

- Set production count

```
$ curl http://localhost:8080/production/50
```

- Run Prometheus

```
$ docker run --rm -it -p 9090:9090 -v $(pwd)/prometheus:/etc/prometheus prom/prometheus
```

- Prometheus console: `http://localhost:9090 `

- Prometheus targets: `http://localhost:9090/targets`

- Prometheus query API

```
$ curl http://localhost:9090/api/v1/query?query=car_orders_total{type="van"}
```

- Run Grafana

```
$ docker run --rm -it -p 3000:3000 grafana/grafana
```

- Add datasource - Prometheus

- Import [JVM](https://grafana.com/grafana/dashboards/4701) dashboard

- Configure custom dashboard

- Configure `Slack` notification chanel and alert rules
