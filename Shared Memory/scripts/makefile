JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	SortingExternal.java \
        MergeSortThread.java \
        MergeSortFourThreads.java \
        MergeSortEightThreads.java \

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
