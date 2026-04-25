package com.example.medicinalplant.controller;

import com.example.medicinalplant.entity.PlantDiseaseLink;
import com.example.medicinalplant.service.PlantDiseaseLinkService;
import com.example.medicinalplant.common.Result;
import com.example.medicinalplant.util.UserIdThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 药用植物和病虫害关联控制器
 */
@RestController
@RequestMapping("/api/plant-disease-links")
@CrossOrigin
public class PlantDiseaseLinkController {

    @Autowired
    private PlantDiseaseLinkService plantDiseaseLinkService;

    /**
     * 分页查询关联关系
     */
    @GetMapping
    public Result getPage(@RequestParam(defaultValue = "1") int page,
                         @RequestParam(defaultValue = "10") int pageSize,
                         @RequestParam(required = false) String plantName,
                         @RequestParam(required = false) String diseaseName,
                         @RequestParam(required = false) String vulnerability) {
        try {
            List<PlantDiseaseLink> links = plantDiseaseLinkService.getPage(plantName, diseaseName, vulnerability, page, pageSize);
            int total = plantDiseaseLinkService.getCount(plantName, diseaseName, vulnerability);
            
            Map<String, Object> data = new HashMap<>();
            data.put("data", links);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);
            
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 添加关联关系
     */
    @PostMapping
    public Result add(@RequestBody PlantDiseaseLink link) {
        try {
            // 从ThreadLocal中获取当前用户ID
            Integer userId = UserIdThreadLocal.get();
            if (userId != null) {
                link.setCreatedBy(userId);
            }

            boolean success = plantDiseaseLinkService.add(link);
            if (success) {
                return Result.success("添加成功");
            } else {
                return Result.error("添加失败");
            }
        } catch (Exception e) {
            return Result.error("添加失败: " + e.getMessage());
        }
    }

    /**
     * 更新关联关系
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Integer id, @RequestBody PlantDiseaseLink link) {
        try {
            link.setId(id);
            boolean success = plantDiseaseLinkService.update(link);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除关联关系
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        try {
            boolean success = plantDiseaseLinkService.delete(id);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除关联关系
     */
    @DeleteMapping("/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        try {
            boolean success = plantDiseaseLinkService.batchDelete(ids);
            if (success) {
                return Result.success("批量删除成功");
            } else {
                return Result.error("批量删除失败");
            }
        } catch (Exception e) {
            return Result.error("批量删除失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询关联关系
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        try {
            PlantDiseaseLink link = plantDiseaseLinkService.getById(id);
            if (link != null) {
                return Result.success(link);
            } else {
                return Result.error("关联关系不存在");
            }
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据植物ID查询关联关系
     */
    @GetMapping("/plant/{plantId}")
    public Result getByPlantId(@PathVariable Integer plantId) {
        try {
            List<PlantDiseaseLink> links = plantDiseaseLinkService.getByPlantId(plantId);
            return Result.success(links);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据病虫害ID查询关联关系
     */
    @GetMapping("/disease/{pdId}")
    public Result getByPestDiseaseId(@PathVariable Integer pdId) {
        try {
            List<PlantDiseaseLink> links = plantDiseaseLinkService.getByPestDiseaseId(pdId);
            return Result.success(links);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据易感性查询关联关系
     */
    @GetMapping("/vulnerability/{vulnerability}")
    public Result getByVulnerability(@PathVariable String vulnerability) {
        try {
            List<PlantDiseaseLink> links = plantDiseaseLinkService.getByVulnerability(vulnerability);
            return Result.success(links);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 统计信息
     */
    @GetMapping("/statistics")
    public Result getStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalCount", plantDiseaseLinkService.getTotalCount());
            statistics.put("vulnerabilityDistribution", plantDiseaseLinkService.getVulnerabilityDistribution());
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error("获取统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 检查关联关系是否存在
     */
    @GetMapping("/exists")
    public Result checkExists(@RequestParam Integer plantId, @RequestParam Integer pdId) {
        try {
            boolean exists = plantDiseaseLinkService.exists(plantId, pdId);
            return Result.success(exists);
        } catch (Exception e) {
            return Result.error("检查失败: " + e.getMessage());
        }
    }
}
