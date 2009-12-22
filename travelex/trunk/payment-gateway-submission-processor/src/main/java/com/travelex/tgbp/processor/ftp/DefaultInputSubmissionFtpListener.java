package com.travelex.tgbp.processor.ftp;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

import com.travelex.tgbp.processor.SubmissionProcessor;
import com.travelex.tgbp.store.domain.Submission;

/**
 * Default implementation for {@link InputSubmissionFtpListener}.
 */
public class DefaultInputSubmissionFtpListener implements InputSubmissionFtpListener {

    @Reference protected SubmissionProcessor submissionProcessor;

    @Property(required=true) protected String baseDir;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onFileUpload(String fileName, InputStream data) throws Exception {
        byte[] rawData = IOUtils.toByteArray(data);
        saveFile(fileName, rawData);
        submissionProcessor.onSubmission(new Submission(fileName, rawData));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream onFileDownload(String fileName) throws Exception {
        return new ByteArrayInputStream(FileUtils.readFileToByteArray(createFile(fileName)));
    }

    /*
     * Creates file object using given file name and configured base path
     */
    private File createFile(String fileName) {
        return new File(baseDir + "/" + fileName);
    }

    /*
     * Creates physical file containing given data and stores it in to base directory with given name
     */
    private void saveFile(String fileName, byte[] rawData) throws IOException {
        final File dataFile = createFile(fileName);
        if (!dataFile.exists()) {
            dataFile.createNewFile();
        }
        final FileWriter fw = new FileWriter(dataFile);
        IOUtils.write(rawData, fw);
        IOUtils.closeQuietly(fw);
    }

}
