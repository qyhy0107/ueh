<template>
  <div class="app-container">
    <div ref="OperateBox" class="operateBox">
      <el-form ref="queryForm" :model="queryParams" :inline="true" label-position="left">
        <el-form-item label="系统模块" prop="title">
          <el-select v-model="queryParams.title" placeholder="请选择系统模块" clearable filterable size="small" @change="handleQuery">
            <el-option v-for="dict in typeSelects" :key="dict" :label="dict" :value="dict" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作人员" prop="operName">
          <!-- <el-select v-model="queryParams.operName" placeholder="请选择操作人员" filterable clearable size="small" @change="handleQuery">
            <el-option v-for="item in userOptions" :key="item.user_id" :label="item.user_name" :value="item.user_name" />
          </el-select> -->
          <el-input v-model="queryParams.operName" placeholder="请输入操作人员" filterable clearable size="small" @change="handleQuery" />
        </el-form-item>
        <el-form-item label="发起主机" prop="operIp">
          <!-- <el-select v-model="queryParams.operName" placeholder="请选择操作人员" filterable clearable size="small" @change="handleQuery">
            <el-option v-for="item in userOptions" :key="item.user_id" :label="item.user_name" :value="item.user_name" />
          </el-select> -->
          <el-input v-model="queryParams.operIp" placeholder="请输入发起主机" filterable clearable size="small" @change="handleQuery" />
        </el-form-item>
        <el-form-item label="操作类型" prop="businessType">
          <el-select v-model="queryParams.businessType" placeholder="操作类型" clearable size="small" filterable @change="handleQuery">
            <el-option v-for="dict in typeOptions" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="操作状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="操作状态" clearable size="small" @change="handleQuery">
            <el-option v-for="dict in statusOptions" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue" />
          </el-select>
        </el-form-item> -->
        <el-form-item label="操作时间">
          <!-- <el-button :class="timecheck=='1h'?'timeButtonCheck':'timeButtonUncheck'" size="mini" v-on:click="initChart('1h')">1小时
          </el-button> -->
          <el-button :class="timecheck=='1d'?'timeButtonCheck':'timeButtonUncheck'" size="mini" @click="initChart('1d')">1天
          </el-button>
          <el-button :class="timecheck=='1w'?'timeButtonCheck':'timeButtonUncheck'" size="mini" @click="initChart('1w')">1周
          </el-button>
          <el-button :class="timecheck=='1m'?'timeButtonCheck':'timeButtonUncheck'" size="mini" @click="initChart('1m')">1月
          </el-button>
          <el-date-picker v-model="dateRange" size="small" value-format="yyyy-MM-dd" type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" style="width:58%" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="showTableBox" :style="{height:OperateBoxHeight+'px'}">
      <el-table v-if="list.length" v-loading="loading" :data="list" :height="'90%'" @selection-change="handleSelectionChange">
        <!-- <el-table-column type="selection" width="55" align="center" /> -->
        <template v-if="!loading" slot="empty" style="height:100%">
          <Deficiency width="30%" height="auto" />
        </template>
        <el-table-column label="操作人员" align="left" header-align="left" prop="operName" />
        <el-table-column label="访问模块" align="left" prop="title" />
        <el-table-column label="访问类型" align="left" prop="businessType" :formatter="typeFormat" />
        <el-table-column label="日期" align="left" prop="operTime">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.operTime) }}</span>
          </template>
        </el-table-column>
        <!-- <el-table-column label="请求方式" align="left" prop="requestMethod" /> -->
        <el-table-column label="操作状态" align="left" prop="status" :formatter="statusFormat" />
        <el-table-column label="操作参数" width="300" align="left" prop="operParam" :show-overflow-tooltip="true" />
        <el-table-column label="所在主机" align="left" prop="operIp" :show-overflow-tooltip="true" />
        <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button v-hasPermi="['monitor:operlog:query']" size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row,scope.index004)">详细
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total>0" :page-sizes="[10, 20, 50, 200]" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
      <!-- <div style="height: 50px"></div> -->
    </div>

    <!-- 操作日志详细 -->
    <el-dialog title="操作日志详细" :visible.sync="open" width="700px">
      <el-form ref="form" :model="form" label-width="100px" size="mini">
        <el-row>
          <el-col :span="12">
            <el-form-item label="操作模块：">{{ form.title }}/{{ form.businessTypes }}</el-form-item>
            <el-form-item label="登录信息：">{{ form.operName }} / {{ form.operIp }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="请求地址：">{{ form.operUrl }}</el-form-item>
            <el-form-item label="请求方式：">{{ form.requestMethod }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="操作方法：">{{ form.method }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="操作参数：">{{ form.operParam }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="返回参数：">{{ form.jsonResult }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作状态：">
              <div v-if="form.status === 0">正常</div>
              <div v-else-if="form.status === 1">失败</div>
              <div v-else-if="form.status === 2">告警</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作时间：">{{ parseTime(form.operTime) }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item v-if="form.status === 1" label="异常信息：">{{ form.errorMsg }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item v-if="form.status === 2" label="异常信息：">{{ form.errorMsg }}</el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="open = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  list,
  delOperlog,
  cleanOperlog,
  exportOperlog,
  getSelect
} from '@/api/monitor/operlog'
// import { getSysUserList } from '@/api/statisticalAnalysis/operLog'
import moment from 'moment'

import Deficiency from '@/components/Deficiency'

export default {
  components: { Deficiency },
  data() {
    return {
      // 页面高度
      OperateBoxHeight: 100,
      timecheck: '1d',
      // 遮罩层
      loading: true,
      // 所有用户
      userOptions: [],
      // 选中数组
      ids: [],
      // 非多个禁用
      multiple: true,
      // 总条数
      total: 0,
      // 表格数据
      list: [],
      // 是否显示弹出层
      open: false,
      // 类型数据字典
      typeOptions: [],
      // 时间
      startTime: '',
      endTime: '',
      // 类型数据字典
      statusOptions: [],
      typeSelects: [],
      // 日期范围
      dateRange: [],
      // 日期数组
      dateRangeData: [],
      // dataArray: [],
      // 表单参数
      form: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 50,
        title: undefined,
        operName: undefined,
        businessType: undefined,
        status: undefined,
        projectId: 10
      }
    }
  },
  created() {
    this.dateRangeData.push(
      moment().subtract(1, 'h').format('YYYY-MM-DD HH:mm:ss')
    )
    this.dateRangeData.push(moment().format('YYYY-MM-DD HH:mm:ss'))
    this.getList()
    this.getDicts('sys_oper_type').then((response) => {
      this.typeOptions = response.data
    })
    this.getDicts('sys_common_status').then((response) => {
      this.statusOptions = response.data
    })
    this.getSelectData()
    // this.getUserList()
    /* this.getSelectData();
    this.getUserList(); */
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
    // 获取用户标识下拉框
    // getUserList() {
    //   getSysUserList().then((response) => {
    //     this.userOptions = response.datas
    //   })
    // },
    getSelectData() {
      getSelect({ projectId: 10 }).then((response) => {
        // console.log(response)
        this.typeSelects = response.listData
      })
    },
    /** 日期与查询按钮操作 */
    initChart(flag) {
      if (flag) {
        this.timecheck = flag
      } else {
        this.timecheck = '1h'
      }
      if (flag === undefined) {
        if (this.dateRange == '' || this.dateRange == null) {
          this.endTime = moment().format('YYYY-MM-DD HH:mm:ss')
          this.startTime = moment()
            .subtract(1, 'h')
            .format('YYYY-MM-DD HH:mm:ss')
        } else {
          this.timecheck = ''
          const stTime = moment(this.dateRange[0]).format('YYYYMMDD')
          const enTime = moment(this.dateRange[1]).format('YYYYMMDD')
          // 查询时间不能超过6个月
          if (enTime - stTime > 180) {
            this.startTime = moment()
              .subtract(6, 'M')
              .format('YYYY-MM-DD 00:00:00')
            this.endTime = moment().format('YYYY-MM-DD 23:59:59')
          } else {
            this.startTime = moment(this.dateRange[0]).format(
              'YYYY-MM-DD 00:00:00'
            )
            this.endTime = moment(this.dateRange[1]).format(
              'YYYY-MM-DD 23:59:59'
            )
          }
        }
      } else {
        this.dateRangeData = []
        this.endTime = moment().format('YYYY-MM-DD 23:59:59')
        if (flag === '1h') {
          this.startTime = moment()
            .subtract(1, 'h')
            .format('YYYY-MM-DD HH:mm:ss')
          this.endTime = moment().format('YYYY-MM-DD HH:mm:ss')
        } else if (flag === '1d') {
          this.startTime = moment().format('YYYY-MM-DD 00:00:00')
        } else if (flag === '1w') {
          this.startTime = moment()
            .subtract(6, 'd')
            .format('YYYY-MM-DD 00:00:00')
        } else if (flag === '1m') {
          this.startTime = moment()
            .subtract(1, 'M')
            .format('YYYY-MM-DD 00:00:00')
        } else {
          this.startTime = moment()
            .subtract(1, 'h')
            .format('YYYY-MM-DD HH:mm:ss')
          this.endTime = moment().format('YYYY-MM-DD HH:mm:ss')
        }
      }
      this.dateRangeData.push(this.startTime)
      this.dateRangeData.push(this.endTime)
      this.getList()
    },
    /** 查询登录日志 */
    getList() {
      this.loading = true
      if (this.dateRangeData.length != 0) {
        list(this.addDateRange(this.queryParams, this.dateRangeData)).then(
          (response) => {
            this.list = response.rows
            this.total = response.total
            this.loading = false
            this.dateRangeData = []
          }
        )
      } else {
        list(this.addDateRange(this.queryParams, this.dateRange)).then(
          (response) => {
            this.list = response.rows
            this.total = response.total
            this.loading = false
          }
        )
      }
    },
    // 操作日志状态字典翻译
    statusFormat(row, column) {
      return this.selectDictLabel(this.statusOptions, row.status)
    },
    // 操作日志类型字典翻译
    typeFormat(row, column) {
      return this.selectDictLabel(this.typeOptions, row.businessType)
    },
    /** 查询按钮操作 */
    handleQuery() {
      this.timecheck = ''
      this.queryParams.pageNum = 1
      this.initChart()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = []
      this.queryParams.pageNum = 1
      this.initChart()
      // this.dateRangeData.push(moment().subtract(1, "h").format("YYYY-MM-DD HH:mm:ss"));
      // this.dateRangeData.push(moment().format("YYYY-MM-DD HH:mm:ss"));
      this.resetForm('queryForm')
      // this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.operId)
      this.multiple = !selection.length
    },
    /** 详细按钮操作 */
    handleView(row) {
      this.open = true
      row.businessTypes = this.selectDictLabel(
        this.typeOptions,
        row.businessType
      )
      this.form = row
    }
  }
}
</script>

