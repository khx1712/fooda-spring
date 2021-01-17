package ohlim.fooda.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

// TODO: multipart/form-data 문제가 생겼을 때 추가한 코드로 없어도 동작이 잘된다.
//  이것이 정확하게 어떤 역할을 하는지 공부한 뒤에 제거하자
//@Component
//public class MultipartJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {
//
//    /**
//     * Converter for support http request with header Content-Type: multipart/form-data
//     */
//    public MultipartJackson2HttpMessageConverter(ObjectMapper objectMapper) {
//        super(objectMapper, MediaType.APPLICATION_OCTET_STREAM);
//    }
//}