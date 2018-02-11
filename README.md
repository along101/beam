# Beam
拍拍贷微服务框架2.0之RPC框架
# 概述
Beam是一套高性能、易于使用的分布式远程服务调用(RPC)框架。

# 功能
- 使用protobuf定义IDL，支持契约优先开发模式。
- 支持通过spring配置方式集成，无需额外编写代码即可为服务提供分布式调用能力。
- 支持集成注册中心，提供集群环境的服务发现及治理能力。
- 支持动态自定义负载均衡、流量调整等高级服务调度能力。
- 基于高并发、高负载场景进行优化，保障生产环境下RPC服务高可用。

# Quick Start

快速入门中会给出一些基本使用场景下的配置方式，更详细的使用文档请参考

> 如果要执行快速入门介绍中的例子，你需要:
>  * JDK 1.8或更高版本。
>  * java依赖管理工具，如[Maven][maven]。


## <a id="peer-to-peer"></a>简单调用示例

1. 编写protobuf接口调用的契约IDL

    ```proto
    syntax = "proto3";
    
    package com.yzl.framework.beam.proto;
    service Simple {
        rpc sayHello (HelloRequest) returns (HelloReply) {
        }
    }
    
    message HelloRequest {
        string name = 1;
    }
    
    message HelloReply {
        string message = 1;
    }
    
    ```
    命名为helloworld.proto

2. 通过beam-codegen-cli生成maven工程
    
    ```cmd
    java -jar beam-codegen-VERSION.jar -i ./test -o ./test-generated
    ```
    将上一步编辑好的helloworld.proto拷贝到./test目录下，执行命令生成一个maven工程，该maven工程包含./test目录下所有的proto文件

3. 分别在服务端和客户端的spring boot maven工程中添加依赖
    ```xml
        <dependency>
            <groupId>com.yzl.framework</groupId>
            <artifactId>beam-demo-api</artifactId>
            <version>${beam-demo-api.version}</version>
        </dependency>
        <dependency>
            <groupId>com.yzl.framework</groupId>
            <artifactId>beam-springboot</artifactId>
            <version>${beam.version}</version>
        </dependency>
    ```
    beam-demo-api为上一步生成的maven工程

4. 服务端实现接口，使用@BeamServiceComponent注册spring bean和发布Beam服务
    ```java
    @BeamServiceComponent
    public class SimpleImpl implements Simple {
    
        @Override
        public Helloworld.HelloReply sayHello(Helloworld.HelloRequest request) {
            String hello = "Hello " + request.getName() + ". " + RandomUtils.nextInt();
            return Helloworld.HelloReply.newBuilder().setMessage(hello).build();
        }
    
    }
    ```
5. 修改服务端spring boot配置application.properties
    ```properties
    server.port=8080
    beam.registry.name=direct
    ```
6. 客户端接口调用，在spring bean中使用@BeamClient注解接口的Field，自动注入代理
    ```java
    
    @Component
    @RestController
    public class SayHelloController {
    
        @BeamClient
        private Simple simple;
    
        @RequestMapping("/sayhello")
        public Object sayHello(@RequestParam("name") String name) {
            Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder()
                    .setName(name).build();
            Helloworld.HelloReply helloReply = simple.sayHello(helloRequest);
            return helloReply != null ? helloReply.toString() : "null";
        }
    }
    
    ```
    客户端构造proto的Message对象，通过接口代理直接调用远程服务

7. 修改服务端spring boot配置application.properties
    ```properties
    server.port=8081
    beam.registry.name=direct
    beam.registry.direct.address=localhost:8080
    ```
    
8. 启动服务端客户端spring boot应用，输入地址测试
    ```cmd
    http://客户端host:port/sayhello?name=test
    ```
    