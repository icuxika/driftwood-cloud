本项目所用到的各种服务的Docker Compose文件
==========

# docker 网络

```shell
docker network create --gateway 172.27.0.1 --subnet 172.27.0.0/16 driftwood-cloud-net
```

> 需要处于同一网段下不同的 Docker Compose 组合（比如 driftwood 与 sentinel-dashboard）加入以下配置

```yaml
networks:
  default:
    name: driftwood-cloud-net
    external: true
```