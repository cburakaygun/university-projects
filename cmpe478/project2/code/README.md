CMPE478 - Parallel Processing

Assignment 2: Parallel Versions of Google Ranking Process Using MPI & Thrust

SPRING 2019

***

### INFO:

The programs were implemented using C++14 on Intel(R) Core(TM) i3-6100 CPU @ 3.70GHz (2 Cores, 4 Threads).

MPI and Thrust (OpenMP backend) programs are tested on this CPU. Here are the version info of some related software:

    g++           4.7.0
    open-mpi      2.1.1
    nvcc          9.1.85
    thrust        1.9

***

### HOW TO COMPILE

#### PART 1 (MPI)
For this part, you need to have `METIS` libraries installed on your system.

Compile *hw2_mpi.cpp* via this command:     `mpic++ -std=c++14 hw2_mpi.cpp -lmetis`


#### PART 2 (Thrust)
* For OpenMP Backend: 
  Compile *hw2_thrust.cu* via this command:

  `nvcc -std=c++14 -O2 hw2_thrust.cu -Xcompiler -fopenmp -DTHRUST_DEVICE_SYSTEM=THRUST_DEVICE_SYSTEM_OMP -lgomp -I <path to thrust library>`

* For CUDA Backend:
  Compile *hw2_thrust.cu* via this command:
  
  `nvcc hw2_thrust.cu -DTHRUST_DEVICE_SYSTEM=THRUST_DEVICE_SYSTEM_CUDA`


#### NOTES:
Before compilation, you might need to update `NODE_NUM` and `EDGE_NUM` variables on the program source files if the vertex number and/or edge number in Erdös Web Graph file you have is different.
Note that the numbers stated on https://web-graph.org/ might be wrong.

You can check the VERTEX number with this command:    `cat graph.txt | tr '\t' '\n' | sort | uniq | wc -l`

You can check the EDGE number with this command:      `cat graph.txt | wc -l`

***

### HOW TO RUN:

You need to provide the path of the Erdös Web Graph file (which can be downloaded from https://web-graph.org/) as a single argument. This project is tested on [this](https://web.archive.org/web/20160323033650if_/http://web-graph.org/downloads/graph.txt.bz2) version of the graph file.

#### PART 1 (MPI)
First, you need to execute provided Python3 helper script named `graph_to_metis.py` via this command:

`python3 graph_to_metis.py <path_of_graph_txt_file>`     (Example:   `python3 graph_to_metis.py graph.txt`)
        
This script will transform directed Erdös Web Graph into an undirected one and produce a text file named `metis_graph.txt` which is about **250MB**.
This file contains `xadj` and `adjncy` array arguments for `METIS PartGraphRecursive` C++ API.

After you run the helper Python3 script, run the MPI program as follows:

`mpirun -np <number of processors> ./a.out  <path_of_graph_txt_file>`     (Example:   `mpirun -np 4 ./a.out graph.txt`)


#### PART 2 (Thrust)
Run the executable:     `./a.out <path_of_graph_txt_file>`   (Example:   `./a.out graph.txt`)


While running, the programs will print some information on the terminal about the processes going on.
After each iteration, current difference will be printed. After the difference converges, the elapsed time for the ranking algorithm will be printed.
