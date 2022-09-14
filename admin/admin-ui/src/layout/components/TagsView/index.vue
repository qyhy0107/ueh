<template>
  <div id="tags-view-container" class="tags-view-container">
    <scroll-pane ref="scrollPane" class="tags-view-wrapper">
      <router-link v-for="tag in visitedViews" ref="tag" :key="tag.path" :class="isActive(tag)?'active':''"
        :to="{ path: tag.path, query: tag.query, fullPath: tag.fullPath }" tag="span" class="tags-view-item"
        @click.middle.native="closeSelectedTag(tag)" @contextmenu.prevent.native="openMenu(tag,$event)">
        {{ tag.title }}
        <span v-if="!tag.meta.affix" class="el-icon-close" @click.prevent.stop="closeSelectedTag(tag)" />
      </router-link>
    </scroll-pane>
    <ul v-show="visible" :style="{left:left+'px',top:top+'px'}" class="contextmenu">
      <li @click="refreshSelectedTag(selectedTag)">刷新页面</li>
      <li v-if="!(selectedTag.meta&&selectedTag.meta.affix)" @click="closeSelectedTag(selectedTag)">关闭当前</li>
      <li @click="closeOthersTags">关闭其他</li>
      <li @click="closeAllTags(selectedTag)">关闭所有</li>
    </ul>
  </div>
</template>

