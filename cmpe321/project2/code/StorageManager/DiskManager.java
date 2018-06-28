import java.io.*;

class DiskManager{
    private final String DISK_NAME = "DB_DISK";
    private final int DISK_SIZE = 10 * 1024 * 1024;   // Bytes
    private final String PAGE_SEPARATOR = "#";

    private File disk;              // Physical disk
    private String[] diskPages;     // Representation of virtual memory
    boolean isDiskNew;      // A boolean states whether the disk is formatted or not
    private BufferedReader br;      // A reader for reading `disk`
    private FileWriter fw;          // A writer for writing `disk`

    DiskManager() throws IOException{
        this.disk = new File(DISK_NAME);
        if(disk.isFile()) {
            Logger.info("DISK is found.");
            this.isDiskNew = false;
            loadDiskPages();
        } else {
            this.isDiskNew = true;
            disk.createNewFile();
            Logger.info("DISK CANNOT be found. A new one is created.");
        }
    }


    /**
     * Formats physical disk with empty pages of size <code>pageSize</code>
     *
     * @param pageSize  size of a page in bytes
     * @return          number of pages created on the disk
     * @throws IOException
     */
    int formatDisk(int pageSize) throws IOException {
        int pageNum = DISK_SIZE / pageSize;
        diskPages = new String[pageNum];
        for(int i=0; i<pageNum; i++){
            diskPages[i] = "";
        }
        saveDiskPages();
        Logger.info("DISK is formatted.");
        return pageNum;
    }


    /**
     * @param pageID    ID (address) of the disk page to be returned
     * @return          the content of the page with ID <code>pageID</code>
     */
    String getDiskPage(int pageID){
        Logger.info("Page#" + pageID + " is being read.");
        return diskPages[pageID];
    }


    /**
     * Writes <code>pageContent</code> into the page with ID <code>pageID</code> on the disk.
     *
     * @param pageID        id of the page in which <code>diskPage</code> will be written
     * @param pageContent   content to be written
     * @throws IOException
     */
    void saveDiskPage(int pageID, String pageContent) throws IOException {
        diskPages[pageID] = pageContent;
        saveDiskPages();
        Logger.info("Page#" + pageID + " is being written.");
    }


    /**
     * Sets the page with <code>pageID</code> on disk as free.
     *
     * @param pageID    ID of the page to be set free
     * @throws IOException
     */
    void freeDiskPage(int pageID) throws IOException {
        diskPages[pageID] = "";
        saveDiskPages();
        Logger.info("Page#" + pageID + " is set as free.");
    }


    /**
     * @return  ID of the first free page on disk. If there aren't any free pages, returns -1.
     */
    int getFirstFreePageID(){
        for(int i=0; i<diskPages.length; i++){
            if(diskPages[i].isEmpty())
                return i;
        }
        return -1;
    }


    /**
     * Loads pages of physical disk into `diskPages`.
     *
     * @throws IOException
     */
    private void loadDiskPages() throws IOException {
        br = new BufferedReader(new FileReader(disk));
        String rawDisk = br.readLine();

        if(rawDisk == null){
            Logger.info("DISK structure is broken.");
            this.isDiskNew = true;
        }else
            diskPages = rawDisk.split(PAGE_SEPARATOR, -1);

        br.close();
    }


    /**
     * Writes pages in <code>diskPages</code> into physical disk.
     *
     * @throws IOException
     */
    private void saveDiskPages() throws IOException {
        fw = new FileWriter(disk);
        fw.write(String.join(PAGE_SEPARATOR, diskPages));
        fw.close();
    }

}