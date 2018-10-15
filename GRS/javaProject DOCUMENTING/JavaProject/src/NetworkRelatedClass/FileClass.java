package NetworkRelatedClass;

import CommonClass.AlertClass;

import java.io.*;

/**
 * This file is used to send file from user to server
 */
public class FileClass implements Serializable{


    private byte[] myByteArray;
    private String fileName=null;
    private String filePath;

    public FileClass(String fileName, String filePath){
        this.fileName = fileName;
        this.filePath = filePath;

        try {
            File myFile = new File(filePath);
            myByteArray = new byte[(int) myFile.length()];

            BufferedInputStream bis=new BufferedInputStream(new FileInputStream(myFile));
            bis.read(myByteArray, 0, myByteArray.length);
        } catch (FileNotFoundException e) {
            AlertClass alertClass=new AlertClass();
            alertClass.showErrorAlert("File Not Found");
          //  e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public FileClass(String fileName, byte[] myByteArray){
        this.fileName = fileName;
        this.myByteArray = myByteArray;

    }

    public byte[] getMyByteArray() {
        return myByteArray;
    }
    public void setMyByteArray(byte[] myByteArray) {
        this.myByteArray = myByteArray;
    }

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
