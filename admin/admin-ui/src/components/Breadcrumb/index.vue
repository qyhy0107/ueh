<template>
  <el-breadcrumb class="app-breadcrumb" separator="/">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="(item, index) in levelList" :key="item.path">
        <span
          v-if="item.redirect === 'noRedirect' || index == levelList.length - 1"
          class="no-redirect"
          >{{ item.meta.title }}</span
        >
        <a v-else @click.prevent="handleLink(item)">{{ item.meta.title }}</a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script>
import pathToRegexp from "path-to-regexp";

export default {
  data() {
    return {
      levelList: null
    };
  },
  watch: {
    $route(route) {
      // if you go to the redirect page, do not update the breadcrumbs
      if (route.path.startsWith("/redirect/")) {
        return;
      }
      this.getBreadcrumb();
    }
  },
  created() {
    this.getBreadcrumb();
  },
  methods: {
    getBreadcrumb() {
      // only show routes with meta.title
      let matched = this.$route.matched.filter(
        item => item.meta && item.meta.title
      );
      const first = matched[0];
      // if (!this.isDashboard(first)) {
      //   if (this.$route.path === "/box/index") {
      //     // matched[0].name=this.$route.query.name;
      //     matched[0].meta.title=this.$route.query.name;
      //   }
      //   matched = [{ path: "/index", meta: { title: "首页" } }].concat(matched);
      // }
      this.levelList = matched.filter(
        item => item.meta && item.meta.title && item.meta.breadcrumb !== false
      );
    },
    isDashboard(route) {
      const name = route && route.name;
      if (!name) {
        return false;
      }
      return name.trim() === "首页";
    },
    pathCompile(path) {
      const { params } = this.$route;
      var toPath = pathToRegexp.compile(path);
      return toPath(params);
    },
    handleLink(item) {
      const { redirect, path } = item;
      if (redirect) {
        this.$router.push(redirect);
        return;
      }
      this.$router.push(this.pathCompile(path));
    }
  }
};
</script>

<style lang="scss" scoped>
.app-breadcrumb.el-breadcrumb {
  display: inline-block;
  font-size: 14px;
  line-height: 50px;
  margin-left: 20px;
  vertical-align: text-bottom;
  .no-redirect {
    color: #97a8be;
    cursor: text;
  }
}
.el-breadcrumb__item:last-child{
  .el-breadcrumb__inner{
    .no-redirect{
      color:#F5F7FA!important;
    }
  }
}
.light{
  .app-breadcrumb.el-breadcrumb {
  .no-redirect {
     color:#7F819D;
  }
}
.el-breadcrumb__item:last-child{
  .el-breadcrumb__inner{
    .no-redirect{
      color: #333333!important;
    }
  }
}
}
</style>
