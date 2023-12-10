package de.sustineo.acc.servertools.controller;

import de.sustineo.acc.servertools.configuration.ProfileManager;
import de.sustineo.acc.servertools.services.raceapp.RaceAppService;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@Profile(ProfileManager.PROFILE_RACEAPP)
@RestController
@RequestMapping("/api/raceapp")
public class RaceAppController {
    private final RaceAppService raceAppService;

    public RaceAppController(RaceAppService raceAppService) {
        this.raceAppService = raceAppService;
    }

    @GetMapping(value = "/events/{id}/bookings", produces = "text/csv")
    public ResponseEntity<FileSystemResource> getRaceAppEvent(@PathVariable Integer id) throws IOException {
        File file = raceAppService.getEventBookingsFile(id);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + file.getName())
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new FileSystemResource(file));
    }
}
