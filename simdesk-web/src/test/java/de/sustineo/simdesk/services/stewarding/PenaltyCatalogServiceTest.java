package de.sustineo.simdesk.services.stewarding;

import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.stewarding.PenaltyCatalog;
import de.sustineo.simdesk.entities.stewarding.PenaltyDefinition;
import de.sustineo.simdesk.entities.stewarding.PenaltySessionType;
import de.sustineo.simdesk.mybatis.mapper.PenaltyCatalogMapper;
import de.sustineo.simdesk.mybatis.mapper.PenaltyDefinitionMapper;
import de.sustineo.simdesk.services.IdGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ActiveProfiles({SpringProfile.STEWARDING})
@SpringBootTest(classes = {
        PenaltyCatalogService.class,
        SpringProfile.class
})
class PenaltyCatalogServiceTest {

    @Autowired
    private PenaltyCatalogService penaltyCatalogService;

    @MockitoBean
    private PenaltyCatalogMapper penaltyCatalogMapper;

    @MockitoBean
    private PenaltyDefinitionMapper penaltyDefinitionMapper;

    @MockitoBean
    private IdGenerator idGenerator;

    @Test
    void getDefinitionsForSessionType_shouldReturnMatchingDefinitions() {
        PenaltyDefinition racePenalty = PenaltyDefinition.builder()
                .id("def123456789")
                .catalogId("cat123456789")
                .code("PEN-001")
                .name("Causing a collision")
                .sessionType(PenaltySessionType.RACE)
                .defaultPenalty("5 second time penalty")
                .build();

        when(penaltyDefinitionMapper.findByCatalogIdAndSessionType("cat123456789", "RACE"))
                .thenReturn(List.of(racePenalty));

        List<PenaltyDefinition> result = penaltyCatalogService.getDefinitionsForSessionType("cat123456789", "RACE");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Causing a collision");
    }

    @Test
    void getAllCatalogs_shouldReturnAll() {
        PenaltyCatalog catalog = PenaltyCatalog.builder()
                .id("cat123456789")
                .name("2025 Season Rules")
                .description("Standard penalty rules for 2025")
                .build();

        when(penaltyCatalogMapper.findAll()).thenReturn(List.of(catalog));

        List<PenaltyCatalog> result = penaltyCatalogService.getAllCatalogs();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("2025 Season Rules");
    }
}
