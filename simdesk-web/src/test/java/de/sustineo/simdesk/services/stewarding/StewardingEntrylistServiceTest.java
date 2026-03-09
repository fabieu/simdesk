package de.sustineo.simdesk.services.stewarding;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.stewarding.StewardingEntrylist;
import de.sustineo.simdesk.entities.stewarding.StewardingEntrylistDriver;
import de.sustineo.simdesk.entities.stewarding.StewardingEntrylistEntry;
import de.sustineo.simdesk.mybatis.mapper.StewardingEntrylistDriverMapper;
import de.sustineo.simdesk.mybatis.mapper.StewardingEntrylistEntryMapper;
import de.sustineo.simdesk.mybatis.mapper.StewardingEntrylistMapper;
import de.sustineo.simdesk.services.IdGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles({SpringProfile.STEWARDING})
@SpringBootTest(classes = {
        StewardingEntrylistService.class,
        ObjectMapper.class,
        SpringProfile.class
})
class StewardingEntrylistServiceTest {

    @Autowired
    private StewardingEntrylistService entrylistService;

    @MockitoBean
    private StewardingEntrylistMapper entrylistMapper;

    @MockitoBean
    private StewardingEntrylistEntryMapper entrylistEntryMapper;

    @MockitoBean
    private StewardingEntrylistDriverMapper entrylistDriverMapper;

    @MockitoBean
    private IdGenerator idGenerator;

    @Test
    void uploadEntrylistForRound_shouldParseValidAccJson() {
        when(entrylistMapper.findByRoundId("round1")).thenReturn(Collections.emptyList());
        when(idGenerator.generateRandomString(12)).thenReturn("testid123456");

        String accJson = """
                {
                  "entries": [
                    {
                      "drivers": [
                        {
                          "firstName": "Max",
                          "lastName": "Mustermann",
                          "shortName": "MUS",
                          "driverCategory": 1,
                          "playerID": "S76561198000000001"
                        }
                      ],
                      "raceNumber": 1,
                      "forcedCarModel": 35,
                      "defaultGridPosition": -1,
                      "overrideDriverInfo": 0
                    },
                    {
                      "drivers": [
                        {
                          "firstName": "John",
                          "lastName": "Doe",
                          "shortName": "DOE",
                          "driverCategory": 2,
                          "playerID": "S76561198000000002"
                        },
                        {
                          "firstName": "Jane",
                          "lastName": "Smith",
                          "shortName": "SMI",
                          "driverCategory": 1,
                          "playerID": "S76561198000000003"
                        }
                      ],
                      "raceNumber": 42,
                      "forcedCarModel": 30
                    }
                  ],
                  "forceEntryList": 1
                }
                """;

        entrylistService.uploadEntrylistForRound("round1", accJson);

        verify(entrylistMapper).deleteByRoundId("round1");
        verify(entrylistMapper).insert(any(StewardingEntrylist.class));
        verify(entrylistEntryMapper, times(2)).insert(any(StewardingEntrylistEntry.class));
        verify(entrylistDriverMapper, times(3)).insert(any(StewardingEntrylistDriver.class));
    }

    @Test
    void uploadEntrylistForRound_shouldHandleEmptyEntries() {
        when(entrylistMapper.findByRoundId("round1")).thenReturn(Collections.emptyList());
        when(idGenerator.generateRandomString(12)).thenReturn("testid123456");

        String accJson = """
                {
                  "entries": [],
                  "forceEntryList": 1
                }
                """;

        entrylistService.uploadEntrylistForRound("round1", accJson);

        verify(entrylistMapper).deleteByRoundId("round1");
        verify(entrylistMapper).insert(any(StewardingEntrylist.class));
        verify(entrylistEntryMapper, never()).insert(any());
        verify(entrylistDriverMapper, never()).insert(any());
    }
}
