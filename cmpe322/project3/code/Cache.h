#ifndef CACHE_H
#define CACHE_H

#include <vector>
#include <list>

using namespace std;

/*
 * Representation of the cache.
 */
struct Cache{

    int cacheSize = 0;          // Cache size in blocks.
    vector<int> cacheArray;     // Implementation of the cache. This array stores block numbers as contents of blocks.
    
    list<int> cacheIndexList;   // Implementation of LRU scheme.
                                // This list stores index numbers of 'cacheArray'.
                                // When a block is referenced in 'cacheArray', 
                                // the node which contains the corresponding index number of the array cell is removed from this list 
                                // and then inserted to the tail of it.
                                // Hence, the head of the list stores the index number of 'cacheArray' cell which holds the LRU block.


    /*
     * If 'cacheArray' contains 'blockNo', returns true.
     * Also, moves the node (of 'cacheIndexList') which holds the index number of 'cacheArray' cell at which 'blockNo' resides
     * to the tail of the list.
     * 
     * If 'cacheArray' does not contain 'blockNo', just returns false.
     */
    bool contains(const int &blockNo);


    /*
     * Inserts 'blockNo' to 'cacheArray' (into the position of the LRU block no) and
     * moves the node (of 'cacheIndexList')  which holds the index number of 'cacheArray' cell at which 'blockNo' resides
     * to the tail of the list.
     */
    void insert(const int &blockNo);


    /*
     * Moves the node which is pointed to by 'it' to the tail of 'cacheIndexList'.
     */
    void updateCacheIndexList(list<int>::iterator it);

};

#endif
