
### 搭建RabbitMQ生产环境
#### 一、硬件配置
阿里云1核2G1M带宽
#### 二、安装erlang
按照rabbitmq官网安装的erlang(http://www.rabbitmq.com/install-debian.html)，个人觉得非常复杂，原因是不太懂ubuntu的apt原理，但最后还是安装成功了，可以用erl命令来确认erlang是否安装成功，当然，这个命令也可以查看erlang的version，我安装的是21version；
#### 三、安装rabbitmq
一开始按照rabbitmq官网安装(http://www.rabbitmq.com/install-debian.html)，但以失败而告终，原因是不太懂ubuntu但apt原理，但最后还是安装成功了(version是3.7.9)，采用的是解压安装包的方式，对于我这种linux小白，这是我最喜欢的安装方式，步骤如下：
* wget https://github.com/rabbitmq/rabbitmq-server/releases/download/v3.7.9-rc.3/rabbitmq-server-generic-unix-3.7.9-rc.3.tar.xz
* xz -d rabbitmq-server-generic-unix-3.7.9-rc.3.tar.xz
* tar -xvf rabbitmq-server-generic-unix-3.7.9-rc.3.tar
* cd rabbitmq-server-generic-unix-3.7.9-rc.3/sbin
* ./rabbitmq-server start
* 查看是否启动成功：./rabbitmqctl status
如果启动成功，此时rabbitmq会默认为我们创建一个guest guest的用户，默认情况下，该账户仅限localhost使用，这里使用rabbitmqctl创建一个用户(rabbitmqctl常用命令：http://www.rabbitmq.com/rabbitmqctl.8.html#Access_Control)：
```./rabbitmqctl add_user hassan hassan```，然后执行```./rabbitmqctl list_users```发现hassan用户的tags(如果不知道什么是tag，可以阅读这片文章的用户管理部分：https://blog.csdn.net/qq_33730729/article/details/83755276)为空，说明它没有任何权限，
执行```./rabbitmqctl set_user_tags hassan administrator```为hassan用户赋予最高权限；那么就开始运行一个helloworld吧！
* 运行chapter01.helloworld.RabbitProducer，报错：reply-code=530, reply-text=NOT_ALLOWED - access to vhost '/' refused for user 'hassan'
* 执行```./rabbitmqctl set_permissions -p / hassan . . .```为hassan用户赋予vhost为"/"的所有权限
* 继续运行运行chapter01.helloworld.RabbitProducer，没有任何报错，说明生产成功
* 运行chapter01.helloworld.RabbitConsumer，控制台打印：receive message: Hello world，消费成功！！！！
