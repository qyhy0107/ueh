<template >
  <div class="drawer-container">
    <div>
      <h3 class="drawer-title">主题设置</h3>

      <div class="drawer-item">
        <span>主题颜色</span>
        <el-switch
          v-model="themeClass"
          active-text="亮色主题"
          inactive-text="暗色主题"
          active-value="light"
          inactive-value="dark"
          >
        </el-switch>
      </div>
          <div class="drawer-item">
        <span>固定 Header</span>
        <el-switch v-model="fixedHeader" class="drawer-switch" />
      </div>
      <div class="drawer-item">
        <span>开启 Tags-Views</span>
        <el-switch v-model="tagsView" class="drawer-switch" />
      </div>
    </div>
  </div>
</template>

<script>
import ThemePicker from '@/components/ThemePicker'
export default {
  components: { ThemePicker },
  data() {
    return {}
  },
  computed: {
     themeClass: {
      get() {
        return this.$store.state.settings.themeClass
      },
      set(val) {
        localStorage.setItem("themeClass",val)
        this.$store.dispatch('settings/changeClass', {
          key: 'themeClass',
          value: val
        })
        location.reload();
      }
    },
    fixedHeader: {
      get() {
        return this.$store.state.settings.fixedHeader
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'fixedHeader',
          value: val
        })
      }
    },
    tagsView: {
      get() {
        return this.$store.state.settings.tagsView
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'tagsView',
          value: val
        })
      }
    },
    sidebarLogo: {
      get() {
        return this.$store.state.settings.sidebarLogo
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'sidebarLogo',
          value: val
        })
      }
    },
  },
  methods: {
    themeChange(val) {
      this.$store.dispatch('settings/changeClass', {
        key: 'themeClass',
        value: val
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.drawer-container {
  padding: 24px;
  font-size: 14px;
  line-height: 1.5;
  word-wrap: break-word;
  .drawer-title {
    margin-bottom: 12px;
    color: rgba(0, 0, 0, .85);
    font-size: 14px;
    line-height: 22px;
  }
  .drawer-item {
    color: rgba(0, 0, 0, .65);
    font-size: 14px;
    padding: 12px 0;
  }
  .drawer-switch {
    float: right
  }
}
</style>
