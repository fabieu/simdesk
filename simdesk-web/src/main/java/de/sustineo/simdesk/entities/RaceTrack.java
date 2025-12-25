package de.sustineo.simdesk.entities;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Map;

@Data
@Builder
public class RaceTrack {
    private final String globalId;
    private final String displayName;
    private final double latitude;
    private final double longitude;
    @Singular("simulationId")
    private final Map<Simulation, String> simulationIds;

    public String getId(Simulation simulation) {
        if (simulationIds == null || simulation == null) {
            return null;
        }

        return simulationIds.get(simulation);
    }
}
