package com.example.medicinalplant.controller;

import com.example.medicinalplant.common.Result;
import com.example.medicinalplant.entity.Pesticide;
import com.example.medicinalplant.entity.ExpertUser;
import com.example.medicinalplant.mapper.ExpertUserMapper;
import com.example.medicinalplant.service.PesticideService;
import com.example.medicinalplant.util.UserIdThreadLocal;
import com.example.medicinalplant.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/pesticides")
public class PesticideController {
    
    @Autowired
    private PesticideService pesticideService;
    
    @Autowired
    private ExpertUserMapper expertUserMapper;
    
    /**
     * 获取所有农药列表
     */
    @GetMapping("/all")
    public Result<List<Pesticide>> getAllPesticides() {
        List<Pesticide> pesticides = pesticideService.getAllPesticides();
        return Result.success(pesticides);
    }
    
    /**
     * 根据ID获取农药详情
     */
    @GetMapping("/{id}")
    public Result<Pesticide> getById(@PathVariable Integer id) {
        Pesticide pesticide = pesticideService.getById(id);
        if (pesticide != null) {
            return Result.success(pesticide);
        }
        return Result.error("未找到该农药");
    }
    
    /**
     * 添加农药
     */
    @PostMapping
    public Result<Boolean> add(@RequestBody Pesticide pesticide) {
        // 从ThreadLocal中获取当前登录用户ID
        Integer userId = UserIdThreadLocal.get();
        if (userId != null) {
            // 验证用户是否为专家用户
            ExpertUser expertUser = expertUserMapper.findById(userId);
            if (expertUser != null) {
                pesticide.setCreatedBy(userId);
                boolean success = pesticideService.add(pesticide);
                return Result.success(success);
            }
        }
        return Result.error("只有专家用户才能添加农药信息");
    }
    
    /**
     * 更新农药信息
     */
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Integer id, @RequestBody Pesticide pesticide) {
        pesticide.setId(id);
        boolean success = pesticideService.update(pesticide);
        return Result.success(success);
    }
    
    /**
     * 删除农药
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Integer id) {
        boolean success = pesticideService.delete(id);
        return Result.success(success);
    }

    /**
     * 批量删除农药
     */
    @DeleteMapping("/batch")
    public Result<String> deleteByIds(@RequestBody List<Integer> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return Result.error("请选择要删除的农药");
            }
            boolean success = pesticideService.deleteByIds(ids);
            if (success) {
                return Result.success("批量删除成功");
            } else {
                return Result.error("批量删除失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 上传农药图片
     */
    @PostMapping("/upload/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = pesticideService.uploadImage(file);
            return Result.success(imageUrl);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 上传农药使用说明书
     */
    @PostMapping("/upload/manual")
    public Result<String> uploadManual(@RequestParam("file") MultipartFile file) {
        try {
            String manualUrl = pesticideService.uploadManual(file);
            return Result.success(manualUrl);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据类别获取农药列表
     */
    @GetMapping("/category/{category}")
    public Result<List<Pesticide>> getPesticidesByCategory(@PathVariable String category) {
        List<Pesticide> pesticides = pesticideService.getPesticidesByCategory(category);
        return Result.success(pesticides);
    }
    
    /**
     * 搜索农药
     */
    @GetMapping("/search")
    public Result<List<Pesticide>> searchPesticides(@RequestParam String keyword) {
        List<Pesticide> pesticides = pesticideService.searchPesticides(keyword);
        return Result.success(pesticides);
    }

    @GetMapping
    public Result<PageVO<Pesticide>> getPage(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageVO<Pesticide> pageVO = pesticideService.getPage(search, category, page, pageSize);
        return Result.success(pageVO);
    }
} 