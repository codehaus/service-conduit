package com.travelex.tgbp.processor.ftp;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;

/**
 * Default implementation for {@link InputSubmissionFtpListener}.
 */
@EagerInit
@Scope("COMPOSITE")
public class DefaultInputSubmissionFtpListener implements InputSubmissionFtpListener {

    @Property(required=true) protected String baseDir;

    @Init
    public void init() {
       final File baseDirectory = new File(baseDir);
       if (!baseDirectory.exists() || !baseDirectory.isDirectory()) {
           throw new RuntimeException("Base directory not found : " + baseDir);
       }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onFileUpload(String fileName, InputStream data) throws Exception {
        final File dataFile = createFile(fileName);
        if (!dataFile.exists()) {
            dataFile.createNewFile();
        }
        final FileWriter fw = new FileWriter(createFile(fileName));
        IOUtils.copy(data, fw);
        IOUtils.closeQuietly(fw);
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

}
