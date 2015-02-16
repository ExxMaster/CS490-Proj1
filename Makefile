all: compile

compile:
	javac ChatClient.java
	javac SingleThreadedChatServer.java


multi:
	javac MultithreadedChatServer.java
	javac MChatClient.java
clean:
	rm *.class *.java~
	clear
