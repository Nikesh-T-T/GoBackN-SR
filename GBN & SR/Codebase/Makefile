JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = MySender.java MyReceiver.java

all: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
