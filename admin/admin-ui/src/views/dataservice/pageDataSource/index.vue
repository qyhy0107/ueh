<template>
  <div class="app-container">
    <div class="operateBox">
      <el-form ref="queryForm" :model="queryParams" :inline="true" label-position="left" label-width="90px">
        <el-form-item label="数据源名称" prop="datasourceName">
          <el-input v-model="queryParams.datasourceName" placeholder="请输入数据源名称" clearable size="small" style="width: 240px" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-row :gutter="20">
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
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="datasourceName" header-align="center" label="数据源名称" width="180" align="left" :show-overflow-tooltip="true" />
        <el-table-column prop="accessMode" header-align="center" label="访问类型" align="left" :show-overflow-tooltip="true" width="100" />
        <el-table-column prop="alias" header-align="center" label="别名" align="left" :show-overflow-tooltip="true" width="100" />
        <!-- <el-table-column
        prop="adapter"
        header-align="center"
        align="left"
        label="适配器名称"
        :show-overflow-tooltip="true"
        width="300"
        ></el-table-column>-->
        <el-table-column prop="driverName" header-align="center" align="left" label="数据库驱动名称" :show-overflow-tooltip="true" width="300" />
        <el-table-column width="500" prop="url" header-align="center" align="left" label="访问URL" :show-overflow-tooltip="true" />
        <el-table-column prop="databaseType" header-align="center" label="数据库类型" align="left" :show-overflow-tooltip="true" width="150" />
        <el-table-column prop="database" header-align="center" align="left" label="数据库名" :show-overflow-tooltip="true" width="150" />
        <el-table-column prop="username" header-align="center" label="账号" align="left" :show-overflow-tooltip="true" width="150" />
        <el-table-column prop="password" header-align="center" label="密码" align="left" :show-overflow-tooltip="true" width="150" />
        <el-table-column prop="isEnable" header-align="center" align="left" label="是否启用" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isEnable=='N'" size="small" type="danger">否</el-tag>
            <el-tag v-else size="small">是</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isWrite" header-align="center" align="left" label="是否可编辑" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isWrite=='N'" size="small" type="danger">否</el-tag>
            <el-tag v-else size="small">是</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isDataSource" header-align="center" align="left" label="是否加入连接池" width="120">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isDataSource=='N'" size="small" type="danger">否</el-tag>
            <el-tag v-else size="small">是</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150" fixed="right">
          <template slot-scope="scope">
            <el-button v-hasPermi="['system:group:edit']" size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改</el-button>
            <!-- <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['system:group:remove']"
            >删除</el-button>-->
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total>0" :page-sizes="[10, 20, 50, 200]" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
    </div>

    <!-- 添加或修改参数配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="50%">
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="数据源名称" prop="datasourceName">
              <el-input v-model="form.datasourceName" placeholder="数据源名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="别名" prop="alias">
              <el-input v-model="form.alias" placeholder="别名" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="访问URL" prop="url">
              <el-input v-model="form.url" type="textarea" placeholder="访问URL" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="访问类型" prop="accessMode">
              <el-radio-group v-model="form.accessMode">
                <el-radio :label="'jdbc'">jdbc</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数据库类型" prop="databaseType">
              <el-select v-model="form.databaseType" placeholder="请选择" style="width:100%">
                <el-option v-for="dict in databaseTypeOptions" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数据库驱动名称" prop="driverName">
              <el-input v-model="form.driverName" placeholder="数据库驱动名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数据库名" prop="database">
              <el-input v-model="form.database" placeholder="数据库名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账号" prop="username">
              <el-input v-model="form.username" placeholder="账号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" placeholder="密码" />
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
              <el-input v-model="form.remark" placeholder="请输入内容" />
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
  listPageDataSource,
  getPageDataSource,
  delPageDataSource,
  addPageDataSource,
  updatePageDataSource
} from '@/api/dataservice/pageDataSource'

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
      databaseTypeOptions: [
        {
          dictLabel: 'mysql',
          dictValue: 'mysql'
        }
      ],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        datasourceName: undefined,
        disEnable: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        datasourceName: [
          { required: true, message: '数据源名称不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
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
      listPageDataSource(
        this.addDateRange(this.queryParams, this.dateRange)
      ).then((response) => {
        this.pageDataSourceList = response.rows
        this.total = response.total
        this.loading = false
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
        datasourceName: undefined,
        accessMode: 'jdbc',
        alias: undefined,
        adapter: 'com.jz.data.service.adapter.JdbcAdapter',
        driverName: undefined,
        url: undefined,
        databaseType: undefined,
        database: undefined,
        username: undefined,
        password: undefined,
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
      this.title = '添加数据源配置参数'
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
      getPageDataSource(id).then((response) => {
        this.form = response.data
        this.open = true
        this.title = '修改数据源配置参数'
      })
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          if (this.form.id != undefined) {
            updatePageDataSource(this.form).then((response) => {
              if (response.code === 200) {
                this.msgSuccess('修改成功')
                this.open = false
                this.getList()
              } else {
                this.msgError(response.msg)
              }
            })
          } else {
            addPageDataSource(this.form).then((response) => {
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
      this.$confirm('是否确认删除参数编号为"' + ids + '"的数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(function() {
          return delPageDataSource(ids)
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
