package de.sustineo.simdesk.entities.record;

import de.sustineo.simdesk.entities.Lap;
import de.sustineo.simdesk.entities.Track;

public record LapByTrack(Track track, Lap lap) {
    public static LapByTrack of(Track track, Lap lap) {
        return new LapByTrack(track, lap);
    }
}
