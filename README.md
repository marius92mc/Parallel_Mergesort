# Parallel Mergesort

##### Compile
    $ make

##### Run
    $ java MergeSortInParallel flags
    Where flags could be n,    where n represents the number of elements
                         -show, to show the elements 
                         -threads number_of_threads, where n represents the number of threads to spawn 


##### Clean
    $ make clean

<br /> <br /> <br />
### Indian grammar, derivation of a given word

##### Compiling indian grammar file
    $ g++ -std=c++0x -pthread -o indian_grammar indian_grammar_derivation_of_string.cpp

##### Run
    $ ./indian_grammar 

##### Example of input file 
grammar.in <br /> 
4 <br />
S AA <br />
A aA <br />
A bA <br />
A e <br />
<br />
aAaA 

