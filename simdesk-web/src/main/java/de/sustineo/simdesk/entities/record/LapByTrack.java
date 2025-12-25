package de.sustineo.simdesk.entities.record;

import de.sustineo.simdesk.entities.Lap;
import de.sustineo.simdesk.entities.RaceTrack;

public record LapByTrack(RaceTrack raceTrack, Lap lap) {
}
