import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class DDLOperations{
    private static Scanner scanner = new Scanner(System.in);


    /**
     * List all the types in the DB for user to select one.
     *
     * @param fileManager   A <code>FileManager</code> object which interacts with physical disk.
     * @param message       a String to be displayed to the user
     * @return              A <code>DataType</code> object that is chosen by the user.
     */
    static DataType getTypeFromUser(FileManager fileManager, String message) {
        List<DataType> types = getAllTypes(fileManager);

        if(types.isEmpty()){
            Logger.info("There aren't any TYPEs in the DB.");
            return null;
        }

        Logger.input(message);
        for(int i=0; i<types.size(); i++)
            System.out.println("\t[" + (i+1) + "]\t" + types.get(i));
        System.out.print(">>\t");
        int selection = scanner.nextInt();
        return types.get(selection-1);
    }


    /**
     * Creates a new data type (provided by the user) in the system.
     *
     * @param fileManager   A <code>FileManager</code> object which interacts with physical disk.
     * @throws IOException
     */
    static void createType(FileManager fileManager) throws IOException {
        Logger.input("<type_name> <N=field_count> <filed1_name> <field2_name> ... <filedN_name>");

        System.out.print(">>\t");
        String typeName = scanner.next();
        byte fieldNum = scanner.nextByte();

        // Each field of a record is of type integer which is 4 bytes.
        byte maxRecNum = (byte)(Page.PAGE_DATA_SIZE / (fieldNum * 4));

        String[] fieldNames = new String[fieldNum];
        for(int i=0; i<fieldNum; i++){
            fieldNames[i] = scanner.next();
        }

        DataType dataType = new DataType(typeName, maxRecNum, fieldNum, fieldNames);
        if(fileManager.createFile(typeName + ".txt") != -1){
            byte maxTypeNum = Page.PAGE_DATA_SIZE / 146;     // A type record spans 146 bytes in System Catalog
            if(fileManager.insertRecord(Main.SYSTEM_CATALOG_FILE_NAME, dataType.getRawDataType(), maxTypeNum))
                Logger.success("New TYPE (" + typeName + ") is created.");
            else {
                fileManager.deleteFile(typeName + ".txt");
                Logger.fail("TYPE (" + typeName + ") CANNOT be created.");
            }
        }else
            Logger.fail("TYPE (" + typeName + ") CANNOT be created.");
    }

    /**
     * Deletes a data type (provided by the user) from the system.
     *
     * @param fileManager   A <code>FileManager</code> object which interacts with physical disk.
     * @throws IOException
     */
    static void deleteType(FileManager fileManager) throws IOException {
        DataType dataType = getTypeFromUser(fileManager, "Select the TYPE to be deleted");
        if(dataType == null)
            return;

        fileManager.deleteFile(dataType.name + ".txt");

        String fileName = Main.SYSTEM_CATALOG_FILE_NAME;
        List<String> rawDataTypes;
        while( (rawDataTypes = fileManager.getRecords(fileName)) != null ){
            for(int i=0; i<rawDataTypes.size(); i++)
                if(dataType.name.equals((new DataType(rawDataTypes.get(i))).name)) {
                    fileManager.removeRecord(i);
                    Logger.success("TYPE " + dataType.name + " is deleted.");
                    return;
                }

            fileName = "";
        }
    }


    /**
     * Lists all the data types in the system.
     *
     */
    static void listAllTypes(FileManager fileManager) {
        List<DataType> types = getAllTypes(fileManager);
        Logger.info(types.size() + " TYPEs are found.");
        for(int i=0; i<types.size(); i++)
            System.out.println("\t" + types.get(i));
    }


    /**
     * @param fileManager   A <code>FileManager</code> object which interacts with physical disk.
     * @return              List of all the <code>DataType</code>s in the system.
     */
    static private List<DataType> getAllTypes(FileManager fileManager) {
        String fileName = Main.SYSTEM_CATALOG_FILE_NAME;
        List<DataType> allTypes = new ArrayList<>();
        List<String> rawPageTypes;
        while( (rawPageTypes = fileManager.getRecords(fileName)) != null ){
            for(String rawDataType: rawPageTypes)
                allTypes.add(new DataType(rawDataType));

            fileName = "";
        }
        return allTypes;
    }

}