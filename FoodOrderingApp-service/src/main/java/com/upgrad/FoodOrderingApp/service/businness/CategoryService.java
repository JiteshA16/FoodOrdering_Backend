package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ItemDao itemDao;

    public List<CategoryEntity> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    public CategoryEntity getCategoryById(String categoryId) throws CategoryNotFoundException {
        if (categoryId.isEmpty() || categoryId == null) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }
        CategoryEntity categoryEntity = categoryDao.getCategoryById(categoryId);
        if (categoryEntity == null) {
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }
        return categoryEntity;
    }

    public List<ItemEntity> getItemsForCategory(CategoryEntity categoryEntity) {
        List<CategoryItemEntity> categoryItemEntities = categoryDao.getItemsByCategoryId(categoryEntity);
        List<ItemEntity> itemEntities = new ArrayList<>();

        for (CategoryItemEntity categoryItemEntity: categoryItemEntities) {
            itemEntities.add(getItemById(categoryItemEntity.getItemId().getUuid()));
        }
        return itemEntities;
    }

    private ItemEntity getItemById(String itemId) {
        return itemDao.getItemById(itemId);
    }
}
