<template>
  <div class="app-container">
    <div ref="OperateBox" class="operateBox">
      <el-form ref="queryForm" :model="queryParams" :inline="true" @submit.native.prevent>
        <el-form-item label="参数组名称" prop="dictName">
          <el-input v-model.trim="queryParams.dictName" placeholder="请输入参数组名称" clearable size="small" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <!-- <el-form-item label="参数组类型" prop="dictType">
          <el-input v-model="queryParams.dictType" placeholder="请输入参数组类型" clearable size="small" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="状态" clearable size="small" @change="handleQuery">
            <el-option v-for="dict in statusOptions" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue" />
          </el-select>
        </el-form-item> -->
        <!-- <el-form-item label="创建时间">
          <el-date-picker v-model="dateRange" size="small" style="width: 240px" value-format="yyyy-MM-dd" type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"></el-date-picker>
        </el-form-item> -->
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
        <el-form-item>
          <el-button v-hasPermi="['system:group:add']" icon="el-icon-plus" size="mini" class="addButton" @click="handleAdd">新增</el-button>
          <el-button v-hasPermi="['system:group:remove']" icon="el-icon-delete" size="mini" :disabled="multiple" class="redButton" @click="handleDelete">批量删除</el-button>
        </el-form-item>
      </el-form>

      <!-- <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['system:group:add']">新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="success" icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['system:group:edit']">修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:group:remove']">删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['system:group:export']">导出</el-button>
        </el-col>
      </el-row> -->
    </div>
    <div class="showTableBox" :style="{height:OperateBoxHeight+'px'}">
      <el-table empty-text=" " element-loading-text="拼命加载中..."  v-loading="loading" :data="typeList" @selection-change="handleSelectionChange">
        <template v-if="!loading" slot="empty" style="height:100%">
          <Deficiency width="30%" height="auto" />
        </template>
        <el-table-column type="selection" width="55" align="center" />
        <!-- <el-table-column label="字典编号" align="center" prop="dictId" /> -->
        <el-table-column label="参数组名称" align="left" header-align="left" prop="dictName" width="200" :show-overflow-tooltip="true" />
        <el-table-column label="参数组类型" align="left" header-align="left" :show-overflow-tooltip="true" width="200">
          <template slot-scope="scope">
            <!-- <router-link :to="'/dict/type/data/' + scope.row.dictId" class="link-type"> -->
            <span>{{ scope.row.dictType }}</span>
            <!-- </router-link> -->
          </template>
        </el-table-column>
        <!-- <el-table-column label="启用" align="center" prop="status" :formatter="statusFormat" width="200" /> -->
        <el-table-column align="center" label="启用" prop="status" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === '1'" size="small" type="danger">不启用</el-tag>
            <el-tag v-else size="small">启用</el-tag>
          </template>
        </el-table-column>

        <!-- <el-table-column label="创建时间" align="center" prop="createTime" width="200">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column> -->
        <el-table-column label="备注" align="left" header-align="left" prop="remark" :show-overflow-tooltip="true" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
          <template slot-scope="scope">
            <el-button v-hasPermi="['system:group:edit']" size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button v-hasPermi="['system:group:remove']" size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" :page-sizes="[10, 20, 50, 200]" @pagination="getList" />
    </div>

    <!-- 添加或修改参数配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="参数组名称" prop="dictName">
          <el-input v-model.trim="form.dictName" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="参数组类型" prop="dictType">
          <el-input v-model.trim="form.dictType" placeholder="请输入字典类型" />
        </el-form-item>
        <el-form-item label="启用" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in statusOptions" :key="dict.dictValue" :label="dict.dictValue">{{ dict.dictLabel }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model.trim="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button class="cancel_button" @click="cancel">取 消</el-button>
        <el-button type="primary" class="confirm_button" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listType,
  getType,
  delType,
  addType,
  updateType,
  exportType
} from '@/api/system/dict/type'

import Deficiency from '@/components/Deficiency'

export default {
  components: { Deficiency },
  data() {
    return {
      // 页面高度
      OperateBoxHeight: 100,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      dictNames: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 总条数
      total: 0,
      // 字典表格数据
      typeList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 状态数据字典
      statusOptions: [],
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        dictName: undefined,
        dictType: undefined,
        status: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        dictName: [
          { required: true, message: '字典名称不能为空', trigger: 'blur' }
        ],
        dictType: [
          { required: true, message: '字典类型不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
    this.getDicts('sys_normal_disable').then((response) => {
      this.statusOptions = response.data
    })
  },
  mounted() {
    this.getOperateBoxHeight()
  },
  updated() {
    this.getOperateBoxHeight()
  },
  methods: {
    // 获取页面高度
    getOperateBoxHeight() {
      this.$nextTick(function() {
        this.OperateBoxHeight =
          document.getElementsByClassName('app-container')[0].offsetHeight -
          document.getElementsByClassName('operateBox')[0].offsetHeight -
          10
      })
    },
    /** 查询字典类型列表 */
    getList() {
      this.loading = true
      listType(this.addDateRange(this.queryParams, this.dateRange)).then(
        (response) => {
          this.typeList = response.rows
          this.total = response.total
          this.loading = false
        }
      )
    },
    // 字典状态字典翻译
    statusFormat(row, column) {
      return this.selectDictLabel(this.statusOptions, row.status)
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        dictId: undefined,
        dictName: undefined,
        dictType: undefined,
        status: '0',
        remark: undefined
      }
      this.resetForm('form')
    },
    /** 查询按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = []
      this.resetForm('queryForm')
      this.handleQuery()
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增'
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.dictId)
      this.dictNames = selection.map((item) => item.dictName)
      this.single = selection.length != 1
      this.multiple = !selection.length
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const dictId = row.dictId || this.ids
      getType(dictId).then((response) => {
        this.form = response.data
        this.open = true
        this.title = '修改'
      })
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          if (this.form.dictId != undefined) {
            updateType(this.form).then((response) => {
              if (response.code === 200) {
                this.msgSuccess('修改成功')
                this.open = false
                this.getList()
              } else {
                this.msgError(response.msg)
              }
            })
          } else {
            addType(this.form).then((response) => {
              if (response.code === 200) {
                this.msgSuccess('新增成功')
                this.open = false
                this.getList()
              } else {
                this.msgError(response.msg)
              }
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const dictIds = row.dictId || this.ids
      const dictName = row.dictName || this.dictNames
      this.$confirm('是否确认删除该项数据?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(function() {
          return delType(dictIds)
        })
        .then(() => {
          this.getList()
          this.msgSuccess('删除成功')
        })
        .catch(function() {})
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams
      this.$confirm('是否确认导出所有类型数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(function() {
          return exportType(queryParams)
        })
        .then((response) => {
          this.download(response.msg)
        })
        .catch(function() {})
    }
  }
}
</script>

