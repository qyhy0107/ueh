<template>
  <div class="noneDateBox">
    <img src="../../assets/common/icon_nodata.svg" :width="width" :height="height">
    <div>&nbsp;&nbsp;
      <i v-if="iconImg" style="color:rgba(24, 144, 255, 1);cursor: pointer" class="el-icon-paperclip" />
      <!-- {{ lableshow }} -->
    </div>
  </div>
</template>

<script>
export default {
  name: 'Deficiency',
  props: {
    iconImg: {
      type: Boolean,
      default: false
    },
    height: {
      type: String,
      default: 'auto'
    },
    width: {
      type: String,
      default: '25%'
    },
    lableshow: {
      type: String,
      default: '暂无数据~'
    }
  }
}
</script>

<style scoped></style>
