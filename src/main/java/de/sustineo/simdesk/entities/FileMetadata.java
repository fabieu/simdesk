package de.sustineo.simdesk.entities;

import lombok.Data;
import lombok.extern.java.Log;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.logging.Level;

@Data
@Log
public class FileMetadata {
    private Path file;
    private Path absolutePath;
    private Path directory;
    private String name;
    private String checksum;
    private Instant modifiedDatetime;

    public FileMetadata(Path file) {
        this.file = file;
        this.absolutePath = file.toAbsolutePath();
        this.directory = file.getParent();
        this.name = file.getFileName().toString();
        this.checksum = calculateChecksum();
        this.modifiedDatetime = calculateModifiedDatetime();
    }

    private Instant calculateModifiedDatetime() {
        try {
            FileTime modifiedTime = Files.getLastModifiedTime(file, LinkOption.NOFOLLOW_LINKS);
            return modifiedTime.toInstant();
        } catch (IOException e) {
            return null;
        }
    }

    public String calculateChecksum() {
        try {
            byte[] data = Files.readAllBytes(file);
            checksum = DigestUtils.sha1Hex(data);

            return checksum;
        } catch (IOException e) {
            log.log(Level.WARNING, String.format("Could not calculate checksum for file %s", file), e);
            return null;
        }
    }
}
