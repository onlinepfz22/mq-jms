package com.bsoft.mq.jms.queue;

import com.bsoft.mq.jms.Constants;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.xml.soap.Text;

/**
 * @Description 消息消费者
 *
 * */
public class MessageConsumer {

    private final static String am_url = "tcp://localhost:61616";
    private final static String queueName = "jms-queue-model";

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
            Constants.AUTHENTICATION_USERNAME_ADMIN,
            Constants.AUTHENTICATION_PASSWORD_ADMIN, am_url);

        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();//开启连接,物理连接；

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);//第二个参数为应答模式

            //创建一个目标即队列(消息提供者)
            Destination destination = session.createQueue(queueName);

            //创建消费者
            javax.jms.MessageConsumer messageConsumer = session.createConsumer(destination);

            /*(1)、recieve方式消费消息*/
           /* while(true) {
                //消费者消费目的地的消息,此时队列模式为P2P模式，即目的地队列消息中一个消息只能被一个消费者所消费
                TextMessage textMessage = (TextMessage) messageConsumer.receive();
                System.out.println("客户端消费的服务消息为：" + textMessage==null?"no message" : textMessage.getText());
            }*/

            /*(2)、监听器消费消息*/
            messageConsumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    TextMessage textMessage = (TextMessage) message;
                    System.out.println("客户端消费的服务消息为：" + textMessage==null?"no message" : textMessage);
                }
            });
        } catch (JMSException e) {

        } finally {
            //关闭连接
            /*if(null != connection) {
                try {
                    connection.close();
                } catch (JMSException e) {

                }
            }*/
        }
    }
}