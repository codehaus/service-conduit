/*
 * SCA4J
 * Copyright (c) 2008-2012 Service Symphony Limited
 *
 * This proprietary software may be used only in connection with the SCA4J license
 * (the ?License?), a copy of which is included in the software or may be obtained 
 * at: http://www.servicesymphony.com/licenses/license.html.
 *
 * Software distributed under the License is distributed on an as is basis, without 
 * warranties or conditions of any kind.  See the License for the specific language 
 * governing permissions and limitations of use of the software. This software is 
 * distributed in conjunction with other software licensed under different terms. 
 * See the separate licenses for those programs included in the distribution for the 
 * permitted and restricted uses of such software.
 *
 */
package org.sca4j.fabric.services.archive;

import java.io.File;
import static java.io.File.separator;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

import org.sca4j.fabric.util.FileHelper;
import org.sca4j.host.runtime.HostInfo;
import org.sca4j.spi.services.archive.ArchiveStore;
import org.sca4j.spi.services.archive.ArchiveStoreException;

/**
 * The default implementation of ArchiveStore
 *
 * @version $Rev: 4108 $ $Date: 2008-05-04 04:06:21 +0100 (Sun, 04 May 2008) $
 */
@EagerInit
public class ArchiveStoreImpl implements ArchiveStore {
    protected File root;
    protected Map<URI, URL> archiveUriToUrl;
    protected String storeId;
    protected String domain;
    protected String repository;
    private boolean persistent = true;
    private File baseDir;

    /**
     * Creates a new archive store service instance
     *
     * @param hostInfo the host info for the runtime
     * @throws java.io.IOException if an error occurs initializing the repository
     */
    public ArchiveStoreImpl(@Reference HostInfo hostInfo) throws IOException {
        archiveUriToUrl = new ConcurrentHashMap<URI, URL>();
        baseDir = hostInfo.getBaseDir();
    }

    @Property(required = false)
    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    @Property(required = false)
    // TODO fixme when boolean properties supported
    public void setPersistent(String persistent) {
        this.persistent = Boolean.valueOf(persistent);
    }

    @Property(required = false)
    public void setRepository(String repository) {
        this.repository = repository;
    }

    @Init
    public void init() throws IOException {
        if (!persistent) {
            return;
        }
        if (repository != null) {
            root = new File(repository + separator + "index" + separator);
        } else {
            root = new File(baseDir, "stores" + separator + storeId + separator + "store");
        }
        FileHelper.forceMkdir(root);
        if (!root.exists() || !this.root.isDirectory() || !root.canRead()) {
            throw new IOException("The repository location is not a directory: " + repository);
        }
        FileHelper.forceMkdir(root);
        if (!root.exists() || !this.root.isDirectory() || !root.canRead()) {
            throw new IOException("The repository location is not a directory: " + repository);
        }
    }

    public URL store(URI uri, InputStream stream) throws ArchiveStoreException {
        try {
            File location = mapToFile(uri);
            // create the parent directory if necessary
            FileHelper.forceMkdir(location.getParentFile());
            write(stream, location);
            URL locationUrl = location.toURL();
            archiveUriToUrl.put(uri, locationUrl);
            return locationUrl;
        } catch (IOException e) {
            String id = uri.toString();
            throw new ArchiveStoreException("Error storing archive: " + id, id, e);
        }
    }

    public URL store(URI uri, URL sourceURL) throws ArchiveStoreException {
        try {
            // where the file should be stored in the repository
            File location = mapToFile(uri);
            File source = FileHelper.toFile(sourceURL);
            if (source == null || source.isFile()) {
                InputStream stream = sourceURL.openStream();
                try {
                    return store(uri, stream);
                } finally {
                    if (stream != null) {
                        stream.close();
                    }
                }
            } else {
                FileHelper.forceMkdir(location);
                FileHelper.copyDirectory(source, location);
                URL locationUrl = location.toURL();
                archiveUriToUrl.put(uri, locationUrl);
                return location.toURL();
            }
        } catch (IOException e) {
            String id = sourceURL.toString();
            throw new ArchiveStoreException("Error storing archive: " + id, id, e);
        }
    }

    public URL find(URI uri) {
        return archiveUriToUrl.get(uri);
    }

    public void remove(URI uri) {
        throw new UnsupportedOperationException();
    }

    public List<URI> list() {
        return new ArrayList<URI>(archiveUriToUrl.keySet());
    }

    /**
     * Resolve contribution location in the repository -> root repository / contribution file -> contribution group id / artifact id / version
     *
     * @param uri the uri to resolve
     * @return the mapped file
     */
    private File mapToFile(URI uri) {
        // FIXME: Map the contribution URI to a file?
        return new File(root, uri.getPath());
    }

    private void write(InputStream source, File target) throws IOException {
        RandomAccessFile file = new RandomAccessFile(target, "rw");
        FileChannel channel = null;
        FileLock lock = null;
        try {
            channel = file.getChannel();
            lock = channel.lock();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            byte[] bytes = buffer.array();
            int limit;
            while (-1 != (limit = source.read(bytes))) { // NOPMD
                buffer.flip();
                buffer.limit(limit);
                while (buffer.hasRemaining()) {
                    channel.write(buffer);
                }
                buffer.clear();
            }
            channel.force(true);
        } finally {
            if (channel != null) {
                if (lock != null) {
                    lock.release();
                }
                channel.close();
            }
            file.close();
        }

    }
}
