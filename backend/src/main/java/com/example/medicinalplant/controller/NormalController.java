package com.example.medicinalplant.controller;

import com.example.medicinalplant.entity.Comment;
import com.example.medicinalplant.entity.ExpertUser;
import com.example.medicinalplant.entity.HelpReply;
import com.example.medicinalplant.entity.HelpRequest;
import com.example.medicinalplant.entity.MedicinalPlant;
import com.example.medicinalplant.entity.NormalUser;
import com.example.medicinalplant.entity.PestDisease;
import com.example.medicinalplant.entity.Pesticide;
import lombok.extern.slf4j.Slf4j;
import com.example.medicinalplant.mapper.ExpertUserMapper;
import com.example.medicinalplant.mapper.HelpRequestMapper;
import com.example.medicinalplant.mapper.NormalUserMapper;
import com.example.medicinalplant.mapper.PlantDiseaseLinkMapper;
import com.example.medicinalplant.mapper.CommentMapper;
import com.example.medicinalplant.mapper.DiseasePesticideLinkMapper;
import com.example.medicinalplant.service.PlantService;
import com.example.medicinalplant.service.PestDiseaseService;
import com.example.medicinalplant.service.PesticideService;
import com.example.medicinalplant.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/normal")
@CrossOrigin(origins = "*")
public class NormalController {

    @Autowired
    private PlantService plantService;

    @Autowired
    private PestDiseaseService pestDiseaseService;

    @Autowired
    private PesticideService pesticideService;

    @Autowired
    private HelpRequestMapper helpRequestMapper;

    @Autowired
    private ExpertUserMapper expertUserMapper;

    @Autowired
    private NormalUserMapper normalUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PlantDiseaseLinkMapper plantDiseaseLinkMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private DiseasePesticideLinkMapper diseasePesticideLinkMapper;

