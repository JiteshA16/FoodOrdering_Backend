package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantDao restaurantDao;

    public List<RestaurantEntity> getAllRestaurants() {
        return restaurantDao.getAllRestaurants();
    }

    public List<RestaurantEntity> getRestaurantByName(String restaurantName) throws RestaurantNotFoundException {
        if (restaurantName.isEmpty() || restaurantName == null) {
            throw new RestaurantNotFoundException("RNF-003", "Restaurant name field should not be empty");
        }
        return restaurantDao.getRestaurantsByName(restaurantName);
    }

    public List<RestaurantEntity> getRestaurantsByCategoryId(CategoryEntity categoryEntity) throws CategoryNotFoundException {
        List<RestaurantCategoryEntity> restaurantCategoryEntities = restaurantDao.getRestaurantsByCategoryId(categoryEntity);

        List<RestaurantEntity> restaurantEntities = new ArrayList<>();

        for (RestaurantCategoryEntity restaurantCategoryEntity: restaurantCategoryEntities) {
            restaurantEntities.add(restaurantDao.getRestaurantById(restaurantCategoryEntity.getRestaurantId().getUuid()));
        }
        return restaurantEntities;
    }

    public RestaurantEntity getRestaurantById(String restaurantId) throws RestaurantNotFoundException {
        if (restaurantId.isEmpty() || restaurantId == null) {
            throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty");
        }
        RestaurantEntity restaurantEntity = restaurantDao.getRestaurantById(restaurantId);
        if (restaurantEntity == null) {
            throw new  RestaurantNotFoundException("RNF-001", "No restaurant by this id");
        }
        return restaurantEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public RestaurantEntity updateRestaurantEntity(RestaurantEntity restaurantEntity, Double customerRating) throws InvalidRatingException {
        if (customerRating == null || customerRating < 1.0 || customerRating > 5.0) {
            throw new  InvalidRatingException("IRE-001", "Restaurant rating should be in the range of 1 to 5");
        }
        double currentRestaurantRating = restaurantEntity.getCustomerRating();
        Integer numberOfCustomersRated = restaurantEntity.getNumberOfCustomersRated();

        restaurantEntity.setNumberOfCustomersRated(numberOfCustomersRated + 1);

        double newRating = ((currentRestaurantRating*numberOfCustomersRated)+customerRating)/numberOfCustomersRated;
        restaurantEntity.setCustomerRating(newRating);

        RestaurantEntity updatedRestaurantEntity = restaurantDao.updateRestaurantRating(restaurantEntity);
        return updatedRestaurantEntity;
    }
}
