<template>
  <div id="app" :class="themeClass">
    <router-view v-if="isRouterAlive" />
  </div>
</template>

<script>
export default {
  name: 'App',
  provide() {
    // 父组件中通过provide来提供变量，在子组件中通过inject来注入变量。
    return {
      reload: this.reload
    }
  },
  data() {
    return {
      isRouterAlive: true // 控制视图是否显示的变量
    }
  },
  computed: {
    themeClass: {
      get() {
        var bodyEle = document.body
        bodyEle.className = ''
        bodyEle.classList.add(this.$store.state.settings.themeClass)
        return this.$store.state.settings.themeClass
      },
      set(val) {
        this.$store.dispatch('settings/changeClass', {
          key: 'themeClass',
          value: val
        })
      }
    }
  },
  methods: {
    reload() {
      this.isRouterAlive = false // 先关闭，
      this.$nextTick(function() {
        this.isRouterAlive = true // 再打开
      })
    }
  }
}
</script>
<style lang="scss">
body .el-table th.gutter {
  display: table-cell !important;
}

body .el-table colgroup.gutter {
  display: table-cell !important;
}
</style>
