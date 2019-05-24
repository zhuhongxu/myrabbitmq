
### 第三章
摘抄自朱忠华《RabbitMQ实战指南》
#### 3.1 连接RabbitMQ
可以通过指定用户名密码ip端口来实现，也可以通过uri的方式来实现；
注意点：
* Connection可以用来创建多个channel实例，但是channel实例不能在线程之间共享，应用程序应该为每一个线程开辟一个channel，多线程之间共享channel实例是非线程安全的。
* 不要在生产环境使用connection或者channel的isopen()方法，因为会产生竞争，通常情况下，当我们newConnection()或者createChannel()的时候，就认为它们已经成功的处于开启状态。如果在使用channel的过程中如果其已经处于关闭状态，那么程序会抛出一个com.rabbitmq.client.ShutdownSignalException，我们只需要捕获这个异常就行，同时也要试着捕获IOException或者SocketException，以防止Connection意外关闭。
#### 3.2 使用交换器和队列
交换器和队列是AMQP中high-level层面的构建模块，应用程序需要在使用它们的时候就已经存在了，在使用之前需要先声明（declare）它们。
#####  3.2.1 声明交换器
* 使用exchangeDeclare方法；
* durable：是否持久化；
* atuoDelete：设置是否删除，为true表示自动删除。自动删除的前提是至少有一个队列或者交换器与这个交换器绑定，之后所有与这个交换器绑定的交换器和队列都与此解绑。错误的理解：与此交换器连接的客户端都断开时，RabbitMQ会自动删除本交换器。
##### 3.2.2 声明队列
* 使用queueDeclare方法；
* durable：是否持久化；
* exclusive：是否排他。排他的意思是该队列可对当前channel的Connection层面下的所有channel可见，Connection与Connection之间声明的排他队列不能重名，当连接断开或者客户端退出的时候，排他队列会被自动删除，即便该队列时持久化的也没有用。
* atuoDelete：设置是否删除，为true表示自动删除。自动删除的前提是至少有一个消费者连接到这个队列，之后所有与这个队列连接的消费者都断开时，才会自动删除。错误的理解：当连接到此队列的所有客户端都断开时，这个队列会自动删除。
* 当然，我们也可以声明一个队列，使其在整个应用中可以共享。
##### 3.2.3 queueBind方法详解
将队列和交换器绑定。
##### 3.2.4 exchangeBind方法详解
将交换器与交换器绑定，作用是将消息从source交换器转发到destination交换器。
#### 3.3 发送消息
如果要发送一条消息，可以使用channel的basicPublish方法，该方法可以设定投递模式，优先级，内容类型，headers，过期时间等。
#### 3.4 消息消费
RabbitMQ的消费模式分为两种：推（push）模式和拉（pull）模式。推模式采用Basiic.Consume进行消费，而拉模式则是调用Basic.Get进行消费。
##### 3.4.1 推模式
channel.basicConsume(...)
* queue：队列的名称
* autoAck：设置是否自动确认，建议设置成false，即不自动确认
* consumerTag：消费者标签，用来区分不同的消费者
* noLocal：设置为true则表示不能将同一个Connection中生产者发送的消息传递给这个Connection中的消费者
* exclusive：设置是否排他
* arguments：设置消费者的其他参数
* callback：设置消费者的回调函数，用来处理RabbitMQ推送过来的消息
###### 最佳实践：每个channel拥有自己独立的线程，一个channel对应一个消费者。
##### 3.4.2 拉模式
channel.basicGet(...)
* 返回值是GetResponse，可以使用GetResponse.getBody()来获取消息内容
* 功能是单条的获取信息
###### 推模式和拉模式的区别？
Basic.Consume将信道(Channel)置为接受模式，直到取消队列的订阅为止，在接受模式期间，RabbitMQ会不断的推送消息给消费者，当然推送消息的个数还是会受到Basic.Qos的限制。如果只想从队列获得单条消息而不是持续订阅，建议还是使用Basic.Get进行消费。但是不能将Basic.Get放在一个循环里来代替Basic.Consume，这样会严重影响RabbitMQ的性能，如果要使用高吞吐，理应使用Basic.Consume方法。
#### 3.5 消息的拒绝与确认
为了保证消息从队列可靠的到达消费者，RabbitMQ提供了消息确认机制（message acknowledgement）,消费者在订阅队列时，可以指定autoAck参数，当autoAck等于false时，RabbitMQ会等待消费者显示地回复确认信号后才从内存（或者磁盘）中移出消息（实质上是先打删除标记，然后再删除）；当autoAck置为true时，RabbitMQ会直接吧发出去的消息置为确认，然后从内存（或者磁盘中删除），而不管消费者是否真正消费到了这些消息。
#### 3.6 关闭连接
在应用程序使用完之后，需要关闭连接，释放资源：
channel.close();
connection.close();
显示地关闭channel是个好习惯，但这不是必须的，当connection关闭的时候，channel也会自动关闭；
#### 3.7 小结
本章主要介绍 RabbitMQ 客户端开发的简单使用，按照一个生命周期的维度对连接、创建、 生产、消费和关闭等几个方面进行笼统的介绍，读者学习完本章的内容之后，就能够有效地进 行与 RabbitMQ 相关的开发工作。知是行之始，行是知之成，不如现在动手编写几个程序来实践一下吧。
	


