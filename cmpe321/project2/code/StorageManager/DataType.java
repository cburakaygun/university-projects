/**
 * Represents a data type information (which is stored in System Catalog)
 */
class DataType {
    private static final int MAX_TYPE_NAME_SIZE = 16;   // Characters
    private static final int MAX_FIELD_NAME_SIZE = 16;  // Characters
    private static final int MAX_FIELD_NUM = 8;
    private static char NAME_PADDING_CHAR = '_';

    String name;            // Name of the type
    byte maxRecNum;         // Maximum number of records a data page can store of this type
    byte fieldNum;          // Number of fields of this type
    String[] fieldNames;    // Names of fields

    DataType(String name, byte maxRecNum, byte fieldNum, String[] fieldNames){
        this.name = name;
        this.maxRecNum = maxRecNum;
        this.fieldNum = fieldNum;
        this.fieldNames = fieldNames.clone();
    }

    DataType(String rawDataType){
        String[] dataTypeArr = rawDataType.split(Page.RECORD_FIELD_SEP, -1);
        this.name = dataTypeArr[0].replaceAll(NAME_PADDING_CHAR + "", "");
        this.maxRecNum = Byte.parseByte(dataTypeArr[1]);
        this.fieldNum = Byte.parseByte(dataTypeArr[2]);
        this.fieldNames = new String[fieldNum];
        for(int i=0; i<fieldNum; i++)
            this.fieldNames[i] = dataTypeArr[3+i].replaceAll(NAME_PADDING_CHAR + "", "");
    }

    @Override
    public String toString(){
        String result = name + '(';
        for(int i=0; i<fieldNum; i++){
            result += fieldNames[i];
            if(i != fieldNum-1)
                result += ", ";
        }
        return result + ")";
    }

    public String getRawDataType(){
        String rawDataType = rightPad(this.name, MAX_TYPE_NAME_SIZE, NAME_PADDING_CHAR) +
                Page.RECORD_FIELD_SEP + this.maxRecNum + Page.RECORD_FIELD_SEP + this.fieldNum;
        for(String fieldName: fieldNames)
            rawDataType += (Page.RECORD_FIELD_SEP +
                    rightPad(fieldName, MAX_FIELD_NAME_SIZE, NAME_PADDING_CHAR));

        for(int i=fieldNum; i<MAX_FIELD_NUM; i++)
            rawDataType += (Page.RECORD_FIELD_SEP +
                    rightPad("", MAX_FIELD_NAME_SIZE, NAME_PADDING_CHAR));

        return rawDataType;
    }

    /**
     * Inserts <code>paddingChar</code> to the right of <code>str</code> until the size (character number)
     * of <code>str</code> becomes equal to <code>charSize</code>.
     *
     * @param str           String to be padded
     * @param charSize      number of characters when the padding stops
     * @param paddingChar   character to be appended to <code>str</code>
     * @return              right padded <code>str</code>
     */
    private String rightPad(String str, int charSize, char paddingChar){
        String result = str;
        for(int i=str.length(); i<charSize; i++){
            result += paddingChar;
        }
        return result;
    }

}