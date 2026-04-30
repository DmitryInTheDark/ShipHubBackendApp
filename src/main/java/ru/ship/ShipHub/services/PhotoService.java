package ru.ship.ShipHub.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import ru.ship.ShipHub.models.entity.EquipmentImageEntity;
import ru.ship.ShipHub.repositories.EquipmentImageRepository;

@Service
public class PhotoService {

    private final EquipmentImageRepository imageRepository;

    public PhotoService(EquipmentImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public EquipmentImageEntity getPhotoById(Long id){
        return imageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Фото не найдено"));
    }
}
