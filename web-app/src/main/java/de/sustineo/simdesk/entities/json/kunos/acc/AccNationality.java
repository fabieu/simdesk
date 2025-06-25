package de.sustineo.simdesk.entities.json.kunos.acc;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum AccNationality {
    ANDORRA(49, "Andorra", "AD"),
    ARGENTINA(14, "Argentina", "AR"),
    ARMENIA(28, "Armenia", "AM"),
    AUSTRALIA(41, "Australia", "AU"),
    AUSTRIA(9, "Austria", "AT"),
    AZERBAIJAN(50, "Azerbaijan", "AZ"),
    BAHRAIN(71, "Bahrain", "BH"),
    BELGIUM(7, "Belgium", "BE"),
    BRAZIL(17, "Brazil", "BR"),
    BULGARIA(51, "Bulgaria", "BG"),
    CANADA(34, "Canada", "CA"),
    CHILE(82, "Chile", "CL"),
    CHINA(35, "China", "CN"),
    CHINESE_TAIPEI(81, "Chinese Taipei", "TW"),
    COLOMBIA(47, "Colombia", "CO"),
    CROATIA(33, "Croatia", "HR"),
    CUBA(52, "Cuba", "CU"),
    CZECH_REPUBLIC(53, "Czech Republic", "CZ"),
    DENMARK(32, "Denmark", "DK"),
    ENGLAND(86, "England", "GB-ENG"),
    ESTONIA(54, "Estonia", "EE"),
    FINLAND(31, "Finland", "FI"),
    FRANCE(3, "France", "FR"),
    GEORGIA(55, "Georgia", "GE"),
    GERMANY(2, "Germany", "DE"),
    GREAT_BRITAIN(5, "Great Britain", "GB"),
    GREECE(22, "Greece", "GR"),
    HONG_KONG(46, "Hong Kong", "HK"),
    HUNGARY(6, "Hungary", "HU"),
    INDIA(56, "India", "IN"),
    INDONESIA(38, "Indonesia", "ID"),
    IRAN(78, "Iran", "IR"),
    ISRAEL(57, "Israel", "IL"),
    ITALY(1, "Italy", "IT"),
    JAMAICA(58, "Jamaica", "JM"),
    JAPAN(48, "Japan", "JP"),
    KUWAIT(45, "Kuwait", "KW"),
    LATVIA(59, "Latvia", "LV"),
    LEBANON(27, "Lebanon", "LB"),
    LITHUANIA(60, "Lithuania", "LT"),
    LUXEMBOURG(44, "Luxembourg", "LU"),
    MACAU(61, "Macau", "MO"),
    MADAGASCAR(84, "Madagascar", "MG"),
    MALAYSIA(62, "Malaysia", "MY"),
    MALTA(85, "Malta", "MT"),
    MEXICO(29, "Mexico", "MX"),
    MONACO(15, "Monaco", "MC"),
    MOROCCO(88, "Morocco", "MA"),
    NEPAL(63, "Nepal", "NP"),
    NETHERLANDS(12, "Netherlands", "NL"),
    NEW_CALEDONIA(64, "New Caledonia", "NC"),
    NEW_ZEALAND(40, "New Zealand", "NZ"),
    NIGERIA(65, "Nigeria", "NG"),
    NORTHERN_IRELAND(66, "Northern Ireland", "GB-NIR"),
    NORWAY(24, "Norway", "NO"),
    OMAN(21, "Oman", "OM"),
    PAPUA_NEW_GUINEA(67, "Papua New Guinea", "PG"),
    PHILIPPINES(68, "Philippines", "PH"),
    POLAND(13, "Poland", "PL"),
    PORTUGAL(36, "Portugal", "PT"),
    PUERTO_RICO(19, "Puerto Rico", "PR"),
    QATAR(69, "Qatar", "QA"),
    ROMANIA(70, "Romania", "RO"),
    RUSSIA(10, "Russia", "RU"),
    SAN_MARINO(42, "San Marino", "SM"),
    SAUDI_ARABIA(23, "Saudi Arabia", "SA"),
    SCOTLAND(71, "Scotland", "GB-SCT"),
    SERBIA(72, "Serbia", "RS"),
    SINGAPORE(37, "Singapore", "SG"),
    SOUTH_AFRICA(18, "South Africa", "ZA"),
    SOUTH_KOREA(26, "South Korea", "KR"),
    SPAIN(4, "Spain", "ES"),
    SWEDEN(30, "Sweden", "SE"),
    SWITZERLAND(8, "Switzerland", "CH"),
    TAIWAN(74, "Taiwan", "TW"),
    THAILAND(11, "Thailand", "TH"),
    TURKEY(25, "Turkey", "TR"),
    UKRAINE(75, "Ukraine", "UA"),
    UNITED_ARAB_EMIRATES(43, "United Arab Emirates", "AE"),
    URUGUAY(83, "Uruguay", "UY"),
    USA(39, "USA", "US"),
    VENEZUELA(76, "Venezuela", "VE"),
    WALES(77, "Wales", "GB-WLS"),
    ZIMBABWE(80, "Zimbabwe", "ZW"),
    _OTHER(0, "Other", "XX");

    @JsonValue
    private final int id;
    private final String shortName;
    private final String alpha2Code;

    AccNationality(int id, String shortName, String alpha2Code) {
        this.id = id;
        this.shortName = shortName;
        this.alpha2Code = alpha2Code;
    }
}
