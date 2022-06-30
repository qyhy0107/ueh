<template>
  <div class="login">
    <div class="left">
      <div class="main-img">
        <!-- <img  src="../assets/login/image-bottom3.png"> -->
      </div>
    </div>
    <div class="right">
      <!--       <img src="../assets/login/logo.png" width="117px" class="logo">-->
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
        <h3 class="title">{{projectName}}</h3>
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" type="text" auto-complete="off" placeholder="账号" class="loginInput">
            <!-- <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" /> -->
            <img slot="prefix" src="../assets/login/enter_user.png" alt="" height="15px" style="margin:12px 4px">
            <!-- <i class="el-icon-user" slot="prefix" style="color:#1890FF" ></i> -->
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" auto-complete="off" placeholder="密码" class="loginInput" @keyup.enter.native="handleLogin(1)">
            <!-- <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon"/> -->
            <img slot="prefix" src="../assets/login/enter_password.png" alt="" height="15px" style="margin:12px 4px">
            <!-- <i class="el-icon-lock" slot="prefix"  style="color:#1890FF"></i> -->
          </el-input>
        </el-form-item>
        <el-form-item style="width:100%;">
          <el-row :gutter="20">
            <el-col :span="12">
              <span>&nbsp;</span>
            </el-col>
            <el-col :span="12">
              <el-button :loading="loading1" size="medium" type="primary" style="width:100%;" @click.native.prevent="handleLogin(0)">
                <span v-if="!loading1">登录</span>
                <span v-else>登 录 中...</span>
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
    </div>
    <div class="edition">
      <img src="../../src/assets/image/company.png" alt />
      <span>{{projectName}}V0.0.1</span>
    </div>
  </div>
</template>

<script>
import { getRouters } from '@/api/menu'
import {title} from "@/settings"
export default {
  name: 'Login',
  data() {
    return {
      cookiePassword: '',
      projectName: title,
      loginForm: {
        username: '',
        password: '',
        rememberMe: false,
        isDomain: 0
      },
      loginRules: {
        username: [{ required: true, trigger: 'blur', message: '用户名不能为空' }],
        password: [{ required: true, trigger: 'blur', message: '密码不能为空' }]
      },
      loading: false,
      loading1: false,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    // getDictTypes("sys_project_name").then(response => {
    //   this.projectName = response.data[0].dictValue;
    // });
  },
  methods: {
    handleLogin(isDomain) {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.loginForm.isDomain = isDomain
          isDomain ? (this.loading = true) : (this.loading1 = true)
          this.$store
            .dispatch('Login', this.loginForm)
            .then((response) => {
              const { code, msg } = response
              if (code !== 200) {
                this.loading = false
                this.loading1 = false
                this.msgError(msg)
              } else {
                getRouters().then((res) => {
                  localStorage.setItem('mune', JSON.stringify(res.data))
                  if (res.data[0].children) {
                    const temp = []
                    res.data[0].children.forEach((element) => {
                      if (!element.hidden) {
                        temp.push(element)
                      }
                    })
                    // console.log( res.data[0].path + '/' + temp[0].path)
                    const path = (res.data[0].path + '/' + temp[0].path).replace('//', '/')
                    localStorage.setItem('pathone', path)
                    this.$router.push({ path: path })
                  } else {
                    localStorage.setItem('pathone', res.data[0].path)
                    this.$router.push({ path: res.data[0].path })
                  }
                })
              }
            })
            .catch(() => {
              this.loading = false
              this.loading1 = false
              this.getCode()
            })
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  // background-image: url("../assets/image/login-background.jpg");
  background: url('../assets/login/bg4.png') no-repeat;
  background-size: auto 130%;
  background-position: center center;
  // background-size: cover;
  .edition {
    position: fixed;
    bottom: 10px;
    right: 10px;
    height: 40px;
    span {
      font-size: 12px;
      font-weight: 700;
      color: #c7c8ca;
      vertical-align: text-top;
      margin-left: 10px;
    }
    img {
      height: 40px;
    }
  }
  .left {
    display: inline-block;
    width: 60%;
    height: 100%;
    position: relative;

    .main-img {
      position: absolute;
      top: 50%;
      right: 10%;
      transform: translateY(-50%);
      // width: 800px;
      width: 70%;

      img {
        width: 95%;
      }
      span {
        display: inline-block;
        width: 95%;
        height: 100%;
        line-height: 120%;
        text-align: center;
        color: #fff;
        // font-weight: bold;
        font-size: 32px;
      }
    }
  }
  .right {
    display: inline-block;
    width: 40%;
    position: relative;
    .logo {
      position: absolute;
      top: -10px;
      left: 30px;
    }
    .el-form-item {
      margin-bottom: 25px;
    }
  }
}
.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #333;
}

.login-form {
  // border-radius: 6px;
  background: #ffffff;
  width: 100%;
  height: 100vh;
  padding: 30vh 10vw;
  .el-input {
    height: 38px;
    input {
      height: 38px;
    }
  }
  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 2px;
  }
}
.login-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}
.login-code {
  width: 33%;
  height: 38px;
  float: right;
  img {
    cursor: pointer;
    vertical-align: middle;
  }
}
.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}
.loginInput {
  .el-input__inner {
    -webkit-appearance: none;
    background-color: #f6f6fa !important;
    background-image: none;
    border-radius: 4px;
    border: 1px solid #dcdfe6;
    box-sizing: border-box;
    color: #606266 !important;
    display: inline-block;
    font-size: inherit;
    height: 40px;
    line-height: 40px;
    outline: none;
    padding: 0 30px;
    transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
    width: 100%;
  }
}
@media screen and (max-width: 1400px) {
  .main-img {
    position: absolute;
    top: 50%;
    right: 10%;
    transform: translateY(-50%);
    // width: 800px;
    width: 70%;

    img {
      width: 95%;
    }
    span {
      display: inline-block;
      width: 95%;
      height: 100%;
      line-height: 120%;
      text-align: center;
      color: #fff;
      // font-weight: bold;
      font-size: 24px !important;
    }
  }
}
</style>
