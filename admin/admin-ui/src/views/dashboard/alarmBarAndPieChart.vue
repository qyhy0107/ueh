<template>
  <div v-if="Chartheight" :id="idname" :class="className" :style="{ height: height, width: width }" />
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
    alarmTypeArr: {
      type: Array,
      default: () => [{ value: 0, name: '未知' }, { value: 0, name: '通知' }, { value: 0, name: '警告' }, { value: 0, name: '问题' }, { value: 0, name: '严重' }, { value: 0, name: '灾难' }]
    },
    healthScoreObj: {
      type: Object,
      default: () => {}
    },
    healthAll1: {
      type: Array,
      default: () => [0, 0]
    },
    idname: {
      type: String,
      default: 'main966'
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
      Chartheight: '',
      healthAll: [0, 0],
      chart: null,
      ww: null // 屏幕宽度
    }
  },
  watch: {
    height: {
      handler(val) {
        // if (!this.chart) {
        //   return
        // }
        // console.log(val)
        // this.chart.dispose()
        // this.chart = null
        this.Chartheight = val
        this.initChart()
      },
      deep: true
    },
    alarmTypeArr: {
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
    if (this.chart) {
      this.chart.dispose()
      this.chart = null
    }
  },
  methods: {
    initChart(val) {
      var chartDom = document.getElementById(this.idname)
      if (!chartDom) {
        return
      }
      this.chart = echarts.init(chartDom)
      var option = {
        grid: [
          {
            top: '25%',
            bottom: '5%',
            right: '40%'
            // containLabel: true
            // left: '15%'
          }
        ],
        legend: {
          top: '5%',
          right: '20%',
          // color: '#fff',
          itemWidth: 10,
          itemHeight: 10,
          textStyle: { color: this.$store.state.settings.themeClass === 'dark' ? '#fff' : '#333' },
          data: ['未知', '通知', '警告', '问题', '严重', '灾难'],
          selectedMode: false,
          selected: {
            未知: this.alarmTypeArr[0].value,
            通知: this.alarmTypeArr[1].value,
            警告: this.alarmTypeArr[2].value,
            问题: this.alarmTypeArr[3].value,
            严重: this.alarmTypeArr[4].value,
            灾难: this.alarmTypeArr[5].value
          }
        },
        color: this.$store.state.settings.themeClass === 'dark' ? ['#c2b8b8b3', '#10aaf4b3', '#FCCE10b3', '#E87C25b3', '#f0191bb3', '#7a0c0cb3'] : ['#c2b8b8b3', '#7499FF', '#FFC859', '#FFA059', '#E97659', '#E45959'],
        // color: ['#c2b8b8b3', '#E97659', '#FFA059', '#FFC859', '#7499FF', '#E45959'],

        // xAxis: {
        //   type: 'category',
        //   data: ['未知', '通知', '警告', '问题', '严重', '灾难']
        // },
        xAxis: {
          type: 'category',
          data: ['未知', '通知', '警告', '问题', '严重', '灾难'],
          axisLine: {
            show: false // 隐藏X轴轴线
          },
          axisTick: {
            show: false // 隐藏X轴轴线
          },
          splitLine: {
            show: false,
            lineStyle: {
              color: this.$store.state.settings.themeClass === 'dark' ? 'rgba(192, 208, 208, 1)' : 'rgba(0,0,0,.45)',
              width: 2
            }
          },
          axisLabel: {
            show: false,
            margin: 14,
            fontSize: 14,
            fontFamily: 'dapingA',
            textStyle: {
              color: this.$store.state.settings.themeClass === 'dark' ? 'rgba(192, 208, 208, 1)' : 'rgba(0,0,0,.45)' // X轴文字颜色
            }
          }
        },
        // yAxis: {
        //   type: 'value'
        // },
        yAxis: [
          {
            type: 'value',
            gridIndex: 0,
            min: 0,
            // max: 100,
            // interval: 100,
            // splitNumber: 4,
            splitLine: {
              show: true,
              lineStyle: {
                color: 'rgba(77, 128, 254, 0.1)',
                width: 1
              }
            },
            axisTick: {
              show: false
            },
            axisLine: {
              show: true,
              lineStyle: {
                color: 'rgba(77, 128, 254, 0.2)'
              }
            },
            axisLabel: {
              show: true,
              margin: 14,
              fontSize: 10,
              textStyle: {
                // color: '#65D5FF'
                // fontFamily: 'number',
                // fontSize: 14,
                color: this.$store.state.settings.themeClass === 'dark' ? 'rgba(192, 208, 208, 1)' : 'rgba(0,0,0,.45)'
              }
            }
          }
        ],
        series: [
          {
            type: 'bar',
            name: this.$store.state.settings.themeClass,
            itemStyle: {
              normal: {
                color: function(params) {
                  // console.log(params)
                  // build a color map as your need.
                  var colorList = params.seriesName === 'dark' ? ['#c2b8b8b3', '#10aaf4b3', '#FCCE10b3', '#E87C25b3', '#f0191bb3', '#7a0c0cb3'] : ['#c2b8b8b3', '#7499FF', '#FFC859', '#FFA059', '#E97659', '#E45959']
                  // var colorList = ['#c2b8b8b3', '#E97659', '#FFA059', '#FFC859', '#7499FF', '#E45959']
                  return colorList[params.dataIndex]
                },
                // 以下为是否显示，显示位置和显示格式的设置了
                label: {
                  show: true,
                  fontFamily: 'number',
                  color: this.$store.state.settings.themeClass === 'dark' ? '#fff' : '#333',
                  fontSize: 18,
                  position: 'top',
                  formatter: '{c}'
                  // formatter: '{b}\n{c}'
                }
              }
            },
            // label: {
            //   show: true,
            //   fontFamily: 'number',
            //   color: '#fff',
            //   fontSize: 18
            // },
            // emphasis: {
            //   label: {
            //     show: true,
            //     fontFamily: 'number',
            //     color: '#fff',
            //     fontSize: 18
            //   }
            // },
            data: this.alarmTypeArr
          },
          {
            // name: 'Area Mode',
            type: 'pie',
            radius: [10, 75],
            center: ['80%', '60%'],
            roseType: 'area',
            label: {
              show: true,
              // formatter: '{per|{d}%}{b|{b}}\n\n',
              // borderWidth: 0,
              // borderRadius: 4,
              padding: [0, -25],
              // rich: {
              //   b: {
              //     color: '#fff',
              //     fontSize: 12,
              //     lineHeight: 33
              //   },
              //   per: {
              //     fontSize: 12,
              //     padding: [2, 4],
              //     borderRadius: 2
              //   }
              // },
              position: 'inside',
              fontFamily: 'number',
              color: '#fff',
              fontSize: 14,
              formatter: '{d}%'
            },
            labelLine: {
              normal: {
                length: 0,
                length2: 0
              }
            },
            itemStyle: {
              borderRadius: 2
            },
            data: this.alarmTypeArr
          }
        ]
      }
      option && this.chart.setOption(option, true)
    }
  }
}
</script>
