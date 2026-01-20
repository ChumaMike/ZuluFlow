package com.zuluflow.kyc.api.controller;

import com.zuluflow.kyc.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/kyc")
@RequiredArgsConstructor
public class KycController {

    private final StorageService storageService;

    // POST /api/v1/kyc/upload
    // Consumes: multipart/form-data (Standard file upload)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadDocument(@RequestParam("file") MultipartFile file) {

        // 1. Send to MinIO
        String filename = storageService.uploadFile(file);

        // 2. Return the reference ID (In a real app, we would save this to the DB now)
        return ResponseEntity.ok(Map.of(
                "message", "Upload successful",
                "documentReference", filename
        ));
    }
}