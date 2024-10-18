package de.sustineo.simdesk.services.entrylist;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.entrylist.Entry;
import de.sustineo.simdesk.entities.entrylist.Entrylist;
import de.sustineo.simdesk.entities.json.kunos.AccDriver;
import de.sustineo.simdesk.entities.validation.ValidationData;
import de.sustineo.simdesk.entities.validation.ValidationError;
import de.sustineo.simdesk.entities.validation.ValidationRule;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

@Profile(ProfileManager.PROFILE_ENTRYLIST)
@Log
@Service
public class EntrylistService {
    public ValidationData validateRules(Entrylist entrylist, Set<ValidationRule> rules) {
        return validate(entrylist, rules);
    }

    @SuppressWarnings("unchecked")
    private ValidationData validate(Entrylist entrylist, Set<ValidationRule> rules) {
        List<ValidationError> errors = new ArrayList<>();

        for (ValidationRule validationRule : rules) {
            try {
                Method method = EntrylistService.class.getDeclaredMethod(validationRule.getMethodName(), Entrylist.class, ValidationRule.class);
                List<ValidationError> validationErrors = (List<ValidationError>) method.invoke(method, entrylist, validationRule);

                errors.addAll(validationErrors);
            } catch (NoSuchMethodException e) {
                log.log(Level.SEVERE, "Missing method for validation rule " + validationRule.name(), e);
            } catch (InvocationTargetException | IllegalAccessException e) {
                log.log(Level.SEVERE, "Error calling method for validation rule " + validationRule.name(), e);
            }
        }

        return new ValidationData(rules, errors);
    }

    @SuppressWarnings("unused")
    protected static List<ValidationError> validateDriverNames(Entrylist entrylist, ValidationRule validationRule) {
        List<ValidationError> errors = new ArrayList<>();

        List<Entry> entries = entrylist.getEntries();
        for (Entry entry : entries) {
            List<AccDriver> drivers = entry.getDrivers();

            if (entry.getOverrideDriverInfo() == 1) {
                for (AccDriver driver : drivers) {
                    if (StringUtils.isBlank(driver.getFirstName())) {
                        String message = String.format("firstName of %s for car number #%s is missing or empty", driver, entry.getRaceNumber());
                        errors.add(new ValidationError(validationRule, message, driver));
                    }

                    if (StringUtils.isBlank(driver.getLastName())) {
                        String message = String.format("lastName of %s for car number #%s is missing or empty", driver, entry.getRaceNumber());
                        errors.add(new ValidationError(validationRule, message, driver));
                    }

                    if (StringUtils.isBlank(driver.getShortName())) {
                        String message = String.format("shortName of %s for car number #%s is missing or empty", driver, entry.getRaceNumber());
                        errors.add(new ValidationError(validationRule, message, driver));
                    }
                }
            }
        }

        return errors;
    }

    @SuppressWarnings("unused")
    protected static List<ValidationError> validateDriverCategories(Entrylist entrylist, ValidationRule validationRule) {
        List<ValidationError> errors = new ArrayList<>();

        List<Entry> entries = entrylist.getEntries();
        for (Entry entry : entries) {
            List<AccDriver> drivers = entry.getDrivers();

            if (entry.getOverrideDriverInfo() == 1) {
                for (AccDriver driver : drivers) {
                    if (!Set.of(0, 1, 2, 3).contains(driver.getDriverCategory())) {
                        String message = String.format("driverCategory of %s for car number #%s has to be one of [0, 1, 2, 3]", driver, entry.getRaceNumber());
                        errors.add(new ValidationError(validationRule, message, driver));
                    }
                }
            }
        }

        return errors;
    }

