<template>
  <div class="app-container">
    <div class="operateBox">
      <el-row>
        <el-col :span="24">

          <el-form :inline="true" label-position="left">
            <el-form-item label="厂商名称">
              <el-input v-model="queryParams.name" placeholder="请输入厂商名称" clearable size="small" @keyup.enter.native="handleSearch()" />
            </el-form-item>
            <el-form-item label="启用" prop="isEnable">
              <el-select v-model="queryParams.isEnable" placeholder="请选择" clearable size="small" @change="handleSearch()">
                <el-option v-for="dict in typeOptions" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" size="mini" @click="handleSearch()">查询</el-button>
              <el-button icon="el-icon-refresh" size="mini" @click="rest">重置</el-button>
              <el-button class="addButton" icon="el-icon-plus" size="mini" @click="handleAdd">新增</el-button>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
    </div>
    <div class="showTableBox" :style="{height:OperateBoxHeight+'px'}">
      <el-table  v-loading="loading" :data="dataList"  height="90%">
        <template v-if="!loading" slot="empty" style="height:100%">
          <Deficiency width="30%" height="auto" />
        </template>
        <el-table-column align="left" label="厂商名称" prop="name" :show-overflow-tooltip="true" />
        <el-table-column align="left" label="显示名称" prop="labelText" :show-overflow-tooltip="true" />
        <el-table-column align="left" label="显示顺序" prop="displayOrder" :show-overflow-tooltip="true" />
        <el-table-column align="left" label="启用" prop="isEnable" :show-overflow-tooltip="true" />
        <el-table-column align="left" label="备注" prop="remark" :show-overflow-tooltip="true" />
        <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="totalCount>0" :page-sizes="[10, 20, 50, 200]" :total="totalCount" :page.sync="queryParams.start" :limit.sync="queryParams.limit" @pagination="handleSearch()" />
    </div>
    <!-- 添加或修改菜单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" :onclose="cancel">
      <el-form ref="form" :model="form" label-width="120px" :rules="rules">
        <el-form-item label="厂商名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入属性组名称" clearable :disabled="title=='修改'" />
        </el-form-item>

        <el-form-item label="显示名称" prop="labelText">
          <el-input v-model="form.labelText" placeholder="请输入属性组显示名称" clearable style="width:100%" />
        </el-form-item>

        <el-form-item label="显示顺序" prop="displayOrder">
          <el-input-number v-model="form.displayOrder" :min="1000" :step="10" controls-position="right" />
        </el-form-item>

        <el-form-item label="启用" prop="isEnable">
          <el-radio-group v-model="form.isEnable">
            <el-radio label="Y">启用</el-radio>
            <el-radio label="N">不启用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" clearable />
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
  getVendorList,
  addVendor,
  getVendorById,
  updateVendor,
  delVendor
} from '@/api/cm/vendor.js'
import Deficiency from '@/components/Deficiency'
export default {
  components: { Deficiency },
  data() {
    return {

      OperateBoxHeight: 100,
      // 遮罩层
      loading: true,
      classIdName: '',
      classIdName1: '',
      totalCount: 0,
      saveOrUpdate: '',
      open: false,
      form: {
        id: '',
        name: '',
        labelText: '',
        isEnable: 'Y',
        displayOrder: 1000,
        remark: '',
        classId: 0,
        projectId: 10
      },
      title: '新增',
      total: 0,
      dataList: [],
      // 类型数据字典
      typeOptions: [
        {
          dictValue: 'Y',
          dictLabel: '启用'
        },
        {
          dictValue: 'N',
          dictLabel: '不启用'
        }
      ],
      queryParams: {
        start: 1,
        limit: 20,
        classId: undefined,
        name: '',
        isEnable: 'Y',
        projectId: 10,
        engine: 'Freemarker'
      },
      deviceCategories: [],
      deviceCategories1: [],
      rules: {
        name: [
          { required: true, message: '请输入属性组名称', trigger: 'blur' }
        ],
        classId: [
          { required: true, message: '请选择资源类别', trigger: 'blur' }
        ],
        displayOrder: [{ required: true, message: '请输入属性显示顺序', trigger: 'blur' }]
      },
      rooms: [],
      specification: []
    }
  },

  created() {
    this.handleSearch()
  },
  updated() {
    this.getOperateBoxHeight()
  },
  mounted() {
    this.getOperateBoxHeight()
  },
  methods: {
    filterNode(value, data) {
      if (!value) return true
      const stringTemp = data.label.toLowerCase()
      const stringTemp1 = value.toLowerCase()
      return stringTemp.indexOf(stringTemp1) !== -1
    },
    // 获取页面高度
    getOperateBoxHeight() {
      this.$nextTick(function() {
        this.OperateBoxHeight =
          document.getElementsByClassName('app-container')[0].offsetHeight -
          document.getElementsByClassName('operateBox')[0].offsetHeight -
          40
      })
    },

    handleNodeClick(data) {
      const temp = []
      if (data.children) {
        if (data.children.length) {
          data.children.forEach(element => {
            temp.push(element.id)
          })
        }
      }
      this.queryParams.classIdList = temp
      // 这里主要配置树形组件点击节点后，设置选择器的值；自己配置的数据，仅供参考
      this.classIdName = data.label
      // this.queryParams.kpiGroupId = ''
      // this.queryParams.kpiName = ''
      this.queryParams.classId = data.id
      // 选择器执行完成后，使其失去焦点隐藏下拉框的效果
      this.$refs.selectUpResId.blur()
      // this.groupOptions = []
      this.handleSearch()
    },

    handleAdd() {
      // this.cancel();
      this.$nextTick(() => {
        if (this.$refs.form) {
          this.$refs.form.clearValidate()
        }
      })
      this.form.id = ''
      this.form.classId = ''
      this.form.name = ''
      this.form.labelText = ''
      this.form.isEnable = 'Y'
      this.form.displayOrder = 1000
      this.form.remark = ''
      this.form.projectId = 10
      this.saveOrUpdate = 'Save'
      this.title = '新增'
      this.open = true
    },
    handleUpdate(row) {
      this.form.id = row.id
      getVendorById({
        projectId: row.projectId,
        engine: 'Freemarker',
        id: row.id
      }).then((response) => {
        // console.log(response)
        if (response.success != null) {
          this.form = response.mapData
        }
      })
      this.saveOrUpdate = 'Update'
      this.title = '修改'
      this.open = true
    },
    cancel() {
      this.$nextTick(() => {
        if (this.$refs.form) {
          this.$refs.form.clearValidate()
        }
      })
      this.form = {
        id: '',
        name: '',
        labelText: '',
        isEnable: 'Y',
        displayOrder: 1000,
        remark: '',
        projectId: 10
      }
      this.open = false
    },

    handleDelete(row) {
      this.$confirm('是否确认删除该数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          delVendor({
            projectId: row.projectId,
            engine: 'Freemarker',
            ids: row.id
          }).then((response) => {
            if (response.success) {
              this.queryParams.start = 1
              this.msgSuccess('删除成功')
              this.handleSearch()
            } else {
              this.msgError('删除失败：\n' + response.errorMsg)
            }
          })
        })
        .catch(function(e) {
          console.error(e.message)
        })
    },
    submitForm() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          this.form.engine = 'Freemarker'
          if (!this.form.labelText) {
            this.form.labelText = this.form.name
          }
          if (this.saveOrUpdate === 'Save') {
            addVendor(this.form).then((response) => {
              if (response.success === true) {
                this.msgSuccess('新增成功')
                this.queryParams.start = 1
                this.handleSearch()
                this.open = false
              } else {
                this.msgError('新增失败:\n' + response.errorMsg)
                this.open = false
              }
            })
          } else {
            if (this.form.id) {
              updateVendor(this.form).then((response) => {
                if (response.success === true) {
                  this.msgSuccess('修改成功')
                  this.queryParams.start = 1
                  this.handleSearch()
                  this.open = false
                } else {
                  this.msgError('修改失败:\n' + response.errorMsg)
                  this.open = false
                }
              })
            }
          }
        }
      })
    },
    handleSearch(tag) {
      this.loading = true
      if (tag) {
        this.queryParams.start = tag
      }
      getVendorList(this.queryParams).then((response) => {
        // console.log(this.queryParams)
        if (response.success) {
          // console.log(response)
          this.dataList = response.datas
          this.totalCount = response.totalCount
          this.loading = false
        } else {
          this.dataList = []
          this.loading = false
          this.msgError('查询失败：\n' + response.errorMsg)
        }
      })
    },
    rest() {
      // this.queryParams.name = "";
      this.queryParams = {
        start: 1,
        limit: 20,
        classId: undefined,
        name: '',
        isEnable: 'Y',
        projectId: 10,
        engine: 'Freemarker'
      }
      this.handleSearch(1)
    }
  }
}
</script>

