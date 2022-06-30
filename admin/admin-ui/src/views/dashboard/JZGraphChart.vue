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
      default: 'praph'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '400px'
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
      chart: null,
      ww: null // 屏幕宽度
    }
  },
  watch: {
    links: {
      handler() {
        this.initChart()
      },
      deep: true
    },
    data: {
      handler() {
        this.$nextTick(() => {
          this.initChart()
        })
      },
      deep: true
    }
  },
  beforeCreate() {
    this.ww = window.screen.width
  },
  created() {
    this.ww = window.screen.width
  },
  mounted() {
    this.ww = window.screen.width
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
      this.data ? this.setOptions(this.data) : null
    },
    setOptions({ expectedData, actualData } = {}) {
      this.chart.setOption({
        itemStyle: {
          normal: {
            color: '#000'
          },
          shadowBlur: 2
        },
        animationDuration: 1500,
        animationEasingUpdate: 'quinticInOut',
        xAxis: {
          show: false,
          type: 'value'
        },
        yAxis: {
          show: false,
          type: 'value'
        },
        series: [
          {
            type: 'graph',
            coordinateSystem: 'cartesian2d',
            // legendHoverLink: false,
            symbol: 'roundRect',
            hoverAnimation: true,
            nodeScaleRatio: false,
            // 建头
            // edgeSymbol: ['circle', 'arrow'],
            // edgeSymbolSize: this.ww > 1400 ? [2, 15] : [1, 10],
            edgeLabel: {
              show: false,
              normal: {
                show: true,
                position: 'middle',
                textStyle: {
                  fontSize: 12
                },
                formatter: '{c}'
              }
            },
            focusNodeAdjacency: true,
            roam: true,
            categories: [
              {
                name: '一级关系',
                itemStyle: {
                  // 可配置颜色
                  normal: {
                    color: '#A170DD'
                  }
                }
              },
              {
                name: '二级关系',
                itemStyle: {
                  // 可配置颜色
                  normal: {
                    color: '#41B1EF'
                  }
                }
              },
              {
                name: '三级关系',
                itemStyle: {
                  // 可配置颜色
                  normal: {
                    color: '#667AED'
                  }
                }
              }
            ],
            // 圆形上面的文字
            label: {
              show: true,
              fontSize: 16,
              formatter: function(params) {
                if (params.data.percentage) {
                  return `{subt|${params.name}}\n\n{mi|${params.data.percentage[0]}} {mt|${params.data.percentage[1]}}`
                }
                if (params.data.label) {
                  return `{subt|${params.name}}\n\n{mt|${params.data.label}}`
                } else {
                  return `{subt|${params.name}}`
                }
              },
              rich: {
                subt: {
                  fontSize: this.ww > 1400 ? 14 : 10,
                  color: '#fff'
                },
                mt: {
                  fontSize: this.ww > 1400 ? 12 : 14,
                  color: '#fff'
                },
                mi: {
                  fontSize: 10,
                  color: '#fff'
                }
              }
            },
            // force: {
            //   repulsion: 1000
            // },
            force: {
              repulsion: 2500
            },
            itemStyle: {
              normal: {
                color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                  {
                    offset: 0,
                    color: '#157eff'
                  },
                  {
                    offset: 1,
                    color: '#35c2ff'
                  }
                ])
              },
              shadowBlur: 10
            },
            lineStyle: {
              normal: {
                opacity: 0.9,
                width: 1,
                curveness: this.curveness // 连接线弧度
              },
              label: {
                color: '#fff'
                // position:'insideBottomRight',
              }
            },
            data: this.data,
            links: this.links
          }
        ]
      })
    }
  }
}
</script>
