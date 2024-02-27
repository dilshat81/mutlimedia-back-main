package com.altrh.multimedia.serviceImpl;

import com.altrh.multimedia.constents.MultimediaConstants;
import com.altrh.multimedia.jwt.JwtFilter;
import com.altrh.multimedia.models.Category;
import com.altrh.multimedia.models.Product;
import com.altrh.multimedia.repositories.ProductDao;
import com.altrh.multimedia.service.ProductService;
import com.altrh.multimedia.utils.MultimediaUtils;
import com.altrh.multimedia.wrapper.ProductWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductDao productDao;
    @Autowired
    private JwtFilter jwtFilter;
    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateProductMap(requestMap, false)){
                    productDao.save(getProductForm(requestMap, false));
                    return MultimediaUtils.getResponseEntity("Product added Successfully", HttpStatus.OK);
                }
                return MultimediaUtils.getResponseEntity(MultimediaConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }else{
               return MultimediaUtils.getResponseEntity(MultimediaConstants.UNAUTHORIZED_ACCES, HttpStatus.UNAUTHORIZED);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return MultimediaUtils.getResponseEntity(MultimediaConstants.SOMTING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private Product getProductForm(Map<String, String> requestMap, boolean isAdd) {
        Category category = new Category();
        category.setId(Integer.parseInt(requestMap.get("categoryId")));

        Product product = new Product();

        if(isAdd){
            product.setId(Integer.parseInt(requestMap.get("id")));
        }else{
            product.setStatus("true");
        }
        product.setCategory(category);
        product.setName(requestMap.get("name"));
        product.setDescription(requestMap.get("description"));
        product.setPrice(Integer.parseInt(requestMap.get("price")));
        return product;
    }

    private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {

        if(requestMap.containsKey("name")){
            if(requestMap.containsKey("id") && validateId){
                return true;
            }else if (!validateId){
                return true;
            }
        }
        return false;
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProduct() {
        try {

            return new ResponseEntity<>(productDao.getAllProduct(), HttpStatus.OK);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
               if(validateProductMap(requestMap, true)){
                  Optional<Product> optional = productDao.findById(Integer.parseInt(requestMap.get("id")));
                  if(!optional.isEmpty()){
                      Product product = getProductForm(requestMap, true);
                      product.setStatus(optional.get().getStatus());
                      productDao.save(product);
                      return MultimediaUtils.getResponseEntity("Product updated successfully", HttpStatus.OK);
                  }else{
                      return MultimediaUtils.getResponseEntity("Product id is does not exist", HttpStatus.OK);
                  }
                }else{
                    return MultimediaUtils.getResponseEntity(MultimediaConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<String> deleteProduct(Integer id) {
        try {
            if(jwtFilter.isAdmin()){
                Optional optional = productDao.findById(id);
                if(!optional.isEmpty()){
                    productDao.deleteById(id);
                    return MultimediaUtils.getResponseEntity("Product deleted Successfully", HttpStatus.OK);
                }else{
                    return  MultimediaUtils.getResponseEntity("Product id does not exist", HttpStatus.OK);
                }
            }else{
                return MultimediaUtils.getResponseEntity(MultimediaConstants.UNAUTHORIZED_ACCES, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return MultimediaUtils.getResponseEntity(MultimediaConstants.SOMTING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
                Optional optional = productDao.findById(Integer.parseInt(requestMap.get(("id"))));
                if(!optional.isEmpty()){
                    productDao.updateProductStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    return MultimediaUtils.getResponseEntity("Product status updated successfully", HttpStatus.OK);
                }else{
                    return  MultimediaUtils.getResponseEntity("Product id does not exist", HttpStatus.OK);
                }
            }else{
                return MultimediaUtils.getResponseEntity(MultimediaConstants.UNAUTHORIZED_ACCES, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return MultimediaUtils.getResponseEntity(MultimediaConstants.SOMTING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(Integer id) {
        try {
            return new ResponseEntity<>(productDao.getProductByCategory(id), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductWrapper> getProductById(Integer id) {
        try {

                return new ResponseEntity<>(productDao.getProductById(id), HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ProductWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
