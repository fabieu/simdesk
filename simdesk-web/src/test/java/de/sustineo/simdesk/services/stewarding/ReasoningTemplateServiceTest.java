package de.sustineo.simdesk.services.stewarding;

import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.stewarding.ReasoningTemplate;
import de.sustineo.simdesk.mybatis.mapper.ReasoningTemplateMapper;
import de.sustineo.simdesk.services.IdGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ActiveProfiles({SpringProfile.STEWARDING})
@SpringBootTest(classes = {
        ReasoningTemplateService.class,
        SpringProfile.class
})
class ReasoningTemplateServiceTest {

    @Autowired
    private ReasoningTemplateService reasoningTemplateService;

    @MockitoBean
    private ReasoningTemplateMapper reasoningTemplateMapper;

    @MockitoBean
    private IdGenerator idGenerator;

    @Test
    void renderTemplate_shouldReplacePlaceholders() {
        String template = "Car {car_a} made contact with Car {car_b} at {location}.";
        Map<String, String> variables = Map.of(
                "car_a", "#001",
                "car_b", "#042",
                "location", "Turn 5"
        );

        String result = reasoningTemplateService.renderTemplate(template, variables);

        assertThat(result).isEqualTo("Car #001 made contact with Car #042 at Turn 5.");
    }

    @Test
    void renderTemplate_shouldKeepUnmatchedPlaceholders() {
        String template = "Car {car_a} was penalized for {reason}.";
        Map<String, String> variables = Map.of("car_a", "#001");

        String result = reasoningTemplateService.renderTemplate(template, variables);

        assertThat(result).isEqualTo("Car #001 was penalized for {reason}.");
    }

    @Test
    void renderTemplate_shouldHandleEmptyVariables() {
        String template = "No placeholders here.";
        Map<String, String> variables = Map.of();

        String result = reasoningTemplateService.renderTemplate(template, variables);

        assertThat(result).isEqualTo("No placeholders here.");
    }

    @Test
    void getAllTemplates_shouldReturnAll() {
        ReasoningTemplate template = ReasoningTemplate.builder()
                .id("tmpl12345678")
                .name("Test Template")
                .category("Contact")
                .templateText("Template text")
                .sortOrder(0)
                .build();

        when(reasoningTemplateMapper.findAll()).thenReturn(List.of(template));

        List<ReasoningTemplate> result = reasoningTemplateService.getAllTemplates();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Test Template");
    }

    @Test
    void getTemplatesByCategory_shouldFilterByCategory() {
        ReasoningTemplate template = ReasoningTemplate.builder()
                .id("tmpl12345678")
                .name("Contact Template")
                .category("Contact")
                .templateText("Contact template text")
                .sortOrder(0)
                .build();

        when(reasoningTemplateMapper.findByCategory("Contact")).thenReturn(List.of(template));

        List<ReasoningTemplate> result = reasoningTemplateService.getTemplatesByCategory("Contact");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCategory()).isEqualTo("Contact");
    }
}
