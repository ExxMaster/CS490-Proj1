all: compile

compile:
	javac RMIChatServer.java
	javac RMIChatClient.java
	javac RMIImplementation.java
	javac RMIInterface.java

create:
	rmic RMIImplementation
	rmiregistry &

server:
	java RMIChatServer
	
client:
	java RMIChatClient 127.0.0.1 55 45
	
clear:
	rm *.class
