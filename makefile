JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
        NetSim.java \
		InputReader.java \
		Host.java \
		Router.java \
		DuplexLink.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
	$(RM) *.log
