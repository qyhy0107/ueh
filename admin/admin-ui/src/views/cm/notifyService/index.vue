<template>
  <div class="app-container">
    <div class="showTableBox">
      <el-tabs type="border-card">
        <!-- 邮箱 -->
        <!-- 邮箱表单 -->
        <el-tab-pane label="邮件">
          <el-form ref="mail" :model="mailForm" :rules="mailRules" label-width="100px" class="demo-ruleForm" label-position="top">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="邮件服务器" prop="media_settings.mail_server">
                  <el-input v-model.trim="mailForm.media_settings.mail_server" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="发件Email" prop="media_settings.from_mail">
                  <el-input v-model.trim="mailForm.media_settings.from_mail" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="密码" prop="media_settings.from_mail_password">
                  <el-input v-model.trim="mailForm.media_settings.from_mail_password" type="password" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="邮件标题" prop="media_settings.mailSubject">
                  <el-input v-model.trim="mailForm.media_settings.mailSubject" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="邮件模板" prop="media_templet">
              <el-input v-model.trim="mailForm.media_templet" type="textarea" :rows="10" placeholder="请填入邮件模板" />
            </el-form-item>
            <el-form-item>
              <el-button size="mini" @click="resetForm('mail')">刷新</el-button>
              <el-button type="primary" size="mini" @click="submitForm('mail')">保存</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <!-- <el-tab-pane label="消息中心">
          <el-form ref="msCenter" :model="msCenterForm" :rules="msCenterRules" label-width="100px" class="demo-ruleForm" label-position="top">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="消息中心地址" prop="media_settings.url">
                  <el-input v-model.trim="msCenterForm.media_settings.url" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="Token名称" prop="media_settings.tokenName">
                  <el-input v-model.trim="msCenterForm.media_settings.tokenName" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="Token值" prop="media_settings.tokenValue">
                  <el-input v-model.trim="msCenterForm.media_settings.tokenValue" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="融讯通接口地址" prop="media_settings.appUrn">
                  <el-input v-model.trim="msCenterForm.media_settings.appUrn" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="邮件接口地址" prop="media_settings.mailUrn">
                  <el-input v-model.trim="msCenterForm.media_settings.mailUrn" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="邮件标题" prop="media_settings.mailSubject">
                  <el-input v-model.trim="msCenterForm.media_settings.mailSubject" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="消息模板" prop="media_templet">
              <el-input v-model.trim="msCenterForm.media_templet" type="textarea" :rows="6" placeholder="请填入消息模板" />
            </el-form-item>
            <el-form-item>
              <el-button size="mini" @click="resetForm('msCenter')">刷新</el-button>
              <el-button type="primary" size="mini" @click="submitForm('msCenter')">保存</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane> -->
        <!-- 微信 -->
        <!-- <el-tab-pane label="工单">
          <el-form ref="flow" :model="flowForm" :rules="flowRules" label-width="100px" class="demo-ruleForm" label-position="top">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="工单接口地址" prop="media_settings.url">
                  <el-input v-model.trim="flowForm.media_settings.url" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="用户名" prop="media_settings.userName">
                  <el-input v-model.trim="flowForm.media_settings.userName" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="密码" prop="media_settings.password">
                  <el-input v-model.trim="flowForm.media_settings.password" type="password" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="消息模板" prop="media_templet">
              <el-input v-model.trim="flowForm.media_templet" type="textarea" :rows="15" placeholder="请填入消息模板" />
            </el-form-item>
            <el-form-item>
              <el-button size="mini" @click="resetForm('flow')">刷新</el-button>
              <el-button type="primary" size="mini" @click="submitForm('flow')">保存</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane> -->
      </el-tabs>
    </div>
  </div>
</template>

