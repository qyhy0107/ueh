<template>
  <div class="sidebar-logo-container" :class="{ collapse: collapse }">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" :to="toPath">
        <!-- <img v-if="logo" :src="logo" /> -->
        <svg-icon class-name="logo" icon-class="logo" style="width:22px;height:22px;transform: translate(-50%, 50%);position: absolute;" />
        <!-- <h1 v-else class="sidebar-title">{{ title }}</h1> -->
        <!-- <h1  class="sidebar-title">{{ title }}</h1> -->
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" :to="toPath">
        <!-- <img v-if="logo" :src="logo" class="sidebar-logo" />
        <h1 class="sidebar-title">{{ title }}</h1> -->
        <svg-icon class-name="logo" icon-class="logo" style="width:22px;height:22px;transform: translate(-80%, 50%);position: absolute;" />
        <h1 class="sidebar-title">{{ title }}</h1>
      </router-link>
    </transition>
  </div>
</template>

<script>
import logoImg from "@/assets/icons/svg/logo.svg"; 
import { getRouters } from "@/api/menu";
import {title} from "@/settings"
export default {
  name: "SidebarLogo",
  props: {
    collapse: {
      type: Boolean,
      required: true
    }
  },
  data() {
    return {
      title: title,
      logo: logoImg, 
      toPath:""
    };
  },
  created(){
          /* getRouters().then(res => {
            if(res.data[0].children){
                  this.toPath=res.data[0].path+"/"+res.data[0].children[0].path;
            }else{
                  this.toPath=res.data[0].path;
            }                 
      }) */
      this.toPath=localStorage.getItem("pathone");
  }
};
</script>

<style lang="scss" scoped>
@import "../../../assets/styles/index.scss";

.sidebarLogoFade-enter-active {
  transition: opacity 1.5s;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  width: 100%;
  height: 50px;
  line-height: 50px;
  // background: #2b2f3a;
  background-color: #09426b;
  text-align: center;
  overflow: hidden;
  border: 1px solid #4481e621;

  & .sidebar-logo-link {
    position: relative;
    height: 100%;
    width: 100%;

    & .sidebar-logo {
      width: 40px;
      // height: 32px;
      vertical-align: middle;
      margin-right: 18px;
    }

    & .sidebar-title {
      display: inline-block;
      margin-left: 12px;
      margin-top:0;
      margin-bottom: 0;
      color: $white;
      font-weight: 600;
      line-height: 50px;
      font-size: 14px;
      font-family: Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
      vertical-align: middle;
      // color: #606266;
      color: #fff;
    }
  }

  &.collapse {
    .sidebar-logo {
      margin-right: 0px;
    }
  }
}


</style>
