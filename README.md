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
* header：通过键值对匹配，不常用；
### 2.1.5 RabbitMQ运转流程
#### 生产者发送消息的时候
1. 生产者连接到RabbitMQ Broker，建立一个连接（connection），开启一个信道（channel）；
2. 生产者声明一个交换器，并设置相关属性，比如交换器类型，是否持久化等；
3. 生产者声明一个队列并设置相关属性，比如是否排他，是否持久化，是否自动删除等；
4. 生产者通过路由键将交换器和队列绑定起来；
5. 生产者发送信息至RabbitMQ Broker，其中包含路由键，交换器等信息；
6. 相应的交换器根据接收到的路由键查找相匹配的队列；
7. 如果没有找到，则根据生产者配置的属性选择丢弃还是会退给生产者；
8. 关闭信道；
9. 关闭连接。
#### 消费者消费消息的时候
1. 消费者连接到RabbitMQ Broker，建立一个连接（connection），开启一个信道（channel）；
2. 消费者向RabbitMQ Broker请求消费相应队列中的消息，可能会设置相应的回调函数，以及做一些准备工作；
3. 等待RabbitMQ Broker回应并投递相应队列中的消息，消费者接收消息；
4. 消费者确认（ack）接收到的消息；
5. RabbitMQ从队列中删除相应已经被确认的消息；
6. 关闭信道；
7. 关闭连接；
#### 关于信道（channel）的一些思考
我们知道，无论生产者还是消费者，都需要和RabbitMQ Broker建立连接，这个连接就是一条TCP连接，也就是Connection。一旦TCP连接建立起来，客户端紧接着可以创建一个AMQP信道（Channel），每一个信道都会被指派一个唯一的ID。信道是建立在Connect上的虚拟连接，RabbitMQ处理每条AMQP指令都是通过信道完成的，我们完全可以直接使用Connection就能完成信道的工作，为什么还要引入信道呢？
试想这样一个场景，一个应用程序需要很多歌消费者和生产者从RabbitMQ Broker消费和生产消息，那么必然需要建立很多个Connection，也就是许多个TCP连接。然而对于操作系统而言，建立和销毁TCP连接是非常昂贵的开销，如果遇到使用高峰，性能瓶颈也随时显现。RabbitMQ采用类似NIO的做法，选择了TCP连接复用，不仅可以减少性能开销，同时也便于管理。
每个线程把持一个信道，所以信道复用了Connection的TCP连接。同时RabbitMQ可以确保每个线程的私密性，就像拥有独立的连接一样，当每个信道的流量不是很大时，复用单一的Connection可以在产生性能瓶颈的情况下有效的节省TCP连接资源。但是当信道本身的流量很大时，这时候多个信道复用一个Connection就会产生性能瓶颈，
进而使整体的流量被限制了，此时就需要开辟多个Connection，将这些信道均摊到这些Connection中。
信道在AMQP中是一个很重要的概念，大多数操作都是在信道这个层面上展开的，例如：channel.exchangeDeclare、channel.queueDeclare、channel.basicPublish、channel.basicConsum等。
### 2.2 AMQP协议介绍
* Module Layer:位于协议最高层，主要定义了一些客户端调用的命令，客户端可以利用这些命令实现自己的业务逻辑，例如Queue.Declare命令声明一个队列，Basic.Consum订阅消费一个队列中的消息。
* Session Layer:位于中间层，主要负责将客户端的命令发送给服务器，再将服务端的应答返回给客户端，主要为客户端和服务器之间的通信提供可靠性同步机制和错误处理。
* Transport Layer:位于最底层，主要传输一些二进制流数据，提供帧的处理、信道复用、错误检测和数据表示等。
AMQP说到底还是一个通信协议，从low-level层面来说，它是一个应用层协议，用于填充TCP协议层的数据部分。而从high-level层面来说，AMQP是通过协议命令进行交互的，可以将其看作一系列结构化命令的集合，这里的命令代表一种操作。
### 2.3 小结
本章主要是RabbitMQ的入门知识，首先介绍了生产者、消费者、队列、交换器、绑定、路由键、连接、信道等基本属于，后来又介绍了交换器的4种基本类型。
RabbitMQ可以看做是AMQP协议的具体实现，2.2节大致介绍了AMQP命令以及与RabbitMQ客户端中方法如何一一对应以及AMQP命令运转流程的介绍（这部分上面没写，只是一句话带过）。
