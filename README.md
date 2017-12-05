# EasyID 分布式主键生成系统

EasyID是一个以snowflake算法为基础的轻量、高效的分布式主键生成系统。<br/>
生成的ID是一个64位的 长整型，全局唯一，保持递增，相对有序。<br/>


# 特点

 - 轻量级，微服务部署，部署方便，不需要单独的服务器，以jar包的形式提供服务；<br/>
 - 一次编译，多处部署，无需多余配置，方便扩容与缩容；<br/>
 - 无延迟，客户端从redis队列中获取ID，服务端保证队列中始终有值，保证应用平滑不停顿；<br/>
 - 高并发；<br/>
 - 高可用，可部署多台EasyID服务，通过zookeeper集中管理服务的注册与退出；<br/>
 - 负载均衡，所有在同一zookeeper中管理的服务端，采用轮询方式创建ID。<br/>
 - redis队列自动扩容与缩容，创建的ID存放在redis队列中供客户端提取，redis队列的大小根据服务端应用数量，自动扩大或缩小；<br/>
  
  
# 技术选型

 - 基于snowflake算法生成ID；<br/>
 - 基于netty实现远程通信；<br/>
 - 基于zookeeper实现服务注册、负载均衡；<br/>
 - 基于redis实现批量存储；<br/>
  
  
# 系统架构

   ![image](https://github.com/fup1990/EasyID/blob/master/EasyID%E7%B3%BB%E7%BB%9F%E6%9E%B6%E6%9E%84%E5%9B%BE.png)
  
 # 编译项目
 
  - 使用maven命令打包easyid:mvn clean instll -Dmaven.test.skip=true；<br/>
  
# 服务部署

 - 将本地ip地址配置到hosts文件中；<br/>
 - 部署easyid-server：java -jar EasyID-Server-STABLE-1.0.jar -zookeeper127.0.0.1:2181 -redis127.0.0.1:6379<br/>
 >参数说明：<br/>
 >-zookeeper：zookeeper地址<br/>
 >-redis：redis地址<br/>
 
 # 客户端

 - 添加easyid-cli项目的依赖；<br/>
 - new EasyID(zk,redis)；<br/>
 > EasyID需要两个构造参数。其中，zk表示zookeeper的服务地址，redis表示redis的服务地址。详情请参考easy-demo项目Main.java
 - 通过EasyID类的nextId()，获取id。<br/>

# docker部署
 - docker build ![Dockerfile](https://github.com/fup1990/EasyID/blob/master/Dockerfile)
 - docker run --name easyid --net=host -p 9131:9131 -d easyid<br/>
 > 注：--net=host，选用host模式设置docker的网络连接，将宿主机的ip注册到zookeeper，否则将访问不到docker服务
  