    /**
     * 获取药用植物列表
     */
    @GetMapping("/plants")
    public Result<Map<String, Object>> getPlants(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int pageSize,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String medicinalParts,
            @RequestParam(required = false) String medicinalPart,
            @RequestParam(required = false) String part,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "default") String sortBy,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String orderBy) {

        try {
            // 获取所有植物
            List<MedicinalPlant> plants = plantService.getAllPlants();

            // 搜索功能 - 只按名称搜索
            String searchKeyword = search != null ? search : (keyword != null ? keyword : name);
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                searchKeyword = searchKeyword.trim();
                final String finalKeyword = searchKeyword;
                plants = plants.stream()
                    .filter(plant ->
                        plant.getName() != null && plant.getName().contains(finalKeyword)
                    )
                    .collect(java.util.stream.Collectors.toList());
            }

            // 药用部位筛选 - 支持多个参数名，中文转英文
            String partFilter = medicinalParts != null ? medicinalParts : (medicinalPart != null ? medicinalPart : part);
            if (partFilter != null && !partFilter.trim().isEmpty()) {
                partFilter = partFilter.trim();

                // 中文药用部位转换为英文（数据库存储的是英文）
                String englishPart = convertChineseToEnglishPart(partFilter);
                final String finalPart = englishPart;

                plants = plants.stream()
                    .filter(plant -> plant.getMedicinalParts() != null &&
                        (plant.getMedicinalParts().equals(finalPart) ||
                         plant.getMedicinalParts().contains(finalPart)))
                    .collect(java.util.stream.Collectors.toList());
            }

            // 排序功能 - 支持多个参数名
            String sortField = sortBy != null && !sortBy.equals("default") ? sortBy :
                              (sort != null ? sort : orderBy);
            if (sortField != null && !sortField.equals("default")) {
                switch (sortField) {
                    case "name":
                        plants.sort((a, b) -> {
                            String nameA = a.getName() != null ? a.getName() : "";
                            String nameB = b.getName() != null ? b.getName() : "";
                            return nameA.compareTo(nameB);
                        });
                        break;
                    case "created_at":
                    case "createdAt":
                        plants.sort((a, b) -> {
                            if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                            if (a.getCreatedAt() == null) return 1;
                            if (b.getCreatedAt() == null) return -1;
                            return b.getCreatedAt().compareTo(a.getCreatedAt()); // 降序
                        });
                        break;
                    case "view_count":
                    case "viewCount":
                        plants.sort((a, b) -> {
                            Integer viewA = a.getViewCount() != null ? a.getViewCount() : 0;
                            Integer viewB = b.getViewCount() != null ? b.getViewCount() : 0;
                            return viewB.compareTo(viewA); // 降序
                        });
                        break;
                }
            }

            // 分页处理
            int total = plants.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);

            List<MedicinalPlant> pagedPlants = start < total ? plants.subList(start, end) : java.util.Collections.emptyList();

            // 为每个植物添加评论数量
            List<Map<String, Object>> plantsWithCommentCount = new ArrayList<>();
            for (MedicinalPlant plant : pagedPlants) {
                Map<String, Object> plantMap = new HashMap<>();
                plantMap.put("id", plant.getId());
                plantMap.put("name", plant.getName());
                plantMap.put("alias", plant.getAlias());
                plantMap.put("description", plant.getDescription());
                plantMap.put("imageUrl", plant.getImageUrl());
                plantMap.put("medicinalParts", plant.getMedicinalParts());
                plantMap.put("efficacy", plant.getEfficacy());
                plantMap.put("viewCount", plant.getViewCount());
                plantMap.put("createdAt", plant.getCreatedAt());
                plantMap.put("updatedAt", plant.getUpdatedAt());
                plantMap.put("createdBy", plant.getCreatedBy());

                // 获取该植物的评论数量（只统计已审核通过的评论）
                List<Comment> plantComments = commentMapper.findByTargetTypeAndTargetId("plant", plant.getId());
                long commentCount = plantComments.stream()
                    .filter(comment -> "approved".equals(comment.getStatus()))
                    .count();
                plantMap.put("commentCount", commentCount);

                plantsWithCommentCount.add(plantMap);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("list", plantsWithCommentCount);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);

            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取植物列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取植物详情
     */
    @GetMapping("/plants/{id}")
    public Result<Map<String, Object>> getPlantDetail(@PathVariable Long id) {
        try {
            log.info("获取植物详情，植物ID: {}", id);

            MedicinalPlant plant = plantService.getPlantById(id.intValue());
            if (plant == null) {
                return Result.error("植物不存在");
            }

            // 增加查看次数
            plantService.incrementViewCount(id.intValue());

            // 获取相关病虫害信息
            List<Map<String, Object>> relatedDiseases = getRelatedDiseases(id.intValue());
            log.info("植物ID: {} 的相关病虫害数量: {}", id, relatedDiseases.size());

            Map<String, Object> data = new HashMap<>();
            data.put("plant", plant);
            data.put("relatedDiseases", relatedDiseases);

            return Result.success(data);
        } catch (Exception e) {
            log.error("获取植物详情异常: {}", e.getMessage(), e);
            return Result.error("获取植物详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取病虫害列表
     */
    @GetMapping("/pest-diseases")
    public Result<Map<String, Object>> getPestDiseases(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int pageSize,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "default") String sortBy,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String orderBy) {

        try {
            List<PestDisease> pestDiseases = pestDiseaseService.getAllPestDiseases();

            // 搜索功能 - 只按名称搜索
            String searchKeyword = search != null ? search : (keyword != null ? keyword : name);
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                searchKeyword = searchKeyword.trim();
                final String finalKeyword = searchKeyword;
                pestDiseases = pestDiseases.stream()
                    .filter(pd ->
                        pd.getName() != null && pd.getName().contains(finalKeyword)
                    )
                    .collect(java.util.stream.Collectors.toList());
            }

            // 类型筛选
            if (type != null && !type.trim().isEmpty()) {
                type = type.trim();
                final String finalType = type;
                pestDiseases = pestDiseases.stream()
                    .filter(pd -> pd.getType() != null && pd.getType().equals(finalType))
                    .collect(java.util.stream.Collectors.toList());
            }

            // 排序功能
            String sortField = sortBy != null && !sortBy.equals("default") ? sortBy :
                              (sort != null ? sort : orderBy);
            if (sortField != null && !sortField.equals("default")) {
                switch (sortField) {
                    case "name":
                        pestDiseases.sort((a, b) -> {
                            String nameA = a.getName() != null ? a.getName() : "";
                            String nameB = b.getName() != null ? b.getName() : "";
                            return nameA.compareTo(nameB);
                        });
                        break;
                    case "created_at":
                    case "createdAt":
                        pestDiseases.sort((a, b) -> {
                            if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                            if (a.getCreatedAt() == null) return 1;
                            if (b.getCreatedAt() == null) return -1;
                            return b.getCreatedAt().compareTo(a.getCreatedAt()); // 降序
                        });
                        break;
                    case "view_count":
                    case "viewCount":
                        pestDiseases.sort((a, b) -> {
                            Integer viewA = a.getViewCount() != null ? a.getViewCount() : 0;
                            Integer viewB = b.getViewCount() != null ? b.getViewCount() : 0;
                            return viewB.compareTo(viewA); // 降序
                        });
                        break;
                }
            }

            // 分页处理
            int total = pestDiseases.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);

            List<PestDisease> pagedPestDiseases = start < total ? pestDiseases.subList(start, end) : java.util.Collections.emptyList();

            // 为每个病虫害添加评论数量
            List<Map<String, Object>> pestDiseasesWithCommentCount = new ArrayList<>();
            for (PestDisease pestDisease : pagedPestDiseases) {
                Map<String, Object> pestDiseaseMap = new HashMap<>();
                pestDiseaseMap.put("id", pestDisease.getId());
                pestDiseaseMap.put("name", pestDisease.getName());
                pestDiseaseMap.put("type", pestDisease.getType());
                pestDiseaseMap.put("description", pestDisease.getDescription());
                pestDiseaseMap.put("symptoms", pestDisease.getSymptoms());
                pestDiseaseMap.put("imageUrl", pestDisease.getImageUrl());
                pestDiseaseMap.put("viewCount", pestDisease.getViewCount());
                pestDiseaseMap.put("createdAt", pestDisease.getCreatedAt());
                pestDiseaseMap.put("updatedAt", pestDisease.getUpdatedAt());
                pestDiseaseMap.put("createdBy", pestDisease.getCreatedBy());

                // 获取该病虫害的评论数量（只统计已审核通过的评论）
                List<Comment> pestDiseaseComments = commentMapper.findByTargetTypeAndTargetId("pest_disease", pestDisease.getId());
                long commentCount = pestDiseaseComments.stream()
                    .filter(comment -> "approved".equals(comment.getStatus()))
                    .count();
                pestDiseaseMap.put("commentCount", commentCount);

                pestDiseasesWithCommentCount.add(pestDiseaseMap);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("list", pestDiseasesWithCommentCount);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);

            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取病虫害列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取病虫害详情
     */
    @GetMapping("/pest-diseases/{id}")
    public Result<Map<String, Object>> getPestDiseaseDetail(@PathVariable Long id) {
        try {
            log.info("获取病虫害详情，病虫害ID: {}", id);

            PestDisease pestDisease = pestDiseaseService.getPestDiseaseById(id.intValue());
            if (pestDisease == null) {
                return Result.error("病虫害不存在");
            }

            // 增加查看次数
            pestDiseaseService.incrementViewCount(id.intValue());

            // 获取相关农药信息
            List<Map<String, Object>> relatedPesticides = getRelatedPesticides(id.intValue());
            log.info("病虫害ID: {} 的相关农药数量: {}", id, relatedPesticides.size());

            Map<String, Object> data = new HashMap<>();
            data.put("pestDisease", pestDisease);
            data.put("relatedPesticides", relatedPesticides);

            return Result.success(data);
        } catch (Exception e) {
            log.error("获取病虫害详情异常: {}", e.getMessage(), e);
            return Result.error("获取病虫害详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取农药列表
     */
    @GetMapping("/pesticides")
    public Result<Map<String, Object>> getPesticides(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int pageSize,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "default") String sortBy,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String orderBy) {

        try {
            List<Pesticide> pesticides = pesticideService.getAllPesticides();

            // 搜索功能 - 只按名称搜索
            String searchKeyword = search != null ? search : (keyword != null ? keyword : name);
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                searchKeyword = searchKeyword.trim();
                final String finalKeyword = searchKeyword;
                pesticides = pesticides.stream()
                    .filter(p ->
                        p.getName() != null && p.getName().contains(finalKeyword)
                    )
                    .collect(java.util.stream.Collectors.toList());
            }

            // 分类筛选
            if (category != null && !category.trim().isEmpty()) {
                category = category.trim();
                final String finalCategory = category;
                pesticides = pesticides.stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().equals(finalCategory))
                    .collect(java.util.stream.Collectors.toList());
            }

            // 排序功能
            String sortField = sortBy != null && !sortBy.equals("default") ? sortBy :
                              (sort != null ? sort : orderBy);
            if (sortField != null && !sortField.equals("default")) {
                switch (sortField) {
                    case "name":
                        pesticides.sort((a, b) -> {
                            String nameA = a.getName() != null ? a.getName() : "";
                            String nameB = b.getName() != null ? b.getName() : "";
                            return nameA.compareTo(nameB);
                        });
                        break;
                    case "created_at":
                    case "createdAt":
                        pesticides.sort((a, b) -> {
                            if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                            if (a.getCreatedAt() == null) return 1;
                            if (b.getCreatedAt() == null) return -1;
                            return b.getCreatedAt().compareTo(a.getCreatedAt()); // 降序
                        });
                        break;
                    case "view_count":
                    case "viewCount":
                        pesticides.sort((a, b) -> {
                            Integer viewA = a.getViewCount() != null ? a.getViewCount() : 0;
                            Integer viewB = b.getViewCount() != null ? b.getViewCount() : 0;
                            return viewB.compareTo(viewA); // 降序
                        });
                        break;
                }
            }

            // 分页处理
            int total = pesticides.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);

            List<Pesticide> pagedPesticides = start < total ? pesticides.subList(start, end) : java.util.Collections.emptyList();

            // 为每个农药添加评论数量
            List<Map<String, Object>> pesticidesWithCommentCount = new ArrayList<>();
            for (Pesticide pesticide : pagedPesticides) {
                Map<String, Object> pesticideMap = new HashMap<>();
                pesticideMap.put("id", pesticide.getId());
                pesticideMap.put("name", pesticide.getName());
                pesticideMap.put("category", pesticide.getCategory());
                pesticideMap.put("activeIngredient", pesticide.getActiveIngredient());
                pesticideMap.put("usageInstructions", pesticide.getUsageInstructions());
                pesticideMap.put("imageUrl", pesticide.getImageUrl());
                pesticideMap.put("viewCount", pesticide.getViewCount());
                pesticideMap.put("createdAt", pesticide.getCreatedAt());
                pesticideMap.put("updatedAt", pesticide.getUpdatedAt());
                pesticideMap.put("createdBy", pesticide.getCreatedBy());

                // 获取该农药的评论数量（只统计已审核通过的评论）
                List<Comment> pesticideComments = commentMapper.findByTargetTypeAndTargetId("pesticide", pesticide.getId());
                long commentCount = pesticideComments.stream()
                    .filter(comment -> "approved".equals(comment.getStatus()))
                    .count();
                pesticideMap.put("commentCount", commentCount);

                pesticidesWithCommentCount.add(pesticideMap);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("list", pesticidesWithCommentCount);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);

            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取农药列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取农药详情
     */
    @GetMapping("/pesticides/{id}")
    public Result<Map<String, Object>> getPesticideDetail(@PathVariable Long id) {
        try {
            log.info("获取农药详情，农药ID: {}", id);

            Pesticide pesticide = pesticideService.getById(id.intValue());
            if (pesticide == null) {
                return Result.error("农药不存在");
            }

            // 增加查看次数
            pesticideService.incrementViewCount(id.intValue());

            Map<String, Object> data = new HashMap<>();
            data.put("pesticide", pesticide);

            return Result.success(data);
        } catch (Exception e) {
            log.error("获取农药详情异常: {}", e.getMessage(), e);
            return Result.error("获取农药详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取求助列表（只返回当前用户的求助）
     */
    @GetMapping("/help-requests")
    public Result<Map<String, Object>> getHelpRequests(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String auditStatus,
            @RequestParam(defaultValue = "created_at") String sortBy,
            @RequestHeader(value = "User-Id", required = false) Integer userId) {

        try {
            log.info("获取求助列表参数 - page: {}, pageSize: {}, search: {}, status: {}, auditStatus: {}, sortBy: {}, userId: {}",
                    page, pageSize, search, status, auditStatus, sortBy, userId);

            // 获取当前用户ID
            Integer currentUserId = getCurrentUserId(userId);
            if (currentUserId == null) {
                return Result.error("无法获取用户身份信息");
            }

            // 获取所有求助
            List<HelpRequest> helpRequests = helpRequestMapper.findAll();
            log.info("从数据库获取到 {} 条求助记录", helpRequests.size());

            // 只返回当前用户的求助
            helpRequests = helpRequests.stream()
                .filter(help -> help.getUserId() != null && help.getUserId().equals(currentUserId))
                .collect(java.util.stream.Collectors.toList());
            log.info("过滤后当前用户的求助: {} 条", helpRequests.size());

            // 搜索筛选
            if (search != null && !search.trim().isEmpty()) {
                final String searchKeyword = search.trim();
                helpRequests = helpRequests.stream()
                    .filter(help ->
                        (help.getTitle() != null && help.getTitle().contains(searchKeyword)) ||
                        (help.getDescription() != null && help.getDescription().contains(searchKeyword))
                    )
                    .collect(java.util.stream.Collectors.toList());
            }

            // 审核状态筛选（使用数据库中的status字段）
            if (auditStatus != null && !auditStatus.trim().isEmpty()) {
                final String finalAuditStatus = auditStatus.trim();
                helpRequests = helpRequests.stream()
                    .filter(help -> help.getStatus() != null && help.getStatus().equals(finalAuditStatus))
                    .collect(java.util.stream.Collectors.toList());
            }

            // 回复状态筛选（根据是否有回复来判断）
            if (status != null && !status.trim().isEmpty()) {
                final String finalStatus = status.trim();
                System.out.println("筛选回复状态: " + finalStatus);

                // 为了性能，先获取所有求助的ID，然后批量查询回复数量
                Map<Integer, Integer> replyCountMap = new HashMap<>();
                for (HelpRequest help : helpRequests) {
                    List<HelpReply> replies = helpRequestMapper.findRepliesByHelpId(help.getId());
                    replyCountMap.put(help.getId(), replies != null ? replies.size() : 0);
                }

                helpRequests = helpRequests.stream()
                    .filter(help -> {
                        int replyCount = replyCountMap.getOrDefault(help.getId(), 0);

                        switch (finalStatus) {
                            case "pending":
                                return replyCount == 0; // 待回复：没有回复
                            case "replied":
                                return replyCount > 0; // 已回复：有回复
                            case "resolved":
                                // 已解决：这里可以根据业务逻辑判断，暂时返回false
                                // 可以后续添加一个resolved字段到数据库来标记已解决状态
                                return false;
                            default:
                                return true;
                        }
                    })
                    .collect(java.util.stream.Collectors.toList());

                System.out.println("筛选后求助数量: " + helpRequests.size());
            }

            // 排序
            if ("created_at".equals(sortBy)) {
                helpRequests.sort((a, b) -> {
                    if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt()); // 降序
                });
            }

            // 分页处理
            int total = helpRequests.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);

            List<HelpRequest> pagedHelpRequests = start < total ? helpRequests.subList(start, end) : java.util.Collections.emptyList();

            // 为每个求助添加回复数量信息
            List<Map<String, Object>> helpRequestsWithReplyCount = new ArrayList<>();
            for (HelpRequest help : pagedHelpRequests) {
                Map<String, Object> helpMap = new HashMap<>();
                helpMap.put("id", help.getId());
                helpMap.put("userId", help.getUserId());
                helpMap.put("title", help.getTitle());
                helpMap.put("description", help.getDescription());
                helpMap.put("imageUrl", help.getImageUrl());
                helpMap.put("status", help.getStatus());
                helpMap.put("createdAt", help.getCreatedAt());

                // 获取回复数量
                List<HelpReply> replies = helpRequestMapper.findRepliesByHelpId(help.getId());
                helpMap.put("replyCount", replies != null ? replies.size() : 0);

                helpRequestsWithReplyCount.add(helpMap);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("list", helpRequestsWithReplyCount);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);

            return Result.success(data);
        } catch (Exception e) {
            System.out.println("获取求助列表异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("获取求助列表失败: " + e.getMessage());
        }
    }

    /**
     * 创建求助
     */
    @PostMapping("/help-requests")
    public Result<String> createHelpRequest(
            @RequestBody Map<String, Object> helpRequestData,
            @RequestHeader(value = "User-Id", required = false) Integer userId) {
        try {
            log.info("收到求助请求数据: {}, 用户ID: {}", helpRequestData, userId);

            // 获取当前用户ID
            Integer currentUserId = getCurrentUserId(userId);
            if (currentUserId == null) {
                return Result.error("无法获取用户身份信息");
            }

            // 创建求助对象
            HelpRequest helpRequest = new HelpRequest();
            helpRequest.setUserId(currentUserId);
            helpRequest.setTitle((String) helpRequestData.get("title"));
            helpRequest.setDescription((String) helpRequestData.get("description"));
            helpRequest.setImageUrl((String) helpRequestData.get("imageUrl"));
            helpRequest.setStatus("pending"); // 默认状态为待审核
            helpRequest.setCreatedAt(java.time.LocalDateTime.now());

            System.out.println("准备保存求助对象: " + helpRequest);

            // 保存到数据库
            int result = helpRequestMapper.insert(helpRequest);
            System.out.println("数据库插入结果: " + result);

            if (result > 0) {
                System.out.println("求助保存成功，ID: " + helpRequest.getId());

                // 立即查询验证数据是否真的保存了
                List<HelpRequest> allHelps = helpRequestMapper.findAll();
                System.out.println("保存后查询所有求助数量: " + allHelps.size());
                if (!allHelps.isEmpty()) {
                    HelpRequest lastHelp = allHelps.get(allHelps.size() - 1);
                    System.out.println("最新求助: ID=" + lastHelp.getId() + ", 标题=" + lastHelp.getTitle());
                }

                return Result.success("求助发布成功，等待审核");
            } else {
                System.out.println("求助保存失败，插入结果为0");
                return Result.error("求助保存失败");
            }
        } catch (Exception e) {
            System.out.println("发布求助异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("发布求助失败: " + e.getMessage());
        }
    }

    /**
     * 获取个人信息
     */
    @GetMapping("/profile")
    public Result<Map<String, Object>> getProfile(
            @RequestHeader(value = "User-Id", required = false) Integer userId) {
        try {
            log.info("获取普通用户个人信息，用户ID: {}", userId);

            // 获取当前用户ID
            Integer currentUserId = getCurrentUserId(userId);
            if (currentUserId == null) {
                return Result.error("无法获取用户身份信息");
            }

            // 获取用户信息
            NormalUser user = normalUserMapper.findById(currentUserId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 构建用户信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("nickname", user.getNickname());
            userInfo.put("phone", user.getPhone());
            userInfo.put("email", user.getEmail());
            userInfo.put("avatarUrl", user.getAvatarUrl() != null ? user.getAvatarUrl() : "");
            userInfo.put("createdAt", user.getCreatedAt());

            // 获取用户统计信息
            Map<String, Object> userStats = getUserStats(currentUserId);

            Map<String, Object> data = new HashMap<>();
            data.put("userInfo", userInfo);
            data.put("userStats", userStats);

            return Result.success(data);
        } catch (Exception e) {
            log.error("获取个人信息异常: {}", e.getMessage(), e);
            return Result.error("获取个人信息失败: " + e.getMessage());
        }
    }

    /**
     * 更新个人信息
     */
    @PutMapping("/profile")
    public Result<String> updateProfile(
            @RequestBody Map<String, Object> profileData,
            @RequestHeader(value = "User-Id", required = false) Integer userId) {
        try {
            log.info("更新普通用户个人信息，用户ID: {}, 数据: {}", userId, profileData);

            // 获取当前用户ID
            Integer currentUserId = getCurrentUserId(userId);
            if (currentUserId == null) {
                return Result.error("无法获取用户身份信息");
            }

            // 获取用户信息
            NormalUser user = normalUserMapper.findById(currentUserId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 更新用户信息
            if (profileData.containsKey("nickname")) {
                user.setNickname((String) profileData.get("nickname"));
            }
            if (profileData.containsKey("phone")) {
                user.setPhone((String) profileData.get("phone"));
            }
            if (profileData.containsKey("email")) {
                user.setEmail((String) profileData.get("email"));
            }
            if (profileData.containsKey("avatarUrl")) {
                user.setAvatarUrl((String) profileData.get("avatarUrl"));
            }

            // 保存到数据库
            int result = normalUserMapper.update(user);

            if (result > 0) {
                log.info("用户信息更新成功，用户ID: {}", currentUserId);
                return Result.success("个人信息更新成功");
            } else {
                return Result.error("个人信息更新失败");
            }
        } catch (Exception e) {
            log.error("更新个人信息异常: {}", e.getMessage(), e);
            return Result.error("更新个人信息失败: " + e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/change-password")
    public Result<String> changePassword(
            @RequestBody Map<String, Object> passwordData,
            @RequestHeader(value = "User-Id", required = false) Integer userId) {
        try {
            log.info("修改密码请求，用户ID: {}, 请求数据: {}", userId, passwordData);

            // 获取当前用户ID
            Integer currentUserId = getCurrentUserId(userId);
            if (currentUserId == null) {
                return Result.error("无法获取用户身份信息");
            }

            // 获取密码参数
            String oldPassword = (String) passwordData.get("oldPassword");
            String newPassword = (String) passwordData.get("newPassword");

            log.info("解析的密码参数 - 原密码: {}, 新密码: {}",
                    oldPassword != null ? "***" : "null",
                    newPassword != null ? "***" : "null");

            if (oldPassword == null || oldPassword.trim().isEmpty()) {
                log.warn("原密码为空或null");
                return Result.error("请输入原密码");
            }
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return Result.error("请输入新密码");
            }
            if (newPassword.length() < 6) {
                return Result.error("新密码长度不能少于6位");
            }

            // 获取用户信息
            NormalUser user = normalUserMapper.findById(currentUserId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            log.info("用户信息 - ID: {}, 用户名: {}, 密码哈希: {}",
                    user.getId(), user.getUsername(),
                    user.getPassword() != null ? user.getPassword().substring(0, 10) + "..." : "null");

            // 验证原密码（使用BCrypt验证）
            boolean passwordMatches = passwordEncoder.matches(oldPassword, user.getPassword());
            log.info("密码验证结果: {}", passwordMatches);

            if (!passwordMatches) {
                return Result.error("原密码错误");
            }

            // 更新密码（使用BCrypt加密）
            String encodedPassword = passwordEncoder.encode(newPassword);
            int result = normalUserMapper.updatePassword(currentUserId, encodedPassword);

            log.info("密码更新结果: {}, 用户ID: {}", result, currentUserId);

            if (result > 0) {
                log.info("密码修改成功，用户ID: {}", currentUserId);

                // 验证密码是否真的更新了
                NormalUser updatedUser = normalUserMapper.findById(currentUserId);
                boolean newPasswordMatches = passwordEncoder.matches(newPassword, updatedUser.getPassword());
                log.info("密码更新验证 - 新密码匹配: {}", newPasswordMatches);

                return Result.success("密码修改成功");
            } else {
                log.error("密码修改失败，数据库更新返回: {}", result);
                return Result.error("密码修改失败");
            }
        } catch (Exception e) {
            log.error("修改密码异常: {}", e.getMessage(), e);
            return Result.error("修改密码失败: " + e.getMessage());
        }
    }

    /**
     * 重置密码为已知密码（临时接口，用于调试）
     */
    @PostMapping("/reset-password")
    public Result<String> resetPassword(
            @RequestHeader(value = "User-Id", required = false) Integer userId) {
        try {
            Integer currentUserId = getCurrentUserId(userId);

            NormalUser user = normalUserMapper.findById(currentUserId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 重置密码为 "123456"
            String newPassword = "123456";
            String encodedPassword = passwordEncoder.encode(newPassword);
            int result = normalUserMapper.updatePassword(currentUserId, encodedPassword);

            if (result > 0) {
                log.info("密码重置成功 - 用户: {}, 新密码: {}", user.getUsername(), newPassword);
                return Result.success("密码已重置为: " + newPassword);
            } else {
                return Result.error("密码重置失败");
            }
        } catch (Exception e) {
            return Result.error("重置失败: " + e.getMessage());
        }
    }

    /**
     * 测试密码验证（临时接口，用于调试）
     */
    @PostMapping("/test-password")
    public Result<String> testPassword(
            @RequestBody Map<String, Object> data,
            @RequestHeader(value = "User-Id", required = false) Integer userId) {
        try {
            Integer currentUserId = getCurrentUserId(userId);
            String testPassword = (String) data.get("password");

            NormalUser user = normalUserMapper.findById(currentUserId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            boolean matches = passwordEncoder.matches(testPassword, user.getPassword());
            log.info("密码测试 - 用户: {}, 测试密码: {}, 数据库密码: {}, 匹配结果: {}",
                    user.getUsername(), testPassword, user.getPassword(), matches);

            return Result.success("密码匹配: " + matches + ", 用户: " + user.getUsername());
        } catch (Exception e) {
            return Result.error("测试失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户密码信息（临时接口，用于调试）
     */
    @GetMapping("/current-password-info")
    public Result<Map<String, Object>> getCurrentPasswordInfo(
            @RequestHeader(value = "User-Id", required = false) Integer userId) {
        try {
            Integer currentUserId = getCurrentUserId(userId);

            NormalUser user = normalUserMapper.findById(currentUserId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            Map<String, Object> info = new HashMap<>();
            info.put("username", user.getUsername());
            info.put("passwordHash", user.getPassword());
            info.put("passwordLength", user.getPassword() != null ? user.getPassword().length() : 0);

            // 测试几个常见密码
            String[] testPasswords = {"123456", "password123", "admin123"};
            Map<String, Boolean> testResults = new HashMap<>();
            for (String testPassword : testPasswords) {
                boolean matches = passwordEncoder.matches(testPassword, user.getPassword());
                testResults.put(testPassword, matches);
            }
            info.put("testResults", testResults);

            return Result.success(info);
        } catch (Exception e) {
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 获取求助详情
     */
    @GetMapping("/help-requests/{id}")
    public Result<Map<String, Object>> getHelpRequestDetail(@PathVariable Integer id) {
        try {
            log.info("获取求助详情，ID: {}", id);

            HelpRequest helpRequest = helpRequestMapper.findById(id);
            if (helpRequest == null) {
                return Result.error("求助不存在");
            }

            // 构建返回数据
            Map<String, Object> helpMap = new HashMap<>();
            helpMap.put("id", helpRequest.getId());
            helpMap.put("userId", helpRequest.getUserId());
            helpMap.put("title", helpRequest.getTitle());
            helpMap.put("description", helpRequest.getDescription());
            helpMap.put("imageUrl", helpRequest.getImageUrl());
            helpMap.put("status", helpRequest.getStatus());
            helpMap.put("createdAt", helpRequest.getCreatedAt());
            helpMap.put("viewCount", 0); // 暂时设为0，后续可以添加查看次数功能

            // 获取回复数量
            List<HelpReply> replies = helpRequestMapper.findRepliesByHelpId(id);
            helpMap.put("replyCount", replies != null ? replies.size() : 0);

            // 处理图片URL为数组格式（兼容前端）
            if (helpRequest.getImageUrl() != null && !helpRequest.getImageUrl().isEmpty()) {
                helpMap.put("images", java.util.Arrays.asList(helpRequest.getImageUrl()));
            } else {
                helpMap.put("images", new ArrayList<>());
            }

            // 添加作者信息
            helpMap.put("authorId", helpRequest.getUserId());
            helpMap.put("userId", helpRequest.getUserId()); // 兼容前端

            return Result.success(helpMap);
        } catch (Exception e) {
            log.error("获取求助详情异常: {}", e.getMessage(), e);
            return Result.error("获取求助详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取求助回复列表
     */
    @GetMapping("/help-requests/{helpId}/replies")
    public Result<Map<String, Object>> getHelpRequestReplies(
            @PathVariable Integer helpId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            log.info("获取求助回复列表，求助ID: {}, page: {}, pageSize: {}", helpId, page, pageSize);

            // 检查求助是否存在
            HelpRequest helpRequest = helpRequestMapper.findById(helpId);
            if (helpRequest == null) {
                return Result.error("求助不存在");
            }

            // 获取回复列表
            List<HelpReply> replies = helpRequestMapper.findRepliesByHelpId(helpId);

            // 为每个回复添加专家信息
            List<Map<String, Object>> repliesWithExpertInfo = new ArrayList<>();
            for (HelpReply reply : replies) {
                Map<String, Object> replyMap = new HashMap<>();
                replyMap.put("id", reply.getId());
                replyMap.put("helpId", reply.getHelpId());
                replyMap.put("expertId", reply.getExpertId());
                replyMap.put("content", reply.getContent());
                replyMap.put("createdAt", reply.getCreatedAt());

                // 根据专家ID获取真实的专家信息
                Map<String, Object> expertInfo = getExpertInfoById(reply.getExpertId());
                replyMap.put("expertName", expertInfo.get("name"));
                replyMap.put("expertTitle", expertInfo.get("title"));
                replyMap.put("expertAvatar", expertInfo.get("avatarUrl"));
                replyMap.put("images", new ArrayList<>()); // 暂时没有回复图片

                repliesWithExpertInfo.add(replyMap);
            }

            // 分页处理（简单实现）
            int total = repliesWithExpertInfo.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);

            List<Map<String, Object>> pagedReplies = start < total ?
                repliesWithExpertInfo.subList(start, end) : new ArrayList<>();

            Map<String, Object> data = new HashMap<>();
            data.put("list", pagedReplies);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);

            return Result.success(data);
        } catch (Exception e) {
            log.error("获取求助回复列表异常: {}", e.getMessage(), e);
            return Result.error("获取回复列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取评论列表
     */
    @GetMapping("/comments")
    public Result<Map<String, Object>> getComments(
            @RequestParam String targetType,
            @RequestParam Integer targetId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            log.info("获取评论列表 - 目标类型: {}, 目标ID: {}, 页码: {}, 页大小: {}",
                    targetType, targetId, page, pageSize);

            // 获取所有评论
            List<Comment> allComments = commentMapper.findByTargetTypeAndTargetId(targetType, targetId);

            // 只返回已审核通过的评论
            List<Map<String, Object>> approvedComments = new ArrayList<>();
            for (Comment comment : allComments) {
                if ("approved".equals(comment.getStatus())) {
                    Map<String, Object> commentMap = buildCommentMap(comment);
                    approvedComments.add(commentMap);
                }
            }

            // 简单分页处理
            int total = approvedComments.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);

            List<Map<String, Object>> pageComments = new ArrayList<>();
            if (start < total) {
                pageComments = approvedComments.subList(start, end);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("list", pageComments);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);

            return Result.success(data);
        } catch (Exception e) {
            log.error("获取评论列表异常: {}", e.getMessage(), e);
            return Result.error("获取评论列表失败: " + e.getMessage());
        }
    }

    /**
     * 添加评论
     */
    @PostMapping("/comments")
    public Result<String> addComment(
            @RequestBody Map<String, Object> commentData,
            @RequestHeader(value = "User-Id", required = false) Integer userId) {
        try {
            log.info("添加评论 - 数据: {}, 用户ID: {}", commentData, userId);

            // 获取当前用户ID
            Integer currentUserId = getCurrentUserId(userId);
            if (currentUserId == null) {
                return Result.error("无法获取用户身份信息");
            }

            // 创建评论对象
            Comment comment = new Comment();
            comment.setUserId(currentUserId);
            comment.setUserType("normal"); // 普通用户
            comment.setTargetType((String) commentData.get("targetType"));
            comment.setTargetId(Integer.valueOf(commentData.get("targetId").toString()));
            comment.setContent((String) commentData.get("content"));
            comment.setStatus("pending"); // 待审核
            comment.setCreatedAt(java.time.LocalDateTime.now());

            // 保存评论
            int result = commentMapper.insert(comment);

            if (result > 0) {
                log.info("评论添加成功，评论ID: {}", comment.getId());
                return Result.success("评论提交成功，等待审核");
            } else {
                return Result.error("评论添加失败");
            }
        } catch (Exception e) {
            log.error("添加评论异常: {}", e.getMessage(), e);
            return Result.error("添加评论失败: " + e.getMessage());
        }
    }

    /**
     * 获取我的评论列表
     */
    @GetMapping("/my-comments")
    public Result<Map<String, Object>> getMyComments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestHeader(value = "User-Id", required = false) Integer userId) {
        try {
            log.info("获取我的评论列表 - page: {}, pageSize: {}, type: {}, status: {}, userId: {}",
                    page, pageSize, type, status, userId);

            // 获取当前用户ID
            Integer currentUserId = getCurrentUserId(userId);
            if (currentUserId == null) {
                return Result.error("无法获取用户身份信息");
            }

            // 获取所有评论
            List<Comment> allComments = new ArrayList<>();
            allComments.addAll(commentMapper.findByStatus("pending"));
            allComments.addAll(commentMapper.findByStatus("approved"));
            allComments.addAll(commentMapper.findByStatus("rejected"));

            // 只返回当前用户的评论
            List<Comment> myComments = allComments.stream()
                .filter(comment -> comment.getUserId() != null && comment.getUserId().equals(currentUserId))
                .filter(comment -> "normal".equals(comment.getUserType()))
                .collect(java.util.stream.Collectors.toList());

            log.info("用户ID: {} 的评论总数: {}", currentUserId, myComments.size());

            // 类型筛选
            if (type != null && !type.trim().isEmpty()) {
                final String finalType = type.trim();
                myComments = myComments.stream()
                    .filter(comment -> comment.getTargetType() != null && comment.getTargetType().equals(finalType))
                    .collect(java.util.stream.Collectors.toList());
            }

            // 状态筛选
            if (status != null && !status.trim().isEmpty()) {
                final String finalStatus = status.trim();
                myComments = myComments.stream()
                    .filter(comment -> comment.getStatus() != null && comment.getStatus().equals(finalStatus))
                    .collect(java.util.stream.Collectors.toList());
            }

            // 按创建时间倒序排列
            myComments.sort((a, b) -> {
                if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                if (a.getCreatedAt() == null) return 1;
                if (b.getCreatedAt() == null) return -1;
                return b.getCreatedAt().compareTo(a.getCreatedAt());
            });

            // 分页处理
            int total = myComments.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);

            List<Comment> pagedComments = start < total ? myComments.subList(start, end) : new ArrayList<>();

            // 构建返回数据
            List<Map<String, Object>> commentsWithTargetInfo = new ArrayList<>();
            for (Comment comment : pagedComments) {
                Map<String, Object> commentMap = buildMyCommentMap(comment);
                commentsWithTargetInfo.add(commentMap);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("list", commentsWithTargetInfo);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);

            return Result.success(data);
        } catch (Exception e) {
            log.error("获取我的评论列表异常: {}", e.getMessage(), e);
            return Result.error("获取我的评论列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取我的评论统计数据
     */
    @GetMapping("/my-comments-stats")
    public Result<Map<String, Object>> getMyCommentsStats(
            @RequestHeader(value = "User-Id", required = false) Integer userId) {
        try {
            log.info("获取我的评论统计数据 - userId: {}", userId);

            // 获取当前用户ID
            Integer currentUserId = getCurrentUserId(userId);
            if (currentUserId == null) {
                return Result.error("无法获取用户身份信息");
            }

            // 获取所有评论
            List<Comment> allComments = new ArrayList<>();
            allComments.addAll(commentMapper.findByStatus("pending"));
            allComments.addAll(commentMapper.findByStatus("approved"));
            allComments.addAll(commentMapper.findByStatus("rejected"));

            // 只统计当前用户的评论
            List<Comment> myComments = allComments.stream()
                .filter(comment -> comment.getUserId() != null && comment.getUserId().equals(currentUserId))
                .filter(comment -> "normal".equals(comment.getUserType()))
                .collect(java.util.stream.Collectors.toList());

            // 统计各状态的评论数量
            long totalCount = myComments.size();
            long pendingCount = myComments.stream()
                .filter(comment -> "pending".equals(comment.getStatus()))
                .count();
            long approvedCount = myComments.stream()
                .filter(comment -> "approved".equals(comment.getStatus()))
                .count();
            long rejectedCount = myComments.stream()
                .filter(comment -> "rejected".equals(comment.getStatus()))
                .count();

            // 构建统计数据
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", totalCount);
            stats.put("pending", pendingCount);
            stats.put("approved", approvedCount);
            stats.put("rejected", rejectedCount);

            log.info("我的评论统计数据 - 用户ID: {}, 总数: {}, 审核中: {}, 已通过: {}, 已拒绝: {}",
                    currentUserId, totalCount, pendingCount, approvedCount, rejectedCount);

            return Result.success(stats);

        } catch (Exception e) {
            log.error("获取我的评论统计数据异常: {}", e.getMessage(), e);
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取我的求助统计数据
     */
    @GetMapping("/my-help-stats")
    public Result<Map<String, Object>> getMyHelpStats(
            @RequestHeader(value = "User-Id", required = false) Integer userId) {
        try {
            log.info("获取我的求助统计数据 - userId: {}", userId);

            // 获取当前用户ID
            Integer currentUserId = getCurrentUserId(userId);
            if (currentUserId == null) {
                return Result.error("无法获取用户身份信息");
            }

            // 获取所有求助
            List<HelpRequest> allHelpRequests = helpRequestMapper.findAll();

            // 只统计当前用户的求助
            List<HelpRequest> myHelpRequests = allHelpRequests.stream()
                .filter(help -> help.getUserId() != null && help.getUserId().equals(currentUserId))
                .collect(java.util.stream.Collectors.toList());

            // 统计各状态的求助数量
            long totalCount = myHelpRequests.size();
            long pendingCount = myHelpRequests.stream()
                .filter(help -> "pending".equals(help.getStatus()))
                .count();
            long approvedCount = myHelpRequests.stream()
                .filter(help -> "approved".equals(help.getStatus()))
                .count();
            long rejectedCount = myHelpRequests.stream()
                .filter(help -> "rejected".equals(help.getStatus()))
                .count();

            // 构建统计数据
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", totalCount);
            stats.put("pending", pendingCount);
            stats.put("approved", approvedCount);
            stats.put("rejected", rejectedCount);

            log.info("我的求助统计数据 - 用户ID: {}, 总数: {}, 审核中: {}, 已通过: {}, 已拒绝: {}",
                    currentUserId, totalCount, pendingCount, approvedCount, rejectedCount);

            return Result.success(stats);

        } catch (Exception e) {
            log.error("获取我的求助统计数据异常: {}", e.getMessage(), e);
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/comments/{id}")
    public Result<String> deleteComment(
            @PathVariable Integer id,
            @RequestHeader(value = "User-Id", required = false) Integer userId) {
        try {
            log.info("删除评论，评论ID: {}, 用户ID: {}", id, userId);

            // 获取当前用户ID
            Integer currentUserId = getCurrentUserId(userId);
            if (currentUserId == null) {
                return Result.error("无法获取用户身份信息");
            }

            // 检查评论是否存在
            Comment comment = commentMapper.findById(id);
            if (comment == null) {
                return Result.error("评论不存在");
            }

            // 检查是否是评论的创建者
            if (!comment.getUserId().equals(currentUserId) || !"normal".equals(comment.getUserType())) {
                return Result.error("只能删除自己发表的评论");
            }

            // 删除评论
            int result = commentMapper.deleteById(id);

            if (result > 0) {
                log.info("评论删除成功，评论ID: {}", id);
                return Result.success("评论删除成功");
            } else {
                return Result.error("评论删除失败");
            }
        } catch (Exception e) {
            log.error("删除评论异常: {}", e.getMessage(), e);
            return Result.error("删除评论失败: " + e.getMessage());
        }
    }

    /**
     * 更新评论
     */
    @PutMapping("/comments/{id}")
    public Result<String> updateComment(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> commentData,
            @RequestHeader(value = "User-Id", required = false) Integer userId) {
        try {
            log.info("更新评论，评论ID: {}, 数据: {}, 用户ID: {}", id, commentData, userId);

            // 获取当前用户ID
            Integer currentUserId = getCurrentUserId(userId);
            if (currentUserId == null) {
                return Result.error("无法获取用户身份信息");
            }

            // 检查评论是否存在
            Comment comment = commentMapper.findById(id);
            if (comment == null) {
                return Result.error("评论不存在");
            }

            // 检查是否是评论的创建者
            if (!comment.getUserId().equals(currentUserId) || !"normal".equals(comment.getUserType())) {
                return Result.error("只能修改自己发表的评论");
            }

            // 只有待审核的评论才能修改
            if (!"pending".equals(comment.getStatus())) {
                return Result.error("只有待审核的评论才能修改");
            }

            // 更新评论内容
            String newContent = (String) commentData.get("content");
            if (newContent == null || newContent.trim().isEmpty()) {
                return Result.error("评论内容不能为空");
            }

            comment.setContent(newContent.trim());
            int result = commentMapper.update(comment);

            if (result > 0) {
                log.info("评论更新成功，评论ID: {}", id);
                return Result.success("评论更新成功");
            } else {
                return Result.error("评论更新失败");
            }
        } catch (Exception e) {
            log.error("更新评论异常: {}", e.getMessage(), e);
            return Result.error("更新评论失败: " + e.getMessage());
        }
    }

    /**
     * 删除求助
     */
    @DeleteMapping("/help-requests/{id}")
    public Result<String> deleteHelpRequest(
            @PathVariable Integer id,
            @RequestHeader(value = "User-Id", required = false) Integer userId) {
        try {
            log.info("删除求助，求助ID: {}, 用户ID: {}", id, userId);

            // 获取当前用户ID
            Integer currentUserId = getCurrentUserId(userId);
            if (currentUserId == null) {
                return Result.error("无法获取用户身份信息");
            }

            // 检查求助是否存在
            HelpRequest helpRequest = helpRequestMapper.findById(id);
            if (helpRequest == null) {
                return Result.error("求助不存在");
            }

            // 检查是否是求助的创建者
            if (!helpRequest.getUserId().equals(currentUserId)) {
                return Result.error("只能删除自己发布的求助");
            }

            // 删除求助
            int result = helpRequestMapper.deleteById(id);

            if (result > 0) {
                log.info("求助删除成功，求助ID: {}", id);
                return Result.success("求助删除成功");
            } else {
                return Result.error("求助删除失败");
            }
        } catch (Exception e) {
            log.error("删除求助异常: {}", e.getMessage(), e);
            return Result.error("删除求助失败: " + e.getMessage());
        }
    }

    /**
     * 根据专家ID获取专家信息
     */
    private Map<String, Object> getExpertInfoById(Integer expertId) {
        Map<String, Object> expertInfo = new HashMap<>();

        try {
            if (expertId != null) {
                ExpertUser expert = expertUserMapper.findById(expertId);
                if (expert != null) {
                    expertInfo.put("name", expert.getName() != null ? expert.getName() : expert.getUsername());
                    expertInfo.put("title", expert.getTitle() != null ? expert.getTitle() : "专家");
                    expertInfo.put("avatarUrl", expert.getAvatarUrl() != null ? expert.getAvatarUrl() : "");
                } else {
                    // 专家不存在，使用默认信息
                    expertInfo.put("name", "专家" + expertId);
                    expertInfo.put("title", "专家");
                    expertInfo.put("avatarUrl", "");
                }
            } else {
                // 专家ID为空，使用默认信息
                expertInfo.put("name", "未知专家");
                expertInfo.put("title", "专家");
                expertInfo.put("avatarUrl", "");
            }
        } catch (Exception e) {
            log.error("获取专家信息失败，专家ID: {}, 错误: {}", expertId, e.getMessage());
            // 出错时使用默认信息
            expertInfo.put("name", "专家" + (expertId != null ? expertId : ""));
            expertInfo.put("title", "专家");
            expertInfo.put("avatarUrl", "");
        }

        return expertInfo;
    }

    /**
     * 获取用户统计信息
     */
    private Map<String, Object> getUserStats(Integer userId) {
        Map<String, Object> stats = new HashMap<>();

        try {
            // 获取求助数量
            List<HelpRequest> helpRequests = helpRequestMapper.findAll();
            long helpCount = helpRequests.stream()
                .filter(help -> help.getUserId() != null && help.getUserId().equals(userId))
                .count();

            // 获取评论数量（暂时设为0，后续可以实现评论功能）
            int commentCount = 0;

            stats.put("helpCount", helpCount);
            stats.put("commentCount", commentCount);

            log.info("用户统计信息 - 用户ID: {}, 求助数: {}, 评论数: {}", userId, helpCount, commentCount);
        } catch (Exception e) {
            log.error("获取用户统计信息失败，用户ID: {}, 错误: {}", userId, e.getMessage());
            stats.put("helpCount", 0);
            stats.put("commentCount", 0);
        }

        return stats;
    }

    /**
     * 获取植物相关的病虫害信息
     */
    private List<Map<String, Object>> getRelatedDiseases(Integer plantId) {
        List<Map<String, Object>> relatedDiseases = new ArrayList<>();

        try {
            // 获取植物-病虫害关联关系
            List<com.example.medicinalplant.entity.PlantDiseaseLink> links =
                plantDiseaseLinkMapper.findByPlantId(plantId);

            log.info("植物ID: {} 的关联关系数量: {}", plantId, links.size());

            for (com.example.medicinalplant.entity.PlantDiseaseLink link : links) {
                // 获取病虫害详细信息
                com.example.medicinalplant.entity.PestDisease disease =
                    pestDiseaseService.getPestDiseaseById(link.getPdId());

                if (disease != null) {
                    Map<String, Object> diseaseInfo = new HashMap<>();
                    diseaseInfo.put("id", disease.getId());
                    diseaseInfo.put("name", disease.getName());
                    diseaseInfo.put("type", disease.getType());
                    diseaseInfo.put("description", disease.getDescription());
                    diseaseInfo.put("symptoms", disease.getSymptoms());
                    diseaseInfo.put("imageUrl", disease.getImageUrl()); // 添加图片字段
                    diseaseInfo.put("vulnerability", link.getVulnerability());
                    diseaseInfo.put("occurrenceSeason", link.getOccurrenceSeason());
                    diseaseInfo.put("affectedParts", link.getAffectedParts());
                    diseaseInfo.put("notes", link.getNotes());

                    relatedDiseases.add(diseaseInfo);
                }
            }

            log.info("成功获取植物ID: {} 的相关病虫害: {} 个", plantId, relatedDiseases.size());
        } catch (Exception e) {
            log.error("获取植物相关病虫害失败，植物ID: {}, 错误: {}", plantId, e.getMessage(), e);
        }

        return relatedDiseases;
    }

    /**
     * 获取病虫害相关的农药信息
     */
    private List<Map<String, Object>> getRelatedPesticides(Integer pestDiseaseId) {
        List<Map<String, Object>> relatedPesticides = new ArrayList<>();

        try {
            // 获取病虫害-农药关联关系
            List<com.example.medicinalplant.entity.DiseasePesticideLink> links =
                diseasePesticideLinkMapper.findByPestDiseaseId(pestDiseaseId);

            log.info("病虫害ID: {} 的关联关系数量: {}", pestDiseaseId, links.size());

            for (com.example.medicinalplant.entity.DiseasePesticideLink link : links) {
                // 获取农药详细信息
                com.example.medicinalplant.entity.Pesticide pesticide =
                    pesticideService.getById(link.getPesticideId());

                if (pesticide != null) {
                    Map<String, Object> pesticideInfo = new HashMap<>();
                    pesticideInfo.put("id", pesticide.getId());
                    pesticideInfo.put("name", pesticide.getName());
                    pesticideInfo.put("category", pesticide.getCategory());
                    pesticideInfo.put("description", pesticide.getUsageInstructions()); // 使用usageInstructions作为描述
                    pesticideInfo.put("activeIngredient", pesticide.getActiveIngredient());
                    pesticideInfo.put("imageUrl", pesticide.getImageUrl()); // 添加图片URL
                    pesticideInfo.put("effectiveness", link.getEffectiveness());
                    pesticideInfo.put("dosage", link.getUsageDosage()); // 使用usageDosage
                    pesticideInfo.put("applicationMethod", link.getApplicationMethod());
                    pesticideInfo.put("notes", link.getNotes());

                    relatedPesticides.add(pesticideInfo);
                }
            }

            log.info("成功获取病虫害ID: {} 的相关农药: {} 个", pestDiseaseId, relatedPesticides.size());
        } catch (Exception e) {
            log.error("获取病虫害相关农药失败，病虫害ID: {}, 错误: {}", pestDiseaseId, e.getMessage(), e);
        }

        return relatedPesticides;
    }

    /**
     * 构建评论信息Map
     */
    private Map<String, Object> buildCommentMap(Comment comment) {
        Map<String, Object> commentMap = new HashMap<>();
        commentMap.put("id", comment.getId());
        commentMap.put("content", comment.getContent());
        commentMap.put("createdAt", comment.getCreatedAt());
        commentMap.put("userId", comment.getUserId());
        commentMap.put("userType", comment.getUserType());

        // 获取用户信息
        try {
            if ("normal".equals(comment.getUserType())) {
                NormalUser user = normalUserMapper.findById(comment.getUserId());
                if (user != null) {
                    commentMap.put("username", user.getUsername());
                    commentMap.put("nickname", user.getNickname() != null ? user.getNickname() : user.getUsername());
                    commentMap.put("avatarUrl", user.getAvatarUrl());
                }
            } else if ("expert".equals(comment.getUserType())) {
                ExpertUser user = expertUserMapper.findById(comment.getUserId());
                if (user != null) {
                    commentMap.put("username", user.getUsername());
                    commentMap.put("nickname", user.getName() != null ? user.getName() : user.getUsername());
                    commentMap.put("avatarUrl", user.getAvatarUrl());
                    commentMap.put("title", user.getTitle()); // 专家职称
                }
            }
        } catch (Exception e) {
            log.error("获取评论用户信息失败: {}", e.getMessage());
            commentMap.put("username", "未知用户");
            commentMap.put("nickname", "未知用户");
        }

        return commentMap;
    }

    /**
     * 构建我的评论信息Map
     */
    private Map<String, Object> buildMyCommentMap(Comment comment) {
        Map<String, Object> commentMap = new HashMap<>();
        commentMap.put("id", comment.getId());
        commentMap.put("content", comment.getContent());
        commentMap.put("createdAt", comment.getCreatedAt());
        commentMap.put("status", comment.getStatus());
        commentMap.put("type", comment.getTargetType());
        commentMap.put("targetId", comment.getTargetId());

        // 获取目标信息（植物、病虫害、农药名称）
        try {
            String targetName = getTargetName(comment.getTargetType(), comment.getTargetId());
            commentMap.put("targetName", targetName);
        } catch (Exception e) {
            log.error("获取目标信息失败: {}", e.getMessage());
            commentMap.put("targetName", "未知");
        }

        return commentMap;
    }

    /**
     * 获取目标名称
     */
    private String getTargetName(String targetType, Integer targetId) {
        try {
            switch (targetType) {
                case "plant":
                    com.example.medicinalplant.entity.MedicinalPlant plant = plantService.getPlantById(targetId);
                    return plant != null ? plant.getName() : "未知植物";
                case "pest_disease":
                    com.example.medicinalplant.entity.PestDisease disease = pestDiseaseService.getPestDiseaseById(targetId);
                    return disease != null ? disease.getName() : "未知病虫害";
                case "pesticide":
                    com.example.medicinalplant.entity.Pesticide pesticide = pesticideService.getById(targetId);
                    return pesticide != null ? pesticide.getName() : "未知农药";
                default:
                    return "未知类型";
            }
        } catch (Exception e) {
            log.error("获取目标名称失败，类型: {}, ID: {}, 错误: {}", targetType, targetId, e.getMessage());
            return "获取失败";
        }
    }

    /**
     * 获取当前用户ID
     * 优先级：请求头 > JWT Token > Session > 默认值
     */
    private Integer getCurrentUserId(Integer headerUserId) {
        // 1. 优先使用请求头中的用户ID（用于测试和临时方案）
        if (headerUserId != null && headerUserId > 0) {
            log.info("从请求头获取用户ID: {}", headerUserId);
            return headerUserId;
        }

        // 2. 从JWT Token中获取（TODO: 实现JWT解析）
        // String token = request.getHeader("Authorization");
        // if (token != null) {
        //     return parseUserIdFromToken(token);
        // }

        // 3. 从Session中获取（TODO: 实现Session管理）
        // HttpSession session = request.getSession(false);
        // if (session != null) {
        //     return (Integer) session.getAttribute("userId");
        // }

        // 4. 默认值（临时方案，生产环境应该移除）
        log.warn("无法获取用户ID，使用默认值1");
        return 1;
    }

    /**
     * 中文药用部位转换为英文
     */
    private String convertChineseToEnglishPart(String chinesePart) {
        switch (chinesePart) {
            case "根": return "root";
            case "茎": return "stem";
            case "叶": return "leaf";
            case "花": return "flower";
            case "果实": return "fruit";
            case "种子": return "seed";
            case "全草": return "whole";
            case "皮": return "bark";
            default: return chinesePart; // 如果已经是英文或其他，直接返回
        }
    }
}
