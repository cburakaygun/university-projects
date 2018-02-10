/*
Student Name:       Cemal Burak Aygün
Student Number:     2014400072
Project Number:     5
Operating System:   Xubuntu (Ubuntu 14.04) (on VirtualBox)
Compile Status:     Compiles correctly.
Program Status:     Gives output correctly.
Notes:

*/

#include <iostream>
#include <vector>
#include <limits.h>

using namespace std;


struct Node{

    long id;    // ID number of the vertex in 'graph'
    long dist;  // The distance of the path between 'SOURCE' and 'DESTINATION' passing through the vertex with ID number 'id'
    Node* prev = nullptr;
    Node* next = nullptr;

    Node( long id , long dist , Node* prev , Node* next ){

        this->id = id;
        this->dist = dist;
        this->prev = prev;
        this->next = next;

    }

};


/*
 * Representation of a priority queue of 'Node's.
 * 'Nodes' are sorted in non-decreasing order according to their 'dist'.
 */
struct NodeList{

    Node* head;

    NodeList(){
        head = nullptr;
    }


    /*
     * Creates a 'Node' with the parameters and inserts it to the right place.
     *
     * id:      ID number of the vertex in 'graph'
     * dist:    The distance of the path between 'SOURCE' and 'DESTINATION' passing through the vertex with ID number 'id'
     *          ( = Real distance from 'SOURCE' to vertex with 'id' + the heuristic distance from vertex with 'id' to 'DESTINATION' )
     */
    void insert( long id , long dist ){

        if( head == nullptr ){
            head = new Node( id, dist , nullptr , nullptr);
        }else{

            Node* current = head;
            while( current->next != nullptr && current->dist < dist ){
                current = current->next;
            }

            if( current->dist < dist ){

                current->next = new Node( id , dist , current , current->next );
                if( current->next->next != nullptr ){
                    current->next->next->prev = current->next;
                }

            }else{

                current->prev = new Node( id , dist , current->prev , current );
                if( current->prev->prev != nullptr ){
                    current->prev->prev->next = current->prev;
                }else{
                    head = current->prev;
                }

            }

        }

    }


    /*
     * Deletes the 'head' ( the node with minimum 'dist' ) and
     * returns the deleted 'Node'.
     */
    Node& deleteMin(){

        Node* current = head;
        head = head->next;

        if( head != nullptr ){

            head->prev = nullptr;
            current->next = nullptr;

        }

        return *current;

    }


    /*
     * Updates the 'dist' of 'Node' with ID number 'id' and
     * replaces the updated 'Node' according to 'dist'.
     *
     * id   :   ID number of 'Node' which to be updated.
     * dist :   New 'dist' value of 'Node' with id number 'id'
     */
    void update( long id , long dist ){

        Node* n1 = head;    // The pointer that points to the 'Node' before which the updated 'Node' will be placed.
        while( n1->dist < dist ){
            n1 = n1->next;
        }

        Node* n2 = n1;      // The pointer that points to the 'Node' which will be updated.
        while( n2->id != id ){
            n2 = n2->next;
        }

        n2->dist = dist;

        if( n1 == n2 ){
            return;
        }

        n2->prev->next = n2->next;
        if( n2->next != nullptr ){
            n2->next->prev = n2->prev;
        }

        n2->prev = n1->prev;
        if( n1->prev != nullptr ){
            n1->prev->next = n2;
        }

        n1->prev = n2;

        n2->next = n1;

        if( n1 == head){
            head = n1->prev;
        }

    }


    /*
     * Returns whether 'NodeList' is empty or not.
     */
    bool isEmpty(){
        return head == nullptr;
    }


};


int main(int argc, char* argv[]) {


    if (argc != 3) {

        cout << "Run the code with the following command: ./project5 [input_file] [output_file]" << endl;
        return 1;

    }


    FILE *fp = fopen(argv[1] , "r");


    long NUM_VERTEX , NUM_EDGE;
    fscanf(fp , "%ld %ld" , &NUM_VERTEX , &NUM_EDGE);

    vector< vector< pair<long , long> > > graph(NUM_VERTEX);

    long lineDist[NUM_VERTEX];  // The heuristic distance from a vertex to 'DESTINATION'

    for( long i = 0 ; i < NUM_VERTEX ; i++ ){

        long token;
        fscanf(fp , "%ld" , &token);

        lineDist[i] = token;

    }

    for( long i = 0 ; i < NUM_EDGE ; i++ ){

        long v1 , v2 , edge;
        fscanf(fp , "%ld %ld %ld" , &v1 , &v2 , &edge);

        graph[v1].push_back( make_pair( v2 , edge ) );
        graph[v2].push_back( make_pair( v1 , edge ) );

    }

    long SOURCE , DESTINATION;
    fscanf(fp , "%ld %ld" , &SOURCE , &DESTINATION);

    fclose(fp);

    lineDist[DESTINATION] = 0;

    long distFromSource[NUM_VERTEX];    // The real distance from 'SOURCE' to a vertex.
    for( long i = 0 ; i < NUM_VERTEX ; i++ ){
        distFromSource[i] = LONG_MAX;
    }
    distFromSource[SOURCE] = 0;

    bool isDone[NUM_VERTEX];    // Stores if we are done with the vertex (with ID number = index number in array) or not.
    bool inNodeList[NUM_VERTEX];    // Stores if there is a 'Node' in 'NodeList' with id number = index number in array or not.
    for( long i = 0 ; i < NUM_VERTEX ; i++ ){
        isDone[i] = false;
        inNodeList[i] = false;
    }

    NodeList nodeList;

    nodeList.insert( SOURCE , lineDist[SOURCE] );
    inNodeList[SOURCE] = true;

    while( !nodeList.isEmpty() ){

        Node current = nodeList.deleteMin();

        if( current.id == DESTINATION ){
            fp = fopen(argv[2] , "w");
            fprintf(fp , "%ld" , current.dist);
            fclose(fp);
            return 0;
        }

        inNodeList[current.id] = false;
        isDone[ current.id ] = true;

        for( long i = 0 ; i < graph[current.id].size() ; i++ ){

            long neighbourID = graph[current.id][i].first;

            if( !isDone[neighbourID] ){

                long newDistFromSource = distFromSource[current.id] + graph[current.id][i].second;

                if( !inNodeList[neighbourID] ){

                    distFromSource[ neighbourID ] = newDistFromSource;
                    nodeList.insert( neighbourID , newDistFromSource+lineDist[neighbourID] );
                    inNodeList[neighbourID] = true;

                }else if( newDistFromSource < distFromSource[neighbourID] ){

                    distFromSource[ neighbourID ] = newDistFromSource;
                    nodeList.update( neighbourID , newDistFromSource+lineDist[neighbourID] );

                }

            }

        }

    }


    return 0;


}