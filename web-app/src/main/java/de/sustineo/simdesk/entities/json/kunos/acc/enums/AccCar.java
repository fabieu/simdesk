package de.sustineo.simdesk.entities.json.kunos.acc.enums;

import de.sustineo.simdesk.entities.CarGroup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.sustineo.simdesk.entities.CarGroup.*;

@Getter
@RequiredArgsConstructor
public enum AccCar {
    // GT3
    PORSCHE_991_GT3_R(0, "Porsche", "Porsche 991 GT3 R", GT3),
    MERCEDES_AMG_GT3(1, "Mercedes-AMG", "Mercedes-AMG GT3", GT3),
    FERRARI_488_GT3(2, "Ferrari", "Ferrari 488 GT3", GT3),
    AUDI_R8_LMS(3, "Audi", "Audi R8 LMS", GT3),
    LAMBORGHINI_HURACAN_GT3(4, "Lamborghini", "Lamborghini Huracan GT3", GT3),
    MCLAREN_650S_GT3(5, "McLaren", "McLaren 650S GT3", GT3),
    NISSAN_GT_R_NISMO_GT3_2018(6, "Nissan", "Nissan GT-R Nismo GT3 2018", GT3),
    BMW_M6_GT3(7, "BMW", "BMW M6 GT3", GT3),
    BENTLEY_CONTINENTAL_GT3_2018(8, "Bentley", "Bentley Continental GT3 2018", GT3),
    NISSAN_GT_R_NISMO_GT3_2017(10, "Nissan", "Nissan GT-R Nismo GT3 2017", GT3),
    BENTLEY_CONTINENTAL_GT3_2016(11, "Bentley", "Bentley Continental GT3 2016", GT3),
    ASTON_MARTIN_V12_VANTAGE_GT3(12, "Aston Martin", "Aston Martin V12 Vantage GT3", GT3),
    LAMBORGHINI_GALLARDO_R_EX(13, "Lamborghini", "Lamborghini Gallardo R‑EX", GT3),
    JAGUAR_G3(14, "Jaguar", "Jaguar G3", GT3),
    LEXUS_RC_F_GT3(15, "Lexus", "Lexus RC F GT3", GT3),
    LAMBORGHINI_HURACAN_EVO_2019(16, "Lamborghini", "Lamborghini Huracan Evo (2019)", GT3),
    HONDA_NSX_GT3(17, "Honda", "Honda NSX GT3", GT3),
    AUDI_R8_LMS_EVO_2019(19, "Audi", "Audi R8 LMS Evo (2019)", GT3),
    AMR_V8_VANTAGE_2019(20, "Aston Martin Racing", "AMR V8 Vantage (2019)", GT3),
    HONDA_NSX_EVO_2019(21, "Honda", "Honda NSX Evo (2019)", GT3),
    MCLAREN_720S_GT3_2019(22, "McLaren", "McLaren 720S GT3 (2019)", GT3),
    PORSCHE_911II_GT3_R_2019(23, "Porsche", "Porsche 911II GT3 R (2019)", GT3),
    FERRARI_488_GT3_EVO_2020(24, "Ferrari", "Ferrari 488 GT3 Evo 2020", GT3),
    MERCEDES_AMG_GT3_2020(25, "Mercedes-AMG", "Mercedes-AMG GT3 2020", GT3),
    BMW_M4_GT3(30, "BMW", "BMW M4 GT3", GT3),
    AUDI_R8_LMS_GT3_EVO_II(31, "Audi", "Audi R8 LMS GT3 evo II", GT3),
    FERRARI_296_GT3(32, "Ferrari", "Ferrari 296 GT3", GT3),
    LAMBORGHINI_HURACAN_EVO2(33, "Lamborghini", "Lamborghini Huracan Evo2", GT3),
    PORSCHE_992_GT3_R(34, "Porsche", "Porsche 992 GT3 R", GT3),
    MCLAREN_720S_GT3_EVO_2023(35, "McLaren", "McLaren 720S GT3 Evo 2023", GT3),
    FORD_MUSTANG_GT3(36, "Ford", "Ford Mustang GT3", GT3),

