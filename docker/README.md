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

# 控制台端口分配

| 控制台                | 端口   | 
|--------------------|------|
| sentinel-dashboard | 8899 |
| nginx              | 8880 |
| rocketmq-dashboard | 8882 |
| xxl-job-admin      | 8883 |
| minio              | 8884 |

# 服务补充说明

## sentinel-dashboard

在 [Sentinel Releases](https://github.com/alibaba/Sentinel/releases) 下载所需的控制台`jar`包，修改`.env`
文件中`SENTINEL_DASHBOARD_VERSION`属性的值为正确版本，然后使用`docker-compose build`命令进行构建