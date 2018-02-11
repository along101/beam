###关于URL  
URL的定义为：protocol://host:port/path?param1=value1&param2=value2...  
URL分为以下几类，使用urlType参数区分:

#### registryUrl 注册中心Url
定义注册中心Url
protocol：为注册中心名称
host: 注册中心host
port：端口
path：注册中心实现类的包名+类名
urlType：registry

#### serviceUrl 服务提供Url
定义服务提供方Url
protocol：服务提供的协议
host: 提供方ip地址
port：端口
path：服务接口的包名+类名
urlType：service

#### referUrl 服务引用的Url
订阅服务是引用端的Url
protocol：服务提供的协议
host：引用的客户端的ip地址
port：端口为0
path：服务接口的包名+类名
urlType：refer
provider

### api 客户端使用
1. 选协议
2. 选注册中心
3. 选ha、loadbalance初始化集群
4. 输入接口生成代理

### api 服务端注册发布服务
1. 选协议
2. 生成provider
3. 注册provider的URL
4.

通过协议生成provider，通过


### beam协议约定
采用http + protobuf

####服务提供方
http uri：http://ip:port/basePath/接口的包名.类名#方法名?version=版本
version参数可以为空
http request body： 使用