import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a file manager which interacts with physical disk through a disk manager.
 * File manager is responsible for creating files on disk and allocating pages to store records.
 *
 * First page (ID = 0) of the physical disk is allocated for "Disk Directory".
 * Disk Directory stores the information of ID (address) of the next free page and (file_name, page ID) pairs.
 */
class FileManager{
    private Page currentPage;   // Stores the most recently accessed page.
    private DiskManager diskManager;

    /**
     * Stores the "Disk Directory".
     * First element (index 0) stores the ID (address) of the next free disk page.
     * Other elements stores a String which encodes (file_name, page ID) pairs.
     */
    private List<String> diskDirArr;

    FileManager() throws IOException {
        this.currentPage = null;
        this.diskManager = new DiskManager();

        if(diskManager.isDiskNew)
            formatFileStructure();
        else
            loadDiskDirectory();
    }


    /**
     * Creates a new file with name <code>fileName</code> and
     * returns the page ID (address) of it.
     *
     * @param fileName  name of the file to be created
     * @return          page ID of the file created
     * @throws IOException
     */
    int createFile(String fileName) throws IOException {
        Page page = getNewPage();
        if(page == null){
            printNoSpaceOnDisk();
            return -1;
        }
        Logger.info(fileName + " is created.");
        diskDirArr.add(fileName + Page.RECORD_FIELD_SEP + page.pageID);
        updateNextFreePageID(-1);
        saveDiskDirectory();
        return page.pageID;
    }


    /**
     * Deletes a file (and all its pages) from the disk.
     *
     * @param fileName  name of the file to be deleted
     * @throws IOException
     */
    void deleteFile(String fileName) throws IOException {
        for(int i=1; i<diskDirArr.size(); i++){
            String[] fileInfoArr = diskDirArr.get(i).split(Page.RECORD_FIELD_SEP, -1);
            if(fileInfoArr[0].equals(fileName)) {
                Logger.info(fileName + " is being deleted.");
                freePages(Integer.parseInt(fileInfoArr[1]));
                Logger.info(fileName + " is deleted.");
                diskDirArr.remove(i);
                saveDiskDirectory();
                return;
            }
        }
        printFileNotFound(fileName);
    }


    /**
     * @param fileName  name of the file to check for existence
     * @return          true if file with <code>fileName</code> exists on the disk, otherwise false
     */
    boolean fileExists(String fileName) {
        return (getFilePageID(fileName) != -1);
    }


    /**
     * If <code>fileName</code> is not empty, returns the records of the first page of <code>fileName</code>.
     * If <code>fileName</code> is empty, returns the records of the next page of <code>currentPage</code>.
     * If <code>currentPage</code> has no next page, returns <code>null</code>.
     *
     * @param fileName  name of the file records of which will be returned
     * @return          valid records of the first page of <code>fileName</code> or the next page of <code>currentPageData</code>
     */
    List<String> getRecords(String fileName) {
        if(fileName.isEmpty()){
            if(currentPage.nextPageID == -1)
                return null;
            else
                currentPage = getPage(currentPage.nextPageID);
        }else {
            int pageID = getFilePageID(fileName);
            if(pageID == -1){
                printFileNotFound(fileName);
                return null;
            }
            currentPage = getPage(pageID);
        }

        return currentPage.records.subList(0, currentPage.recNum);
    }


    /**
     * Inserts <code>record</code> into a convenient page of the disk.
     *
     * @param fileName      name of the file into which <code>record</code> will be inserted.
     * @param record        record to be inserted to a page
     * @param maxRecNum     maximum number of records a page can store of <code>record</code>'s type
     * @return              true if insertion is successful, false otherwise
     * @throws IOException
     */
    boolean insertRecord(String fileName, String record, byte maxRecNum) throws IOException {
        int filePageID = getFilePageID(fileName);
        Page curPage = getPage(filePageID);

        while(curPage.isEmpty == 0 && curPage.nextPageID != -1)
            curPage = getPage(curPage.nextPageID);

        if(curPage.isEmpty == 0){
            Page newPage = getNewPage();
            if(newPage == null){
                printNoSpaceOnDisk();
                return false;
            }else{
                updateNextFreePageID(-1);
                saveDiskDirectory();
                curPage.nextPageID = newPage.pageID;
                savePage(curPage);
                curPage = newPage;
            }
        }
        curPage.insertRecord(record);
        if(curPage.recNum == maxRecNum)
            curPage.isEmpty = (byte)0;
        savePage(curPage);
        return true;
    }


    /**
     * Removes a record from <code>currentPage</code>.
     *
     * @param recIndex  index of the record (in <code>currentPage.records</code>) to be removed
     * @throws IOException
     */
    void removeRecord(int recIndex) throws IOException {
        currentPage.deleteRecord(recIndex);
        savePage(currentPage);
    }


