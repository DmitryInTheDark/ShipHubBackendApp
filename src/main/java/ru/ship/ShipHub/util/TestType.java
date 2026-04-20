package ru.ship.ShipHub.util;

public enum TestType {

    SECURITY("Испытания на безопасность"),
    ELECTROMAGNETIC_COMPATIBILITY("Электромагнитная совместимость"),
    CLIMATE_TEST("Климатические испытания"),
    MECHANIC("Механические испытания"),
    MATERIAL_ANALYZE("Анализ материалов"),
    CERTIFICATION("Сертификация"),
    OTHER("Другое");

    private final String displayName;

    TestType(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
