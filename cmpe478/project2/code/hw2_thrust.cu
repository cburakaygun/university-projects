#include <time.h>
#include <fstream>
#include <iostream>
#include <unordered_map>
#include <vector>

#include <thrust/copy.h>
#include <thrust/device_vector.h>
#include <thrust/host_vector.h>
#include <thrust/inner_product.h>
#include <thrust/iterator/permutation_iterator.h>
#include <thrust/reduce.h>
#include <thrust/transform.h>


#define NODE_NUM 1850065  // Number of nodes (vertices) of the graph.
#define EDGE_NUM 16741171 // Number of edges of the graph.

#define ALPHA 0.2
#define EPSILON 0.000001


using namespace std;


// Absolute value of the difference of the given arguments
struct differenceAbs_functor
{
    __host__ __device__
    double operator()(const double& x, const double& y) const {
        return abs(x - y);
    }
};



// Reads Erdös Web Graph file and creates related P matrix in CSR format.
void createCSRArrays(char *graphFilePath, vector<int> &rowBegin, vector<double> &values, vector<int> &columnIndices) {

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
        double value = 1.0 / outDegrees[nodeIndex];
        values[i] = value;
    }

    printf("DONE\n");  // CSR-format related arrays are constructed.

}


int main(int argc, char *argv[]) {

    vector<int> rowBegin(NODE_NUM+1); // Row indices array of the CSR format of P matrix.
    vector<double> values(EDGE_NUM); // Nonzero values of P matrix.
    vector<int> columnIndices(EDGE_NUM); // Column indices array of the CSR format of P matrix.

    createCSRArrays(argv[1], rowBegin, values, columnIndices);

    printf("\n");

    thrust::device_vector<double > values_D(values); // Copies values array from host to device

    thrust::device_vector<double> r1_D(NODE_NUM, 1); // r^(t)
    thrust::device_vector<double> r2_D(NODE_NUM, 0); // r^(t+1)
    thrust::device_vector<double> difference_D(NODE_NUM, 0);  // ith element = |r_i^(t+1) - r_i^(t)|

    int iterationCount = 0;

    auto clockStart = clock();

    while(true) {
        iterationCount++;

        // r2_D = P * r1_D
        for (int i=0; i<NODE_NUM; i++){
            int x = rowBegin[i];   // Begin of row-i
            int y = rowBegin[i+1]; // End of row-i

            auto permIter = thrust::make_permutation_iterator(r1_D.begin(), columnIndices.begin()+x);

            r2_D[i] = ALPHA * thrust::inner_product(values_D.begin()+x, values_D.begin()+y, permIter, 0.0) + (1-ALPHA);
        }

        // difference_D[i] = | r2_D[i] - r1_D[i] |
        thrust::transform(r2_D.begin(), r2_D.end(), r1_D.begin(), difference_D.begin(), differenceAbs_functor());

        double result = thrust::reduce(difference_D.begin(), difference_D.end());
        printf("Difference after %02d iterations: %f\n", iterationCount, result);

        if (result <= EPSILON) {
            break;
        } else {
            thrust::copy(r2_D.begin(), r2_D.end(), r1_D.begin());
        }
    }

    float elapsedTime = (float)(clock() - clockStart) / CLOCKS_PER_SEC;
    printf("\nTime Elapsed for Ranking Algorithm: %f sec\n", elapsedTime);

    return 0;
}
