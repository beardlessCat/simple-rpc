#RPC框架
## 一、介绍
RPC，即 Remote Procedure Call（远程过程调用），调用远程计算机上的服务，就像调用本地服务一样。RPC可以很好的解耦系统，如WebService就是一种基于Http协议的RPC。
## 二、实现的功能
（1）除了Java序列化协议，还支持protobuf序列化协议，配置即用。

（2）增加多种负载均衡算法（随机、轮询、加权轮询、平滑加权轮询），配置即用。

（3）客户端增加本地服务列表缓存，提高性能。

（4）通过netty建立长链接进行通讯。

（5）服务提供者使用线程池进行异步处理。
## 三、技术栈
- spingBoot starter进行依赖包开发
- 使用zookeeper作为注册中心
- 使用netty作为通信框架
- 消息编解码使用protostuff、java
- 使用SPI来根据配置动态选择负载均衡算法等

服务端增加线程池提高消息处理能力
(1)区分provider与consumer
(2)通讯流转
(3)客户端发送消息后如何如何将收到的结果返回
(4)provider中服务的注册与管理
(5)consumer中客户端的注册与动态代理
(6)本地服务列表
(7)服务注册监听机制