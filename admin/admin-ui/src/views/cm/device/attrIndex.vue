<template>
  <div class="app-container">
    <!-- <div class="buttonBox">
      <el-button size="mini" @click="backFatherPage">返回</el-button>
    </div> -->
    <div class="operationInfoBox">
      <el-row :gutter="20">
        <el-col :span="8">
          <span class="attrName"><i class="el-icon-monitor" />设备名称:</span>
          <span class="attrValue">{{ attrInfo.name||'暂无数据' }}</span>
        </el-col>
        <el-col :span="8">
          <span class="attrName"><i class="el-icon-aim" />设备标识:</span>
          <span class="attrValue">{{ attrInfo.labelText||'暂无数据' }}</span>
        </el-col>
        <el-col :span="8">
          <span class="attrName"><i class="el-icon-folder-opened" />设备类别:</span>
          <span class="attrValue">{{ attrInfo.className||'暂无数据' }}</span>
        </el-col>
      </el-row>
      <el-row :gutter="20" style="margin-top:8px">
        <el-col :span="8">
          <span class="attrName"><i class="el-icon-link" />设备IP:</span>
          <span class="attrValue">{{ attrInfo.ipAddress||'暂无数据' }}</span>
        </el-col>
        <el-col :span="8">
          <span class="attrName"><i class="el-icon-user" />负责人:</span>
          <span class="attrValue">{{ attrInfo.owner||'暂无数据' }}</span>
        </el-col>
        <el-col :span="8">
          <span class="attrName"><i class="el-icon-location-outline" />位置:</span>
          <span class="attrValue">{{ attrInfo.area||'暂无数据' }}</span>
        </el-col>
      </el-row>
    </div>
    <div ref="OperateBox" class="operateBox">
      <el-row>
        <el-col :span="24">
          <el-form ref="queryParams" :model="queryParams" :rules="rules" :inline="true" label-position="left" style="position:relative;">
            <el-form-item label="属性组" prop="attrGroupId">
              <el-select v-model="queryParams.attrGroupId" placeholder="请选择属性组" clearable filterable size="small" @change="queryParams.attrId='';getAttrs()">
                <el-option v-for="item in attrGroups" :key="item.id" :label="item.labelText" :value="item.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="属性名称" prop="attrId">
              <el-select v-model="queryParams.attrId" placeholder="请选择属性" clearable filterable size="small" @change="isEnableAttrId">
                <el-option v-for="item in attrs" :key="item.id" :label="item.labelText" :value="item.id" />
                <!-- <el-option v-for="item in attrs" :key="item.id" :label="item.labelText" :value="item.id" :disabled="!item.isEnable" /> -->
              </el-select>
            </el-form-item>
            <el-form-item label="属性值" prop="value">
              <el-input v-model.trim="queryParams.value" placeholder="请输入属性值" clearable size="small" />
            </el-form-item>
            <el-form-item label="显示顺序" prop="displayOrder">
              <el-input-number v-model="queryParams.displayOrder" placeholder="请输入显示顺序" :min="1000" :step="10" clearable size="small" controls-position="right" />
            </el-form-item>
            <el-form-item label="启用" prop="isEnable">
              <el-select v-model="queryParams.isEnable" placeholder="请选择" clearable size="small">
                <el-option v-for="dict in typeOptions" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue" />
              </el-select>
            </el-form-item>
            <!-- <el-form-item style="position:absolute;right:-5px;top:-5px">
              <el-button size="mini" @click="backFatherPage">返回</el-button>
            </el-form-item> -->
            <!-- <el-row>
              <el-col :span="24"> -->
            <el-form-item style="float:right">
              <el-button class="addButton" icon="el-icon-plus" size="mini" @click="handleAdd">保存</el-button>
              <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery()">查询</el-button>
              <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
            </el-form-item>
            <!-- </el-col>
            </el-row> -->
          </el-form>
        </el-col>
      </el-row>
    </div>
    <div class="showTableBox" :style="{height:OperateBoxHeight+'px'}">
      <el-table v-if="dataList.length" v-loading="loading" border :data="dataList" :height="'90%'">
        <el-table-column align="left" label="设备类别" prop="className" :show-overflow-tooltip="true">
          <!-- <template>
            {{ device }}
          </template> -->
        </el-table-column>
        <el-table-column align="left" label="设备名称" prop="resName" :show-overflow-tooltip="true" width="200" />
        <el-table-column align="left" label="属性组" prop="attrGroupName" :show-overflow-tooltip="true" :formatter="ismappingValue" />
        <el-table-column align="left" label="属性" prop="attrName" :show-overflow-tooltip="true" :formatter="ismappingValue" />
        <el-table-column align="left" label="值" prop="value" :show-overflow-tooltip="true" width="200" />
        <el-table-column align="left" label="CMDB属性名称" prop="accessDescription" :show-overflow-tooltip="true" width="120" />
        <el-table-column align="left" label="CMDB属性编号" prop="accessName" :show-overflow-tooltip="true" width="150" />
        <el-table-column align="left" label="显示顺序" prop="displayOrder" :show-overflow-tooltip="true" />
        <el-table-column align="left" label="启用" prop="isEnable" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isEnable=='N'" size="small" type="danger">不启用</el-tag>
            <el-tag v-else size="small">启用</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="left" label="备注" prop="remark" :show-overflow-tooltip="true" />
        <el-table-column label="操作" align="center" width="250" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <Deficiency v-else width="25%" height="auto" />
      <pagination v-show="totalCount>0" :page-sizes="[10, 20, 50, 200]" :total="totalCount" :page.sync="queryParams.start" :limit.sync="queryParams.limit" @pagination="handleQuery()" />
    </div>
  </div>
