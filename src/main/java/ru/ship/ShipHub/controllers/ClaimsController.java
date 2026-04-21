package ru.ship.ShipHub.controllers;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.ship.ShipHub.models.dto.claim.ClaimDTO;
import ru.ship.ShipHub.models.dto.claim.UpdateClaimDTO;
import ru.ship.ShipHub.security.PersonDetails;
import ru.ship.ShipHub.services.ClaimsService;

import java.util.Collections;
import java.util.List;

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
            @RequestParam(value = "photo1", required = false) MultipartFile photo1,
            @RequestParam(value = "photo2", required = false) MultipartFile photo2,
            @RequestParam(value = "photo3", required = false) MultipartFile photo3,
            @RequestBody(required = true) @Valid ClaimDTO dto,
            @AuthenticationPrincipal PersonDetails personDetails
    ){
        return claimsService.createClaim(Collections.emptyList(), dto, personDetails);
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
