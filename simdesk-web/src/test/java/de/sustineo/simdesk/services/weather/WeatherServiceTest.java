package de.sustineo.simdesk.services.weather;

import de.sustineo.simdesk.configuration.SpringProfile;
import de.sustineo.simdesk.configuration.TestRestClientConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({SpringProfile.MAP})
@SpringBootTest(classes = {
        WeatherService.class
})
@Import(TestRestClientConfiguration.class)
class WeatherServiceTest {
    @Autowired
    private WeatherService weatherService;

    @Test
    public void testNoRain() {
        // No rain, should return 0.0
        assertThat(weatherService.convertRainIntensityToPercentage(0.0)).isEqualTo(0.0);
        assertThat(weatherService.convertRainIntensityToPercentage(0.4)).isEqualTo(0.0);
    }

    /**
     * Test for weak rain (intensity between 0.5 mm/h and 2 mm/h).
     */
    @Test
    public void testWeakRain() {
        assertThat(weatherService.convertRainIntensityToPercentage(0.5)).isEqualTo(0.02);
        assertThat(weatherService.convertRainIntensityToPercentage(1.0)).isEqualTo(0.03);
        assertThat(weatherService.convertRainIntensityToPercentage(2.0)).isEqualTo(0.07);
    }

    /**
     * Test for moderate rain (intensity between 2 mm/h and 6 mm/h).
     */
    @Test
    public void testModerateRain() {
        assertThat(weatherService.convertRainIntensityToPercentage(2.5)).isEqualTo(0.09);
        assertThat(weatherService.convertRainIntensityToPercentage(4.0)).isEqualTo(0.14);
        assertThat(weatherService.convertRainIntensityToPercentage(6.0)).isEqualTo(0.2);
    }

    /**
     * Test for heavy rain (intensity between 6 mm/h and 10 mm/h).
     */
    @Test
    public void testHeavyRain() {
        assertThat(weatherService.convertRainIntensityToPercentage(6.5)).isEqualTo(0.22);
        assertThat(weatherService.convertRainIntensityToPercentage(8.0)).isEqualTo(0.27);
        assertThat(weatherService.convertRainIntensityToPercentage(10.0)).isEqualTo(0.33);
    }

    /**
     * Test for very heavy rain (intensity between 10 mm/h and 18 mm/h).
     */
    @Test
    public void testVeryHeavyRain() {
        assertThat(weatherService.convertRainIntensityToPercentage(10.0)).isEqualTo(0.33);
        assertThat(weatherService.convertRainIntensityToPercentage(14.0)).isEqualTo(0.47);
        assertThat(weatherService.convertRainIntensityToPercentage(18.0)).isEqualTo(0.6);
    }

    /**
     * Test for shower (intensity between 18 mm/h and 30 mm/h).
     */
    @Test
    public void testShower() {
        assertThat(weatherService.convertRainIntensityToPercentage(18.0)).isEqualTo(0.6);
        assertThat(weatherService.convertRainIntensityToPercentage(25.0)).isEqualTo(0.83);
        assertThat(weatherService.convertRainIntensityToPercentage(30.0)).isEqualTo(1.0);
    }

    /**
     * Test for cloudburst (> 30 mm/h).
     */
    @Test
    public void testCloudburst() {
        assertThat(weatherService.convertRainIntensityToPercentage(35.0)).isEqualTo(1.0);
        assertThat(weatherService.convertRainIntensityToPercentage(50.0)).isEqualTo(1.0);
    }

    @Test
    public void calculateVarianceWithEmptyList() {
        assertThat(weatherService.calculateVariance(new ArrayList<>())).isEqualTo(0.0);
    }

    @Test
    public void calculateVarianceWithSingleValue() {
        List<Double> values = List.of(1.0);
        assertThat(weatherService.calculateVariance(values)).isEqualTo(0.0);
    }

    @Test
    public void calculateVarianceWithMaximumVariancePair() {
        List<Double> values = List.of(0.0, 1.0);
        assertThat(weatherService.calculateVariance(values)).isEqualTo(0.25);
    }

    @Test
    public void calculateVarianceWithMaximumVarianceList() {
        List<Double> values = List.of(0.0, 1.0, 0.0, 1.0, 0.0, 1.0);
        assertThat(weatherService.calculateVariance(values)).isEqualTo(0.25);
    }

    @Test
    public void calculateVarianceWithIdenticalValues() {
        List<Double> values = List.of(0.45, 0.45, 0.45);
        assertThat(weatherService.calculateVariance(values)).isEqualTo(0.0);
    }

    @Test
    public void calculateVarianceWithRandomValues() {
        List<Double> values = List.of(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0);
        assertThat(weatherService.calculateVariance(values)).isEqualTo(0.0825);
    }
}