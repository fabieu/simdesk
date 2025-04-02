## Description

The `acc-entrylist` feature enables the Entrylist Editor to manipulate Assetto Corsa Competizione `entrylist.json` files
and validate them for syntax errors and inconsistencies.

**View Demo**: [Entrylist Editor](https://simdesk.eu/entrylist/editor)

Enable the leaderboard feature by adding `acc-entrylist` to `SPRING_PROFILES_ACTIVE`.

## Entrylist Editor

### Import results

The Entrylist Editor allows you to update the grid positions based on result files from previous sessions. You can
optionally define a grid start position, which will be used as the starting point for the actual grid positions.
The car number (`raceNumber`) will be used to match the results with the entrylist entries, so make sure that the car
numbers are identical.

## Load custom cars
Specify custom car definitions using an external file, ensuring they follow the required format (detailed below). 
The custom car definitions will be used to override the `customCar` and `overrideCarModelForCustomCar` properties in the entrylist file.
```json
[
  {
    "carNumber": 1,
    "customCar": "SIMDESK_1.json",
    "overrideCarModelForCustomCar": false
  },
  {
    "carNumber": 2,
    "customCar": "SIMDESK_2.json",
    "overrideCarModelForCustomCar": true
  }
]
```


### Reverse grid positions

When grid positions are defined, you can choose to reverse the grid positions. This is useful for reverse grid
scenarios.

### Validation Rules

The following validation rules are available and can be toggled on or off:

- **Invalid race numbers:** Check if race numbers are valid, also check for duplicate race numbers.
- **Invalid SteamIDs:** Check if SteamIDs are present, also check for duplicate SteamIDs.
- **Invalid grid positions:** Check if grid positions are valid, also check for duplicate grid positions.
- **Invalid ballast values:** Check if ballast values are inside the allowed threshold.
- **Invalid restrictor values:** Check if restrictor values are inside the allowed threshold.
- **Missing driver names:** Check if firstName, lastName and shortName are set if `overrideDriverInfo` is set to `1` in
  the entrylist file.
- **Invalid driver categories:** Check if driver categories are valid if `overrideDriverInfo` is set to `1` in the
  entrylist file.
