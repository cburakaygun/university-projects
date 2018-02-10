/*
Student Name:       Cemal Burak Aygün
Student Number:     2014400072
Project Number:     3
Operating System:   Xubuntu (Ubuntu 14.04) (on VirtualBox)
Compile Status:     Compiles correctly.
Program Status:     Gives output correctly.
Notes:              I modified the function "top_sort_using_adj_list" written in ps8
                    and used it in this project.
*/

#include <iostream>
#include <vector>
#include <queue>
#include <iomanip>

using namespace std;


int main(int argc, char* argv[]){

    if (argc != 3) {

        cout << "Run the code with the following command: ./project3 [input_file] [output_file]" << endl;
        return 1;

    }

    int NUM_VERTEX = -1;     // Total number of vertices in the graph.
    int NUM_EDGE = -1;       // Total number of edges in the graph.

    freopen( argv[1] , "r" , stdin );

    cin >> NUM_VERTEX;
    cin >> NUM_EDGE;

    int inDegree[NUM_VERTEX];   // Stores the number of indegrees a vertex has. (index number in array = vertex id)
    for( int i = 0 ; i < NUM_VERTEX ; i++ ){
        inDegree[i] = 0;        // Initializes 'inDegree[]' to 0.
    }
    
    double time[NUM_VERTEX];    // Stores the maximum of all the possible sums of 'duration's of all vertices
                                // along the path from this (index number) vertex to its last successor.
    for( int i = 0 ; i < NUM_VERTEX ; i++ ){
        double duration;    // The time needed to complete the process (vertex).
        cin >> duration;
        time[i] = duration;     // At the beginning, initializes 'time[]' with 'duration'
    }

    vector< vector<int> > graph(NUM_VERTEX);    // Representation of the graph.
    int from, to;
    for( int i = 0 ; i < NUM_EDGE ; i++ ){

        cin >> from;
        cin >> to;
        graph[from].push_back(to);
        inDegree[to]++;

    }

    queue<int> zeroList;    // Stores the index (id) of vertices in 'graph' that does not have any indegrees.
    for( int i = 0 ; i < NUM_VERTEX ; i++ ){
        if( inDegree[i] == 0 ){
            zeroList.push(i);
        }
    }

    int topologicalOrder[NUM_VERTEX];   // Stores vertex ids in topological order.
    int cntr = 0;   // This is used to check whether the graph is cyclic or acyclic.
    while( !zeroList.empty() ){

        int vertex = zeroList.front();
        zeroList.pop();
        topologicalOrder[cntr] = vertex;

        cntr++;

        for( int i = 0 ; i < graph[vertex].size() ; i++ ){

            int currentVertex = graph[vertex][i];   // The successor of 'vertex'.
            inDegree[currentVertex]--;
            if( inDegree[currentVertex] == 0 ){
                zeroList.push(currentVertex);
            }

        }
        
    }

    freopen(argv[2] , "w" , stdout);

    if( cntr < NUM_VERTEX ){    // If the graph is cyclic
        cout << setprecision(6) << fixed << -1.0;
    }else{      // If the graph is acyclic

        double result = -1;     // The result that will be printed in output file.
        for( int i = NUM_VERTEX-1 ; i >= 0 ; i-- ){

            int vertex = topologicalOrder[i];
            if( graph[vertex].size() != 0 ){

                // The maximum of 'time's that the successors of 'vertex' have.
                double maxSucTime = time[ graph[vertex][0] ];
                for( int i = 1 ; i < graph[vertex].size() ; i++ ){

                    if( time[ graph[vertex][i] ] > maxSucTime ){
                        maxSucTime = time[ graph[vertex][i] ];
                    }

                }
                time[vertex] += maxSucTime;   // 'time' of a vertex = 'maxSucTime' + 'duration'.
                // Note that at the beginning 'time' was initialized with 'duration'.

            }

            if( time[vertex] > result ){
                result = time[vertex];
            }

        }

        cout << setprecision(6) << fixed << result;

    }

    return 0;

}