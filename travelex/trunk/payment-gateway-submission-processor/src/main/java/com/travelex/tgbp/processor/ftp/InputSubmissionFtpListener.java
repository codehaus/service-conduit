package com.travelex.tgbp.processor.ftp;

import java.io.InputStream;

/**
 * Listens to incoming submission via FTP.
 */
public interface InputSubmissionFtpListener {

    /**
     * Call back received from FTP server on new file upload.
     *
     * @param fileName - name of file.
     * @param data - stream of file data.
     * @throws Exception - on error.
     */
    void onFileUpload(String fileName, InputStream data) throws Exception;

    /**
     * Call back received from FTP server on file down load request.
     *
     * @param fileName - name of file.
     * @param data - stream of file data.
     * @return stream of data
     * @throws Exception - on error.
     */
    InputStream onFileDownload(String fileName) throws Exception;
}
