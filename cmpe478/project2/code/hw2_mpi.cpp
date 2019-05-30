#include <chrono>
#include <fstream>
#include <iostream>
#include <unordered_map>
#include <vector>
#include <algorithm>
#include <metis.h>
#include <time.h>
#include <mpi.h>


#define NODE_NUM 1850065  // Number of nodes (vertices) of the graph.
#define EDGE_NUM 16741171 // Number of edges of the graph.

#define ALPHA 0.2
#define EPSILON 0.000001

#define METIS_GRAPH_PATH "./metis_graph.txt"

#define PARTIAL_P_TAG 1
#define PARTIAL_DIFF_TAG 2

using namespace std;


// Reads Erdös Web Graph file and creates related P matrix in CSR format.
void createCSRArrays(char *graphFilePath, int *rowBegin, double *values, int *columnIndices) {

    int i; // Loop variable
    string node1, node2; // Represents 2 adjacent nodes in the graph.

    int IN_NODE_NUM; // Number of nodes with incoming edges.

    vector<pair<string, string>> graphVector(EDGE_NUM); // Stores graph.txt file.
    unordered_map<string, int> nodeIndexMap;  // Maps node IDs to a row/column index of P matrix.

    // graphFilePath is the path of graph.txt file.
    ifstream graphFile(graphFilePath);

    printf("Reading %s\t\t\t\t\t\t\t... ", graphFilePath);
    fflush(stdout);

    int graphVector_i = 0;
    // Iterates over graph.txt file.
    while (graphFile >> node1 >> node2) { // There is a directed edge from node 1 to node 2.
        graphVector[graphVector_i++] = make_pair(node1, node2);

        if (nodeIndexMap.find(node2) == nodeIndexMap.end()) { // If node 2 is not in `nodeIndexMap` ...
            nodeIndexMap.emplace(node2, nodeIndexMap.size()); // ... inserts it with the next index.
        }
    }

    graphFile.close();
    printf("DONE\n");

    IN_NODE_NUM = nodeIndexMap.size(); // The WHILE-LOOP above inserts only the nodes with incoming edges to `nodeIndexMap`.

    printf("Constructing CSR-format related arrays\t\t\t\t\t... ");
    fflush(stdout);

    vector<int> outDegrees(NODE_NUM, 0); // Stores the out degree of each node.

    auto graphVectorItr = graphVector.begin();

    int indexOfNextRow = 0;  // Index of (next) row in values (and columnIndices) arrays.
    int rowBegin_i = 0;
    int columnIndices_i = 0;

    // First IN_NODE_NUM indices are assigned to the nodes with incoming edges.
    for (int nodeIndex = 0; nodeIndex < IN_NODE_NUM; nodeIndex++) {
        rowBegin[rowBegin_i++] = indexOfNextRow;

        while (graphVectorItr != graphVector.end()) {
            node1 = graphVectorItr->first;
            node2 = graphVectorItr->second;

            if (nodeIndexMap.find(node2)->second != nodeIndex) {
                break;  // If index of `node2` is not equal to `nodeIndex`, breaks the WHILE-LOOP.
            }

            if (nodeIndexMap.find(node1) == nodeIndexMap.end()) { // If node 1 is not in `nodeIndexMap` ...
                nodeIndexMap.emplace(node1, nodeIndexMap.size()); // ... inserts it with the next index.
            }

            int node1Index = nodeIndexMap.find(node1)->second;
            outDegrees[node1Index]++; // There is a directed edge from node 1 to node 2.
            columnIndices[columnIndices_i++] = node1Index;
            indexOfNextRow++;
            graphVectorItr++;
        }
    }

    // Inserts row indices for all-zero rows of P matrix.
    for (i = rowBegin_i; i < NODE_NUM + 1; i++) {
        rowBegin[i] = EDGE_NUM;
    }

    for (i = 0; i < EDGE_NUM; i++) {
        int nodeIndex = columnIndices[i];
        values[i] = 1.0 / outDegrees[nodeIndex];
    }

    printf("DONE\n");  // CSR-format related arrays are constructed.

}


// Reads metis_graph.txt file and creates `xadj` and `adjncy` arrays.
void readMetisArrays(idx_t *xadj, idx_t *adjncy) {

    ifstream metisGraphFile(METIS_GRAPH_PATH);

    printf("Reading %s\t\t\t\t\t\t... ", METIS_GRAPH_PATH);
    fflush(stdout);

    int x;

    for (int i = 0; i < NODE_NUM + 1; i++) {
        metisGraphFile >> x;
        xadj[i] = x;
    }

    for (int i = 0; i < 2 * EDGE_NUM; i++) {
        metisGraphFile >> x;
        adjncy[i] = x;
    }

    metisGraphFile.close();
    printf("DONE\n");  // Metis input arrays are read.

}


