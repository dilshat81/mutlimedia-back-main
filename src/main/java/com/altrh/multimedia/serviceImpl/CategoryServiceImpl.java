package com.altrh.multimedia.serviceImpl;

import com.altrh.multimedia.constents.MultimediaConstants;
import com.altrh.multimedia.jwt.JwtFilter;
import com.altrh.multimedia.models.Category;
import com.altrh.multimedia.repositories.CategoryDao;
import com.altrh.multimedia.service.CategoryService;
import com.altrh.multimedia.utils.MultimediaUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryDao categoryDao;

    @Autowired
    JwtFilter jwtFilter;
    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
                if(validateCategoryMap(requestMap, false)){
                    categoryDao.save(getCategoryFormMap(requestMap, false));
                    return MultimediaUtils.getResponseEntity("Catégorie ajoutée avec succès", HttpStatus.OK);
                }
            }else{
                return MultimediaUtils.getResponseEntity(MultimediaConstants.UNAUTHORIZED_ACCES, HttpStatus.UNAUTHORIZED);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return MultimediaUtils.getResponseEntity(MultimediaConstants.SOMTING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        try {
            if(!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")){
                log.info("Inside");
                return new ResponseEntity<List<Category>>(categoryDao.getAllCategory(), HttpStatus.OK);
            }
            return new ResponseEntity(categoryDao.findAll(), HttpStatus.OK);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<List<Category>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
                if(validateCategoryMap(requestMap,true)){
                   Optional optional = categoryDao.findById(Integer.parseInt(requestMap.get("id")));
                   if(!optional.isEmpty()){
                        categoryDao.save(getCategoryFormMap(requestMap, true));
                       return MultimediaUtils.getResponseEntity("Catégorie mise à jour avec succès", HttpStatus.OK);
                   }else{
                       return MultimediaUtils.getResponseEntity("Catégorie id n'existe pas", HttpStatus.OK);
                   }
                }
                return MultimediaUtils.getResponseEntity(MultimediaConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }else{
                return MultimediaUtils.getResponseEntity(MultimediaConstants.UNAUTHORIZED_ACCES, HttpStatus.BAD_REQUEST);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return MultimediaUtils.getResponseEntity(MultimediaConstants.SOMTING_WENT_WRONG, HttpStatus.UNAUTHORIZED);
    }


    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {
        if(requestMap.containsKey("name")){
            if(requestMap.containsKey("id") && validateId){
                return true;
            }else if(!validateId){
                return true;
            }
        }
            return false;
    }

    private Category getCategoryFormMap(Map<String, String> requestMap, Boolean isAdd){
        Category category = new Category();
        if(isAdd){
            category.setId(Integer.parseInt(requestMap.get("id")));
        }
        category.setName(requestMap.get("name"));
        return category;
    }
}
