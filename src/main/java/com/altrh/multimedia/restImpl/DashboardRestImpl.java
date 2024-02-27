package com.altrh.multimedia.restImpl;

import com.altrh.multimedia.rest.DashboardRest;
import com.altrh.multimedia.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
public class DashboardRestImpl implements DashboardRest {
    @Autowired
    private DashboardService dashboardService;
    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        return dashboardService.getCount();
    }
}
