package de.sustineo.simdesk.entities;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.*;

public final class RaceTracks {
    private static final Set<RaceTrack> RACE_TRACKS = new LinkedHashSet<>();
    private static final Map<Simulation, Set<RaceTrack>> RACE_TRACKS_BY_SIMULATION = new HashMap<>();

    public static final RaceTrack IMOLA;
    public static final RaceTrack MONZA;
    public static final RaceTrack BRANDS_HATCH;
    public static final RaceTrack PAUL_RICARD;
    public static final RaceTrack VALENCIA;
    public static final RaceTrack ZANDVOORT;
    public static final RaceTrack ZOLDER;
    public static final RaceTrack BARCELONA;
    public static final RaceTrack COTA;
    public static final RaceTrack DONINGTON;
    public static final RaceTrack HUNGARORING;
    public static final RaceTrack INDIANAPOLIS;
    public static final RaceTrack KYALAMI;
    public static final RaceTrack LAGUNA_SECA;
    public static final RaceTrack MISANO;
    public static final RaceTrack MOUNT_PANORAMA;
    public static final RaceTrack NURBURGRING_NORDSCHLEIFE;
    public static final RaceTrack NURBURGRING;
    public static final RaceTrack OULTON_PARK;
    public static final RaceTrack RED_BULL_RING;
    public static final RaceTrack SILVERSTONE;
    public static final RaceTrack SNETTERTON;
    public static final RaceTrack SPA;
    public static final RaceTrack SUZUKA;
    public static final RaceTrack WATKINS_GLEN;

    private static final RaceTrack UNKNOWN_TRACK;

