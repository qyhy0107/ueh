<template>
  <div ref="alarmfullbox" :class="{ 'app-container': true, 'app-container1': isFullscreen }" @dblclick="getFullSreen">
    <div v-if="isFullscreen" class="lineBox-title1">数据中心告警总览</div>
    <div :class="{ specialBox: true, specialBox1: isFullscreen }">
      <div class="main-box box-card">
        <div class="lineBox-title" @click="turnToRouter('实时告警')">告警确认（{{ hours }}h）</div>
        <div v-loading="loading1" class="oneBox">
          <el-row>
            <el-col :span="6" class="allTitle">类别</el-col>
            <el-col :span="16" class="allTitle">
              未确认/总数
            </el-col>
          </el-row>
          <el-row :style="{ marginBottom: isFullscreen ? '10px' : 0 }">
            <el-col :span="6" class="allText"><img src="../../../assets/imgforindex/1.png" height="12px">合计</el-col>
            <el-col :span="16" class="allNumber">
              <span>{{ confirmAll }}</span>
              <span>/</span>
              <span>{{ totalAlarm }}</span>
            </el-col>
          </el-row>
          <el-row :style="{ marginBottom: isFullscreen ? '10px' : 0 }">
            <el-col :span="6" class="allText"><img src="../../../assets/imgforindex/5.png" height="12px">灾难</el-col>
            <el-col :span="16" class="allNumber">
              <span>{{ TypeAlarm[5].unAcknowledged || 0 }}</span>
              <span>/</span>
              <span>{{ TypeAlarm[5].total || 0 }}</span>
            </el-col>
          </el-row>
          <el-row :style="{ marginBottom: isFullscreen ? '10px' : 0 }">
            <el-col :span="6" class="allText"><img src="../../../assets/imgforindex/4.png" height="12px">严重</el-col>
            <el-col :span="16" class="allNumber">
              <span>{{ TypeAlarm[4].unAcknowledged || 0 }}</span>
              <span>/</span>
              <span>{{ TypeAlarm[4].total || 0 }}</span>
            </el-col>
          </el-row>
          <el-row :style="{ marginBottom: isFullscreen ? '10px' : 0 }">
            <el-col :span="6" class="allText"><img src="../../../assets/imgforindex/3.png" height="12px">问题</el-col>
            <el-col :span="16" class="allNumber">
              <span>{{ TypeAlarm[3].unAcknowledged || 0 }}</span>
              <span>/</span>
              <span>{{ TypeAlarm[3].total || 0 }}</span>
            </el-col>
          </el-row>
          <el-row :style="{ marginBottom: isFullscreen ? '10px' : 0 }">
            <el-col :span="6" class="allText"><img src="../../../assets/imgforindex/2.png" height="12px">警告</el-col>
            <el-col :span="16" class="allNumber">
              <span>{{ TypeAlarm[2].unAcknowledged || 0 }}</span>
              <span>/</span>
              <span>{{ TypeAlarm[2].total || 0 }}</span>
            </el-col>
          </el-row>
          <el-row :style="{ marginBottom: isFullscreen ? '10px' : 0 }">
            <el-col :span="6" class="allText"><img src="../../../assets/imgforindex/1.png" height="12px">通知</el-col>
            <el-col :span="16" class="allNumber">
              <span>{{ TypeAlarm[1].unAcknowledged || 0 }}</span>
              <span>/</span>
              <span>{{ TypeAlarm[1].total || 0 }}</span>
            </el-col>
          </el-row>
          <el-row :style="{ marginBottom: isFullscreen ? '10px' : 0 }">
            <el-col :span="6" class="allText"><img src="../../../assets/imgforindex/1.png" height="12px">未知</el-col>
            <el-col :span="16" class="allNumber">
              <span>{{ TypeAlarm[0].unAcknowledged || 0 }}</span>
              <span>/</span>
              <span>{{ TypeAlarm[0].total || 0 }}</span>
            </el-col>
          </el-row>
        </div>
      </div>
      <div class="main-box box-card">
        <div class="lineBox-title" @click="turnToRouter('实时告警')">告警级别分布（{{ hours }}h）</div>
        <div v-if="loading2" class="twoBox">
          <alarmBarAndPieChart :alarm-type-arr="alarmTypeArr" :height="OperateBoxHeight + 'px'" />
        </div>
        <div v-else style="text-align:center">
          <img src="../../../assets/common/nonedata6.png" alt="" width="100px" style="margin-top:13%">
          <div style="margin-top:10px">无告警发生，运行正常</div>
        </div>
      </div>
      <div class="main-box box-card">
        <div class="lineBox-title" @click="turnToRouter('设备管理')">告警设备分布（{{ hours }}h）</div>
        <div v-show="categoryArr.length" class="threeBox">
          <alarmPieChart :alarm-type-arr="categoryArr" :category-arr-x="categoryArrX" :height="OperateBoxHeight + 'px'" />
        </div>
        <div v-show="!categoryArr.length" class="threeBox" style="text-align:center">
          <!-- <alarmPieChart :alarm-type-arr="categoryArr" :category-arr-x="categoryArrX" :height="OperateBoxHeight + 'px'" /> -->
          <img src="../../../assets/common/nonedata6.png" alt="" width="100px" style="margin-top:25%">
          <div style="margin-top:10px">无告警发生，运行正常</div>
        </div>
      </div>
      <div class="main-box box-card">
        <div class="lineBox-title" @click="turnToRouter('历史告警')">历史告警（{{ monthsDay }}天）</div>
        <div v-loading="loading4" class="fourBox">
          <alarmLineChart :alarm-arr="seriesArr" :alarm-arr-x="seriesArrX" :height="OperateBoxHeight + 'px'" />
        </div>
      </div>
      <div class="main-box box-card">
        <div class="lineBox-title" @click="turnToRouter('实时告警')">当前告警（{{ hours }}h）</div>
        <div class="fiveBox">
          <el-timeline v-if="activities.length">
            <el-timeline-item size="large" placement="top">
              <div class="timer">时间</div>
              <div class="content">
                <el-row>
                  <el-col :span="5">
                    主机
                  </el-col>
                  <el-col :span="10" :offset="1">
                    问题•严重性
                  </el-col>
                  <el-col :span="3" :offset="1">
                    持续时长
                  </el-col>
                  <el-col :span="2">
                    事件类型
                  </el-col>
                  <el-col :span="1">
                    确认
                  </el-col>
                </el-row>
              </div>
            </el-timeline-item>
            <el-timeline-item v-for="(activity, index) in activities" :key="index" icon="el-icon-warning-outline" :color="activity.Severity" size="large" placement="top">
              <div class="timer">{{ activity.FirstOccurrence | getDate }}</div>
              <div class="content ">
                <el-row>
                  <el-col :span="5">
                    <span style=" cursor: pointer;" @click="turnToRouter1(activity)">{{ activity.NodeAlias }}</span>
                  </el-col>
                  <el-col v-if="$store.state.settings.themeClass === 'dark'" :span="10" :offset="1" :style="{ background: activity.Severity + '4d' }" class="contentBox ">
                    {{ activity.Summary }}
                  </el-col>
                  <el-col v-else :span="10" :offset="1" :style="{ background: activity.Severity }" class="contentBox ">
                    {{ activity.Summary }}
                  </el-col>
                  <!-- <el-col v-if="activity.EventType === 'P'" :span="3" :offset="1">-</el-col> -->
                  <el-col v-if="activity.RecoveredStatus == 1" :span="3" :offset="1">
                    {{ (momentDate - activity.FirstOccurrence) | getdiffLongTime1 }}
                  </el-col>
                  <el-col v-else-if="activity.RecoveredStatus == 2" :span="3" :offset="1">
                    {{ (activity.LastOccurrence - activity.FirstOccurrence) | getdiffLongTime1 }}
                  </el-col>
                  <!-- <el-col v-else :span="3" :offset="1">
                    {{ (momentDate - activity.FirstOccurrence) | getdiffLongTime1 }}
                  </el-col> -->
                  <el-col :span="2">
                    <!-- {{ activity.EventSeverityType }} -->
                    <span v-if="activity.EventSeverityType == 2" style="color:rgb(38 185 95)">恢复事件</span>
                    <span v-else>告警事件</span>
                  </el-col>
                  <el-col :span="1">
                    <!-- {{ activity.Acknowledged }} -->
                    <span v-if="activity.Acknowledged" style="color:rgb(38 185 95)">已确认</span>
                    <span v-else>未确认</span>
                  </el-col>
                </el-row>
              </div>
            </el-timeline-item>
          </el-timeline>
          <div v-else style="text-align:center">
            <img src="../../../assets/common/nonedata10.png" alt="" width="200px" style="margin-top:10%">
            <div style="margin-top:10px">无告警发生，运行正常</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import alarmBarAndPieChart from '@/views/dashboard/alarmBarAndPieChart'
