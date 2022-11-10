package de.sustineo.acc.leaderboard.services;

import de.sustineo.acc.leaderboard.entities.json.AccSession;

import java.util.List;

public interface ContentService {
    List<AccSession> getSessions();
}
