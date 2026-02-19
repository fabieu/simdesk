package de.sustineo.simdesk.services.stewarding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.stewarding.StewardingEntrylist;
import de.sustineo.simdesk.entities.stewarding.StewardingEntrylistDriver;
import de.sustineo.simdesk.entities.stewarding.StewardingEntrylistEntry;
import de.sustineo.simdesk.mybatis.mapper.StewardingEntrylistDriverMapper;
import de.sustineo.simdesk.mybatis.mapper.StewardingEntrylistEntryMapper;
import de.sustineo.simdesk.mybatis.mapper.StewardingEntrylistMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Profile(SpringProfile.STEWARDING)
@Service
@RequiredArgsConstructor
public class StewardingEntrylistService {
    private final StewardingEntrylistMapper entrylistMapper;
    private final StewardingEntrylistEntryMapper entryMapper;
    private final StewardingEntrylistDriverMapper driverMapper;
    private final ObjectMapper objectMapper;

    public StewardingEntrylist getEntrylistByWeekendId(Integer weekendId) {
        List<StewardingEntrylist> entrylists = entrylistMapper.findByRaceWeekendId(weekendId);
        return entrylists.isEmpty() ? null : entrylists.getFirst();
    }

    public StewardingEntrylist getEntrylistBySessionId(Integer sessionId) {
        List<StewardingEntrylist> entrylists = entrylistMapper.findBySessionId(sessionId);
        return entrylists.isEmpty() ? null : entrylists.getFirst();
    }

    public List<StewardingEntrylistEntry> getEntriesByEntrylistId(Integer entrylistId) {
        return entryMapper.findByEntrylistId(entrylistId);
    }

    public List<StewardingEntrylistDriver> getDriversByEntryId(Integer entryId) {
        return driverMapper.findByEntryId(entryId);
    }

    @Transactional
    public void uploadEntrylist(Integer weekendId, String jsonContent) {
        deleteEntrylist(weekendId);

        JsonNode root;
        try {
            root = objectMapper.readTree(jsonContent);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON content", e);
        }

        StewardingEntrylist entrylist = new StewardingEntrylist();
        entrylist.setRaceWeekendId(weekendId);
        entrylist.setRawJson(jsonContent);
        entrylistMapper.insert(entrylist);

        parseAndInsertEntries(root, entrylist);
    }

    @Transactional
    public void uploadEntrylistForSession(Integer sessionId, Integer weekendId, String jsonContent) {
        deleteEntrylistForSession(sessionId);

        JsonNode root;
        try {
            root = objectMapper.readTree(jsonContent);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON content", e);
        }

        StewardingEntrylist entrylist = new StewardingEntrylist();
        entrylist.setSessionId(sessionId);
        entrylist.setRaceWeekendId(weekendId);
        entrylist.setRawJson(jsonContent);
        entrylistMapper.insert(entrylist);

        parseAndInsertEntries(root, entrylist);
    }

    private void parseAndInsertEntries(JsonNode root, StewardingEntrylist entrylist) {

        JsonNode entries = root.get("entries");
        if (entries == null || !entries.isArray()) {
            return;
        }

        for (JsonNode entryNode : entries) {
            int raceNumber = entryNode.path("raceNumber").asInt();
            int forcedCarModel = entryNode.path("forcedCarModel").asInt();

            JsonNode driversNode = entryNode.get("drivers");
            String displayName = "#" + raceNumber;
            if (driversNode != null && driversNode.isArray() && !driversNode.isEmpty()) {
                String shortName = driversNode.get(0).path("shortName").asText("");
                displayName = "#" + raceNumber + " \u2014 " + shortName;
            }

            StewardingEntrylistEntry entry = new StewardingEntrylistEntry();
            entry.setEntrylistId(entrylist.getId());
            entry.setRaceNumber(raceNumber);
            entry.setCarModelId(forcedCarModel);
            entry.setDisplayName(displayName);
            entryMapper.insert(entry);

            if (driversNode != null && driversNode.isArray()) {
                for (JsonNode driverNode : driversNode) {
                    StewardingEntrylistDriver driver = new StewardingEntrylistDriver();
                    driver.setEntryId(entry.getId());
                    driver.setFirstName(driverNode.path("firstName").asText(""));
                    driver.setLastName(driverNode.path("lastName").asText(""));
                    driver.setShortName(driverNode.path("shortName").asText(""));
                    driver.setSteamId(driverNode.path("playerID").asText(""));
                    driver.setCategory(driverNode.path("driverCategory").asInt(0));
                    driverMapper.insert(driver);
                }
            }
        }
    }

    @Transactional
    public void deleteEntrylist(Integer weekendId) {
        List<StewardingEntrylist> existing = entrylistMapper.findByRaceWeekendId(weekendId);
        for (StewardingEntrylist entrylist : existing) {
            deleteEntrylistEntries(entrylist);
        }
        entrylistMapper.deleteByRaceWeekendId(weekendId);
    }

    @Transactional
    public void deleteEntrylistForSession(Integer sessionId) {
        List<StewardingEntrylist> existing = entrylistMapper.findBySessionId(sessionId);
        for (StewardingEntrylist entrylist : existing) {
            deleteEntrylistEntries(entrylist);
        }
        entrylistMapper.deleteBySessionId(sessionId);
    }

    private void deleteEntrylistEntries(StewardingEntrylist entrylist) {
        List<StewardingEntrylistEntry> entries = entryMapper.findByEntrylistId(entrylist.getId());
        for (StewardingEntrylistEntry entry : entries) {
            driverMapper.deleteByEntryId(entry.getId());
        }
        entryMapper.deleteByEntrylistId(entrylist.getId());
    }
}