    @SuppressWarnings("unused")
    protected static List<ValidationError> validateSteamIDs(Entrylist entrylist, ValidationRule validationRule) {
        List<ValidationError> errors = new ArrayList<>();
        HashMap<String, AccDriver> seenSteamIds = new HashMap<>();

        List<Entry> entries = entrylist.getEntries();
        for (Entry entry : entries) {
            List<AccDriver> drivers = entry.getDrivers();

            for (AccDriver driver : drivers) {
                if (StringUtils.isBlank(driver.getPlayerId()) || !driver.getPlayerId().startsWith("S") || driver.getPlayerId().length() != 18) {
                    String message = String.format("steamID of %s for car number #%s is missing or invalid", driver, entry.getRaceNumber());
                    errors.add(new ValidationError(validationRule, message, driver));
                }

                if (seenSteamIds.containsKey(driver.getPlayerId())) {
                    AccDriver seenDriver = seenSteamIds.get(driver.getPlayerId());
                    String message = String.format("%s and %s have the same SteamID", seenDriver, driver);
                    errors.add(new ValidationError(validationRule, message, List.of(seenDriver, driver)));
                } else {
                    seenSteamIds.put(driver.getPlayerId(), driver);
                }
            }
        }

        return errors;
    }

    @SuppressWarnings("unused")
    protected static List<ValidationError> validateRaceNumbers(Entrylist entrylist, ValidationRule validationRule) {
        List<ValidationError> errors = new ArrayList<>();
        HashMap<Integer, Entry> seenRaceNumbers = new HashMap<>();

        List<Entry> entries = entrylist.getEntries();
        for (Entry entry : entries) {
            Integer raceNumber = entry.getRaceNumber();

            if (raceNumber == null || raceNumber < 1 || raceNumber > 999) {
                String message = String.format("RaceNumber of %s is missing or not between 1 and 999", raceNumber);
                errors.add(new ValidationError(validationRule, message, entry));
            }

            if (seenRaceNumbers.containsKey(raceNumber)) {
                Entry seenEntry = seenRaceNumbers.get(raceNumber);
                String message = String.format("The following entries have the same raceNumber #%s", raceNumber);
                errors.add(new ValidationError(validationRule, message, List.of(seenEntry, entry)));
            } else {
                seenRaceNumbers.put(raceNumber, entry);
            }
        }

        return errors;
    }

    @SuppressWarnings("unused")
    protected static List<ValidationError> validateGridPositions(Entrylist entrylist, ValidationRule validationRule) {
        List<ValidationError> errors = new ArrayList<>();
        HashMap<Integer, Entry> seenGridPositions = new HashMap<>();

        List<Entry> entries = entrylist.getEntries();
        for (Entry entry : entries) {
            Integer defaultGridPosition = entry.getDefaultGridPosition();

            if (defaultGridPosition != null && defaultGridPosition > 0) {
                if (seenGridPositions.containsKey(defaultGridPosition)) {
                    Entry seenEntry = seenGridPositions.get(defaultGridPosition);
                    String message = String.format("The following raceNumbers have the same grid position %s - #%s and #%s", defaultGridPosition, seenEntry.getRaceNumber(), entry.getRaceNumber());
                    errors.add(new ValidationError(validationRule, message, List.of(seenEntry, entry)));
                } else {
                    seenGridPositions.put(defaultGridPosition, entry);
                }
            }
        }

        return errors;
    }

    @SuppressWarnings("unused")
    protected static List<ValidationError> validateBallast(Entrylist entrylist, ValidationRule validationRule) {
        List<ValidationError> errors = new ArrayList<>();

        List<Entry> entries = entrylist.getEntries();
        for (Entry entry : entries) {
            Integer ballastKg = entry.getBallastKg();

            if (ballastKg == null || ballastKg < 0 || ballastKg > 100) {
                String message = String.format("Invalid ballast value %s kg for car number #%s", ballastKg, entry.getRaceNumber());
                errors.add(new ValidationError(validationRule, message, entry));
            }
        }

        return errors;
    }

    @SuppressWarnings("unused")
    protected static List<ValidationError> validateRestrictor(Entrylist entrylist, ValidationRule validationRule) {
        List<ValidationError> errors = new ArrayList<>();

        List<Entry> entries = entrylist.getEntries();
        for (Entry entry : entries) {
            Integer restrictor = entry.getRestrictor();

            if (restrictor == null || restrictor < 0 || restrictor > 20) {
                String message = String.format("Invalid restrictor value %s for car number #%s", restrictor, entry.getRaceNumber());
                errors.add(new ValidationError(validationRule, message, entry));
            }
        }

        return errors;
    }
}
