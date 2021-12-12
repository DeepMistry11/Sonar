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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
// add your own imports below
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;
import java.util.regex.Pattern;


public class SonarB {

  // add any private vars below 'pfile'
  // add your own methods *below* get_time

  private String pbroker, pqueue, puser, ppass; // properties
  private String pfile = "USS.properties"; // must be in current dir
  private Properties Uss = null;
  public int count = 0;
  private static String shipName;
  private boolean authhh = false;
  private boolean isFormatted = false;
  private static String messageProp;
  private int authCount = 0;
  private int unAuthCount = 0;
  private int incomingMsgCount = 0;
  private String completeMessage = null;

  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.err.println(" Usage: java Sonar <shipname>  ## Consumer mode\n");
      System.exit(2);
    }

    shipName = args[0];
    String pfile = args[1];

    SonarB c = new SonarB(args[1]);
    c.run(); //Ask if we can do this or not -Deep
  } // main

  // load properties file
  public void loadprop(String filename) {
    // ADC
    //same as a2
    try (FileInputStream PropertiesFile = new FileInputStream(filename)) {
      Properties Uss = new Properties(); //D

      Uss.load(PropertiesFile); //D

      pbroker = Uss.getProperty("broker"); //D
      pqueue = Uss.getProperty("queue"); //D
      puser = Uss.getProperty("username"); //D
      ppass = Uss.getProperty("password"); //D
    } catch (IOException e) { //D
      e.printStackTrace();
    }
  }

  // this is the only Ctor permitted
  public SonarB(String a) {
    loadprop(pfile);
    // other init stuff: ADC

  } // ctor CX()

  // the actual worker, do not change the signature!
  int run() throws javax.jms.JMSException { //want to ask if we can pass some arguments here or not
    //String ship = args[0], propsF = args[1];
    Connection cx = null;
    Session sx = null; // NOC
    ConnectionFactory cf = null; // NOC
    
    Connection cx1 = null;
    Session sx1 = null;
    ConnectionFactory cf1 = null;
    // ADC additional vars here if needed

    try {
      cf = new ActiveMQConnectionFactory(pbroker);
      cx = cf.createConnection(puser, ppass);


      //cf1 = new ActiveMQConnectionFactory(pbroker);
      //cx1 = cf.createConnection(puser, ppass);
      
      System.out.println(
        "*DRILL *DRILL *DRILL " +
        pfile +
        " " +
        shipName +
        " Sonar connected to: " +
        pbroker
      ); //D

      sx = cx.createSession(false, sx.AUTO_ACKNOWLEDGE); // NOC
      //sx1 = cx1.createSession(false, sx1.AUTO_ACKNOWLEDGE);
      // createSession should not change

      // make Destination, Consumer
      Destination destination = sx.createQueue(pqueue); // ADC, must use session.createXXXXXX
      //Destination topic = sx1.createTopic("TSONAR");
      //MessageConsumer consumer = sx.createConsumer(destination); // ADC, must use session.createConsumer

      // start consumer
      cx.start(); // D
      //cx1.start();
      

      // if statement from line 108 can come here.

      //Message message = consumer.receive();
      System.out.println(
        "*DRILL *DRILL *DRILL " +
        pfile +
        " " +
        shipName +
        " at " +
        get_time() +
        ": READY "
      ); //D

      //int count = 0; //D
      String Boat = "boat";
      MessageConsumer consumer = sx.createConsumer(
        destination,
        "boat = '" + shipName + "'"
      );
      
      //MessageConsumer consumer1 = sx.createConsumer(topic);

      while (true) { // NOC: while loop as is
        TextMessage textMessage = null;
        textMessage = (TextMessage) consumer.receive(200); //AM

        if (textMessage != null) {
          String text = textMessage.getText(); //D
          completeMessage = text;
          messageProp = textMessage.getStringProperty("signature");
          String boatName = textMessage.getStringProperty("boat");
          
          boolean con = boatName.equals(shipName);

          boolean validation = isValid(text);
          String temp=text.toUpperCase();

          if (temp.endsWith("END")) { 
            //System.exit(0);
            break;
          } else if (con == true) {
            if (validation) { 
              authhh = true;
              System.out.println(
                get_time() +
                " " +
                shipName +
                ": " + text +
                " (AUTH-OK, FMT-OK)"
              ); //DM
              Thread.sleep(70);
              count++;
            } else {
              System.out.println(
                get_time() +
                " " +
                shipName +
                ": " + text +
                " (AUTH-BAD, FMT-BAD)"
              );
            } //D
          }
          // ADC - loop body to get all messages
          // break gracefully when done
        } // if !null
      } // while
    } catch (Exception e) { // and fall thru, gracefully, to "finally" // loop gracefully exited, print message
      e.printStackTrace();
    } finally {
      // ADC - to close resources etc
      if (sx != null) {
        //sx1.close();
        sx.close();
        //consumer1.close();
        System.out.println(
          get_time() +
          ":" +
          " **TERMINATE DRILL" +
          "( " +
          (authCount) +
          " GOOD messages and " +
          unAuthCount +
          " Bad Messages " +
          "received" +
          " )"
        ); //D
      }
      if (cx != null) cx.close();
    } // try {} ends here

    return count; // add appropriate return here
  } // run() run - Consumer

  // ADC isValid - but do not change the signature
  public boolean isValid(String message) {
    //String description = "\\w{3}\\ \\w{5}\\-\\w{6}\\-\\w{7}\\-\\w{6}\\/\\^(0[1-9]|1[0-5])$\\";
    String description = "EAM DRILL-TARGET-PACKAGE-\\w{6}\\/\\d\\ \\w{0,20}";  
    //breakString();
    boolean valid = false;
    Pattern pattern = Pattern.compile(description);
    Matcher matcher = pattern.matcher(message);    
    if(matcher.matches()){
      authCount++;
        // ADC -- add your code here!
      Auth seqKey = new Auth("AABECZT");
      String messageEnc = message;
      String signatureEnc = seqKey.sign(messageEnc);
      
      boolean yes = messageProp.equals(signatureEnc);
      valid = yes;      
      return yes;
    }else{
      unAuthCount++;
      valid = false;
      return valid;
    }   
    //return valid;
  }

  private Mac auth;

  public void Auth(String var1) {
    Object var2 = null;
    SecretKeySpec var3 = null;

    try {
      byte[] var6 = MessageDigest
        .getInstance("SHA-1")
        .digest(var1.getBytes("UTF-8"));
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
  
 // public void breakString(){      
   //  String REGEX = "([-/ ])";
     //String INPUT = completeMessage;
     
    // Pattern pattern = Pattern.compile(REGEX);
     // String[] result = pattern.split(INPUT);
      //for (String data: result) {
          //System.out.println(data);
       //   incomingDescription = result[4];
       //   incomingMessage = result[6];
      //}      
      //System.out.println("--------------------------> " + incomingDescription + " " + incomingMessage);
  //}


  String get_time() {
    String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
    .format(Calendar.getInstance().getTime());
    return timeStamp;
    // ADC use SimpleDateFormat to obtain the desired timestamp
    // This method comes in handy above when printing timestamp/s
  }

  public static void thread(Runnable runnable, boolean daemon) {
    Thread brokerThread = new Thread(runnable);
    brokerThread.setDaemon(daemon);
    brokerThread.start();
  }
} // class
