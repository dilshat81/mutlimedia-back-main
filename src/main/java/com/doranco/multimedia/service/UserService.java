package com.doranco.multimedia.service;

import com.doranco.multimedia.wrapper.UserRequest;
import com.doranco.multimedia.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserService {
    ResponseEntity<String> signUp(UserRequest userRequest);
    ResponseEntity<String> login(Map<String, String> requestMap);

    ResponseEntity<List<UserWrapper>> getAllUser();

    ResponseEntity<String> update(Map<String, String> requestMap);

    ResponseEntity<String> checkToken();

    ResponseEntity<String> changePassword(Map<String, String> requestMap);
    ResponseEntity<String> forgetPassword(Map<String, String> requestMap);
}
