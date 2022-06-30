<template>
  <div class="app-container">
    <el-row :gutter="20" style="height: 100%">
      <el-col :span="6" :xs="24" style="height: 100%; padding-right: 0">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>个人信息</span>
          </div>
          <div>
            <div class="text-center">
              <!-- <userAvatar :user="user"/> -->
              <img src="../../../../assets/common/touxiang.svg" alt="" height="80px" />
            </div>
            <ul class="list-group list-group-striped">
              <li class="list-group-item">
                <div class="pull-left"><svg-icon icon-class="person_user" />用户名称</div>
                <div class="pull-left1">{{ user.userName }}</div>
              </li>
              <li class="list-group-item">
                <div class="pull-left"><svg-icon icon-class="person_iphone" />手机号码</div>
                <div class="pull-left1">{{ user.phonenumber }}</div>
              </li>
              <li class="list-group-item">
                <div class="pull-left"><svg-icon icon-class="person_email" />用户邮箱</div>
                <div class="pull-left1">{{ user.email }}</div>
              </li>
              <!-- <li class="list-group-item">
                <svg-icon icon-class="tree" />所属部门
                <div class="pull-right" v-if="user.dept">{{ user.dept.deptName }} / {{ postGroup }}</div>
              </li> -->
              <li class="list-group-item">
                <div class="pull-left"><svg-icon icon-class="person_juese" />所属角色</div>
                <div class="pull-left1">{{ roleGroup }}</div>
              </li>
              <li class="list-group-item">
                <div class="pull-left"><svg-icon icon-class="person_date" />创建日期</div>
                <div class="pull-left1">{{ user.createTime }}</div>
              </li>
            </ul>
          </div>
        </el-card>
      </el-col>
      <el-col
        :span="18"
        :xs="24"
        class="leftBox"
        style="
          height: 100%;
          padding-left: 0;
        "
      >
        <el-card>
          <!-- <div slot="header" class="clearfix">
            <span>基本资料</span>
          </div> -->
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本资料" name="userinfo">
              <userInfo :user="user" />
            </el-tab-pane>
            <el-tab-pane label="修改密码" name="resetPwd">
              <resetPwd :user="user" />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import userAvatar from './userAvatar'
import userInfo from './userInfo'
import resetPwd from './resetPwd'
import { getUserProfile } from '@/api/system/user'

export default {
  name: 'Profile',
  components: { userAvatar, userInfo, resetPwd },
  data() {
    return {
      user: {},
      roleGroup: {},
      postGroup: {},
      activeTab: 'userinfo'
    }
  },
  created() {
    this.getUser()
  },
  methods: {
    getUser() {
      getUserProfile().then((response) => {
        this.user = response.data
        this.roleGroup = response.roleGroup
        this.postGroup = response.postGroup
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
.avatar-uploader-icon {
  font-size: 28px;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}
.el-card {
  background-color: rgba(6, 72, 100, 0.6);
  .el-tabs__item {
    color: #ffffff;
  }

  .el-tabs__item.is-active {
    color: #1890ff;
  }
  .el-tabs {
    height: 100%;
    color: #fff;
    border-radius: 2px;
    background: transparent;
    border-color: transparent;
  }
  .el-tabs__nav-wrap::after {
    height: 1px;
    background-color: #1890ff;
  }
  .list-group-item {
    border-bottom: 1px solid transparent;
    border-top: 1px solid transparent;
    margin-bottom: -1px;
    padding: 11px 0px;
    font-size: 13px;
  }
}
.light {
  .leftBox {
    // height: 100%;
    // padding-left: 0;
    border-left: 1px solid rgba(216, 216, 216, 1);
  }
  .pull-left {
    float: left;
  }
  .pull-left1 {
    float: left;
    padding-top: 2px;
    padding-left: 10px;
  }
  .el-card {
    background-color: #fff;
    box-shadow: none;
    height: 100%;
    .el-card__header {
      padding: 14px 15px 7px;
      min-height: 40px;
      background-color: transparent;
      color: #333;
      border-bottom: 1px solid #e6e6e6;
      border: none !important;
    }
    .el-tabs__item {
      color: #333;
    }
    .el-tabs__nav-wrap {
      border-bottom: 1px solid rgba(246, 246, 250, 1);
    }
    .el-tabs__item.is-active {
      color: rgba(24, 144, 255, 1);
    }
    .el-tabs {
      height: 100%;
      color: rgba(51, 51, 51, 1);
      border-radius: 2px;
      background: transparent;
      border-color: transparent;
    }
    .el-tabs__nav-wrap::after {
      height: 1px;
      background-color: #fff;
    }
    .list-group-item {
      border-bottom: 1px solid transparent;
      border-top: 1px solid transparent;
      color: rgba(51, 51, 51, 1);
      margin-bottom: -1px;
      padding: 20px 30px;
      font-size: 13px;
      .svg-icon {
        padding-right: 4px;
        font-size: 16px;
      }
    }
  }
}
.dark {
  .leftBox {
    // height: 100%;
    // padding-left: 0;
    border-left: 1px solid rgba(66, 73, 88, 1);
  }
  .pull-left {
    float: left;
  }
  .pull-left1 {
    float: left;
    padding-top: 2px;
    padding-left: 10px;
  }
  .el-card {
    background-color: #2a2f39;
    box-shadow: none;
    height: 100%;
    .el-card__header {
      padding: 14px 15px 7px;
      min-height: 40px;
      background-color: transparent;
      color: rgba(215, 215, 215, 1);
      border-bottom: 1px solid rgba(66, 73, 88, 1);
      border: none !important;
    }
    .el-tabs__item {
      color: rgba(215, 215, 215, 1);
    }
    .el-tabs__nav-wrap {
      border-bottom: 1px solid rgba(66, 73, 88, 1);
    }
    .el-tabs__item.is-active {
      color: rgba(24, 144, 255, 1);
    }
    .el-tabs {
      height: 100%;
      color: rgba(215, 215, 215, 1);
      border-radius: 2px;
      background: transparent;
      border-color: transparent;
    }
    .el-tabs__nav-wrap::after {
      height: 1px;
      background-color: rgba(66, 73, 88, 1);
    }
    .list-group-item {
      border-bottom: 1px solid transparent;
      border-top: 1px solid transparent;
      color: rgba(215, 215, 215, 1);
      margin-bottom: -1px;
      padding: 20px 30px;
      font-size: 13px;
      .svg-icon {
        padding-right: 4px;
        font-size: 16px;
      }
    }
  }
}
</style>
