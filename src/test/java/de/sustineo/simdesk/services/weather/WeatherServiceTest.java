package de.sustineo.simdesk.services.weather;

import de.sustineo.simdesk.configuration.ProfileManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
@ActiveProfiles(ProfileManager.PROFILE_MAP)
@SpringBootTest
class WeatherServiceTest {
    @Autowired
    private WeatherService weatherService;

    /**
     * Test for the no rain case (intensity < 0.5 mm/h).
     */
    @Test
    public void testNoRain() {
        // No rain, should return 0.0
        assertEquals(0.0, weatherService.convertRainIntensityToPercentage(0.0));
        assertEquals(0.0, weatherService.convertRainIntensityToPercentage(0.4));
    }

    /**
     * Test for weak rain (intensity between 0.5 mm/h and 2 mm/h).
     */
    @Test
    public void testWeakRain() {
        // For intensity of 0.5 mm/h, result should be 0.02 (2%)
        assertEquals(0.02, weatherService.convertRainIntensityToPercentage(0.5));
        // For intensity of 1.0 mm/h, it should be closer to the middle of the range
        assertEquals(0.0667, weatherService.convertRainIntensityToPercentage(1.0), 0.0001);
        // For intensity of 2.0 mm/h, it should be 0.2 (20%)
        assertEquals(0.2, weatherService.convertRainIntensityToPercentage(2.0));
    }

    /**
     * Test for moderate rain (intensity between 2 mm/h and 6 mm/h).
     */
    @Test
    public void testModerateRain() {
        // For intensity of 2.5 mm/h
        assertEquals(0.1, weatherService.convertRainIntensityToPercentage(2.5));
        // For intensity of 4.0 mm/h
        assertEquals(0.175, weatherService.convertRainIntensityToPercentage(4.0));
        // For intensity of 6.0 mm/h
        assertEquals(0.33, weatherService.convertRainIntensityToPercentage(6.0), 0.0001);
    }

    /**
     * Test for heavy rain (intensity between 6 mm/h and 10 mm/h).
     */
    @Test
    public void testHeavyRain() {
        // For intensity of 6.5 mm/h
        assertEquals(0.375, weatherService.convertRainIntensityToPercentage(6.5));
        // For intensity of 8.0 mm/h
        assertEquals(0.475, weatherService.convertRainIntensityToPercentage(8.0));
        // For intensity of 10.0 mm/h
        assertEquals(0.6, weatherService.convertRainIntensityToPercentage(10.0));
    }

    /**
     * Test for very heavy rain (intensity between 10 mm/h and 18 mm/h).
     */
    @Test
    public void testVeryHeavyRain() {
        // For intensity of 10.0 mm/h
        assertEquals(0.6, weatherService.convertRainIntensityToPercentage(10.0));
        // For intensity of 14.0 mm/h
        assertEquals(0.7875, weatherService.convertRainIntensityToPercentage(14.0));
        // For intensity of 18.0 mm/h
        assertEquals(1.0, weatherService.convertRainIntensityToPercentage(18.0));
    }

    /**
     * Test for shower (intensity between 18 mm/h and 30 mm/h).
     */
    @Test
    public void testShower() {
        // For intensity of 18.0 mm/h
        assertEquals(0.6, weatherService.convertRainIntensityToPercentage(18.0));
        // For intensity of 25.0 mm/h
        assertEquals(0.9167, weatherService.convertRainIntensityToPercentage(25.0), 0.0001);
        // For intensity of 30.0 mm/h
        assertEquals(1.0, weatherService.convertRainIntensityToPercentage(30.0));
    }

    /**
     * Test for cloudburst (> 30 mm/h).
     */
    @Test
    public void testCloudburst() {
        // Anything above 30 mm/h should return 1.0 (100%)
        assertEquals(1.0, weatherService.convertRainIntensityToPercentage(35.0));
        assertEquals(1.0, weatherService.convertRainIntensityToPercentage(50.0));
    }

}