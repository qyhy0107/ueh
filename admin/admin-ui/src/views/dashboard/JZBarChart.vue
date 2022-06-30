<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import * as echarts from 'echarts'

require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'wave-echart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '200px'
    },
    barWidth: {
      type: String,
      default: 'auto'
    },
    autoResize: {
      type: Boolean,
      default: true
    },
    // x轴名称
    xname: {
      type: String,
      default: ''
    },
    // 坐标系top位置
    gridTop: {
      type: String,
      default: '30%'
    },
    chartData: {
      type: Array,
      default: () => []

    }
  },
  data() {
    return {
      chart: null,
      borderColor:
        this.chartData.data <= 70
          ? '#1890ff'
          : this.chartData.data > 70 && this.chartData.data <= 90
            ? '#ff9800'
            : '#ff5722',
      innerborderColor:
        this.chartData.data <= 70
          ? '#1890ff33'
          : this.chartData.data > 70 && this.chartData.data < 90
            ? '#ff980033'
            : '#ff572233'
    }
  },
  watch: {
    chartData: {
      handler() {
        this.$nextTick(() => {
          this.initChart()
        })
      },
      deep: true
    }
  },
  mounted() {
    this.$nextTick(() => {
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
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')
      this.chartData ? this.setOptions(this.chartData) : null
    },
    setOptions({ expectedData, actualData } = {}) {
      this.chart.setOption({
        color:
          this.$store.state.settings.themeClass === 'dark'
            ? 'rgba(85, 136, 237, .8)'
            : 'rgba(24, 144, 255, 1)',
        tooltip: {
          trigger: 'axis',
          textStyle: {
            color: '#ffffff'
          },
          axisPointer: {
            // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
          }
        },
        grid: {
          top: this.gridTop,
          left: '5%',
          right: '10%',
          bottom: '0%',
          containLabel: true
        },
        yAxis: {
          type: 'value',
          name: '(条)',
          nameTextStyle: {
            fontSize: '10px',
            color:
              this.$store.state.settings.themeClass === 'dark'
                ? 'rgba(192, 208, 208, .6)'
                : 'rgba(51, 51, 51, .45)'
          },
          splitLine: {
            show: true,
            lineStyle: {
              type: 'solid',
              color:
                this.$store.state.settings.themeClass === 'dark'
                  ? 'rgba(77, 77, 77, .4)'
                  : 'rgba(0, 0, 0, 0)', // 左边线的颜色
              width: '1' // 坐标线的宽度
            }
          }, // 去除网格线
          splitArea: { show: false }, // 保留网格区域
          axisLine: {
            lineStyle: {
              type: 'solid',
              color:
                this.$store.state.settings.themeClass === 'dark'
                  ? 'rgba(77, 77, 77, .4)'
                  : 'rgba(51, 51, 51, .0)', // 左边线的颜色
              width: '1' // 坐标线的宽度
            }
          },
          axisLabel: {
            textStyle: {
              color:
                this.$store.state.settings.themeClass === 'dark'
                  ? 'rgba(192, 208, 208, .6)'
                  : 'rgba(51, 51, 51, .45)' // 坐标值得具体的颜色
            }
          }
        },
        xAxis: {
          name: this.xname,
          type: 'category',
          nameTextStyle: {
            fontSize: '10px',
            padding: [0, 0, 0, -20],
            color:
              this.$store.state.settings.themeClass === 'dark'
                ? 'rgba(192, 208, 208, .6)'
                : 'rgba(51, 51, 51, .45)'
          },
          // data: ["POST","OPTIONS","DELETE","PUT","PROPFIND","COPY","LOCK","MKCOL","GET","TRACE","HEAD","SEARCH","MOVE","PROPPATCH","UNLOCK","其他"],
          splitLine: {
            show: false,
            lineStyle: {
              type: 'solid',
              color:
                this.$store.state.settings.themeClass === 'dark'
                  ? 'rgba(77, 77, 77, .4)'
                  : 'rgba(0, 0, 0, 0)', // 左边线的颜色
              width: '1' // 坐标线的宽度
            }
          }, // 去除网格线
          splitArea: { show: false }, // 保留网格区域
          axisLine: {
            lineStyle: {
              type: 'solid',
              color:
                this.$store.state.settings.themeClass === 'dark'
                  ? 'rgba(77, 77, 77, .4)'
                  : 'rgba(0, 0, 0, 0)', // 左边线的颜色
              width: '1' // 坐标线的宽度
            }
          },
          axisLabel: {
            interval: 0,
            textStyle: {
              fontSize: '10px',
              color:
                this.$store.state.settings.themeClass === 'dark'
                  ? 'rgba(192, 208, 208, .6)'
                  : 'rgba(0, 0, 0, .45)' // 坐标值得具体的颜色
            }
          }
        },
        series: [
          {
            type: 'bar',
            barWidth: this.barWidth,
            itemStyle: {
              normal: {
                label: {
                  // formatter: "{c}",
                  formatter: function(params) {
                    return params.data[1]
                  },
                  show: true,
                  position: 'top',
                  textStyle: {
                    fontWeight: 'bolder',
                    fontSize: '12',
                    color:
                      this.$store.state.settings.themeClass === 'dark'
                        ? '#fff'
                        : '#333'
                  }
                }
              }
            },
            data: this.chartData
          }
        ]
      })
    }
  }
}
</script>
