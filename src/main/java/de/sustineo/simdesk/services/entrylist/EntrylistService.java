package de.sustineo.simdesk.services.entrylist;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.json.kunos.acc.AccEntrylist;
import de.sustineo.simdesk.entities.json.kunos.acc.AccEntrylistEntry;
import de.sustineo.simdesk.entities.json.kunos.acc.AccDriver;
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
    public ValidationData validateRules(AccEntrylist entrylist, Set<ValidationRule> rules) {
        return validate(entrylist, rules);
    }

    @SuppressWarnings("unchecked")
    private ValidationData validate(AccEntrylist entrylist, Set<ValidationRule> rules) {
        List<ValidationError> errors = new ArrayList<>();

        for (ValidationRule validationRule : rules) {
            try {
                Method method = EntrylistService.class.getDeclaredMethod(validationRule.getMethodName(), AccEntrylist.class, ValidationRule.class);
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
    protected static List<ValidationError> validateDriverNames(AccEntrylist entrylist, ValidationRule validationRule) {
        List<ValidationError> errors = new ArrayList<>();

        List<AccEntrylistEntry> entries = entrylist.getEntries();
        for (AccEntrylistEntry entry : entries) {
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
    protected static List<ValidationError> validateDriverCategories(AccEntrylist entrylist, ValidationRule validationRule) {
        List<ValidationError> errors = new ArrayList<>();

        List<AccEntrylistEntry> entries = entrylist.getEntries();
        for (AccEntrylistEntry entry : entries) {
            List<AccDriver> drivers = entry.getDrivers();

            if (entry.getOverrideDriverInfo() == 1) {
                for (AccDriver driver : drivers) {
                    if (driver.getDriverCategory() == null) {
                        String message = String.format("driverCategory of %s for car number #%s is missing", driver, entry.getRaceNumber());
                        errors.add(new ValidationError(validationRule, message, driver));
                    }
                }
            }
        }

        return errors;
    }

    @SuppressWarnings("unused")
    protected static List<ValidationError> validateSteamIDs(AccEntrylist entrylist, ValidationRule validationRule) {
        List<ValidationError> errors = new ArrayList<>();
        HashMap<String, AccDriver> seenSteamIds = new HashMap<>();

        List<AccEntrylistEntry> entries = entrylist.getEntries();
        for (AccEntrylistEntry entry : entries) {
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
    protected static List<ValidationError> validateRaceNumbers(AccEntrylist entrylist, ValidationRule validationRule) {
        List<ValidationError> errors = new ArrayList<>();
        HashMap<Integer, AccEntrylistEntry> seenRaceNumbers = new HashMap<>();

        List<AccEntrylistEntry> entries = entrylist.getEntries();
        for (AccEntrylistEntry entry : entries) {
            Integer raceNumber = entry.getRaceNumber();

            if (raceNumber == null || raceNumber < 1 || raceNumber > 998) {
                String message = String.format("RaceNumber of %s is missing or not between 1 and 998", raceNumber);
                errors.add(new ValidationError(validationRule, message, entry));
            }

            if (seenRaceNumbers.containsKey(raceNumber)) {
                AccEntrylistEntry seenEntry = seenRaceNumbers.get(raceNumber);
                String message = String.format("The following entries have the same raceNumber #%s", raceNumber);
                errors.add(new ValidationError(validationRule, message, List.of(seenEntry, entry)));
            } else {
                seenRaceNumbers.put(raceNumber, entry);
            }
        }

        return errors;
    }

    @SuppressWarnings("unused")
    protected static List<ValidationError> validateGridPositions(AccEntrylist entrylist, ValidationRule validationRule) {
        List<ValidationError> errors = new ArrayList<>();
        HashMap<Integer, AccEntrylistEntry> seenGridPositions = new HashMap<>();

        List<AccEntrylistEntry> entries = entrylist.getEntries();
        for (AccEntrylistEntry entry : entries) {
            Integer defaultGridPosition = entry.getDefaultGridPosition();

            if (defaultGridPosition != null && defaultGridPosition > 0) {
                if (seenGridPositions.containsKey(defaultGridPosition)) {
                    AccEntrylistEntry seenEntry = seenGridPositions.get(defaultGridPosition);
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
    protected static List<ValidationError> validateBallast(AccEntrylist entrylist, ValidationRule validationRule) {
        List<ValidationError> errors = new ArrayList<>();

        List<AccEntrylistEntry> entries = entrylist.getEntries();
        for (AccEntrylistEntry entry : entries) {
            Integer ballastKg = entry.getBallastKg();

            if (ballastKg == null || ballastKg < 0 || ballastKg > 100) {
                String message = String.format("Invalid ballast value %s kg for car number #%s", ballastKg, entry.getRaceNumber());
                errors.add(new ValidationError(validationRule, message, entry));
            }
        }

        return errors;
    }

    @SuppressWarnings("unused")
    protected static List<ValidationError> validateRestrictor(AccEntrylist entrylist, ValidationRule validationRule) {
        List<ValidationError> errors = new ArrayList<>();

        List<AccEntrylistEntry> entries = entrylist.getEntries();
        for (AccEntrylistEntry entry : entries) {
            Integer restrictor = entry.getRestrictor();

            if (restrictor == null || restrictor < 0 || restrictor > 20) {
                String message = String.format("Invalid restrictor value %s for car number #%s", restrictor, entry.getRaceNumber());
                errors.add(new ValidationError(validationRule, message, entry));
            }
        }

        return errors;
    }
}
