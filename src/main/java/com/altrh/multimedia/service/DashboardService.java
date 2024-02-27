package com.altrh.multimedia.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface DashboardService {
    ResponseEntity<Map<String, Object>> getCount();
}
