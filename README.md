## 第二章、RabbitMQ入门
### 2.1	相关概念介绍
#### 2.1.1	生产者和消费者
生产者：就是消息投递的一方；
消费者：就是接受消息的一方；
Broker：消息中间件的服务节点；
#### 2.1.2	队列
队列：是RabbitMQ的内部对象，用于存储消息；
#### 2.1.1	交换器、路由键、绑定
Exchange：交换器，生产者现将消息发送到Exchange，由焦化你将消息路由到一个或者多个队列，RabbitMQ中有4中类型的交换器；
