学习记录
==========

### spring-cloud-starter-alibaba-sentinel

- 此依赖会引入`spring-cloud-circuitbreaker-sentinel`，为Feign接口配置了Fallback时，若只引入`spring-cloud-circuitbreaker-sentinel`
  依赖时，请求的目标微服务若不可用则会进入fallback，而请求超时则不会进入fallback；若引入`spring-cloud-starter-alibaba-sentinel`
  依赖，则微服务不可用和请求超时都会进入fallback，细节待学习源码后补充。