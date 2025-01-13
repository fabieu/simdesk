package de.sustineo.simdesk.entities;

import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class Car extends Model {
    private static final HashMap<Integer, Car> carModels = new HashMap<>();
    private final Integer modelId;
    private final String name;
    private final CarGroup group;

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
        addCarModel(9, "Porsche 991II GT3 Cup", CarGroup.GTC);
        addCarModel(10, "Nissan GT-R Nismo GT3 2017", CarGroup.GT3);
        addCarModel(11, "Bentley Continental GT3 2016", CarGroup.GT3);
        addCarModel(12, "Aston Martin V12 Vantage GT3", CarGroup.GT3);
        addCarModel(13, "Lamborghini Gallardo R-EX", CarGroup.GT3);
        addCarModel(14, "Jaguar G3", CarGroup.GT3);
        addCarModel(15, "Lexus RC F GT3", CarGroup.GT3);
        addCarModel(16, "Lamborghini Huracan Evo (2019)", CarGroup.GT3);
        addCarModel(17, "Honda NSX GT3", CarGroup.GT3);
        addCarModel(18, "Lamborghini Huracan SuperTrofeo", CarGroup.GT2);
        addCarModel(19, "Audi R8 LMS Evo (2019)", CarGroup.GT3);
        addCarModel(20, "AMR V8 Vantage (2019)", CarGroup.GT3);
        addCarModel(21, "Honda NSX Evo (2019)", CarGroup.GT3);
        addCarModel(22, "McLaren 720S GT3 (2019)", CarGroup.GT3);
        addCarModel(23, "Porsche 911II GT3 R (2019)", CarGroup.GT3);
        addCarModel(24, "Ferrari 488 GT3 Evo 2020", CarGroup.GT3);
        addCarModel(25, "Mercedes-AMG GT3 2020", CarGroup.GT3);
        addCarModel(26, "Ferrari 488 Challenge Evo", CarGroup.GT2);
        addCarModel(27, "BMW M2 CS Racing", CarGroup.TCX);
        addCarModel(28, "Porsche 911 GT3 Cup (Type 992)", CarGroup.GTC);
        addCarModel(29, "Lamborghini Huracán Super Trofeo EVO2", CarGroup.GT2);
        addCarModel(30, "BMW M4 GT3", CarGroup.GT3);
        addCarModel(31, "Audi R8 LMS GT3 evo II", CarGroup.GT3);
        addCarModel(32, "Ferrari 296 GT3", CarGroup.GT3);
        addCarModel(33, "Lamborghini Huracan Evo2", CarGroup.GT3);
        addCarModel(34, "Porsche 992 GT3 R", CarGroup.GT3);
        addCarModel(35, "McLaren 720S GT3 Evo 2023", CarGroup.GT3);
        addCarModel(36, "Ford Mustang GT3", CarGroup.GT3);
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

    public Car(Integer modelId, String name, CarGroup group) {
        this.modelId = modelId;
        this.name = name;
        this.group = group;
    }

    public Car(Integer modelId, String name) {
        this(modelId, name, CarGroup.UNKNOWN);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return Objects.equals(modelId, car.modelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelId);
    }

    private static void addCarModel(int modelId, String carName, CarGroup group) {
        carModels.put(modelId, new Car(modelId, carName, group));
    }

    public static Car getCarById(Integer carModelId) {
        return carModels.get(carModelId);
    }

    public static String getNameById(Integer modelId) {
        return Optional.ofNullable(carModels.get(modelId))
                .map(Car::getName)
                .orElse(UNKNOWN);
    }

    public static Integer getModelIdByName(String name) {
        return carModels.values().stream()
                .filter(car -> car.getName().equals(name))
                .map(Car::getModelId)
                .findFirst()
                .orElse(null);
    }

    public static CarGroup getGroupById(Integer modelId) {
        return Optional.ofNullable(carModels.get(modelId))
                .map(Car::getGroup)
                .orElse(CarGroup.UNKNOWN);
    }

    public static List<Car> getAllSortedByName() {
        return carModels.values().stream()
                .sorted(Comparator.comparing(Car::getName))
                .collect(Collectors.toList());
    }
}
