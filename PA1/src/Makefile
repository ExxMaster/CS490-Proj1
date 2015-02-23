all: compile

compile:
	javac RMIChatServer.java
	javac RMIChatClient.java
	javac RMIImplementation.java
	javac RMIInterface.java
	javac ClientInterface.java
	javac ClientImplementation.java
	rmic RMIImplementation ClientImplementation

client:
	java RMIChatClient 127.0.0.1

server:
	java RMIChatServer

clean:
	rm *.class
