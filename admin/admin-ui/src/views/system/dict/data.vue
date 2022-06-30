<template>
  <div class="app-container">
    <div ref="OperateBox" class="operateBox">
      <el-form ref="queryForm" :model="queryParams" :inline="true" label-position="left">
        <el-form-item label="参数组名称" prop="dictType">
          <el-select v-model="queryParams.dictType" size="small" @change="handleQuery">
            <el-option v-for="item in typeOptions" :key="item.dictId" :label="item.dictName" :value="item.dictType" />
          </el-select>
        </el-form-item>
        <el-form-item label="参数名称" prop="dictLabel">
          <el-input v-model.trim="queryParams.dictLabel" placeholder="请输入参数名称" size="small" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <!-- <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="数据状态" clearable size="small">
            <el-option v-for="dict in statusOptions" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue" />
          </el-select>
        </el-form-item> -->

        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>

        <el-form-item>
          <el-button v-hasPermi="['system:dict:add']" class="addButton" icon="el-icon-plus" size="mini" @click="handleAdd">新增</el-button>
          <el-button v-hasPermi="['system:dict:remove']" class="redButton" icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete">批量删除</el-button>
        </el-form-item>
      </el-form>
      <!-- <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button class="addButton" icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['system:dict:add']">新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button class="greenButton" icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['system:dict:edit']">修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button class="redButton" icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:dict:remove']">删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['system:dict:export']">导出</el-button>
        </el-col>
      </el-row> -->
    </div>
    <div class="showTableBox" :style="{height:OperateBoxHeight+'px'}">
      <el-table v-if="dataList.length" v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column
          label="参数组名称"
          sortable
          align="left
        "
          prop="dictType"
          width="200"
        >
          <template slot-scope="scope">
            {{ scope.row.dictName }}
          </template>
        </el-table-column>
        <!-- <el-table-column label="字典编码" align="center" prop="dictCode" /> -->
        <el-table-column
          label="参数名称"
          sortable
          align="left
        "
          prop="dictLabel"
          :show-overflow-tooltip="true"
          min-width="150"
        />
        <el-table-column
          label="参数值"
          sortable
          align="left
        "
          prop="dictValue"
          :show-overflow-tooltip="true"
          min-width="150"
          max-width="250"
        />
        <!--      <el-table-column label="状态" sortable align="center" prop="status" :formatter="statusFormat" width="200" >-->
        <!-- <el-table-column label="状态" align="center" header-align="center" prop="status">
          <template slot-scope="scope">
            <el-switch v-model="scope.row.status" active-value="0" inactive-value="1" @change="handleStatusChange(scope.row)" />
          </template>
        </el-table-column> -->
        <el-table-column align="center" label="启用" prop="status" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === '1'" size="small" type="danger">不启用</el-tag>
            <el-tag v-else size="small">启用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="显示顺序" sortable align="left" header-align="left" prop="dictSort" />

        <el-table-column label="修改时间" sortable align="left" header-align="left" prop="updateTime" min-width="150">
          <template slot-scope="scope">
            <!-- {{ scope.row.updateTime }} -->
            <span v-if="scope.row.updateTime">{{ parseTime(scope.row.updateTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="备注"
          sortable
          align="left"
          header-align="left
        "
          prop="remark"
          :show-overflow-tooltip="true"
          min-width="100"
          max-width="250"
        />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button v-hasPermi="['system:dict:edit']" size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button v-hasPermi="['system:dict:remove']" size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-else class="noneDateBox">
        <img src="../../../assets/common/nonedata.png" width="25%">
        <div>&nbsp;&nbsp;暂无数据~</div>
      </div>
      <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" :page-sizes="[10, 20, 50, 200]" @pagination="getList" />
    </div>

    <!-- 添加或修改参数配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="参数组名称" prop="dictType">
          <el-select v-model="form.dictType" :disabled="isEnable" size="small">
            <el-option v-for="item in typeOptions" :key="item.dictType" :label="item.dictName" :value="item.dictType" />
          </el-select>
        </el-form-item>

        <el-form-item label="参数名称" prop="dictLabel">
          <!--          <el-input v-model="form.dictLabel" :disabled="isEnable" placeholder="请输入数据标签" />-->
          <el-input v-model.trim="form.dictLabel" :disabled="isEnable" size="small" />

        </el-form-item>
        <el-form-item label="参数值" prop="dictValue">
          <el-input v-model.trim="form.dictValue" placeholder="请输入数据键值" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="dictSort">
          <el-input-number v-model="form.dictSort" controls-position="right" :min="10" />
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
  listData,
  getData,
  delData,
  addData,
  updateData,
  changeDictLabelStatus,
  updateRouter,
  getCountDictLabel
} from '@/api/system/dict/data'
import { listType } from '@/api/system/dict/type'

export default {
  data() {
    return {
      // 页面高度
      OperateBoxHeight: 100,
      isEnable: false,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      dictLabels: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 总条数
      total: 0,
      // 字典表格数据
      dataList: [],
      // 默认字典类型
      defaultDictType: '',
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 状态数据字典
      statusOptions: [],
      // 类型数据字典
      typeOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        dictName: undefined,
        dictType: undefined,
        status: undefined,
        projectId: 10
      },
      baseDict: '',
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        dictLabel: [
          { required: true, message: '数据标签不能为空', trigger: 'blur' }
        ],
        dictValue: [
          { required: true, message: '数据键值不能为空', trigger: 'blur' }
        ],
        dictSort: [
          { required: true, message: '数据顺序不能为空', trigger: 'blur' }
        ],
        dictType: [
          { required: true, message: '参数组名称不能为空', trigger: 'blur' }
        ]
      }
      // 翻译字段
      // dictdata:{

      // }
    }
  },
  created() {
    this.queryParams.projectId = localStorage.getItem('project_id')
    // const dictId = this.$route.params && this.$route.params.dictId;
    // debugger
    // this.getType(dictId);
    this.getList()
    this.getDicts('sys_normal_disable').then((response) => {
      this.statusOptions = response.data
    })
    this.getTypeList()
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

    /** 查询字典类型详细 */
    // getType(dictId) {
    //   getType(dictId).then(response => {
    //     this.queryParams.dictType = response.data.dictType;
    //     this.defaultDictType = response.data.dictType;
    //     this.getList();
    //   });
    // },
    /** 查询字典类型列表 */
    getTypeList() {
      listType().then((response) => {
        this.typeOptions = response.rows

        // this.form.dictType = response.rows[0].dictType;
        this.$set(this.form, 'dictType', response.rows[0].dictType)

        // this.roleNameChange(this.form.dictType);
        // console.log(this.typeOptions)
        // this.typeOptions.forEach(item=>{
        //   this.dictdata[item.dictType]=item.dictName
        // })
      })
    },
    /** 查询字典数据列表 */
    getList() {
      this.loading = true
      listData(this.queryParams).then((response) => {
        // debugger
        this.dataList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 数据状态字典翻译
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
        dictCode: undefined,
        dictLabel: undefined,
        dictValue: undefined,
        dictSort: 1000,
        status: '0',
        remark: undefined
      }
      this.isEnable = false
      this.resetForm('form')
    },
    /** 查询按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.queryParams.dictType = this.defaultDictType
      this.handleQuery()
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增'
      this.isEnable = false
      this.getTypeList()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.dictCode)
      this.dictLabels = selection.map((item) => item.dictLabel)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      this.isEnable = true
      const dictCode = row.dictCode || this.ids

      getData(dictCode).then((response) => {
        this.form = response.data
        this.open = true
        this.title = '修改'
      })
    },
    handleStatusChange(row) {
      const text = row.status === '0' ? '启用' : '停用'
      this.$confirm(
        '确认要' + text + '"' + row.dictLabel + '"角色吗?',
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
        .then(function() {
          changeDictLabelStatus({
            dict_label: row.dictLabel,
            status: row.status
          })
        })
        .then(() => {
          this.msgSuccess(text + '成功')
        })
        .catch(function() {
          row.status = row.status === '0' ? '1' : '0'
        })
    },
    /** 提交按钮 */
    submitForm: function() {
      this.form.projectId = localStorage.getItem('project_id')
      this.$refs['form'].validate((valid) => {
        if (valid) {
          if (this.form.dictCode !== undefined) {
            this.form.projectId = this.queryParams.projectId
            updateData(this.form).then((response) => {
              // console.log(this.form)
              if (response.code === 200) {
                // console.log(this.form)

                this.open = false
                this.getList()
                // 如果是运行总览中的就走这个判断
                if (this.form.dictType === 'sys_url') {
                  // dictType: "sys_url"
                  this.form.projectId = this.queryParams.projectId
                  updateRouter(this.form).then(res => {
                    // console.log(res)
                    if (res.success) {
                      this.msgSuccess('修改成功,路由将在重新登录后生效')
                    }
                  })
                } else {
                  this.msgSuccess('修改成功')
                }
              } else {
                this.msgError(response.msg)
              }
            })
          } else {
            getCountDictLabel({ projectId: this.queryParams.projectId, dict_label: this.form.dictLabel, dict_type: this.form.dictType }).then((response) => {
              if (response.success) {
                if (response.totalCount === 0) {
                  addData(this.form).then((response) => {
                    if (response.code === 200) {
                      this.msgSuccess('新增成功')
                      this.open = false
                      this.getList()
                    } else {
                      this.msgError(response.msg)
                    }
                  })
                } else {
                  this.msgInfo('该条信息已存在')
                }
              }
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const dictCodes = row.dictCode || this.ids
      const dictLabel = row.dictLabel || this.dictLabels
      this.$confirm(
        '是否确认删除参数名称为"' + dictLabel + '"的数据项?',
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
        .then(function() {
          return delData(dictCodes)
        })
        .then(() => {
          this.getList()
          this.msgSuccess('删除成功')
        })
        .catch(function() {})
    }
    /** 导出按钮操作 */
    // handleExport() {
    //   const queryParams = this.queryParams;
    //   this.$confirm("是否确认导出所有数据项?", "警告", {
    //     confirmButtonText: "确定",
    //     cancelButtonText: "取消",
    //     type: "warning",
    //   })
    //     .then(function () {
    //       return exportData(queryParams);
    //     })
    //     .then((response) => {
    //       this.download(response.msg);
    //     })
    //     .catch(function () {});
    // },
    // 强制刷新页面
    // roleNameChange(val) {
    //   // console.log(val)
    //   getBaseDictLabel({ dict_type: val }).then((response) => {
    //     console.log(response)
    //     this.baseDict = response.datas;
    //     if (this.baseDict) this.form.dictLabel = this.baseDict[0].dict_label;
    //   });
    //   this.$forceUpdate();
    // },
  }
}
</script>

