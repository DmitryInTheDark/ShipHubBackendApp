package ru.ship.ShipHub.models.dto;

import java.util.List;

public record ListDTO<T> (
        long count,
        List<T> items
){}

