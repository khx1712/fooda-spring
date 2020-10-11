package ohlim.fooda.controller;

import ohlim.fooda.domain.RestImage;
import ohlim.fooda.dto.FolderDto;
import ohlim.fooda.dto.RestImageDto;
import ohlim.fooda.service.RestImageNotFoundException;
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
public class RestImageErrorAdvice {
    private Logger log = LoggerFactory.getLogger(ApplicationRunner.class);

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(RestImageNotFoundException.class)
    public RestImageDto.ResRestImageDto handlerNotFound(RestImageNotFoundException e){
        log.error(e.getMessage(), e);
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("success", false);
        errorAttributes.put("msg", e.getMessage());
        return RestImageDto.ResRestImageDto.builder().meta(errorAttributes).build();
    }
}