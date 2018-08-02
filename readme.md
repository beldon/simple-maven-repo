# maven repo

### 说明
用java写的一个简单的maven私服

### 技术栈
* spring boot

### 配置

```
maven:
  pass: root # maven用户名
  user: root # maven用户名
  context-path: /maven # maven仓库前缀
  repos: # 仓库配置，是一个list，可以配置多个仓库
  - id: release # 仓库id，唯一，会映射到url
    name: release #仓库名字，
    type: hosted #仓库类型，group（仓库组），hosted（宿主仓库），proxy（代理仓库）
  - id: snapshots
    name: snapshots
    type: hosted
  - id: aliyun
    name: aliyun
    type: proxy
    proxy-url: http://maven.aliyun.com/nexus/content/groups/public/
  - id: public
    name: public
    type: group
    group: # 仓库组，表示依赖其他仓库，如果aliyun仓库找不到则去release找，再找不到就去snapshots找
    - aliyun
    - release
    - snapshots
```

* pom.xml demo
```
<repositories>
		<repository>
			<id>custom</id>
			<name>public</name>
			<url>http://localhost:9999/maven/public</url>
			<releases>
				<enabled>true</enabled>
			</releases>
			<snapshots>
				<enabled>true</enabled>
			</snapshots>
		</repository>
	</repositories>
```
url规则是 {url}:{server.port}/{maven.context-path}/{repo.id}   

具体参考 `application.yml`
### todo
* 并发下载