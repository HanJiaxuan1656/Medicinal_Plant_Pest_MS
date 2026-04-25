package com.example.medicinalplant.controller;

import com.example.medicinalplant.common.Result;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final Path fileStorageLocation;

    public FileController() {
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "images";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                System.out.println("创建上传目录成功: " + uploadDir);
            } else {
                System.err.println("创建上传目录失败: " + uploadDir);
            }
        } else {
            System.out.println("上传目录已存在: " + uploadDir);
        }
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        System.out.println("文件存储位置: " + this.fileStorageLocation);
    }

    @GetMapping("/test")
    public Result<String> testConnection() {
        return Result.success("文件服务连接正常");
    }

    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                return Result.error("上传文件不能为空");
            }

            // 检查文件大小（限制为10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                return Result.error("文件大小不能超过10MB");
            }

            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只能上传图片文件");
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + fileExtension;

            // 保存文件
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 返回文件访问URL
            String fileUrl = "http://localhost:8080/api/files/download/" + fileName;
            System.out.println("文件上传成功: " + fileUrl);
            return Result.success(fileUrl);

        } catch (IOException ex) {
            System.err.println("文件上传失败: " + ex.getMessage());
            ex.printStackTrace();
            return Result.error("文件上传失败: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("文件上传异常: " + ex.getMessage());
            ex.printStackTrace();
            return Result.error("文件上传异常: " + ex.getMessage());
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                // 对文件名进行URL编码以支持中文字符
                String encodedFileName = URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8)
                        .replaceAll("\\+", "%20");

                return ResponseEntity.ok()
                       .contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
                       .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                       .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException ex) {
            return ResponseEntity.status(500).body(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}