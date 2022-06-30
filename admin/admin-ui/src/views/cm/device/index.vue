<template>
  <div v-loading="uploadExcelLoading" class="app-container" element-loading-text="正在上传中...">
    <div ref="OperateBox" class="operateBox">
      <el-row>
        <el-col :span="24">
          <el-form ref="queryParams" :model="queryParams" :inline="true" label-position="left">
            <el-form-item label="设备类别">
              <!-- <el-select v-model="queryParams.classId" placeholder="请选择设备类别" clearable filterable @change="handleQuery">
                <el-option v-for="item in deviceCategories" :key="item.id" :label="item.labelText" :value="item.id" />
              </el-select> -->
              <el-select ref="selectUpResId" v-model="queryParams.classId" clearable size="small" placeholder="请选择" @focus="filterText = ''">
                <el-option hidden :value="queryParams.classId" :label="classIdName" />
                <el-input v-model.trim="filterText" placeholder="输入关键字进行过滤" size="mini" style="margin:5px;margin-bottom:0;width:95%" />
                <el-tree ref="tree" :filter-node-method="filterNode" :data="deviceCategories1" default-expand-all clearable @node-click="handleNodeClick" />
              </el-select>
            </el-form-item>
            <el-form-item label="设备名称">
              <el-input v-model.trim="queryParams.name" placeholder="请输入设备名称" clearable size="small" @keyup.enter.native="handleQuery" />
            </el-form-item>
            <!-- <el-form-item label="机柜名称">
              <el-select v-model="queryParams.gname" placeholder="请选择机柜名称" clearable filterable size="small">
                <el-option v-for="item in resourceAttributeGroup" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item> -->
            <el-form-item label="厂商/产品" prop="vendor">
              <el-select v-model="queryParams.vendor" placeholder="请选择" filterable clearable size="small" @change="handleQuery">
                <el-option v-for="item in vendors" :key="item.name" :label="item.labelText" :value="item.name" />
              </el-select>
            </el-form-item>
            <el-form-item label="添加时间">
              <el-date-picker v-model="queryParams.FirstOccurrence" clearable size="small" type="datetimerange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="timestamp" @change="handleQuery" />
            </el-form-item>
            <el-form-item label="启用" prop="isEnable">
              <el-select v-model="queryParams.isEnable" placeholder="请选择" clearable size="small" @change="handleQuery">
                <el-option v-for="dict in typeOptions" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery()">查询</el-button>
              <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
              <el-button class="addButton" size="mini" @click="exportSelected">导出</el-button>
              <!-- <el-button class="addButton" size="mini" @click="importDeviceTemplate">下载模板</el-button>
              <el-upload action="/xyz_b/event/importDevice" :headers="headersToken" :on-success="handleAvatarSuccess" :on-progress="onProgress" :file-list="fileList" :show-file-list="false" style="display: inline-block;margin:0 10px;">
                <el-button size="small" class="addButton">导入</el-button>
              </el-upload> -->
              <el-button class="addButton" size="mini" :disabled="!idSelections.length" @click="dialogFormVisible = true;startOrStop = 'Y'">批量启停</el-button>
              <el-button class="addButton" size="mini" @click="handleAdd">新增</el-button>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
    </div>
    <div class="showTableBox" :style="{ height: OperateBoxHeight + 'px' }">
      <el-table v-if="dataList.length" v-loading="loading" :data="dataList" border :height="'90%'" @selection-change="handleSelectionChange">
        <el-table-column type="selection" align="center" min-width="5%" :show-overflow-tooltip="true" />
        <el-table-column align="left" label="设备类别" prop="className" min-width="7.5%" :show-overflow-tooltip="true" />
        <el-table-column align="left" label="设备名称" prop="name" min-width="10%" :show-overflow-tooltip="true" />
        <el-table-column align="left" label="显示名称" prop="labelText" min-width="15%" :show-overflow-tooltip="true" />
        <el-table-column align="left" label="设备标识" prop="key_" min-width="10%" :show-overflow-tooltip="true">
          <!-- <template slot-scope="scope">
            <span v-if="scope.row.classId !== 283">
              <el-tooltip class="item" effect="dark" content="点击跳转" placement="top">
                <span class="tagLink" @click="turnPage(scope.row)">
                  <i class="el-icon-paperclip" />
                  {{ scope.row.key_ }}
                </span>
              </el-tooltip>
            </span>
            <span v-else>
              {{ scope.row.key_ }}
            </span>
          </template> -->
        </el-table-column>
        <el-table-column label="IP地址" prop="ipAddress" min-width="10%" :show-overflow-tooltip="true" />
        <!-- <el-table-column align="center" label="关联设备" prop="controlIp" :show-overflow-tooltip="true" width="150" /> -->
        <!-- <el-table-column align="left" label="负责人" prop="owner" :show-overflow-tooltip="true" /> -->
        <!-- <el-table-column align="center" label="位置" prop="area" :show-overflow-tooltip="true" /> -->
        <el-table-column label="厂商/产品" prop="vendor" min-width="7.5%" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <div>
              <div v-for="item in vendors" :key="item.name">
                <span v-if="scope.row.vendor == item.name">{{ item.labelText }}</span>
              </div>
              <span v-if="!scope.row.vendor">-</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="添加时间" prop="create_timestamp" min-width="10%" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            {{ scope.row.create_timestamp | getDate }}
          </template>
        </el-table-column>
        <el-table-column align="center" label="启用" prop="isEnable" min-width="10%" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isEnable == 'N'" size="small" type="danger">不启用</el-tag>
            <el-tag v-else size="small">启用</el-tag>
          </template>
        </el-table-column>
        <!-- <el-table-column align="center" label="备注" prop="remark" :show-overflow-tooltip="true" /> -->
        <el-table-column label="操作" align="center" min-width="15%" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改</el-button>
            <!-- <el-button size="mini" type="text" icon="el-icon-edit" @click="handleAtrrUpdate(scope.row)">属性维护</el-button> -->
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <Deficiency v-else width="25%" height="auto" />
      <pagination v-show="totalCount > 0" :page-sizes="[10, 20, 50, 200]" :total="totalCount" :page.sync="queryParams.start" :limit.sync="queryParams.limit" @pagination="getList()" />
    </div>
    <!-- 添加或修改菜单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="50%">
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col>
            <el-form-item label="设备类别" prop="classId">
              <!-- <el-select v-model="form.classId" placeholder="请选择" filterable>
            <el-option v-for="item in deviceCategories" :key="item.id" :label="item.labelText" :value="item.id" />
          </el-select> -->
              <el-select ref="selectUpResIdform" v-model="form.classId" clearable size="small" placeholder="请选择" @focus="filterText1 = ''">
                <el-option hidden :value="form.classId" :label="classIdName1" />
                <el-input v-model="filterText1" placeholder="输入关键字进行过滤" size="mini" style="margin:5px;margin-bottom:0;width:95%" />
                <el-tree ref="treeForm" :filter-node-method="filterNode" :data="deviceCategories1" default-expand-all @node-click="findGroupByClassId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col>
            <el-form-item label="设备名称" prop="name">
              <el-input v-model.trim="form.name" placeholder="请输入设备名称" clearable />
            </el-form-item>
          </el-col>
          <el-col>
            <el-form-item label="显示名称" prop="labelText">
              <el-input v-model.trim="form.labelText" placeholder="显示名称" clearable />
            </el-form-item>
          </el-col>
          <el-col>
            <el-form-item label="设备标识" prop="key_">
              <el-input v-model.trim="form.key_" placeholder="设备标识" clearable @blur="getHost" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="IP地址" prop="ipAddress">
              <el-input v-model.trim="form.ipAddress" placeholder="IP地址" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联设备" prop="controlIp">
              <el-input v-model.trim="form.controlIp" placeholder="关联设备" clearable />
            </el-form-item>
          </el-col>
          <el-col>
            <el-form-item label="负责人" prop="owner">
              <el-input v-model.trim="form.owner" placeholder="负责人" clearable />
            </el-form-item>
          </el-col>
          <el-col>
            <el-form-item label="厂商/产品" prop="vendor">
              <el-select v-model="form.vendor" placeholder="请选择" filterable clearable>
                <el-option v-for="item in vendors" :key="item.name" :label="item.labelText" :value="item.name" />
              </el-select>
            </el-form-item>
          </el-col>
          <!-- <el-col>
            <el-form-item label="模板" prop="templateid">
              <el-select v-model="form.templateid" placeholder="请选择" filterable multiple clearable>
                <el-option v-for="template in templates" :key="template.hostid" :label="template.host" :value="template.hostid" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col>
            <el-form-item label="群组" prop="groupid">
              <el-select v-model="form.groupid" placeholder="请选择" filterable multiple clearable>
                <el-option v-for="group in hostGroups" :key="group.groupid" :label="group.name" :value="group.groupid" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col>
            <el-form-item label="Zabbix Proxy" prop="proxyid">
              <el-select v-model="form.proxyid" placeholder="请选择" filterable clearable>
                <el-option v-for="proxyAgent in proxyAgents" :key="proxyAgent.hostid" :label="proxyAgent.host" :value="proxyAgent.hostid" />
              </el-select>
            </el-form-item>
          </el-col> -->
          <el-col>
            <!--<el-form-item label="接口" prop="vendor">
          <el-select v-model="form.vendor" placeholder="请选择" filterable>
            <el-option v-for="item in vendors" :key="item.name" :label="item.labelText" :value="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="宏" prop="vendor">
          <el-select v-model="form.vendor" placeholder="请选择" filterable>
            <el-option v-for="item in vendors" :key="item.name" :label="item.labelText" :value="item.name" />
          </el-select>
        </el-form-item>-->
            <el-form-item label="设备位置" prop="area">
              <el-input v-model.trim="form.area" placeholder="设备位置" clearable />
            </el-form-item>
          </el-col>
          <el-col>
            <el-form-item label="启用" prop="isEnable">
              <el-radio-group v-model="form.isEnable">
                <el-radio v-for="dict in typeOptions" :key="dict.dictValue" :label="dict.dictValue">{{ dict.dictLabel }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col>
            <el-form-item label="备注" clearable prop="remark">
              <el-input v-model.trim="form.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button class="cancel_button" @click="cancel">取 消</el-button>
        <el-button type="primary" class="confirm_button" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 批量启停 -->
    <el-dialog title="批量启停" :visible.sync="dialogFormVisible">
      <el-form label-width="37%">
        <el-form-item label="启用" prop="isEnable" style="width:100%">
          <el-radio-group v-model="startOrStop">
            <el-radio v-for="dict in typeOptions" :key="dict.dictValue" :label="dict.dictValue">{{ dict.dictLabel }}</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleStartOrStop">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getResourceList, addResource, getResourceById, updateResource, delResourceById/* , getZabbixHostGroups, getZabbixHost, getHostRelation, startMonitor, stopMonitor  */} from '@/api/cm/device.js'
import { getPhysicalCategories } from '@/api/cm/physicalCategory'
import { listVendor } from '@/api/cm/vendor'
import { getToken } from '@/utils/auth'

// 省缺组件
import Deficiency from '@/components/Deficiency/index'
import { exportDevice, importDeviceTemplate, startOrStopDevice } from '@/api/cm/device'
import { isTurnHost } from '@/api/operation/alarmOverview'
export default {
  name: 'Device',
  components: { Deficiency },
  data() {
    return {
      startOrStop: 'Y',
      dialogFormVisible: false, // 是否打开批量启用弹窗
      // 上传蒙层
      uploadExcelLoading: false,
      filterText: '',
      filterText1: '',
      // 页面高度
      classIdName: '',
      classIdName1: '',
      fileList: [],
      OperateBoxHeight: 100,
      show: false,
      totalCount: 0,
      open: false,
      form: {},
      vendors: [],
      title: '新增',
      dataList: [],
      queryParams: {
        start: 1,
        limit: 20,
        vendor: '',
        name: '',
        FirstOccurrence: '',
        classId: null,
        isEnable: 'Y',
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
        classId: [{ required: true, message: '请选择设备类别', trigger: 'blur' }],
        name: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
        key_: [
          {
            required: true,
            message: '请输入设备标识，唯一性标识',
            trigger: 'blur'
          }
        ],
        ipAddress: [{ required: true, message: '请输入IP地址', trigger: 'blur' }],
        isEnable: [{ required: true, message: '请选择是否启用', trigger: 'blur' }],
        state: [{ required: true, message: '请选择状态', trigger: 'blur' }],
        /* vendor: [{ required: true, message: '请选择厂商/产品', trigger: 'blur' }], */
        groupid: [{ required: true, message: '请选择设备分组', trigger: 'blur' }]
      },
      deviceCategories: [],
      deviceCategories1: [],
      loading: false,
      hostGroups: [], // 组
      templates: [], // 模板
      proxyAgents: [], // 代理agent
      projectId: 10,
      idSelections: []
    }
  },
  computed: {
    headersToken() {
      return { Authorization: 'Bearer ' + getToken() }
    }
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val)
    },
    filterText1(val) {
      this.$refs.treeForm.filter(val)
    },
    'form.classId': function(newValue, oldValue) {
      if (!this.form.classId) {
        this.classIdName1 = ''
      }
      this.findGroupByClassId()
    },
    'queryParams.classId': function(newValue, oldValue) {
      if (!this.queryParams.classId) {
        this.classIdName = ''
        this.handleQuery()
      }
      // this.findGroupByClassId()
    }
  },
  created() {
    if (this.$route.params.name) {
      this.queryParams = {
        start: 1,
        limit: 20,
        name: '',
        vendor: '',
        FirstOccurrence: '',
        classIdName: '',
        classId: null,
        isEnable: 'Y',
        projectId: 10,
        engine: 'Freemarker'
      }
      this.queryParams.name = this.$route.params.name
    } else if (this.$route.params.class_id) {
      this.queryParams = {
        start: 1,
        limit: 20,
        name: '',
        vendor: '',
        FirstOccurrence: '',
        classIdName: '',
        classId: null,
        isEnable: 'Y',
        projectId: 10,
        engine: 'Freemarker'
      }
      this.queryParams.classId = this.$route.params.class_id
      this.$set(this.queryParams, 'classId', this.$route.params.class_id)
      // this.classIdName = this.queryParams.classIdName
      this.handleNodeClick1()
    } else if (localStorage.getItem('deviceParams')) {
      this.queryParams = JSON.parse(localStorage.getItem('deviceParams'))
      this.classIdName = this.queryParams.classIdName
    }
    this.queryParams.projectId = localStorage.getItem('project_id')
    this.projectId = localStorage.getItem('project_id')
    this.getPhysicalCategory()
    this.getVendors()
    this.getList()
    this.getZabbixInfo() // 获取zabbix相关数据
  },
  updated() {
    this.getOperateBoxHeight()
  },
  mounted() {
    this.getOperateBoxHeight()
  },
  activated() {
    if (this.$route.params.name) {
      this.queryParams = {
        start: 1,
        limit: 20,
        FirstOccurrence: '',
        vendor: '',
        name: '',
        classIdName: '',
        classId: null,
        isEnable: 'Y',
        projectId: 10,
        engine: 'Freemarker'
      }
      this.queryParams.name = this.$route.params.name
      // this.$router.push({ query: {}})
    } else if (this.$route.params.class_id) {
      this.queryParams = {
        start: 1,
        limit: 20,
        name: '',
        FirstOccurrence: '',
        vendor: '',
        classIdName: '',
        classId: null,
        isEnable: 'Y',
        projectId: 10,
        engine: 'Freemarker'
      }
      // this.queryParams.classId = this.$route.params.class_id
      this.$set(this.queryParams, 'classId', this.$route.params.class_id)
      // this.classIdName = this.queryParams.classIdName
      this.handleNodeClick1()
    } else if (localStorage.getItem('deviceParams')) {
      this.queryParams = JSON.parse(localStorage.getItem('deviceParams'))
      this.classIdName = this.queryParams.classIdName
    }
    this.getPhysicalCategory()
    this.getVendors()
    this.getList()
    this.getZabbixInfo() // 获取zabbix相关数据
  },
  // deactivated() {
  //   this.classIdName = ''
  //   localStorage.setItem('deviceParams', JSON.stringify({
  //     start: 1,
  //     limit: 20,
  //     name: '',
  //     classIdName: '',
  //     classId: null,
  //     isEnable: 'Y',
  //     projectId: 10,
  //     engine: 'Freemarker'
  //   }))
  // },
  methods: {
    // 批量启停
    handleStartOrStop() {
      /*this.idSelections.forEach((item, index) => {
         getResourceById({
          projectId: 10,
          engine: 'Freemarker',
          id: item
        }).then((response) => {
          var tempOBJ = response.mapData
          const { key_ } = response.mapData
          if (key_) {
            getHostRelation({ projectId: this.projectId, host: key_ }).then((response) => {
              if (response.success) {
                const { hostid, templateid, groupid } = response.mapData
                if (templateid) {
                  const templateIdArr = templateid.split(',').map((data) => {
                    return +data
                  })
                  this.$set(tempOBJ, 'templateid', templateIdArr)
                }
                if (groupid) {
                  const groupIdArr = groupid.split(',').map((data) => {
                    return +data
                  })
                  this.$set(tempOBJ, 'groupid', groupIdArr)
                }
                if (hostid) {
                  this.$set(tempOBJ, 'proxyid', +hostid)
                }
                // console.log(tempOBJ)
                this.$set(tempOBJ, 'isEnable', this.startOrStop)
                startMonitor(tempOBJ).then((tempRes) => {
                  if (tempRes.code === 200) {
                    // this.msgSuccess('ZABBIX设备修改成功，即将修改门户设备')
                  } else {
                    this.$message({
                      showClose: true,
                      duration: 0,
                      message: '设备修改失败，' + tempRes.errorMsg,
                      type: 'error'
                    })
                    return
                  }
                  if (this.idSelections.length - 1 === index) {
                    this.msgSuccess('ZABBIX设备修改成功，即将修改门户设备') */
                    // 修改zabbix上的启用状态
                    startOrStopDevice({
                      projectId: this.queryParams.projectId,
                      engine: 'Freemarker',
                      ids: this.idSelections,
                      is_enable: this.startOrStop
                    }).then((response) => {
                      // console.log(response)
                      if (response.success) {
                        this.msgSuccess('门户设备修改成功')
                      } else {
                        this.$message({
                          showClose: true,
                          duration: 0,
                          message: '门户设备修改失败，' + response.errorMsg,
                          type: 'error'
                        })
                      }
                      this.dialogFormVisible = false
                      this.handleQuery()
                    });
                  //}
                /* })
              }
            })
          }
        })
      }) */
    },
    filterNode(value, data) {
      if (!value) return true
      const stringTemp = data.label.toLowerCase()
      const stringTemp1 = value.toLowerCase()
      return stringTemp.indexOf(stringTemp1) !== -1
    },
    handleSelectionChange(selection) {
      this.idSelections = selection.map((item) => item.id)
    },
    turnPage(row) {
      const userStr = localStorage.getItem('user')
      const userObj = JSON.parse(userStr)
      const userId = userObj['userId']
      const roleId = userObj['roleId']
      // console.log(row)
      isTurnHost({
        projectId: 10,
        host: row.key_,
        userId,
        roleId,
        engine: 'Freemarker'
      }).then((response) => {
        if (response.datas[0].isTurn || !roleId) {
          const obj = {
            host: row.key_,
            res_vendor: row.vendor,
            clabel_text: row.className,
            label_text: row.labelText,
            classId: row.classId,
            resourceId: row.id,
            res_ip_address: row.ipAddress,
            res_control_ip: row.controlIp
          }
          const query = { name: obj }
          // console.log(query)
          let tempPath = this.toRouter
          // 服务器
          switch (query.name.classId) {
            case 28:
              tempPath = 'windows'
              break
            case 29:
              tempPath = 'Linux'
              break
            case 253:
              tempPath = '负载均衡'
              break
            case 249:
              tempPath = '路由器'
              break
            case 55:
              tempPath = '防火墙'
              break
            case 252:
              // tempPath = '交换机-堆叠'
              tempPath = '交换机'
              break
            case 255:
              tempPath = '光纤交换机'
              break
            case 315:
              tempPath = '博科光纤交换机'
              break
            case 57:
              tempPath = '数据库-MySQL'
              break
            case 31:
              tempPath = '数据库-Oracle'
              break
            case 33:
              tempPath = '数据库-SQL Server'
              break
            case 265:
              tempPath = '虚拟化-vc'
              break
            case 259:
              tempPath = '中间件-IIS'
              break
            case 280:
              tempPath = '华为硬件'
              break
            case 281:
              tempPath = '浪潮硬件'
              break
            case 282:
              tempPath = '惠普硬件'
              break
            case 267:
              tempPath = '存储-NetApp'
              break
            case 269:
              tempPath = '存储-华为'
              break
            case 279:
              tempPath = '存储-惠普'
              break
            // case 278:
            //   tempPath = '存储-日立'
            //   break
            case 289:
              tempPath = '上网行为管理'
              break
            case 285:
              tempPath = '无线控制器'
              break
            case 36:
              tempPath = '中间件-Tomcat'
              break
            case 58:
              tempPath = '中间件-Weblogic'
              break
            case 63:
              tempPath = '数据库-Redis'
              break
            case 319:
              tempPath = 'VPN'
              break
            default:
              tempPath = '通用设备详情页'
          }
          this.$router.push({ name: tempPath, params: query })
        } else {
          this.msgError('抱歉您没有当前页面的访问权限！')
        }
      })
    },
    /* getHost() {
      const { key_ } = this.form
      if (key_) {
        getHostRelation({ projectId: this.projectId, host: key_ }).then((response) => {
          if (response.success) {
            const mapData = response.mapData
            if (mapData != null) {
              const { hostid, templateid, groupid } = mapData
              if (templateid) {
                const templateIdArr = templateid.split(',').map((data) => {
                  return +data
                })
                this.$set(this.form, 'templateid', templateIdArr)
              }
              if (groupid) {
                const groupIdArr = groupid.split(',').map((data) => {
                  return +data
                })
                this.$set(this.form, 'groupid', groupIdArr)
              }
              if (hostid) {
                this.$set(this.form, 'proxyid', +hostid)
              }
            }
          }
        })
      }
    }, */
    findGroupByClassId(data) {
      // console.log(data)
      if (!data) return
      this.form.classId = data.id
      this.classIdName1 = data.label
      this.$refs.selectUpResIdform.blur()
    },
    handleNodeClick(data) {
      // console.log(data)
      const temp = []
      if (data.children) {
        if (data.children.length) {
          data.children.forEach((element) => {
            temp.push(element.id)
          })
        }
      }
      // 这里主要配置树形组件点击节点后，设置选择器的值；自己配置的数据，仅供参考
      this.classIdName = data.label
      this.queryParams.classIdName = data.label
      this.queryParams.classId = data.id
      this.queryParams.classIdList = temp
      // 选择器执行完成后，使其失去焦点隐藏下拉框的效果
      this.$refs.selectUpResId.blur()
      this.handleQuery()
    },
    handleNodeClick1() {
      getPhysicalCategories({
        projectId: this.queryParams.projectId,
        engine: 'Freemarker',
        isEnable: 'Y'
      }).then((response) => {
        if (response.success) {
          this.deviceCategories = response.datas
          const temp = []
          // this.deviceCategories1 = JSON.parse(JSON.stringify(this.handleTree(response.datas, 'id')).replace(new RegExp('labelText', 'g'), 'label'))
          this.deviceCategories.forEach((item, index) => {
            // console.log(item)
            if (item.id === this.queryParams.classId) {
              // 这里主要配置树形组件点击节点后，设置选择器的值；自己配置的数据，仅供参考
              this.classIdName = item.labelText
              this.queryParams.classIdName = item.labelText
              this.queryParams.classId = item.id
              // this.queryParams.classIdList = temp
            }
            if (item.parentId === this.queryParams.classId) {
              temp.push(item.id)
              this.queryParams.classIdList = temp
            }
            if (index + 1 === this.deviceCategories.length) {
              this.getList()
            }
          })
        } else {
          this.msgError('查询设备类别失败：\n' + response.errorMsg)
        }
      })
    },
    onProgress(event, file, fileList) {
      this.uploadExcelLoading = true
    },
    handleAvatarSuccess(file, fileList) {
      if (fileList.response.code !== 500) {
        this.msgSuccess('上传成功！')
      } else {
        this.$message({
          showClose: true,
          duration: 0,
          message: fileList.response.msg,
          type: 'error'
        })
      }
      this.uploadExcelLoading = false
      this.handleQuery()
    },
    importDeviceTemplate() {
      const response = importDeviceTemplate().then((response) => {
        const { code, msg } = response
        if (code !== 200) {
          this.msgInfo(msg)
        } else {
          this.download(msg)
        }
      })
    },
    exportSelected() {
      const queryParams = this.queryParams
      queryParams.ids = this.idSelections
      this.$confirm('是否确认导出所有数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(function() {
          return exportDevice(queryParams)
        })
        .then((response) => {
          const { code, msg } = response
          if (code !== 200) {
            this.msgInfo(msg)
          } else {
            this.download(msg)
          }
        })
        .catch(function() {})
    },
    getPhysicalCategory() {
      getPhysicalCategories({
        projectId: this.queryParams.projectId,
        engine: 'Freemarker',
        isEnable: 'Y'
      }).then((response) => {
        if (response.success) {
          this.deviceCategories = response.datas
          this.deviceCategories1 = JSON.parse(JSON.stringify(this.handleTree(response.datas, 'id')).replace(new RegExp('labelText', 'g'), 'label'))
        } else {
          this.msgError('查询设备类别失败：\n' + response.errorMsg)
        }
      })
    },
    getVendors() {
      listVendor({
        projectId: this.queryParams.projectId,
        engine: 'Freemarker',
        isEnable: 'Y'
      }).then((response) => {
        if (response.success) {
          this.vendors = response.datas
        } else {
          this.msgError('查询设备厂商/产品失败:\n' + response.errorMsg)
        }
      })
    },
    // 获取页面高度
    getOperateBoxHeight() {
      this.$nextTick(function() {
        this.OperateBoxHeight = document.getElementsByClassName('app-container')[0].offsetHeight - document.getElementsByClassName('operateBox')[0].offsetHeight - 10
      })
    },
    handleUpdate(row) {
      // this.reset()
      this.classIdName1 = row.className
      // this.loading = true;
      getResourceById({
        projectId: row.projectId,
        engine: 'Freemarker',
        id: row.id
      }).then((response) => {
        if (response.success) {
          this.form = response.mapData
          /* const { key_ } = response.mapData
          if (key_) {
            getHostRelation({ projectId: this.projectId, host: key_ }).then((response) => {
              if (response.success) {
                const { hostid, templateid, groupid } = response.mapData
                if (templateid) {
                  const templateIdArr = templateid.split(',').map((data) => {
                    return +data
                  })
                  this.$set(this.form, 'templateid', templateIdArr)
                }
                if (groupid) {
                  const groupIdArr = groupid.split(',').map((data) => {
                    return +data
                  })
                  this.$set(this.form, 'groupid', groupIdArr)
                }
                if (hostid) {
                  this.$set(this.form, 'proxyid', +hostid)
                }
              }
            })
          } */
        }
        this.open = true
        this.title = '修改'
      })
    },
    handleAdd() {
      this.reset()
      this.title = '新增'
      this.open = true
    },
    cancel() {
      this.reset()
      this.open = false
    },
    handleDelete(row) {
      const labelText = row.labelText
      this.$confirm('是否确认删除' + labelText + '的数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          delResourceById({ projectId: this.queryParams.projectId, engine: 'Freemarker', ids: row.id }).then((response) => {
            if (response.success) {
              /* if (row.isComponent == 'N') {
                stopMonitor({ key_: row.key_ }).then((response) => {})
              } */
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
      /* 1.数据需要同步到Zabbix；2.删除设备时需要删除掉属性值*/
      this.$refs['form'].validate((valid) => {
        if (valid) {
          // if (this.form.labelText) {
          this.form.labelText = this.form.labelText ? this.form.labelText : this.form.name
          // }
          this.form.engine = 'Freemarker'
          if (this.form.id !== undefined) {
            // 修改
            /* startMonitor(this.form).then((response) => {
              if (response.code == 200) {
                this.msgSuccess('ZABBIX设备修改成功，即将修改门户设备') */
                updateResource(this.form).then((res) => {
                  if (res.success) {
                    this.open = false;
                    this.getList();
                    this.msgSuccess('门户设备修改成功');
                  } else {
                    this.msgError('门户设备修改失败，' + res.errorMsg)
                  }
                })
                /*
              } else {
                this.msgInfo('设备修改失败，' + response.msg)
              }
            }) */
          } else {
            // 新增
            this.form.projectId = this.queryParams.projectId
            /* startMonitor(this.form).then((response) => {
              if (response.code == 200) {
                this.msgSuccess('ZABBIX设备新增成功，即将添加门户设备') */
                addResource(this.form).then((res) => {
                  if (res.success) {
                    this.msgSuccess('门户设备新增成功');
                    this.open = false;
                    this.getList();
                  } else {
                    // this.msgError('门户设备新增失败，' + res.errorMsg)
                    this.$message({
                      showClose: true,
                      duration: 0,
                      message: '门户设备新增失败，' + res.errorMsg,
                      type: 'error'
                    })
                  }
                })
                /* this.open = false
                this.getList()
              } else {
                this.msgInfo('设备新增失败，' + response.msg)
              }
            }) */
          }
        }
      })
    },
    getList() {
      this.loading = true
      localStorage.setItem('deviceParams', JSON.stringify(this.queryParams))
      getResourceList(this.queryParams).then((response) => {
        this.loading = false
        if (response.success) {
          this.totalCount = response.totalCount
          this.dataList = response.datas
        } else {
          this.dataList = []
          this.msgError('查询失败：\n' + response.errorMsg)
        }
      })
    },
    reset() {
      this.form = {
        classId: undefined,
        name: undefined,
        labelText: undefined,
        key_: undefined,
        ipAddess: undefined,
        controlIP: undefined,
        owner: undefined,
        area: undefined,
        vendor: undefined,
        isEnable: 'Y',
        remark: undefined,
        templateid: [],
        groupid: [],
        proxyid: undefined
      }
      this.resetForm('form')
    },
    /** 搜索按钮操作 */
    handleQuery() {
      localStorage.setItem('deviceParams', JSON.stringify(this.queryParams))
      this.queryParams.start = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.classIdName = ''
      this.classIdName1 = ''
      this.queryParams = {
        start: 1,
        limit: 20,
        name: '',
        vendor: '',
        FirstOccurrence: '',
        classIdName: '',
        classId: null,
        isEnable: 'Y',
        projectId: 10,
        engine: 'Freemarker'
      }
      this.resetForm('queryParams')
      this.handleQuery()
    },
    handleAtrrUpdate(row) {
      localStorage.setItem('deviceParams', JSON.stringify(this.queryParams))
      this.$router.push({ name: '属性维护', params: row })
    },
    getZabbixInfo() {
      const param = { projectId: this.queryParams.projectId }
      getZabbixHostGroups(param).then((response) => {
        const { success, datas } = response
        if (success) {
          this.hostGroups = datas
        }
      }) // 查询主机组
      param.status = 3
      getZabbixHost(param).then((response) => {
        //
        const { success, datas } = response
        if (success) {
          this.templates = datas
        }
      }) // 查询模板
      param.status = 5
      getZabbixHost(param).then((response) => {
        //
        const { success, datas } = response
        if (success) {
          this.proxyAgents = datas
        }
      }) // 查询代理agent
    }
  }
}
</script>
<style lang="scss" scoped>
@import '../../../assets/styles/index.scss';
.operateBox {
  //   border-radius: 4px;
  padding: 0;
  padding-left: 15px;
}
.showTableBox {
  margin-top: 20px;
  padding: 0px;
}

.tagLink {
  background-color: transparent;
  border-color: transparent;
  height: 20px;
  // padding: 0 6px;
  line-height: 20px;
  font-size: 12px;
  cursor: pointer;
  color: #c0d0d0;
  i {
    color: rgba(24, 144, 255, 1);
  }
}
</style>
