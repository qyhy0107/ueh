<template>
  <div :class="className" :style="{height:chartHeight,width:chartWidth}" />
</template>

<script>
  import * as  echarts  from "echarts";
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
      chartYtitle:{
        type:String,
        default:''
      },
      chartXtitle:{
        type:String,
        default:''
      },
      legendData:{
        type: Array,
        required: true
      },
    },
    data() {
      return {
        chart: null
      }
    },
    watch: {
      chartData: {
        deep: true,
        handler(val) {
          if(this.chart!=null)this.chart.clear();
          this.setOptions(val);
          if(this.chart!=null)this.chart.resize();
        }
      },
    },
    mounted() {
      this.$nextTick(() => {
        this.initChart()
        if(this.chart!=null)this.chart.resize();
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
        this.setOptions(this.chartData)
      },
      setOptions(perfDatas = []) {
        this.chart.setOption({
          /*legend:true,*/
          backgroundColor: 'transparent',
          color:['#016fe2','#02feff','#f6c329','#53e961',"#7e2dff"],
          xAxis: {
            type: 'category',
            splitLine: {
              show: true,
              lineStyle:{
                width: .3,
                type: 'solid'
              },
              splitArea : {show : false},

            },
            axisLine:{
              lineStyle:{
                // color:["#ccc"],
                  color:this.$store.state.settings.themeClass=='dark'?"#ccc":"#333",
                width:.3
              } ,
            },

          },
          grid: {
            left: 45,
            right: 20,
            bottom: 2,
            top:28,
            containLabel: true,
          },

          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'cross',
              label: {
                backgroundColor: '#183FA5',
                // color:"red"
                // borderColor:"red",
                // borderWidth:0,
                shadowBlur:0
              },
            },
            showContent:true,
            padding: [5, 5],
            extraCssText:'max-height:300px;overflow:auto;scrollbar-base-color:#000',
            confine: true,
            enterable:true
          },
          legend: {
            data: this.legendData,
            textStyle:{//图例文字的样式
              // color:'#f7f7f7',
                color:this.$store.state.settings.themeClass=='dark'?"#f7f7f7":"#333",
            }
          },
          yAxis: {
            type:'value',
            name:this.chartYtitle,
            splitArea : {show : false},
            splitLine: {
              show: true,
              lineStyle:{
                width: .3,
                type: 'solid'
              },

            },
            axisLine:{
              lineStyle:{
                // color:["#ccc"],
                  color:this.$store.state.settings.themeClass=='dark'?"#f7f7f7":"#ccc",
                width:.3
              } ,

            }
          },
          series: perfDatas
        },true);


        this.chart.resize();
      }
    }
  }
</script>
