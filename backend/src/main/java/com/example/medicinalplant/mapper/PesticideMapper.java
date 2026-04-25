package com.example.medicinalplant.mapper;

import com.example.medicinalplant.entity.Pesticide;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 农药Mapper接口
 */
@Mapper
public interface PesticideMapper {

    /**
     * 分页查询农药
     */
    List<Pesticide> selectPage(@Param("search") String search, @Param("category") String category, @Param("offset") int offset, @Param("pageSize") int pageSize);


    /**
     * 统计查询结果数量
     */
    int selectCount(@Param("search") String search, @Param("category") String category);

    /**
     * 插入农药
     */
    int insert(Pesticide pesticide);

    /**
     * 更新农药
     */
    int update(Pesticide pesticide);

    /**
     * 删除农药
     */
    int delete(@Param("id") Integer id);

    /**
     * 批量删除农药
     */
    int deleteByIds(@Param("ids") List<Integer> ids);

    /**
     * 根据ID查询农药
     */
    Pesticide selectById(@Param("id") Integer id);

    /**
     * 查询所有农药
     */
    List<Pesticide> findAll();

    /**
     * 根据类别查询农药
     */
    List<Pesticide> findByCategory(@Param("category") String category);

    /**
     * 根据名称查询农药
     */
    List<Pesticide> findByName(@Param("name") String name);

    /**
     * 统计农药总数
     */
    int count();

    /**
     * 查询类别分布
     */
    List<CategoryCount> findCategoryDistribution();

    /**
     * 查询热门农药
     */
    List<Pesticide> findTopPesticides();

    /**
     * 查询最近添加的农药
     */
    List<Pesticide> findRecentPesticides();

    /**
     * 查询所有农药（不分页）
     */
    List<Pesticide> selectAll();

    /**
     * 根据类别查询农药
     */
    List<Pesticide> selectByCategory(@Param("category") String category);

    /**
     * 根据关键词查询农药
     */
    List<Pesticide> selectByKeyword(@Param("keyword") String keyword);

    /**
     * 根据条件查询农药
     */
    List<Pesticide> findByCondition(@Param("name") String name, @Param("category") String category);

    /**
     * 统计指定专家创建的农药数量
     */
    int countByCreatedBy(@Param("createdBy") Integer createdBy);

    /**
     * 增加查看次数
     */
    int incrementViewCount(@Param("id") Integer id);

    /**
     * 类别统计
     */
    class CategoryCount {
        private String category;
        private Integer count;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }
}