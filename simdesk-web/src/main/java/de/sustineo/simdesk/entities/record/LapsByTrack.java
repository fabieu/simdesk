package de.sustineo.simdesk.entities.record;

import de.sustineo.simdesk.entities.Lap;
import de.sustineo.simdesk.entities.RaceTrack;

import java.util.List;

public record LapsByTrack(RaceTrack raceTrack, List<Lap> laps) {
}
