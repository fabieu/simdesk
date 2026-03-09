package de.sustineo.simdesk.services.stewarding;

import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.stewarding.PenaltyCatalog;
import de.sustineo.simdesk.entities.stewarding.PenaltyDefinition;
import de.sustineo.simdesk.mybatis.mapper.PenaltyCatalogMapper;
import de.sustineo.simdesk.mybatis.mapper.PenaltyDefinitionMapper;
import de.sustineo.simdesk.services.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Profile(SpringProfile.STEWARDING)
@Service
@RequiredArgsConstructor
public class PenaltyCatalogService {
    private final PenaltyCatalogMapper catalogMapper;
    private final PenaltyDefinitionMapper definitionMapper;
    private final IdGenerator idGenerator;

    public List<PenaltyCatalog> getAllCatalogs() {
        return catalogMapper.findAll();
    }

    public PenaltyCatalog getCatalogById(String id) {
        return catalogMapper.findById(id);
    }

    @Transactional
    public void createCatalog(PenaltyCatalog catalog) {
        catalog.setId(idGenerator.generateRandomString(12));
        catalogMapper.insert(catalog);
    }

    @Transactional
    public void updateCatalog(PenaltyCatalog catalog) {
        catalogMapper.update(catalog);
    }

    @Transactional
    public void deleteCatalog(String id) {
        catalogMapper.delete(id);
    }

    public List<PenaltyDefinition> getDefinitionsByCatalogId(String catalogId) {
        return definitionMapper.findByCatalogId(catalogId);
    }

    public List<PenaltyDefinition> getDefinitionsForSessionType(String catalogId, String sessionType) {
        return definitionMapper.findByCatalogIdAndSessionType(catalogId, sessionType);
    }

    @Transactional
    public void createDefinition(PenaltyDefinition definition) {
        definition.setId(idGenerator.generateRandomString(12));
        definitionMapper.insert(definition);
    }

    @Transactional
    public void updateDefinition(PenaltyDefinition definition) {
        definitionMapper.update(definition);
    }

    @Transactional
    public void deleteDefinition(String id) {
        definitionMapper.delete(id);
    }
}
