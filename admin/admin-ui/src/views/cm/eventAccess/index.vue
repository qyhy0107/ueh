<template>
  <div class="app-container">
    <div ref="OperateBox" class="operateBox">
      <el-form :inline="true" :model="formInline" label-position="left">
        <el-form-item label="接入名称">
          <el-input v-model.trim="formInline.name" clearable size="small" placeholder="请输入名称" @keyup.enter.native="onSearch" />
        </el-form-item>
        <el-form-item label="事件源类型" prop="source_type">
          <el-select v-model="formInline.source_type" clearable placeholder="请选择事件源类型" @change="onSearch">
            <el-option v-for="item in eventSourceTypeSelect" :key="item.dictLabel" :label="item.dictLabel" :value="item.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="mini" @click="onSearch">查询</el-button>
          <el-button class="addButton" size="mini" style="float: right; margin-top: 4px" @click="onAdd">新增</el-button>
          <el-button size="mini" @click="onRest">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="showTableBox" :style="{ height: OperateBoxHeight + 'px' }">
      <el-table v-if="dataList.length" :data="dataList" border>
        <el-table-column v-if="show" prop="id" label="id" width="10" />
        <el-table-column prop="name" label="接入名称" show-overflow-tooltip width="220" />
        <el-table-column prop="sourceType" label="事件源类型" width="180" />
        <el-table-column prop="probeKey" label="接入Key" show-overflow-tooltip width="360" />
        <!-- <el-table-column prop="isEnable" label="启用" align="center" width="180">
          <template slot-scope="scope">
            <el-switch v-model="scope.row.isEnable" :disabled="true" />
          </template>
        </el-table-column> -->
        <el-table-column align="center" label="启用" prop="isEnable" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <!-- {{ scope.row.isEnable }} -->
            <el-tag v-if="scope.row.isEnable == false" size="small" type="danger">不启用</el-tag>
            <el-tag v-else size="small">启用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <!-- <el-table-column label="查看策略" align="center" width="200" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleWatch(scope.row)">
              查看策略
            </el-button>
          </template>
        </el-table-column> -->
        <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-view" @click="handleWatch(scope.row)">查看</el-button>
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <Deficiency v-else width="25%" height="auto" />
      <pagination v-show="totalCount > 0" :page-sizes="[10, 20, 50, 200]" :total="totalCount" :page.sync="queryParams.start" :limit.sync="queryParams.limit" @pagination="getEventProbeDefines()" />
    </div>
    <!-- 添加或修改菜单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1000px" class="menuDialog" :before-close="closeHander">
      <el-row style="height: 100%">
        <el-col :span="4" style="height: 100%">
          <el-menu v-model="activeIndex" :default-active="activeIndex" class="el-menu-vertical-demo" @select="IsActive">
            <el-menu-item index="2">
              <span slot="title">接入信息</span>
            </el-menu-item>
            <el-menu-item index="3">
              <span slot="title">字段映射</span>
            </el-menu-item>
            <el-menu-item index="4">
              <span slot="title">级别映射</span>
            </el-menu-item>
          </el-menu>
        </el-col>
        <!-- 接入信息 -->
        <el-col v-show="activeIndex == '2'" :span="20" style="padding: 10px">
          <el-form ref="ruleForm" :model="ruleForm" :rules="rules" label-width="100px" class="demo-ruleForm">
            <el-form-item label="接入名称" prop="name">
              <el-input v-model.trim="ruleForm.name" placeholder="请输入字段名称" :disabled="disable" />
            </el-form-item>
            <el-form-item label="事件源类型" prop="sourceType">
              <el-select v-model="ruleForm.sourceType" :disabled="disable" placeholder="请选择事件源类型" @change="changeSourceType">
                <el-option v-for="item in eventSourceTypeSelect" :key="item.dictLabel" :label="item.dictLabel" :value="item.dictValue" />
              </el-select>
            </el-form-item>
            <el-row>
              <el-col :span="16">
                <el-form-item label="接入Key" prop="probeKey">
                  <el-input v-model.trim="ruleForm.probeKey" :disabled="disable" placeholder="请输入接入Key" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-button class="addButton" size="mini" style="margin-top: 3px; margin-left: 8px" @click="setProbeKey">生成</el-button>
              </el-col>
            </el-row>
            <el-form-item v-show="syslogShow" label="Syslog类型" prop="syslogType">
              <el-radio-group v-model="ruleForm.columnMapping1.syslogType" :disabled="disable">
                <el-radio label="Y">标准</el-radio>
                <el-radio label="N">非标准</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-show="ruleForm.columnMapping1.syslogType === 'N' && syslogShow" label="来源标识" prop="syslogSource">
              <el-input v-model.trim="ruleForm.columnMapping1.syslogSource" :disabled="disable" placeholder="请输入非标Syslog来源标识" />
            </el-form-item>
            <!--  syslogSeparatorCharShow -->
            <el-form-item v-show="ruleForm.columnMapping1.syslogType === 'N' && syslogShow" label="非标Syslog分割符" prop="syslogType">
              <el-input v-model.trim="ruleForm.columnMapping1.syslogSeparatorChar" :disabled="disable" placeholder="请输入非标Syslog分隔符" />
            </el-form-item>
            <el-form-item label="启用" prop="isEnable">
              <el-switch v-model="ruleForm.isEnable" :disabled="disable" />
            </el-form-item>
            <el-form-item label="备注" prop="remark">
              <el-input v-model.trim="ruleForm.remark" type="textarea" :disabled="disable" placeholder="请输入备注" />
            </el-form-item>
          </el-form>
        </el-col>
        <el-col v-show="activeIndex == '3'" :span="20" style="padding: 10px">
          <el-table height="520" border :data="ruleForm.columnMapping1.columnMapping">
            <el-table-column prop="platformColumn" label="平台字段名称" />
            <el-table-column prop="platformDataType" :formatter="getPlatformDataType" label="平台字段类型" />
            <el-table-column label="取值类型" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-select v-model="scope.row.mappingType" :disabled="disable" placeholder="取值类型">
                  <el-option label="单一" value="one" />
                  <el-option label="拼接" value="join" />
                  <el-option label="表达式" value="expression" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="来源类型" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-select v-model="scope.row.sourceDataType" clearable="" placeholder="选择类型" :disabled="disable">
                  <el-option value="STRING" label="字符串" />
                  <el-option value="INT" label="整型" />
                  <el-option value="FLOAT" label="非整型数字" />
                  <el-option value="LONGTIMESTAMP" label="毫秒时间戳" />
                  <el-option value="UNIXTIMESTAMP" label="UNIX时间戳" />
                  <el-option value="DATE" label="日期" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="源字段名称" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-input v-model.trim="scope.row.sourceColumn" :disabled="disable" placeholder="映射字段名称" :value="scope.row.sourceColumn" />
              </template>
            </el-table-column>
            <el-table-column label="默认值(字段不存在)" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-input v-model.trim="scope.row.sourceValue" :disabled="disable" placeholder="映射默认值" :value="scope.row.sourceValue" />
              </template>
            </el-table-column>
          </el-table>
        </el-col>
        <!-- 级别映射 -->
        <el-col v-show="activeIndex == '4'" :span="20" style="padding: 10px">
          <el-row>
            <el-input v-model.trim="ruleForm.severityMapping1.eventSourceSeverityColumn" :disabled="disable" placeholder="请输入级别字段名称" />
          </el-row>
          <el-row>
            <el-table height="520" border :data="ruleForm.severityMapping1.valueMapping">
              <el-table-column prop="platformSeverity" label="平台事件告警级别" />
              <el-table-column label="操作符" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                  <el-select v-model="scope.row.operator" :disabled="disable" :value="scope.row.operator">
                    <el-option label="=" value="=" />
                    <el-option label="IN" value="in" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="源级别" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                  <el-input v-model.trim="scope.row.sourceSeverity" :disabled="disable" controls-position="right" />
                </template>
              </el-table-column>
            </el-table>
          </el-row>
        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button v-if="!disable" class="cancel_button" @click="resetForm('ruleForm')">取 消</el-button>
        <el-button v-else class="cancel_button" @click="resetForm('ruleForm')">关 闭</el-button>
        <el-button v-show="!disable" type="primary" class="confirm_button" @click="submitForm('ruleForm')">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getEventProbeDefine, addEventProbeDefine, delEventProbeDefine, getEventProbeDefineById, updateEventProbeDefine, getEventColumnDefineMapping, getEventSeverityDefineMapping, getEventSourceTypeSelect } from '@/api/cm/eventAccess'