    /**
     * Formats file structure on the disk and creates an initial "Disk Directory" in the first disk page.
     *
     * @throws IOException
     */
    private void formatFileStructure() throws IOException {
        diskManager.formatDisk(Page.PAGE_SIZE);
        Logger.info("Disk Directory is being created.");
        Logger.info("Page#0 is being read.");
        diskDirArr = new ArrayList<>(1);
        diskDirArr.add("1");  // First element stores the ID (address) of the next free PAGE
        saveDiskDirectory();
    }


    /**
     * Loads the "Disk Directory" from disk into <code>diskDirArr</code>.
     *
     * @throws IOException
     */
    private void loadDiskDirectory() throws IOException {
        String rawDiskDir = diskManager.getDiskPage(0);
        if(rawDiskDir.isEmpty())
            formatFileStructure();
        else {
            String[] diskDirArr = rawDiskDir.split(Page.RECORD_SEP, -1);
            this.diskDirArr = new ArrayList<>(diskDirArr.length);
            Collections.addAll(this.diskDirArr, diskDirArr);
            Logger.info("Disk Directory is loaded.");
        }
    }


    /**
     * Writes <code>diskDirArr</code> to the disk.
     *
     * @throws IOException
     */
    private void saveDiskDirectory() throws IOException {
        String rawDiskDir = String.join(Page.RECORD_SEP, diskDirArr);
        diskManager.saveDiskPage(0, rawDiskDir);
        Logger.info("Disk Directory is saved.");
    }


    /**
     * @param fileName  name of the file page ID (address) of which to be returned
     * @return          if <code>fileName</code> exists, page ID (address) of it; else -1
     */
    private int getFilePageID(String fileName) {
        for(int i=1; i<diskDirArr.size(); i++){
            String[] fileInfoArr = diskDirArr.get(i).split(Page.RECORD_FIELD_SEP, -1);
            if(fileInfoArr[0].equals(fileName))
                return Integer.parseInt(fileInfoArr[1]);
        }
        return -1;
    }


    /**
     * @return  first value of <code>diskDirArr</code> parsed into Integer
     */
    private int getNextFreePageID() {
        return Integer.parseInt(diskDirArr.get(0));
    }


    /**
     * If <code>pageID</code> is -1, gets the ID (address) of the first free disk page from <code>diskManager</code>
     * and updates the first element of <code>diskDirArr</code> with this value.
     *
     * If <code>pageID</code> is not -1, writes it to the first element of <code>diskDirArr</code> if
     * current value in <code>diskDirArr</code> is bigger than <code>pageID</code>
     *
     * @param pageID    ID of the next free disk page
     */
    private void updateNextFreePageID(int pageID) {
        if(pageID != -1) {
            int currentFreePageID = getNextFreePageID();
            if (currentFreePageID == -1 || pageID < currentFreePageID)
                diskDirArr.set(0, "" + pageID);
        }else
            diskDirArr.set(0, "" + diskManager.getFirstFreePageID());
    }


    /**
     * Loads the content of disk page with ID (address) <code>pageID</code> in a <code>Page</code> object
     * and returns it.
     *
     * @param pageID    ID (address) of disk page content of which is to be returned
     * @return          a <code>Page</code> which stores the content of disk page with ID <code>pageID</code>
     */
    private Page getPage(int pageID){
        if(pageID < 0)
            return null;

        String rawPage = diskManager.getDiskPage(pageID);
        if(rawPage.isEmpty())
            return new Page(pageID, -1, (byte)1, (byte)0, new ArrayList<>(0));

        return new Page(rawPage);
    }


    /**
     * Allocates a free disk page and returns it.
     * If no free page is available in the disk, returns null.
     *
     * @return  Empty page or null
     * @throws IOException
     */
    private Page getNewPage() throws IOException {
        int freePageID = getNextFreePageID();
        if(freePageID == -1){
            return null;
        }else{
            Page newPage = getPage(freePageID);
            savePage(newPage);
            Logger.info("New page (Page#" + freePageID + ") is allocated.");
            return newPage;
        }
    }


    /**
     * Writes <code>page</code> into the disk.
     *
     * @param page  a <code>Page</code> to be written in the disk.
     * @throws IOException
     */
    private void savePage(Page page) throws IOException {
        diskManager.saveDiskPage(page.pageID, page.getRawPage());
    }


    /**
     * Sets page with ID <code>initialPageID</code> and all its next pages as free on the disk.
     *
     * @param initialPageID     ID of the first page to be set as free on disk
     * @throws IOException
     */
    private void freePages(int initialPageID) throws IOException {
        Page curPage;
        int pageID = initialPageID;
        while( (curPage = getPage(pageID)) != null ){
            diskManager.freeDiskPage(pageID);
            updateNextFreePageID(pageID);
            pageID = curPage.nextPageID;
        }
    }


    private void printNoSpaceOnDisk(){
        Logger.info("!!! THERE IS NO AVAILABLE SPACE ON DISK. ALL THE PAGES ARE ALLOCATED !!!");
    }


    private void printFileNotFound(String fileName){
        Logger.info("File " + fileName + " CANNOT be found on the disk.");
    }

}