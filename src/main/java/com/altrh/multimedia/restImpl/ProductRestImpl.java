package com.altrh.multimedia.restImpl;

import com.altrh.multimedia.constents.MultimediaConstants;
import com.altrh.multimedia.rest.ProductRest;
import com.altrh.multimedia.serviceImpl.ProductServiceImpl;
import com.altrh.multimedia.utils.MultimediaUtils;
import com.altrh.multimedia.wrapper.ProductWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ProductRestImpl implements ProductRest {
    @Autowired
    private ProductServiceImpl productService;
    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try{
            return productService.addNewProduct(requestMap);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return MultimediaUtils.getResponseEntity(MultimediaConstants.SOMTING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProduct() {
        try {
            return productService.getAllProduct();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try {
            return productService.updateProduct(requestMap);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return MultimediaUtils.getResponseEntity(MultimediaConstants.SOMTING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
       try {
            return productService.deleteProduct(id);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return MultimediaUtils.getResponseEntity(MultimediaConstants.SOMTING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try {
            return productService.updateStatus(requestMap);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return MultimediaUtils.getResponseEntity(MultimediaConstants.SOMTING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(Integer id) {
        try {
            return productService.getByCategory(id);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductWrapper> getProductById(Integer id) {
        try {
            return productService.getProductById(id);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ProductWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
