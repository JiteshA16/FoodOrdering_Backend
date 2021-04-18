package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RestaurantDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<RestaurantEntity> getAllRestaurants() {
        try {
            List<RestaurantEntity> restaurantList = entityManager.createNamedQuery("getAllRestaurants", RestaurantEntity.class).getResultList();
            return restaurantList;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<RestaurantEntity> getRestaurantsByName(String restaurantName) {
        try {
            List<RestaurantEntity> restaurantList = entityManager.createNamedQuery("getRestaurantsByName", RestaurantEntity.class)
                                                                 .setParameter("restaurantName", restaurantName.toLowerCase())
                                                                 .getResultList();
            return restaurantList;
        } catch (NoResultException nre) {
            return null;
        }
    }
}