// Returns the partitioning of Erdös Web Graph (undirected version).
int getPartitioning(int partitionNumber, idx_t *part) {
    // Undirected Graph Matrix for METIS
    idx_t *xadj = new idx_t[NODE_NUM + 1];
    idx_t *adjncy = new idx_t[2 * EDGE_NUM];
    readMetisArrays(xadj, adjncy);

    idx_t nvtxs = NODE_NUM; // Number of vertices
    idx_t ncon = 1; // Number of balancing constraints
    idx_t nparts = partitionNumber; // Number of partitions

    idx_t objval; // METIS return value

    return METIS_PartGraphRecursive(&nvtxs, &ncon, xadj, adjncy, NULL, NULL, NULL, &nparts, NULL, NULL, NULL, &objval, part);
}


// Fills the partialP which contains some entities to be sent to other processors.
void fillPartialP(double *partialP, vector<int> &nodes, vector<int> &partialRowBegin, vector<double> &partialValues, vector<int> &partialColIndices) {
    int ind = 0;

    partialP[ind++] = nodes.size();

    for (double elem: nodes) {
        partialP[ind++] = elem;
    }

    for (double elem: partialRowBegin) {
        partialP[ind++] = elem;
    }

    partialP[ind++] = partialValues.size();

    for (double elem: partialValues) {
        partialP[ind++] = elem;
    }

    for (double elem: partialColIndices) {
        partialP[ind++] = elem;
    }
}


