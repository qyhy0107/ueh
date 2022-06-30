module.exports = {
  title: '竭峙信息事件宝',

  /**
   * 是否系统布局配置
   */
  showSettings: false,
  // themeClass: localStorage.getItem("themeClass") == "dark" || !localStorage.getItem("themeClass") ? "dark" : "light",
  // themeClass:localStorage.getItem("themeClass")=="light"?"light":"dark",
  // themeClass:"dark",
  /**
   * 是否显示 tagsView
   */
  // tagsView: true,
  tagsView: false,

  /**
   * 是否固定头部
   */
  fixedHeader: false,

  /**
   * 是否显示logo
   */
  sidebarLogo: true,

  /**
   * @type {string | array} 'production' | ['production', 'development']
   * @description Need show err logs component.
   * The default is only used in the production env
   * If you want to also use it in dev, you can pass ['production', 'development']
   */
  errorLog: 'production'
}
