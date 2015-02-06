all:
	javac MatchingServer.java
	javac ChatClient.java
	javac SingleThreadedChatrServer.java

clean:
	rm *.o *.class
	clear
