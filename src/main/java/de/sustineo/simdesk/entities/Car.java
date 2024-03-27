package de.sustineo.simdesk.entities;

import de.sustineo.simdesk.entities.enums.CarGroup;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class Car extends Entity {
    private static final HashMap<Integer, Car> carModels = new HashMap<>();
    private final Integer carId;
    private final String carName;
    private final CarGroup carGroup;

    static {
        addCarModel(0, "Porsche 991 GT3 R", CarGroup.GT3);
        addCarModel(1, "Mercedes-AMG GT3", CarGroup.GT3);
        addCarModel(2, "Ferrari 488 GT3", CarGroup.GT3);
        addCarModel(3, "Audi R8 LMS", CarGroup.GT3);
        addCarModel(4, "Lamborghini Huracan GT3", CarGroup.GT3);
        addCarModel(5, "McLaren 650S GT3", CarGroup.GT3);
        addCarModel(6, "Nissan GT-R Nismo GT3 2018", CarGroup.GT3);
        addCarModel(7, "BMW M6 GT3", CarGroup.GT3);
        addCarModel(8, "Bentley Continental GT3 2018", CarGroup.GT3);
        addCarModel(9, "Porsche 991II GT3 Cup", CarGroup.CUP);
        addCarModel(10, "Nissan GT-R Nismo GT3 2017", CarGroup.GT3);
        addCarModel(11, "Bentley Continental GT3 2016", CarGroup.GT3);
        addCarModel(12, "Aston Martin V12 Vantage GT3", CarGroup.GT3);
        addCarModel(13, "Lamborghini Gallardo R-EX", CarGroup.GT3);
        addCarModel(14, "Jaguar G3", CarGroup.GT3);
        addCarModel(15, "Lexus RC F GT3", CarGroup.GT3);
        addCarModel(16, "Lamborghini Huracan Evo (2019)", CarGroup.GT3);
        addCarModel(17, "Honda NSX GT3", CarGroup.GT3);
        addCarModel(18, "Lamborghini Huracan SuperTrofeo", CarGroup.ST);
        addCarModel(19, "Audi R8 LMS Evo (2019)", CarGroup.GT3);
        addCarModel(20, "AMR V8 Vantage (2019)", CarGroup.GT3);
        addCarModel(21, "Honda NSX Evo (2019)", CarGroup.GT3);
        addCarModel(22, "McLaren 720S GT3 (2019)", CarGroup.GT3);
        addCarModel(23, "Porsche 911II GT3 R (2019)", CarGroup.GT3);
        addCarModel(24, "Ferrari 488 GT3 Evo 2020", CarGroup.GT3);
        addCarModel(25, "Mercedes-AMG GT3 2020", CarGroup.GT3);
        addCarModel(26, "Ferrari 488 Challenge Evo", CarGroup.CHL);
        addCarModel(27, "BMW M2 CS Racing", CarGroup.TCX);
        addCarModel(28, "Porsche 911 GT3 Cup (Type 992)", CarGroup.CUP);
        addCarModel(29, "Lamborghini Huracán Super Trofeo EVO2", CarGroup.ST);
        addCarModel(30, "BMW M4 GT3", CarGroup.GT3);
        addCarModel(31, "Audi R8 LMS GT3 evo II", CarGroup.GT3);
        addCarModel(32, "Ferrari 296 GT3", CarGroup.GT3);
        addCarModel(33, "Lamborghini Huracan Evo2", CarGroup.GT3);
        addCarModel(34, "Porsche 992 GT3 R", CarGroup.GT3);
        addCarModel(35, "McLaren 720S GT3 Evo 2023", CarGroup.GT3);
        addCarModel(50, "Alpine A110 GT4", CarGroup.GT4);
        addCarModel(51, "AMR V8 Vantage GT4", CarGroup.GT4);
        addCarModel(52, "Audi R8 LMS GT4", CarGroup.GT4);
        addCarModel(53, "BMW M4 GT4", CarGroup.GT4);
        addCarModel(55, "Chevrolet Camaro GT4", CarGroup.GT4);
        addCarModel(56, "Ginetta G55 GT4", CarGroup.GT4);
        addCarModel(57, "KTM X-Bow GT4", CarGroup.GT4);
        addCarModel(58, "Maserati MC GT4", CarGroup.GT4);
        addCarModel(59, "McLaren 570S GT4", CarGroup.GT4);
        addCarModel(60, "Mercedes-AMG GT4", CarGroup.GT4);
        addCarModel(61, "Porsche 718 Cayman GT4", CarGroup.GT4);
        addCarModel(80, "Audi R8 LMS GT2", CarGroup.GT2);
        addCarModel(82, "KTM XBOW GT2", CarGroup.GT2);
        addCarModel(83, "Maserati MC20 GT2", CarGroup.GT2);
        addCarModel(84, "Mercedes AMG GT2", CarGroup.GT2);
        addCarModel(85, "Porsche 911 GT2 RS CS Evo", CarGroup.GT2);
        addCarModel(86, "Porsche 935", CarGroup.GT2);
    }

    public Car(Integer carId, String carName, CarGroup carGroup) {
        this.carId = carId;
        this.carName = carName;
        this.carGroup = carGroup;
    }

    public Car(Integer carId, String carName) {
        this(carId, carName, CarGroup.UNKNOWN);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return Objects.equals(carId, car.carId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carId);
    }

    private static void addCarModel(int carId, String carName, CarGroup carGroup) {
        carModels.put(carId, new Car(carId, carName, carGroup));
    }

    public static Car getCarById(Integer carId) {
        return carModels.get(carId);
    }

    public static String getCarNameById(Integer carId) {
        return Optional.ofNullable(carModels.get(carId))
                .map(Car::getCarName)
                .orElse(UNKNOWN);
    }

    public static Integer getCarIdByName(String carName) {
        return carModels.values().stream()
                .filter(car -> car.getCarName().equals(carName))
                .map(Car::getCarId)
                .findFirst()
                .orElse(null);
    }

    public static CarGroup getCarGroupById(Integer carId) {
        return Optional.ofNullable(carModels.get(carId))
                .map(Car::getCarGroup)
                .orElse(CarGroup.UNKNOWN);
    }

    public static List<Car> getAllSortedByName() {
        return carModels.values().stream()
                .sorted(Comparator.comparing(Car::getCarName))
                .collect(Collectors.toList());
    }
}
