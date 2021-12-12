
JAVAC=/afs/cad/linux/java8/bin/javac
JAVA=/afs/cad/linux/java8/bin/java
JFLAGS=-cp ../amq/activemq-all-5.10.0.jar
BRK=NOTSPECIFIED
MSG=Hello

.SUFFIXES: .java .class
.java.class:
	$(JAVAC) $(JFLAGS) $*.java

default:  TestAMQ.class

prod:	default
	echo "Run Producer"
	$(JAVA) $(JFLAGS):. TestAMQ P $(BRK) usr using QSONAR $(MSG)

cons:	default
	echo "Run Consumer"
	$(JAVA) $(JFLAGS):. TestAMQ C $(BRK) usr using QSONAR 100
