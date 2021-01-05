package ohlim.fooda.controller;

import ohlim.fooda.dto.RestaurantDto.*;
import ohlim.fooda.service.IncorrectParameterException;
import ohlim.fooda.service.RestImageNotFoundException;
import ohlim.fooda.service.RestaurantNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestaurantErrorAdvice {
    private Logger log = LoggerFactory.getLogger(ApplicationRunner.class);

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResRestaurantDto handlerNotFound(RestaurantNotFoundException e){
        log.error(e.getMessage(), e);
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("success", false);
        errorAttributes.put("msg", e.getMessage());
        return ResRestaurantDto.builder().meta(errorAttributes).build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IncorrectParameterException.class})
    public ResRestaurantDto handlerIncorrectParams(IncorrectParameterException e){
        log.error(e.getMessage(), e);
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("success", false);
        errorAttributes.put("msg", e.getMessage());
        return ResRestaurantDto.builder().meta(errorAttributes).build();
    }
}
