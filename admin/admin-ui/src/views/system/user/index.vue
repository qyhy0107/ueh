<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!--用户数据-->
      <el-col :span="24" :xs="24">
        <div ref="OperateBox" class="operateBox">
          <el-row>
            <el-col :lg="24" :xl="24">
              <el-form ref="queryForm" :model="queryParams" :inline="true" label-position="left">
                <el-form-item label="用户名" prop="userName">
                  <el-input v-model.trim="queryParams.userName" placeholder="请输入用户名" clearable size="small" @submit.native.prevent @keyup.enter.native="handleQuery" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">查询
                  </el-button>
                  <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置
                  </el-button>
                </el-form-item>
                <el-form-item>
                  <el-button v-hasPermi="['system:user:add']" class="addButton" icon="el-icon-plus" size="mini" @click="handleAdd">新增
                  </el-button>
                  <el-button v-hasPermi="['system:user:remove']" class="redButton" icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete">批量删除
                  </el-button>
                </el-form-item>
              </el-form>
            </el-col>
          </el-row>
        </div>
        <div class="showTableBox" :style="{height:OperateBoxHeight+'px'}">
          <el-table v-if="userList.length" v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
            <el-table-column type="selection" min-width="2%" align="center" :show-overflow-tooltip="true" />
            <!-- <el-table-column v-if="show" align="left"  label="id" prop="userId" :show-overflow-tooltip="true" /> -->
            <el-table-column label="用户名" min-width="12%" :show-overflow-tooltip="true" prop="userName" header-align="left" align="left" />
            <el-table-column label="昵称" min-width="14%" :show-overflow-tooltip="true" prop="nickName" header-align="left" align="left" />
            <el-table-column label="部门" min-width="12%" :show-overflow-tooltip="true" align="left" header-align="left" prop="dept.deptName" />
            <el-table-column label="角色" min-width="12%" :show-overflow-tooltip="true" align="left" header-align="left" prop="role" :formatter="getUserRoles" />
            <el-table-column label="联系电话" min-width="10%" :show-overflow-tooltip="true" prop="phonenumber" />
            <el-table-column align="center" min-width="8%" label="启用" prop="status" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.status === '1'" size="small" type="danger">不启用</el-tag>
                <el-tag v-else size="small">启用</el-tag>
              </template>
            </el-table-column>
            <!-- <el-table-column label="创建时间" align="center" prop="createTime" width="200">
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.createTime) }}</span>
              </template>
            </el-table-column> -->
            <el-table-column min-width="14%" label="备注" align="left" prop="remark" :show-overflow-tooltip="true" />
            <el-table-column min-width="16%" label="操作" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-button v-hasPermi="['system:user:edit']" size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改
                </el-button>
                <el-button v-if="scope.row.userId !== 1" v-hasPermi="['system:user:remove']" size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除
                </el-button>
                <el-button v-hasPermi="['system:user:resetPwd']" size="mini" type="text" icon="el-icon-key" @click="handleResetPwd(scope.row)">密码修改
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-else class="noneDateBox">
            <img src="../../../assets/common/nonedata.png" width="25%">
            <div>&nbsp;&nbsp;暂无数据~</div>
          </div>
          <pagination v-show="total > 0" :page-sizes="[20, 50, 200]" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
        </div>
      </el-col>
    </el-row>
    <!-- 添加或修改参数配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="40%">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model.trim="form.userName" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item v-if="form.userId == undefined" label="密码" prop="password">
          <el-input v-model.trim="form.password" placeholder="请输入密码" type="password" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickName">
          <el-input v-model.trim="form.nickName" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phonenumber">
          <el-input v-model.trim="form.phonenumber" placeholder="请输入联系电话" maxlength="11" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model.trim="form.email" placeholder="请输入邮箱" maxlength="50" />
        </el-form-item>

        <el-form-item label="性别">
          <el-radio-group v-model="form.sex" placeholder="请选择" popper-class="select-dropdown-style">
            <el-radio v-for="dict in sexOptions" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue" />
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="form.roleId" placeholder="请选择角色" popper-class="select-dropdown-style">
            <el-option v-for="item in roleOptions" :key="item.roleId" :label="item.roleName" :value="item.roleId" :disabled="item.status == 1" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门" prop="deptId">
          <treeselect v-model="form.deptId" :options="deptOptions" placeholder="请选择部门" />
        </el-form-item>
        <el-form-item label="启用">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in statusOptions" :key="dict.dictValue" :label="dict.dictValue">{{ dict.dictLabel }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model.trim="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button class="cancel_button" @click="cancel">取 消</el-button>
        <el-button class="confirm_button" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listUser,
  getUser,
  delUser,
  addUser,
  updateUser,
  exportUser,
  resetUserPwd,
  changeUserStatus
  // getListUser,
  // getQueryListUser,
} from '@/api/system/user'
import { treeselect } from '@/api/system/dept'
import { listPost } from '@/api/system/post'
import { listRole } from '@/api/system/role'
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import moment from 'moment'