</template>

<script>
import {
  canAdd,
  getResourceAttributeValueList,
  getResourceAttributeValue,
  addResourceAttributeValue,
  updateResourceAttributeValue,
  delResourceAttributeValue
} from '@/api/cm/device.js'
import { getResourceAttributeGroupList } from '@/api/cm/deviceAttrGroup.js'
import { getResourceAttributeList } from '@/api/cm/deviceAttr.js'
import { getPhysicalCategories } from '@/api/cm/physicalCategory'
// 省缺组件
import Deficiency from '@/components/Deficiency/index'
export default {
  name: 'DeviceAttr',
  components: { Deficiency },
  data() {
    return {
      // 页面高度
      OperateBoxHeight: 100,
      // show: false,
      isCheckAttrId: [],
      indexArr: [],
      totalCount: 0,
      dataList: [],
      // attrInfo: JSON.parse(localStorage.getItem('deviceAttr')),
      queryParams: {
        start: 1,
        limit: 20,
        attrGroupId: undefined,
        attrId: undefined,
        value: '',
        resId: null,
        isEnable: 'Y',
        displayOrder: 1000,
        projectId: 10,
        engine: 'Freemarker'
      },
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
      rules: {
        attrId: [{ required: true, message: '请选择属性', trigger: 'blur' }],
        value: [{ required: true, message: '请输入属性值', trigger: 'blur' }]
      },
      deviceCategories: [],
      loading: false,
      device: {},
      attrGroups: [],
      attrs: []
    }
  },
  computed: {
    // 计算属性的 getter
    attrInfo: function() {
      //  : JSON.parse(localStorage.getItem('deviceAttr')),
      return { ...this.device }
    }
  },
  watch: {
    'queryParams.attrGroupId': function(newValue, oldValue) {
      this.getAttrs()
    }
  },
  created() {
    // this.device = JSON.parse(localStorage.getItem('deviceAttr'))
    if (!this.$route.params || JSON.stringify(this.$route.params) === '{}') {
      this.device = JSON.parse(localStorage.getItem('deviceAttr'))
    } else {
      // if (this.$route.params.classId) {
      // 获取到缓存里的值给temp
      const temp = JSON.parse(localStorage.getItem('deviceAttr')) || {}
      // 判断两个host是否相同
      if (temp && JSON.stringify(temp) !== '{}') {
        if (this.$route.params.key_ !== temp.key_) {
          this.reload()
          this.resetQuery()
          this.device = this.$route.params
          localStorage.setItem('deviceAttr', JSON.stringify(this.$route.params))
        } else {
          this.device = this.$route.params
        }
        // }
      } else {
        // this.reload()
        // this.resetQuery()
        this.device = this.$route.params
        localStorage.setItem('deviceAttr', JSON.stringify(this.device))
      }
    }
    this.queryParams.projectId = this.device.projectId || 10
    this.queryParams.resId = this.device.id || null
    this.getAttrs()
    this.getAttrGroups()
    this.getList()
  },
  updated() {
    this.getOperateBoxHeight()
  },
  mounted() {
    this.getAttrs()
    this.getOperateBoxHeight()
  },
  inject: ['reload'],
  activated() {
    if (!this.$route.params || JSON.stringify(this.$route.params) === '{}') {
      this.device = JSON.parse(localStorage.getItem('deviceAttr'))
    } else {
      // if (this.$route.params.classId) {
      // 获取到缓存里的值给temp
      const temp = JSON.parse(localStorage.getItem('deviceAttr')) || {}
      // 判断两个host是否相同
      if (temp && JSON.stringify(temp) !== '{}') {
        if (this.$route.params.key_ !== temp.key_) {
          this.reload()
          this.resetQuery()
          this.device = this.$route.params
          localStorage.setItem('deviceAttr', JSON.stringify(this.$route.params))
        // }
        } else {
          this.device = this.$route.params
        }
      } else {
        // this.reload()
        // this.resetQuery()
        this.device = this.$route.params
        localStorage.setItem('deviceAttr', JSON.stringify(this.device))
      }
    }
    // this.reload()
    // this.device = JSON.parse(localStorage.getItem('deviceAttr'))
    this.queryParams.projectId = this.device.projectId || 10
    this.queryParams.resId = this.device.id || null
    this.getAttrGroups()
    this.getList()
  },
  methods: {
    ismappingValue(row, column, cellValue, index) {
      // console.log(row, column, cellValue, index)
      // console.log(cellValue)
      if (cellValue) {
        return cellValue
      } else {
        return <span style='color:#909399'>-待映射-</span>
      }
    },
    isEnableAttrId(value) {
      // console.log(value)
      this.$set(this.queryParams, 'attrId', value)
    },
    // 返回上级页面
    backFatherPage() {
      this.$router.go(-1)
    },
    getPhysicalCategory() {
      getPhysicalCategories({
        projectId: this.queryParams.projectId,
        engine: 'Freemarker',
        isEnable: 'Y'
      }).then((response) => {
        if (response.success) {
          this.deviceCategories = response.datas
        } else {
          this.msgError('查询设备类别失败：\n' + response.errorMsg)
        }
      })
    },
    getAttrGroups() {
      getResourceAttributeGroupList({
        projectId: this.queryParams.projectId,
        classId: this.device.classId,
        engine: 'Freemarker',
        isEnable: 'Y'
      }).then((response) => {
        if (response.success) {
          this.attrGroups = response.datas
        } else {
          this.msgError('查询设备属性组失败：\n' + response.errorMsg)
        }
      })
    },
    getAttrs() {
      getResourceAttributeList({
        projectId: this.queryParams.projectId,
        classId: this.device.classId,
        attrGroupId: this.queryParams.attrGroupId,
        engine: 'Freemarker',
        isEnable: 'Y'
      }).then((response) => {
        if (response.success) {
          this.attrs = response.datas
          this.indexArr = []
          this.isCheckAttrId.forEach((item) => {
            this.attrs.forEach((itemc, index) => {
              // itemc.isEnable = true
              // console.log(itemc)
              if (itemc.id === item) {
                this.indexArr.push(index)
              }
            })
          })
          // this.indexArr.forEach((item) => {
          //   this.attrs[item].isEnable = false
          // })
        } else {
          this.msgError('查询设备属性失败：\n' + response.errorMsg)
        }
      })
    },
    // 获取页面高度
    getOperateBoxHeight() {
      this.$nextTick(function() {
        this.OperateBoxHeight =
          document.getElementsByClassName('app-container')[0].offsetHeight -
          document.getElementsByClassName('operateBox')[0].offsetHeight -
          80
      })
    },
    handleUpdate(row) {
      this.resetQuery()
      // this.loading = true;
      this.$message('请在查询区域对内容进行修改和保存的操作')
      getResourceAttributeValue({
        projectId: row.projectId,
        engine: 'Freemarker',
        id: row.id
      }).then((response) => {
        if (response.success) {
          this.queryParams = response.mapData
        }
      })
    },
    handleAdd() {
      this.submitForm()
    },
    handleDelete(row) {
      // console.log(row)
      const labelText = row.attrName
      this.$confirm('是否确认删除属性组为' + labelText + '的数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          delResourceAttributeValue({
            projectId: this.queryParams.projectId,
            engine: 'Freemarker',
            ids: row.id
          }).then((response) => {
            if (response.success) {
              this.getList()
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
      this.$refs['queryParams'].validate((valid) => {
        if (valid) {
          this.queryParams.engine = 'Freemarker'
          if (this.queryParams.id !== undefined) {
            updateResourceAttributeValue(this.queryParams).then((response) => {
              if (response.success) {
                this.msgSuccess('修改成功')
                this.open = false
                this.getList()
              } else {
                this.msgError(response.errorMsg)
              }
            })
          } else {
            canAdd(this.queryParams).then((response) => {
              // console.log(response)
              if (response.strData && response.success && response.strData !== '0') {
                this.msgInfo('该属性名称已有记录')
              } else {
                this.queryParams.projectId = this.queryParams.projectId
                addResourceAttributeValue(this.queryParams).then((response) => {
                  if (response.success) {
                    this.msgSuccess('新增成功')
                    this.open = false
                    this.getList()
                  } else {
                    this.msgError(response.errorMsg)
                  }
                })
              }
            })
            // this.queryParams.projectId = this.queryParams.projectId;
            // addResourceAttributeValue(this.queryParams).then((response) => {
            //   if (response.success) {
            //     this.msgSuccess('新增成功')
            //     this.open = false
            //     this.getList()
            //   } else {
            //     this.msgError(response.errorMsg)
            //   }
            // })
          }
        }
      })
    },
    getList() {
      this.loading = true
      this.queryParams.resId = this.device.id
      this.queryParams.classId = this.device.classId
      getResourceAttributeValueList(this.queryParams).then((response) => {
        this.loading = false
        if (response.success) {
          this.totalCount = response.totalCount
          this.dataList = response.datas
          this.isCheckAttrId = []
          this.dataList.forEach((item) => {
            // console.log(item)
            this.isCheckAttrId.push(item.attrId)
          })
        } else {
          this.dataList = []
          this.msgError('查询失败：\n' + response.errorMsg)
        }
      })
    },
    /** 查询按钮操作 */
    handleQuery() {
      // this.queryParams.start = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.queryParams = {
        start: 1,
        limit: 20,
        name: '',
        attrId: '',
        displayOrder: 1000,
        classId: null,
        isEnable: 'Y',
        projectId: 10,
        engine: 'Freemarker'
      }
      this.resetForm('queryParams')
      this.handleQuery()
    }
  }
}
</script>
<style lang="scss" scoped>
</style>

