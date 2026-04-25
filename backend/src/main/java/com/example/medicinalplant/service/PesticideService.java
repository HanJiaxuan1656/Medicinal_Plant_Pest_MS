package com.example.medicinalplant.service;

import com.example.medicinalplant.entity.Pesticide;
import com.example.medicinalplant.mapper.PesticideMapper;
import com.example.medicinalplant.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 农药服务接口
 */
public interface PesticideService {

    PageVO<Pesticide> getPage(String search, String category, int page, int pageSize);
    
    Pesticide getById(Integer id);
    
    boolean add(Pesticide pesticide);
    
    boolean update(Pesticide pesticide);
    
    boolean delete(Integer id);

    /**
     * 批量删除农药
     *
     * @param ids 农药ID列表
     * @return 删除是否成功
     */
    boolean deleteByIds(List<Integer> ids);

    List<Pesticide> getAllPesticides();
    
    List<Pesticide> getPesticidesByCategory(String category);
    
    List<Pesticide> searchPesticides(String keyword);

    /**
     * 上传农药图片
     * 
     * @param file 图片文件
     * @return 图片URL
     */
    String uploadImage(MultipartFile file);
    
    /**
     * 上传农药使用说明书
     *
     * @param file 说明书文件
     * @return 说明书URL
     */
    String uploadManual(MultipartFile file);

    /**
     * 增加查看次数
     *
     * @param id 农药ID
     */
    void incrementViewCount(Integer id);
} 