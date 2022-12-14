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
    bottom: {
      type: String,
      default: '4%'
    },
    offsetCenter: {
      type: Array,
      default: () => [0, 50]
    },
    center: {
      type: Array,
      default: () => ['50%', '50%']
    },
    width: {
      type: String,
      default: '180px'
    },
    height: {
      type: String,
      default: '180px'
    },
    autoResize: {
      type: Boolean,
      default: true
    },
    radius: {
      type: String,
      default: '180%'
    },
    radius1: {
      type: String,
      default: '90%'
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
    radius: {
      handler(val, oldVal) {
        // this.initChart();
        this.chart.dispose()
        this.initChart()
      },
      deep: true
    },
    radius1: {
      handler(val, oldVal) {
        // this.initChart();
        this.chart.dispose()
        this.initChart()
      },
      deep: true
    },
    height: {
      handler(val, oldVal) {
        // this.initChart();
        this.chart.dispose()
        this.initChart()
      },
      deep: true
    },
    width: {
      handler(val, oldVal) {
        // this.initChart();
        this.chart.dispose()
        this.initChart()
      },
      deep: true
    },
    center: {
      handler(val, oldVal) {
        // this.initChart();
        this.chart.dispose()
        this.initChart()
      },
      deep: true
    },
    offsetCenter: {
      handler(val, oldVal) {
        // this.initChart();
        this.chart.dispose()
        this.initChart()
      },
      deep: true
    },
    chartData: {
      handler(val, oldVal) {
        // this.initChart();
        this.chart.dispose()
        this.borderColor =
          val.data <= 70
            ? '#1890ff'
            : val.data > 70 && val.data <= 90
              ? '#ff9800'
              : '#ff5722'
        this.innerborderColor =
          val.data <= 70
            ? '#1890ff33'
            : val.data > 70 && val.data < 90
              ? '#ff980033'
              : '#ff572233'
        this.initChart()
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
        // backgroundColor: "#FFFFFF",
        title: [
          {
            text: this.chartData.nameTitle,
            bottom: this.bottom,
            x: 'center',
            borderColor: this.borderColor,
            borderWidth: 1,
            borderRadius: 15,
            backgroundColor: this.borderColor,
            padding: [4, 14],
            textStyle: {
              fontWeight: 'bold',
              fontSize: 13,
              color: '#fff'
            }
          }
        ],
        angleAxis: {
          show: false,
          max: (100 * 360) / 270, // -45??????225????????????????????????270??????360???
          type: 'value',
          startAngle: 225, // ?????????????????????
          splitLine: {
            show: false
          }
        },
        barMaxWidth: 5, // ????????????
        radiusAxis: {
          show: false,
          type: 'category'
        },
        // ?????????????????????
        polar: {
          center: this.center,
          radius: this.radius
        },
        series: [
          {
            type: 'bar',
            data: [
              {
                // ???????????????????????????
                value: this.chartData.data,
                itemStyle: {
                  color: this.borderColor
                }
              }
            ],
            barGap: '-100%', // ????????????,????????????????????????
            coordinateSystem: 'polar',
            roundCap: true, // ????????????
            z: 3 // ??????????????????zindex
          },
          {
            // ??????????????????????????????
            type: 'bar',
            data: [
              {
                value: 100,
                itemStyle: {
                  color: this.borderColor,
                  opacity: 0.2,
                  borderWidth: 0
                }
              }
            ],
            barGap: '-100%',
            coordinateSystem: 'polar',
            roundCap: true,
            z: 1
          },
          // ?????????
          {
            type: 'gauge',
            center: this.center,
            startAngle: 225, // ???????????????????????????
            endAngle: -45, // ???????????????????????????
            axisLine: {
              show: false
            },
            splitLine: {
              show: false
            },
            axisTick: {
              show: false
            },
            axisLabel: {
              show: false
            },
            splitLabel: {
              show: false
            },
            pointer: {
              // ?????????
              shadowColor: 'auto', // ????????????
              shadowBlur: 5,
              length: '50%',
              width: '2'
            },
            itemStyle: {
              color: this.borderColor,
              borderColor: this.borderColor,
              borderWidth: 1
            },
            detail: {
              formatter: function(params) {
                return params + '%'
              },
              color: this.borderColor,
              fontSize: 18,
              offsetCenter: this.offsetCenter
            },
            title: {
              show: false
            },
            data: [
              {
                value: this.chartData.data
              }
            ]
          },
          {
            name: '????????????',
            type: 'gauge',
            center: this.center,
            radius: this.radius1,
            min: 0, // ????????????
            max: 100, // ????????????
            splitNumber: 10, // ????????????
            startAngle: 225,
            endAngle: -45,
            axisLine: {
              show: true,
              lineStyle: {
                width: 1,
                color: [[1, 'rgba(0,0,0,0)']]
              }
            }, // ???????????????
            axisLabel: {
              show: false,
              color: '#4d5bd1',
              distance: 15
            }, // ???????????????
            axisTick: {
              show: true,
              splitNumber: 3,
              lineStyle: {
                color: '#C7CBCF', // ?????????????????????????????????
                width: 1
              },
              length: -8
            }, // ????????????
            splitLine: {
              show: false,
              length: -20,
              lineStyle: {
                color: '#C7CBCF' // ?????????????????????????????????
              }
            }, // ???????????????
            detail: {
              show: false
            },
            pointer: {
              show: false
            }
          },
          {
            // ????????????
            type: 'pie',
            hoverAnimation: false,
            legendHoverLink: false,
            radius: ['5%', '10%'],
            center: this.center,
            z: 10,
            label: {
              normal: {
                show: false
              }
            },
            labelLine: {
              normal: {
                show: false
              }
            },
            data: [
              {
                value: 100,
                itemStyle: {
                  normal: {
                    color: this.borderColor
                  }
                }
              }
            ]
          },
          {
            // ????????????
            type: 'pie',
            hoverAnimation: false,
            legendHoverLink: false,
            radius: ['2%', '5%'],
            center: this.center,
            z: 10,
            label: {
              normal: {
                show: false
              }
            },
            labelLine: {
              normal: {
                show: false
              }
            },
            data: [
              {
                value: 100,
                itemStyle: {
                  normal: {
                    color: '#FFFFFF'
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
