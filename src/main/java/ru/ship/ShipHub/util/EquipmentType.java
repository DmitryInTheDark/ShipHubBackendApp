package ru.ship.ShipHub.util;

public enum EquipmentType {

    ELECTRO_TOOLS("Электрообуродование"),
    MACHINES("Станки"),
    INSTRUMENTATION_TOOLS("КИПиА"),
    MEDICAL("Медицинское оборудование"),
    OTHER("Другое");

    private final String displayName;

    EquipmentType(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
