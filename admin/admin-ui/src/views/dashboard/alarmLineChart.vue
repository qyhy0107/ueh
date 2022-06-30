<template>
  <div :id="idname" :class="className" :style="{ height: height, width: width }" />
</template>

<script>
import * as echarts from 'echarts'

require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'

export default {
  mixins: [resize],
  props: {
    vcenter: {
      type: String,
      default: null
    },
    turnType: {
      type: String,
      default: null
    },
    alarmArr: {
      type: Array,
      default: () => []
    },
    alarmArrX: {
      type: Array,
      default: () => []
    },
    idname: {
      type: String,
      default: 'main139'
    },
    className: {
      type: String,
      default: 'praph'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '280px'
    },
    data: {
      type: Array,
      default: () => []
    },
    links: {
      type: Array,
      default: () => []
    },
    curveness: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      healthAll: [0, 0],
      chart: null,
      ww: null // 屏幕宽度
    }
  },
  watch: {
    height: {
      handler() {
        this.initChart()
      },
      deep: true
    },
    alarmArr: {
      handler() {
        this.initChart()
      },
      deep: true
    },
    alarmArrX: {
      handler() {
        this.initChart()
      },
      deep: true
    }
  },
  beforeCreate() {
    this.ww = window.screen.width
  },
  created() {
    this.ww = window.screen.width
    // this.chart.dispose()
    // this.chart = null
    // this.initChart()
  },
  mounted() {
    this.$nextTick(() => {
      //   this.chart.dispose()
      //   this.chart = null
      this.initChart()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart(val) {
      var chartDom = document.getElementById(this.idname)
      this.chart = echarts.init(chartDom)
      var option = {
        // color: ['#FF965C', '#F85043', '#FB160C'],
        // color: ['#c2b8b8b3', '#10aaf4b3', '#FCCE10b3', '#E87C25b3', '#f0191bb3', '#7a0c0cb3'],
        grid: {
          left: '5%',
          top: '15%',
          bottom: '10%',
          right: '5%',
          containLabel: true
        },
        legend: {
          top: '5%',
          // right: '20%',
          // color: '#fff',
          itemWidth: 10,
          itemHeight: 10,
          textStyle: { color: this.$store.state.settings.themeClass === 'dark' ? '#fff' : '#333' },
          data: ['未知', '通知', '警告', '问题', '严重', '灾难'],
          selectedMode: false
          // selected: {
          //   '未知': this.alarmTypeArr[0].value,
          //   '通知': this.alarmTypeArr[1].value,
          //   '警告': this.alarmTypeArr[2].value,
          //   '问题': this.alarmTypeArr[3].value,
          //   '严重': this.alarmTypeArr[4].value,
          //   '灾难': this.alarmTypeArr[5].value
          // }

        },
        tooltip: {
          trigger: 'axis'
        },
        toolbox: {
          show: true,
          orient: 'vertical',
          left: 'right',
          top: 'center'
        },
        xAxis: [
          {
            type: 'category',
            data: this.alarmArrX,
            axisTick: { show: false },
            axisLine: {
              show: false,
              lineStyle: {
                color: this.$store.state.settings.themeClass === 'dark' ? 'rgba(77, 77, 77, .5)' : '#00CDD8'
              }
            },
            axisLabel: {
              margin: 20,
              color: this.$store.state.settings.themeClass === 'dark' ? 'rgba(192, 208, 208, 1)' : 'rgba(0,0,0,.45)',
              textStyle: {
                fontSize: 12
              }
            }
          }
        ],
        yAxis: [
          {
            type: 'value',
            axisLabel: {
              color: this.$store.state.settings.themeClass === 'dark' ? 'rgba(192, 208, 208, 1)' : 'rgba(0,0,0,.45)',
              textStyle: {
                fontSize: 12,
                fontWeight: 'normal'
              }
            },
            axisLine: {
              show: false,
              lineStyle: {
                color: '#00CDD8'
              }
            },
            splitLine: {
              lineStyle: {
                color: this.$store.state.settings.themeClass === 'dark' ? 'rgba(77, 77, 77, .5)' : '#F6F6F6'
              }
            },
            splitArea: { show: false },
            axisTick: {
              show: false
            }
          }
        ],
        series: this.alarmArr
      }
      option && this.chart.setOption(option)
    }
  }
}
</script>
