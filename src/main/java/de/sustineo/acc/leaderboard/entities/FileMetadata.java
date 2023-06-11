package de.sustineo.acc.leaderboard.entities;

import lombok.Data;
import lombok.extern.java.Log;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private Instant creationDatetime;

    public FileMetadata(Path file) {
        this.file = file;
        this.absolutePath = file.toAbsolutePath();
        this.directory = file.getParent();
        this.name = file.getFileName().toString();
        this.checksum = calculateChecksum();
        this.creationDatetime = calculateCreationDatetime();
    }

    private Instant calculateCreationDatetime() {
        try {
            FileTime creationTime = (FileTime) Files.getAttribute(file, "creationTime");
            return creationTime.toInstant();
        } catch (IOException ignored) {
            return null;
        }
    }

    public String calculateChecksum() {
        MessageDigest messageDigest;
        String checksum;

        try {
            messageDigest = MessageDigest.getInstance("SHA-256");

            byte[] data = Files.readAllBytes(file);

            // Get the hash's bytes
            byte[] hash = messageDigest.digest(data);

            // This byte array has bytes in decimal format, convert it to hexadecimal format
            checksum = new BigInteger(1, hash).toString(16);
        } catch (NoSuchAlgorithmException | IOException e) {
            log.log(Level.WARNING, String.format("Could not calculate checksum for file %s", file), e);
            return null;
        }

        return checksum;
    }
}