    static {
        BARCELONA = RaceTrack.builder()
                .globalId("barcelona")
                .displayName("Circuit de Barcelona-Catalunya")
                .latitude(41.57)
                .longitude(2.258056)
                .simulationId(Simulation.ACC, "barcelona")
                .build();
        register(BARCELONA);

        BRANDS_HATCH = RaceTrack.builder()
                .globalId("brands_hatch")
                .displayName("Brands Hatch")
                .latitude(51.359444)
                .longitude(0.260556)
                .simulationId(Simulation.ACC, "brands_hatch")
                .build();
        register(BRANDS_HATCH);

        COTA = RaceTrack.builder()
                .globalId("cota")
                .displayName("Circuit of the Americas")
                .latitude(30.136611)
                .longitude(-97.630692)
                .simulationId(Simulation.ACC, "cota")
                .build();
        register(COTA);

        DONINGTON = RaceTrack.builder()
                .globalId("donington_park")
                .displayName("Donington Park")
                .latitude(52.830556)
                .longitude(-1.375278)
                .simulationId(Simulation.ACC, "donington")
                .build();
        register(DONINGTON);

        HUNGARORING = RaceTrack.builder()
                .globalId("hungaroring")
                .displayName("Hungaroring")
                .latitude(47.583056)
                .longitude(19.251111)
                .simulationId(Simulation.ACC, "hungaroring")
                .build();
        register(HUNGARORING);

        IMOLA = RaceTrack.builder()
                .globalId("imola")
                .displayName("Autodromo Enzo e Dino Ferrari")
                .latitude(44.340278)
                .longitude(11.713611)
                .simulationId(Simulation.ACC, "imola")
                .build();
        register(IMOLA);

        INDIANAPOLIS = RaceTrack.builder()
                .globalId("indianapolis")
                .displayName("Indianapolis Motor Speedway")
                .latitude(39.794853)
                .longitude(-86.234822)
                .simulationId(Simulation.ACC, "indianapolis")
                .build();
        register(INDIANAPOLIS);

        KYALAMI = RaceTrack.builder()
                .globalId("kyalami")
                .displayName("Kyalami Grand Prix Circuit")
                .latitude(-25.998779)
                .longitude(28.069907)
                .simulationId(Simulation.ACC, "kyalami")
                .build();
        register(KYALAMI);

        LAGUNA_SECA = RaceTrack.builder()
                .globalId("laguna_seca")
                .displayName("Laguna Seca Raceway")
                .latitude(36.584722)
                .longitude(-121.752778)
                .simulationId(Simulation.ACC, "laguna_seca")
                .build();
        register(LAGUNA_SECA);

        MISANO = RaceTrack.builder()
                .globalId("misano")
                .displayName("Misano World Circuit Marco Simoncelli")
                .latitude(43.96138)
                .longitude(12.6833339)
                .simulationId(Simulation.ACC, "misano")
                .build();
        register(MISANO);

        MONZA = RaceTrack.builder()
                .globalId("monza")
                .displayName("Autodromo Nazionale Monza")
                .latitude(45.61896)
                .longitude(9.281216)
                .simulationId(Simulation.ACC, "monza")
                .build();
        register(MONZA);

        MOUNT_PANORAMA = RaceTrack.builder()
                .globalId("mount_panorama")
                .displayName("Mount Panorama Circuit")
                .latitude(-33.4475)
                .longitude(149.556389)
                .simulationId(Simulation.ACC, "mount_panorama")
                .build();
        register(MOUNT_PANORAMA);

        NURBURGRING = RaceTrack.builder()
                .globalId("nürburgring")
                .displayName("Nürburgring")
                .latitude(50.33174)
                .longitude(6.941024)
                .simulationId(Simulation.ACC, "nurburgring")
                .build();
        register(NURBURGRING);

        NURBURGRING_NORDSCHLEIFE = RaceTrack.builder()
                .globalId("nürburgring_nordschleife")
                .displayName("Nürburgring Nordschleife")
                .latitude(50.353248)
                .longitude(6.948595)
                .simulationId(Simulation.ACC, "nurburgring_24h")
                .build();
        register(NURBURGRING_NORDSCHLEIFE);

        OULTON_PARK = RaceTrack.builder()
                .globalId("oulton_park")
                .displayName("Oulton Park")
                .latitude(53.177594)
                .longitude(-2.614378)
                .simulationId(Simulation.ACC, "oulton_park")
                .build();
        register(OULTON_PARK);

        PAUL_RICARD = RaceTrack.builder()
                .globalId("paul_ricard")
                .displayName("Circuit Paul Ricard")
                .latitude(43.250556)
                .longitude(5.791667)
                .simulationId(Simulation.ACC, "paul_ricard")
                .build();
        register(PAUL_RICARD);

        RED_BULL_RING = RaceTrack.builder()
                .globalId("red_bull_ring")
                .displayName("Red Bull Ring")
                .latitude(47.219722)
                .longitude(14.764722)
                .simulationId(Simulation.ACC, "red_bull_ring")
                .build();
        register(RED_BULL_RING);

        SILVERSTONE = RaceTrack.builder()
                .globalId("silverstone")
                .displayName("Silverstone Circuit")
                .latitude(52.070278)
                .longitude(-1.016667)
                .simulationId(Simulation.ACC, "silverstone")
                .build();
        register(SILVERSTONE);

        SNETTERTON = RaceTrack.builder()
                .globalId("snetterton")
                .displayName("Snetterton Motor Racing Circuit")
                .latitude(52.466389)
                .longitude(0.945833)
                .simulationId(Simulation.ACC, "snetterton")
                .build();
        register(SNETTERTON);

        SPA = RaceTrack.builder()
                .globalId("spa")
                .displayName("Spa-Francorchamps")
                .latitude(50.438056)
                .longitude(5.969722)
                .simulationId(Simulation.ACC, "spa")
                .build();
        register(SPA);

        SUZUKA = RaceTrack.builder()
                .globalId("suzuka")
                .displayName("Suzuka International Racing Course")
                .latitude(34.844444)
                .longitude(136.533333)
                .simulationId(Simulation.ACC, "suzuka")
                .build();
        register(SUZUKA);

        VALENCIA = RaceTrack.builder()
                .globalId("valencia")
                .displayName("Circuit Ricardo Tormo")
                .latitude(39.485833)
                .longitude(-0.628056)
                .simulationId(Simulation.ACC, "valencia")
                .build();
        register(VALENCIA);

        WATKINS_GLEN = RaceTrack.builder()
                .globalId("watkins_glen")
                .displayName("Watkins Glen International")
                .latitude(42.336944)
                .longitude(-76.927222)
                .simulationId(Simulation.ACC, "watkins_glen")
                .build();
        register(WATKINS_GLEN);

        ZANDVOORT = RaceTrack.builder()
                .globalId("zandvoort")
                .displayName("Circuit Zandvoort")
                .latitude(52.388056)
                .longitude(4.544444)
                .simulationId(Simulation.ACC, "zandvoort")
                .build();
        register(ZANDVOORT);

        ZOLDER = RaceTrack.builder()
                .globalId("zolder")
                .displayName("Circuit Zolder")
                .latitude(50.989422)
                .longitude(5.25705)
                .simulationId(Simulation.ACC, "zolder")
                .build();
        register(ZOLDER);

        UNKNOWN_TRACK = RaceTrack.builder()
                .globalId("unknown_track")
                .displayName("Unknown track")
                .latitude(0.0)
                .longitude(0.0)
                .build();
    }

