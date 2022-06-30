import Vue from "vue";
import Router from "vue-router";

Vue.use(Router);

/* Layout */
import Layout from "@/layout";

/**
 * Note: 路由配置项
 *
 * hidden: true                   // 当设置 true 的时候该路由不会再侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
 * alwaysShow: true               // 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
 *                                // 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
 *                                // 若你想不管路由下面的 children 声明的个数都显示你的根路由
 *                                // 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
 * redirect: noRedirect           // 当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
 * name:'router-name'             // 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
 * meta : {
    roles: ['admin','editor']    // 设置该路由进入的权限，支持多个权限叠加
    title: 'title'               // 设置该路由在侧边栏和面包屑中展示的名字
    icon: 'svg-name'             // 设置该路由的图标，对应路径src/icons/svg
    breadcrumb: false            // 如果设置为false，则不会在breadcrumb面包屑中显示
  }
 */

// 公共路由
export const constantRoutes = [
  {
    path: "/redirect",
    component: Layout,
    hidden: true,
    children: [
      {
        path: "/redirect/:path*",
        component: () => import("@/views/redirect")
      }
    ]
  },
  {
    path: "/login",
    component: () => import("@/views/login"),
    hidden: true
  },
  {
    path: "/404",
    component: () => import("@/views/error/404"),
    hidden: true
  },
  {
    path: "/401",
    component: () => import("@/views/error/401"),
    hidden: true
  },
  {
    path: "/checkUser",
    component: () => import("@/views/checkUser/index"),
    hidden: true
  },
  // {
  //   path: "",
  //   component: Layout,
  //   children: [
  //     {
  //       path: "attrIndex",
  //       component: () => import("@/views/cm/device/attrIndex"),
  //       name: "attrIndex",
  //       // meta: { title: "属性维护"},
  //       hidden: true
  //     }
  //   ]
  // },
  // {
  //   path: "",
  //   component: Layout,
  //   redirect: "server-window",
  //   children: [
  //     {
  //       path: "server-window",
  //       component: () => import("@/views/operation/server-window/index"),
  //       name: "服务器",
  //       meta: {
  //         title: "服务器",
  //         icon: "dashboard",
  //         noCache: true,
  //         affix: true
  //       },
  //       hidden: true
  //     }
  //   ]
  // },
  // {
  //   path: "",
  //   component: Layout,
  //   redirect: "switches-operation",
  //   children: [
  //     {
  //       path: "switches-operation",
  //       component: () => import("@/views/operation/switches/index"),
  //       name: "网络设备",
  //       meta: {
  //         title: "网络设备",
  //         icon: "dashboard",
  //         noCache: true,
  //         affix: true
  //       },
  //       hidden: true
  //     }
  //   ]
  // },
  /* {
    path: "",
    component: Layout,
    redirect: "topo-editor",
    children: [
      {
        path: "topo-editor",
        component: () => import("@/views/cm/topo/topo-editor"),
        name: "拓扑编辑",
        meta: {
          title: "拓扑编辑",
          icon: "dashboard",
          noCache: true,
          affix: true
        },
        hidden: true
      }
    ]
  }, */
  // {
  //   path: "",
  //   component: Layout,
  //   redirect: "middleware-operation",
  //   children: [
  //     {
  //       path: "middleware-operation",
  //       component: () => import("@/views/operation/middleware/index"),
  //       name: "中间件",
  //       meta: {
  //         title: "中间件",
  //         icon: "dashboard",
  //         noCache: true,
  //         affix: true
  //       },
  //       hidden: true
  //     }
  //   ]
  // },
  {
    path: "/",
    redirect: localStorage.getItem("pathone") || "/index"
  },
  {
    path: "/user",
    component: Layout,
    hidden: true,
    redirect: "noredirect",
    children: [
      {
        path: "profile",
        component: () => import("@/views/system/user/profile/index"),
        name: "个人中心",
        meta: { title: "个人中心", icon: "user" }
      }
    ]
  }
];



export default new Router({
  //mode: 'history', // 去掉url中的#

  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
});
