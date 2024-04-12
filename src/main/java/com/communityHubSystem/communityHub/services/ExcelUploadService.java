package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface ExcelUploadService {
    boolean isValidExcelFile(MultipartFile file);
    List<User> getEmployeeDataFromExcel(InputStream inputStream);
}
