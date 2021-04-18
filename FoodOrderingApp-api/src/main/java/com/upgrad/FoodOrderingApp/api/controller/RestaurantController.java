package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ItemService itemService;

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<RestaurantList>> getAllRestaurants() {
        List<RestaurantEntity> restaurantEntities = restaurantService.getAllRestaurants();

        List<RestaurantList> restaurantList = this.getRestaurantList(restaurantEntities);

        return new ResponseEntity<List<RestaurantList>>(restaurantList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/name/{reastaurant_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<RestaurantList>> getRestaurantsByName(@PathVariable("reastaurant_name") final String restaurantName) throws RestaurantNotFoundException {
        List<RestaurantEntity> restaurantEntities = restaurantService.getRestaurantByName(restaurantName);

        List<RestaurantList> restaurantList = this.getRestaurantList(restaurantEntities);

        return new ResponseEntity<List<RestaurantList>>(restaurantList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<RestaurantList>> getRestaurantsByCategoryId(@PathVariable("category_id") final String categoryId) throws CategoryNotFoundException {
        CategoryEntity categoryEntity = categoryService.getCategoryById(categoryId);

        List<RestaurantEntity> restaurantEntities = restaurantService.getRestaurantsByCategoryId(categoryEntity);

        List<RestaurantList> restaurantList = this.getRestaurantList(restaurantEntities);

        return new ResponseEntity<List<RestaurantList>>(restaurantList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/api/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> getRestaurantByRestaurantId(@PathVariable("restaurant_id") final String restaurantId) throws RestaurantNotFoundException {
        RestaurantEntity restaurantEntity = restaurantService.getRestaurantById(restaurantId);

        List<CategoryEntity> categoryEntities = categoryService.getCategoriesForRestaurant(restaurantEntity);

        List<CategoryList> categoryLists = new ArrayList<>();

        for (CategoryEntity categoryEntity: categoryEntities) {
            List<ItemEntity> itemEntities = itemService.getItemsByRestaurantAndCategory(restaurantEntity, categoryEntity);

            List<ItemList> itemLists = new ArrayList<>();
            for (ItemEntity itemEntity: itemEntities) {
                itemLists.add(new ItemList()
                        .id(UUID.fromString(itemEntity.getUuid()))
                        .itemName(itemEntity.getItemName())
                        .price(itemEntity.getPrice())
                        .itemType(ItemList.ItemTypeEnum.fromValue(itemEntity.getType().equals("0") ? "VEG" : "NON_VEG")));
            }

            categoryLists.add(new CategoryList()
                                    .id(UUID.fromString(categoryEntity.getUuid()))
                                    .categoryName(categoryEntity.getCategoryName())
                                    .itemList(itemLists));
        }

        RestaurantDetailsResponseAddressState addressStateResponse = this.getRestaurantState(restaurantEntity);

        RestaurantDetailsResponseAddress addressResponse = this.getRestaurantAddress(restaurantEntity, addressStateResponse);

        RestaurantDetailsResponse restaurantDetailsResponse = new RestaurantDetailsResponse()
                                                                        .id(UUID.fromString(restaurantEntity.getUuid()))
                                                                        .restaurantName(restaurantEntity.getRestaurantName())
                                                                        .photoURL(restaurantEntity.getPhotoUrl())
                                                                        .customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()))
                                                                        .averagePrice(restaurantEntity.getAveragePrice())
                                                                        .numberCustomersRated(restaurantEntity.getNumberOfCustomersRated())
                                                                        .address(addressResponse)
                                                                        .categories(categoryLists);

        return new ResponseEntity<RestaurantDetailsResponse>(restaurantDetailsResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/api/restaurant/{restaurant_id}", params = "customer_rating", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantUpdatedResponse> updateRestaurantDetails(@PathVariable("restaurant_id") final String restaurantId, @RequestHeader("authorization") final String accessToken, @RequestParam(value = "customer_rating") final Double customerRating) throws RestaurantNotFoundException, AuthorizationFailedException, InvalidRatingException {
        /*
        Need to perform customer auth token validation
         */

        RestaurantEntity restaurantEntity = restaurantService.getRestaurantById(restaurantId);

        RestaurantEntity updatedRestaurantEntity = restaurantService.updateRestaurantEntity(restaurantEntity, customerRating);

        RestaurantUpdatedResponse restaurantUpdatedResponse = new RestaurantUpdatedResponse()
                                                                    .id(UUID.fromString(restaurantId))
                                                                    .status("RESTAURANT RATING UPDATED SUCCESSFULLY");

        return new ResponseEntity<RestaurantUpdatedResponse>(restaurantUpdatedResponse, HttpStatus.OK);
    }

    private List<RestaurantList> getRestaurantList(List<RestaurantEntity> restaurantEntities) {
        List<RestaurantList> restaurantList = new ArrayList<>();

        for (RestaurantEntity restaurantEntity: restaurantEntities) {
            List<String> categoryList = new ArrayList<>();
            String categoryString = "";
            RestaurantDetailsResponseAddressState addressStateResponse = this.getRestaurantState(restaurantEntity);

            RestaurantDetailsResponseAddress addressResponse = this.getRestaurantAddress(restaurantEntity, addressStateResponse);

            List<CategoryEntity> categoryEntities = categoryService.getCategoriesForRestaurant(restaurantEntity);

            for (CategoryEntity categoryEntity: categoryEntities) {
                categoryList.add(categoryEntity.getCategoryName());
            }

            if (categoryList.size() > 0) {
                Collections.sort(categoryList);
                categoryString = String.join(", ", categoryList);
            }

            restaurantList.add(new RestaurantList()
                    .id(UUID.fromString(restaurantEntity.getUuid()))
                    .restaurantName(restaurantEntity.getRestaurantName())
                    .photoURL(restaurantEntity.getPhotoUrl())
                    .customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()))
                    .averagePrice(restaurantEntity.getAveragePrice())
                    .numberCustomersRated(restaurantEntity.getNumberOfCustomersRated())
                    .address(addressResponse)
                    .categories(categoryString));
        }
        return restaurantList;
    }

    private RestaurantDetailsResponseAddressState getRestaurantState(RestaurantEntity restaurantEntity) {
        return new RestaurantDetailsResponseAddressState()
                        .id(UUID.fromString(restaurantEntity.getAddress().getState().getUuid()))
                        .stateName(restaurantEntity.getAddress().getState().getStateName());
    }

    private RestaurantDetailsResponseAddress getRestaurantAddress(RestaurantEntity restaurantEntity, RestaurantDetailsResponseAddressState addressStateResponse) {
        return new RestaurantDetailsResponseAddress()
                            .id(UUID.fromString(restaurantEntity.getAddress().getUuid()))
                            .flatBuildingName(restaurantEntity.getAddress().getFlatBuilNumber())
                            .locality(restaurantEntity.getAddress().getLocality())
                            .city(restaurantEntity.getAddress().getCity())
                            .pincode(restaurantEntity.getAddress().getPincode())
                            .state(addressStateResponse);
    }

}
