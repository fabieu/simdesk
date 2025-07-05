
package de.sustineo.simdesk.client.protocol.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Nationality {
    ANY(0, "Any"),
    ITALY(1, "Italy"),
    GERMANY(2, "Germany"),
    FRANCE(3, "France"),
    SPAIN(4, "Spain"),
    GREAT_BRITAIN(5, "GreatBritain"),
    HUNGARY(6, "Hungary"),
    BELGIUM(7, "Belgium"),
    SWITZERLAND(8, "Switzerland"),
    AUSTRIA(9, "Austria"),
    RUSSIA(10, "Russia"),
    THAILAND(11, "Thailand"),
    NETHERLANDS(12, "Netherlands"),
    POLAND(13, "Poland"),
    ARGENTINA(14, "Argentina"),
    MONACO(15, "Monaco"),
    IRELAND(16, "Ireland"),
    BRAZIL(17, "Brazil"),
    SOUTH_AFRICA(18, "SouthAfrica"),
    PUERTO_RICO(19, "PuertoRico"),
    SLOVAKIA(20, "Slovakia"),
    OMAN(21, "Oman"),
    GREECE(22, "Greece"),
    SAUDIARABIA(23, "SaudiArabia"),
    NORWAY(24, "Norway"),
    TURKEY(25, "Turkey"),
    SOUTH_KOREA(26, "SouthKorea"),
    LEBANON(27, "Lebanon"),
    ARMENIA(28, "Armenia"),
    MEXICO(29, "Mexico"),
    SWEDEN(30, "Sweden"),
    FINLAND(31, "Finland"),
    DENMARK(32, "Denmark"),
    CROATIA(33, "Croatia"),
    CANADA(34, "Canada"),
    CHINA(35, "China"),
    PORTUGAL(36, "Portugal"),
    SINGAPORE(37, "Singapore"),
    INDONESIA(38, "Indonesia"),
    USA(39, "USA"),
    NEW_ZEALAND(40, "NewZealand"),
    AUSTRALIA(41, "Australia"),
    SAN_MARINO(42, "SanMarino"),
    UAE(43, "UAE"),
    LUXEMBOURG(44, "Luxembourg"),
    KUWAIT(45, "Kuwait"),
    HONGKONG(46, "HongKong"),
    COLOMBIA(47, "Colombia"),
    JAPAN(48, "Japan"),
    ANDORRA(49, "Andorra"),
    AZERBAIJAN(50, "Azerbaijan"),
    BULGARIA(51, "Bulgaria"),
    CUBA(52, "Cuba"),
    CZECH_REPUBLIC(53, "CzechRepublic"),
    ESTONIA(54, "Estonia"),
    GEORGIA(55, "Georgia"),
    INDIA(56, "India"),
    ISRAEL(57, "Israel"),
    JAMAICA(58, "Jamaica"),
    LATVIA(59, "Latvia"),
    LITHUANIA(60, "Lithuania"),
    MACAU(61, "Macau"),
    MALAYSIA(62, "Malaysia"),
    NEPAL(63, "Nepal"),
    NEW_CALEDONIA(64, "NewCaledonia"),
    NIGERIA(65, "Nigeria"),
    NORTHERN_IRELAND(66, "NorthernIreland"),
    PAPUA_NEW_GUINEA(67, "PapuaNewGuinea"),
    PHILIPPINES(68, "Philippines"),
    QATAR(69, "Qatar"),
    ROMANIA(70, "Romania"),
    SCOTLAND(71, "Scotland"),
    SERBIA(72, "Serbia"),
    SLOVENIA(73, "Slovenia"),
    TAIWAN(74, "Taiwan"),
    UKRAINE(75, "Ukraine"),
    VENEZUELA(76, "Venezuela"),
    WALES(7, "Wales");

    private final int id;
    private final String name;

    public static Nationality fromId(int id) {
        for (Nationality nationality : Nationality.values()) {
            if (nationality.getId() == id) {
                return nationality;
            }
        }

        return ANY;
    }
}
