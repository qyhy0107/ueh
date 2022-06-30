<template>
  <div :class="className" :style="{ height: windowWidth > 1400 ? chartHeight : '300px', width: chartWidth }" />
</template>

<script>
// import echarts from 'echarts'
import * as echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    chartWidth: {
      type: String,
      default: '100%'
    },
    chartHeight: {
      type: String,
      default: '350px'
    },
    autoResize: {
      type: Boolean,
      default: true
    },
    chartData: {
      type: Array,
      required: true
    },
    chartYtitle: {
      type: String,
      default: ''
    },
    chartXtitle: {
      type: String,
      default: ''
    },
    windowWidth: {
      type: Number,
      default: 1366
    }
  },
  data() {
    return {
      chart: null,

      chartName: []
    }
  },
  computed: {
    isShow: function() {
      return this.chartData.length > 1
    }
  },
  watch: {
    chartData: {
      deep: true,
      handler(val) {
        if (val) {
          this.chartName = []
          val.forEach((item) => {
            this.chartName.push(item.name)
          })
        }
        if (this.chart != null) this.chart.clear()
        this.setOptions(val)
        if (this.chart != null) this.chart.resize()
      }
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
      if (this.chart != null) this.chart.resize()
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
      // if (this.chart) {
      //   this.chart.dispose()
      // }
      this.chart = echarts.init(this.$el, 'macarons')
      // console.log(this.chartData)
      //  symbol: 'triangle',
      this.setOptions(this.chartData)
    },
    setOptions(perfDatas = []) {
      this.chart.setOption(
        {
          title: {
            text: this.chartYtitle ? '(' + this.chartYtitle + ')' : '',
            left: '10',
            top: '40%',
            rotate: '-90deg',
            textStyle: {
              fontSize: 12,
              color: '#5588ed',
              rotate: '-90deg'
            }

          },
          /* legend:true,*/
          backgroundColor: 'transparent',
          color: ['#4a57ff'],
          xAxis: {
            type: 'time',
            // name: '123',
            splitLine: {
              show: true,
              lineStyle: {
                width: 0.3,
                type: 'solid'
              },
              splitArea: { show: false }
            },
            axisLine: {
              lineStyle: {
                color: this.$store.state.settings.themeClass === 'dark' ? '#ccc' : '#333',
                width: 0.3
              }
            }
          },
          grid: {
            left: 45,
            right: 20,
            bottom: 50,
            top: this.isShow ? 30 : 10,
            containLabel: true
          },
          legend: {
            show: this.isShow,
            data: this.chartName,
            type: 'scroll', // 设置图例翻页
            itemWidth: 8, // 图例图形宽度
            itemHeight: 8,
            orient: 'horizontal', // 图例横向排布
            // itemGap: 20,
            left: 'center',				// 图例位置
            // top: 8,
            // right:'30%',
            // padding: 20,
            itemGap: 20,
            symbolKeepAspect: true,
            textStyle: {
              fontSize: 10,
              // padding: 4,
              margin: 10,
              padding: 5,

              // overflow: 'none',
              color: this.$store.state.settings.themeClass === 'dark' ? '#ccc' : '#333'
            }
          },
          dataZoom: [
            {
              show: !!perfDatas.length,
              start: 0,
              end: 100,
              bottom: '6%',
              height: 20
            }
          ],
          tooltip: {
            confine: true,
            trigger: 'axis',
            // marker: '123',
            formatter: function(params) {
              // console.log(params)
              var res = '<div>' + params[0].axisValueLabel + '</div>' // 字符串形式的html标签会被echarts转换渲染成数据，这个res主要是画的tooltip里的上部分的标题部分
              for (var i = 0; i < params.length; i++) {
                // 因为是个数组，所以要遍历拿到里面的数据，并加入到tooltip的数据内容部分里面去
                res += `<div style="color: #fff;font-size: 14px; padding:0 12px;line-height: 24px">
                  <span style="display:inline-block;margin-right:5px;border-radius:2px;width:10px;height:10px;background-color:${[params[i].color]};"></span>
                 <span style="display:inline-block;margin-right:5px;color:${[params[i].color]};">${params[i].seriesName}</span> 
                  ${params[i].data[1]}
                </div>`
              }
              return res
            },
            textStyle: {
              color: '#ffffff'
            },
            axisPointer: {
              type: 'cross',
              label: {
                backgroundColor: '#183FA5',
                shadowBlur: 0
                // color: "#fff",
              }
            },
            showContent: true,
            padding: [5, 5],
            extraCssText: 'max-height:300px;overflow:auto;scrollbar-base-color:#000',
            // confine: true,
            enterable: true
          },
          yAxis: {
            type: 'value',
            splitArea: { show: false },
            splitLine: {
              show: true,
              lineStyle: {
                width: 0.3,
                type: 'solid'
              }
            },
            axisLine: {
              lineStyle: {
                // color:["#ccc"],
                color: this.$store.state.settings.themeClass === 'dark' ? '#ccc' : '#333',
                width: 0.3
              }
            }
          },
          series: perfDatas
        },
        true
      )
      this.chart.resize()
    }
  }
}
</script>
