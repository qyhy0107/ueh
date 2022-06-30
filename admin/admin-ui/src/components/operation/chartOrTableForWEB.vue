<template>
  <div>
    <!--指标组menu-->
    <el-menu :default-active="active.menu" class="el-menu-demo" mode="horizontal" style="height:30px" @select="changeKpiGroup">
      <el-menu-item v-for="kpiGroupData in kpiGroup" :key="kpiGroupData.id" :index="kpiGroupData.id + ''">
        {{ kpiGroupData.labelText }}
      </el-menu-item>
    </el-menu>
    <div v-show="!performanceLoading" v-loading="performanceLoading" class="cpuBox">
      <el-form ref="form" label-width="90px">
        <el-form-item label="性能指标">
          <el-select v-model="selectedkeyOps" :popper-append-to-body="false" placeholder="请选择指标" filterable style="width: 260px" size="small">
            <el-option v-for="keyOpss in keyOps" :key="keyOpss.kpi_name" :label="keyOpss.label_text" :value="keyOpss.kpi_name" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围" style="width:100%">
          <el-col class="timebox">
            <el-button size="mini" :class="timecheck == '1h' ? 'timeButtonCheck' : 'timeButtonUncheck'" @click="selectCardDate('1h')">1小时</el-button>
            <el-button size="mini" :class="timecheck == '1d' ? 'timeButtonCheck' : 'timeButtonUncheck'" @click="selectCardDate('1d')">1天</el-button>
            <el-button size="mini" :class="timecheck == '1w' ? 'timeButtonCheck' : 'timeButtonUncheck'" @click="selectCardDate('1w')">1周</el-button>
            <el-button size="mini" style="margin-right: 10px" :class="timecheck == '1m' ? 'timeButtonCheck' : 'timeButtonUncheck'" @click="selectCardDate('1m')">1月</el-button>
            <el-date-picker v-model="dateSelected" size="small" style="width: 480px; margin-right: 10px" type="datetimerange" :default-time="['24:00:00']" unlink-panels range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" @change="selectCardDate()" />
            <el-button type="primary" size="mini" icon="el-icon-search" @click="selectCardDate(timecheck)">查询</el-button>
            <span style="float:right;padding-right:20px;">
              <el-tooltip v-if="selectedArr.indexOf('LINE')!==-1" class="item" effect="dark" content="点击切换为折线图" placement="top">
                <svg-icon
                  icon-class="line-chart(1)"
                  class="el-input__icon"
                  :style="{ color: selectedEct === 'line' ? '#1890ff' : '#c0d0d0' }"
                  @click="
                    selectedEct = 'line'
                    selectCardDate(timecheck)
                  "
                />
              </el-tooltip>
              <el-tooltip v-if="selectedArr.indexOf('BAR')!==-1" class="item" effect="dark" content="点击切换为柱状图" placement="top">
                <svg-icon
                  icon-class="barimage"
                  class="el-input__icon"
                  :style="{ color: selectedEct === 'bar' ? '#1890ff' : '#c0d0d0' }"
                  @click="
                    selectedEct = 'bar'
                    selectCardDate(timecheck)
                  "
                />
              </el-tooltip>
              <el-tooltip v-if="selectedArr.indexOf('TABLE')!==-1" class="item" effect="dark" content="点击切换为表格" placement="top">
                <svg-icon

                  icon-class="tableimage"
                  class="el-input__icon"
                  :style="{ color: selectedEct === 'table' ? '#1890ff' : '#c0d0d0' }"
                  @click="
                    selectedEct = 'table'
                    selectCardDate(timecheck)
                  "
                />
              </el-tooltip>
              <el-tooltip class="item" effect="dark" content="点击导出查询数据" placement="top">
                <svg-icon
                  icon-class="link"
                  class="el-input__icon"
                  style="color:#c0d0d0;"
                  @click="exportSelected"
                />
              </el-tooltip>
            </span>

          </el-col>
        </el-form-item>
      </el-form>
      <!--表格-->
      <div v-if="selectedEct == 'table'" v-loading="performanceLoading1" style="padding-bottom: 10px" element-loading-text="拼命加载中..." empty-text=" ">
        <el-table :data="perfDatas">
          <template v-if="!loading" slot="empty" style="height:100%">
            <Deficiency height="40%" width="auto" class="deficiency1" />
          </template>
          <el-table-column label="时间" prop="clock" header-align="left" align="left" :formatter="clockTime" />
          <el-table-column label="值" align="left" header-align="left" prop="value" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span v-if="keyOpsObj[selectedkeyOps].display_operator==='uptime'||keyOpsObj[selectedkeyOps].display_operator==='date'">{{ scope.row.value }}</span>
              <span v-else>{{ (scope.row.value )+" "+(keyOpsObj[selectedkeyOps].data_unit||"") }}</span>
            </template>
          </el-table-column>
          <el-table-column label="tag" prop="tag" header-align="left" align="left" :show-overflow-tooltip="true">
            <template slot-scope="scope">{{ scope.row.tag | isNaN }}</template>
          </el-table-column>
        </el-table>
        <pagination v-show="totalCount > 0" :limit.sync="queryParams.limit" :page.sync="queryParams.start" :total="totalCount" :page-sizes="[10, 20, 50, 200]" @pagination="getlist({ host: host, startTime, endTime })" />
      </div>
      <!--echarts图表-->
      <div v-else v-loading="performanceLoading1" element-loading-text="拼命加载中...">
        <time-line-chart v-if="perfDatas.length || performanceLoading1" :chart-ytitle="keyOpsObj[selectedkeyOps].data_unit" :chart-data="perfDatas" chart-height="400px" :window-width="ww" />
        <Deficiency v-else height="40%" width="auto" class="deficiency" />
      </div>
    </div>
  </div>