import { v1 as uuidv1 } from 'uuid'
// 省缺组件
import Deficiency from '@/components/Deficiency/index'
export default {
  components: { Deficiency },
  data() {
    return {
      // 字段映射
      eventField: [],
      eventLevel: [
        {
          name: '严重',
          characte: '=',
          IsRequired: true,
          zabbixName: 0
        },
        {
          name: '一般',
          characte: '=',
          IsRequired: true,
          zabbixName: 0
        },
        {
          name: '中等',
          characte: '=',
          IsRequired: true,
          zabbixName: 0
        },
        {
          name: '发生时间2',
          characte: '=',
          IsRequired: true,
          zabbixName: 0
        }
      ],
      activeIndex: '2',
      // 获取表格页面高度
      OperateBoxHeight: 400,
      // 弹出弹窗title显示
      title: '新增',
      content: '',
      // 遮罩层
      loading: true,
      disable: false,
      syslogShow: false,
      syslogSeparatorCharShow: false,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 总条数
      total: 0,
      totalCount: 0,
      queryParams: {
        start: 1,
        limit: 20,
        projectId: 10
      },
      formInline: {
        name: '',
        source_type: ''
      },
      // 新增、修改弹框
      open: false,
      // 列表id隐藏
      show: false,
      dataList: [],
      saveOrUpdate: 'save',
      ruleForm: {
        name: '',
        sourceType: 'RESTAPI',
        probeKey: '',
        isEnable: true,
        columnMapping1: {
          syslogType: 'Y',
          syslogSeparatorChar: '',
          syslogSource: '',
          columnMapping: []
        },
        columnMapping: '',
        severityMapping: '',
        severityMapping1: {
          eventSourceSeverityColumn: 'Severity',
          valueMapping: []
        },
        remark: ''
      },
      // probeKey#sourceType#name
      rules: {
        name: [{ required: true, message: '请输入接入名称', trigger: 'blur' }],
        sourceType: [{ required: true, message: '请选择类型', trigger: 'change' }],
        probeKey: [{ required: true, message: '请输入ProbeKey', trigger: 'blur' }]
      },
      eventSourceTypeSelect: []
    }
  },
  watch: {
    // 'ruleForm.severityMapping1': function(newValue, oldValue) {
    //   console.log(newValue, oldValue)
    // },
    // 'ruleForm.columnMapping1': function(newValue, oldValue) {
    //   console.log(newValue, oldValue)
    // },
    // ruleForm.sourceType
    // 'ruleForm.sourceType': function(newValue, oldValue) {
    //   // console.log(newValue, oldValue)
    //   if (newValue === 'SYSLOG') {
    //     this.ruleForm.syslogType = 'Y'
    //     this.ruleForm.columnMapping1.syslogType = 'Y'
    //     this.ruleForm.columnMapping1.syslogSource = ''
    //     this.ruleForm.columnMapping1.syslogSeparatorChar = ''
    //     this.syslogShow = true
    //   } else {
    //     this.syslogShow = false
    //   }
    //   this.syslogSeparatorCharShow = false
    // },
    'ruleForm.columnMapping1.syslogType': function(newValue, oldValue) {
      if (newValue === 'Y') {
        this.syslogSeparatorCharShow = false
        this.ruleForm.columnMapping1.syslogSource = ''
        this.ruleForm.columnMapping1.syslogSeparatorChar = ''
      } else {
        this.syslogSeparatorCharShow = true
        // this.ruleForm.columnMapping1.syslogSource = ''
        // this.ruleForm.columnMapping1.syslogSeparatorChar = ''
      }
    }
  },

  created() {
    this.queryParams.projectId = localStorage.getItem('project_id')
    this.getEventProbeDefines()
    this.getEventSourceTypeSelect()
  },
  updated() {
    this.getOperateBoxHeight()
  },
  methods: {
    changeSourceType() {
      if (this.ruleForm.sourceType === 'SYSLOG') {
        this.ruleForm.syslogType = 'Y'
        this.ruleForm.columnMapping1.syslogType = 'Y'
        this.ruleForm.columnMapping1.syslogSource = ''
        this.ruleForm.columnMapping1.syslogSeparatorChar = ''
        this.syslogShow = true
      } else {
        this.syslogShow = false
      }
      this.syslogSeparatorCharShow = false
    },
    getEventSourceTypeSelect() {
      getEventSourceTypeSelect({ projectId: this.queryParams.projectId }).then((res) => {
        if (res.success) {
          this.eventSourceTypeSelect = res.datas
        }
      })
    },
    setProbeKey() {
      this.ruleForm.probeKey = uuidv1()
    },
    getPlatformDataType(row) {
      if (row.platformDataType === 'longtimestamp' || row.platformDataType === 'LONGTIMESTAMP') {
        return '时间戳'
      } else if (row.platformDataType === 'string' || row.platformDataType === 'STRING') {
        return '字符串'
      } else if (row.platformDataType === 'int' || row.platformDataType === 'INT') {
        return '整型'
      } else if (row.platformDataType === 'float' || row.platformDataType === 'FLOAT') {
        return '非整型数字'
      }
    },
    // 接入信息数据加载
    getEventProbeDefines() {
      this.loading = true
      getEventProbeDefine({
        sourceType: this.formInline.source_type,
        name: this.formInline.name,
        projectId: this.queryParams.projectId,
        start: this.queryParams.start,
        limit: this.queryParams.limit
      }).then((response) => {
        this.loading = false
        this.dataList = response.datas

        this.totalCount = response.totalCount
      })
    },
    // 获取页面高度
    getOperateBoxHeight() {
      this.$nextTick(function() {
        this.OperateBoxHeight = document.getElementsByClassName('app-container')[0].offsetHeight - document.getElementsByClassName('operateBox')[0].offsetHeight - 10
      })
    },
    IsActive(value) {
      this.activeIndex = value
    },
    // 查询方法
    onSearch() {
      this.getEventProbeDefines()
    },
    // 重置方法
    onRest() {
      this.formInline = {
        name: '',
        source_type: ''
      }
      this.onSearch()
    },
    // 添加方法
    onAdd() {
      this.title = '新增'
      this.saveOrUpdate = 'save'
      this.syslogShow = false
      this.open = true
      this.getEventColumnDefineMappings()
      this.getEventSeverityDefineMappings()
      // this.getEventField();
      this.setProbeKey()
    },
    // 获取字段映射
    getEventColumnDefineMappings() {
      getEventColumnDefineMapping({ projectId: this.queryParams.projectId }).then((response) => {
        if (response.success) {
          // this.ruleForm.columnMapping1.columnMapping = response.datas
          this.$set(this.ruleForm.columnMapping1, 'columnMapping', response.datas)
        } else {
          this.msgError('查询平台字段失败：\n' + response.errorMsg)
        }
      })
    },
    // 获取级别映射
    getEventSeverityDefineMappings() {
      getEventSeverityDefineMapping({ projectId: this.queryParams.projectId }).then((response) => {
        if (response.success) {
          this.ruleForm.severityMapping1.valueMapping = response.datas
        } else {
          this.msgError('查询平台级别失败：\n' + response.errorMsg)
        }
      })
    },
    submitForm() {
      this.$refs['ruleForm'].validate((valid) => {
        if (valid) {
          if (this.saveOrUpdate === 'save') {
            this.ruleForm.projectId = this.queryParams.projectId
            if (this.ruleForm.isEnable === true) {
              this.ruleForm.isEnable = 'Y'
            } else {
              this.ruleForm.isEnable = 'N'
            }
            this.ruleForm.severityMapping = JSON.stringify(this.ruleForm.severityMapping1)
            for (var i = 0; i < this.ruleForm.columnMapping1.columnMapping.length; i++) {
              if (this.ruleForm.columnMapping1.columnMapping[i].sourceValue != '' && this.ruleForm.columnMapping1.columnMapping[i].sourceValue != null) {
                this.ruleForm.columnMapping1.columnMapping[i].sourceColumn = ''
              }
            }
            this.ruleForm.columnMapping = JSON.stringify(this.ruleForm.columnMapping1)
            this.ruleForm.severityMapping1 = {}
            this.ruleForm.columnMapping1 = {}
            addEventProbeDefine(this.ruleForm).then((response) => {
              if (response.success) {
                this.open = false
                this.msgSuccess('保存成功')
                this.getEventProbeDefines()
                this.resetForm('ruleForm')
              } else {
                this.msgError('保存失败：\n' + response.errorMsg)
              }
            })
          } else {
            // this.ruleForm.projectId = this.queryParams.projectId;
            if (this.ruleForm.isEnable === true) {
              this.ruleForm.isEnable = 'Y'
            } else {
              this.ruleForm.isEnable = 'N'
            }
            this.ruleForm.severityMapping = JSON.stringify(this.ruleForm.severityMapping1)
            for (var i = 0; i < this.ruleForm.columnMapping1.columnMapping.length; i++) {
              if (this.ruleForm.columnMapping1.columnMapping[i].sourceValue !== '' && this.ruleForm.columnMapping1.columnMapping[i].sourceValue != null) {
                this.ruleForm.columnMapping1.columnMapping[i].sourceColumn = ''
              }
            }
            this.ruleForm.columnMapping = JSON.stringify(this.ruleForm.columnMapping1)
            this.ruleForm.severityMapping1 = {}
            this.ruleForm.columnMapping1 = {}
            updateEventProbeDefine(this.ruleForm).then((response) => {
              if (response.success) {
                this.open = false
                this.msgSuccess('修改成功')
                this.getEventProbeDefines()
                this.resetForm('ruleForm')
              } else {
                this.msgError('修改失败：\n' + response.errorMsg)
              }
            })
          }
        }
      })
    },
    closeHander() {
      this.resetForm('ruleForm')
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
      this.open = false
      this.disable = false
      this.eventField = []
      this.activeIndex = '2'
      this.ruleForm = {
        name: '',
        sourceType: 'ZABBIX',
        probeKey: '',
        isEnable: true,
        syslogType: 'Y',
        syslogSeparatorChar: '',
        columnMapping: '',
        columnMapping1: {
          syslogType: 'Y',
          syslogSeparatorChar: '',
          syslogSource: '',
          columnMapping: []
        },
        severityMapping: '',
        severityMapping1: { eventSourceSeverityColumn: 'Severity', valueMapping: [] },
        remark: ''
      }
    },
    // 删除方法
    handleDelete(row) {
      this.$confirm('是否确认删除该项数据', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delEventProbeDefine({ id: row.id, projectId: this.queryParams.projectId }).then((response) => {
          this.queryParams.start = 1
          this.msgSuccess('删除成功')
          this.getEventProbeDefines()
        })
      })
    },
    // 查看方法
    handleWatch(row) {
      this.disable = true
      this.title = '查看'
      getEventProbeDefineById({ id: row.id, projectId: this.queryParams.projectId }).then((response) => {
        if (response['success']) {
          this.ruleForm = response.mapData
          // this.ruleForm.columnMapping1.syslogType=''
          this.ruleForm.columnMapping1 = JSON.parse(response.mapData.columnMapping)
          this.ruleForm.severityMapping1 = JSON.parse(response.mapData.severityMapping)
          this.$set(this.ruleForm.columnMapping1, 'syslogType', JSON.parse(response.mapData.columnMapping).syslogType)
          if (this.ruleForm.sourceType === 'SYSLOG') {
            this.syslogShow = true
          } else {
            this.syslogShow = false
          }
          this.open = true
          // console.log(this.ruleForm)
        }
      })
    },
    // 修改方法
    handleUpdate(row) {
      this.disable = false
      this.saveOrUpdate = 'update'
      this.title = '修改'
      // this.getEventColumnDefineMappings()
      getEventColumnDefineMapping({ projectId: this.queryParams.projectId }).then((response) => {
        if (response.success) {
          // this.ruleForm.columnMapping1.columnMapping = response.datas
          this.$set(this.ruleForm.columnMapping1, 'columnMapping', response.datas)
          getEventProbeDefineById({ id: row.id, projectId: this.queryParams.projectId }).then((response) => {
            if (response['success']) {
              const oldColumn = this.ruleForm.columnMapping1.columnMapping
              this.ruleForm = response.mapData
              if (this.ruleForm.sourceType === 'SYSLOG') {
                this.syslogShow = true
              } else {
                this.syslogShow = false
              }
              this.ruleForm.columnMapping1 = JSON.parse(response.mapData.columnMapping)
              oldColumn.forEach((item, index) => {
                this.ruleForm.columnMapping1.columnMapping.forEach((item1, index1) => {
                  if (item1.platformColumn === item.platformColumn) {
                    this.$set(oldColumn, index, item1)
                  }
                  if (index === oldColumn.length - 1 && index1 === this.ruleForm.columnMapping1.columnMapping.length - 1) {
                    // this.ruleForm.columnMapping1.columnMapping = oldColumn
                    // ruleForm.columnMapping1.columnMapping
                    this.$set(this.ruleForm.columnMapping1, 'columnMapping', oldColumn)
                  }
                })
              })
              this.ruleForm.severityMapping1 = JSON.parse(response.mapData.severityMapping)
              this.ruleForm = JSON.parse(JSON.stringify(this.ruleForm))
              this.open = true
            }
          })
        } else {
          this.msgError('查询平台字段失败：\n' + response.errorMsg)
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@media screen and (max-width: 1400px) {
  .el-col-4 {
    width: auto;
  }
}

.dark,
.light {
  .menuDialog /deep/.el-dialog__body {
    height: 600px !important;
    padding: 0 !important;
  }
  .el-menu {
    border-right: solid 1px rgba(66, 170, 211, 0.1) !important;
    .el-menu-item:hover,
    .el-menu-item:focus {
      // background:rgb(85, 136, 237)!important;
      background: rgba(85, 136, 237, 0.1) !important;
    }
  }
}
</style>
