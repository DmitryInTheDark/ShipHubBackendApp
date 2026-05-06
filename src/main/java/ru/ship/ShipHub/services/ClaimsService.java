package ru.ship.ShipHub.services;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.ship.ShipHub.models.dto.DocumentDTO;
import ru.ship.ShipHub.models.dto.DocumentInfoDTO;
import ru.ship.ShipHub.models.dto.claim.ClaimDTO;
import ru.ship.ShipHub.models.dto.claim.UpdateClaimDTO;
import ru.ship.ShipHub.models.entity.ClaimEntity;
import ru.ship.ShipHub.models.entity.DocumentEntity;
import ru.ship.ShipHub.models.entity.EquipmentEntity;
import ru.ship.ShipHub.models.entity.EquipmentImageEntity;
import ru.ship.ShipHub.repositories.*;
import ru.ship.ShipHub.config.security.PersonDetails;
import ru.ship.ShipHub.util.*;
import ru.ship.ShipHub.util.exceptions.BadRequestException;
import ru.ship.ShipHub.util.exceptions.ClaimNotFoundException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Service
public class ClaimsService {

    private final Mapper mapper;
    private final Logger log;
    private final ClaimRepository claimRepository;
    private final EquipmentRepository equipmentRepository;
    private final EquipmentImageRepository equipmentImageRepository;
    private final DocumentRepository documentRepository;
    private final EquipmentImageRepository imageRepository;

    final static List<String> allowedContentTypes = new ArrayList<>(
            List.of(
                "application/pdf",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            )
    );

    public ClaimsService(
            Mapper mapper,
            MailUtil mailUtil,
            PasswordEncoder passwordEncoder,
            LegalInfoRepository legalInfoRepository,
            PhysicalRepository physicalRepository,
            ClaimRepository claimRepository,
            EquipmentRepository equipmentRepository,
            EquipmentImageRepository equipmentImageRepository, DocumentRepository documentRepository, EquipmentImageRepository imageRepository
    ) {
        this.mapper = mapper;
        this.claimRepository = claimRepository;
        this.equipmentRepository = equipmentRepository;
        this.equipmentImageRepository = equipmentImageRepository;
        this.documentRepository = documentRepository;
        this.imageRepository = imageRepository;
        this.log = LoggerFactory.getLogger(AuthService.class);
    }


    public ClaimDTO createClaim(
            ClaimDTO dto,
            @AuthenticationPrincipal PersonDetails personDetails
    ){
        ClaimEntity claim = mapper.map(dto);
        claim.setDateCreate(LocalDateTime.now());
        EquipmentEntity equipment = mapper.map(dto.getEquipment());
        equipmentRepository.save(equipment);
        claim.setEquipment(equipment);
        if (claim.isCustomType() && claim.getTestType() != TestType.OTHER) {
            throw new BadRequestException("Заявка с нестандартным типом тестирования не может содержать другой тип тестирования");
        }
        if (claim.getEquipment().isCustomType() && claim.getEquipment().getEquipmentType() != EquipmentType.OTHER) {
            throw new BadRequestException("Оборудование с нестандартным типом не может содержать другой тип оборудования");
        }
        claim.setWhoCreate(personDetails.getPerson());
        claim.setStatus(ClaimStatus.CREATED);
        ClaimEntity savedClaim = claimRepository.save(claim);
        return mapper.map(savedClaim);
    }

