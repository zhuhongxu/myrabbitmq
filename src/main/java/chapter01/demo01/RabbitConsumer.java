package chapter01.demo01;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 消费者
 */
public class RabbitConsumer {
    private static final String QUEUE_NAME = "zhx_queue";
    private static final String IP_ADDRESS = "192.168.1.198";
    private static final String USERNAME = "test";
    private static final String PASSWORD = "test123";
    private static final int PORT = 5672;

    public static void main(String[] args) {
        Address[] addresses = new Address[]{
                new Address(IP_ADDRESS, PORT)
        };
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername(USERNAME);
        connectionFactory.setPassword(PASSWORD);

        try {
            Connection connection = connectionFactory.newConnection(addresses);
            final Channel channel = connection.createChannel();

            //设置客户端最多接收未被ack的消息的个数
            channel.basicQos(64);

            final Consumer consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println("receive message: " + new String(body));
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            };
            channel.basicConsume(QUEUE_NAME, consumer);
            TimeUnit.SECONDS.sleep(5);
            channel.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
