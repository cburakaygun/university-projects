import java.util.ArrayList;
import java.util.List;

class Page {
    static final int PAGE_SIZE = 1024;      // Bytes
    static final int PAGE_HEADER_SIZE = 10; // Bytes, ID(4) + NextPageID(4) + isEmpty(1) + recNum(1)
    static final int PAGE_DATA_SIZE = PAGE_SIZE - PAGE_HEADER_SIZE;
    static String RECORD_SEP = "/";
    static String RECORD_FIELD_SEP = "&";

    // PAGE HEADER COMPONENTS
    int pageID;         // ID (address) of page
    int nextPageID;     // ID (address) of the next page of the file
    byte isEmpty;       // A flag states whether are is any space for a record to be inserted or not
    byte recNum;        // Number of valid records in the page

    List<String> records;   // Actual records in the page

    public Page(int pageID, int nextPageID, byte isEmpty, byte recNum, List<String> records){
        this.pageID = pageID;
        this.nextPageID = nextPageID;
        this.isEmpty = isEmpty;
        this.recNum = recNum;
        this.records = records;
    }

    public Page(String rawPage){
        String[] pageArr = rawPage.split(RECORD_SEP + "", -1);
        String[] pageHeaderArr = pageArr[0].split(RECORD_FIELD_SEP + "", -1);

        this.pageID = Integer.parseInt(pageHeaderArr[0]);
        this.nextPageID = Integer.parseInt(pageHeaderArr[1]);
        this.isEmpty = Byte.parseByte(pageHeaderArr[2]);
        this.recNum = Byte.parseByte(pageHeaderArr[3]);

        this.records = new ArrayList<>(pageArr.length-1);
        for(int i=1; i<pageArr.length; i++)
            this.records.add(pageArr[i]);
    }

    /**
     * Inserts a new <code>record</code> in the page.
     * A new <code>record</code> is always inserted after the <code>recNum</code>th <code>record</code> in the page.
     *
     * @param record  record to be inserted into the page
     */
    void insertRecord(String record){
        if(recNum == records.size())
            records.add(record);
        else
            records.set(recNum, record);

        recNum++;
    }

    /**
     * Deletes a record from the page.
     * Delete operation is as follows:
     * Last record of the page is copied into the position of the record to be deleted
     * and <code>recNum</code> is decreased by 1.
     *
     * @param recIndex  index of record (in <code>records</code>) which will be deleted.
     */
    void deleteRecord(int recIndex){
        String lastRecord = records.get(recNum-1);
        records.set(recIndex, lastRecord);
        recNum--;
        isEmpty = (byte)1;
    }

    String getRawPage(){
        String rawPage = "" + pageID + RECORD_FIELD_SEP + nextPageID + RECORD_FIELD_SEP +
                isEmpty + RECORD_FIELD_SEP + recNum;

        for(String record: records)
            rawPage += (RECORD_SEP + record);

        return rawPage;
    }

}