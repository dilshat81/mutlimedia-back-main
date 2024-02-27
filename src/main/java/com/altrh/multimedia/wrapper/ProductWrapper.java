package com.altrh.multimedia.wrapper;

import com.altrh.multimedia.models.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductWrapper {

    private Integer id;
    private String name;
    private String description;
    private Integer price;
    private String status;
    private Integer categoryId;
    private String categoryName;

    public ProductWrapper(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public ProductWrapper(Integer id, String name, String description, Integer price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
