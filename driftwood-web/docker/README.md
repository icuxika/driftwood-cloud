部署到 Docker (Nginx) 环境
==========

> 调整 `driftwood-cloud/.env` 中的 `DRIFTWOOD_CLOUD_HOST` 为后端网关ip地址，不包含端口

# 构建页面
> 当前目录: `driftwood-cloud/driftwood-web`
```shell
yarn build
```

# 构建容器
```shell
cd ..
```
> 当前目录: `driftwood-cloud`

```shell
docker-compose build driftwood-web
```

# 运行

```shell
docker-compose up -d driftwood-web
```

# 补充

1. 上面的方式会导致每一次部署都要重新构建镜像，一种方便的方式是使用`driftwood-cloud/docker/basic`中`nginx`独立部署的方式。
2. 将`driftwood-cloud/driftwood-web/docker/dist`的内容复制到`driftwood-cloud/docker/basic/etc/nginx/html`。
3. 你需要修改`driftwood-cloud/docker/basic/etc/nginx/conf.d/default.conf`中 `proxy_pass  http://192.168.1.60:8900/;`
   为你的后端网关地址，不要遗忘 `/`。
4. 3的方式或许不够优雅，你也可以选择在`driftwood-cloud/docker/basic/docker-compose.yml`中指定

```yaml
extra_hosts:
  - "driftwood-cloud:${DRIFTWOOD_CLOUD_HOST}"
```

并提供一个`.env`文件和设置`DRIFTWOOD_CLOUD_HOST`的值，然后设置`
proxy_pass  http://driftwood-cloud:8900/;`，对于上面的方式来说，`driftwood-cloud/docker-compose.yml`这些都已经配置好了。

然后

```shell
cd driftwood-cloud\docker\basic
docker-compose up -d nginx
```

由于

```yaml
volumes:
  - ./etc/nginx/html:/usr/share/nginx/html
  - ./etc/nginx/nginx.conf:/etc/nginx/nginx.conf
  - ./etc/nginx/conf.d/:/etc/nginx/conf.d
  - ./etc/nginx/log:/var/log/nginx
```
`nginx`主要的目录都已挂载出来，所以更新页面或者`nginx`配置后，只需要`reload`或者重启容器就好了。