    // GT4
    ALPINE_A110_GT4(50, "Alpine", "Alpine A110 GT4", GT4),
    AMR_V8_VANTAGE_GT4(51, "Aston Martin Racing", "AMR V8 Vantage GT4", GT4),
    AUDI_R8_LMS_GT4(52, "Audi", "Audi R8 LMS GT4", GT4),
    BMW_M4_GT4(53, "BMW", "BMW M4 GT4", GT4),
    CHEVROLET_CAMARO_GT4(55, "Chevrolet", "Chevrolet Camaro GT4", GT4),
    GINETTA_G55_GT4(56, "Ginetta", "Ginetta G55 GT4", GT4),
    KTM_X_BOW_GT4(57, "KTM", "KTM X‑Bow GT4", GT4),
    MASERATI_MC_GT4(58, "Maserati", "Maserati MC GT4", GT4),
    MCLAREN_570S_GT4(59, "McLaren", "McLaren 570S GT4", GT4),
    MERCEDES_AMG_GT4(60, "Mercedes-AMG", "Mercedes-AMG GT4", GT4),
    PORSCHE_718_CAYMAN_GT4(61, "Porsche", "Porsche 718 Cayman GT4", GT4),

    // GT2
    LAMBORGHINI_HURACAN_SUPERTROFEO(18, "Lamborghini", "Lamborghini Huracan SuperTrofeo", GT2),
    FERRARI_488_CHALLENGE_EVO(26, "Ferrari", "Ferrari 488 Challenge Evo", GT2),
    LAMBORGHINI_HURACAN_SUPERTROFEO_EVO2(29, "Lamborghini", "Lamborghini Huracán Super Trofeo EVO2", GT2),
    AUDI_R8_LMS_GT2(80, "Audi", "Audi R8 LMS GT2", GT2),
    KTM_XBOW_GT2(82, "KTM", "KTM XBOW GT2", GT2),
    MASERATI_MC20_GT2(83, "Maserati", "Maserati MC20 GT2", GT2),
    MERCEDES_AMG_GT2(84, "Mercedes-AMG", "Mercedes AMG GT2", GT2),
    PORSCHE_911_GT2_RS_CS_EVO(85, "Porsche", "Porsche 911 GT2 RS CS Evo", GT2),
    PORSCHE_935(86, "Porsche", "Porsche 935", GT2),

    // CUP
    PORSCHE_991II_GT3_CUP(9, "Porsche", "Porsche 991II GT3 Cup", GTC),
    PORSCHE_911_GT3_CUP_TYPE_992(28, "Porsche", "Porsche 911 GT3 Cup (Type 992)", GTC),

    // TCX
    BMW_M2_CS_RACING(27, "BMW", "BMW M2 CS Racing", TCX),

    // Unknown
    UNKNOWN(255, "Unknown", "Unknown", CarGroup.UNKNOWN);

    private final int id;
    private final String manufacturer;
    private final String model;
    private final CarGroup group;

    private static final Map<Integer, AccCar> CARS_BY_ID = Stream.of(values())
            .collect(Collectors.toMap(AccCar::getId, Function.identity()));

    private static final Map<String, AccCar> CARS_BY_MODEL = Stream.of(values())
            .collect(Collectors.toMap(AccCar::getModel, Function.identity()));

    public static AccCar getCarById(int id) {
        return CARS_BY_ID.get(id);
    }

    public static String getModelById(int id) {
        return CARS_BY_ID.getOrDefault(id, AccCar.UNKNOWN).getModel();
    }

    public static Integer getIdByName(String model) {
        return Optional.ofNullable(CARS_BY_MODEL.get(model))
                .map(AccCar::getId)
                .orElse(null);
    }

    public static CarGroup getGroupById(int id) {
        return Optional.ofNullable(CARS_BY_ID.get(id))
                .map(AccCar::getGroup)
                .orElse(CarGroup.UNKNOWN);
    }

    public static List<AccCar> getAllSortedByModel() {
        return Stream.of(values())
                .filter(car -> car != AccCar.UNKNOWN)
                .sorted(Comparator.comparing(AccCar::getModel))
                .collect(Collectors.toList());
    }
}