</template>

<script>
import Deficiency from '@/components/Deficiency/index'
import TimeLineChart from '@/views/dashboard/TimeLineChart'
import moment from 'moment'
import { getIsKeyDatas, getCustomKeyDatas, getKpiGroup, getCustomKeyDataList, exportPerformanceData } from '@/api/operation/server'
export default {
  components: {
    Deficiency,
    TimeLineChart
  },
  filters: {},
  props: {
    otherHost: {
      type: String,
      default: ''
    },
    // host值
    host: {
      type: String,
      default: null
    },
    classId: {
      type: Number,
      default: null
    },
    kpiGroup1: {
      type: Number,
      default: null
    },
    // 用于判断是否需要刷新数据
    // eslint-disable-next-line vue/require-prop-types
    ifChange: { default: null }
  },
  data() {
    return {
      loading: false,
      clickButton: 'hour',
      clickButtonAlarm: 'hour',
      exportParams: '',
      routerInfo: '',
      dateSelected: '',
      servers: [],
      ww: null,
      active: {
        tab: 'first', // 关键指标 性能详情 事件tab
        menu: '' // 性能详情 指标组menu
      },
      performanceLoading: true,
      performanceLoading1: false,
      // 已选kpi
      selectedkeyOps: '',
      keyOpsObj: {},
      // 指标列表
      keyOps: [],
      indexes: [],
      perfDatas: [],
      // 选中的echarts series type
      selectedEct: '',
      selectedArr: '',
      selectedkeyOpsLable: '',
      selected: {
        server: '',
        label_text: '',
        component: '',
        index: ''
      },
      // loading: '',
      fileList: [],
      see: false,
      ofsee: true,
      kpiGroup: [],
      startTime: '',
      endTime: '',
      show: 'line',
      timecheckType: '折线图',
      timecheck: '1h',
      totalCount: 0,
      orderType: 'asc',
      queryParams: {
        start: 1,
        limit: 10
        // orderType: 'asc'
      }
    }
  },

  watch: {
    ifChange: {
      handler(val, oldval) {
        this.KeyParams.start = 1
        this.resourceKeys()
      }
    },
    selectedEct: function(newValue, oldValue) {
      if (newValue === 'table') {
        this.orderType = 'desc'
      } else if (newValue === 'line' || newValue === 'bar') {
        this.orderType = 'asc'
      }
    },
    // 根据选择的指标切换对应的类型的echarts
    selectedkeyOps: function(newValue) {
      // 1获取echarts类型
      this.queryParams.start = 1
      const tmp = this.keyOps.filter((value) => {
        const { kpi_name } = value
        return kpi_name === newValue
      })
      const tmp1 = this.keyOps.filter((value) => {
        return value.kpi_name === newValue
      })
      // console.log(tmp1)
      if (tmp1.length) {
        this.selectedkeyOpsLable = tmp1[0].label_text
      }

      // 2设置echarts以及echarts选择框类型
      const [{ display_type }] = tmp
      this.selectedArr = display_type
      this.selectedEct = display_type.split('#')[0].toLowerCase()
      // this.selectCardDate('1h')
      this.selectCardDate(this.timecheck)
    }
  },
  created() {
    // 获取表单数据
    this.ww = window.screen.width
    this.getSecondTabDatas()
    this.formArm = { Acknowledged: '' }
  },
  activated() {},
  methods: {
    exportSelected() {
      // this.exportParams
      const queryParams = this.exportParams
      queryParams.lable_text = this.selectedkeyOpsLable
      queryParams.selectedTag = [this.exportParams.tag]
      // console.log(queryParams)
      // queryParams.eventIds = this.idSelections
      this.$confirm('是否确认导出所有数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(function() {
          return exportPerformanceData(queryParams)
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
    // 获取时间戳
    getUnixTimeStamp(flag) {
      let { startTime, endTime } = this.getTimeStamp(flag)
      startTime = moment(startTime).valueOf() / 1000
      endTime = moment(endTime).valueOf() / 1000
      return { startTime, endTime }
    },
    // 切换指标组
    changeKpiGroup(index, indexPath) {
      /* this.timecheck = "1h";
      this.dateSelected = "";
      this.selected.component = index;*/
      // ;
      // 1获取指标组下相应指标
      const classId = this.classId
      this.queryParams.start = 1
      getIsKeyDatas({
        projectId: 10,
        classId,
        kpiGroupId: index,
        isEnable: 'Y',
        engine: 'Freemarker'
      }).then((response) => {
        response.datas.forEach(item => {
          // [item.kpi_name] = item
          this.$set(this.keyOpsObj, item.kpi_name, item)
        })
        this.keyOps = response.datas
        this.selectedkeyOps = response.datas[0]['kpi_name']
        // 2.获取每个指标对应的数据
        // this.selectCardDate('1h')
      })
    },
    selectCardDate(flag) {
      this.queryParams.start = 1
      this.performanceLoading1 = true
      // 3获取对应指标下的性能数据
      if (flag) {
        // 传了flag,为固定日期
        var { startTime, endTime } = this.getUnixTimeStamp(flag)
        this.timecheck = flag
        this.dateSelected = ''
        this.startTime = startTime
        this.endTime = endTime
      } else if (this.dateSelected) {
        // 为选择日期
        // eslint-disable-next-line no-redeclare
        var startTime = this.dateSelected[0] / 1000
        // eslint-disable-next-line no-redeclare
        var endTime = this.dateSelected[1] / 1000
        this.timecheck = ''
        this.startTime = startTime
        this.endTime = endTime
      }
      const host = this.otherHost
      const tag = this.host
      this.exportParams = {
        kpi_name: this.selectedkeyOps,
        display_operator: this.keyOpsObj[this.selectedkeyOps].display_operator,
        display_offset_value: this.keyOpsObj[this.selectedkeyOps].display_offset_value,
        mappingValue: this.keyOpsObj[this.selectedkeyOps].mappingValue ? JSON.parse(this.keyOpsObj[this.selectedkeyOps].mappingValue) : null,
        host,
        tag,
        projectId: 10,
        engine: 'Freemarker',
        startTime: this.startTime,
        endTime: this.endTime
      }
      // 3.1封装图表数据
      if (this.selectedEct !== 'table') {
        // console.log(this.keyOpsObj[this.selectedkeyOps].mappingValue)
        getCustomKeyDatas({
          kpi_name: this.selectedkeyOps,
          display_operator: this.keyOpsObj[this.selectedkeyOps].display_operator,
          display_offset_value: this.keyOpsObj[this.selectedkeyOps].display_offset_value,
          mappingValue: this.keyOpsObj[this.selectedkeyOps].mappingValue ? JSON.parse(this.keyOpsObj[this.selectedkeyOps].mappingValue) : null,
          host,
          tag,
          projectId: 10,
          engine: 'Freemarker',
          startTime: this.startTime,
          endTime: this.endTime
        }).then((response) => {
          this.performanceLoading1 = false
          const keyDatas = response.datas
          this.perfDatas = []
          const perfDatas = new Map()
          if (keyDatas != null && keyDatas.length > 0) {
            // 3.1.1提取性能数据到临时变量 tag:图表data
            keyDatas.map((currentValue) => {
              if (!perfDatas.has(currentValue['tag'])) {
                perfDatas.set(currentValue['tag'], {
                  name: currentValue['tag'],
                  // classId: this.classId,
                  type: this.selectedEct,
                  showSymbol: false,
                  hoverAnimation: false,
                  smooth: true,
                  data: []
                })
              }
              perfDatas.get(currentValue['tag'])['data'].push([currentValue['clock'] * 1000, currentValue['value']])
            })
            // 3.1.2提取临时变量的值到图表data
            perfDatas.forEach((value) => {
              this.perfDatas.push(value)
            })
          }
        })
      } else {
        this.perfDatas = []
        // 3.2封装列表数据
        this.getlist({
          host,
          startTime: this.startTime,
          endTime: this.endTime

        })
      }
    },
    // 获取其他性能指标组，指标组下面的指标以及数据
    getSecondTabDatas() {
      // 1获取指标组
      const classId = this.classId
      getKpiGroup({
        projectId: 10,
        classId,
        isEnable: 'Y',
        engine: 'Freemarker'
      }).then((response) => {
        // console.log(response)
        // 2获取对应指标组下面的指标
        // console.log(response)
        this.performanceLoading = false
        if (this.kpiGroup1) {
          this.kpiGroup = response.datas.filter(item => item.id === this.kpiGroup1)
        } else {
          this.kpiGroup = response.datas
        }
        this.active.menu = this.kpiGroup[0]['id'] + ''
        const classId = this.classId
        // console.log(this.active.menu,11)
        getIsKeyDatas({
          projectId: 10,
          classId,
          kpiGroupId: this.active.menu,
          isEnable: 'Y',
          engine: 'Freemarker'
        }).then((response) => {
          // console.log(response)
          response.datas.forEach(item => {
            this.$set(this.keyOpsObj, item.kpi_name, item)
          })
          this.keyOps = response.datas
          this.selectedkeyOps = response.datas[0]['kpi_name'] || ''
          // 3获取对应指标下的性能数据
          // this.selectCardDate('1h')
          this.selectCardDate(this.timecheck)
        })
      })
    },
    // 获取表格
    getlist(params) {
      const { startTime, endTime } = params
      const tag = this.host
      const host = this.otherHost
      this.performanceLoading1 = true
      this.perfDatas = []
      // console.log(
      //   {
      //     kpi_name: this.selectedkeyOps,
      //     display_operator: this.keyOpsObj[this.selectedkeyOps].display_operator,
      //     display_offset_value: this.keyOpsObj[this.selectedkeyOps].display_offset_value,
      //     mappingValue: this.keyOpsObj[this.selectedkeyOps].mappingValue ? JSON.parse(this.keyOpsObj[this.selectedkeyOps].mappingValue) : null,
      //     host,
      //     tag,
      //     projectId: 10,
      //     startTime,
      //     endTime,
      //     orderType: this.orderType,
      //     start: this.queryParams.start,
      //     limit: this.queryParams.limit
      //   }
      // )
      getCustomKeyDataList({
        kpi_name: this.selectedkeyOps,
        display_operator: this.keyOpsObj[this.selectedkeyOps].display_operator,
        display_offset_value: this.keyOpsObj[this.selectedkeyOps].display_offset_value,
        mappingValue: this.keyOpsObj[this.selectedkeyOps].mappingValue ? JSON.parse(this.keyOpsObj[this.selectedkeyOps].mappingValue) : null,
        host,
        tag,
        projectId: 10,
        startTime,
        endTime,
        orderType: this.orderType,
        start: this.queryParams.start,
        limit: this.queryParams.limit
      }).then((response) => {
        this.perfDatas = response.datas
        this.totalCount = response.totalCount
        this.performanceLoading1 = false
      })
    },
    getTimeStamp(flag) {
      let endTime = moment().format('YYYY-MM-DD HH:mm:ss')
      let startTime = moment()
        .subtract(1, 'h')
        .format('YYYY-MM-DD HH:mm:ss')
      if (flag === '1h') {
        startTime = moment()
          .subtract(1, 'h')
          .format('YYYY-MM-DD HH:mm:ss')
      } else if (flag === '1d') {
        startTime = moment().format('YYYY-MM-DD 00:00:00')
        endTime = moment().format('YYYY-MM-DD 23:59:59')
      } else if (flag === '1w') {
        startTime = moment()
          .subtract(6, 'd')
          .format('YYYY-MM-DD 00:00:00')
        endTime = moment().format('YYYY-MM-DD 23:59:59')
      } else if (flag === '1m') {
        startTime = moment()
          .subtract(1, 'M')
          .format('YYYY-MM-DD 00:00:00')
        endTime = moment().format('YYYY-MM-DD 23:59:59')
      }
      return { startTime, endTime }
    },
    // 时间戳转换为日期
    clockTime(row) {
      return moment(row.clock * 1000).format('YYYY-MM-DD HH:mm:ss')
    }
  }
}
</script>
<style lang="scss" scoped>
$bgMainColor: rgba(42, 47, 57, 1);
$MainText: rgba(192, 208, 208, 1);
$MainBlue: rgba(85, 136, 237, 1);
$white: white;
/deep/.el-table__empty-text {
  height: 100%;
}
/deep/.el-table--medium th,
/deep/.el-table--medium td {
  padding: 5px 0;
}
/deep/.el-form-item {
  margin-bottom: 0px;
}
/deep/.el-form-item:last-child {
  margin-bottom: 18px;
}
.light {
  .el-form-item:first-child {
    img {
      vertical-align: middle;
    }
    .spanbg1 {
      vertical-align: middle;
      padding: 5px 10px;
      color: rgba(70, 188, 116, 1);
      font-size: 10px;
      background: rgba(59, 216, 53, 0.2);
      border-radius: 15px;
    }
    .spanbg2 {
      vertical-align: middle;
      padding: 5px 10px;
      color: rgba(221, 144, 20, 1);
      font-size: 10px;
      background: rgba(255, 193, 10, 0.2);
      border-radius: 15px;
    }
    .spanbg3 {
      vertical-align: middle;
      padding: 5px 10px;
      color: rgba(255, 85, 6, 1);
      font-size: 10px;
      background: rgba(255, 85, 6, 0.2);
      border-radius: 15px;
    }
  }
  .btnBox {
    background-color: #fff !important;
    padding: 15px;
    font-size: 12px;
    color: #333;
    .infoBox {
      background: rgba(24, 144, 255, 0.07);
      padding: 0px 15px;
      padding-left: 5px;
      margin-top: 10px;
      .el-form-item {
        font-size: 12px;
        position: relative;
        padding: 0 5px;
        margin-right: 0px;
      }
      .el-form-item::after {
        content: '';
        position: absolute;
        height: 30%;
        width: 1px;
        background: rgba(0, 71, 79, 0.2);
        right: 0;
        top: 50%;
        transform: translateY(-50%);
      }
      .el-form-item:last-child::after,
      .el-form-item:first-child::after {
        content: '';
        position: absolute;
        height: 30%;
        width: 1px;
        background: rgba(0, 71, 79, 0);
        right: 0;
        top: 50%;
        transform: translateY(-50%);
      }
      .el-form-item:first-child {
        .spanbg1 {
          padding: 5px 10px;
          color: rgba(70, 188, 116, 1);
          font-size: 10px;
          background: rgba(59, 216, 53, 0.2);
          border-radius: 15px;
        }
        .spanbg2 {
          padding: 5px 10px;
          color: rgba(221, 144, 20, 1);
          font-size: 10px;
          background: rgba(255, 193, 10, 0.2);
          border-radius: 15px;
        }
        .spanbg3 {
          padding: 5px 10px;
          color: rgba(255, 85, 6, 1);
          font-size: 10px;
          background: rgba(255, 85, 6, 0.2);
          border-radius: 15px;
        }
      }
    }
    .el-form-item {
      margin-bottom: 0px;
    }
    .typeShow {
      margin-top: 6px;
      box-shadow: inset 1px 1px 2px 1px rgba(255, 255, 255, 0.5);
    }
  }
  .linkBox {
    .right {
      .el-button {
        color: #fafafa;
      }
    }
  }
  .tableBox {
    margin-top: 10px;
    border-radius: 2px;
    .el-tabs {
      height: 100%;
    }
    /deep/.el-tabs__content {
      padding: 0px;
      overflow: auto;
      .el-menu-item {
        height: 30px;
        line-height: 30px;
      }
    }

    /deep/.el-menu {
      //   margin-bottom: 15px;
      margin: 5px 0;
      background: transparent;
      // color: #f8f8f8 !important;
    }

    .cpuBox {
      .cardContent {
        height: 270px;
      }

      /deep/ .el-form-item__label {
        color: #f8f8f8;
      }
    }
  }

  .el-form-item__label {
    color: #f8f8f8;
  }

  .iconBox {
    text-align: right;
    padding: 10px;

    .c-icon {
      width: 36px;
      height: 36px;
      fill: currentColor;
      -webkit-transition: 0.2s;
      transition: 0.2s;
    }

    .c-link--twitter:hover {
      color: #1890ff;
    }
    .c-link--twitter:hover::after {
      background: #1890ff;
    }
    .c-link {
      position: relative;
      // color: #fff;
    }
    .c-link:not(:last-of-type) {
      margin-right: 15px;
    }
    .c-tooltip::before,
    .c-tooltip::after {
      display: block;
      position: absolute;
      left: 50%;
      -webkit-transform: translate(-50%, -10px);
      transform: translate(-50%, -10px);
      opacity: 0;
      -webkit-transition: 0.2s;
      transition: 0.2s;
    }
    .c-tooltip::before {
      content: '';
      bottom: calc(100% + 2px);
      border: solid 5px transparent;
      border-top-color: currentColor;
    }
    .c-tooltip::after {
      content: attr(aria-label);
      bottom: calc(100% + 12px);
      padding: 0.61em 0.93em;
      font-size: 0.875rem;
      color: white;
      border-radius: 3px;
    }
    .c-tooltip:hover::before,
    .c-tooltip:hover::after {
      opacity: 1;
      -webkit-transform: translate(-50%);
      transform: translate(-50%);
    }
  }
  .tableBox ul {
    border-bottom: none;
  }
  .tableBox li.el-menu-item {
    background-color: transparent !important;
    // border: 1px solid #cecdcd !important;
    color: #333 !important;
    margin-right: -1px;
    // margin-right:-1px;
  }
  .tableBox li.el-menu-item.is-active {
    background-color: transparent !important;
    // border-bottom: 1px solid rgba(24, 144, 255, 1) !important;
    border-bottom: 1px solid transparent !important;
    color: rgba(24, 144, 255, 1) !important;
    position: relative;
    margin-right: -1px;
    z-index: 999;
  }
  .tableBox li.el-menu-item.is-active::after {
    content: '';
    height: 100%;
    width: 1px;
    // background: rgba(24, 144, 255, 1);
    position: absolute;
    top: 0;
    right: -1px;
  }
}
.dark {
  .el-form-item:first-child {
    img {
      line-height: 100%;
      vertical-align: middle;
    }
    .spanbg1 {
      padding: 5px 10px;
      color: rgba(30, 241, 112, 1);
      font-size: 10px;
      background: rgba(30, 241, 112, 0.2);
      border-radius: 15px;
      vertical-align: middle;
    }
    .spanbg2 {
      padding: 5px 10px;
      color: rgba(255, 170, 10, 1);
      font-size: 10px;
      background: rgba(241, 166, 30, 0.2);
      border-radius: 15px;
      vertical-align: middle;
    }
    .spanbg3 {
      padding: 5px 10px;
      color: rgba(241, 30, 30, 1);
      font-size: 10px;
      background: rgba(241, 30, 30, 0.2);
      border-radius: 15px;
      vertical-align: middle;
    }
  }
  .btnBox {
    background-color: $bgMainColor !important;
    padding: 15px;
    font-size: 12px;
    color: $MainText;
    .infoBox {
      background: rgba($color: $white, $alpha: 0.1);
      padding: 0px 15px;
      padding-left: 5px;
      margin-top: 10px;
      .el-form-item {
        font-size: 12px;
        position: relative;
        padding: 0 5px;
        margin-right: 0px;
      }
      .el-form-item::after {
        content: '';
        position: absolute;
        height: 30%;
        width: 1px;
        background: rgba(0, 71, 79, 0.2);
        right: 0;
        top: 50%;
        transform: translateY(-50%);
      }
      .el-form-item:last-child::after,
      .el-form-item:first-child::after {
        content: '';
        position: absolute;
        height: 30%;
        width: 1px;
        background: rgba(0, 71, 79, 0);
        right: 0;
        top: 50%;
        transform: translateY(-50%);
      }
      .el-form-item:first-child {
        .spanbg1 {
          padding: 5px 10px;
          color: rgba(30, 241, 112, 1);
          font-size: 10px;
          background: rgba(30, 241, 112, 0.2);
          border-radius: 15px;
        }
        .spanbg2 {
          padding: 5px 10px;
          color: rgba(255, 170, 10, 1);
          font-size: 10px;
          background: rgba(241, 166, 30, 0.2);
          border-radius: 15px;
        }
        .spanbg3 {
          padding: 5px 10px;
          color: rgba(241, 30, 30, 1);
          font-size: 10px;
          background: rgba(241, 30, 30, 0.2);
          border-radius: 15px;
        }
      }
    }
    .el-form-item {
      margin-bottom: 0px;
    }
    .typeShow {
      margin-top: 6px;
      box-shadow: inset 1px 1px 2px 1px rgba(255, 255, 255, 0.5);
    }
  }
  .linkBox {
    .right {
      .el-button {
        color: $MainText;
      }
    }
  }
  .tableBox {
    margin-top: 10px;
    border-radius: 2px;
    .el-tabs {
      height: 100%;
    }
    /deep/.el-tabs__content {
      overflow: auto;
      padding: 0px;

      .el-menu-item {
        height: 30px;
        line-height: 30px;
      }
    }

    /deep/ .el-menu {
      //   margin-bottom: 15px;
      margin: 5px 0;
      background: transparent;
    }

    .cpuBox {
      .cardContent {
        height: 270px;
      }

      /deep/ .el-form-item__label {
        color: $MainText;
      }
    }
  }

  .el-form-item__label {
    color: $MainText;
  }

  .iconBox {
    text-align: right;
    padding: 10px;

    .c-icon {
      width: 36px;
      height: 36px;
      fill: currentColor;
      -webkit-transition: 0.2s;
      transition: 0.2s;
    }

    .c-link--twitter:hover {
      color: $MainBlue;
    }
    .c-link--twitter:hover::after {
      background: $MainBlue;
    }
    .c-link {
      position: relative;
    }
    .c-link:not(:last-of-type) {
      margin-right: 15px;
    }
    .c-tooltip::before,
    .c-tooltip::after {
      display: block;
      position: absolute;
      left: 50%;
      -webkit-transform: translate(-50%, -10px);
      transform: translate(-50%, -10px);
      opacity: 0;
      -webkit-transition: 0.2s;
      transition: 0.2s;
    }
    .c-tooltip::before {
      content: '';
      bottom: calc(100% + 2px);
      border: solid 5px transparent;
      border-top-color: currentColor;
    }
    .c-tooltip::after {
      content: attr(aria-label);
      bottom: calc(100% + 12px);
      padding: 0.61em 0.93em;
      font-size: 0.875rem;
      color: $MainText;
      border-radius: 3px;
    }
    .c-tooltip:hover::before,
    .c-tooltip:hover::after {
      opacity: 1;
      -webkit-transform: translate(-50%);
      transform: translate(-50%);
    }
  }
  .tableBox ul {
    border: none;
  }
  .tableBox li.el-menu-item {
    background-color: transparent !important;
    // border-bottom: 1px solid rgba($color: $MainText, $alpha: 0.5) !important;
    color: $MainText !important;
    margin-right: -1px;
    // margin-right:-1px;
  }
  .tableBox li.el-menu-item.is-active {
    background-color: transparent !important;
    // border-bottom: 1px solid $MainBlue !important;
    border-bottom: 1px solid transparent !important;
    color: $MainBlue !important;
    position: relative;
    margin-right: -1px;
    z-index: 999;
  }
  .tableBox li.el-menu-item.is-active::after {
    content: '';
    height: 100%;
    width: 1px;
    // background: $MainBlue;
    position: absolute;
    top: 0;
    right: -1px;
  }
}
.dark,
.light {
  .el-table {
    height: 385px;
  }
  .deficiency {
    height: 385px;
  }
  .deficiency1 {
    height: 370px;
  }
}
// .el-form-item.el-form-item--medium
@media screen and (max-width: 1400px) {
  .dark,
  .light {
    .el-table {
      height: 250px;
      // overflow: hidden;
      .el-table__body-wrapper {
        overflow-y: auto;
        overflow-x: hidden;
        position: relative;
        height: 90%;
      }
    }
    .deficiency {
      height: 250px;
    }
    .deficiency1 {
      height: 220px;
    }
    .el-form-item.el-form-item--medium {
      display: inline-block;
    }
    .el-form-item:last-child {
      margin-bottom: 0;
    }
    .pagination-container {
      // height:30px;
      // line-height:30px;
      margin: 0;

      /deep/.el-pagination {
        padding: 0;
        padding-right: 15px;
        button {
          font-size: 10px;
          margin-top: 2px;
          height: 24px !important;
          line-height: 26px !important;
        }
        .el-pager li {
          margin-top: 2px;
          font-size: 10px;
          height: 24px;
          line-height: 24px;
        }
        .el-pagination__total,
        .el-pagination__jump {
          font-size: 10px !important;
          margin-left: 12px !important;
        }
      }
    }
  }
}
</style>
