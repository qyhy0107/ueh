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
    severityNumberList: {
      type: Array,
      default: () => []
    },
    severityList: {
      type: Array,
      default: () => []
    },
    idname: {
      type: String,
      default: 'main1'
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
      default: '350px'
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
    severityList: {
      handler(val, val1) {
        // console.log(val, val1)
        this.initChart()
      },
      deep: true
    },
    severityNumberList: {
      handler() {
        // console.log(val, val1)
        // this.chart.dispose()
        // this.chart = null
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
        // backgroundColor:'#031d33',
        animation: true,
        grid: {
          top: '5%',
          bottom: '32%',
          right: '0%',
          left: '15%'
        },
        xAxis: {
          data: this.severityList,
          axisLine: {
            show: false // 隐藏X轴轴线
          },
          axisTick: {
            show: false // 隐藏X轴轴线
          },
          splitLine: {
            show: false,
            lineStyle: {
              color: 'rgba(77, 128, 254, 0.2)',
              width: 2
            }
          },
          axisLabel: {
            show: true,
            margin: 14,
            fontSize: 16,
            fontFamily: 'dapingA',
            textStyle: {
              color: '#65D5FF' // X轴文字颜色
            }
          }
        },
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
                color: '#65D5FF'
              }
            }
          }
        ],
        series: [
          {
            name: '设备在线率',
            type: 'bar',
            barWidth: 16,
            label: {
              show: true,
              fontSize: 20,
              position: 'top',
              color: '#d7d7d7',
              fontFamily: 'number'
              // valueAnimation: true
            },
            itemStyle: {
              normal: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  {
                    offset: 0,
                    color: 'rgba(146, 225, 255, 1)'
                  },
                  {
                    offset: 1,
                    color: 'rgba(0, 151, 251, 1)'
                  }
                ])
              }
            },
            data: this.severityNumberList,
            z: 10,
            zlevel: 0
          },
          {
            // 分隔
            type: 'pictorialBar',
            itemStyle: {
              normal: {
                color: '#0F375F'
              }
            },
            symbolRepeat: 'fixed',
            symbolMargin: 6,
            symbol: 'rect',
            symbolClip: true,
            symbolSize: [18, 2],
            symbolPosition: 'start',
            symbolOffset: [1, 1],
            data: this.severityNumberList,
            width: 2,
            z: 0,
            zlevel: 1
          },
          {
            name: '外框',
            type: 'bar',
            barGap: '-110%', // 设置外框粗细
            // data: [100, 100, 100, 100, 100, 100, 100],
            barWidth: 16,
            itemStyle: {
              normal: {
                color: 'transparent', // 填充色
                // barBorderRadius: 0, //圆角半径
                label: {
                  // 标签显示位置
                  show: false,
                  position: 'top' // insideTop 或者横向的 insideLeft
                }
              }
            },
            z: 0
          },
          {
            name: '背影',
            type: 'line',
            smooth: true, // 平滑曲线显示
            showAllSymbol: false, // 显示所有图形。
            symbolSize: 0,
            lineStyle: {
              width: 0
            },
            areaStyle: {
              color: 'rgba(0, 151, 251, 0.1)'
            },
            data: this.severityNumberList,
            z: 5
          }
        ],
        dataZoom: [
          {
            type: 'slider',
            show: false,
            xAxisIndex: [0],
            endValue: 5,
            startValue: 0
          }
        ]
      }
      // var option = {
      //   title: [
      //     {
      //       // text: 'Tangential Polar Bar Label Position (middle)'
      //     }
      //   ],

      //   polar: {
      //     radius: [2, '75%']
      //   },
      //   angleAxis: {
      //     max: 100,
      //     startAngle: 0
      //   },
      //   radiusAxis: {
      //     type: 'category',
      //     data: ['低级', '中级', '高级', '紧急']
      //   },
      //   tooltip: {},
      //   series: {
      //     type: 'bar',
      //     data: [35, 40, 10, 15],
      //     coordinateSystem: 'polar',
      //     label: {
      //       show: true,
      //       position: 'middle',
      //       formatter: '{b}: {c}'
      //     }
      //   }
      // }
      option && this.chart.setOption(option)
    }
    // initChart() {
    //   this.chart = echarts.init(this.$el, 'macarons')
    //   this.data ? this.setOptions(this.data) : null
    // },
    // setOptions({ expectedData, actualData } = {}) {
    //   this.chart.setOption({
    //     itemStyle: {
    //       normal: {
    //         color: '#000'
    //       },
    //       shadowBlur: 2
    //     },
    //     animationDuration: 1500,
    //     animationEasingUpdate: 'quinticInOut',
    //     xAxis: {
    //       show: false,
    //       type: 'value'
    //     },
    //     yAxis: {
    //       show: false,
    //       type: 'value'
    //     },
    //     series: [
    //       {
    //         type: 'graph',
    //         coordinateSystem: 'cartesian2d',
    //         // legendHoverLink: false,
    //         symbol: 'roundRect',
    //         hoverAnimation: true,
    //         nodeScaleRatio: false,
    //         // 建头
    //         // edgeSymbol: ['circle', 'arrow'],
    //         // edgeSymbolSize: this.ww > 1400 ? [2, 15] : [1, 10],
    //         edgeLabel: {
    //           show: false,
    //           normal: {
    //             show: true,
    //             position: 'middle',
    //             textStyle: {
    //               fontSize: 12
    //             },
    //             formatter: '{c}'
    //           }
    //         },
    //         focusNodeAdjacency: true,
    //         roam: true,
    //         categories: [
    //           {
    //             name: '一级关系',
    //             itemStyle: {
    //               // 可配置颜色
    //               normal: {
    //                 color: '#A170DD'
    //               }
    //             }
    //           },
    //           {
    //             name: '二级关系',
    //             itemStyle: {
    //               // 可配置颜色
    //               normal: {
    //                 color: '#41B1EF'
    //               }
    //             }
    //           },
    //           {
    //             name: '三级关系',
    //             itemStyle: {
    //               // 可配置颜色
    //               normal: {
    //                 color: '#667AED'
    //               }
    //             }
    //           }
    //         ],
    //         // 圆形上面的文字
    //         label: {
    //           show: true,
    //           fontSize: 16,
    //           formatter: function(params) {
    //             if (params.data.percentage) {
    //               return `{subt|${params.name}}\n\n{mi|${params.data.percentage[0]}} {mt|${params.data.percentage[1]}}`
    //             }
    //             if (params.data.label) {
    //               return `{subt|${params.name}}\n\n{mt|${params.data.label}}`
    //             } else {
    //               return `{subt|${params.name}}`
    //             }
    //           },
    //           rich: {
    //             subt: {
    //               fontSize: this.ww > 1400 ? 14 : 10,
    //               color: '#fff'
    //             },
    //             mt: {
    //               fontSize: this.ww > 1400 ? 12 : 14,
    //               color: '#fff'
    //             },
    //             mi: {
    //               fontSize: 10,
    //               color: '#fff'
    //             }
    //           }
    //         },
    //         // force: {
    //         //   repulsion: 1000
    //         // },
    //         force: {
    //           repulsion: 2500
    //         },
    //         itemStyle: {
    //           normal: {
    //             color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
    //               {
    //                 offset: 0,
    //                 color: '#157eff'
    //               },
    //               {
    //                 offset: 1,
    //                 color: '#35c2ff'
    //               }
    //             ])
    //           },
    //           shadowBlur: 10
    //         },
    //         lineStyle: {
    //           normal: {
    //             opacity: 0.9,
    //             width: 1,
    //             curveness: this.curveness // 连接线弧度
    //           },
    //           label: {
    //             color: '#fff'
    //             // position:'insideBottomRight',
    //           }
    //         },
    //         data: this.data,
    //         links: this.links
    //       }
    //     ]
    //   })
    // }
  }
}
</script>
