JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
		Agent.java \
		AgentEnum.java \
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
		Router.java \
		Sniffer.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
	$(RM) *.log
