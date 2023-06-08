package de.sustineo.acc.leaderboard.entities;

import lombok.Data;

import java.util.HashMap;

@Data
public class Car {
    private static final HashMap<Integer, String> carModels = new HashMap<>();
    private static final String DEFAULT_CAR_NAME = "Unknown";

    static {
        carModels.put(0, "Porsche 991 GT3 R");
        carModels.put(1, "Mercedes-AMG GT3");
        carModels.put(2, "Ferrari 488 GT3");
        carModels.put(3, "Audi R8 LMS");
        carModels.put(4, "Lamborghini Huracan GT3");
        carModels.put(5, "McLaren 650S GT3");
        carModels.put(6, "Nissan GT-R Nismo GT3 2018");
        carModels.put(7, "BMW M6 GT3");
        carModels.put(8, "Bentley Continental GT3 2018");
        carModels.put(9, "Porsche 991II GT3 Cup");
        carModels.put(10, "Nissan GT-R Nismo GT3 2017");
        carModels.put(11, "Bentley Continental GT3 2016");
        carModels.put(12, "Aston Martin V12 Vantage GT3");
        carModels.put(13, "Lamborghini Gallardo R-EX");
        carModels.put(14, "Jaguar G3");
        carModels.put(15, "Lexus RC F GT3");
        carModels.put(16, "Lamborghini Huracan Evo (2019)");
        carModels.put(17, "Honda NSX GT3");
        carModels.put(18, "Lamborghini Huracan SuperTrofeo");
        carModels.put(19, "Audi R8 LMS Evo (2019)");
        carModels.put(20, "AMR V8 Vantage (2019)");
        carModels.put(21, "Honda NSX Evo (2019)");
        carModels.put(22, "McLaren 720S GT3 (2019)");
        carModels.put(23, "Porsche 911II GT3 R (2019)");
        carModels.put(24, "Ferrari 488 GT3 Evo 2020");
        carModels.put(25, "Mercedes-AMG GT3 2020");
        carModels.put(26, "Ferrari 488 Challenge Evo");
        carModels.put(27, "BMW M2 CS Racing");
        carModels.put(28, "Porsche 911 GT3 Cup (Type 992)");
        carModels.put(29, "Lamborghini Huracán Super Trofeo EVO2");
        carModels.put(30, "BMW M4 GT3");
        carModels.put(31, "Audi R8 LMS GT3 evo II");
        carModels.put(32, "Ferrari 296 GT3");
        carModels.put(33, "Lamborghini Huracan Evo2");
        carModels.put(34, "Porsche 992 GT3 R");
        carModels.put(35, "McLaren 720S GT3 Evo 2023");
        carModels.put(50, "Alpine A110 GT4");
        carModels.put(51, "AMR V8 Vantage GT4");
        carModels.put(52, "Audi R8 LMS GT4");
        carModels.put(53, "BMW M4 GT4");
        carModels.put(55, "Chevrolet Camaro GT4");
        carModels.put(56, "Ginetta G55 GT4");
        carModels.put(57, "KTM X-Bow GT4");
        carModels.put(58, "Maserati MC GT4");
        carModels.put(59, "McLaren 570S GT4");
        carModels.put(60, "Mercedes-AMG GT4");
        carModels.put(61, "Porsche 718 Cayman GT4");
    }

    public static String getCarNameById(Integer carModel) {
        return carModels.getOrDefault(carModel, DEFAULT_CAR_NAME);
    }
}
