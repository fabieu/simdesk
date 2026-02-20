package de.sustineo.simdesk.services.stewarding;

import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.stewarding.ReasoningTemplate;
import de.sustineo.simdesk.mybatis.mapper.ReasoningTemplateMapper;
import de.sustineo.simdesk.services.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Profile(SpringProfile.STEWARDING)
@Service
@RequiredArgsConstructor
public class ReasoningTemplateService {
    private final ReasoningTemplateMapper templateMapper;
    private final IdGenerator idGenerator;

    public List<ReasoningTemplate> getAllTemplates() {
        return templateMapper.findAll();
    }

    public ReasoningTemplate getTemplateById(String id) {
        return templateMapper.findById(id);
    }

    public List<ReasoningTemplate> getTemplatesByCategory(String category) {
        return templateMapper.findByCategory(category);
    }

    @Transactional
    public void createTemplate(ReasoningTemplate template) {
        template.setId(idGenerator.generateRandomString(12));
        templateMapper.insert(template);
    }

    @Transactional
    public void updateTemplate(ReasoningTemplate template) {
        templateMapper.update(template);
    }

    @Transactional
    public void deleteTemplate(String id) {
        templateMapper.delete(id);
    }

    public String renderTemplate(String templateText, Map<String, String> variables) {
        String result = templateText;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }
}
