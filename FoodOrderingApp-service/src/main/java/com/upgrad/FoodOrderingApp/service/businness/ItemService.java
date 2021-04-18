package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemDao itemDao;

    public List<ItemEntity> getItemsForCategory(CategoryEntity categoryEntity) {
        List<CategoryItemEntity> categoryItemEntities = itemDao.getItemsByCategoryId(categoryEntity);
        List<ItemEntity> itemEntities = new ArrayList<>();

        for (CategoryItemEntity categoryItemEntity: categoryItemEntities) {
            itemEntities.add(getItemById(categoryItemEntity.getItemId().getUuid()));
        }
        return itemEntities;
    }

    private ItemEntity getItemById(String itemId) {
        return itemDao.getItemById(itemId);
    }

    public List<ItemEntity> getItemsByRestaurantAndCategory(RestaurantEntity restaurantEntity, CategoryEntity categoryEntity) {
        List<CategoryItemEntity> categoryItemEntities = itemDao.getItemsByCategoryId(categoryEntity);
        List<RestaurantItemEntity> restaurantItemEntities = itemDao.getItemsByRestaurant(restaurantEntity);

        List<ItemEntity> itemEntities = new ArrayList<>();

        restaurantItemEntities.forEach(restaurantItemEntity -> {
            categoryItemEntities.forEach(categoryItemEntity -> {
                if (restaurantItemEntity.getItemId().getUuid().equals(categoryItemEntity.getItemId().getUuid())) {
                    itemEntities.add(restaurantItemEntity.getItemId());
                }
            });
        });

        return itemEntities;
    }
}
