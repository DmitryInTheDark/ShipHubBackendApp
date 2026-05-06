package ru.ship.ShipHub.models.entity;

import jakarta.persistence.*;
import ru.ship.ShipHub.util.DocumentType;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "bytes")
    private byte[] bytes;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    private DocumentType type;

    @Column(name = "date_reate")
    private LocalDateTime dateCreate;

    @ManyToOne
    @JoinColumn(
            name = "claim_id",
            referencedColumnName = "id",
            nullable = false
    )
    private ClaimEntity claim;

    public DocumentEntity() {}

    public DocumentEntity(byte[] bytes, String contentType, String name, DocumentType type, LocalDateTime dateCreate, ClaimEntity claim) {
        this.bytes = bytes;
        this.contentType = contentType;
        this.name = name;
        this.type = type;
        this.dateCreate = dateCreate;
        this.claim = claim;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DocumentType getType() {
        return type;
    }

    public void setType(DocumentType type) {
        this.type = type;
    }

    public LocalDateTime getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(LocalDateTime dateCreate) {
        this.dateCreate = dateCreate;
    }

    public ClaimEntity getClaim() {
        return claim;
    }

    public void setClaim(ClaimEntity claim) {
        this.claim = claim;
    }
}
