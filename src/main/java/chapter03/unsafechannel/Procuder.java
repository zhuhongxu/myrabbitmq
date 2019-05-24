package chapter03.unsafechannel;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import system.RabitMqEnum;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

public class Procuder {
    private static final String EXCHANGE_NAME = "zhx_exchange";
    private static final String ROUTING_KEY = "zhx_routing_key";
    private static final String QUEUE_NAME = "zhx_queue";

    public static void main(String[] args) {
        try {
            Date startDate = new Date();
            //用于记录一共生产了多少条消息
            final AtomicLong count = new AtomicLong();

            //创建一个Channel
            Object[] arr = getChannelAndConnection();
            Connection connection = (Connection) arr[1];
            final Channel channel = (Channel) arr[0];

            //创建一个exchange(a durable, non-autodelete exchange of "direct" type)
            channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);

            //创建一个queue(a durable, non-exclusive, non-autodelete queue with a well-known name)
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            //将上面的exchange和queue通过一个routingKey绑定
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);


            Thread produceA = new Thread(new Runnable() {
                public void run() {
                    for (int i = 0; i < 10000; i ++){
                        String msg = "unsafe channel" + i;
                        try {
                            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
                            count.addAndGet(1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            Thread produceB = new Thread(new Runnable() {
                public void run() {
                    for (int i = 10000; i < 20000; i ++){
                        String msg = "unsafe channel" + i;
                        try {
                            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
                            count.addAndGet(1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            Thread produceC = new Thread(new Runnable() {
                public void run() {
                    for (int i = 20000; i < 30000; i ++){
                        String msg = "unsafe channel" + i;
                        try {
                            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
                            count.addAndGet(1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });


            Thread produceD = new Thread(new Runnable() {
                public void run() {
                    for (int i = 30000; i < 40000; i ++){
                        String msg = "unsafe channel" + i;
                        try {
                            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
                            count.addAndGet(1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            produceA.start();
            produceB.start();
            produceC.start();
            produceD.start();
            try {
                produceA.join();
                produceB.join();
                produceC.join();
                produceD.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Date endDate = new Date();
            System.out.println("一共生产了：" + count.get() + " 条数据，耗时：" + (endDate.getTime() - startDate.getTime()) + " 毫秒。");


            //关闭资源
            channel.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }


    }

    public static Object[] getChannelAndConnection() throws IOException, TimeoutException {
        Object[] arr = new Object[2];

        //创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(RabitMqEnum.IP_ADDRESS.getValue());
        connectionFactory.setPort(Integer.parseInt(RabitMqEnum.PORT.getValue()));
        connectionFactory.setUsername(RabitMqEnum.USERNAME.getValue());
        connectionFactory.setPassword(RabitMqEnum.PASSWORD.getValue());

        //创建一个连接
        Connection connection = connectionFactory.newConnection();

        //创建一个Channel
        Channel channel = connection.createChannel();
        arr[0] = channel;
        arr[1] = connection;
        return arr;
    }
}
