import java.io.*;
import java.util.Scanner;

public class Main {
    static final String SYSTEM_CATALOG_FILE_NAME = "SystemCatalog.txt";
    private static Scanner scanner = new Scanner(System.in);

    private enum Operation{
        DDL_CREATE_TYPE, DDL_DELETE_TYPE, DDL_LIST_TYPES,
        DML_CREATE_REC, DML_DELETE_REC, DML_FIND_REC, DML_LIST_RECS,
        QUIT
    }


    /**
     * Creates System Catalog file if it does not exist.
     *
     * @param fileManager       A <code>FileManager</code> object which interacts with physical disk.
     * @return                  true if System Catalog file exists or can be created, false if it does not exists and cannot be created
     * @throws IOException
     */
    private static boolean checkSystemCatalog(FileManager fileManager) throws IOException {
        if(fileManager.fileExists(SYSTEM_CATALOG_FILE_NAME))
            return true;
        else
            return fileManager.createFile(SYSTEM_CATALOG_FILE_NAME) != -1;
    }


    /**
     * List a list of operations for user to select.
     *
     * @return  the <code>Operation</code> the user selected
     */
    private static Operation selectOperation() {
        Logger.input("SELECT an Operation");
        System.out.println("\t[1]\tCreate a Type");
        System.out.println("\t[2]\tDelete a Type");
        System.out.println("\t[3]\tList all Types");
        System.out.println("\t[4]\tCreate a Record");
        System.out.println("\t[5]\tDelete a Record");
        System.out.println("\t[6]\tFind a Record");
        System.out.println("\t[7]\tList all Records (of a Type)");
        System.out.println("\t[8]\tQUIT");

        System.out.print(">>\t");
        int selection = scanner.nextInt();
        switch (selection) {
            case 1:
                return Operation.DDL_CREATE_TYPE;
            case 2:
                return Operation.DDL_DELETE_TYPE;
            case 3:
                return Operation.DDL_LIST_TYPES;
            case 4:
                return Operation.DML_CREATE_REC;
            case 5:
                return Operation.DML_DELETE_REC;
            case 6:
                return Operation.DML_FIND_REC;
            case 7:
                return Operation.DML_LIST_RECS;
            case 8:
                return Operation.QUIT;
            default:
                return null;
        }
    }

    public static void main(String[] args) throws IOException {

        FileManager fileManager = new FileManager();

        if(!checkSystemCatalog(fileManager)){
            Logger.fail("SystemCatalog file CANNOT be created.");
            return;
        }

        while(true){
            System.out.println();
            Operation op = selectOperation();
            if(op == null){
                Logger.fail("Unknown operation.");
                continue;
            }
            switch (op){
                case DDL_CREATE_TYPE:
                    DDLOperations.createType(fileManager);
                    break;
                case DDL_DELETE_TYPE:
                    DDLOperations.deleteType(fileManager);
                    break;
                case DDL_LIST_TYPES:
                    DDLOperations.listAllTypes(fileManager);
                    break;
                case DML_CREATE_REC:
                    DMLOperations.createRecord(fileManager);
                    break;
                case DML_DELETE_REC:
                    DMLOperations.deleteRecord(fileManager);
                    break;
                case DML_FIND_REC:
                    DMLOperations.findRecord(fileManager);
                    break;
                case DML_LIST_RECS:
                    DMLOperations.listAllRecords(fileManager);
                    break;
                default:
                    System.out.println("BYE!");
                    return;
            }

        }

    }
}