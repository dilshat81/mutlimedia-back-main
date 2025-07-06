package com.doranco.multimedia.models;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;


import java.io.Serializable;


//@NamedQuery(name="Bill.getAllBills", query="select b from Bill b order by b.id desc")
//@NamedQuery(name="Bill.getAllBillByUserName", query="select b from Bill b where b.createdBy=:username order by b.id desc")

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
    private String paymentMethode;
    private Integer total;
    private String productDetail;
    private String createdBy;
    private byte[] pdfData;
}
