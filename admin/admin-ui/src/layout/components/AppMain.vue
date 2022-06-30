<template>
  <section class="app-main">
    <!-- <breadcrumb id="breadcrumb-container" class="breadcrumb-container" />  -->
    <transition name="fade-transform" mode="out-in">
      <keep-alive :exclude="extendsViews" include="Device">
        <router-view :key="key" />
      </keep-alive>
    </transition>
  </section>
</template>

<script>
// import Breadcrumb from '@/components/Breadcrumb'
export default {
  name: 'AppMain',
  // components: {
  //   Breadcrumb
  // },
  computed: {
    extendsViews() {
      return this.$store.state.tagsView.extendsViews
    },
    cachedViews() {
      return this.$store.state.tagsView.cachedViews
    },
    key() {
      return this.$route.path
    }
  }
}
</script>

<style lang="scss" scoped>
.app-main {
  /* 50= navbar  50  */
  min-height: calc(100vh);
  width: 100%;
  position: relative;
  background-origin:padding-box;
  background-size:auto 100%;
  background-position:center center;
}

.fixed-header+.app-main {
  padding-top: 50px;
}

.hasTagsView {
  .app-main {
    min-height: calc(100vh - 114px);
  }

  .fixed-header+.app-main {
    padding-top: 84px;
  }
}
.light{
 .app-main {
  background-image:none;
  background-color: #F7F8FA;
 }
}
</style>

<style lang="scss">
// fix css style bug in open el-dialog
.el-popup-parent--hidden {
  .fixed-header {
    padding-right: 15px;
  }
}
.el-dialog {
  background-origin:padding-box;
  background-size:auto 100%;
  background-position:center center;
}

</style>
