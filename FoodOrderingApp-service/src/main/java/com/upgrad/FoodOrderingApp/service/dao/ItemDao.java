package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ItemDao {

    @PersistenceContext
    private EntityManager entityManager;

    public ItemEntity getItemById(String itemId) {
        try {
            ItemEntity itemEntity = entityManager.createNamedQuery("getItemById", ItemEntity.class)
                                                 .setParameter("uuid", itemId)
                                                 .getSingleResult();
            return itemEntity;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<CategoryItemEntity> getItemsByCategoryId(CategoryEntity categoryEntity) {
        try {
            List<CategoryItemEntity> categoryItemEntities = entityManager.createNamedQuery("getItemsByCategoryId", CategoryItemEntity.class)
                                                                         .setParameter("categoryId", categoryEntity)
                                                                         .getResultList();
            return categoryItemEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<RestaurantItemEntity> getItemsByRestaurant(RestaurantEntity restaurantEntity) {
        try {
            List<RestaurantItemEntity> restaurantItemEntities = entityManager.createNamedQuery("getItemsByRestaurant", RestaurantItemEntity.class)
                                                                             .setParameter("restaurantId", restaurantEntity)
                                                                             .getResultList();
            return restaurantItemEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }
}
