package com.example.medicinalplant.mapper;

import com.example.medicinalplant.entity.PlantDiseaseLink;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 药用植物和病虫害关联Mapper接口
 */
@Mapper
public interface PlantDiseaseLinkMapper {

    /**
     * 分页查询关联关系
     */
    List<PlantDiseaseLink> selectPage(@Param("plantName") String plantName,
                                     @Param("diseaseName") String diseaseName,
                                     @Param("vulnerability") String vulnerability,
                                     @Param("offset") int offset,
                                     @Param("pageSize") int pageSize);

    /**
     * 统计查询结果数量
     */
    int selectCount(@Param("plantName") String plantName,
                   @Param("diseaseName") String diseaseName,
                   @Param("vulnerability") String vulnerability);

    /**
     * 插入关联关系
     */
    int insert(PlantDiseaseLink link);

    /**
     * 更新关联关系
     */
    int update(PlantDiseaseLink link);

    /**
     * 删除关联关系
     */
    int delete(@Param("id") Integer id);

    /**
     * 批量删除关联关系
     */
    int deleteByIds(@Param("ids") List<Integer> ids);

    /**
     * 根据ID查询关联关系
     */
    PlantDiseaseLink selectById(@Param("id") Integer id);

    /**
     * 查询所有关联关系
     */
    List<PlantDiseaseLink> findAll();

    /**
     * 根据植物ID查询关联关系
     */
    List<PlantDiseaseLink> findByPlantId(@Param("plantId") Integer plantId);

    /**
     * 根据病虫害ID查询关联关系
     */
    List<PlantDiseaseLink> findByPestDiseaseId(@Param("pdId") Integer pdId);

    /**
     * 检查关联关系是否已存在
     */
    int checkExists(@Param("plantId") Integer plantId, @Param("pdId") Integer pdId);

    /**
     * 根据易感性查询关联关系
     */
    List<PlantDiseaseLink> findByVulnerability(@Param("vulnerability") String vulnerability);

    /**
     * 根据发生季节查询关联关系
     */
    List<PlantDiseaseLink> findByOccurrenceSeason(@Param("occurrenceSeason") String occurrenceSeason);

    /**
     * 统计总数
     */
    int count();

    /**
     * 查询易感性分布统计
     */
    List<VulnerabilityCount> findVulnerabilityDistribution();

    /**
     * 统计指定专家创建的植物-病虫害关系数量
     */
    int countByCreatedBy(@Param("createdBy") Integer createdBy);

    /**
     * 易感性统计内部类
     */
    class VulnerabilityCount {
        private String vulnerability;
        private Integer count;

        public String getVulnerability() {
            return vulnerability;
        }

        public void setVulnerability(String vulnerability) {
            this.vulnerability = vulnerability;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }
}
