<template>
  <div :class="className" :style="{height:chartHeight,width:chartWidth}" />
</template>

<script>
import * as echarts from "echarts";
require("echarts/theme/macarons"); // echarts theme
import resize from "./mixins/resize";

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: "chart",
    },
    chartWidth: {
      type: String,
      default: "100%",
    },
    chartHeight: {
      type: String,
      default: "300px",
    },
    perfDatas: {
      type: Array,
      required: true,
    },
    autoResize: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      chart: null,
    };
  },
  watch: {
    perfDatas: {
      deep: true,
      handler(val) {
        if (this.chart != null) this.chart.clear();
        this.setOptions(val);
        if (this.chart != null) this.chart.resize();
      },
    },
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart();
      if (this.chart != null) this.chart.resize();
    });
  },

  beforeDestroy() {
    if (!this.chart) {
      return;
    }
    this.chart.dispose();
    this.chart = null;
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$el, "macarons");
      this.setOptions(this.perfDatas);
    },
    setOptions(perfDatas = []) {
      this.chart.setOption({
        color: [
          new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            {
              offset: 0,
              color: "rgba(233, 60, 167, 1)",
            },
            {
              offset: 1,
              color: "rgba(255, 171, 97, 1)",
            },
          ]),
          new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            {
              offset: 0,
              color: "rgba(107, 18, 204, 1)",
            },
            {
              offset: 1,
              color: "rgba(44, 107, 248, 1)",
            },
          ]),
          new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            {
              offset: 0,
              color: "rgba(40, 177, 255, 1)",
            },
            {
              offset: 1,
              color: "rgba(43, 138, 255, 1)",
            },
          ]),
        ],
        tooltip: {
          trigger: "item",
          formatter: "{a} <br/>{b} : {c} ({d}%)",
          extraCssText:
            "max-height:300px;overflow:auto;scrollbar-base-color:#000",
          confine: true,
          enterable: true,
          textStyle: {
            color: "#ffffff",
          },
        },
        legend: {
          orient: "vertical",
          left: 10,
          bottom: 50,
          data: ["网络设备", "用户行为日志", "其他"],
          textStyle: {
            color: "#fff",
            // fontSize :20
          },
        },
        series: this.perfDatas,
        //   title: {
        //   text: '数据接入',
        //   left: 10,
        //   top: 20,
        //   textStyle: {
        //       color: '#fff',
        //       fontSize :20
        //   }
        // },
      });
    },
  },
};
</script>
