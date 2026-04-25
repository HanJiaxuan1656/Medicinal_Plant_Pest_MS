package com.example.medicinalplant.service;

import com.example.medicinalplant.common.PageResult;
import com.example.medicinalplant.entity.PestDisease;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 病虫害服务接口
 */
public interface PestDiseaseService {
    
    /**
     * 获取所有病虫害
     * 
     * @return 病虫害列表
     */
    List<PestDisease> getAllPestDiseases();
    
    /**
     * 根据ID获取病虫害
     * 
     * @param id 病虫害ID
     * @return 病虫害对象
     */
    PestDisease getPestDiseaseById(Integer id);
    
    /**
     * 添加病虫害
     * 
     * @param pestDisease 病虫害对象
     */
    void addPestDisease(PestDisease pestDisease);
    
    /**
     * 更新病虫害
     * 
     * @param pestDisease 病虫害对象
     */
    void updatePestDisease(PestDisease pestDisease);
    
    /**
     * 删除病虫害
     *
     * @param id 病虫害ID
     */
    void deletePestDisease(Integer id);

    /**
     * 批量删除病虫害
     *
     * @param ids 病虫害ID列表
     */
    void deletePestDiseases(List<Integer> ids);

    /**
     * 上传病虫害图片
     *
     * @param file 图片文件
     * @return 图片URL
     */
    String uploadImage(MultipartFile file);
    
    /**
     * 根据类型获取病虫害
     * 
     * @param type 类型
     * @return 病虫害列表
     */
    List<PestDisease> getPestDiseasesByType(String type);
    
    /**
     * 搜索病虫害
     * 
     * @param keyword 关键词
     * @return 病虫害列表
     */
    List<PestDisease> searchPestDiseases(String keyword);

    /**
     * 条件查询病虫害（分页）
     */
    PageResult<PestDisease> findByConditions(String search, String type, int page, int pageSize);
    
    PestDisease findById(Integer id);
    
    PestDisease save(PestDisease pestDisease);
    
    PestDisease update(PestDisease pestDisease);

    void delete(Integer id);

    /**
     * 增加查看次数
     *
     * @param id 病虫害ID
     */
    void incrementViewCount(Integer id);
} 