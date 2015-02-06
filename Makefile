all: compile

compile:
	javac ChatClient.java
	javac SingleThreadedChatServer.java

clean:
	rm *.class
	clear
