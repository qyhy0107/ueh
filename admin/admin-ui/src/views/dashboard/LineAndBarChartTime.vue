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
    }
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
        // color:['#ff20cf','yellow','#02feff','#f6c329','#53e961',"#7e2dff"],
        // color: [
        //     "#76f2f2",
        //     "#fc97af",
        //     "#87f7cf",
        //     "#f7f494",
        //     "#72ccff",
        //     "#f7c5a0",
        //     "#d4a4eb",
        //     "#d2f5a6",
        // ],
         color: [
            '#1a4aff',
            "#76f2f2",
            "#ffaf51",
            "#7c60ad",
            "#ffee51",
            "#0389c4",
            "#7fd9f7",
            "#d12482",
            "#a591f7",
            "#c941c4",
            "#d6d962"
        ],
        //    "color": [
        //      "#797fba",
        //     "#3fb1e3",
        //     "#6be6c1",
        //     "#626c91",
        //     "#a0a7e6",
        //     "#c4ebad",
        //     "#96dee8"
        // ],
        xAxis: {
          type: 'time',
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
          // padding: [5, 5],
          extraCssText:'max-height:300px;overflow:auto;scrollbar-base-color:#000',
          confine: true,
          enterable:true,
          formatter: function (params) {
            // params[0].axisValueLabel=params[0].data[0];
            // alert(params[0].axisValueLabel)
            // 当我们想要自定义提示框内容时，可以先将鼠标悬浮的数据打印出来，然后根据需求提取出来即可
            var pa=params[0].axisValueLabel=params[0].data[0]

            for(var i=0;i<params.length;i++){
              pa=pa+'<br>'+params[i].marker+params[i].seriesName+params[i].data[1]
            }
            return pa
          },

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
             color:this.$store.state.settings.themeClass=='dark'?"#ccc":"#333",
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