<script>
import ScrollPane from './ScrollPane'
import path from 'path'
export default {
  components: { ScrollPane },
  data() {
    return {
      visible: false,
      top: 0,
      left: 0,
      selectedTag: {},
      affixTags: []
    }
  },
  computed: {
    visitedViews() {
      return this.$store.state.tagsView.visitedViews
    },
    extendsViews() {
      return this.$store.state.tagsView.extendsViews
    },
    routes() {
      return this.$store.state.permission.routes
    }
  },
  watch: {
    $route() {
      this.addTags()
      this.moveToCurrentTag()
    },
    visible(value) {
      if (value) {
        document.body.addEventListener('click', this.closeMenu)
      } else {
        document.body.removeEventListener('click', this.closeMenu)
      }
    }
  },
  mounted() {
    this.initTags()
    this.addTags()
  },
  inject: ['reload'],
  methods: {
    isActive(route) {
      return route.path === this.$route.path
    },
    filterAffixTags(routes, basePath = '/') {
      let tags = []
      routes.forEach(route => {
        if (route.meta && route.meta.affix) {
          const tagPath = path.resolve(basePath, route.path)
          tags.push({
            fullPath: tagPath,
            path: tagPath,
            name: route.name,
            meta: { ...route.meta }
          })
        }
        if (route.children) {
          const tempTags = this.filterAffixTags(route.children, route.path)
          if (tempTags.length >= 1) {
            tags = [...tags, ...tempTags]
          }
        }
      })
      return tags
    },
    initTags() {
      const affixTags = this.affixTags = this.filterAffixTags(this.routes)
      for (const tag of affixTags) {
        // Must have tag name
        if (tag.name) {
          this.$store.dispatch('tagsView/addVisitedView', tag)
        }
      }
    },
    addTags() {
      const { name } = this.$route
      if (name) {
        this.$store.dispatch('tagsView/addView', this.$route)
      }
      return false
    },
    moveToCurrentTag() {
      const tags = this.$refs.tag
      // console.log(tags)
      this.$nextTick(() => {
        for (const tag of tags) {
          if (tag.to.path === this.$route.path) {
            this.$refs.scrollPane.moveToTarget(tag)
            // when query is different then update
            if (tag.to.fullPath !== this.$route.fullPath) {
              this.$store.dispatch('tagsView/updateVisitedView', this.$route)
            }
            // break
          }
        }
      })
    },
    refreshSelectedTag(view) {
      this.$store.dispatch('tagsView/delCachedView', view).then(() => {
        const { fullPath } = view
        this.$nextTick(() => {
          this.$router.replace({
            path: '/redirect' + fullPath
          })
        })
      })
    },
    closeSelectedTag(view) {
      // console.log(view)
      let temp = ''
      switch (view.title) {
        case ' 光纤交换机':
          temp = 'FSWOverviewVue'
          break
        case '博科光纤交换机':
          temp = 'FSW'
          break
        case '操作系统':
          temp = 'ServerOverview'
          // localStorage.removeItem(temp)
          break
        case '服务器硬件':
          temp = 'HardwareOverview'
          // localStorage.removeItem(temp)
          break
        case '网络设备':
          temp = 'NetworkOverview'
          // localStorage.removeItem(temp)
          break
        case '中间件':
          temp = 'MiddlewareOverview'
          // localStorage.removeItem(temp)
          break
        case '数据库':
          temp = 'DatabaseOverview'
          // localStorage.removeItem(temp)
          break
        case '虚拟化':
          temp = 'VirtualizationOverview'
          // localStorage.removeItem(temp)
          break
        case '虚拟化-vc':
          temp = 'VcenterOverview'
          break
        case 'windows':
          temp = 'Windows'
          break
        case 'Linux':
          temp = 'Linux'
          break
        case '华为硬件':
          temp = 'HWhardware'
          break
        case '浪潮硬件':
          temp = 'LChardware'
          break
        case '惠普硬件':
          temp = 'HPhardware'
          break
        case '防火墙':
          temp = 'Firewall'
          break
        case '路由器':
          temp = 'Router'
          break
        case '交换机':
          temp = 'Switches'
          break
        case '负载均衡':
          temp = 'LoadBalance'
          break
        case '中间件-IIS':
          temp = 'IIS'
          break
        case '数据库-Oracle':
          temp = 'Oracle'
          break
        case '数据库-SQL Server':
          temp = 'Sqls'
          break
        case '数据库-MySQL':
          temp = 'Mysql'
          break
        case '虚拟化-VMware':
          temp = 'Vm'
          break
        case '虚拟化-宿主机':
          temp = 'Hypervisor'
          break
        case '虚拟化-Datastore':
          temp = 'Datastore'
          break
        case '存储-NetApp':
          temp = 'NetAppSD'
          break
        case '光纤交换机':
          temp = 'FSW'
          break
        case '存储-华为':
          temp = 'HuaweiSD'
          break
        case '存储-惠普':
          temp = 'HP'
          break
        case '设备管理':
          temp = 'Device'
          break
      }
      // 属性缓存

      // console.log(view.name)
      if (view.title === '设备管理') {
        localStorage.setItem('deviceParams', JSON.stringify({
          start: 1,
          limit: 20,
          name: '',
          classIdName: '',
          classId: null,
          isEnable: 'Y',
          projectId: 10,
          engine: 'Freemarker'
        }))
      }
      this.$store.state.tagsView.extendsViews = []
      this.$store.state.tagsView.extendsViews.push(temp)
      this.$store.state.tagsView.extendsViews = []
      this.$store.dispatch('tagsView/delView', view).then(({ visitedViews }) => {
        // console.log(visitedViews)
        if (this.isActive(view)) {
          // console.log(this.isActive(view))
          window.onbeforeunload = function() {
            // 执行的代码
            return '确定要离开吗?'
          }
          // this.reload()
          this.toLastView(visitedViews, view)
        }
      })
    },
    closeOthersTags() {
      this.$router.push(this.selectedTag)
      this.$store.dispatch('tagsView/delOthersViews', this.selectedTag).then(() => {
        window.onbeforeunload = function() {
          // 执行的代码
          return '确定要离开吗?'
        }
        // this.reload()
        this.moveToCurrentTag()
      })
    },
    closeAllTags(view) {
      // 关闭的时候清空缓存
      localStorage.setItem('deviceParams', JSON.stringify({
        start: 1,
        limit: 20,
        name: '',
        classIdName: '',
        classId: null,
        isEnable: 'Y',
        projectId: 10,
        engine: 'Freemarker'
      }))
      this.$store.dispatch('tagsView/delAllViews').then(({ visitedViews }) => {
        if (this.affixTags.some(tag => tag.path === view.path)) {
          return
        }
        // this.reload()
        window.onbeforeunload = function() {
          // 执行的代码
          return '确定要离开吗?'
        }
        this.toLastView(visitedViews, view)
      })
    },
    toLastView(visitedViews, view) {
      const latestView = visitedViews.slice(-1)[0]
      if (latestView) {
        this.$router.push(latestView)
      } else {
        // now the default is to redirect to the home page if there is no tags-view,
        // you can adjust it according to your needs.
        if (view.name === 'Dashboard') {
          // to reload home page
          this.$router.replace({ path: '/redirect' + view.fullPath })
        } else {
          this.$router.push('/')
        }
      }
    },
    openMenu(tag, e) {
      const menuMinWidth = 105
      const offsetLeft = this.$el.getBoundingClientRect().left // container margin left
      const offsetWidth = this.$el.offsetWidth // container width
      const maxLeft = offsetWidth - menuMinWidth // left boundary
      const left = e.clientX - offsetLeft + 15 // 15: margin right

      if (left > maxLeft) {
        this.left = maxLeft
      } else {
        this.left = left
      }

      this.top = e.clientY
      this.visible = true
      this.selectedTag = tag
    },
    closeMenu() {
      this.visible = false
      window.onbeforeunload = function() {
        // 执行的代码
        return '确定要离开吗?'
      }
      // this.reload()
    }
  }
}
</script>