    private static void register(RaceTrack raceTrack) {
        // Register raceTrack globally
        RACE_TRACKS.add(raceTrack);

        // Register raceTrack for each simulationId it is available in
        for (Simulation simulation : raceTrack.getSimulationIds().keySet()) {
            RACE_TRACKS_BY_SIMULATION
                    .computeIfAbsent(simulation, s -> new LinkedHashSet<>())
                    .add(raceTrack);
        }

    }

    /**
     * Returns all registered race tracks.
     *
     * @return an immutable or backing set of all registered tracks
     */
    public static Set<RaceTrack> getAll() {
        return Collections.unmodifiableSet(RACE_TRACKS);
    }

    /**
     * Returns all race tracks available for the given simulation.
     *
     * @param simulation the simulation to query
     * @return a set of tracks available for the simulation,
     * or an empty set if none are registered
     */
    public static Set<RaceTrack> getAllBySimulation(@Nullable Simulation simulation) {
        return Collections.unmodifiableSet(RACE_TRACKS_BY_SIMULATION.getOrDefault(simulation, Collections.emptySet()));
    }

    /**
     * Checks whether a racetrack with the given simulation-specific id exists.
     *
     * @param simulation the simulation the id belongs to
     * @param id         the simulation-specific track id
     * @return {@code true} if a matching track exists, {@code false} otherwise
     */
    public static boolean exists(@Nullable Simulation simulation, @Nullable String id) {
        if (simulation == null || id == null) {
            return false;
        }

        return RACE_TRACKS_BY_SIMULATION.getOrDefault(simulation, Collections.emptySet()).stream()
                .anyMatch(raceTrack -> id.equals(raceTrack.getId(simulation)));
    }

    /**
     * Returns the racetrack matching the given simulation-specific id.
     *
     * <p>If no matching track is found, {@link #UNKNOWN_TRACK} is returned.
     *
     * @param simulation the simulation the id belongs to
     * @param id         the simulation-specific track id
     * @return the matching {@link RaceTrack} or {@link #UNKNOWN_TRACK} if not found
     */
    @Nonnull
    public static RaceTrack getById(@Nullable Simulation simulation, @Nullable String id) {
        if (simulation == null || id == null) {
            return UNKNOWN_TRACK;
        }

        return RACE_TRACKS_BY_SIMULATION.getOrDefault(simulation, Collections.emptySet()).stream()
                .filter(raceTrack -> id.equals(raceTrack.getId(simulation)))
                .findFirst()
                .orElse(UNKNOWN_TRACK);
    }
}
