package de.sustineo.simdesk.services.stewarding;

import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.entities.stewarding.Series;
import de.sustineo.simdesk.mybatis.mapper.PenaltyCatalogMapper;
import de.sustineo.simdesk.mybatis.mapper.SeriesMapper;
import de.sustineo.simdesk.services.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Profile(SpringProfile.STEWARDING)
@Service
@RequiredArgsConstructor
public class SeriesService {
    private final SeriesMapper seriesMapper;
    private final PenaltyCatalogMapper catalogMapper;
    private final IdGenerator idGenerator;

    public List<Series> getAllSeries() {
        return seriesMapper.findAll();
    }

    public Series getSeriesById(String id) {
        Series series = seriesMapper.findById(id);
        if (series != null && series.getPenaltyCatalogId() != null) {
            series.setPenaltyCatalog(catalogMapper.findById(series.getPenaltyCatalogId()));
        }
        return series;
    }

    @Transactional
    public void createSeries(Series series) {
        series.setId(idGenerator.generateRandomString(12));
        seriesMapper.insert(series);
    }

    @Transactional
    public void updateSeries(Series series) {
        seriesMapper.update(series);
    }

    @Transactional
    public void deleteSeries(String id) {
        seriesMapper.delete(id);
    }
}
