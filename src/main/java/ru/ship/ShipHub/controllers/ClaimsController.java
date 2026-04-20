package ru.ship.ShipHub.controllers;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.ship.ShipHub.models.dto.claim.ClaimDTO;
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

    @PostMapping(value = "/create")
    public ClaimDTO createClaim(
            @RequestParam(value = "photo1", required = false) MultipartFile photo1,
            @RequestParam(value = "photo2", required = false) MultipartFile photo2,
            @RequestParam(value = "photo3", required = false) MultipartFile photo3,
            @RequestBody(required = true) @Valid ClaimDTO dto
    ){
        return claimsService.createClaim(Collections.emptyList(), dto);
    }

    @GetMapping
    public List<ClaimDTO> getAllClaims(){
        return claimsService.getAllClaims();
    }

}
