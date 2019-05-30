#include <iostream>
#include <unordered_map>
#include <vector>
#include <omp.h>
#include <fstream>

#define NODE_NUM 1850065 // Number of nodes (vertices) of the graph.

#define ALPHA 0.2
#define EPSILON 0.000001

#define MIN_THREAD_NUMS 1 // Minimum number of threads to test.
#define MAX_THREAD_NUMS 8 // Maximum number of threads to test.

#define CSV_FILE_NAME "results.csv"

using namespace std;

int main(int argc, char *argv[]) {

    int i; // Loop variable
    string node1, node2; // Represents 2 adjacent nodes in the graph.

    int IN_NODE_NUM; // Number of nodes with incoming edges.

    vector<pair<string, string>> graphVector; // Stores graph.txt file.
    unordered_map<string, int> nodeIndexMap;  // Maps node IDs to a row/column index of P matrix.

    freopen(argv[1], "r", stdin); // argv[1] is the path of graph.txt file.
    printf("Parsing %s\t... ", argv[1]);

    // Iterates over graph.txt file.
    while (cin >> node1 >> node2) { // There is a directed edge from node 1 to node 2.
        graphVector.emplace_back(node1, node2);

        if (nodeIndexMap.find(node2) == nodeIndexMap.end()) { // If node 2 is not in `nodeIndexMap` ...
            nodeIndexMap.emplace(node2, nodeIndexMap.size()); // ... inserts it with the next index.
        }
    }

    fclose(stdin);

    IN_NODE_NUM = nodeIndexMap.size(); // The WHILE-LOOP above inserts only the nodes with incoming edges to `nodeIndexMap`.

    printf("DONE\nConstructing CSR-format related arrays\t... ");

    vector<int > rowBegin; // Row indices array of the CSR format of P matrix.
    vector<double> values; // Nonzero values of P matrix.
    vector<int> columnIndices; // Column indices array of the CSR format of P matrix.

    int outDegrees[NODE_NUM]; // Stores the out degree of each node.
    for (i=0; i<NODE_NUM; i++) {
        outDegrees[i] = 0;
    }

    auto graphVectorItr = graphVector.begin();

    // First IN_NODE_NUM indices are assigned to the nodes with incoming edges.
    for (int nodeIndex=0; nodeIndex<IN_NODE_NUM; nodeIndex++) {
        vector<int> colIndices; // Column indices of the nonzero values of the current row (nodeIndex) of P matrix.

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
            colIndices.push_back(node1Index);

            graphVectorItr++;
        }

        rowBegin.push_back(columnIndices.size());
        columnIndices.insert(columnIndices.end(), colIndices.begin(), colIndices.end());

    }

    // Inserts row indices for all-zero rows of P matrix.
    for (int k=rowBegin.size(); k<NODE_NUM+1; k++) {
        rowBegin.push_back(columnIndices.size());
    }

    for (i=0; i<columnIndices.size(); i++) {
        int nodeIndex = columnIndices[i];
        double value = 1.0 / outDegrees[nodeIndex];
        values.push_back(value);
    }

    printf("DONE\n");  // CSR-format related arrays are constructed.

    double *rt1 = new double[NODE_NUM]; // r^t vector
    double *rt2 = new double[NODE_NUM]; // r^(t+1) vector
    double *tmp; // A dummy pointer used to swap `rt1` with `rt2`.

    int iterationCount = 0;

    int columnKey, j; // Used for matrix-vector multiplication.

    int chunkSizes[3] = {1000, 3000, 5000};

    pair<omp_sched_t, string> ompScheduleTypes[3] = {
            make_pair(omp_sched_static, "STATIC"),
            make_pair(omp_sched_dynamic, "DYNAMIC"),
            make_pair(omp_sched_guided, "GUIDED")
    };

    int testNo = 0;

    ofstream csvFile;
    csvFile.open(CSV_FILE_NAME);
    csvFile << "Test No., Scheduling Method, Chunk Size, No. of Iterations";
    for (i=MIN_THREAD_NUMS; i<=MAX_THREAD_NUMS; i++) {
        csvFile << "," << i << " Thread(s)";
    }
    csvFile << "\n";

    printf("\nStarting Tests:\n");

    for (auto &ompScheduleType : ompScheduleTypes) {
        for (int chunkSize : chunkSizes) {
            testNo++;

            string schedulingMethod = ompScheduleType.second;
            omp_set_schedule(ompScheduleType.first, chunkSize);

            printf("\t[TEST %i/9] Scheduling Method: %s\tChunk Size: %i\t... ", testNo, schedulingMethod.c_str(), chunkSize);
            csvFile << testNo << "," << schedulingMethod << "," << chunkSize;

            for(int numThreads=MIN_THREAD_NUMS; numThreads<=MAX_THREAD_NUMS; numThreads++) {
                // Initializes r^t vector.
                for (i=0; i<NODE_NUM; i++) {
                    rt1[i] = 1;
                }

                iterationCount = 0;

                double beginTime = omp_get_wtime();

                while (true) {
                    iterationCount++;

                    double difference = 0;

                    omp_set_num_threads(numThreads);

                    #pragma omp parallel \
                    shared(rt1, rt2, rowBegin, columnIndices, values) \
                    private(i, j, columnKey)
                    {
                        // Matrix-vector multiplication.
                        #pragma omp for schedule(runtime) reduction(+: difference)
                        for (i=0; i<NODE_NUM; i++){
                            rt2[i] = 0;

                            // r^(t+1) = P * r^t
                            for (columnKey=rowBegin[i]; columnKey<rowBegin[i+1]; columnKey++) {
                                j = columnIndices[columnKey];
                                rt2[i] += values[columnKey] * rt1[j];
                            }

                            rt2[i] = ALPHA * rt2[i] + (1-ALPHA); // r^(t+1) = ALPHA * P * r^t + (1-ALPHA)
                            difference += abs(rt2[i] - rt1[i]);
                        }
                    }

                    if (difference <= EPSILON) {
                        break;
                    } else {
                        tmp = rt2;
                        rt2 = rt1;
                        rt1 = tmp;
                    }

                }

                double endTime = omp_get_wtime();

                if (numThreads == 1) {
                    csvFile << "," << iterationCount;
                }

                csvFile << "," << endTime-beginTime;
            }

            printf("DONE\n");
            csvFile << "\n";

        }

    }

    csvFile.close();

    printf("\n\nTest results were saved in %s.\n", CSV_FILE_NAME);
    printf("\n5 Most Highest Ranked Hosts (IDs):\n");

    // Finds the top 5 highest ranked hosts.
    for(i=0; i<5; i++) {
        int nodeIndex = -1;
        double rank = 0;

        // Finds the highest rank with the corresponding host (node index).
        for(int j=0; j<NODE_NUM; j++) {
            if (rt2[j] > rank) {
                nodeIndex = j;
                rank = rt2[j];
            }
        }

        rt2[nodeIndex] = -1; // Eliminates this host from the next search.

        unordered_map<string, int>::iterator itr;

        // Finds the name (ID) of the host from its index.
        for (itr=nodeIndexMap.begin(); itr != nodeIndexMap.end(); itr++) {
            if (itr->second == nodeIndex) {
                cout << i+1 << ": " << itr->first << endl;
                break;
            }
        }
    }

    return 0;
}
