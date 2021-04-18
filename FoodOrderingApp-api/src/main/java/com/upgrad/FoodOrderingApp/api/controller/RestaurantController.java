package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

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

    private List<RestaurantList> getRestaurantList(List<RestaurantEntity> restaurantEntities) {
        List<RestaurantList> restaurantList = new ArrayList<>();

        for (RestaurantEntity restaurantEntity: restaurantEntities) {
            RestaurantDetailsResponseAddressState addressStateResponse = new RestaurantDetailsResponseAddressState();
            addressStateResponse.setId(UUID.fromString(restaurantEntity.getAddress().getState().getUuid()));
            addressStateResponse.setStateName(restaurantEntity.getAddress().getState().getStateName());

            RestaurantDetailsResponseAddress addressResponse = new RestaurantDetailsResponseAddress();
            addressResponse.setId(UUID.fromString(restaurantEntity.getAddress().getUuid()));
            addressResponse.setFlatBuildingName(restaurantEntity.getAddress().getFlatBuilNumber());
            addressResponse.setLocality(restaurantEntity.getAddress().getLocality());
            addressResponse.setCity(restaurantEntity.getAddress().getCity());
            addressResponse.setPincode(restaurantEntity.getAddress().getPincode());
            addressResponse.setState(addressStateResponse);

            restaurantList.add(new RestaurantList()
                    .id(UUID.fromString(restaurantEntity.getUuid()))
                    .restaurantName(restaurantEntity.getRestaurantName())
                    .photoURL(restaurantEntity.getPhotoUrl())
                    .customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()))
                    .averagePrice(restaurantEntity.getAveragePrice())
                    .numberCustomersRated(restaurantEntity.getNumberOfCustomersRated())
                    .address(addressResponse)
                    .categories("Hello, wadasdasdsad"));
        }
        return restaurantList;
    }

}
