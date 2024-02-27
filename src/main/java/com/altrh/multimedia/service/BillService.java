package com.altrh.multimedia.service;

import com.altrh.multimedia.models.Bill;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


public interface BillService {
    ResponseEntity<String> generateReport(Map<String, Object> requestMap);
    ResponseEntity<List<Bill>> getBills();

    ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap);

    ResponseEntity<String> deleteBill(Integer id);

}
