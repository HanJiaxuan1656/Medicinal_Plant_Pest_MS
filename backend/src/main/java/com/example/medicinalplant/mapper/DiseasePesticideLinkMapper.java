package com.example.medicinalplant.mapper;

import com.example.medicinalplant.entity.DiseasePesticideLink;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 病虫害和农药关联Mapper接口
 */
@Mapper
public interface DiseasePesticideLinkMapper {

    /**
     * 分页查询关联关系
     */
    List<DiseasePesticideLink> selectPage(@Param("diseaseName") String diseaseName,
                                         @Param("pesticideName") String pesticideName,
                                         @Param("effectiveness") String effectiveness,
                                         @Param("offset") int offset,
                                         @Param("pageSize") int pageSize);

    /**
     * 统计查询结果数量
     */
    int selectCount(@Param("diseaseName") String diseaseName,
                   @Param("pesticideName") String pesticideName,
                   @Param("effectiveness") String effectiveness);

    /**
     * 插入关联关系
     */
    int insert(DiseasePesticideLink link);

    /**
     * 更新关联关系
     */
    int update(DiseasePesticideLink link);

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
    DiseasePesticideLink selectById(@Param("id") Integer id);

    /**
     * 查询所有关联关系
     */
    List<DiseasePesticideLink> findAll();

    /**
     * 根据病虫害ID查询关联关系
     */
    List<DiseasePesticideLink> findByPestDiseaseId(@Param("pdId") Integer pdId);

    /**
     * 根据农药ID查询关联关系
     */
    List<DiseasePesticideLink> findByPesticideId(@Param("pesticideId") Integer pesticideId);

    /**
     * 检查关联关系是否已存在
     */
    int checkExists(@Param("pdId") Integer pdId, @Param("pesticideId") Integer pesticideId);

    /**
     * 根据效果查询关联关系
     */
    List<DiseasePesticideLink> findByEffectiveness(@Param("effectiveness") String effectiveness);

    /**
     * 根据施用方法查询关联关系
     */
    List<DiseasePesticideLink> findByApplicationMethod(@Param("applicationMethod") String applicationMethod);

    /**
     * 统计总数
     */
    int count();

    /**
     * 查询效果分布统计
     */
    List<EffectivenessCount> findEffectivenessDistribution();

    /**
     * 统计指定专家创建的病虫害-农药关系数量
     */
    int countByCreatedBy(@Param("createdBy") Integer createdBy);

    /**
     * 效果统计内部类
     */
    class EffectivenessCount {
        private String effectiveness;
        private Integer count;

        public String getEffectiveness() {
            return effectiveness;
        }

        public void setEffectiveness(String effectiveness) {
            this.effectiveness = effectiveness;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }
}
