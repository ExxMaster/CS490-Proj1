all: compile

compile:
	javac RMIChatServer.java
	javac RMIChatClient.java
	javac RMIImplementation.java
	javac RMIInterface.java
	javac ClientInterface.java
	javac ClientImplementation.java
	rmic RMIImplementation ClientImplementation
	
	rmiregistry &
	
	java RMIChatServer

client:
	java RMIChatClient 127.0.0.1

clear:
	rm *.class
