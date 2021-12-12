/*
 * AMQ Sonar CS656 V3.00
 * Sonar
 * Group: d17
 * Group Members: Aditya Kuntal Maniar (am3326), Deep Ketan Mistry (dm728), Tanishq Dwivedi(td43), Siddhesh Nitin Amrale (sa2567), Paritosh Paritosh (pp233)

 * Note: follow all instructions in this file
 * - 'ADC' means "add your code here"
 * - 'NOC' means don't change i.e. you should use as is
 * - Do not change any method signatures
 * - your own helper methods are OK
 */
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Topic;
import javax.jms.Destination;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.apache.activemq.ActiveMQConnectionFactory;
// add your own imports below
import java.util.Calendar;

public class Sonar1 {

  // add any private vars below 'pfile'
  // add your own methods *below* get_time

  private String pbroker, pqueue, puser, ppass; // properties
  private String pfile = "USS.properties"; // must be in current dir
  private Properties Uss = null;
  public int count;
  private static String shipName; //DM

  public static void main(String[] args) throws Exception {
    
    if (args.length < 1) {
      System.err.println(" Usage: java Sonar <shipname>  ## Consumer mode\n");
      System.exit(2);
    }
    
    shipName = args[0]; //DM
    String pfile = args[1]; //DM
    

    Sonar1 c = new Sonar1(args[1]);
    c.run();

  } // main
  
  public static void thread(Runnable runnable, boolean daemon) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
   }

  // load properties file
  public void loadprop(String filename) {
    // ADC
    //same as a2
    try(FileInputStream PropertiesFile = new FileInputStream(filename)){
    Properties Uss = new Properties(); //D

    Uss.load(PropertiesFile); //D


    pbroker = Uss.getProperty("broker"); //D
    pqueue = Uss.getProperty("queue"); //D
    puser = Uss.getProperty("username"); //D
    ppass = Uss.getProperty("password"); //D

    System.out.println("Broker: " + Uss.getProperty("broker")); //D
    System.out.println("Queue: " + Uss.getProperty("queue")); //D
    System.out.println("Username: " + Uss.getProperty("username")); //D
    System.out.println("Password: " + Uss.getProperty("password")); //D   
    
    } //D
    catch(IOException e){
      e.printStackTrace();
    }


  }

  // this is the only Ctor permitted
  public Sonar1(String a) {

    loadprop( pfile );

    // other init stuff: ADC
    

  } // ctor CX()

  // the actual worker, do not change the signature!
  int run() throws javax.jms.JMSException 
  {
  
    Connection cx=null; 
    Session sx=null;    // NOC
    ConnectionFactory cf=null;              // NOC
    // ADC additional vars here if needed
    MessageConsumer consumer = null;
    try {
       cf = new ActiveMQConnectionFactory(pbroker);
       cx = cf.createConnection(puser, ppass);
       
      System.out.println("*DRILL *DRILL *DRILL " + pfile + " " + shipName + " Sonar connected to: " + pbroker); //D
       
       sx = cx.createSession(false, sx.AUTO_ACKNOWLEDGE); // NOC
       // createSession should not change
             
       // make Destination, Consumer
       Destination destination = sx.createQueue(pqueue); // ADC, must use session.createXXXXXX

       // start consumer
       cx.start(); // D
       System.out.println("*DRILL *DRILL *DRILL " + pfile + " " + shipName + " at " + get_time() + ": READY "); //D       
       
       count = 0; //DM
       consumer = sx.createConsumer(destination);
       
       while (true) {        // NOC: while loop as is
       
       
         TextMessage textMessage = null;
         textMessage = (TextMessage)consumer.receive(70);//AM
         if(textMessage!=null){
         String text = textMessage.getText();//AM
         
          // ADC - loop body to get all messages
          // break gracefully when done
          
         if(text.equalsIgnoreCase("END")){ //DM       
          System.out.println(get_time() + ":" + " **TERMINATE DRILL  " ); //DM
          break; //DM
         }else{ //DM
           System.out.println(get_time() + " " + shipName + ": " + text); //DM
           count++;
           Thread.sleep(70);  
         }//D        
        } // while  
      }
      }
       // loop gracefully exited, print message
       // and fall thru, gracefully, to "finally"

     catch( Exception e) {
        e.printStackTrace();
    } finally {
        // ADC - to close resources etc
        sx.close(); //DM
        cx.close(); //DM
        consumer.close();     

    } // try {} ends here
        System.out.println("Total Messages received are: " + count);
     return count++; // add appropriate return here
  } // run() run - Consumer

  // ADC isValid - but do not change the signature
  //public boolean isValid(String message)
  //{
    // ADC -- add your code here!
  //}

   String get_time() {   
       String timeStamp = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss").format(Calendar.getInstance().getTime());
       return timeStamp;
    // ADC use SimpleDateFormat to obtain the desired timestamp
    // This method comes in handy above when printing timestamp/s
  }
  
   

} // class