int main(int argc, char *argv[]) {

    MPI_Init(&argc, &argv);

    int numberOfNodes; // Number of nodes assigned to one processor

    // Only the rows of P matrix that corresponds to the nodes assigned to one processor is given to that processor.
    // This variable is in the form:
    // <number of nodes>|<nodes>|<elements of partial rowBegin array>|<size of partial values array>|<partial values array>|<partial colIndices array>
    double *partialP;
    int nodesStart = 1; // Starting index of the nodes (1st element is the number of nodes)
    int partialRowBeginStart = -1; // Starting index of partial rowBegin in partialP (to be set)
    int partialValuesStart = -1; // Starting index of partial values in partialP (to be set)
    int partialColIndicesStart = -1; // Starting index of partial coldIndices in partialP (to be set)

    double partialSumOfDifference;
    double sumOfDifference;


    int world_rank, world_size;
    MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);
    MPI_Comm_size(MPI_COMM_WORLD, &world_size);


    // Only valid on processor-0
    int *rowBegin; // Row indices array of the CSR format of P matrix.
    double *values; // Nonzero values of P matrix.
    int *columnIndices; // Column indices array of the CSR format of P matrix.

    // Maps processor ranks to vectors of nodes assigned to that processor
    // Only valid on processor-0
    unordered_map<int, vector<int>> rankToNodes;

    double *rt1 = new double[NODE_NUM]; // r^(t) vector
    double *partial_rt2; // Partial r^(t+1) vector

    if (world_rank == 0) {
        rowBegin = new int[NODE_NUM + 1];
        values = new double[EDGE_NUM];
        columnIndices = new int[EDGE_NUM];

        createCSRArrays(argv[1], rowBegin, values, columnIndices);

        idx_t *part = new idx_t[NODE_NUM]; // Resulting partitioning of Metis

        int result = getPartitioning(world_size, part);

        if (result != METIS_OK) {
            printf("[ERROR] METIS_PartGraphRecursive failed with return code %d\n", result);
            MPI_Abort(MPI_COMM_WORLD, 1);
        }

        printf("\n");

        // Initialize `rankToNodes`
        for (int i = 0; i < world_size; i++) {
            rankToNodes.emplace(i, vector<int>());
        }

        // Fill `rankToNodes`
        for (int i = 0; i < NODE_NUM; i++) {
            rankToNodes.find(part[i])->second.push_back(i);
        }
    }

    MPI_Barrier(MPI_COMM_WORLD); // All processors wait for processor-0 to finish setting up

    if (world_rank == 0) {

        // Initialize r^(t) vector to 1
        for (int i = 0; i < NODE_NUM; i++) {
            rt1[i] = 1;
        }

        vector<int> partialRowBegin;
        vector<double> partialValues;
        vector<int> partialColIndices;

        // Processor-0 distributes partial P matrices to other processors
        for (int i = 0; i < world_size; i++) {
            vector<int> nodes = rankToNodes.find(i)->second; // Nodes assigned to processor-i

            partialRowBegin.clear();
            partialValues.clear();
            partialColIndices.clear();

            for (int node: nodes) {
                partialRowBegin.push_back(partialValues.size());
                for (int colInd = rowBegin[node]; colInd < rowBegin[node + 1]; colInd++) {
                    partialValues.push_back(values[colInd]);
                    partialColIndices.push_back(columnIndices[colInd]);
                }
            }
            partialRowBegin.push_back(partialValues.size());

            // <number of nodes> + <nodes> + <elements of partial rowBegin> +
            // <size of partial values/colIndices> + <elements of partial values> + <elements of partial colIndices>
            int totalSize = 1 + nodes.size() + partialRowBegin.size() + 1 +  partialValues.size() + partialColIndices.size();

            if (i == 0) {
                partialP = new double[totalSize];
                fillPartialP(partialP, nodes, partialRowBegin, partialValues, partialColIndices);

                numberOfNodes = nodes.size();
                partialRowBeginStart = nodesStart + numberOfNodes;
                partialValuesStart = partialRowBeginStart + (numberOfNodes+1) + 1;
                partialColIndicesStart = partialValuesStart + partialValues.size();
                partial_rt2 = new double[numberOfNodes];
            } else {
                double *partP = new double[totalSize];
                fillPartialP(partP, nodes, partialRowBegin, partialValues, partialColIndices);

                MPI_Send(partP, totalSize, MPI_DOUBLE, i, PARTIAL_P_TAG, MPI_COMM_WORLD);
            }
        }

        delete[] rowBegin; delete[] values; delete[] columnIndices;

    } else {

        MPI_Status status;
        int messageSize; // Length of partial P coming from processor 0

        MPI_Probe(0, PARTIAL_P_TAG, MPI_COMM_WORLD, &status);
        MPI_Get_count(&status, MPI_DOUBLE, &messageSize);

        partialP = new double[messageSize];

        MPI_Recv(partialP, messageSize, MPI_DOUBLE, 0, PARTIAL_P_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);

        numberOfNodes = partialP[0];
        partialRowBeginStart = nodesStart + numberOfNodes;
        partialValuesStart = partialRowBeginStart + (numberOfNodes+1) + 1;
        int partialValuesSize = partialP[partialValuesStart - 1];
        partialColIndicesStart = partialValuesStart + partialValuesSize;

        partial_rt2 = new double[numberOfNodes];

    }

    MPI_Barrier(MPI_COMM_WORLD); // Wait for all processors to receive their partial P matrix.

    int iterationCount = 0; // Only valid on processor-0

    double beginTime; // Only valid on processor-0
    if (world_rank == 0) {
        beginTime = MPI_Wtime();
    }

    while (true) {
        MPI_Bcast(rt1, NODE_NUM, MPI_DOUBLE, 0, MPI_COMM_WORLD);

        partialSumOfDifference = 0;

        // Each processor calculates its partial r^(t+1) vector
        for (int i = 0; i < numberOfNodes; i++){
            partial_rt2[i] = 0;

            // r^(t+1) = P * r^t
            for (int columnKey = partialP[partialRowBeginStart + i]; columnKey < partialP[partialRowBeginStart + i + 1]; columnKey++) {
                int j = partialP[partialColIndicesStart + columnKey];
                partial_rt2[i] += partialP[partialValuesStart + columnKey] * rt1[j];
            }

            partial_rt2[i] = ALPHA * partial_rt2[i] + (1-ALPHA); // r^(t+1) = ALPHA * P * r^t + (1-ALPHA)
            partialSumOfDifference += abs(partial_rt2[i] - rt1[(int)partialP[nodesStart + i]]);
        }

        MPI_Allreduce(&partialSumOfDifference, &sumOfDifference, 1, MPI_DOUBLE, MPI_SUM, MPI_COMM_WORLD);

        if (world_rank == 0) {
            iterationCount++;
            printf("Difference after %02d iterations: %f\n", iterationCount, sumOfDifference);
        }

        if (sumOfDifference <= EPSILON) {
            break;
        }

        if (world_rank == 0) {
            // Processor-0 collects partial r^(t+1) vectors from all processors into r^(t) vector
            for (int i = 0; i < world_size; i++) {
                vector<int> nodes = rankToNodes.find(i)->second; // Nodes assigned to processor-i
                int nodeNum = nodes.size();

                double *part_rt2; // Partial r^(t+1) vector that is calculated by processor-i
                if (i == 0) {
                    part_rt2 = partial_rt2;
                } else {
                    part_rt2 = new double[nodeNum];
                }

                if (i != 0) {
                    MPI_Recv(part_rt2, nodeNum, MPI_DOUBLE, i, PARTIAL_DIFF_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
                }

                for (int j = 0; j < nodeNum; j++) {
                    rt1[nodes[j]] = part_rt2[j];
                }
            }
        } else {
            MPI_Send(partial_rt2, numberOfNodes, MPI_DOUBLE, 0, PARTIAL_DIFF_TAG, MPI_COMM_WORLD);
        }
    }

    if (world_rank == 0) {
        double endTime = MPI_Wtime();
        printf("\nTime Elapsed for Ranking Algorithm: %f sec\n", endTime - beginTime);
    }

    MPI_Finalize();

}
