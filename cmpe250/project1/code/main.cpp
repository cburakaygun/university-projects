#include <iostream>
#include <string>
#include <vector>
#include <sstream>
#include <algorithm>
#include <fstream>
#include "BigInteger.h"

using namespace std;

ostream& operator<<(ostream& out, const Node& node) {
    Node n2 = node;
    Node* p = &n2;
    out << p->data << " ";
    while(p->next) {
        p = p->next;
        out << p->data << " ";
    }
    return out;
}

ostream &operator<<(ostream &out, const BigInteger &bigInteger)  {
    string str = "";
    Node *head = bigInteger.num->head;
    while (head) {
        str += to_string(head->data);
        head = head->next;
    }
    reverse(str.begin(), str.end());
    out << str;
    return out;
}


int main (int argc, char* argv[]) {

    if (argc != 3) {
        cout << "Run the code with the following command: ./project1 [input_file] [output_file]" << endl;
        return 1;
    }

    cout << "input file: " << argv[1] << endl;
    cout << "output file: " << argv[2] << endl;

    ifstream infile(argv[1]);
    string line;
    vector<string> input;
    while (getline(infile, line)) {
        istringstream iss(line);
        string str;
        iss >> str;
        input.push_back(str);
    }

    cout << "input file has been read" << endl;
    cout << "\toperator: " << input[0] << endl;
    cout << "\tnumber1: " << input[1] << endl;
    cout << "\tnumber2: " << input[2] << endl;

    BigInteger int1(input[1]);
    BigInteger int2(input[2]);

    BigInteger result;
    if( input[0] == "+" ){
      result = int1 + int2;
    }else{
       result = int1 * int2;
    }
    cout << "Result: " << result << endl;
    cout << "The result is also written in " << argv[2] << endl;

    freopen(argv[2], "w", stdout);
    cout << result;

    return 0;
}