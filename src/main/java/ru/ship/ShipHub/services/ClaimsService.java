package ru.ship.ShipHub.services;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.ship.ShipHub.models.dto.DocumentDTO;
import ru.ship.ShipHub.models.dto.DocumentInfoDTO;
import ru.ship.ShipHub.models.dto.ListDTO;
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


    @Transactional
    public ClaimDTO createClaim(
            ClaimDTO dto,
            @AuthenticationPrincipal PersonDetails personDetails
    ){
        if (dto.isCustomType() && dto.getTestType() != TestType.OTHER) {
            throw new BadRequestException("Заявка с нестандартным типом тестирования не может содержать другой тип тестирования");
        }
        if (dto.getEquipment().isCustomType() && dto.getEquipment().getEquipmentType() != EquipmentType.OTHER) {
            throw new BadRequestException("Оборудование с нестандартным типом не может содержать другой тип оборудования");
        }
        ClaimEntity claim = mapper.map(dto);
        claim.setDateCreate(LocalDateTime.now());
        EquipmentEntity equipment = mapper.map(dto.getEquipment());
        equipment.setClaim(claim);
        claim.setEquipment(equipment);
        claim.setWhoCreate(personDetails.getPerson());
        claim.setStatus(ClaimStatus.CREATED);
        ClaimEntity savedClaim = claimRepository.save(claim);
        return mapper.map(savedClaim);
    }

    @Transactional
    public ClaimDTO createClaimWithPhotos(
            ClaimDTO dto,
            @AuthenticationPrincipal PersonDetails personDetails,
            MultipartFile photo1,
            MultipartFile photo2,
            MultipartFile photo3,
            MultipartFile document1,
            String documentType1,
            MultipartFile document2,
            String documentType2,
            MultipartFile document3,
            String documentType3
    ){
        ClaimDTO created = createClaim(dto, personDetails);
        var photoProblems = attachPhotos(created.getId(), photo1, photo2, photo3, personDetails);
        if (!photoProblems.isEmpty()) {
            log.warn("Некоторые фото не удалось прикрепить: {}", photoProblems);
        }

        List<MultipartFile> documents = new ArrayList<>();
        documents.add(document1);
        documents.add(document2);
        documents.add(document3);
        List<String> documentTypes = new ArrayList<>();
        documentTypes.add(documentType1);
        documentTypes.add(documentType2);
        documentTypes.add(documentType3);
        for (int i = 0; i < documents.size(); i++) {
            MultipartFile document = documents.get(i);
            String documentType = documentTypes.get(i);
            if (document == null || document.isEmpty()) {
                continue;
            }
            if (documentType == null || documentType.isBlank()) {
                throw new BadRequestException("Для документа document" + (i + 1) + " не задан document_type");
            }
            attachDocument(created.getId(), document, documentType, personDetails);
        }

        return created;
    }

        public Map<Integer, String> attachPhotos(
            Long claimId,
            MultipartFile photo1,
            MultipartFile photo2,
            MultipartFile photo3,
            PersonDetails actor
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
        if (equipment.getImages() != null && equipment.getImages().size() + notEmptyPhotos.size() > 3) {
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
                // update claim last update info
                var claimEntity = claimRepository.findById(claimId).orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Заявка с таким id не найдена"));
                claimEntity.setLastUpdateAt(java.time.LocalDateTime.now());
                if (actor != null) claimEntity.setLastUpdateBy(actor.getPerson());
                claimEntity.setLastUpdate("Фотография добавлена");
                claimRepository.save(claimEntity);
            } catch (IOException e) {
                log.error("Ошибка чтения файла: ", e);
                problemPhotos.put(i+1, "Файл не удалось прочитать");
            }
            index = index + 1;
        }
        return problemPhotos;
    }

    @Transactional
    public ListDTO<ClaimDTO> getAllClaims(
            Integer pageNumber,
            Integer pageSize,
            PersonDetails personDetails
    ) {
        PageRequest pageRequest;
        if (pageNumber == null) {
            pageRequest = null;
        }else{
            pageRequest = PageRequest.of(pageNumber, pageSize == null ? 20 : pageSize, Sort.by("id").ascending());
        }
        List<ClaimDTO> claims;
        if (isManager(personDetails)){
            if (pageRequest != null){
                claims = claimRepository.findAll(pageRequest).stream().map(mapper::map).toList();
            }else{
                claims = claimRepository.findAll().stream().map(mapper::map).toList();
            }
        }else {
            if (pageRequest != null){
                claims = claimRepository.findByWhoCreateId(personDetails.getPerson().getId(), pageRequest)
                        .stream()
                        .map(mapper::map)
                        .toList();
            }else{
                claims = claimRepository.findByWhoCreateId(personDetails.getPerson().getId())
                        .stream()
                        .map(mapper::map)
                        .toList();
            }
        }
        return new ListDTO<>(claimRepository.count(), claims);
    }

    @Transactional
    public ListDTO<ClaimDTO> getAllClaims(
            PersonDetails personDetails
    ) {
        if (isManager(personDetails)){
            return new ListDTO<>(claimRepository.count(), claimRepository.findAll().stream().map(mapper::map).toList());
        }else{
            return new ListDTO<>(claimRepository.countByWhoCreate(personDetails.getPerson()), claimRepository.findByWhoCreateId(personDetails.getPerson().getId()).stream().map(mapper::map).toList());
        }
    }

    public ClaimDTO getClaimById(Long id){
        var claimEntity = claimRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Заявка не найдена"));
        return mapper.map(claimEntity);
    }

    @Transactional
    public ListDTO<ClaimDTO> getActiveClaims(int pageNumber, int pageSize, PersonDetails personDetails) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("id").ascending());
        var personId = personDetails.getPerson().getId();
        if (isManager(personDetails)){
            var activeClaims = claimRepository.findWithoutStatus(ClaimStatus.ENDED, pageRequest)
                    .stream().map(mapper::map).toList();
            return new ListDTO<>(claimRepository.countWithoutStatus(ClaimStatus.ENDED), activeClaims);
        }else{
            var activeClaims = claimRepository
                    .findWithoutStatusByWhoCreateId(personId, ClaimStatus.ENDED, pageRequest)
                    .stream().map(mapper::map).toList();
            return new ListDTO<>(claimRepository.countWithoutStatusByWhoCreateId(ClaimStatus.ENDED, personId), activeClaims);
        }
    }

    @Transactional
    public ListDTO<ClaimDTO> getClaimsByStatus(int pageNumber, int pageSize, ClaimStatus status, PersonDetails personDetails) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("id").ascending());
        var personId = personDetails.getPerson().getId();
        if (isManager(personDetails)){
            var activeClaims = claimRepository.findByStatus(status, pageRequest)
                    .stream().map(mapper::map).toList();
            return new ListDTO<>(claimRepository.countWithoutStatus(status), activeClaims);
        }else{
            var activeClaims = claimRepository
                    .findByStatusByWhoCreateId(status, personId, pageRequest)
                    .stream().map(mapper::map).toList();
            return new ListDTO<>(claimRepository.countWithStatusByWhoCreateId(status, personId), activeClaims);
        }
    }

    public ClaimDTO updateClaim(Long id, UpdateClaimDTO dto, PersonDetails actor) {
        var claimToUpdate = claimRepository.findById(id).orElseThrow(ClaimNotFoundException::new);
        claimToUpdate.setStatus(dto.getStatus());
        claimToUpdate.setDateUpdate(LocalDateTime.now());
        claimToUpdate.setLastUpdate(dto.getUpdateInfo());
        claimToUpdate.setLastUpdateAt(LocalDateTime.now());
        if (actor != null) claimToUpdate.setLastUpdateBy(actor.getPerson());
        return mapper.map(claimRepository.save(claimToUpdate));
    }

    public EquipmentImageEntity getPhotoById(Long id){
        return imageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Фото не найдено"));
    }

        public boolean attachDocument(
            Long claimId,
            MultipartFile document,
            String documentType,
            PersonDetails actor
        ) {
        var claim = claimRepository.findById(claimId).orElseThrow(() -> new EntityNotFoundException("Заявка не найдена"));
        if (document.isEmpty()) throw new BadRequestException("Файл пустой");
        if (!allowedContentTypes.contains(document.getContentType())) throw new BadRequestException("Формат файла не поддерживается");
        
        // Убираем кавычки, если они есть
        String cleanedType = documentType.replaceAll("^\"|\"$", "");
        
        DocumentType type;
        try{
            type = DocumentType.valueOf(cleanedType.toUpperCase());
        }catch (IllegalArgumentException e){
            throw new BadRequestException("Некоректный тип документа: '" + documentType + "'. Допустимые значения: INFO, ACT, AGREEMENT, CHECK");
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
        // update claim last update info
        claim.setLastUpdateAt(LocalDateTime.now());
        if (actor != null) claim.setLastUpdateBy(actor.getPerson());
        claim.setLastUpdate("Документ добавлен: " + document.getOriginalFilename());
        claimRepository.save(claim);
        return true;
    }

     @Transactional
    public java.util.List<ru.ship.ShipHub.models.dto.NotificationDTO> getNotifications(PersonDetails personDetails) {
        var user = personDetails.getPerson();
        java.util.List<ru.ship.ShipHub.models.entity.ClaimEntity> claims;
        if (isManager(personDetails)) {
            claims = claimRepository.findNotificationsForManager(user.getId());
        } else {
            claims = claimRepository.findNotificationsForUser(user.getId());
        }
        var result = new ArrayList<ru.ship.ShipHub.models.dto.NotificationDTO>();
        for (var c : claims) {
            String text = c.getLastUpdate();
            var author = c.getLastUpdateBy();
            Long authorId = author != null ? author.getId() : null;
            String authorName = author != null ? (author.getLegalInfo() != null && author.getLegalInfo().getOrganizationName() != null && !author.getLegalInfo().getOrganizationName().isBlank() ? author.getLegalInfo().getOrganizationName() : author.getUsername()) : null;
            result.add(new ru.ship.ShipHub.models.dto.NotificationDTO(c.getId(), c.getLastUpdateAt(), text, authorId, authorName));
        }
        return result;
    }

    public DocumentInfoDTO getDocumentInfoById(Long id) {
        var entity = documentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Документ с таким id не найден"));
        return mapper.mapDocumentInfo(entity);
    }

    private boolean isManager(PersonDetails personDetails){
        return personDetails.getAuthorities().stream()
                .anyMatch(auth -> Objects.equals(auth.getAuthority(), "ROLE_MANAGER"));
    }

    public DocumentEntity getDocument(Long id){
        return documentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Документ с таким id не найден"));
    }

    public ListDTO<DocumentDTO> getClaimsDocuments(long claimId, PersonDetails personDetails) {
        var person = personDetails.getPerson();
        var claim = claimRepository.findById(claimId).orElseThrow(() -> new EntityNotFoundException("Заявка не найдена"));
        return new ListDTO<>(documentRepository.countByClaim(claim), documentRepository.findAllByClaim(claim).stream().map(mapper::map).toList());
    }

    public Map<String, String> attachDocuments(Long claimId,
                                               List<MultipartFile> documents,
                                               List<String> documentTypes,
                                               PersonDetails actor) {

        Map<String, String> result = new LinkedHashMap<>(); // сохраняем порядок файлов

        if (documents.isEmpty()) {
            return result; // пустая мапа
        }

        if (documents.size() != documentTypes.size()) {
            throw new BadRequestException("Количество файлов не совпадает с количеством типов документов");
        }

        ClaimEntity claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new EntityNotFoundException("Заявка не найдена"));

        for (int i = 0; i < documents.size(); i++) {
            MultipartFile document = documents.get(i);
            String docTypeStr = documentTypes.get(i);
            String filename = document.getOriginalFilename() != null
                    ? document.getOriginalFilename()
                    : "unknown_file_" + i;

            try {
                // Валидация файла
                if (document.isEmpty()) {
                    result.put(filename, "Файл пустой");
                    continue;
                }

                if (!allowedContentTypes.contains(document.getContentType())) {
                    result.put(filename, "Формат файла не поддерживается: " + document.getContentType());
                    continue;
                }

                // Очистка и валидация типа документа
                String cleanedType = docTypeStr.replaceAll("^\"|\"$", "").trim().toUpperCase();

                DocumentType type;
                try {
                    type = DocumentType.valueOf(cleanedType);
                } catch (IllegalArgumentException e) {
                    result.put(filename, "Некорректный тип документа: '" + docTypeStr +
                            "'. Допустимые: INFO, ACT, AGREEMENT, CHECK");
                    continue;
                }

                // Сохранение
                DocumentEntity documentEntity = new DocumentEntity(
                        document.getBytes(),
                        document.getContentType(),
                        filename,
                        type,
                        LocalDateTime.now(),
                        claim
                );

                documentRepository.save(documentEntity);

                // update claim last update info
                claim.setLastUpdateAt(LocalDateTime.now());
                if (actor != null) claim.setLastUpdateBy(actor.getPerson());
                claim.setLastUpdate("Документ добавлен: " + filename);
                claimRepository.save(claim);

                result.put(filename, "Успешно");

            } catch (IOException e) {
                log.error("Ошибка при чтении файла {}: {}", filename, e.getMessage());
                result.put(filename, "Не удалось прочитать файл");
            } catch (Exception e) {
                log.error("Неожиданная ошибка при обработке файла {}: {}", filename, e.getMessage(), e);
                result.put(filename, "Внутренняя ошибка при обработке файла");
            }
        }

        return result;
    }
}
