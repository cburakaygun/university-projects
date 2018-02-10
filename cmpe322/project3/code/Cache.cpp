#include "Cache.h"


/*
 * If 'cacheArray' contains 'blockNo', returns true.
 * Also, moves the node (of 'cacheIndexList') which holds the index number of 'cacheArray' cell at which 'blockNo' resides
 * to the tail of the list.
 * 
 * If 'cacheArray' does not contain 'blockNo', just returns false.
 */
bool Cache::contains(const int &blockNo){

    list<int>::iterator it = cacheIndexList.begin();
    /*
     * 'cacheIndexList' contains index numbers of 'cacheArray' from  LRU (head) to MRU (tail).
     */
    while( it != cacheIndexList.end() && cacheArray[*it] != blockNo ){
        it++;   // Goes to the next node while the cell of 'cacheArray' the index number of which is stored
                // in the current node (*it) does not contain the 'blockNo'.
    }

    if( it == cacheIndexList.end() ){   // If all the nodes of 'cacheIndexList' (all the indices of 'cacheArray') were iterated ...
        return false;       // ... and 'blockNo' wasn't found in 'cacheArray', returns false.
    }
                                // If 'blockNo' no was found at index i of 'cacheArray', ...
    updateCacheIndexList(it);   // ... moves the node which contains int i to the tail (MRU) of 'cacheIndexList' and ...
    return true;    // ... returns true.

}


/*
 * Inserts 'blockNo' to 'cacheArray' (into the position of the LRU block no) and
 * moves the node (of 'cacheIndexList')  which holds the index number of 'cacheArray' cell at which 'blockNo' resides
 * to the tail of the list.
 */
void Cache::insert(const int &blockNo){
    
    if( cacheArray.size() < cacheSize ){    // If there is some room in the cache, ...
        cacheArray.push_back(blockNo);      // ... inserts the 'blockNo' to the first empty cell (index i) and ...
        cacheIndexList.push_back(cacheArray.size()-1);  // ... inserts a node which contains int i to the tail (MRU) of 'cacheIndexList'.
    
    }else{                                          // If the cache is full, ...
        int cacheIndex = cacheIndexList.front();    // ... gets the index number at which the LRU block no resides and ...
                                                    // (Head of 'cacheIndexList' contains the index of 'cacheArray' at which LRU block no is stored.)
        
        cacheArray[cacheIndex] = blockNo;   // ... overwrites the cell which contains the LRU block no and ...
        updateCacheIndexList( cacheIndexList.begin() );     // ... moves the head (LRU) node to the tail (MRU) of 'cacheIndexList'.
    }

}


/*
 * Moves the node which is pointed to by 'it' to the tail of 'cacheIndexList'.
 */
void Cache::updateCacheIndexList(list<int>::iterator it){
    int cacheIndex = *it;       // Gets the value (say v) of the node pointed to by 'it'.
    cacheIndexList.erase(it);   // Deletes the node pointed to by 'it' from 'cacheIndexList'.
    cacheIndexList.push_back(cacheIndex);   // Add a (new) node which contains the value v to the tail (MRU) of 'cacheIndexList'.
}