import alarmPieChart from '@/views/dashboard/alarmPieChart'
import screenfull from 'screenfull'
import { alarmTurnToHost } from '@/api/event/event-manage'
import alarmLineChart from '@/views/dashboard/alarmLineChart'
import { isTurnHost, turnHost, getMonthsAlarmInfo, getAlarmInfoTable, getAllAlarmCount, getConfirmAll, getSixTypeCount, getAlarmTypeCount, getAlarmCategoryCount, getAlarmTimeToSet } from '@/api/operation/alarmOverview'
import moment from 'moment'
export default {
  components: {
    alarmBarAndPieChart,
    alarmPieChart,
    alarmLineChart
  },
  data() {
    return {
      isFullscreen: false,
      arr2: [],
      hours: 1,
      monthsDay: 30,
      loading1: false,
      loading2: false,
      loading3: false,
      loading4: false,
      loading5: false,
      OperateBoxHeight: 240, // 图表高度
      activities: [],
      // linedata: ,
      seriesArr: [],
      categoryArr: [],
      seriesArrX: [],
      categoryArrX: [],
      alarmTypeArr: [{ value: 0, name: '未知' }, { value: 0, name: '通知' }, { value: 0, name: '警告' }, { value: 0, name: '问题' }, { value: 0, name: '严重' }, { value: 0, name: '灾难' }],
      TypeAlarm: {
        0: { total: 0, unAcknowledged: 0 },
        1: { total: 0, unAcknowledged: 0 },
        2: { total: 0, unAcknowledged: 0 },
        3: { total: 0, unAcknowledged: 0 },
        4: { total: 0, unAcknowledged: 0 },
        5: { total: 0, unAcknowledged: 0 }
      },
      confirmAll: 0,
      totalAlarm: 0,
      momentDate: moment().format('x'),
      nowDate: moment()
        .subtract(this.hours, 'hours')
        .format('x'),
      nowDate1: moment()
        .subtract(this.monthsDay, 'days')
        .format('x')
    }
  },
  watch: {
    hours: {
      handler(val, oldval) {
        this.getTimer()
      }
    },
    monthsDay: {
      handler(val, oldval) {
        this.getMonthsAlarmInfo()
      }
    }
  },
  created() {
    // this.getOperateBoxHeight()
    // this.getAllAlarmCount()
    // this.getConfirmAll()
    // this.getSixTypeCount()
    // this.getAlarmTypeCount()
    // this.getAlarmCategoryCount()
    // this.getAlarmInfoTable()
    // this.getMonthsAlarmInfo()
    this.getAlarmTimeToSet()
    this.getTimer()
    this.getMonthsAlarmInfo()
  },
  updated() {
    this.getOperateBoxHeight()
  },
  mounted() {
    // setTimeout(function() {
    this.init()
    this.getOperateBoxHeight()
    this.timer = setInterval(this.getTimer, 60000)
    // }, 2000)
  },
  beforeDestroy() {
    this.destroy()
    clearInterval(this.timer)
  },
  methods: {
    getAlarmTimeToSet() {
      getAlarmTimeToSet({
        label: '告警时间范围',
        projectId: 10,
        nowDate: this.nowDate,
        engine: 'Freemarker'
      }).then((response) => {
        // console.log(response)
        if (response.success && response.strData) {
          this.hours = +response.strData
        } else {
          this.hours = 1
        }
      })
      getAlarmTimeToSet({
        label: '历史趋势时间范围',
        projectId: 10,
        nowDate: this.nowDate,
        engine: 'Freemarker'
      }).then((response) => {
        // console.log(response)
        if (response.success && response.strData) {
          this.monthsDay = +response.strData
        } else {
          this.monthsDay = 30
        }
      })
    },
    getFullSreen(e) {
      // console.log(1)
      if (!screenfull.enabled) {
        this.$message({
          message: 'you browser can not work',
          type: 'warning'
        })
        return false
      }
      // screenfull.request(this.$refs.haha)
      screenfull.toggle(this.$refs.alarmfullbox)
    },
    change() {
      // console.log(this.isFullscreen)
      this.isFullscreen = screenfull.isFullscreen
      // console.log(this.isFullscreen)
    },
    init() {
      if (screenfull.enabled) {
        screenfull.on('change', this.change)
      }
    },
    destroy() {
      if (screenfull.enabled) {
        screenfull.off('change', this.change)
      }
    },
    turnToRouter1(router) {
      // const query = {}
      // console.log(router)
      // return
      const userStr = localStorage.getItem('user')
      const userObj = JSON.parse(userStr)
      const userId = userObj['userId']
      const roleId = userObj['roleId']
      // 如果是虚拟化的告警
      if (router.EventCategoryLabelText === '虚拟机') {
        const query = {
          name: {
            classId: 265,
            host: router.Node,
            host_name: router.NodeAlias
          }
        }
        let tempPath = ''
        switch (router.AlertGroup) {
          case 'VMWare_VCenter_Vpoller_Hypervisor':
            // localStorage.setItem('HypervisorHost', JSON.stringify({ name: query }))
            tempPath = '虚拟化-宿主机'
            break
          case 'VMWare_VCenter_Vpoller_Virtual-Machine':
            // localStorage.setItem('VMHost', JSON.stringify({ name: query }))
            tempPath = '虚拟化-VMware'
            break
          case 'VMWare_VCenter_Vpoller_Datastore':
            // localStorage.setItem('DatastoreHost', JSON.stringify({ name: query }))
            tempPath = '虚拟化-Datastore'
            break
        }
        if (tempPath) {
          this.$router.push({ name: tempPath, params: query || null })
        } else {
          this.msgError('设备未被纳管!')
        }
        return
      } else if (router.EventCategoryLabelText === 'WEB检测' || router.EventCategoryLabelText === 'PING检测') {
        // const proxy = router.Summary.split(':')[0].replace('发起设备', '') || null
        alarmTurnToHost({
          projectId: 10,
          proxy: router.HostOrProxy,
          Node: router.Node,
          className: router.EventCategoryLabelText,
          engine: 'Freemarker'
        }).then((response) => {
          const query = {
            name: {
              id: response.datas[0].id,
              clabel_text: response.datas[0].clabel_text,
              classId: response.datas[0].classId,
              host: router.Node,
              label_text: response.datas[0].label_text,
              res_ip_address: router.HostOrProxy
            }
          }
          this.$router.push({ name: '检测通用详情页', params: query })
        })
      } else if (router.EventCategoryLabelText === '进程检测') {
        const proxy = router.Summary.split(':')[0].replace('发起设备', '') || null
        alarmTurnToHost({
          projectId: 10,
          proxy,
          Node: router.Node,
          className: router.EventCategoryLabelText,
          engine: 'Freemarker'
        }).then((response) => {
          const query = {
            name: {
              id: response.datas[0].id,
              clabel_text: response.datas[0].clabel_text,
              classId: response.datas[0].classId,
              host: router.Node,
              label_text: response.datas[0].label_text,
              res_ip_address: proxy
            }
          }
          this.$router.push({ name: '检测通用详情页', params: query })
        })

        // }
      } else {
        isTurnHost({
          projectId: 10,
          host: router.Node,
          userId,
          roleId,
          engine: 'Freemarker'
        }).then((response) => {
          // console.log(response)
          if (response.datas[0].isTurn || !roleId) {
            turnHost({
              projectId: 10,
              host: router.Node,
              userId,
              roleId,
              engine: 'Freemarker'
            }).then((response) => {
              // console.log(response)
              if (response.datas.length) {
                const query = { name: response.datas[0] }
                let tempPath = this.toRouter
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
                    // case 279:
                    //   tempPath = '存储-惠普'
                    //   break
                    // case 278:
                    //   tempPath = '存储-日立'
                    break
                  case 289:
                    tempPath = '上网行为管理'
                    break
                  case 285:
                    tempPath = '无线控制器'
                    break
                  case 36:
                    tempPath = '中间件-Tomcat'
                    break
                  case 63:
                    tempPath = '数据库-Redis'
                    break
                  case 58:
                    tempPath = '中间件-Weblogic'
                    break
                  case 319:
                    tempPath = 'VPN'
                    break
                  default:
                    tempPath = '通用设备详情页'
                }
                this.$router.push({ name: tempPath, params: query })
              } else {
                // this.msgError('抱歉当前')
                this.$router.push({ name: '实时告警', params: { name: router }})
              }
            })
          } else {
            this.msgInfo('抱歉您没有当前页面的访问权限！')
          }
        })
      }

      // this.$router.push({ name: router })
    },
    turnToRouter(router) {
      // const query = {}
      this.$router.push({ name: router })
    },
    // 定時器
    getTimer() {
      // console.log(1)
      this.momentDate = moment().format('x')
      this.nowDate = moment()
        .subtract(this.hours, 'hours')
        .format('x')
      this.nowDate1 = moment()
        .subtract(this.monthsDay, 'days')
        .format('x')
      this.getAllAlarmCount()
      this.getConfirmAll()
      this.getSixTypeCount()
      this.getAlarmTypeCount()
      this.getAlarmCategoryCount()
      this.getAlarmInfoTable()
      // this.getMonthsAlarmInfo()
    },
    // 获取图表高度
    getOperateBoxHeight() {
      this.$nextTick(function() {
        this.OperateBoxHeight = document.getElementsByClassName('main-box')[0].offsetHeight - 30
      })
    },
    // 获取告警总数
    getAllAlarmCount: function() {
      getAllAlarmCount({
        projectId: 10,
        nowDate: this.nowDate,
        engine: 'Freemarker'
      }).then((response) => {
        this.totalAlarm = response.strData
      })
    },
    // 获取未确认总数
    getConfirmAll: function() {
      if (!this.confirmAll) {
        this.loading1 = true
      }
      getConfirmAll({
        projectId: 10,
        nowDate: this.nowDate,
        engine: 'Freemarker'
      }).then((response) => {
        this.loading1 = false
        // console.log(response)
        this.confirmAll = response.strData
      })
    },
    // 获取告警确认信息
    getSixTypeCount: function() {
      getSixTypeCount({
        projectId: 10,
        nowDate: this.nowDate,
        engine: 'Freemarker'
      }).then((response) => {
        // console.log(response)
        response.datas.forEach((item) => {
          this.$set(this.TypeAlarm, item.Severity, item)
        })
      })
    },
    // 获取告警分布
    getAlarmTypeCount: function() {
      if (!this.arr2.length) {
        this.loading2 = true
      }
      getAlarmTypeCount({
        projectId: 10,
        nowDate: this.nowDate,
        engine: 'Freemarker'
      }).then((response) => {
        // 用于判断loading是否加载的数组，无实际意义
        this.arr2 = response.datas
        this.loading2 = false
        response.datas.forEach((item) => {
          if (item.count) {
            this.$set(this.alarmTypeArr[item.mapping_value], 'value', item.count || 0)
            // console.log(this.alarmTypeArr)
            // this.loading2
            if (+item.count) {
              this.loading2 = true
            }
          }
        })
      })
    },
    // 获取设备分布
    getAlarmCategoryCount: function() {
      if (!this.categoryArr.length) {
        this.loading3 = true
      }
      getAlarmCategoryCount({
        projectId: 10,
        nowDate: this.nowDate,
        engine: 'Freemarker'
      }).then((response) => {
        // console.log(response)
        this.loading3 = false
        if (response.success && response.datas.length) {
          this.categoryArr = response.datas
          this.categoryArrX = []
          response.datas.forEach((item) => {
            this.categoryArrX.push(item.name)
          })
        } else {
          this.loading3 = true
        }
      })
    },
    // 获取告警列表
    getAlarmInfoTable: function() {
      if (!this.activities.length) {
        this.loading5 = true
      }
      getAlarmInfoTable({
        projectId: 10,
        nowDate: this.nowDate,
        engine: 'Freemarker'
      }).then((response) => {
        // console.log(response)
        this.loading5 = false
        this.activities = response.datas
        // this.categoryArr = response.datas
        // this.categoryArrX = []
        this.activities.forEach((item) => {
          // this.categoryArrX.push(item.name)
          switch (item.Severity) {
            case 0:
              item.Severity = this.$store.state.settings.themeClass === 'dark' ? '#c2b8b8' : '#c2b8b8'
              // item.Severity = '#c2b8b84d'
              break
            case 1:
              item.Severity = this.$store.state.settings.themeClass === 'dark' ? '#10aaf4' : '#7499FF'
              // item.Severity = '#10aaf44d'
              break
            case 2:
              item.Severity = this.$store.state.settings.themeClass === 'dark' ? '#FCCE10' : '#FFC859'
              // item.Severity = '#FCCE104d'
              break
            case 3:
              item.Severity = this.$store.state.settings.themeClass === 'dark' ? '#E87C25' : '#FFA059'
              // item.Severity = '#E87C254d'
              break
            case 4:
              item.Severity = this.$store.state.settings.themeClass === 'dark' ? '#f01919' : '#E97659'
              // item.Severity = '#f019194d'
              break
            case 5:
              item.Severity = this.$store.state.settings.themeClass === 'dark' ? '#7a0c0c' : '#E45959'
              // item.Severity = '#7a0c0c4d'
              break
            default:
              break
          }
        })
      })
    },
    getMonthsAlarmInfo: function() {
      if (!this.seriesArr.length) {
        this.loading4 = true
      }
      getMonthsAlarmInfo({
        projectId: 10,
        nowDate: this.nowDate1,
        engine: 'Freemarker'
      }).then((response) => {
        // console.log(response)
        this.loading4 = false
        this.seriesArr = []
        this.seriesArrX = []
        response.datas.forEach((item) => {
          let a = item.time.split('-')
          a.shift()
          a = a.join('-')
          // console.log(a)
          this.seriesArrX.push(a)
          this.seriesArrX = [...new Set(this.seriesArrX)]
          switch (item.name) {
            case '未知':
              if (!this.seriesArr[0]) {
                this.seriesArr[0] = {
                  name: '未知',
                  type: 'line',
                  color: '#c2b8b8b3',
                  data: []
                }
                this.seriesArr[0].data.push([a, item.count])
              } else {
                this.seriesArr[0].data.push([a, item.count])
              }
              break
            case '通知':
              if (!this.seriesArr[1]) {
                this.seriesArr[1] = {
                  name: '通知',
                  type: 'line',
                  color: this.$store.state.settings.themeClass === 'dark' ? '#10aaf4' : '#7499FF',
                  data: []
                }
                this.seriesArr[1].data.push([a, item.count])
              } else {
                this.seriesArr[1].data.push([a, item.count])
              }
              break
            case '告警':
              if (!this.seriesArr[2]) {
                this.seriesArr[2] = {
                  name: '告警',
                  type: 'line',
                  color: this.$store.state.settings.themeClass === 'dark' ? '#FCCE10' : '#FFC859',
                  data: []
                }
                this.seriesArr[2].data.push([a, item.count])
              } else {
                this.seriesArr[2].data.push([a, item.count])
              }
              break
            case '问题':
              if (!this.seriesArr[3]) {
                this.seriesArr[3] = {
                  name: '问题',
                  type: 'line',
                  color: this.$store.state.settings.themeClass === 'dark' ? '#E87C25' : '#FFA059',
                  data: []
                }
                this.seriesArr[3].data.push([a, item.count])
              } else {
                this.seriesArr[3].data.push([a, item.count])
              }
              break
            case '严重':
              if (!this.seriesArr[4]) {
                this.seriesArr[4] = {
                  name: '严重',
                  type: 'line',
                  color: this.$store.state.settings.themeClass === 'dark' ? '#f0191b' : '#E97659',
                  data: []
                }
                this.seriesArr[4].data.push([a, item.count])
              } else {
                this.seriesArr[4].data.push([a, item.count])
              }
              break
            case '灾难':
              if (!this.seriesArr[5]) {
                this.seriesArr[5] = {
                  name: '灾难',
                  type: 'line',
                  color: this.$store.state.settings.themeClass === 'dark' ? '#7a0c0c' : '#E45959',
                  data: []
                }
                this.seriesArr[5].data.push([a, item.count])
              } else {
                this.seriesArr[5].data.push([a, item.count])
              }
              break
          }
        })
      })
    }
  }
}
</script>
<style lang="scss" scoped>
@import '../../../assets/styles/index.scss';
// 标题
.lineBox-title1 {
  margin-top: 7px;
  margin-bottom: 8px;
  font-size: 36px;
  text-align: center;
  height: 40px;
  color: #10aaf4;
  font-family: 'dapingA';
  // background: url('../../../assets/imgforindex/round1.png') 99% 50% no-repeat, url('../../../assets/imgforindex/round1.png') 1% 50% no-repeat, url('../../../assets/imgforindex/linex.png') left bottom no-repeat, url('../../../assets/imgforindex/linex.png') left 50% no-repeat, url('../../../assets/imgforindex/liney.png') left bottom no-repeat;
  background-size: 8px, 8px, 100% 1px, 15px 1px, 1px 50%;
  // border: 1px solid #10acf462;
}
.clearfix:after {
  /*伪元素是行内元素 正常浏览器清除浮动方法*/
  content: '';
  display: block;
  height: 0;
  clear: both;
  visibility: hidden;
}
.clearfix {
  *zoom: 1; /*ie6清除浮动的方式 *号只有IE6-IE7执行，其他浏览器不执行*/
}

