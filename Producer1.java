
import org.apache.activemq.ActiveMQConnectionFactory;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import javax.jms.*;

public class Producer1 {
    public static class Producer {
        private final Session session;
        private final Destination destination;
        public Producer(Session session, Destination destination) {
            this.session = session;
            this.destination = destination;
        }

        public void sendMessage(Message message) throws JMSException {
            MessageProducer messageProducer = 
              session.createProducer(destination);
            //TextMessage textMessage = 
              //session.createTextMessage(message);
            //messageProducer.send(textMessage);
            messageProducer.send(message);
            //textMessage.setStringProperty("signature222","signature");
            //message.setStringProperty("signature222","signature");
        }
    }

    public static void main(String[] args)
             throws JMSException,InterruptedException {
        String brokerURL = "tcp://afsaccess2.njit.edu:33080", 
               username = "usr", password = "using", 
               queueName = "QSONAR";
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
                System.out.println("TestAMQ - Producer Mode");
                System.out.println("Connected to " + brokerURL);
                Producer producer = new Producer(session, destination);
                
                
                int count=0;
                Auth seqKey = new Auth("AABECZT");
                
                //System.out.println("Sequence key is: " + seqKey);
                
                String message = "EAM DRILL-TARGET-PACKAGE-" + args[0] + "/" + "11" + " " + args[1];
                //System.out.println("Order is: " + message);
                String signature = seqKey.sign(message);
                //System.out.println("Encoded signature is: " + signature);        
                
                Message m2 = session.createTextMessage(message);
                //Message m2 = session.createMessage(); --------------------------------
                //Message m2 = new Message(textMessage); 
                m2.setStringProperty("signature",signature);
                m2.setStringProperty("boat", args[2]);
                System.out.println("For Boat: " + m2.getStringProperty("boat"));
                //m2.setStringProperty("boat1", "Tier");
                //m2.setStringProperty("boat2", "West");
                //m2.setStringProperty("boat3", "York");
                //m2.getMessageNumber();
                
                //String b = m2.getStringProperty("boat1");
                //System.out.println(b);
                //System.out.println("this is signature: " + signature);
                
                String op = m2.getStringProperty("signature222");
                System.out.println("op: " + op);       
                
                //producer.sendMessage(signature);
                producer.sendMessage(m2);
                System.out.println("Sent: " + args[1]);
                count++;
                
                
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            if(session != null) session.close();
            if(connection != null) connection.close();
        }
    }
    
    private Mac auth;
      public void Auth(String var1) {
          Object var2 = null;
          SecretKeySpec var3 = null;
  
          try {
              byte[] var6 = MessageDigest.getInstance("SHA-1").digest(var1.getBytes("UTF-8"));
              var3 = new SecretKeySpec(Arrays.copyOf(var6, 16), "AES");
              this.auth = Mac.getInstance("HmacSHA1");
              this.auth.init(var3);
          } catch (Exception var5) {
              this.auth = null;
              System.out.println("Auth:Auth:ERROR");
              var5.printStackTrace();
          }
  
      }
  
      public String sign(String var1) {
          try {
              byte[] var2 = this.auth.doFinal(var1.getBytes("UTF-8"));
              return Base64.getEncoder().encodeToString(var2);
          } catch (Exception var3) {
              var3.printStackTrace();
              return null;
          }
      }
}
