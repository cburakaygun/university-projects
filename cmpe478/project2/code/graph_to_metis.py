"""
This is a helper script for `hw2_mpi.cpp` C++ program.
It reads the Erdös Web Graph file (graph.txt, which contains a directed graph) given as an argument
and produces `xadj` and `adjncy` arguments for `METIS PartGraphRecursive` C++ API
by making the graph undirected.

One file (named 'metis_graph.txt') is created in the current directory.
This file contains an array element at each line.
First NODE_NUM+1 lines corresponds to `xadj` and the next 2*EDGE_NUM corresponds to `adjncy`.
"""

import sys


NODE_NUM = 1850065  # Number of nodes (vertices) of the graph.
EDGE_NUM = 16741171  # Number of edges of the graph.

OUTPUT_FILE_PATH = './metis_graph.txt'


if len(sys.argv) < 2:
    print("[ERROR]\tYou need to provide the Erdös Web Graph file path (graph.txt) as an argument")
    sys.exit(1)

graph_file_path = sys.argv[1]

graph = [0 for _ in range(EDGE_NUM)]  # Stores the graph.txt file

node_index_dict = {}  # Maps nodes (vertices) to an index

gi = 0
with open(graph_file_path) as f:
    for line in f:
        node1, node2 = line.split()

        graph[gi] = (node1, node2)
        gi += 1

        if node2 not in node_index_dict:  # At first, only the nodes with incoming edges are assigned an index
            node_index_dict[node2] = len(node_index_dict)

print(f"\t> {graph_file_path} read.")


# Adjacency List, maps node indices to a set of node indices
adj_list = {i: set() for i in range(NODE_NUM)}

for (node1, node2) in graph:
    if node1 == node2:
        continue
    
    if node1 not in node_index_dict:  # Then, the nodes with only outgoing edges are assigned an index
        node_index_dict[node1] = len(node_index_dict)

    node1_index = node_index_dict[node1]
    node2_index = node_index_dict[node2]
    adj_list[node1_index].add(node2_index)
    adj_list[node2_index].add(node1_index)

print("\t> Indices assigned to nodes.")

# `xadj` argument of `METIS PartGraphRecursive` C++ API
xadj = [2 * EDGE_NUM for _ in range(NODE_NUM + 1)]

# `adjncy` argument of `METIS PartGraphRecursive` C++ API
adjncy = [0 for _ in range(2 * EDGE_NUM)]

next_index = 0  # Next available index of `adjncy`
for i in range(NODE_NUM):  # For each node
    xadj[i] = next_index
    
    i_nodes = sorted(adj_list[i])  # Neighbor nodes of node-i
    for (j, node) in enumerate(i_nodes):
        adjncy[next_index + j] = node
    
    next_index += len(i_nodes)

print("\t> METIS input arrays created.")


with open(OUTPUT_FILE_PATH, 'w') as f:
    for elem in xadj:
        f.write(f"{elem}\n")
    
    for elem in adjncy:
        f.write(f"{elem}\n")

print(f"\t> {OUTPUT_FILE_PATH} created.")
