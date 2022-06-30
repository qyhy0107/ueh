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
          max: (100 * 360) / 270, // -45度到225度，二者偏移值是270度除360度
          type: 'value',
          startAngle: 225, // 极坐标初始角度
          splitLine: {
            show: false
          }
        },
        barMaxWidth: 5, // 圆环宽度
        radiusAxis: {
          show: false,
          type: 'category'
        },
        // 圆环位置和大小
        polar: {
          center: this.center,
          radius: this.radius
        },
        series: [
          {
            type: 'bar',
            data: [
              {
                // 上层圆环，显示数据
                value: this.chartData.data,
                itemStyle: {
                  color: this.borderColor
                }
              }
            ],
            barGap: '-100%', // 柱间距离,上下两层圆环重合
            coordinateSystem: 'polar',
            roundCap: true, // 顶端圆角
            z: 3 // 圆环层级，同zindex
          },
          {
            // 下层圆环，显示最大值
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
          // 仪表盘
          {
            type: 'gauge',
            center: this.center,
            startAngle: 225, // 起始角度，同极坐标
            endAngle: -45, // 终止角度，同极坐标
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
              // 分隔线
              shadowColor: 'auto', // 默认透明
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
            name: '外部刻度',
            type: 'gauge',
            center: this.center,
            radius: this.radius1,
            min: 0, // 最小刻度
            max: 100, // 最大刻度
            splitNumber: 10, // 刻度数量
            startAngle: 225,
            endAngle: -45,
            axisLine: {
              show: true,
              lineStyle: {
                width: 1,
                color: [[1, 'rgba(0,0,0,0)']]
              }
            }, // 仪表盘轴线
            axisLabel: {
              show: false,
              color: '#4d5bd1',
              distance: 15
            }, // 刻度标签。
            axisTick: {
              show: true,
              splitNumber: 3,
              lineStyle: {
                color: '#C7CBCF', // 用颜色渐变函数不起作用
                width: 1
              },
              length: -8
            }, // 刻度样式
            splitLine: {
              show: false,
              length: -20,
              lineStyle: {
                color: '#C7CBCF' // 用颜色渐变函数不起作用
              }
            }, // 分隔线样式
            detail: {
              show: false
            },
            pointer: {
              show: false
            }
          },
          {
            // 指针外环
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
            // 指针内环
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