<script>
import {
  updateNotifyService,
  getNotifyServices,
  addNotifyService
} from '@/api/cm/notifyService'
export default {
  data() {
    return {
      projectId: 10,
      // 消息中心绑定form
      msCenterForm: {
        id: '',
        media_type: '',
        media_settings: {
          url: '',
          tokenName: '',
          tokenValue: '',
          appUrn: '',
          mailUrn: '',
          mailSubject: ''
        },
        media_templet: ''
      },
      mailForm: {
        id: '',
        media_type: '',
        media_settings: {
          mail_server: '',
          from_mail: '',
          from_mail_password: '',
          mailSubject: ''
        },
        media_templet: ''
      },
      wechatForm: {
        id: '',
        media_type: '',
        media_settings: {
          corpId: '',
          agentId: '',
          secret: ''
        },
        media_templet: ''
      },
      flowForm: {
        id: '',
        media_type: '',
        media_settings: {
          userName: '',
          password: '',
          url: ''
        },
        media_templet: ''
      },
      // 消息中心form校验规则
      msCenterRules: {
        'media_settings.url': [
          { required: true, message: '请输入消息中心服务器地址', trigger: 'blur' }
        ],
        'media_settings.tokenName': [
          { required: true, message: '请输入token名称', trigger: 'blur' }
        ],
        'media_settings.tokenValue': [
          { required: true, message: '请输入token值', trigger: 'blur' }
        ],
        'media_settings.appUrn': [
          { required: true, message: '请输入融讯通接口地址', trigger: 'blur' }
        ],
        'media_settings.mailUrn': [
          { required: true, message: '请输入邮件接口地址', trigger: 'blur' }
        ],
        'media_settings.mailSubject': [
          { required: true, message: '请输入邮件标题', trigger: 'blur' }
        ],
        'media_templet': [
          { required: true, message: '请输入消息中心正文模板', trigger: 'blur' }
        ]
      },
      mailRules: {
        'media_settings.mail_server': [
          { required: true, message: '请输入邮箱服务器地址', trigger: 'blur' }
        ],
        'media_settings.from_mail': [
          { required: true, message: '请输入邮件账号', trigger: 'blur' }
        ],
        'media_settings.from_mail_password': [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ],
        'media_settings.mailSubject': [
          { required: true, message: '请输入邮件标题', trigger: 'blur' }
        ],
        media_templet: [
          { required: true, message: '请输入邮件正文模板', trigger: 'blur' }
        ]
      },
      wechatRules: {
        mailServer: [
          { required: true, message: '请输入邮箱服务器地址', trigger: 'blur' }
        ],
        fromMail: [
          { required: true, message: '请输入邮件账号', trigger: 'blur' }
        ],
        fromMailPassword: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ],
        media_templet: [
          { required: true, message: '请输入邮件正文模板', trigger: 'blur' }
        ]
      },
      flowRules: {
        'media_settings.url': [
          { required: true, message: '请输入工单接口地址', trigger: 'blur' }
        ],
        'media_settings.userName': [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        'media_settings.password': [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ],
        media_templet: [
          { required: true, message: '请输入消息模板', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.projectId = localStorage.getItem('project_id')
    this.getNotifyServiceList()
  },
  updated() {},
  methods: {
    getNotifyServiceList() {
      getNotifyServices({ projectId: 10, isEnable: 'Y' }).then((response) => {
        if (response.success) {
          const datas = response.datas
          for (var i = 0; i < datas.length; i++) {
            if (datas[i].media_type === 'msCenter') {
              this.msCenterForm.id = datas[i].id
              this.msCenterForm.media_type = datas[i].media_type
              this.msCenterForm.media_settings = JSON.parse(datas[i].media_settings)
              this.msCenterForm.project_id = datas[i].project_id
              this.msCenterForm.media_templet = datas[i].media_templet
              this.msCenterForm.is_enable = datas[i].is_enable
            } else if (datas[i].media_type === 'flow') {
              // this.flowForm.media_settings = JSON.parse(datas[i].media_settings);
              this.flowForm.id = datas[i].id
              this.flowForm.media_type = datas[i].media_type
              this.flowForm.media_settings = JSON.parse(datas[i].media_settings)
              this.flowForm.project_id = datas[i].project_id
              this.flowForm.media_templet = datas[i].media_templet
              this.flowForm.is_enable = datas[i].is_enable
            } else if (datas[i].media_type === 'email') {
              this.mailForm.media_type = datas[i].media_type
              this.mailForm.media_settings = JSON.parse(datas[i].media_settings)
              this.mailForm.id = datas[i].id
              this.mailForm.project_id = datas[i].project_id
              this.mailForm.media_templet = datas[i].media_templet
              this.mailForm.is_enable = datas[i].is_enable
            } else if (datas[i].media_type === 'wechat') {
              // this.wechatForm.media_settings = JSON.parse(datas[i].media_settings);
              this.wechatForm.media_type = datas[i].media_type
              this.wechatForm.id = datas[i].id
              this.wechatForm.media_settings = JSON.parse(datas[i].media_settings)
              this.wechatForm.project_id = datas[i].project_id
              this.wechatForm.media_templet = datas[i].media_templet
              this.wechatForm.is_enable = datas[i].is_enable
            }
          }
        }
      })
    },
    // 保存
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (formName === 'msCenter') { // 消息中心
            if (this.msCenterForm.id === undefined || this.msCenterForm.id === '') {
              this.add(this.projectId, this.msCenterForm, formName)
            } else {
              this.update(this.msCenterForm)
            }
          } else if (formName === 'mail') { // 邮件
            if (this.mailForm.id === undefined || this.mailForm.id === '') {
              this.add(this.projectId, this.mailForm, formName)
            } else {
              this.update(this.mailForm)
            }
          } else if (formName === 'wechat') { // 微信
            if (this.wechatForm.id === undefined || this.wechatForm.id === '') {
              this.add(this.projectId, this.wechatForm, formName)
            } else {
              this.update(this.wechatForm)
            }
          } else if (formName === 'flow') { // 工单
            if (this.flowForm.id === undefined || this.flowForm.id === '') {
              this.add(this.projectId, this.flowForm, formName)
            } else {
              this.update(this.flowForm)
            }
          }
        } else {
          return false
        }
      })
    },
    update(form) {
      updateNotifyService({ projectId: form.project_id, media_settings: JSON.stringify(form.media_settings), media_type: form.media_type, media_templet: form.media_templet, is_enable: 'Y', id: form.id }).then((response) => {
        if (response.success) {
          this.msgSuccess('更新成功')
          this.getNotifyServiceList()
        }
      })
    },
    add(projectId, form, mediaType) {
      addNotifyService({ projectId: this.projectId, media_settings: JSON.stringify(form.media_settings), media_type: mediaType, media_templet: form.media_templet, is_enable: 'Y' }).then((response) => {
        if (response.success) {
          // this.open = false;
          this.msgSuccess('新增成功')
          this.getNotifyServiceList()
        }
      })
    },
    // 刷新页面
    resetForm(formName) {
      // this.$refs[formName].resetFields()
      this.getNotifyServiceList()
    }
  }
}
</script>
<style lang="scss" scoped>
@import "src/assets/styles/index";
.operateBox {
  padding: 15px;
}
.showTableBox {
  // margin-top:20px;
  height: 89vh;
  padding: 0px;
  .titleBox {
    padding: 10px;
    font-size: 14px;
    line-height: 36px;
  }
  .el-tabs {
    // height: 100%;
    // zoom:.75;
    /deep/.el-tabs__content {
      height: 85vh;
      overflow: auto;
      // zoom:.75;
      // overflow: auto;
      // height: calc(100% - 50px);
      .redFont {
        color: red;
        font-size: 12px;
        margin-left: 10px;
      }
    }
  }
}
.dark{
.el-textarea /deep/.el-textarea__inner {
    background: #474f5f!important;
    border: #474f5f!important;
    color: white!important;
}
}
</style>
