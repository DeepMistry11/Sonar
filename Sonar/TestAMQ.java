/*
 * TestAMQ: test driver for AMQ platform
 * Author : Vishnu D
 * Please send questions about this code to your
 * TA or Instructor only!
 *
 */
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class TestAMQ {
    public static class Producer {
        private final Session session;
        private final Destination destination;

        public Producer(Session session, Destination destination) {
            this.session = session;
            this.destination = destination;
        }

        public void sendMessage(String message) throws JMSException {
            MessageProducer messageProducer = 
              session.createProducer(destination);
            TextMessage textMessage = 
              session.createTextMessage(message);
            messageProducer.send(textMessage);
        }
    }

    public static class Consumer {
        private final Session session;
        private final Destination destination;

        public Consumer(Session session, Destination destination) {
            this.session = session;
            this.destination = destination;
        }

        public String listenToMessage(long sleeptime)
                 throws JMSException,InterruptedException {
            MessageConsumer messageConsumer = 
               session.createConsumer(destination);
            TextMessage m = null;

            m = (TextMessage) messageConsumer.receive();

            return m.getText();
        }
    }

    public static void main(String[] args)
             throws JMSException,InterruptedException {
        if(args.length < 5 || args.length > 6) {
          System.out.println("Few arguments or too much arguments");
          System.out.println("Try running with arguments: (C/P) " +
                             "ip:port username password queuename message");
            return;
        }
        String commMode = args[0], brokerURL = args[1], 
               username = args[2], password = args[3], 
               queueName = args[4];
        ConnectionFactory connectionFactory = 
                  new ActiveMQConnectionFactory(brokerURL);
        Session session = null;
        Connection connection = null;
        try {
            connection = 
               connectionFactory.createConnection(username, password);
            session =
               connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queueName);
            connection.start();
            if (commMode.equalsIgnoreCase("C")) {
                System.out.println("TestAMQ - Consumer Mode");
                System.out.println("Connected to " + brokerURL +
                                   "...waiting for message");
                Consumer consumer = new Consumer(session, destination);
                String message =
                     consumer.listenToMessage(Long.parseLong(args[5]));
                System.out.println("Recd: " + message);
            } else if (commMode.equalsIgnoreCase("P") 
                       && args.length == 6 
                       && args[5].length() > 0) {
                System.out.println("TestAMQ - Producer Mode");
                System.out.println("Connected to " + brokerURL);
                Producer producer = new Producer(session, destination);
                producer.sendMessage(args[5]);
                System.out.println("Sent: " + args[5]);
            } else {
                System.out.println("TestAMQ shutting down. Invalid comm mode selected.");
            }
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            if(session != null) session.close();
            if(connection != null) connection.close();
        }
    }
}
