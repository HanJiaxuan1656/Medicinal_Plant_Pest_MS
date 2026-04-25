package com.example.medicinalplant.controller;

import com.example.medicinalplant.common.Result;
import com.example.medicinalplant.mapper.HelpRequestMapper;
import com.example.medicinalplant.service.DashboardService;
import com.example.medicinalplant.vo.DashboardDataVO;
import com.example.medicinalplant.vo.PlantOverviewVO;
import com.example.medicinalplant.vo.PestDiseaseOverviewVO;
import com.example.medicinalplant.vo.PesticideOverviewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    
    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private HelpRequestMapper helpRequestMapper;
    
    /**
     * 获取仪表盘概览数据（管理员用）
     */
    @GetMapping
    public Result<DashboardDataVO> getDashboardData() {
        log.info("获取仪表盘概览数据");
        return Result.success(dashboardService.getDashboardData());
    }

    /**
     * 获取专家仪表盘数据
     */
    @GetMapping("/expert")
    public Result<DashboardDataVO> getExpertDashboardData() {
        log.info("获取专家仪表盘数据");

        DashboardDataVO dashboardData = dashboardService.getDashboardData();

        // 专家只能看到已审核通过的求助数量，不能看到待审核评论
        try {
            int approvedHelpRequestCount = helpRequestMapper.countByStatus("approved");
            dashboardData.setPendingHelpRequestCount(approvedHelpRequestCount);
            dashboardData.setPendingCommentCount(0); // 专家不需要看到待审核评论
            log.info("专家仪表盘数据 - 已审核通过的求助数量: {}", approvedHelpRequestCount);
        } catch (Exception e) {
            log.error("获取专家仪表盘数据失败: {}", e.getMessage(), e);
            dashboardData.setPendingHelpRequestCount(0);
            dashboardData.setPendingCommentCount(0);
        }

        return Result.success(dashboardData);
    }
    
    /**
     * 获取药用植物概览数据
     */
    @GetMapping("/plants/overview")
    public Result<PlantOverviewVO> getPlantsOverview() {
        log.info("获取药用植物概览数据");
        return Result.success(dashboardService.getPlantsOverview());
    }
    
    /**
     * 获取病虫害概览数据
     */
    @GetMapping("/pests/overview")
    public Result<PestDiseaseOverviewVO> getPestDiseasesOverview() {
        log.info("获取病虫害概览数据");
        return Result.success(dashboardService.getPestDiseasesOverview());
    }
    
    /**
     * 获取农药概览数据
     */
    @GetMapping("/pesticides/overview")
    public Result<PesticideOverviewVO> getPesticidesOverview() {
        log.info("获取农药概览数据");
        return Result.success(dashboardService.getPesticidesOverview());
    }
} 