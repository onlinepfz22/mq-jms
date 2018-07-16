package com.bsoft.mq.jms.queue;

import com.bsoft.mq.jms.Constants;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * @Description 消息生产者
 *
 *    ActiveMQ执行流程源码调用分析：ActiveMQ采取java设计模式中的工厂模式创建用于操作中间件的类,其实现均基于JMS规范进行实现
 *      (1)、连接工厂ConnectionFactory(JMS) ：连接工厂实现类ActiveMQConnectionFactory，此实现工厂类创建用于连接中间件的类Connection(JMS)
 *          以下小序号中调用的方法均在默认的工厂实现类ActiveMQConnectionFactory中进行,
 *
 *          ActiveMQConnectionFactory(默认连接工厂实现类，实现了JMS规范中的ConnectionFactory接口)
 *              ConnectionFactory，QueueConnectionFactory，TopicConnectionFactory
 *
 *              ActiveMQConnection
 *                  Transport   <---    TcpTransport
 *                        TcpTransport    <---    SocketFactory
 *
 *              ActiveMQSession
 *
 *              ActiveMQDestination
 *                  Queue    <---    Destination
 *                  ActiveMQQueue    <---    ActiveMQDestination
 *
 *              ActiveMQMessageProducer
 *
 *              ActiveMQTextMessage     <---    ActiveMQMessage     <---    Message (继承关系)
 *                  TextMessage实现接口关系
 *
 *          1>、返回ActiveMQConnection,调用createConnection，返回Connection(JMS)的实现类ActiveMQConnection，
 *                  此类中创建了Transport接口的具体实现TcpTransport类，
 *                  并在此TcpTransport类中通过SocketFactory接口的DefaultSocketFactory工厂类创建socket用于后面的连接
 *          2>、返回ActiveMQSession，调用createSession(),返回Session(JMS)的实现类ActiveMQSession，通过此类具体操作针对消息队列的创建，消息的创建等
 *          3>、返回ActiveMQDestination，调用createQueue(),返回Destination(JMS)的实现类ActiveMQDestination，此类即消息队列
 *                  Queue    <---    Destination
 *                  ActiveMQQueue    <---    ActiveMQDestination
 *                  ActiveMQSession调用createQueue()创建队列，创建的队列实现类ActiveMQQueue继承了ActiveMQDestination类并实现了Queue接口，Queue继承Destination接口
 *          4>、返回ActiveMQMessageProducer,调用createProducer(ActiveMQDestination),返回返回MessageProducer(JMS)的实现类ActiveMQMessageProducer
 *                  默认的小心创建者\发布者实现类，实现了JMS规范中的MessageProducer接口,此实现类通过ActiveMQSession的创建并将以后由session创建的消息通过
 *                  ActiveMQMessageProducer实现类向ActiveMQDestination队列发送
 *          5>、ActiveMQTextMessage继承了ActiveMQMessage类并实现了TextMessage(JMS规范)
 *ActiveMQSouceAnalysis
 * */
public class MessageProducer {
    private String description = "消息生产者";
    private final static String am_url = "tcp://localhost:61616";
    private final static String queueName = "jms-queue-model";

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
            Constants.AUTHENTICATION_USERNAME_ADMIN,
            Constants.AUTHENTICATION_PASSWORD_ADMIN, am_url);

        try {
            //createConnection()方法已经创建一个connection连接并通过TcpTransport中的doStart()方法，进而调用connect方法完成socket连接，连接根据url指定连接协议
            //进行连接
            Connection connection = connectionFactory.createConnection();   //ActiveMQConnection
            connection.start();//开启连接,物理连接；

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);//第二个参数为应答模式    ActiveMQSession

            //创建一个目标即队列(提供者)，队列创建交由ActiveMQDestination的setPhysicalName()方法进行创建，方法内部需要检查队列名称是否合法
            Destination destination = session.createQueue(queueName);   //ActiveMQQueue

            //创建生产者,生产者需要知道自己生产消息的地方，即在哪生产，所以创建生产者需要传递一个目的地
            javax.jms.MessageProducer messageProducer = session.createProducer(destination);    //ActiveMQMessageProducer

            for(int i=0;i<10;i++) {
                //创建一个消息
                TextMessage textMessage = session.createTextMessage("send message for text as " + i);
                //通过生产者将创建好的消息发送到目标队列
                messageProducer.send(textMessage);

                System.out.println("message send success，the message is " + textMessage.getText());
            }

            //关闭连接
            connection.close();
        } catch (JMSException e) {

        }
    }

}