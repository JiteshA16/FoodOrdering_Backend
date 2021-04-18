package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class CategoryDao {
    @PersistenceContext
    private EntityManager entityManager;

    public List<CategoryEntity> getAllCategories() {
        try {
            List<CategoryEntity> categoryList = entityManager.createNamedQuery("getAllCategoriesSortedByName", CategoryEntity.class)
                                                             .getResultList();
            return categoryList;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CategoryEntity getCategoryById(String uuid) {
        try {
            CategoryEntity categoryEntity = entityManager.createNamedQuery("getCategoryById", CategoryEntity.class)
                                                         .setParameter("uuid", uuid)
                                                         .getSingleResult();
            return categoryEntity;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<RestaurantCategoryEntity> getCategoriesByRestaurant(RestaurantEntity restaurantEntity) {
        try {
            List<RestaurantCategoryEntity> restaurantCategoryEntities = entityManager.createNamedQuery("getCategoriesByRestaurant", RestaurantCategoryEntity.class)
                                                                                     .setParameter("restaurantId", restaurantEntity)
                                                                                     .getResultList();
            return restaurantCategoryEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }

}
