package com.communityHubSystem.communityHub.services;
import org.apache.tomcat.util.net.jsse.JSSEUtil;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class ScheduledUploadService {

    private final ExcelUploadService excelUploadService;

    public ScheduledUploadService(ExcelUploadService excelUploadService) {
        this.excelUploadService = excelUploadService;
    }

    public void scheduleFixedRateTask() throws IOException {
        System.out.println("It reach here!");
        File file = new File("C:/Users/USER/Downloads/CommunityHub-data.xlsx");
        if (file.exists()) {
            System.out.println("success!!");
            try (FileInputStream input = new FileInputStream(file)) {
                MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", input);
                excelUploadService.uploadEmployeeData(multipartFile);
            }
        }
    }
}

