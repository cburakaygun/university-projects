Run the following command on a **Terminal** to start the program:

`java -jar StorageManager.jar`

A text file with name *DB_DISK* will be created in the directory in which **StorageManager.jar** resides.
This file represents a hard disk drive.


The program guides you by writing appropriate information messages on the screen.
You can find an example usage under section `3 SAMPLE USAGE AND OUTPUTS` in the `REPORT`.
This section containts screenshots of operations.
It also contains some explanations about what is happening in the program while an operation is being performed.


Here are the basic usage of operations:


## DDL OPERATIONS

### Creating a Type
Select **1** in main menu.
Then, give an input in the following format:

`<type_name> <N=field_count> <field1_name> ... <fieldN_name>`


### Deleting a Type
Select **2** in main menu.
Then, select the TYPE to be deleted.


### Listing All the Types
Select **3** in main menu.


## DML OPERATIONS

### Creating a Record
Select **4** in main menu.
Then, select the TYPE of record to be created.
Then, enter the values of fields in the following format:

`<field1_value> <field2_value> ... <fieldN_value>`


### Deleting a Record
Select **5** in main menu.
Then, select the TYPE of record to be deleted.
Then, enter the value of the (primary) key of the record.


### Searching for a Record
Select **6** in main menu.
Then, select the TYPE of record to be found.
Then, enter the value of the (primary) key of the record.


### Listing All the Records of a Type
Select **7** in main menu.
Then, select the TYPE of records to be listed.


### Quiting the Program
Select **8** in main menu.
