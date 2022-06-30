<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import * as echarts from 'echarts'

require('echarts/theme/macarons') // echarts theme
// import "echarts-liquidfill";
import resize from './mixins/resize'

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'fill-echart'
    },
    width: {
      type: String,
      default: '200px'
    },
    height: {
      type: String,
      default: '200px'
    },
    autoResize: {
      type: Boolean,
      default: true
    },
    center: {
      type: Array,
      default: () => ['50%', '58%']
    },
    radius: {
      type: Array,
      default: () => [62, 68]
    },
    chartData: {
      type: Object,
      // eslint-disable-next-line vue/require-valid-default-prop
      default: {
        data: 0,
        nameTitle: 'cpu'
      },
      required: true
    }
  },
  data() {
    return {
      chart: null,
      valNum: this.chartData.data,
      dataStyle: {
        normal: {
          label: {
            show: false
          },
          labelLine: {
            show: false
          },
          shadowBlur: 0,
          shadowColor: '#203665'
        }
      },
      borderColor: this.chartData.data <= 70 ? '#1890ff' : ((this.chartData.data > 70 && this.chartData.data <= 90) ? '#ff9800' : '#ff5722'),
      innerborderColor: this.chartData.data <= 70 ? '#1890ff33' : ((this.chartData.data > 70 && this.chartData.data < 90) ? '#ff980033' : '#ff572233')
    }
  },
  watch: {
    radius: {
      handler(val) {
        if (!val && val !== 0) {
          return
        }
        // if (this.chart) {
        //   this.chart.dispose()
        // }
        // this.valNum = val
        this.initChart()
      },
      deep: true
    },
    'chartData.data': {
      handler(val) {
        if (!val && val !== 0) {
          return
        }
        this.chart.dispose()
        this.valNum = val
        this.borderColor = this.chartData.data <= 70 ? '#1890ff' : ((this.chartData.data > 70 && this.chartData.data <= 90) ? '#ff9800' : '#ff5722')
        this.innerborderColor = this.chartData.data <= 70 ? '#1890ff' : ((this.chartData.data > 70 && this.chartData.data <= 90) ? '#ff9800' : '#ff5722')
        this.initChart()
      }
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
        series: [
          {
            name: this.chartData.nameTitle,
            type: 'pie',
            clockWise: false,
            radius: this.radius,
            itemStyle: this.dataStyle,
            hoverAnimation: false,
            center: this.center,
            data: [
              {
                value: this.chartData.data,
                label: {
                  normal: {
                    rich: {
                      a: {
                        color: this.borderColor,
                        align: 'center',
                        fontSize: 18,
                        fontWeight: 'bold'
                      },
                      b: {
                        color:
                          this.$store.state.settings.themeClass == 'dark'
                            ? '#fff'
                            : '#7C7C7C',
                        align: 'center',
                        fontSize: 14
                      }
                    },
                    formatter: function(params) {
                      return (
                        '{b|' +
                        params.seriesName +
                        '}\n\n' +
                        '{a|' +
                        params.value +
                        '%}'
                      )
                    },
                    position: 'center',
                    show: true,
                    textStyle: {
                      fontSize: '14',
                      fontWeight: 'normal',
                      color: '#fff'
                    }
                  }
                },
                itemStyle: {
                  normal: {
                    color: this.borderColor,
                    shadowColor: '#2c6cc4',
                    shadowBlur: 0
                  }
                }
              },
              {
                value: 100 - this.chartData.data,
                name: 'invisible',
                itemStyle: {
                  normal: {
                    color: this.innerborderColor
                  },
                  emphasis: {
                    color: this.innerborderColor
                  }
                }
              }
            ]
          }
        ]
      })
    }
  }
}
</script>
