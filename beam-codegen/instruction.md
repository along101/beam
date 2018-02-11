## Beam Codegen 使用指南
Codegen(代码生成)功能目前有以下两种使用方式
- maven 插件模式
- 命令行模式


## maven 插件模式
##### 在maven工程的pom中增加插件
```xml
    <build>
        <plugins>
            <plugin>
                <groupId>com.yzl.framework</groupId>
                <artifactId>beam-codegen-maven</artifactId>
                <version>0.1.1-SNAPSHOT</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>proto2java</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```
##### 在maven工程的pom中增加如下依赖
```xml
        <dependency>
            <groupId>com.google.protobuf</groupId>
            <artifactId>protobuf-java</artifactId>
            <version>3.4.0</version>
        </dependency>
        
        <dependency>
            <groupId>com.yzl.framework</groupId>
            <artifactId>beam-core</artifactId>
            <version>0.1.1-SNAPSHOT</version>
        </dependency>
```
##### 编写proto文件
在 src/main/proto 路径下编写proto文件，以下是当前版本codegen所支持的proto文件的demo
```proto
syntax = "proto3";

package com.yzl.framework.beam.proto;
service Simple {
    rpc sayHello (HelloRequest) returns (HelloReply) {
    }
}

// The request message containing the user's name.
message HelloRequest {
    string name = 1;
}

// The response message containing the greetings
message HelloReply {
    string message = 1;
}
```
## 注意 ##
* 本版本的codegen暂不能很好地支持proto的 *option 语法* 
如```option java_outer_classname = "TimestampProto";```等皆会引起异常
故建议以如上的proto文件模板来编写proto文件
* 如若要在message中使用google提供的标准类型，如timestamp，需在pom的*plugin*中添加如下配置
```proto
     <configuration>
         <includeStdTypes>true</includeStdTypes>
     </configuration>
```


## 命令行模式
命令行模式下，默认是生成带有pom文件的maven工程(标准文件夹结构)，其中pom文件中关于maven工程的参数需要通过命令行指定，否则采用默认配置。
#### 最简 Cli Sample
```
java -jar beam-codegen-0.1.1-SNAPSHOT.jar
```
这个命令行，会在beam-codegen-0.1.1-SNAPSHOT.jar所在的文件夹中(同时包括当前文件夹的子文件夹)，搜索用于代码生成的proto文件。
对应的java源码将保存于beam-codegen-0.1.1-SNAPSHOT.jar所在文件夹路径下中的名为"generated-sources"的文件夹中。


#### Cli Parameter
```
java -jar beam-codegen-0.1.1-SNAPSHOT.jar -i D:\test -o D:\test\test-generated -s -package -artifactId demo
```
目前支持的命令行参数

* -i    后跟用于指定用于代码生成的proto文件的绝对路径
* -o    后跟用于指定生成代码的输出地址绝对路径
* -s    用于允许google标准的类型，如 timestamp

* -groupId    后跟maven工程的配置之一
* -artifactId    后跟maven工程的配置之一
* -artifactVersion    后跟maven工程的配置之一
* -package    用于决定是否在代码生成完成后进行*maven package*来打包(默认是jar包形式)，输入此配置代表需要。

