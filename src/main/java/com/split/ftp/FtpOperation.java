package com.split.ftp;
 
 
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
 
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
 
 
/**
   * Class description: file upload and download tools
 */
@Component
@Data
@Slf4j
public class FtpOperation {
    public static final int imageCutSize = 300;
 
    private String userName;
 
    private String passWord;
 
    private String ip;
 
    private int port;
 
    private String CURRENT_DIR; // The directory where the file is stored
 
    public static final String DIRSPLIT = "/";
 
 
         // Downloaded file directory
    private String DOWNLOAD_DIR;
 
         // ftp client
    private FTPClient ftpClient = new FTPClient();

    public FtpOperation(String login, String password, String ip, int port, String folder) {
        this.userName = login;
        this.passWord = password;
        this.ip = ip;
        this.port = port;
        this.CURRENT_DIR = folder;
    }
 
    /**
           * Function: upload file attachment to file server
     *
           * @param buffIn: upload file stream
           * @param fileName: save the file name
           * @param needDelete: whether to delete at the same time
     * @return
     * @throws IOException
     */
    public boolean uploadToFtp(InputStream buffIn, String fileName, boolean needDelete) throws FTPConnectionClosedException, IOException, Exception {
        boolean returnValue = false;
                 // upload files 
        try {
 
                         // establish connection 
            connectToServer();
                         // Set transfer binary file
            setFileType(FTP.BINARY_FILE_TYPE);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw new IOException("failed to connect to the FTP Server:" + ip);
            }
            ftpClient.enterLocalPassiveMode();
               /* if(StringUtils.checkStr(CURRENT_DIR)){
                	if(!existDirectory(CURRENT_DIR)){
                		this.createDirectory(CURRENT_DIR);
                	}
                    ftpClient.changeWorkingDirectory(CURRENT_DIR);
                }*/
                         // Upload file to ftp
            returnValue = ftpClient.storeFile(CURRENT_DIR+fileName, buffIn);
            if (needDelete) {
                ftpClient.deleteFile(CURRENT_DIR+fileName);
            }
                         // Output operation result information
            if (returnValue) {
                log.info("uploadToFtp INFO: upload file  to ftp : succeed!");
            } else {
                log.info("uploadToFtp INFO: upload file  to ftp : failed!");
            }
            buffIn.close();
                         // close the connection
            closeConnect();
        } catch (FTPConnectionClosedException e) {
                         log.error("FTP connection was closed!", e);
            throw e;
        } catch (Exception e) {
            returnValue = false;
            log.error("ERR : upload file  to ftp : failed! ", e);
            throw e;
        } finally {
            try {
                if (buffIn != null) {
                    buffIn.close();
                }
            } catch (Exception e) {
                                 log.error("FTP failed to close the input stream!", e);
            }
            if (ftpClient.isConnected()) {
                closeConnect();
            }
        }
        return returnValue;
    }
 
 
 
 
    /**
           * Function: download file stream according to file name
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public InputStream downloadFile(String filename)
            throws IOException {
        InputStream in = null;
        try {
                         // establish connection 
            connectToServer();
            ftpClient.enterLocalPassiveMode();
                         // Set transfer binary file
            setFileType(FTP.BINARY_FILE_TYPE);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw new IOException("failed to connect to the FTP Server:" + ip);
            }
            ftpClient.changeWorkingDirectory(CURRENT_DIR);
 
                         // ftp file get file
            in = ftpClient.retrieveFileStream(filename);
 
        } catch (FTPConnectionClosedException e) {
                         log.error("FTP connection was closed!", e);
            throw e;
        } catch (Exception e) {
            log.error("ERR : upload file " + filename + " from ftp : failed!", e);
        }
        return in;
    }
 
    /**
           * Transcoding [GBK -> ISO-8859-1] Different platforms require different transcoding
     *
     * @param obj
     * @return
     */
        //    private String gbkToIso8859(Object obj) {
        //        try {
        //            if (obj == null)
        //                return "";
        //            else
        //                return new String(obj.toString().getBytes("GBK"), "iso-8859-1");
        //        } catch (Exception e) {
        //            return "";
        //        }
        //    }
 
    /**
           * Set the type of transferred file [text file or binary file]
     *
     * @param fileType --BINARY_FILE_TYPE、ASCII_FILE_TYPE
     */
    private void setFileType(int fileType) {
        try {
            ftpClient.setFileType(fileType);
        } catch (Exception e) {
                         log.error("Failed to set the type of file transfer by FTP!", e);
        }
    }
 
    /**
           * Function: close the connection
     */
    public void closeConnect() {
        try {
            if (ftpClient != null) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (Exception e) {
                         log.error("Failed to close FTP connection!", e);
        }
    }
 
    /**
           * Connect to ftp server
     */
    private void connectToServer() throws FTPConnectionClosedException, Exception {
        if (!ftpClient.isConnected()) {
            int reply;
            try {
                ftpClient = new FTPClient();
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.enterLocalPassiveMode();
                System.out.println("ip: " +  ip);
                System.out.println("port: " +  port);
                System.out.println("username: " +  userName);
                ftpClient.connect(ip, port);
                ftpClient.login(userName, passWord);
                reply = ftpClient.getReplyCode();
 
                if (!FTPReply.isPositiveCompletion(reply)) {
                    ftpClient.disconnect();
                    log.info("connectToServer FTP server refused connection.");
                }
 
            } catch (FTPConnectionClosedException ex) {
                                 log.error("Server:IP:" + ip + "No connection! There are too many connected users, please try later", ex);
                throw ex;
            } catch (Exception e) {
                                 log.error("Login to ftp server [" + ip + "] failed", e);
                throw e;
            }
        }
    }
 
    // Check the path is exist; exist return true, else false.
    public boolean existDirectory(String path) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        for (FTPFile ftpFile : ftpFileArr) {
            if (ftpFile.isDirectory()
                    && ftpFile.getName().equalsIgnoreCase(path)) {
                flag = true;
                break;
            }
        }
        return flag;
    }
 
    /**
           * Create FTP folder directory
     *
     * @param pathName
     * @return
     * @throws IOException
     */
    public boolean createDirectory(String pathName) throws IOException {
        boolean isSuccess = false;
        try {
            connectToServer();
            isSuccess = ftpClient.makeDirectory(CURRENT_DIR + pathName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
 
    /**
           * Dotted
     *
     * @param fileName
     * @return
     */
    public static String getExtention(String fileName) {
        int pos = fileName.lastIndexOf(".");
        return fileName.substring(pos);
    }
 
    /**
           * Without dots
     *
     * @param fileName
     * @return
     */
    public static String getNoPointExtention(String fileName) {
        int pos = fileName.lastIndexOf(".");
        return fileName.substring(pos + 1);
    }
 
    /**
           * Function: Get the file directory according to the current time
     *
     * @return String
     */
    public static String getDateDir(Date dateParam) {
        Calendar cal = Calendar.getInstance();
        if (null != dateParam) {
            cal.setTime(dateParam);
        }
        int currentYear = cal.get(Calendar.YEAR);
        int currentMouth = cal.get(Calendar.MONTH) + 1;
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);
        //int currentHour = cal.get(Calendar.HOUR_OF_DAY);
        //return currentYear+FtpOperation.DIRSPLIT+currentMouth+FtpOperation.DIRSPLIT+currentDay+FtpOperation.DIRSPLIT+currentHour;
        return currentYear + FtpOperation.DIRSPLIT + currentMouth + FtpOperation.DIRSPLIT + currentDay;
    }
 
 
}