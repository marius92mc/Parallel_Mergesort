#
# Makefile for compiling MergeSortInParallel.java
# 					     MergeSortTask.java
# 						 SortingThreadPool.java
# 					     SortTask.java
#

# define a makefile variable for the java compiler
#
JC = javac

# define a makefile variable for compilation flags
# the -g flag compiles with debugging information
#


# typing 'make' will invoke the first target entry in the makefile 
# (the default one in this case)
#
#default: MergeSortInParallel.class    \
#         MergeSortTask.class          \
#         SortingThreadPool.class      \
#         SortTask.class

# this target entry builds the IndexFiles class
# the IndexFiles.class file is dependent on the IndexFiles.java file
# and the rule associated with this entry gives the command to create it
#
default:
	@echo "Compiling .java..."
	@$(JC) *.java
#	@ls *.class

# To start over from scratch, type 'make clean'.  
# Removes all .class files, so that the next make rebuilds them
#
clean: 
	@echo "Cleanup class files..."
	@rm -rf *.class
	@echo "Done."
