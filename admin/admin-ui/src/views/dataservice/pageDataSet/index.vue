<template>
  <div class="app-container">
    <div class="operateBox">
      <el-form ref="queryForm" :model="queryParams" :inline="true" label-position="left" label-width="110px">
        <el-form-item label="数据集名称" prop="name">
          <el-input v-model="queryParams.name" placeholder="请输入数据集名称" clearable size="small" style="width: 240px" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="数据集组名称" prop="group">
          <el-input v-model="queryParams.group" placeholder="请输入数据集组名称" clearable size="small" style="width: 240px" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-row :gutter="10">
        <el-col :span="1.5">
          <el-button v-hasPermi="['system:config:add']" type="primary" icon="el-icon-plus" size="mini" @click="handleAdd">新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button v-hasPermi="['system:group:edit']" type="success" icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate">修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button v-hasPermi="['system:group:remove']" type="danger" icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete">删除</el-button>
        </el-col>
      </el-row>
    </div>
    <div class="showTableBox" :style="{height:OperateBoxHeight+'px'}">
      <el-table v-loading="loading" :data="pageDataSourceList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="name" header-align="center" align="left" label="数据集名称" :show-overflow-tooltip="true" width="200" />
        <el-table-column prop="group" :show-overflow-tooltip="true" header-align="center" align="left" label="数据集组名称" width="150" />
        <el-table-column prop="labelText" :show-overflow-tooltip="true" header-align="center" align="left" label="显示名称" width="150" />
        <el-table-column width="150" prop="datasetType" :show-overflow-tooltip="true" header-align="center" align="left" label="数据集类型" />
        <el-table-column prop="columns" :show-overflow-tooltip="true" header-align="center" align="left" label="返回列名" width="300" />
        <el-table-column width="300" :show-overflow-tooltip="true" prop="labelTexts" header-align="center" align="left" label="返回列显示名称" />
        <el-table-column width="300" :show-overflow-tooltip="true" prop="dataTypes" header-align="center" align="left" label="返回列类型" />
        <el-table-column width="300" :show-overflow-tooltip="true" prop="returnType" header-align="left" align="center" label="返回结果类型">
          <!-- <template slot-scope="scope">
          <span>{{ dictionary[scope.row.returnType]}}</span>
          </template>-->
        </el-table-column>
        <el-table-column width="140" :show-overflow-tooltip="true" prop="execType" header-align="center" align="left" label="SQL表达式类型">
          <!-- <template slot-scope="scope">
          <span>{{ dictionary[scope.row.execType] }}</span>
          </template>-->
        </el-table-column>
        <el-table-column prop="execSql" width="500" :show-overflow-tooltip="true" header-align="center" align="left" label="表达式" />
        <el-table-column width="180" :show-overflow-tooltip="true" prop="filterParamNames" header-align="center" align="left" label="过滤参数名称" />
        <el-table-column width="100" :show-overflow-tooltip="true" prop="filterValues" header-align="center" align="left" label="过滤参数值" />
        <el-table-column width="150" :show-overflow-tooltip="true" prop="mainDatasourceId" header-align="center" align="left" label="主数据源ID">
          <!-- <template slot-scope="scope">
          <span>{{ dictionaryone[scope.row.mainDatasourceId] }}</span>
          </template>-->
        </el-table-column>
        <el-table-column :show-overflow-tooltip="true" prop="unionDatasetIds" header-align="center" align="left" width="120" label="待合并数据集ID" />
        <el-table-column :show-overflow-tooltip="true" prop="unionColumns" header-align="center" align="left" label="合并条件" width="100" />
        <el-table-column prop="isWrite" width="100" header-align="center" align="left" label="是否可编辑">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isWrite=='N'" size="small" type="danger">否</el-tag>
            <el-tag v-else size="small">是</el-tag>
          </template>
        </el-table-column>
        <el-table-column width="100" prop="isEnable" header-align="center" align="left" label="是否启用">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isEnable=='N'" size="small" type="danger">否</el-tag>
            <el-tag v-else size="small">是</el-tag>
          </template>
        </el-table-column>
        <el-table-column :show-overflow-tooltip="true" prop="remark" width="150" header-align="center" align="left" label="备注" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button v-hasPermi="['system:group:edit']" size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button v-hasPermi="['system:group:remove']" size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total>0" :page-sizes="[10, 20, 50, 200]" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
    </div>

    <!-- 添加或修改参数配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="50%">
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="数据集名称" prop="name">
              <el-input v-model="form.name" placeholder="数据集名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="数据集组名称" prop="group">
              <el-input v-model="form.group" placeholder="数据集组名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="显示名称" prop="labelText">
              <el-input v-model="form.labelText" placeholder="显示名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="数据集类型" prop="datasetType">
              <el-radio-group v-model="form.datasetType">
                <el-radio :label="'mysql'">mysql</el-radio>
              </el-radio-group>
              <!-- <el-input v-model="form.datasetType" placeholder="数据集类型"></el-input> -->
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="返回字段" prop="columns">
              <el-input v-model="form.columns" placeholder="返回字段，以#分隔" type="textarea" rows="2" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="返回字段名称" prop="labelTexts">
              <el-input v-model="form.labelTexts" placeholder="返回字段名称,以#分隔" type="textarea" rows="2" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="返回列类型" prop="dataTypes">
              <el-input v-model="form.dataTypes" placeholder="返回列类型，以#分隔" rows="2" type="textarea" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="返回结果类型" prop="returnType">
              <el-radio-group v-model="form.returnType">
                <el-radio :label="'SRMC'">单行</el-radio>
                <el-radio :label="'MRSC'">多行</el-radio>
                <el-radio :label="'SRSC'">单行单列</el-radio>
                <el-radio :label="'MRMC'">多行多列</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="表达式类型" prop="execType">
              <el-radio-group v-model="form.execType">
                <el-radio :label="'QUERY'">查询</el-radio>
                <el-radio :label="'ADD'">添加</el-radio>
                <el-radio :label="'DELETE'">删除</el-radio>
                <el-radio :label="'UPDATE'">修改</el-radio>
                <el-radio :label="'PROC'">存储过程</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="表达式" prop="execSql">
              <!-- :options="cmOptions" -->
              <!-- <codemirror ref="cmRef" v-model="form.execSql" ></codemirror> -->
              <el-input v-model="form.execSql" placeholder="表达式" rows="2" type="textarea" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="主数据源" prop="mainDatasourceId">
              <el-select v-model="form.mainDatasourceId" placeholder="请选择主数据源ID" style="width:100%">
                <el-option v-for="(item,index) in selectdata" :key="index" :label="item.datasourceName" :value="item.id" />
              </el-select>
              <!-- <el-input v-model="form.mainDatasourceId" placeholder="主数据源ID" rows="5"></el-input> -->
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="待合并数据集" prop="unionDatasetIds">
              <el-select v-model="form.unionDatasetIds" placeholder="请选择待合并数据集ID" multiple style="width:100%">
                <el-option v-for="(item,index) in selectdataTwo" :key="index" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="合并条件" prop="unionColumns">
              <el-input v-model="form.unionColumns" placeholder="合并条件，以#分隔，例如name=hostName#name2#hostName2" rows="2" type="textarea" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="过滤参数名称" prop="filterParamNames">
              <el-input v-model="form.filterParamNames" placeholder="过滤参数名称,以#分隔" rows="2" type="textarea" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="过滤参数值" prop="filterValues">
              <el-input v-model="form.filterValues" placeholder="过滤参数值" rows="2" type="textarea" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否可编辑" size="mini" prop="isWrite">
              <el-radio-group v-model="form.isWrite">
                <el-radio :label="'Y'">是</el-radio>
                <el-radio :label="'N'">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否可用" prop="isEnable">
              <el-radio-group v-model="form.isEnable">
                <el-radio v-for="dict in typeOptions" :key="dict.dictValue" :label="dict.dictValue">{{ dict.dictLabel }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listPageDataSet,
  getPageDataSet,
  delPageDataSet,
  addPageDataSet,
  updatePageDataSet,
  optionselect
} from '@/api/dataservice/pageDataSet'
import { listPageDataSource } from '@/api/dataservice/pageDataSource'
export default {
  data() {
    return {
      OperateBoxHeight: 100,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 总条数
      total: 0,
      // 参数表格数据
      pageDataSourceList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 类型数据字典
      typeOptions: [],
      // 日期范围
      dateRange: [],
      names: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        group: undefined
      },
      selectdata: [],
      selectdataTwo: [],
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: '数据集名称不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
    this.getPageDataSets()
    this.getPageDataSources()
    this.getDicts('sys_yes_no').then((response) => {
      this.typeOptions = response.data
    })
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
          40
      })
    },
    /** 查询参数列表 */
    getList() {
      this.loading = true
      listPageDataSet(this.addDateRange(this.queryParams, this.dateRange)).then(
        (response) => {
          this.pageDataSourceList = response.rows
          this.total = response.total
          this.loading = false
        }
      )
    },
    getPageDataSets() {
      optionselect().then((response) => {
        this.selectdataTwo = response.data
      })
    },
    getPageDataSources() {
      listPageDataSource().then((response) => {
        this.selectdata = response.rows
      })
    },
    // 参数系统内置字典翻译
    typeFormat(row, column) {
      return this.selectDictLabel(this.typeOptions, row.flag)
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        name: undefined,
        group: undefined,
        datasetType: 'mysql',
        columns: undefined,
        labelText: undefined,
        labelTexts: undefined,
        dataTypes: undefined,
        execType: 'QUERY',
        returnType: 'SRMC',
        execSql: undefined,
        filterParam_names: undefined,
        filterValues: undefined,
        mainDatasourceId: undefined,
        unionDatasetIds: undefined,
        unionColumns: undefined,
        unionFilterValues: undefined,
        isWrite: 'Y',
        isEnable: 'Y',
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
      this.title = '添加数据集配置参数'
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id)
      this.single = selection.length != 1
      this.multiple = !selection.length
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getPageDataSet(id).then((response) => {
        this.form = response.data
        this.open = true
        this.title = '修改数据集配置参数'
      })
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          var str = ''
          if (this.form.unionDatasetIds) {
            this.form.unionDatasetIds.forEach((item, index) => {
              if (index == 0) {
                str += item
              } else {
                str += '#' + item
              }
            })
          }
          this.form.unionDatasetIds = str
          if (this.form.id != undefined) {
            updatePageDataSet(this.form).then((response) => {
              if (response.code === 200) {
                this.msgSuccess('修改成功')
                this.open = false
                this.getList()
              } else {
                this.msgError(response.msg)
              }
            })
          } else {
            // }
            addPageDataSet(this.form).then((response) => {
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
      const ids = row.id || this.ids
      const name = row.name || this.names
      this.$confirm('是否确认删除数据集名称为"' + name + '"的数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(function() {
          return delPageDataSet(ids)
        })
        .then(() => {
          this.getList()
          this.msgSuccess('删除成功')
        })
        .catch(function() {})
    }
  }
}
</script>
<style lang="scss" scoped>
@import "../../../assets/styles/index.scss";
.operateBox {
  //   border-radius: 4px;
  padding: 15px;
  // padding-top: 2px;
  padding-left: 15px;
}
.showTableBox {
  margin-top: 20px;
  padding: 0px;
}
</style>
