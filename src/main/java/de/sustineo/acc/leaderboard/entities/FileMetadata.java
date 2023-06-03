package de.sustineo.acc.leaderboard.entities;

import lombok.Data;
import lombok.extern.java.Log;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;

@Data
@Log
public class FileMetadata {
    private Path absolutePath;
    private Path directory;
    private String name;
    private String checksum;

    public FileMetadata(Path path) {
        this.absolutePath = path.toAbsolutePath();
        this.directory = path.getParent();
        this.name = path.getFileName().toString();
        this.checksum = calculateChecksum(path);
    }

    public String calculateChecksum(Path path) {
        MessageDigest messageDigest;
        String checksum;

        try {
            messageDigest = MessageDigest.getInstance("SHA-256");

            byte[] data = Files.readAllBytes(path);

            // Get the hash's bytes
            byte[] hash = messageDigest.digest(data);

            // This byte array has bytes in decimal format, convert it to hexadecimal format
            checksum = new BigInteger(1, hash).toString(16);
        } catch (NoSuchAlgorithmException | IOException e) {
            log.log(Level.WARNING, String.format("Could not calculate checksum for file %s", path.toAbsolutePath()), e);
            return null;
        }

        return checksum;
    }
}
