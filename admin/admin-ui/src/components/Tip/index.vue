<template>
  <div class="box">
  </div>
  <!-- <div class="left">
      <div><img src="../../assets/image/T1-warning-警告 (4).png" alt="" height="20px"><span>{{alarm.count3+alarm.count5+alarm.count8+alarm.count10}}</span></div> -->
      <!--<div><img src="../../assets/image/T1-warning-警告 (6).png" alt="" height="20px"><span>{{alarm.count11}}</span></div>-->
      <!-- <div><img src="../../assets/image/T1-warning-警告 (5).png" alt="" height="20px"><span>{{alarm.count11+alarm.count12}}</span></div>
      <div><img src="../../assets/image/T1-warning-警告 (7).png" alt="" height="20px"><span>{{alarm.count1+alarm.count2+alarm.count4+alarm.count6+alarm.count7+alarm.count9}}</span></div>
    </div> -->
    <!-- <div class="right" v-if="latestEvent.occurtime">{{latestEvent.occurtime}}:[{{latestEvent.host}}]{{latestEvent.description}}</div>
    <div class="right" v-if="latestEvent.time">来访{{latestEvent.ip}}到[{{getYssDescription(latestEvent.yys)}}]时发生异常:{{latestEvent.info}}</div>
  </div>
</template>

<script>
  import {getLatestEvent,getEventCount1d} from '@/api/operation/alarm'
export default {
  data() {
    return {
      alarm:{
        /*diasterCount:0,
        highCount:0,
        averageCount:0,
        warningCount:0*/
        count1:0,
        count2:0,
        count3:0,
        count4:0,
        count5:0,
        count6:0,
        count7:0,
        count8:0,
        count9:0,
        count10:0,
        count11:0,
        count12:0
      },
      latestEvent:{
        occurtime:'',
        host:'',
        type:'',
        yys:'',
        description:''
      }
    };
  },
  watch: {

  },
  created() {
    this.getEventCount();
    this.getLatestEvent();
  },
  methods: {
    getEventCount(){
      getEventCount1d().then(response=>{
        let eventCount=response.datas;
        if(!eventCount.length)return
        for(var i=0;i<eventCount.length;i++){
          var item=eventCount[i];
          //暂时去业务日志告警
          /*if(item.priority===5)this.alarm.diasterCount=item.qty;
          if(item.priority===4)this.alarm.highCount=item.qty;
          if(item.priority===3)this.alarm.averageCount=item.qty;
          if(item.priority===2)this.alarm.warningCount=item.qty;*/
          if(item.priority==="1"){
            this.alarm.count1=item.qty;
          }else if(item.priority==="2"){
            this.alarm.count2=item.qty;
          }else if(item.priority==="3"){
            this.alarm.count3=item.qty;
          }else if(item.priority==="4"){
            this.alarm.count4=item.qty;
          }else if(item.priority==="5"){
            this.alarm.count5=item.qty;
          }else if(item.priority==="6"){
            this.alarm.count6=item.qty;
          }else if(item.priority==="7"){
            this.alarm.count7=item.qty;
          }else if(item.priority==="8"){
            this.alarm.count8=item.qty;
          }else if(item.priority==="9"){
            this.alarm.count9=item.qty;
          }else if(item.priority==="10"){
            this.alarm.count10=item.qty;
          }else if(item.priority==="11"){
            this.alarm.count11=item.qty;
          }else if(item.priority==="12"){
            this.alarm.count12=item.qty;
          }
        }
      });
    },
    getLatestEvent(){
      getLatestEvent().then(response=>{
        if(!response.empty){
          this.latestEvent=response.datas[0];
        }
      });
    },
    getTypeDescription(typeCode){
      if(typeCode==undefined){
        return "";
      }else if(typeCode==="1"){
        return "监委请求";
      }else if(typeCode==="2"){
        return "处理请求并发送http响应";
      }else if(typeCode==="3"){
        return "请求连接中断";
      }else if(typeCode==="4"){
        return "返回请求成功";
      }else if(typeCode==="5"){
        return "请求响应失败";
      }else if(typeCode==="6"){
        return "收到运营商结果请求";
      }else if(typeCode==="7"){
        return "处理结果请求并发送http响应";
      }else if(typeCode==="8"){
        return "请求连接中断";
      }else if(typeCode==="9"){
        return "结果请求成功";
      }else if(typeCode==="10"){
        return "结果请求响应失败";
      }else if(typeCode==="11"){
        return "IP认证标识";
      }else if(typeCode==="12"){
        return "请求头校验标识";
      }
    },
    getYssDescription(yssCode){
      if(yssCode==undefined){
        return "";
      }else if(yssCode==="01"){
        return "电信";
      }else if(yssCode==="02"){
        return "移动";
      }else if(yssCode==="03"){
        return "联通";
      }else if(yssCode==="99"){
        return "监委";
      }

    }
  }
};
</script>

<style lang="scss" scoped>
.box {
  height: 30px;
  overflow: hidden;
  // float: left;
  width: 100%;
  background-color: transparent;
  font-size: 12px;
  .left{
    float:left;
    overflow: hidden;
    div{
      float: left;
      margin: 0 15px;
      padding-top: 5px;
      line-height: 100%;
    }
    span,img{
      line-height: 100%;
      vertical-align: middle;
    }
  }
  .right {
    overflow: hidden;
    line-height: 30px;
    float: right;
    padding-right:10px ;
  }
}
</style>
