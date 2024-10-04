package com.camelsoft.portal.files;

import com.camelsoft.portal.customExceptions.FileStorageException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {
    @Value("${application.file.upload.photos-output-path}")
    private String photosUploadPath;

    @Value("${application.file.upload.cvs-output-path}")
    private String cvsUploadPath;

    public String saveFile(@NonNull MultipartFile sourceFile, @NonNull Integer userId, @NonNull String fileType) {
        validateFileType(sourceFile, fileType);  // Validate file type before proceeding
        String fileUploadSubPath = buildFileUploadSubPath(userId, fileType);
        return uploadFile(sourceFile, fileUploadSubPath);
    }

    private void validateFileType(MultipartFile file, String expectedType) {
        String fileExtension = getFileExtension(file.getOriginalFilename());
        if ("avatar".equalsIgnoreCase(expectedType)) {
            if (!List.of("jpg", "jpeg", "png").contains(fileExtension)) {
                throw new FileStorageException("Invalid file type for avatar. Only JPG, JPEG, and PNG are allowed.");
            }
        } else if ("cv".equalsIgnoreCase(expectedType)) {
            if (!"pdf".equals(fileExtension)) {
                throw new FileStorageException("Invalid file type for CV. Only PDF is allowed.");
            }
        } else {
            throw new FileStorageException("Unknown file type: " + expectedType);
        }
    }

    private String buildFileUploadSubPath(Integer userId, String fileType) {
        if ("avatar".equalsIgnoreCase(fileType)) {
            return "users" + File.separator + userId + File.separator + "avatars";
        } else if ("cv".equalsIgnoreCase(fileType)) {
            return "users" + File.separator + userId + File.separator + "cvs";
        } else {
            throw new FileStorageException("Unknown file type: " + fileType);
        }
    }

    private String uploadFile(@NonNull MultipartFile sourceFile, @NonNull String fileUploadSubPath) {
        final String finalUploadPath = (fileUploadSubPath.contains("cvs") ? cvsUploadPath : photosUploadPath) + File.separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);
        if (!targetFolder.exists()) {
            boolean folderCreated = targetFolder.mkdirs();
            if (!folderCreated) {
                log.warn("Failed to create the target folder");
                return null;
            }
        }
        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        String targetFilePath = finalUploadPath + File.separator + System.currentTimeMillis() + "." + fileExtension;
        Path targetPath = Paths.get(targetFilePath);
        try {
            Files.write(targetPath, sourceFile.getBytes());
            log.info("File saved to " + targetFilePath);
            return targetFilePath;
        } catch (IOException e) {
            log.warn("File was not saved", e);
        }
        return null;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return null;
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }
}