package ohlim.fooda.controller;

import ohlim.fooda.service.FolderNotFoundException;
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
public class FolderErrorAdvice {
    private Logger log = LoggerFactory.getLogger(ApplicationRunner.class);

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(FolderNotFoundException.class)
    public Map<String, String> handlerNotFound(FolderNotFoundException e){
        log.error(e.getMessage(), e);
        Map<String, String> errorAttributes = new HashMap<>();
        errorAttributes.put("code", "FOLDER_NOT_FOUND");
        errorAttributes.put("message", e.getMessage());
        return errorAttributes;
    }
}