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
      default: () => []
    },
    alarmTypeArrX: {
      type: Array,
      default: () => []
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
      default: 'main92'
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
      Chartheight: null,
      healthAll: [0, 0],
      chart: null,
      ww: null // 屏幕宽度
    }
  },
  watch: {
    height: {
      handler(val) {
        // console.log(val)
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
    },
    alarmTypeArrX: {
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
      if (!chartDom) {
        return
      }
      this.chart = echarts.init(chartDom)
      // var option = {
      //   // grid: [
      //   //   {
      //   //     top: '25%',
      //   //     bottom: '32%',
      //   //     right: '40%'
      //   //     // containLabel: true
      //   //     // left: '15%'
      //   //   }
      //   // ],
      //   // legend: {
      //   //   top: '5%',
      //   //   right: '20%',
      //   //   // color: '#fff',
      //   //   itemWidth: 10,
      //   //   itemHeight: 10,
      //   //   textStyle: { color: '#fff' },
      //   //   // data: ['未知', '通知', '警告', '问题', '严重', '灾难'],
      //   //   // selectedMode: false,
      //   //   // selected: {
      //   //   //   '未知': this.alarmTypeArr[0].value,
      //   //   //   '通知': this.alarmTypeArr[1].value,
      //   //   //   '警告': this.alarmTypeArr[2].value,
      //   //   //   '问题': this.alarmTypeArr[3].value,
      //   //   //   '严重': this.alarmTypeArr[4].value,
      //   //   //   '灾难': this.alarmTypeArr[5].value
      //   //   // }

      //   // },
      //   // color: ['#c2b8b8', '#10aaf4', '#FCCE10', '#E87C25', '#f01919', '#7a0c0c', '#7a0c0c'],
      //   series: [
      //     {
      //       // name: 'Area Mode',
      //       type: 'pie',
      //       radius: 50,
      //       center: ['50%', '50%'],
      //       // roseType: 'area',
      //       label: {
      //         show: true,
      //         // formatter: '{per|{d}%}{b|{b}}\n\n',
      //         // borderWidth: 0,
      //         // borderRadius: 4,
      //         // padding: [0, -25],
      //         // rich: {
      //         //   b: {
      //         //     color: '#fff',
      //         //     fontSize: 12,
      //         //     lineHeight: 33
      //         //   },
      //         //   per: {
      //         //     fontSize: 12,
      //         //     padding: [2, 4],
      //         //     borderRadius: 2
      //         //   }
      //         // },
      //         // position: 'inside',
      //         fontFamily: 'number',
      //         color: '#fff',
      //         fontSize: 16,
      //         formatter: '{b}:\n{d}%'
      //       },
      //       labelLine: {
      //         normal: {
      //           length: 2,
      //           length2: 12
      //         }
      //       },
      //       itemStyle: {
      //         borderRadius: 2
      //       },
      //       data: this.alarmTypeArr
      //     }
      //   ]
      // }
      // const dataAxis = ['111点', '击', '柱', '子', '或', '者', '两', '1']
      // const data = [220, 182, 191, 234, 290, 330, 310, 123, 442, 321, 90, 149, 210, 122, 133, 334, 198, 123, 125, 220]
      // const yMax = 500
      // const dataShadow = []
      // for (let i = 0; i < data.length; i++) {
      //   dataShadow.push(yMax)
      // }
      var option = {
        grid: [
          {
            top: '5%',
            bottom: '5%',
            right: '5%',
            // containLabel: true,
            left: '5%'
          }
        ],
        xAxis: {
          type: 'value',
          data: this.alarmTypeArrX,
          // axisLabel: {
          //   // inside: true,
          //   color: '#fff'
          // },
          axisTick: {
            show: false
          },
          axisLine: {
            show: false
          },
          axisLabel: {
            show: false,
            margin: 14,
            fontSize: 14,
            fontFamily: 'dapingA',
            textStyle: {
              color: '#65D5FF' // X轴文字颜色
            }
          },
          splitLine: {
            show: true,
            lineStyle: {
              color: 'rgba(77, 128, 254, 0.1)',
              width: 1
            }
          }
        },
        yAxis: {
          type: 'category',
          axisLine: {
            show: false
          },
          axisTick: {
            show: false
          },
          axisLabel: {
            show: false,
            margin: 14,
            fontSize: 14,
            fontFamily: 'dapingA',
            textStyle: {
              color: '#65D5FF' // X轴文字颜色
            }
          }
        },
        series: [
          {
            type: 'bar',
            showBackground: true,
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#83bff6' }, { offset: 0.5, color: '#188df0' }, { offset: 1, color: '#188df0' }]),

            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#83bff6' }, { offset: 0.5, color: '#188df0' }, { offset: 1, color: '#188df0' }]),
              normal: {
                // 以下为是否显示，显示位置和显示格式的设置了
                label: {
                  show: true,
                  fontFamily: 'number',
                  color: this.$store.state.settings.themeClass === 'dark' ? '#fff' : '#333',
                  textShadowColor: this.$store.state.settings.themeClass === 'dark' ? 'transparent' : '#fff',
                  fontSize: 18,
                  textShadowBlur: 8,
                  position: 'insideLeft',
                  // formatter: '{c}'
                  formatter: '{b} : {c}'
                }
              }
            },

            emphasis: {
              itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#2378f7' }, { offset: 0.7, color: '#2378f7' }, { offset: 1, color: '#83bff6' }])
              }
            },
            data: this.alarmTypeArr
          }
        ]
      }
      option && this.chart.setOption(option, true)
      // this.chart.on('click', (params) => {
      //   // console.log(params.data.name)
      //   var tempPath = ''
      //   switch (params.data.name) {
      //     case 'Windows':
      //       tempPath = '操作系统'
      //       break
      //     case 'Linux':
      //       tempPath = '操作系统'
      //       break
      //     case '负载均衡':
      //       tempPath = '网络设备'
      //       break
      //     case '路由器':
      //       tempPath = '网络设备'
      //       break
      //     case '防火墙':
      //       tempPath = '网络设备'
      //       break
      //     case '交换机':
      //       tempPath = '网络设备'
      //       break
      //     case '光纤交换机':
      //       tempPath = '光纤交换机'
      //       break
      //     case 'MySQL数据库':
      //       tempPath = '数据库'
      //       break
      //     case 'Oracle数据库':
      //       tempPath = '数据库'
      //       break
      //     case 'SQL Server数据库':
      //       tempPath = '数据库'
      //       break
      //     case '虚拟化':
      //       tempPath = '虚拟化'
      //       break
      //     case '虚拟机':
      //       tempPath = '虚拟化'
      //       break
      //     case 'IIS':
      //       tempPath = '中间件'
      //       break
      //     case '华为服务器硬件':
      //       tempPath = '服务器硬件'
      //       break
      //     case '浪潮服务器硬件':
      //       tempPath = '服务器硬件'
      //       break
      //     case 'HP服务器硬件':
      //       tempPath = '服务器硬件'
      //       break
      //     case 'NetApp存储':
      //       tempPath = '存储'
      //       break
      //     case 'Huawei存储':
      //       tempPath = '存储'
      //       break
      //     case 'HP存储':
      //       tempPath = '存储'
      //       break
      //     case '日立存储':
      //       tempPath = '存储'
      //       break
      //   }
      // if(tempPath){

      // }
      // this.$router.push({ name: tempPath })
      // if (!this.isRouter) {
      //   return
      // }
      // if (params.componentType === 'timeline') {
      //   return
      // }
      // const userStr = localStorage.getItem('user')
      // const userObj = JSON.parse(userStr)
      // const userId = userObj['userId']
      // const roleId = userObj['roleId']
      // getNetworkResource({
      //   start: 1,
      //   limit: 10,
      //   resourceName: params.name,
      //   class_id: this.class_id,
      //   userId,
      //   roleId,
      //   projectId: 10,
      //   classIdList: this.classIdList, // classIdList
      //   engine: 'Freemarker'
      // }).then((response) => {
      //   console.log(response)
      // const query = { name: response.datas[0] }
      // let tempPath = this.toRouter
      // switch (query.name.classId) {
      //   case 28:
      //     tempPath = 'windows'
      //     break
      //   case 29:
      //     tempPath = 'Linux'
      //     break
      //   case 253:
      //     tempPath = '负载均衡'
      //     break
      //   case 249:
      //     tempPath = '路由器'
      //     break
      //   case 55:
      //     tempPath = '防火墙'
      //     break
      //   case 252:
      //     tempPath = '交换机'
      //     break
      //   case 255:
      //     tempPath = '光纤交换机'
      //     break
      //   case 57:
      //     tempPath = '数据库-MySQL'
      //     break
      //   case 31:
      //     tempPath = '数据库-Oracle'
      //     break
      //   case 33:
      //     tempPath = '数据库-SQL Server'
      //     break
      //   case 265:
      //     tempPath = '虚拟化-vc'
      //     break
      //   case 259:
      //     tempPath = '中间件-IIS'
      //     break
      //   case 280:
      //     tempPath = '华为硬件'
      //     break
      //   case 281:
      //     tempPath = '浪潮硬件'
      //     break
      //   case 282:
      //     tempPath = '惠普硬件'
      //     break
      //   case 267:
      //     tempPath = '存储-NetApp'
      //     break
      //   case 269:
      //     tempPath = '存储-华为'
      //     break
      //   case 279:
      //     tempPath = '存储-惠普'
      //     break
      //   case 278:
      //     tempPath = '存储-日立'
      //     break
      // }
      // this.$router.push({ name: tempPath, query: query })
      // })
      // })
    }
  }
}
</script>
