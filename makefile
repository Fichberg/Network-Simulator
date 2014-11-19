JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
		Agent.java \
		AgentEnum.java \
		ApplicationLayer.java \
		DNSServer.java \
		DuplexLink.java \
		FTPClient.java \
		FTPServer.java \
		Host.java \
		HTTPClient.java \
		HTTPServer.java \
		InputReader.java \
		NetSim.java \
		Node.java \
		Packet.java \
		Router.java \
		RouterBuffer.java \
		Sniffer.java \
		TCP.java \
		TransportLayer.java \
		UDP.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
	$(RM) *.log
