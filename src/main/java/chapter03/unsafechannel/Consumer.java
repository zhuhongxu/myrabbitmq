package chapter03.unsafechannel;

import com.rabbitmq.client.*;
import system.RabitMqEnum;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

public class Consumer {

    private static final String QUEUE_NAME = "zhx_queue";

    public static void main(String[] args) {
        Address[] addresses = new Address[]{
                new Address(RabitMqEnum.IP_ADDRESS.getValue(), Integer.parseInt(RabitMqEnum.PORT.getValue()))
        };
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername(RabitMqEnum.USERNAME.getValue());
        connectionFactory.setPassword(RabitMqEnum.PASSWORD.getValue());

        try {
            //用于记录一共消费了多少条消息
            final AtomicLong count = new AtomicLong();

            Connection connection = connectionFactory.newConnection(addresses);
            final Channel channel = connection.createChannel();

            //设置客户端最多接收未被ack的消息的个数
            channel.basicQos(64);

            final com.rabbitmq.client.Consumer consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println("receive message: " + new String(body));
//                    try {
//                        TimeUnit.SECONDS.sleep(1);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    count.addAndGet(1);
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            };
            channel.basicConsume(QUEUE_NAME, consumer);



            TimeUnit.SECONDS.sleep(70);
            System.out.println("一共消费了：" + count.get() + " 条数据。");
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