export default {
  components: { Treeselect },
  data() {
    return {
      OperateBoxHeight: 100,
      // 遮罩层
      loading: true,
      // 显示
      show: false,
      // 选中数组
      ids: [],
      userNames: [],
      nickNames: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 总条数
      total: 0,
      // 用户表格数据
      userList: [],
      // 弹出层标题
      title: '',
      // 部门树选项
      deptOptions: [],
      // 是否显示弹出层
      open: false,
      update: false,
      // 部门名称
      deptName: undefined,
      // 默认密码
      initPassword: undefined,
      // 日期范围
      dateRange: [],
      // 状态数据字典
      statusOptions: [],
      // 性别状态字典
      sexOptions: [],
      // 岗位选项
      postOptions: [],
      // 角色选项
      roleOptions: [],
      // 表单参数
      form: {},
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: '',
        phonenumber: '',
        status: '',
        deptId: undefined
      },
      // 表单校验
      rules: {
        roleId: [{ required: true, message: '角色不能为空', trigger: 'blur' }],
        userName: [
          { required: true, message: '用户名称不能为空', trigger: 'blur' }
        ],
        /* nickName: [
          { required: true, message: "用户昵称不能为空", trigger: "blur" },
        ],*/
        deptId: [
          { required: true, message: '归属部门不能为空', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '用户密码不能为空', trigger: 'blur' }
        ],
        email: [
          {
            type: 'email',
            message: "'请输入正确的邮箱地址",
            trigger: ['blur', 'change']
          }
        ],
        phonenumber: [
          {
            pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
            message: '请输入正确的手机号码',
            trigger: 'blur'
          }
        ]
      }
    }
  },
  watch: {
    // 根据名称筛选部门树
    // deptName(val) {
    //   this.$refs.tree.filter(val);
    // },
  },
  created() {
    this.getList()
    this.getTreeselect()
    this.getDicts('sys_normal_disable').then((response) => {
      this.statusOptions = response.data
    })
    this.getDicts('sys_user_sex').then((response) => {
      this.sexOptions = response.data
      this.form.sex = this.sexOptions[0].dictValue
    })
    /* this.getConfigKey("sys.user.initPassword").then(response => {
        this.initPassword = response.data;
      });*/
  },
  // 动态获取高度
  mounted() {
    this.getOperateBoxHeight()
  },
  updated() {
    this.getOperateBoxHeight()
  },
  methods: {
    // 获取页面高度
    getOperateBoxHeight() {
      this.$nextTick(function() {
        this.OperateBoxHeight =
          document.getElementsByClassName('app-container')[0].offsetHeight -
          document.getElementsByClassName('operateBox')[0].offsetHeight -
          10
      })
    },
    /** 查询用户列表 */
    getList() {
      this.loading = true
      listUser(this.addDateRange(this.queryParams, this.dateRange)).then(
        (response) => {
          // console.log(response.rows)
          this.userList = response.rows
          this.total = response.total
          this.loading = false
        }
      )
      // if(this.dateRange.length===0){
      //   getListUser({start: this.queryParams.start, end: this.queryParams.limit,
      //     userName:this.queryParams.userName,phonenumber:this.queryParams.phonenumber,status:this.queryParams.status}).then(response => {
      //     if (response.datas != null) {
      //       this.userList = response.datas;
      //       this.total = response.totalCount;
      //       this.loading = false;
      //     }else{
      //       this.loading = false;
      //     }
      //   });
      // }else {
      //   getQueryListUser({start: this.queryParams.start, end: this.queryParams.limit,
      //     userName:this.queryParams.userName,phonenumber:this.queryParams.phonenumber,status:this.queryParams.status,
      //     startTime:moment(this.dateRange[0]).format("YYYY-MM-DD 00:00:00"),endTime:moment(this.dateRange[1]).format("YYYY-MM-DD 23:59:59")}).then(response => {
      //     if (response.datas != null) {
      //       this.userList = response.datas;
      //       this.total = response.totalCount;
      //       this.loading = false;
      //     }else{
      //       this.loading = false;
      //     }
      //   });
      // }
    },
    /** 查询部门下拉树结构 */
    getTreeselect() {
      treeselect().then((response) => {
        this.deptOptions = response.data
      })
    },
    // 筛选节点
    filterNode(value, data) {
      if (!value) return true
      const stringTemp = data.label.toLowerCase()
      const stringTemp1 = value.toLowerCase()
      return stringTemp.indexOf(stringTemp1) !== -1
    },
    // 节点单击事件
    handleNodeClick(data) {
      this.queryParams.deptId = data.id
      this.getList()
    },
    /** 查询岗位列表 */
    getPosts() {
      listPost().then((response) => {
        this.postOptions = response.rows
      })
    },
    /** 查询角色列表 */
    getRoles() {
      listRole().then((response) => {
        this.roleOptions = response.rows
        // this.form.roleIds=this.roleOptions[0].roleId
      })
    },
    // 表格内角色过滤
    getUserRoles(row) {
      let role = ''
      if (row.roles) {
        const roles = row.roles
        for (var i = 0; i < roles.length; i++) {
          if (role != '') {
            role = role + ',' + roles[i].roleName
          } else {
            role = roles[i].roleName
          }
        }
      }
      return role
    },
    // 用户状态修改
    handleStatusChange(row) {
      const text = row.status === '0' ? '启用' : '停用'
      this.$confirm(
        '确认要"' + text + '""' + row.userName + '"用户吗?',
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
        .then(function() {
          return changeUserStatus(row.userId, row.status)
        })
        .then(() => {
          this.msgSuccess(text + '成功')
        })
        .catch(function() {
          row.status = row.status === '0' ? '1' : '0'
        })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        userId: undefined,
        deptId: 100,
        userName: undefined,
        nickName: undefined,
        password: undefined,
        phonenumber: undefined,
        email: undefined,
        sex: [],
        status: '0',
        remark: undefined,
        postIds: [],
        roleId: undefined
      }
      this.resetForm('form')
    },
    /** 查询按钮操作 */
    handleQuery() {
      this.queryParams.page = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = []
      this.resetForm('queryForm')
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.userId)
      this.userNames = selection.map((item) => item.userName)
      this.single = selection.length != 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.update = false
      this.reset()
      // this.getTreeselect();
      // this.getPosts();
      this.getRoles()
      this.open = true
      this.title = '新增'
      this.form.sex = this.sexOptions[0].dictValue
      // this.form.password = this.initPassword;
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      // debugger
      this.update = false
      this.reset()
      // this.getTreeselect();
      // this.getPosts();
      this.getRoles()
      const userId = row.userId || this.ids
      getUser(userId).then((response) => {
        // debugger
        this.form = response.data
        this.form.postIds = response.postIds
        // this.form.roleIds = response.roleIds[0];
        this.open = true
        this.title = '修改'
        this.form.password = ''
      })
    },
    /** 重置密码按钮操作 */
    handleResetPwd(row) {
      this.$prompt('请输入"' + row.userName + '"的新密码', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      })
        .then(({ value }) => {
          resetUserPwd(row.userId, value).then((response) => {
            if (response.code === 200) {
              this.msgSuccess('修改成功，新密码是：' + value)
            } else {
              this.msgError(response.msg)
            }
          })
        })
        .catch(() => {})
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          if (this.form.userId != undefined) {
            // var roleIds = this.form.roleIds;
            // this.form.roleIds = [];
            // if (roleIds != "") {
            //   this.form.roleIds.push(roleIds);
            // }
            updateUser(this.form).then((response) => {
              if (response.code === 200) {
                this.msgSuccess('修改成功')
                this.open = false
                this.getList()
              } else {
                this.msgError(response.msg)
              }
            })
          } else {
            // var roleIds = this.form.roleIds;
            // this.form.roleIds = [];
            // if (roleIds != "") {
            //   this.form.roleIds.push(roleIds);
            // }

            addUser(this.form).then((response) => {
              if (response.code === 200) {
                this.msgSuccess('新增成功')
                this.open = false
                this.getList()
              } else {
                this.msgError(response.msg)
              }
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const userNickName = row.userName || this.userNames
      const userIds = row.userId || this.ids
      this.$confirm('是否确认删除该项数据?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(function() {
          return delUser(userIds)
        })
        .then(() => {
          this.getList()
          this.msgSuccess('删除成功')
        })
        .catch(function() {})
    }
    /** 导出按钮操作 */
    // handleExport() {
    //   const queryParams = this.queryParams;
    //   this.$confirm("是否确认导出所有用户数据项?", "警告", {
    //     confirmButtonText: "确定",
    //     cancelButtonText: "取消",
    //     type: "warning",
    //   })
    //     .then(function () {
    //       return exportUser(queryParams);
    //     })
    //     .then((response) => {
    //       this.download(response.msg);
    //     })
    //     .catch(function () {});
    // },
  }
}
</script>

