部署到 Docker (Nginx) 环境
==========

> 调整 `nginx.conf` 中 `proxy_pass  http://192.168.1.60:8900/;` 为你的后端网关地址，不要遗忘 `/`

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
> 上面的方式会导致每一次部署都要重新构建镜像，一种方便的方式是使用`driftwood-cloud/docker/basic`中`nginx`独立部署的方式.
将`driftwood-cloud/driftwood-web/docker/dist`的内容复制到`driftwood-cloud/docker/basic/etc/nginx/html`，同样的，你需要修改`driftwood-cloud/docker/basic/etc/nginx/conf.d/default.conf`中 `proxy_pass  http://192.168.1.60:8900/;` 为你的后端网关地址，不要遗忘 `/`。

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