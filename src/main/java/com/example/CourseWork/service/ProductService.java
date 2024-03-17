package com.example.CourseWork.service;

import com.example.CourseWork.entity.Product;
import com.example.CourseWork.exceptions.ProductNotFoundException;
import com.example.CourseWork.exceptions.UserNotFoundException;
import com.example.CourseWork.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public static final int PRODUCTS_PER_PAGE =5;
    public static final int SEARCH_RESULTS_PER_PAGE =10;

    public List<Product> listAll() {
        return (List<Product>) productRepository.findAll();
    }

    public Product save(Product product) throws ProductNotFoundException {
        if(product.getId()==null){
           product.setCreatedTime(new Date());
        }else{
            product.setCreatedTime(get(product.getId()).getCreatedTime());
        }
        if(product.getAlias()==null||product.getAlias().isEmpty()){
            String defaultAlias=product.getName().replaceAll(" ","_");
            product.setAlias(defaultAlias);
        }else{
            product.setAlias(product.getAlias().replaceAll(" ","_"));
        }
        product.setUpdatedTime(new Date());
        return productRepository.save(product);
    }
    public Product updateAlies(Product product){
        return product;
    }
    public String checkUnique(Long id, String name) {
        boolean isCreatingNew = (id == null || id == 0);
        Product productByName = productRepository.findByName(name);

        if (isCreatingNew) {
            if (productByName != null) return "Duplicate";
        } else {
            if (productByName != null && productByName.getId() != id){
                return "Duplicate";
            }
        }
        return "OK";
    }

    public void changeStatus(Long id) throws UserNotFoundException {
        Long countById= productRepository.countById(id);
        if(countById==null||countById==0){
            throw new UserNotFoundException("Не может найти товар с таким ID" + id);
        }
        Product existingCategory=productRepository.findById(id).get();
        if (existingCategory.isEnabled()) {
            existingCategory.setEnabled(false);
        } else {
            existingCategory.setEnabled(true);
        }
        productRepository.save(existingCategory);
    }
    public void delete(Long id) throws ProductNotFoundException {
        Long countById=productRepository.countById(id);
        if(countById==null||countById==0){
            throw new ProductNotFoundException("Не может найти товар с таким id" + id);
        }
        productRepository.deleteById(id);
    }

    public Product get(Long id) throws ProductNotFoundException {
        try{
        return productRepository.findById(id).get();
    }catch(NoSuchElementException ex){
            throw new ProductNotFoundException("Не может найти продукт с таким именем");
        }
    }

    public Page<Product> listByPage(int pageNum, String sortField, String sortDir, String keyword){
        Sort sort= Sort.by(sortField);
        sort=sortDir.equals("asc")? sort.ascending():sort.descending();

        Pageable pageable= PageRequest.of(pageNum-1, PRODUCTS_PER_PAGE,sort);

        if(keyword !=null){
            return productRepository.findAll(keyword,pageable);
        }
        return productRepository.findAll(pageable);
    }
    public boolean findProductByCategoryID(Long id){
        List<Product> productList=(List<Product>) productRepository.findAll();
        for (Product p:productList) {
            if(p.getCategory().getId()==id) return true;
        }
        return false;
    }

    public Page<Product> listByCategory(int pageNum, Long categoryId) {
        String categoryIdMatch ="-" + String.valueOf(categoryId) +"-";
        Pageable pageable = PageRequest.of(pageNum -1 , PRODUCTS_PER_PAGE);
        return productRepository.listByCategory(categoryId, categoryIdMatch, pageable);
    }

    public Product getProduct(String alias) throws ProductNotFoundException {
        Product product=productRepository.findByAlias(alias);
        if(product==null){
            throw new ProductNotFoundException("Продукт не найден");
        }

        return product;
    }

    public Page<Product> search(String keyword, int pageNum) {
        Pageable pageable = PageRequest.of (pageNum - 1, SEARCH_RESULTS_PER_PAGE);
        return productRepository.search(keyword, pageable);

    }
}
