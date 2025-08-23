package de.sustineo.simdesk.entities.record;

import de.sustineo.simdesk.entities.Lap;
import de.sustineo.simdesk.entities.Track;

import java.util.List;

public record LapsByTrack(Track track, List<Lap> laps) {
    public static LapsByTrack of(Track track, List<Lap> laps) {
        return new LapsByTrack(track, laps);
    }
}
