package com.doranco.multimedia.models;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;


import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Document(collection = "factures")
public class Bill implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String uuid;
    private String name;
    private String email;
    private String contactNumber;
    private String paymentMethod;
    private Integer total;
    private String productDetail;
    private String createdBy;
    private LocalDateTime createdAt;
    private byte[] pdfData;
}
