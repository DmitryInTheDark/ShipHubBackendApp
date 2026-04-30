package ru.ship.ShipHub.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ship.ShipHub.services.PhotoService;

@RestController
@RequestMapping("/photos")
public class PhotoController {

    private final PhotoService service;

    public PhotoController(PhotoService service) {
        this.service = service;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<byte[]> getPhotoById(
            @PathVariable("id") Long id
    ){
        var photo = service.getPhotoById(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(photo.getContentType()))
                .body(photo.getBytes());
    }

}