<style lang="scss" scoped>
@import '../../../assets/styles/index.scss';

.tags-view-container {
  height: 34px;
  width: 100%;
  background: $white;
  border-bottom: 1px solid rgba(74, 89, 110, 1);
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, .12), 0 0 3px 0 rgba(0, 0, 0, .04);

  .tags-view-wrapper {
    .tags-view-item {
      display: inline-block;
      position: relative;
      cursor: pointer;
      height: 26px;
      line-height: 26px;
      border: 1px solid rgba(74, 89, 110, 1) !important;
      color: rgba(109, 117, 129, 1) !important;
      background: rgba(140, 148, 157, 0.1) !important;
      padding: 0 8px;
      font-size: 12px;
      margin-left: 5px;
      margin-top: 4px;

      &:first-of-type {
        margin-left: 15px;
      }

      &:last-of-type {
        margin-right: 15px;
      }

      &.active {
        background: rgba(185, 197, 211, 0.30)!important;
        border: 1px solid rgba(255, 255, 255, 1)!important;
        font-weight: 600!important;
        color: #fff!important;
        // border-color:$main-blue;
        // &::before {
        //   content: '';
        //   background: $white;
        //   display: inline-block;
        //   width: 8px;
        //   height: 8px;
        //   border-radius: 50%;
        //   position: relative;
        //   margin-right: 2px;
        // }
      }
    }
  }

  .contextmenu {
    margin: 0;
    background: $white;
    z-index: 3000;
    position: absolute;
    list-style-type: none;
    padding: 5px 0;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 400;
    color: #333;
    box-shadow: 2px 2px 3px 0 rgba(0, 0, 0, .3);

    li {
      margin: 0;
      padding: 7px 16px;
      cursor: pointer;

      &:hover {
        background: #eee;
      }
    }
  }
}
</style>

<style lang="scss">
@import '../../../assets/styles/index.scss';

//reset element css of el-icon-close
.tags-view-wrapper {
  .tags-view-item {
    .el-icon-close {
      width: 16px;
      height: 16px;
      vertical-align: 2px;
      border-radius: 50%;
      text-align: center;
      transition: all .3s cubic-bezier(.645, .045, .355, 1);
      transform-origin: 100% 50%;

      &:before {
        transform: scale(.6);
        display: inline-block;
        vertical-align: -3px;
      }

      &:hover {
        background-color: #b4bccc;
        color: $white;
      }
    }
  }
}
</style>