    public Map<Integer, String> attachPhotos(
            Long claimId,
            MultipartFile photo1,
            MultipartFile photo2,
            MultipartFile photo3
    ){
        var photos = Stream.of(photo1, photo2, photo3)
                .filter(Objects::nonNull)
                .toList();
        var equipment = claimRepository.findById(claimId).orElseThrow(() -> new EntityNotFoundException("Заявка с таким id не найдена"))
                .getEquipment();
        var problemPhotos = new HashMap<Integer, String>(Collections.emptyMap());
        var index = 0;
        var notEmptyPhotos = photos.stream()
                .filter(file -> !file.isEmpty()).toList();
        if (equipment.getImages().size() + notEmptyPhotos.size() > 3) {
            throw new BadRequestException(
                    "К одной заявке нельзя прикрепить больше трёх фото. Текущее количество фото: " + equipment.getImages().size()
            );
        }
        for (int i = 0; i < notEmptyPhotos.size(); i++){
            var photo = notEmptyPhotos.get(i);
            try {
                EquipmentImageEntity photoEntity = new EquipmentImageEntity(
                        photo.getBytes(), equipment, "description", photo.getContentType()
                );
                equipmentImageRepository.save(photoEntity);
            } catch (IOException e) {
                log.error("Ошибка чтения файла: ", e);
                problemPhotos.put(i+1, "Файл не удалось прочитать");
            }
            index = index + 1;
        }
        return problemPhotos;
    }

    @Transactional
    public List<ClaimDTO> getAllClaims(
            @AuthenticationPrincipal PersonDetails personDetails
    ) {
        boolean isManager = personDetails.getAuthorities().stream()
                .anyMatch(auth -> Objects.equals(auth.getAuthority(), "ROLE_MANAGER"));
        if (isManager){
            return claimRepository.findAll().stream().map(mapper::map).toList();
        }else {
            return claimRepository.findByWhoCreateId(personDetails.getPerson().getId())
                    .stream()
                    .map(mapper::map)
                    .toList();
        }
    }

    public ClaimDTO getClaimById(Long id){
        var claimEntity = claimRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Заявка не найдена"));
        return mapper.map(claimEntity);
    }

    public List<ClaimDTO> getActiveClaims(PersonDetails personDetails) {
        return claimRepository.findAll().stream()
                .filter( claim ->
                        claim.getStatus() != ClaimStatus.DOCUMENTS_DELIVERED
                ).map(mapper::map).toList();
    }

    public ClaimDTO updateClaim(Long id, UpdateClaimDTO dto) {
        var claimToUpdate = claimRepository.findById(id).orElseThrow(ClaimNotFoundException::new);
        claimToUpdate.setStatus(dto.getStatus());
        claimToUpdate.setDateUpdate(LocalDateTime.now());
        claimToUpdate.setLastUpdate(dto.getUpdateInfo());
        return mapper.map(claimRepository.save(claimToUpdate));
    }

    public EquipmentImageEntity getPhotoById(Long id){
        return imageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Фото не найдено"));
    }

    public boolean attachDocument(
            Long claimId,
            MultipartFile document,
            String documentType
    ) {
        var claim = claimRepository.findById(claimId).orElseThrow(() -> new EntityNotFoundException("Заявка не найдена"));
        if (document.isEmpty()) throw new BadRequestException("Файл пустой");
        if (!allowedContentTypes.contains(document.getContentType())) throw new BadRequestException("Формат файла не поддерживается");
        DocumentType type;
        try{
            type = DocumentType.valueOf(documentType.toUpperCase());
        }catch (IllegalArgumentException e){
            throw new BadRequestException("Некоректный тип документа");
        }
        DocumentEntity documentEntity;
        try{
            documentEntity = new DocumentEntity(
                    document.getBytes(),
                    document.getContentType(),
                    document.getOriginalFilename(),
                    type,
                    LocalDateTime.now(),
                    claim
            );
        }catch (IOException e){
            log.error(e.getMessage());
            return false;
        }
        documentRepository.save(documentEntity);
        return true;
    }

    public DocumentInfoDTO getDocumentInfoById(Long id) {
        var entity = documentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Документ с таким id не найден"));
        return mapper.mapDocumentInfo(entity);
    }

    public DocumentDTO getDocument(Long id){
        var entity = documentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Документ с таким id не найден"));
        return mapper.map(entity);
    }
}
