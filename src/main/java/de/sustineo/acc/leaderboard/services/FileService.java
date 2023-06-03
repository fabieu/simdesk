package de.sustineo.acc.leaderboard.services;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Log
@Service
public class FileService {
    public String removeBOM(String s) {
        final String BOM_MARKER = "\uFEFF";

        if (s.startsWith(BOM_MARKER)) {
            return s.substring(1);
        } else {
            return s;
        }
    }
}