.app-container {
  overflow: auto;
  height: 95vh;
}
.app-container1 {
  overflow: hidden;
  height: 95vh;
  background: #091020;
}

.specialBox {
  // 告警筛选区样式
  height: 100%;
  /deep/.box-card {
    display: inline-block;
    vertical-align: top;
    // float: left;
    height: 32%;
    width: 24.25% !important;
    margin-left: 1%;
  }
  .box-card:first-child {
    // display: inline-block;
    margin-left: none;
    margin-left: 0%;
    width: 16% !important;
  }
  .box-card:nth-child(2) {
    // display: inline-block;
    // margin-left: none;
    // margin-left: 0%;
    width: 30.5% !important;
  }
  .box-card:nth-child(3) {
    // display: inline-block;
    // margin-left: none;
    // margin-left: 0%;
    width: 16% !important;
  }
  .box-card:nth-child(4) {
    // display: inline-block;
    // margin-left: none;
    // margin-left: 0%;
    width: 34.5% !important;
  }
  .box-card:last-child {
    height: 61%;
    margin-top: 1%;
    margin-left: 0%;
    width: 100% !important;
  }
}

.specialBox1 {
  .box-card:last-child {
    height: calc(50%-60px);
    margin-top: 1%;
    margin-left: 0%;

    width: 100% !important;
  }
}
.main-box {
  // float: left;
  display: inline-block;
  // margin-bottom: 1%;
  // margin-right: 0.4%;
  width: 100%;
  height: 100%;
  // background-image: url('../../../assets/imgforindex/blue1.png');
  // background-size: 50px;
  // background-repeat: no-repeat;
  // background-position:right 95%;
  .lineBox-title {
    width: 100%;
    height: 30px;
    line-height: 25px;
    font-size: 20px;
    padding-left: 30px;
    font-family: 'dapingA';
    background: url('../../../assets/imgforindex/round1.png') 15px 50% no-repeat, url('../../../assets/imgforindex/linex.png') left bottom no-repeat, url('../../../assets/imgforindex/linex.png') 10px 50% no-repeat, url('../../../assets/imgforindex/liney.png') 10px bottom no-repeat;
    background-size: 8px, 100% 1px, 15px 1px, 1px 50%;
    font-weight: 480;
    color: #10aaf4;
    cursor: pointer;
  }
  .oneBox {
    padding: 10px;
    .allTitle {
      font-size: 18px;
      text-align: center;
      font-family: 'dapingA';
      // color: #10aaf4;
      margin-bottom: 10px;
    }
    .allText {
      text-align: center;
      font-size: 18px;
      font-family: 'number';
      vertical-align: middle;
      // background: url('../../../assets/imgforindex/727.png') 95% 50% no-repeat, url('../../../assets/imgforindex/728.png') 5% 50% no-repeat;
      // background-size: auto 100%;
      img {
        margin-left: 2px;
      }
    }
    .allNumber {
      // line-height: 80px;
      // width: 100px;
      // height: 80px;
      // padding: auto 10px;
      font-family: 'number';
      text-align: center;
      font-size: 20px;
      // background: url('../../../assets/imgforindex/727.png') 95% 50% no-repeat, url('../../../assets/imgforindex/728.png') 5% 50% no-repeat;
      // background-size: auto 100%;
      span:nth-child(2) {
        margin: 0 5px;
      }
      span:nth-child(3) {
        // margin: 0 5px;
        color: #10aaf4;
      }
    }
  }
  .fiveBox {
    padding: 20px;
    height: 90%;
    overflow: auto;

    .el-timeline {
      padding-left: 130px;

      /deep/.el-timeline-item__content {
        color: #fff;
        margin-bottom: 30px;
        line-height: 20px;
        height: max-content;
        .el-row {
          // display: inline-block;
          height: max-content;
          // width: 100%;
          .el-col {
            height: max-content;
          }
        }
      }
      /deep/.el-timeline-item__tail {
        border-left: 1px dotted #10aaf4;
      }
      /deep/.el-timeline-item__icon {
        // color: #03a9f4;
        font-size: 18px;
      }
      .timer {
        position: absolute;
        left: -130px;
        top: 1px;
        font-family: 'number';
        font-size: 16px;
      }
      .content {
        width: 99.4%;
        position: absolute;
        top: 1px;
        // height: max-content;
        // height: 100%;

        .contentBox {
          display: -webkit-box;
          overflow: hidden;
          text-overflow: ellipsis;
          word-wrap: break-word;
          white-space: normal !important;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
        }
      }
      .el-timeline-item:first-child {
        // color: turquoise;
        font-family: 'number';
        font-size: 18px;
        padding-bottom: 0 !important;
        /deep/.el-timeline-item__tail {
          border-left: 1px dotted transparent;
        }
        /deep/.el-timeline-item__node {
          background-color: transparent;
        }
        .timer,
        .content {
          color: #10aaf4;
        }
      }
    }
  }
}
.light {
  .box-card {
    width: 100%;
    background-color: #fff;
    float: left !important;
    color: #1f2c33;
  }
  .fiveBox {
    padding: 20px;
    height: 90%;
    overflow: auto;

    .el-timeline {
      padding-left: 130px;

      /deep/.el-timeline-item__content {
        color: #1f2c33;
        margin-bottom: 30px;
        line-height: 20px;
        height: max-content;
      }
      /deep/.el-timeline-item__tail {
        border-left: 1px dotted #10aaf4;
      }
      /deep/.el-timeline-item__icon {
        // color: #03a9f4;
        font-size: 18px;
      }
      .timer {
        position: absolute;
        left: -130px;
        top: 1px;
        font-family: 'number';
        font-size: 16px;
      }
      .content {
        width: 99.4%;
        position: absolute;
        top: 1px;
        height: max-content;
      }
      .el-timeline-item:first-child {
        // color: turquoise;
        font-family: 'number';
        font-size: 18px;
        padding-bottom: 0 !important;
        /deep/.el-timeline-item__tail {
          border-left: 1px dotted transparent;
        }
        /deep/.el-timeline-item__node {
          background-color: transparent;
        }
        .timer,
        .content {
          color: #10aaf4;
        }
      }
    }
  }
}

.dark {
  .box-card {
    width: 100%;
    float: left !important;
    background-color: rgba(42, 47, 57, 0.3);
  }
}
@-webkit-keyframes rotation {
  from {
    -webkit-transform: rotateY(0deg);
  }
  to {
    -webkit-transform: rotateY(360deg);
  }
}

.an {
  -webkit-transform: rotateY(360deg);
  animation: rotation 6s linear infinite;
  -moz-animation: rotation 6s linear infinite;
  -webkit-animation: rotation 6s linear infinite;
  -o-animation: rotation 6s linear infinite;
}
</style>
