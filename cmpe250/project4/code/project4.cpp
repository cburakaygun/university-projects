/*
Student Name:       Cemal Burak Aygün
Student Number:     2014400072
Project Number:     4
Operating System:   Xubuntu (Ubuntu 14.04) (on VirtualBox)
Compile Status:     Compiles correctly.
Program Status:     Gives output correctly.
Notes:

*/

#include <iostream>
#include <algorithm>
#include <vector>
#include <queue>

using namespace std;


/*
 * Representation of an edge in the graph (tree).
 */
struct Edge{

    long v1;
    long v2;
    long weight;

    Edge( long v1 , long v2 , long weight ){
        this->v1 = v1;
        this->v2 = v2;
        this->weight = weight;
    }

};


bool operator<(const Edge& e1, const Edge& e2) {
    return e1.weight < e2.weight;
}


/*
 * The queue in which the edges are sorted in decreasing order according to their 'weight'
 */
priority_queue<Edge> edges;


/*
 * Contains the information about equivalence classes of vertices. (for Disjoint Set operations)
 */
vector<long> equivalenceClass;


/*
 * Returns the root of 'vertex'
 */
long findRoot(long vertex){

    if( equivalenceClass[vertex] < 0 ){     // If 'vertex' is a root itself...
        return vertex;
    }else{      // if 'vertex' has a root...
        return findRoot( equivalenceClass[vertex] );
    }

}


int main(int argc, char* argv[]){

    if (argc != 3) {

        cout << "Run the code with the following command: ./project4 [input_file] [output_file]" << endl;
        return 1;

    }

    long NUM_VERTEX = 0 ;    // Total number of vertices in the graph (tree).
    long NUM_CUT = 0;        // Total number of vertices that the communication between will be cut.

    freopen( argv[1] , "r" , stdin );
    cin >> NUM_VERTEX;
    cin >> NUM_CUT;

    for( long i = 0 ; i < NUM_VERTEX-1 ; i++ ){

        long v1 , v2, weight;
        cin >> v1;      // One vertex in the graph (tree)
        cin >> v2;      // The vertex which is adjacent to 'v1'
        cin >> weight;  // The weight of the edge between 'v1' and 'v2'

        edges.push( Edge(v1 , v2 , weight) );

    }

    equivalenceClass.resize(NUM_VERTEX);
    for( long  i = 0 ; i < NUM_VERTEX ; i++ ){
        equivalenceClass[i] = -1;   // Initially, every vertex is a root
    }

    for( long  i = 0 ; i < NUM_CUT ; i++ ){

        int c;      // The id of the vertex whose communication with some other vertex will be cut.
        cin >> c;
        equivalenceClass[c] = -2;   // To distinguish vertex 'c' from other vertices.

    }

    long result = 0;    // The result that will be written in the output file.

    long counter = 0;   // A counter for the while loop below to stop.
    while( counter < NUM_CUT-1 ){   // To split N vertices from each other, we should "delete" N-1 edges in a tree.

        Edge e = edges.top();
        edges.pop();

        long root1 = findRoot(e.v1);
        long root2 = findRoot(e.v2);

        if( equivalenceClass[root1] == -2 ){
            if( equivalenceClass[root2] == -2 ){
                result += e.weight;
                counter++;
            }else{      // if equivalenceClass[root1] == -2 && equivalenceClass[root2] != -2
                equivalenceClass[root2] = -2;
            }
        }else{      // if equivalenceClass[root1] != -2
            if( equivalenceClass[root2] == -2 ){
                equivalenceClass[root1] = -2;
            }else{      // if equivalenceClass[root1] != -2 && equivalenceClass[root2] != -2
                equivalenceClass[root1] = root2;
            }
        }

    }

    freopen( argv[2] , "w" , stdout );
    cout << result;

    return 0;

}