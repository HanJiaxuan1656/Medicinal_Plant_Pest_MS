<template>
  <div class="drug-management">
    <h1>药物管理</h1>
    <!-- 搜索框 -->
    <el-input v-model="searchQuery" placeholder="搜索药物" @input="searchDrugs" />
    <!-- 表格展示药物列表 -->
    <el-table :data="drugList" style="width: 100%">
      <el-table-column prop="id" label="ID" />
      <el-table-column prop="name" label="药物名称" />
      <el-table-column prop="category" label="类别" />
      <el-table-column prop="description" label="描述" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button @click="editDrug(scope.row)">编辑</el-button>
          <el-button type="danger" @click="deleteDrug(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 添加/编辑药物对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑药物' : '添加药物'">
      <el-form :model="drugForm" label-width="120px">
        <el-form-item label="药物名称">
          <el-input v-model="drugForm.name" />
        </el-form-item>
        <el-form-item label="类别">
          <el-input v-model="drugForm.category" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input type="textarea" v-model="drugForm.description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitDrug">确定</el-button>
        </span>
      </template>
    </el-dialog>
    <!-- 添加药物按钮 -->
    <el-button type="primary" style="margin-top: 20px" @click="showAddDialog">添加药物</el-button>
  </div>
</template>

<script>
import { ref } from 'vue';

export default {
  name: 'DrugManagement',
  setup() {
    // 搜索关键词
    const searchQuery = ref('');
    // 药物列表
    const drugList = ref([
      { id: 1, name: '药物A', category: '西药', description: '用于治疗头痛' },
      { id: 2, name: '药物B', category: '中药', description: '用于调理身体' },
    ]);
    // 对话框是否可见
    const dialogVisible = ref(false);
    // 是否为编辑模式
    const isEdit = ref(false);
    // 药物表单数据
    const drugForm = ref({ id: null, name: '', category: '', description: '' });

    // 搜索药物
    const searchDrugs = () => {
      // 这里实现搜索逻辑
    };

    // 显示添加药物对话框
    const showAddDialog = () => {
      isEdit.value = false;
      drugForm.value = { id: null, name: '', category: '', description: '' };
      dialogVisible.value = true;
    };

    // 编辑药物
    const editDrug = (drug) => {
      isEdit.value = true;
      drugForm.value = { ...drug };
      dialogVisible.value = true;
    };

    // 删除药物
    const deleteDrug = (id) => {
      // 这里实现删除逻辑
      drugList.value = drugList.value.filter((drug) => drug.id !== id);
    };

    // 提交药物信息
    const submitDrug = () => {
      if (isEdit.value) {
        // 编辑药物
        const index = drugList.value.findIndex((drug) => drug.id === drugForm.value.id);
        if (index !== -1) {
          drugList.value[index] = { ...drugForm.value };
        }
      } else {
        // 添加药物
        const newId = drugList.value.length > 0 ? Math.max(...drugList.value.map((drug) => drug.id)) + 1 : 1;
        drugList.value.push({ ...drugForm.value, id: newId });
      }
      dialogVisible.value = false;
    };

    return {
      searchQuery,
      drugList,
      dialogVisible,
      isEdit,
      drugForm,
      searchDrugs,
      showAddDialog,
      editDrug,
      deleteDrug,
      submitDrug,
    };
  },
};
</script>

<style scoped>
.drug-management {
  padding: 20px;
}
</style>