CMPE478 - Parallel Processing

Assignment 1: A Parallel Version of Google Ranking Process Using OpenMP

SPRING 2019

***

This program was implemented and tested via g++ (version 7.3.0) and C++11 on Intel(R) Core(TM) i3-6100 CPU @ 3.70GHz (2 Cores, 4 Threads).

You need to provide the path of the Erdös Web Graph file (which can be downloaded from https://web-graph.org/) as a single argument.

**NOTE:**
You might need to update `NODE_NUM` variable on the 7th line of `main.cpp` if the vertex number in Erdös Web Graph file you have is different.
You can check the vertex number with this command:

`cat graph.txt | tr '\t' '\n' | sort | uniq | wc -l`

(The number stated on https://web-graph.org/ might be wrong. This project is tested on [this](https://web.archive.org/web/20160323033650if_/http://web-graph.org/downloads/graph.txt.bz2) version of the graph file.)

***

### HOW TO RUN:

Compile *main.cpp* via *OpenMP* flag:     `g++ -std=c++11 main.cpp -fopenmp`

Run the executable:     `./a.out <path_of_graph_txt_file>`     (Example:   `./a.out graph.txt`)


While running, the program will print some information on the terminal about the processes going on.
At the end, it will print the 5 highest ranked host IDs on the terminal and create a file named `results.csv` which contains test results.

A total of 9 tests will be done: <3 scheduling methods (static, dynamic, guided)> x <3 chunk sizes (1000, 3000, 5000)>
