package ru.ship.ShipHub.models.dto;

import ru.ship.ShipHub.util.DocumentType;

import java.time.LocalDateTime;

public class DocumentDTO {

    private long id;

    private String name;

    private DocumentType type;

    private String contentType;

    private LocalDateTime dateCreate;

    public DocumentDTO() {}

    public DocumentDTO(long id, String name, DocumentType type, String contentType, LocalDateTime dateCreate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.contentType = contentType;
        this.dateCreate = dateCreate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
