package ru.ship.ShipHub.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.ship.ShipHub.models.dto.claim.ClaimDTO;
import ru.ship.ShipHub.models.dto.claim.UpdateClaimDTO;
import ru.ship.ShipHub.config.security.PersonDetails;
import ru.ship.ShipHub.services.ClaimsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/claims")
public class ClaimsController {

    private final ClaimsService claimsService;

    public ClaimsController(ClaimsService claimsService) {
        this.claimsService = claimsService;
    }

    @PreAuthorize("hasRole('PHYSICAL', 'LEGAL')")
    @PostMapping(value = "/create")
    public ClaimDTO createClaim(
            @RequestBody @Valid ClaimDTO dto,
            @AuthenticationPrincipal PersonDetails personDetails
    ){
        return claimsService.createClaim(dto, personDetails);
    }

    @PreAuthorize("hasRole('PHYSICAL', 'LEGAL')")
    @PostMapping(value = "/attach_photos/{id}")
    public ResponseEntity attachPhoto(
            @RequestPart(value = "photo1", required = false) MultipartFile photo1,
            @RequestPart(value = "photo2", required = false) MultipartFile photo2,
            @RequestPart(value = "photo3", required = false) MultipartFile photo3,
            @PathVariable("id") Long id
    ){
        var notNullablePhotos = new ArrayList<>();
        if (photo1 != null) notNullablePhotos.add(photo1);
        if (photo2 != null) notNullablePhotos.add(photo2);
        if (photo3 != null) notNullablePhotos.add(photo3);
        if (notNullablePhotos.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "Фотографии отсутствуют"));
        var problems = claimsService.attachPhotos(id, photo1, photo2, photo3);
        if (problems.size() == notNullablePhotos.size()){
            return ResponseEntity.status(400).body(Map.of("error", "Ни один из файлов не удалось загрузить на сервер"));
        }else if (!problems.isEmpty()){
            return ResponseEntity.status(207).body(problems);
        }else{
            return ResponseEntity.status(201).build();
        }
    }

    @GetMapping("/{id}")
    public ClaimDTO getClaimById(
            @PathVariable("id") Long id
    ){
        return claimsService.getClaimById(id);
    }

    @GetMapping
    public List<ClaimDTO> getAllClaims(
            @AuthenticationPrincipal PersonDetails personDetails
    ){
        return claimsService.getAllClaims(personDetails);
    }

    @GetMapping("/active")
    public List<ClaimDTO> getActiveClaims(
            @AuthenticationPrincipal PersonDetails personDetails
    ){
        return claimsService.getActiveClaims(personDetails);
    }

    @PatchMapping("/{id}/update")
    public ClaimDTO updateClaim(
            @PathVariable Long id,
            @RequestBody UpdateClaimDTO dto
    ){
        return claimsService.updateClaim(id, dto);
    }

}
