import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class DMLOperations{
    private static Scanner scanner = new Scanner(System.in);

    /**
     * Creates a record with information given by the user.
     *
     * @param fileManager   A <code>FileManager</code> object which interacts with physical disk.
     * @throws IOException
     */
    static void createRecord(FileManager fileManager) throws IOException {
        DataType dataType = DDLOperations.getTypeFromUser(fileManager, "Select the TYPE of record to be created");

        if(dataType == null)
            return;

        Logger.input("Write field values for TYPE " + dataType);
        System.out.print(">>\t");
        String record = "";
        for(int i=0; i<dataType.fieldNum; i++) {
            record += scanner.nextInt();
            if(i != dataType.fieldNum-1)
                record += Page.RECORD_FIELD_SEP;
        }

        if(fileManager.insertRecord(dataType.name + ".txt", record, dataType.maxRecNum))
            Logger.success("Record " + dataType.name + "(" +
                    record.replaceAll(Page.RECORD_FIELD_SEP, ", ") + ") is created.");
        else
            Logger.fail("Record " + dataType.name + "(" +
                    record.replaceAll(Page.RECORD_FIELD_SEP, ", ") + ") CANNOT be created.");
    }


    /**
     * Deletes a record of a type by a (primary) key the user provides.
     *
     * @param fileManager   A <code>FileManager</code> object which interacts with physical disk.
     * @throws IOException
     */
    static void deleteRecord(FileManager fileManager) throws IOException {
        findOrDeleteRecord(fileManager, false, "SELECT the TYPE of record to be deleted");
    }


    /**
     * Finds (retrieves) a record of a type by a (primary) key the user provides.
     *
     * @param fileManager   A <code>FileManager</code> object which interacts with physical disk.
     * @throws IOException
     */
    static void findRecord(FileManager fileManager) throws IOException {
        findOrDeleteRecord(fileManager, true, "SELECT the TYPE of record to be searched");
    }


    /**
     * Finds or deletes a record of a type by a (primary) key the user provides.
     *
     * @param fileManager   A <code>FileManager</code> object which interacts with physical disk.
     * @param isFind        A flag states whether the operation if 'find' or 'delete'
     * @throws IOException
     */
    private static void findOrDeleteRecord(FileManager fileManager, boolean isFind, String message) throws IOException {
        DataType dataType = DDLOperations.getTypeFromUser(fileManager, message);

        if(dataType == null)
            return;

        Logger.input("Write KEY <" + dataType.fieldNames[0] + "> value for TYPE " + dataType.name);
        System.out.print(">>\t");
        int key = scanner.nextInt();

        String fileName = dataType.name + ".txt";
        List<String> records;
        while( (records = fileManager.getRecords(fileName)) != null ) {
            for (int i=0; i<records.size(); i++){
                String record = records.get(i);
                if (key == Integer.parseInt(record.split(Page.RECORD_FIELD_SEP, -1)[0])) {      // First field is the KEY
                    if (isFind) {
                        Logger.success(dataType.name + " with " + dataType.fieldNames[0] + " = " + key + " is found:");
                        printRecord(dataType.name, record);
                    } else {
                        fileManager.removeRecord(i);
                        Logger.success(dataType.name + " with " + dataType.fieldNames[0] + " = " + key + " is deleted.");
                    }

                    return;
                }
            }
            fileName = "";      // To get the next records of next page, sets the fileName to empty String.
        }
        Logger.fail(dataType.name + " with " + dataType.fieldNames[0] + " = "  + key + " CANNOT be found in the DB.");
    }


    /**
     * Lists all the records of a type the user provides.
     *
     * @param fileManager   A <code>FileManager</code> object which interacts with physical disk.
     * @throws IOException
     */
    static void listAllRecords(FileManager fileManager) {
        DataType dataType = DDLOperations.getTypeFromUser(fileManager, "SELECT the TYPE of records to be listed");

        if(dataType == null)
            return;

        List<String> allRecords = new ArrayList<>();
        String fileName = dataType.name + ".txt";
        List<String> pageRecords;
        while( (pageRecords = fileManager.getRecords(fileName)) != null ) {
            allRecords.addAll(pageRecords);
            fileName = "";      // To get the next records of next page, sets the fileName to empty String.
        }

        Logger.info(allRecords.size() + " records are found for TYPE " + dataType.name + ".");
        for(String record: allRecords)
            printRecord(dataType.name, record);
    }


    /**
     * Prints the field values of <code>record</code> in a convenient way.
     *
     * @param typeName  type name of the <code>record</code>
     * @param record    a record field values of which is to be printed
     */
    private static void printRecord(String typeName, String record){
        System.out.println("\t" + typeName + "(" + record.replaceAll(Page.RECORD_FIELD_SEP + "", ", ") + ")");
    }

}