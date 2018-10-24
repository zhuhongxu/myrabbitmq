## 第二章、RabbitMQ入门
### 2.1	相关概念介绍
#### 2.1.1	生产者和消费者
* 生产者：就是消息投递的一方；
* 消费者：就是接受消息的一方；
* Broker：消息中间件的服务节点；
#### 2.1.2	队列
* 队列：是RabbitMQ的内部对象，用于存储消息；
#### 2.1.1	交换器、路由键、绑定
* Exchange：交换器，生产者现将消息发送到Exchange，由交换器将消息路由到一个或者多个队列，RabbitMQ中有4中类型的交换器；
* RoutingKey：生产者将消息发送给交换器的时候，一般会指定一个RoutingKey，用来指定消息的路由规则，而这个RoutingKey需要和交换器类型和绑定键联合使用才能最终生效；
* Binding：绑定，RabbitMQ中通过绑定将交换器和队列关联起来，在绑定的时候一般会指定一个绑定键（BindingKey），这样RabbitMQ就知道如何正确地将消息路由到队列了；
### 2.1.4 交换器类型
RabbitMQ中，常用的交换器有4种：
* fanout：它会把所有发送到该交换器的消息路由到所有与该交换器绑定的队列中；
* direct：它会把消息路由到那些RoutingKey和BindKey完全匹配的队列中；
* topic：它会把小路由到BindKey和RoutingKey相匹配的队列中，但它们两个的匹配规则有所不同：①二者都是.号分割的字符串；②BindKey中可以存在两种特殊字符串*和#用作模糊匹配，其中*的作用是匹配一个单词，#用于匹配多个（可以是零个）单词；
### 2.1.5 RabbitMQ运转流程
#### 生产者发送消息的时候
(1). 生产者连接到RabbitMQ Broker，建立一个连接（connection），开启一个信道（channel）；
(2). 生产者声明一个交换器，并设置相关属性，比如交换器类型，是否持久化等；
(3). 生产者声明一个队列并设置相关属性，比如是否排他，是否持久化，是否自动删除等；
(4). 生产者通过路由键将交换器和队列绑定起来；
(5). 生产者发送信息至RabbitMQ Broker，其中包含路由键，交换器等信息；
(6). 相应的交换器根据接收到的路由键查找相匹配的队列；
(7). 如果没有找到，则根据生产者配置的属性选择丢弃还是会退给生产者；
(8). 关闭信道；
(9). 关闭连接。
