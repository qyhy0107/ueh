<template>
  <div class="app-container">
    <div ref="OperateBox" class="operateBox">
      <el-form :inline="true" :model="formInline" label-position="left" @submit.native.prevent>
        <el-form-item label="策略名称">
          <el-input v-model.trim="formInline.name" clearable size="small" placeholder="请输入名称" @keyup.enter.native="onSearch" />
        </el-form-item>
        <el-form-item label="生效状态">
          <!-- 0.未执行 1.执行中 2.已执行 -->
          <el-select v-model="formInline.status" placeholder="请选择生效状态" clearable size="small" style="width:110px" @change="onSearch">
            <el-option label="未执行" value="0" />
            <el-option label="执行中" value="1" />
            <el-option label="已执行" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="最后修改时间">
          <el-date-picker v-model="formInline.time" style="width:480px" clearable size="small" type="datetimerange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="timestamp" @change="onSearch" />
        </el-form-item>
        <el-form-item label="最后修改人">
          <el-input v-model.trim="formInline.userName" clearable size="small" placeholder="请输入名称" @keyup.enter.native="onSearch" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="mini" @click="onSearch">查询</el-button>
          <el-button size="mini" @click="onRest">重置</el-button>
          <el-button class="addButton" size="mini" @click="onAdd">新增</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="showTableBox" :style="{ height: OperateBoxHeight + 'px' }">
      <el-table v-if="dataList.length" :data="dataList" border>
        <el-table-column prop="name" min-width="10%" label="策略名称" show-overflow-tooltip />
        <el-table-column prop="exec_type" min-width="6%" show-overflow-tooltip label="时间规则" :formatter="getExecType" align="center" />
        <el-table-column prop="interval_type" min-width="6%" label="重复类型" align="center" show-overflow-tooltip />
        <el-table-column prop="timeDescription" min-width="20%" label="时间描述" show-overflow-tooltip :formatter="getTimeDescription" />
        <!-- <el-table-column prop="effectValue" min-width="18%" label="屏蔽类型" :formatter="getEffectValue" show-overflow-tooltip /> -->
        <el-table-column prop="lm_timestamp" min-width="12%" label="最后修改时间" show-overflow-tooltip />
        <el-table-column prop="modifier" min-width="8%" label="最后修改人" show-overflow-tooltip />
        <el-table-column prop="is_enable" min-width="6%" label="启用" align="center" show-overflow-tooltip>
          <template slot-scope="scope">
            <span v-if="scope.row.is_enable == true">
              <el-tag>启用</el-tag>
            </span>
            <span v-else>
              <el-tag type="danger">不启用</el-tag>
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="on_completion" min-width="6%" label="过期保留" align="center" show-overflow-tooltip>
          <template slot-scope="scope">
            <span v-if="scope.row.on_completion == 'Y'">
              <el-tag>保留</el-tag>
            </span>
            <span v-else>
              <el-tag type="danger">不保留</el-tag>
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip min-width="16%" />
        <el-table-column label="操作" align="center" show-overflow-tooltip min-width="15%">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-view" @click="handleWatch(scope.row)">查看</el-button>
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <Deficiency v-else width="25%" height="auto" />
      <pagination v-show="totalCount > 0" :page-sizes="[10, 20, 50, 200]" :total="totalCount" :page.sync="queryParams.start" :limit.sync="queryParams.limit" @pagination="search()" />
    </div>
    <!-- 添加或修改菜单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1000px" class="menuDialog">
      <el-row style="height:100%">
        <el-col :span="3" style="height:100%">
          <el-menu v-model="activeIndex" :default-active="activeIndex" class="el-menu-vertical-demo" @select="IsActive">
            <el-menu-item index="2">
              <span slot="title">基本信息</span>
            </el-menu-item>
            <el-menu-item index="3">
              <span slot="title">规则</span>
            </el-menu-item>
          </el-menu>
        </el-col>
        <el-form ref="ruleForm" :model="ruleForm" :rules="rules" label-width="120px" class="demo-ruleForm">
          <!-- 基本信息 -->
          <el-col v-show="activeIndex == '2'" :span="21" style="padding:10px">
            <el-form-item label="策略名称" prop="name">
              <el-input v-model.trim="ruleForm.name" placeholder="请输入策略名称" :disabled="disable" />
            </el-form-item>
            <el-form-item label="生效规则" prop="exec_type">
              <el-radio-group v-model="ruleForm.exec_type" :disabled="disable" @change="changeExecType">
                <el-radio label="O" value="O">单次</el-radio>
                <el-radio label="R" value="R">重复</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-show="once" label="生效时段" prop="timeDescription">
              <el-date-picker v-model="ruleForm.timeDescription" value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange" :disabled="disable" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" />
            </el-form-item>
            <el-form-item v-show="twice" label="" prop="interval_type">
              <el-radio-group v-model="ruleForm.interval_type" :disabled="disable" @change="changeIntervalType">
                <el-radio v-for="item in radios" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
            <!-- 选择日 -->
            <el-form-item v-show="day" label="">
              <el-time-picker v-model="ruleForm.execute_at" class="date-box" format="HH:mm:ss" size="small" :disabled="disable" placeholder="开始时间" value-format="HH:mm:ss" />
              <el-time-picker v-model="ruleForm.execute_util" class="date-box" size="small" placeholder="结束时间" format="HH:mm:ss" :disabled="disable" value-format="HH:mm:ss" />
            </el-form-item>
            <!-- 选择周 -->
            <el-form-item v-show="week" label="">
              <el-row style="height:40px">
                <el-col :span="6">
                  <el-select v-model="ruleForm.day_of_week_at" placeholder="请选择周" size="small" class="el-form-item__label" :disabled="disable">
                    <el-option v-for="item in weeks" :key="item.id" :label="item.week" :value="item.id" />
                  </el-select>
                </el-col>
                <el-col :span="6">
                  <el-time-picker v-model="ruleForm.execute_at" class="date-box" size="small" format="HH:mm:ss" :disabled="disable" placeholder="开始时间" value-format="HH:mm:ss" />
                </el-col>
                <el-col :span="6">
                  <el-select v-model="ruleForm.day_of_week_util" placeholder="请选择周" :disabled="disable" size="small" class="el-form-item__label">
                    <el-option v-for="item in weeks" :key="item.id" :label="item.week" :value="item.id" />
                  </el-select>
                </el-col>
                <el-col :span="6">
                  <el-time-picker v-model="ruleForm.execute_util" class="date-box" size="small" format="HH:mm:ss" :disabled="disable" placeholder="结束时间" value-format="HH:mm:ss" />
                </el-col>
              </el-row>
            </el-form-item>
            <!-- 选择月 -->
            <el-form-item v-show="month" label="">
              <el-row style="height:40px">
                <el-col :span="6">
                  <el-select v-model="ruleForm.day_start" placeholder="请选择天" size="small" class="el-form-item__label" :disabled="disable">
                    <el-option v-for="item in months" :key="item.id" :label="item.day" :value="item.id" />
                  </el-select>
                </el-col>
                <el-col :span="6">
                  <el-time-picker v-model="ruleForm.execute_at" class="date-box" size="small" format="HH:mm:ss" placeholder="结束时间" value-format="HH:mm:ss" :disabled="disable" />
                </el-col>
                <el-col :span="6">
                  <el-select v-model="ruleForm.day_end" placeholder="请选择天" size="small" class="el-form-item__label" :disabled="disable">
                    <el-option v-for="item in months" :key="item.id" :label="item.day" :value="item.id" />
                  </el-select>
                </el-col>
                <el-col :span="6">
                  <el-time-picker v-model="ruleForm.execute_util" class="date-box" size="small" placeholder="结束时间" format="HH:mm:ss" value-format="HH:mm:ss" :disabled="disable" />
                </el-col>
              </el-row>
            </el-form-item>
            <!-- 选择年 -->
            <el-form-item v-show="year" label="">
              <el-row style="height:40px">
                <el-col>
                  <el-select v-model="ruleForm.month_start" placeholder="请选择月" size="small" class="el-form-item__label" style="width:30%" :disabled="disable">
                    <el-option v-for="item in years" :key="item.id" :label="item.month" :value="item.id" />
                  </el-select>
                  <el-select v-model="ruleForm.day_start" placeholder="请选择天" size="small" class="el-form-item__label" style="width:30%" :disabled="disable">
                    <el-option v-for="item in months" :key="item.id" :label="item.day" :value="item.id" />
                  </el-select>
                  <el-time-picker v-model="ruleForm.execute_at" class="date-box" format="HH:mm:ss" placeholder="开始时间" size="small" value-format="HH:mm:ss" style="width:30%" :disabled="disable" />
                </el-col>
                <el-col>
                  <el-select v-model="ruleForm.month_end" placeholder="请选择月" size="small" class="el-form-item__label" style="width:30%" :disabled="disable">
                    <el-option v-for="item in years" :key="item.id" :label="item.month" :value="item.id" />
                  </el-select>
                  <el-select v-model="ruleForm.day_end" placeholder="请选择天" size="small" class="el-form-item__label" style="width:30%" :disabled="disable">
                    <el-option v-for="item in months" :key="item.id" :label="item.day" :value="item.id" />
                  </el-select>
                  <el-time-picker v-model="ruleForm.execute_util" style="width:30%" class="date-box" size="small" placeholder="结束时间" format="HH:mm:ss" value-format="HH:mm:ss" :disabled="disable" />
                </el-col>
              </el-row>
            </el-form-item>
            <el-row>
              <el-col :span="12">
                <el-form-item label="过期保留" prop="on_completion">
                  <el-radio-group v-model="ruleForm.on_completion" :disabled="disable">
                    <el-radio label="Y">保留</el-radio>
                    <el-radio label="N">不保留</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="是否启用" prop="is_enable">
                  <el-switch v-model="ruleForm.is_enable" :disabled="disable" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="备注" prop="remark">
              <el-input v-model.trim="ruleForm.remark" type="textarea" :disabled="disable" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
          <!-- 规则 -->
          <el-col v-show="activeIndex == '3'" :span="21" style="padding: 10px;overflow: auto;height: 590px;">
            <el-form-item label="事件源类型" prop="event_source_type">
              <el-select v-model="ruleForm.event_source_type" size="mini" :disabled="disable" placeholder="请选择事件源" style="width:50%" @change="getProbeSourceTypes()">
                <el-option label="ZABBIX" value="ZABBIX" />
                <el-option label="CUSTOM" value="CUSTOM" />
              </el-select>
            </el-form-item>
            <el-form-item label="设备类别" prop="classId">
              <el-select ref="selectUpResIdform" v-model="classId" :disabled="disable" clearable size="small" placeholder="请选择设备类别" style="width:30%" @focus="filterText1 = ''">
                <el-option hidden :value="classId" :label="classIdName1" />
                <el-input v-model.trim="filterText1" placeholder="输入关键字进行过滤" size="mini" style="margin:5px;margin-bottom:0;width:95%" />
                <el-tree ref="treeForm" :filter-node-method="filterNode" :data="deviceCategories1" default-expand-all @node-click="findGroupByClassId">
                  <div slot-scope="{ node, data }" class="custom-tree-node" style="width:100%">
                    <span v-if="data.disabled" style="color:rgb(132 130 130);cursor:no-drop;display:inline-block;width:100%">{{ node.label }}</span>
                    <span v-else>{{ node.label }}</span>
                  </div>
                </el-tree>
              </el-select>
              <el-select v-model="deviceArr" filterable size="small" collapse-tags multiple clearable placeholder="请选择设备" style="width:69%" :disabled="!classId" :reserve-keyword="true" @change="selectDevice">
                <el-option v-for="item in deviceInfo" :key="item.key_" :value="item.label_text + '::' + item.key_" :label="item.label_text + '' + item.key_">
                  <span style="float: left">{{ item.label_text }}</span>
                  <span style="float: right; color:rgb(172 182 197); font-size: 13px">{{ item.key_ }}</span>
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="维护期设备" prop="deviceArrTable">
              <el-table empty-text=" " element-loading-text="拼命加载中..." :data="deviceArrTable" border :height="250" style="width: 100%;">
                <template slot="empty" style="height:100%">
                  <Deficiency style="padding-top:240px" height="100px" width="auto" />
                </template>
                <el-table-column prop="label_text" label="设备名称" min-width="65%" />
                <el-table-column prop="key_" label="设备IP" min-width="25%" />
                <!-- <el-table-column label="操作" min-width="15%" /> -->
                <el-table-column label="操作" min-width="10%">
                  <template slot-scope="scope">
                    <el-button size="mini" type="text" :disabled="disable" @click="deleteTableData(scope)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
            <el-form-item label="手工录入设备">
              <el-input v-model.trim="ruleForm.params.ipValue1" :disabled="disable" type="textarea" :rows="4" />
              <div style="line-height: 16px;margin-top:8px">1.维护期设备可以直接复制过来，填写在手工录入设备中，用固定分隔符分隔;</div>
              <div style="line-height: 16px;">2.选择设备类别、设备最终会展示在维护期设备的表格中;</div>
              <div style="line-height: 16px;">3.如果手工录入设备与维护期设备都有数据，提交的时候会结合这两部分的数据</div>
            </el-form-item>
          </el-col>
        </el-form>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button v-if="!disable" class="cancel_button" @click="resetForm('ruleForm')">取 消</el-button>
        <el-button v-else class="cancel_button" @click="resetForm('ruleForm')">关 闭</el-button>
        <el-button v-show="!disable" type="primary" class="confirm_button" @click="submitForm()">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { addHandlerRulesDefine, updateHandlerRulesDefine, getHandlerRulesDefine, deleteHandlerRulesDefine, getEventStrategyById, getProbeSourceType, getManagerSelect, getPhysicalCategory1, getResourceListMP } from '@/api/cm/eventStrategy'
