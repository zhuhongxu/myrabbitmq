package chapter01.demo01;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 生产者
 */
public class RabbitProducer {
    private static final String EXCHANGE_NAME = "zhx_exchange";
    private static final String ROUTING_KEY = "zhx_routing_key";
    private static final String QUEUE_NAME = "zhx_queue";
    private static final String IP_ADDRESS = "192.168.1.198";
    private static final String USERNAME = "test";
    private static final String PASSWORD = "test123";
    private static final int PORT = 5672;

    public static void main(String[] args) {
        try {
            //创建连接工厂
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(IP_ADDRESS);
            connectionFactory.setPort(PORT);
            connectionFactory.setUsername(USERNAME);
            connectionFactory.setPassword(PASSWORD);

            //创建一个连接
            Connection connection = connectionFactory.newConnection();

            //创建一个Channel
            Channel channel = connection.createChannel();

            //创建一个exchange(a durable, non-autodelete exchange of "direct" type)
            channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);

            //创建一个queue(a durable, non-exclusive, non-autodelete queue with a well-known name)
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            //将上面的exchange和queue通过一个routingKey绑定
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

            //向队列中发送一条持久化的消息：Hello world!
            String message = "Hello world";
            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());

            //关闭资源
            channel.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }


    }


}