import moment from 'moment'
import Deficiency from '@/components/Deficiency/index'
import { getPhysicalCategories } from '@/api/cm/physicalCategory'
export default {
  components: { Deficiency },
  data() {
    return {
      deviceArrTable: [],
      deviceArr: [],
      deviceInfo: [],
      classId: '',
      classIdName1: '',
      filterText1: '', // 分组过滤文字
      deviceCategories1: [], // 分组下拉框
      isComponentArr: [{ mappingValue: 0, name: '组件告警' }, { mappingValue: 1, name: '设备告警' }],
      acknowledgedArr: [{ mappingValue: 0, name: '未确认' }, { mappingValue: 1, name: '已确认' }],
      recoveredStatusArr: [{ mappingValue: 1, name: '未恢复' }, { mappingValue: 2, name: '已恢复' }],
      operatorsIntRecoveredStatus: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }],
      opsIntIsComponent: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }],
      operatorsIntAcknowledged: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }],
      manager_types: [],
      operatorsStrEventCategory: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }],
      operatorsStrAgent: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }],
      operatorsStrManager: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }],
      eventSourceTypeSelect: [],
      // 级别数组
      SeverityArr: [],
      conditions: [{ column: null, operator: null, value: null, dataType: null }],
      // TIMESTAMP运算符
      operatorsTime: [{ opertorName: '在  和  之间', opertorChar: 'between' }],
      // 数字
      operatorsInt: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }, { opertorName: '小于', opertorChar: '<' }, { opertorName: '小于等于', opertorChar: '<=' }, { opertorName: '大于', opertorChar: '>' }, { opertorName: '大于等于', opertorChar: '>=' }, { opertorName: '包含', opertorChar: 'in' }, { opertorName: '在  和  之间', opertorChar: 'between' }, { opertorName: '正则', opertorChar: 'regex' }],
      // STRING运算符
      operatorsStr: [
        { opertorName: '等于', opertorChar: '=' },
        { opertorName: '不等于', opertorChar: '!=' },
        /* { opertorName: "包含", opertorChar: "in" },*/
        { opertorName: '相似', opertorChar: 'like' },
        { opertorName: '正则', opertorChar: 'regex' }
      ],
      // 带ip段的STRING运算符
      operatorsStrNode: [
        { opertorName: '等于', opertorChar: '=' },
        { opertorName: '不等于', opertorChar: '!=' },
        { opertorName: 'IP段', opertorChar: 'ip' },
        /* { opertorName: "包含", opertorChar: "in" },*/
        { opertorName: '相似', opertorChar: 'like' },
        { opertorName: '正则', opertorChar: 'regex' }
      ],
      formData3: {
        powerAttrList: [
          {
            conditionColumn: 'Node',
            conditionDataType: 'STRING',
            operator: 'ip',
            conditionValue: [],
            mapping: [
              {
                sourceColumn: '',
                sourceValue: ''
              }
            ],
            resource_type: '',
            resource_name: ''
          }
        ]
      },
      effect: [
        {
          effectColumn: '',
          effectDataType: '',
          effectType: '',
          effectValue: ['NF', 'NN', 'NS', 'NE']
        }
      ],
      status: false,
      // 运算公式
      formular: 'Node',
      // 表达式
      expression: [
        {
          conditionColumn: 'Node',
          conditionDataType: 'STRING',
          operator: 'ip',
          conditionValue: []
        }
      ],
      // 字段映射
      eventField: [
        {
          eventFields: '',
          operators: '',
          resultValues: '',
          logicalOperations: ''
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
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 总条数
      total: 0,
      totalCount: 1,
      queryParams: {
        start: 1,
        limit: 20
      },
      formInline: {
        name: '',
        time: '',
        userName: '',
        shield: ''
      },
      // 新增、修改弹框
      open: false,
      dataList: [],
      show: false,
      radios: [{ label: '日', value: 'day', isChecked: true }, { label: '周', value: 'week', isChecked: false }, { label: '月', value: 'month', isChecked: false }, { label: '年', value: 'year', isChecked: false }],
      sources: [{ id: 'ZABBIX', name: 'ZABBIX' }, { id: 'SYSLOG', name: 'SYSLOG' }, { id: 'SNMPTRAP', name: 'SNMPTRAP' }],
      operators: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }, { opertorName: '小于', opertorChar: '<' }, { opertorName: '小于等于', opertorChar: '<=' }, { opertorName: '大于', opertorChar: '>' }, { opertorName: '大于等于', opertorChar: '>=' }, { opertorName: '包含', opertorChar: 'in' }, { opertorName: '相似', opertorChar: 'like' }, { opertorName: '在  和  之间', opertorChar: 'between' }, { opertorName: '正则', opertorChar: 'regex' }],
      effectExper: [],
      resource_types: '',
      eventSources: '',
      event_source: [],
      ruleForm: {
        name: '',
        exec_type: 'O',
        timeDescription: [moment().format('YYYY-MM-DD 00:00:00'), moment().format('YYYY-MM-DD 23:59:59')],
        execute_util: '',
        execute_at: '',
        is_enable: true,
        event_source_type: 'ZABBIX',
        rule_type: 'MP',
        event_source: '',
        on_completion: 'Y',
        day_of_week_at: '',
        day_of_week_util: '',
        interval_type: 'day',
        day_start: '',
        day_end: '',
        month_start: '',
        month_end: '',
        formular: 'Node',
        effect: [
          {
            effectColumn: '',
            effectDataType: '',
            effectType: '',
            effectValue: ['NF', 'NN', 'NS', 'NE']
          }
        ],
        expression: [
          {
            conditionColumn: 'Node',
            conditionDataType: 'STRING',
            operator: 'ip',
            conditionValue: []
          }
        ],
        remark: '',
        displayOrder: 0,
        params: {
          ipTable: [],
          // 表格ip
          ipValue: '',
          // 手动
          ipValue1: ''
        }
      },
      saveOrUpdate: 'save',
      once: true,
      twice: false,
      day: false,
      week: false,
      month: false,
      year: false,
      years: [{ month: '1 月', id: '01' }, { month: '2 月', id: '02' }, { month: '3 月', id: '03' }, { month: '4 月', id: '04' }, { month: '5 月', id: '05' }, { month: '6 月', id: '06' }, { month: '7 月', id: '07' }, { month: '8 月', id: '08' }, { month: '9 月', id: '09' }, { month: '10 月', id: '10' }, { month: '11 月', id: '11' }, { month: '12 月', id: '12' }],
      months: [
        { day: '1 日', id: '01' },
        { day: '2 日', id: '02' },
        { day: '3 日', id: '03' },
        { day: '4 日', id: '04' },
        { day: '5 日', id: '05' },
        { day: '6 日', id: '06' },
        { day: '7 日', id: '07' },
        { day: '8 日', id: '08' },
        { day: '9 日', id: '09' },
        { day: '10 日', id: '10' },
        { day: '11 日', id: '11' },
        { day: '12 日', id: '12' },
        { day: '13 日', id: '13' },
        { day: '14 日', id: '14' },
        { day: '15 日', id: '15' },
        { day: '16 日', id: '16' },
        { day: '17 日', id: '17' },
        { day: '18 日', id: '18' },
        { day: '19 日', id: '19' },
        { day: '20 日', id: '20' },
        { day: '21 日', id: '21' },
        { day: '22 日', id: '22' },
        { day: '23 日', id: '23' },
        { day: '24 日', id: '24' },
        { day: '25 日', id: '25' },
        { day: '26 日', id: '26' },
        { day: '27 日', id: '27' },
        { day: '28 日', id: '28' },
        { day: '29 日', id: '29' },
        { day: '30 日', id: '30' }
      ],
      weeks: [{ week: '周一', id: '1' }, { week: '周二', id: '2' }, { week: '周三', id: '3' }, { week: '周四', id: '4' }, { week: '周五', id: '5' }, { week: '周六', id: '6' }, { week: '周日', id: '7' }],
      rules: {
        event_source_type: [{ required: true, message: '请选择数据源', trigger: 'blur' }],
        rule_type: [{ required: true, message: '请选择策略类型', trigger: 'blur' }],
        name: [{ required: true, message: '请填写策略名称', trigger: 'blur' }]
      }
    }
  },
  watch: {
    deviceArrTable: {
      handler(val, oldval) {
        this.$set(this.ruleForm.params, 'ipTable', val)
        const temp = val.map((item) => {
          return item.key_
        })
        // 用；分割
        if (temp.length) {
          const temp1 = temp.join(';')
          this.$set(this.ruleForm.params, 'ipValue', temp1)
          var temp2 = ''
          if (this.ruleForm.params.ipValue1) {
            temp2 = this.ruleForm.params.ipValue1 + ';' + temp1
          } else {
            temp2 = temp1
          }
          this.ruleForm.expression[0].conditionValue[0] = temp2
        } else {
          this.$set(this.ruleForm.params, 'ipValue', '')
          if (this.ruleForm.params.ipValue1) {
            this.ruleForm.expression[0].conditionValue[0] = this.ruleForm.params.ipValue1
          } else {
            this.ruleForm.expression[0].conditionValue = []
          }
        }
      },
      deep: true
    },
    'ruleForm.params.ipValue1': function(newValue, oldValue) {
      if (newValue) {
        this.$set(this.ruleForm.params, 'ipValue1', newValue)
        var temp2 = ''
        if (this.ruleForm.params.ipValue) {
          temp2 = this.ruleForm.params.ipValue1 + ';' + this.ruleForm.params.ipValue
        } else {
          temp2 = this.ruleForm.params.ipValue1
        }
        this.ruleForm.expression[0].conditionValue[0] = temp2
      } else {
        this.$set(this.ruleForm.params, 'ipValue1', '')
        if (this.ruleForm.params.ipValue) {
          this.ruleForm.expression[0].conditionValue[0] = this.ruleForm.params.ipValue
        } else {
          this.ruleForm.expression[0].conditionValue = []
        }
      }
    },
    classId: function(newValue, oldValue) {
      this.deviceArr = []
      if (!this.classId) {
        this.classIdName1 = ''
      } else {
        this.getResourceListMP(this.classId)
      }
      this.findGroupByClassId()
    }
  },
  created() {
    this.getPhysicalCategory()
    if (this.$route.params.name) {
      this.formInline.name = this.$route.params.name
    }
    // this.getEventSourceTypeSelect()
    this.getHandlerRulesDefines()
    // this.getPhysicalCategorys()
    // this.getManagerSelect()
  },
  updated() {
    this.getOperateBoxHeight()
  },
  mounted() {
    this.getOperateBoxHeight()
  },
  methods: {
    deleteTableData(val) {
      this.deviceArr = []
      this.classId = ''
      this.classIdName1 = ''
      this.deviceArrTable.splice(val.$index, 1)
    },
    selectDevice(val) {
      val.forEach((item, index) => {
        var temp = item.split('::')
        if (JSON.stringify(this.deviceArrTable).indexOf(JSON.stringify({ label_text: temp[0], key_: temp[1] })) === -1) {
          this.deviceArrTable.unshift({ label_text: temp[0], key_: temp[1] })
        }
      })
    },
    getResourceListMP(val) {
      getResourceListMP({
        classId: val,
        projectId: 10,
        engine: 'Freemarker'
      }).then((response) => {
        if (response.success) {
          this.deviceInfo = response.datas
        } else {
          this.deviceInfo = []
        }
      })
    },
    findGroupByClassId(data) {
      // console.log(data)
      if (!data) return
      if (data.disabled) {
        this.msgError('当前项不可以点击')
        return
      }
      this.classId = data.id
      this.classIdName1 = data.label
      this.$refs.selectUpResIdform.blur()
    },
    // 获取设备分类
    getPhysicalCategory() {
      getPhysicalCategories({
        projectId: 10,
        engine: 'Freemarker',
        isEnable: 'Y'
      }).then((response) => {
        if (response.success) {
          this.deviceCategories1 = []
          // this.deviceCategories = response.datas
          this.deviceCategories1 = JSON.parse(JSON.stringify(this.handleTree(response.datas, 'id')).replace(new RegExp('labelText', 'g'), 'label'))
          this.deviceCategories1.forEach((item, index) => {
            if (item.children) {
              this.deviceCategories1[index].disabled = true
            }
          })
          this.deviceCategories1 = this.deviceCategories1.filter((item, index) => {
            //  排除不展示的项
            if (item.label !== 'CMDB' && item.label !== '动环' && item.label !== '应用系统') {
              return item
            }
          })
        } else {
          this.msgError('查询设备类别失败：\n' + response.errorMsg)
        }
      })
    },
    // 过滤设备类别
    filterNode(value, data) {
      if (!value) return true
      const stringTemp = data.label.toLowerCase()
      const stringTemp1 = value.toLowerCase()
      return stringTemp.indexOf(stringTemp1) !== -1
    },
    // get
    // getManagerSelect() {
    //   getManagerSelect({ projectId: 10 }).then((response) => {
    //     this.manager_types = response.datas
    //   })
    // },
    // getEventSourceTypeSelect() {
    //   getEventSourceTypeSelect({ projectId: 10 }).then((res) => {
    //     if (res.success) {
    //       this.eventSourceTypeSelect = res.datas
    //     }
    //   })
    // },

    // 查询类型设置
    // columnChange(row) {
    //   const { conditionColumn } = row
    //   row.conditionDataType = ''
    //   row.operator = ''
    //   row.conditionValue = []
    //   if (!row.hasOwnProperty('operatorLogic')) {
    //     this.$set(row, 'operatorLogic', '')
    //   }
    //   this.effectExper.map((currentValue) => {
    //     const { columnInDB, dataType } = currentValue
    //     if (conditionColumn === columnInDB) {
    //       row.conditionDataType = dataType
    //     }
    //   })
    // },
    // getPhysicalCategorys() {
    //   getPhysicalCategory1({ projectId: 10 }).then((response) => {
    //     this.resource_types = response.datas
    //   })
    // },
    changeIntervalTypeBy() {
      if (this.ruleForm.interval_type === 'day') {
        this.day = true
        this.week = false
        this.month = false
        this.year = false
      } else if (this.ruleForm.interval_type === 'week') {
        this.day = false
        this.week = true
        this.month = false
        this.year = false
      } else if (this.ruleForm.interval_type === 'year') {
        this.day = false
        this.week = false
        this.month = false
        this.year = true
      } else if (this.ruleForm.interval_type === 'month') {
        this.day = false
        this.week = false
        this.month = true
        this.year = false
      }
    },
    changeIntervalType() {
      if (this.ruleForm.interval_type === 'day') {
        this.ruleForm.day_of_week_at = ''
        this.ruleForm.day_of_week_util = ''
        this.ruleForm.month_start = ''
        this.ruleForm.month_end = ''
        this.ruleForm.day_start = ''
        this.ruleForm.day_end = ''
        this.ruleForm.execute_at = moment().format('00:00:00')
        this.ruleForm.execute_util = moment().format('23:59:59')
        this.day = true
        this.week = false
        this.month = false
        this.year = false
      } else if (this.ruleForm.interval_type === 'week') {
        this.ruleForm.month_start = ''
        this.ruleForm.month_end = ''
        this.ruleForm.day_start = ''
        this.ruleForm.day_end = ''
        var weekOfday = moment().format('E')
        this.ruleForm.day_of_week_at = weekOfday
        this.ruleForm.day_of_week_util = weekOfday
        this.ruleForm.execute_at = moment().format('00:00:00')
        this.ruleForm.execute_util = moment().format('23:59:59')
        this.day = false
        this.week = true
        this.month = false
        this.year = false
      } else if (this.ruleForm.interval_type === 'year') {
        this.ruleForm.day_of_week_at = ''
        this.ruleForm.day_of_week_util = ''
        this.ruleForm.day_start = moment().format('DD')
        this.ruleForm.day_end = moment().format('DD')
        this.ruleForm.month_start = moment().format('MM')
        this.ruleForm.month_end = moment().format('MM')
        this.day = false
        this.week = false
        this.month = false
        this.year = true
      } else if (this.ruleForm.interval_type === 'month') {
        this.ruleForm.day_of_week_at = weekOfday = ''
        this.ruleForm.day_of_week_util = weekOfday = ''
        this.ruleForm.day_start = moment().format('DD')
        this.ruleForm.day_end = moment().format('DD')
        this.day = false
        this.week = false
        this.month = true
        this.year = false
      }
    },
    changeExecType() {
      if (this.ruleForm.exec_type === 'O') {
        this.ruleForm.timeDescription = [moment().format('YYYY-MM-DD 00:00:00'), moment().format('YYYY-MM-DD 23:59:59')]
        this.once = true
        this.twice = false
        this.month = false
        this.year = false
        this.week = false
        this.day = false
      } else if (this.ruleForm.exec_type === 'R') {
        // 第一步
        this.ruleForm.timeDescription = []
        this.ruleForm.execute_at = '00:00:00'
        this.ruleForm.execute_util = '23:59:59'
        this.once = false
        this.twice = true
        this.month = false
        this.year = false
        this.week = false
        this.day = true
        this.ruleForm.interval_type = 'day'
      }
    },
    // 数据初始化
    getHandlerRulesDefines() {
      this.loading = true
      getHandlerRulesDefine({
        engine: 'Freemarker',
        shield: this.formInline.shield,
        status: this.formInline.status,
        name: this.formInline.name,
        time1: this.formInline.time ? this.formInline.time[0] : null,
        time2: this.formInline.time ? this.formInline.time[1] : null,
        userName: this.formInline.userName,
        ruleTypes: ['MP'],
        projectId: 10,
        start: this.queryParams.start,
        limit: this.queryParams.limit
      }).then((response) => {
        this.loading = false
        if (response.success) {
          if (response.datas.length) {
            this.dataList = response.datas.map((item) => {
              item.lm_timestamp = moment(item.lm_timestamp).format('YYYY-MM-DD HH:mm:ss')
              return item
            })
            this.totalCount = response.totalCount
          } else {
            this.dataList = []
          }
        } else {
          this.dataList = []
        }
      })
    },
    getTimeDescription(row) {
      var start
      var end
      if (row.exec_type === 'R') {
        if (row.interval_type === 'week') {
          start = '周' + row.day_of_week_at + ' ' + row.execute_at
          end = '周' + row.day_of_week_util + ' ' + row.execute_util
        } else {
          start = row.execute_at
          end = row.execute_util
        }
      } else {
        start = row.execute_at
        end = row.execute_util
      }
      return start + '~' + end
    },

    getExecType(row) {
      // 生效类型：O单次、R重复
      if (row.exec_type.trim() === 'O') {
        return '单次'
      }
      if (row.exec_type.trim() === 'R') {
        return '重复'
      }
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
      this.getHandlerRulesDefines()
    },
    onRest() {
      // this.formInline.name = ''
      // this.formInline.shield = ''
      this.formInline = {
        name: '',
        time: '',
        status: '',
        shield: '',
        userName: ''
      }
      this.getHandlerRulesDefines()
    },
    search() {
      this.getHandlerRulesDefines()
    },
    // 添加方法
    onAdd() {
      this.deviceArrTable = []
      this.resetForm('ruleForm')
      this.activeIndex = '2'
      this.title = '新增'
      this.classId = ''
      this.classIdName1 = ''
      this.saveOrUpdate = 'save'
      this.disable = false
      // console.log('submit!');
      this.open = true
      // this.ruleForm.usageType='N'
    },
    submitForm() {
      this.$refs['ruleForm'].validate((valid) => {
        if (valid) {
          const ruleFormTempObj = JSON.parse(JSON.stringify(this.ruleForm))
          const userStr = localStorage.getItem('user')
          const modifier = JSON.parse(userStr).userName
          // console.log(modifier)
          this.$set(ruleFormTempObj, 'modifier', modifier)
          if (this.saveOrUpdate === 'save') {
            ruleFormTempObj.project_id = '10'
            if (ruleFormTempObj.is_enable === true) {
              ruleFormTempObj.is_enable = 'Y'
            } else {
              ruleFormTempObj.is_enable = 'N'
            }
            if (ruleFormTempObj.rule_type === 'C') {
              ruleFormTempObj.event_source = this.event_source
            }
            if (ruleFormTempObj.effect !== '') {
              const severityMapping = ruleFormTempObj.effect
              ruleFormTempObj.effect = JSON.stringify(severityMapping)
            }
            ruleFormTempObj.params = JSON.stringify(ruleFormTempObj.params)
            if (ruleFormTempObj.rule_type === 'R') {
              ruleFormTempObj.expression = JSON.stringify(this.formData3.powerAttrList)
            }
            ruleFormTempObj.expression = JSON.stringify(ruleFormTempObj.expression)
            if (ruleFormTempObj.exec_type === 'O') {
              // ruleFormTempObj.interval_type = ''
              ruleFormTempObj.execute_at = ruleFormTempObj.timeDescription[0]
              ruleFormTempObj.execute_util = ruleFormTempObj.timeDescription[1]
            } else {
              if (ruleFormTempObj.interval_type === 'month') {
                ruleFormTempObj.execute_at = ruleFormTempObj.day_start + ' ' + ruleFormTempObj.execute_at
                ruleFormTempObj.execute_util = ruleFormTempObj.day_end + ' ' + ruleFormTempObj.execute_util
              } else if (ruleFormTempObj.interval_type === 'year') {
                ruleFormTempObj.execute_at = ruleFormTempObj.month_start + '-' + ruleFormTempObj.day_start + ' ' + ruleFormTempObj.execute_at
                ruleFormTempObj.execute_util = ruleFormTempObj.month_end + '-' + ruleFormTempObj.day_end + ' ' + ruleFormTempObj.execute_util
              }
            }
            if (ruleFormTempObj.timeDescription.length > 0) {
              // ruleFormTempObj.interval_type = ''
              ruleFormTempObj.execute_at = moment(ruleFormTempObj.timeDescription[0]).format('YYYY-MM-DD HH:mm:ss')
              ruleFormTempObj.execute_util = moment(ruleFormTempObj.timeDescription[1]).format('YYYY-MM-DD HH:mm:ss')
            }
            addHandlerRulesDefine(ruleFormTempObj).then((response) => {
              if (response.success) {
                this.open = false
                this.msgSuccess('保存成功')
                this.getHandlerRulesDefines()
                this.resetForm()
              } else {
                this.msgError('保存失败,已经有相同条件的策略')
                this.resetForm('ruleForm')
              }
            })
          } else {
            ruleFormTempObj.projectId = '10'
            if (ruleFormTempObj.is_enable === true) {
              ruleFormTempObj.is_enable = 'Y'
            } else {
              ruleFormTempObj.is_enable = 'N'
            }
            if (ruleFormTempObj.rule_type === 'C') {
              ruleFormTempObj.event_source = this.event_source
            }
            if (ruleFormTempObj.effect !== '') {
              const severityMapping = ruleFormTempObj.effect
              ruleFormTempObj.effect = JSON.stringify(severityMapping)
            }
            ruleFormTempObj.params = JSON.stringify(ruleFormTempObj.params)
            ruleFormTempObj.expression = JSON.stringify(ruleFormTempObj.expression)
            if (ruleFormTempObj.exec_type === 'O') {
              ruleFormTempObj.execute_at = ruleFormTempObj.timeDescription[0]
              ruleFormTempObj.execute_util = ruleFormTempObj.timeDescription[1]
            } else {
              if (ruleFormTempObj.interval_type === 'month') {
                ruleFormTempObj.execute_at = ruleFormTempObj.day_start + ' ' + ruleFormTempObj.execute_at
                ruleFormTempObj.execute_util = ruleFormTempObj.day_end + ' ' + ruleFormTempObj.execute_util
              } else if (ruleFormTempObj.interval_type === 'year') {
                ruleFormTempObj.execute_at = ruleFormTempObj.month_start + '-' + ruleFormTempObj.day_start + ' ' + ruleFormTempObj.execute_at
                ruleFormTempObj.execute_util = ruleFormTempObj.month_end + '-' + ruleFormTempObj.day_end + ' ' + ruleFormTempObj.execute_util
              }
            }
            updateHandlerRulesDefine(ruleFormTempObj).then((response) => {
              if (response.success) {
                this.open = false
                this.msgSuccess('修改成功')
                this.getHandlerRulesDefines()
                this.resetForm('ruleForm')
              } else {
                this.msgError('保存失败,已经有相同条件的策略')
                this.resetForm('ruleForm')
              }
            })
          }
        }
      })
    },
    resetForm(formName) {
      this.activeIndex = '2'
      this.ruleForm = {
        name: '',
        exec_type: 'O',
        timeDescription: [moment().format('YYYY-MM-DD 00:00:00'), moment().format('YYYY-MM-DD 23:59:59')],
        event_source_type: 'ZABBIX',
        execute_util: '',
        execute_at: '',
        is_enable: true,
        rule_type: 'MP',
        event_source: '',
        on_completion: 'Y',
        day_of_week_at: '',
        day_of_week_util: '',
        interval_type: '',
        day_start: '',
        day_end: '',
        month_start: '',
        month_end: '',
        formular: 'Node',
        effect: [
          {
            effectColumn: '',
            effectDataType: '',
            effectType: '',
            effectValue: ['NF', 'NN', 'NS', 'NE']
          }
        ],
        expression: [
          {
            conditionColumn: 'Node',
            conditionDataType: 'STRING',
            operator: 'ip',
            conditionValue: []
          }
        ],
        remark: '',
        displayOrder: 0,
        params: {
          ipTable: [],
          ipValue: '',
          ipValue1: ''
        }
      }
      this.formData3 = {
        powerAttrList: [
          {
            conditionColumn: '',
            conditionDataType: '',
            operator: '',
            conditionValue: [],
            mapping: [
              {
                sourceColumn: '',
                sourceValue: ''
              }
            ],
            resource_type: '',
            resource_name: ''
          }
        ]
      }
      this.open = false
      this.twice = false
      this.day = false
      this.week = false
      this.month = false
      this.year = false
      this.once = true
    },
    // 删除方法
    handleDelete(row) {
      this.$confirm('是否确认删除该项数据', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          const a = row.id
          deleteHandlerRulesDefine({ id: row.id, projectId: 10 }).then((response) => {
            // this.queryParams.start = 1;
            this.msgSuccess('删除成功')
            this.getHandlerRulesDefines()
          })
        })
        .catch(function(e) {
          console.error(e)
        })
    },
    // 查看方法
    handleWatch(row) {
      this.resetForm('ruleForm')
      this.deviceArrTable = []
      this.disable = true
      this.title = '查看'
      this.deviceArr = []
      this.classId = ''
      this.classIdName1 = ''
      getEventStrategyById({ id: row.id, projectId: 10 }).then((response) => {
        this.open = true
        this.activeIndex = '2'
        // this.ruleForm = response.datas[0]
        response.datas[0].params = JSON.parse(response.datas[0].params)
        this.ruleForm = response.datas[0]
        this.deviceArrTable = response.datas[0].params.ipTable
        // getProbeSourceType({
        //   projectId: 10,
        //   source_type: this.ruleForm.event_source_type
        // }).then((response) => {
        //   this.eventSources = response.datas
        // })
        this.ruleForm.is_enable = this.ruleForm.is_enable.trim()
        // this.ruleForm.timeDescription[0]=
        if (this.ruleForm.is_enable.trim() === 'Y') {
          this.ruleForm.is_enable = true
        } else {
          this.ruleForm.is_enable = false
        }
        this.ruleForm.exec_type = this.ruleForm.exec_type.trim()
        this.ruleForm.on_completion = this.ruleForm.on_completion.trim()
        if (this.ruleForm.rule_type === 'R') {
          this.formData3.powerAttrList = JSON.parse(this.ruleForm.expression)
          this.formData3.powerAttrList = JSON.parse(this.formData3.powerAttrList)
        } else {
          this.ruleForm.expression = JSON.parse(this.ruleForm.expression)
        }
        this.ruleForm.effect = JSON.parse(this.ruleForm.effect)
        // this.ruleForm.params = JSON.parse(this.ruleForm.params)
        if (this.ruleForm.exec_type === 'O') {
          this.once = true
          this.twice = false
          this.$set(this.ruleForm, 'timeDescription', [moment(this.ruleForm.execute_at).format('YYYY-MM-DD HH:mm:ss'), moment(this.ruleForm.execute_util).format('YYYY-MM-DD HH:mm:ss')])
        } else {
          this.once = false
          this.twice = true
          this.changeIntervalTypeBy()
          if (this.ruleForm.interval_type === 'month') {
            var start = this.ruleForm.execute_at.split(/[\s\n]/)
            var end = this.ruleForm.execute_util.split(/[\s\n]/)
            this.ruleForm.day_start = start[0]
            this.ruleForm.day_end = start[0]
            this.ruleForm.execute_at = start[1]
            this.ruleForm.execute_util = end[1]
          } else if (this.ruleForm.interval_type === 'year') {
            var start = this.ruleForm.execute_at.split(/[\s\n]/)
            var end = this.ruleForm.execute_util.split(/[\s\n]/)
            var start_mon = start[0].split('-')
            var end_mon = end[0].split('-')
            this.ruleForm.month_start = start_mon[0]
            this.ruleForm.month_end = end_mon[0]
            this.ruleForm.day_start = start_mon[1]
            this.ruleForm.day_end = end_mon[1]
            this.ruleForm.execute_at = start[1]
            this.ruleForm.execute_util = end[1]
          }
        }
      })
    },
    // 修改方法
    handleUpdate(row) {
      this.deviceArr = []
      this.classId = ''
      this.classIdName1 = ''
      this.deviceArrTable = []
      this.resetForm('ruleForm')
      this.activeIndex = '2'
      this.disable = false
      this.title = '修改'
      this.saveOrUpdate = 'update'
      getEventStrategyById({ id: row.id, projectId: 10 }).then((response) => {
        // console.log(response)
        // console.log(response.datas[0].params.ipTable)
        this.open = true
        response.datas[0].params = JSON.parse(response.datas[0].params)
        this.ruleForm = response.datas[0]
        this.deviceArrTable = response.datas[0].params.ipTable
        // console.log(this.deviceArrTable)
        this.ruleForm.is_enable = this.ruleForm.is_enable.trim()
        // getProbeSourceType({
        //   projectId: 10,
        //   source_type: this.ruleForm.event_source_type
        // }).then((response) => {
        //   this.eventSources = response.datas
        // })
        // this.ruleForm.timeDescription[0]=
        if (this.ruleForm.is_enable.trim() === 'Y') {
          this.ruleForm.is_enable = true
        } else {
          this.ruleForm.is_enable = false
        }
        this.ruleForm.exec_type = this.ruleForm.exec_type.trim()
        this.ruleForm.on_completion = this.ruleForm.on_completion.trim()

        if (this.ruleForm.rule_type === 'R') {
          this.formData3.powerAttrList = JSON.parse(this.ruleForm.expression)
          this.formData3.powerAttrList = JSON.parse(this.formData3.powerAttrList)
        } else {
          this.ruleForm.expression = JSON.parse(this.ruleForm.expression)
        }
        this.ruleForm.effect = JSON.parse(this.ruleForm.effect)
        // this.ruleForm.params = JSON.parse(this.ruleForm.params)
        if (this.ruleForm.exec_type === 'O') {
          this.once = true
          this.twice = false
          this.$set(this.ruleForm, 'timeDescription', [moment(this.ruleForm.execute_at).format('YYYY-MM-DD HH:mm:ss'), moment(this.ruleForm.execute_util).format('YYYY-MM-DD HH:mm:ss')])
        } else {
          this.once = false
          this.twice = true
          this.changeIntervalTypeBy()
          if (this.ruleForm.interval_type === 'month') {
            var start = this.ruleForm.execute_at.split(/[\s\n]/)
            var end = this.ruleForm.execute_util.split(/[\s\n]/)
            this.ruleForm.day_start = start[0]
            this.ruleForm.day_end = start[0]
            this.ruleForm.execute_at = start[1]
            this.ruleForm.execute_util = end[1]
          } else if (this.ruleForm.interval_type === 'year') {
            var start = this.ruleForm.execute_at.split(/[\s\n]/)
            var end = this.ruleForm.execute_util.split(/[\s\n]/)
            var start_mon = start[0].split('-')
            var end_mon = end[0].split('-')
            this.ruleForm.month_start = start_mon[0]
            this.ruleForm.month_end = end_mon[0]
            this.ruleForm.day_start = start_mon[1]
            this.ruleForm.day_end = end_mon[1]
            this.ruleForm.execute_at = start[1]
            this.ruleForm.execute_util = end[1]
          }
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped>
@import '../../../assets/styles/index.scss';
.labelSP {
  width: 100%;
  /deep/.el-form-item__content {
    width: 80%;
  }
}
.dark,
.light {
  .menuDialog /deep/.el-dialog__body {
    height: 650px !important;
    padding: 0 !important;
  }
  .el-menu {
    border-right: solid 1px rgba(66, 170, 211, 0.1) !important;
    .el-menu-item:hover,
    .el-menu-item:focus {
      // background:rgb(85, 136, 237)!important;
      background: rgba(85, 136, 237, 0.1);
    }
  }
}
.radio-box {
  display: inline-block;
  position: relative;
  height: 25px;
  line-height: 25px;
  margin-right: 5px;
}
.radio {
  display: inline-block;
  width: 25px;
  height: 25px;
  vertical-align: middle;
  cursor: pointer;
  background-image: url(../../../assets/gaojingzonglanicon/zengjia.png);
  background-repeat: no-repeat;
  background-position: 0 0;
}
.input-radio {
  display: inline-block;
  position: absolute;
  opacity: 0;
  width: 25px;
  height: 25px;
  cursor: pointer;
  left: 0px;
  outline: none;
  -webkit-appearance: none;
}
.on {
  background-position: -25px 0;
}
.operateBox {
  padding: 15px;
}
.showTableBox {
  margin-top: 20px;
  padding: 0px;
  .titleBox {
    padding: 10px;
    font-size: 14px;
    line-height: 36px;
  }
}

/deep/.el-dialog__body {
  // 弹框按钮样式
  /deep/.el-button--mini {
    padding: 7px 10px;
  }
}
.el-date-editor.el-input,
.el-date-editor.el-input__inner {
  width: 170px;
  // height: 32px;
  // line-height: 32px;
}
/deep/.el-dialog__body {
  .el-table {
    border: 1px solid rgba(66, 170, 211, 0.2);
    border-bottom: 1px solid rgba(66, 170, 211, 0);
  }
}
// 特殊表单样式
.rowStyleForm {
  border: 1px solid rgba(66, 170, 211, 0.2);
  padding: 10px;
  margin-bottom: 22px;
  // .el-dialog__body /deep/.el-form-item{
  //   margin-bottom: 0px;
  // }
}
.light .el-dialog__body .rowStyleForm .el-form-item {
  margin-bottom: 0px !important;
}
/deep/.el-select__input {
  border: none;
  outline: none;
  padding: 0;
  margin-left: 15px;
  color: #fff;
  font-size: 14px;
  appearance: none;
  height: 28px;
  background-color: transparent;
}
</style>
