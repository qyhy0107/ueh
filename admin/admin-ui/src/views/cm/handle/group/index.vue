<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!--部门数据-->
      <!--<el-col :span="4" :xs="24">
        <div class="sliderBox">
          <div class="head-container">
            <el-input
              v-model="deptName"
              placeholder="请输入部门名称"
              clearable
              size="small"
              prefix-icon="el-icon-search"
              style="margin-bottom: 20px"
            />
          </div>
          <div class="head-container">
            <el-tree
              :data="deptOptions"
              :props="defaultProps"
              :expand-on-click-node="false"
              :filter-node-method="filterNode"
              ref="tree"
              default-expand-all
              @node-click="handleNodeClick"
            />
          </div>
        </div>
            </el-col>-->
      <!--用户数据-->
      <el-col :span="24" :xs="24">
        <div ref="OperateBox" class="operateBox">
          <el-row>
            <el-col :lg="24" :xl="24">
              <el-form ref="queryForm" :model="queryParams" :inline="true" label-position="left" @submit.native.prevent>
                <el-form-item prop="condition">
                  <el-input v-model.trim="queryParams.condition" placeholder="请输入查询条件" clearable size="small"
                    @keyup.enter.native="handleQuery" />
                </el-form-item>
                <el-form-item label="启用" prop="isEnable">
                  <el-select v-model="queryParams.isEnable" placeholder="请选择" clearable size="small"
                    @change="handleQuery">
                    <el-option v-for="dict in typeOptions" :key="dict.dictValue" :label="dict.dictLabel"
                      :value="dict.dictValue" />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button class="blueButton" icon="el-icon-search" size="mini" @click="handleQuery">查询</el-button>
                  <el-button class="grayButton" icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
                </el-form-item>
                <el-form-item>
                  <el-button class="grayButton" icon="el-icon-plus" size="mini" @click="handleAdd">新增</el-button>
                </el-form-item>
              </el-form>
            </el-col>
          </el-row>
        </div>
        <div class="showTableBox" :style="{ height: OperateBoxHeight + 'px' }">
          <el-table v-loading="loading" empty-text=" " element-loading-text="拼命加载中..." :data="groupList" @selection-change="handleSelectionChange"
            :style="{ height: OperateBoxHeight-60+ 'px' }">
            <template v-if="!loading" slot="empty" style="height:100%">
              <Deficiency width="30%" height="auto" />
            </template>
            <el-table-column v-if="show" align="left" width="140" label="id" prop="id" :show-overflow-tooltip="true" />
            <el-table-column label="被通知组名称" prop="groupName" header-align="left" align="left" width="140" />
            <el-table-column label="关联设备类别" align="left" prop="categoryNames" header-align="left"
              :show-overflow-tooltip="true" />
            <el-table-column label="修改时间" align="left" header-align="left" prop="modifyTime" width="155" />
            <el-table-column label="启用" prop="isEnabled" header-align="left" align="left" width="90">
              <template slot-scope="scope">
                <!-- <el-tag v-if="scope.row.isEnabled == 'N'" size="small" type="danger">不启用</el-tag>
                <el-tag v-else size="small">启用</el-tag> -->
                <span v-if="scope.row.isEnable == 'N'" class="brownColor">不启用</span>
                <span v-else>启用</span>
              </template>
            </el-table-column>
            <el-table-column label="备注" align="left" header-align="left" prop="remark" width="140" />
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
              <template slot-scope="scope">
                <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改</el-button>
                <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum"
            :limit.sync="queryParams.pageSize" :page-sizes="[20, 50, 200]" @pagination="getList" />
        </div>
      </el-col>
    </el-row>

    <!-- 添加或修改参数配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="40%">
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="被通知组名称" prop="groupName">
          <el-input v-model.trim="form.groupName" placeholder="请输入被通知组名称" />
        </el-form-item>
        <el-form-item label="关联设备类别" prop="categoryId">
          <!-- <el-select v-model="form.categoryIds" placeholder="请选择关联设备分类" filterable multiple clearable>
            <el-option v-for="category in categories" :key="category.categoryId" :label="category.name" :value="category.categoryId" />
          </el-select> -->
          <el-select style="width:100%" ref="selectUpResIdform" v-model="form.categoryIds" multiple clearable
            size="small" placeholder="请选择关联设备分类" @clear="clearSelect">
            <el-option v-for="category in categories" :key="category.categoryId" hidden :label="category.name"
              :value="category.categoryId" />
            <!-- <el-option hidden :value="form.classId" :label="classIdName1" /> -->
            <el-input v-model.trim="filterText1" placeholder="输入关键字进行过滤" size="mini"
              style="margin:5px;margin-bottom:0;width:95%" />
            <el-tree ref="treeForm" node-key="id" show-checkbox :filter-node-method="filterNode"
              :data="deviceCategories1" default-expand-all :check-strictly="true"
              :default-checked-keys="form.categoryIds" @check="findGroupByClassId">
              <div slot-scope="{ node, data }" class="custom-tree-node" style="width:100%">
                <span v-if="data.disabled"
                  style="color:rgb(132 130 130);cursor:no-drop;display:inline-block;width:100%">{{ node.label }}</span>
                <span v-else>{{ node.label }}</span>
              </div>
            </el-tree>
          </el-select>
        </el-form-item>
        <el-form-item label="启用" prop="isEnabled">
          <el-radio-group v-model="form.isEnabled">
            <el-radio v-for="dict in typeOptions" :key="dict.dictValue" :label="dict.dictValue">
              {{ dict.dictLabel }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model.trim="form.remark" placeholder="请输入备注" maxlength="50" />
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
import { getPhysicalCategories } from '@/api/cm/physicalCategory'
// 省缺组件
import Deficiency from '@/components/Deficiency'
import moment from 'moment'
import { addGroup, addGroupCategory, deleteGroup, deleteGroupCategory, findCategorySelect, findGroupIdByName, getCategoryIds, getGroupById, listGroups, updateGroup, updateGroupCategory } from '@/api/cm/handle/group/group'

export default {
  components: { Treeselect, Deficiency },
  data() {
    return {
      filterText1: '',
      classIdName1: '',
      deviceCategories1: [],
      // 物理分类下拉框
      categories: undefined,
      // 是否启用
      typeOptions: [
        {
          dictValue: 'Y',
          dictLabel: '启用'
        },
        {
          dictValue: 'N',
          dictLabel: '不启用'
        }
      ],
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
      // 处理组格数据
      groupList: [],
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
      form: {
        isEnabled: 'Y'
      },
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        projectId: localStorage.getItem('project_id')
      },
      // 表单校验
      rules: {
        groupName: [{ required: true, message: '被通知组名称不能为空', trigger: 'blur' }]
      }
    }
  },
  watch: {
    filterText1(val) {
      this.$refs.treeForm.filter(val)
    }
  },
  created() {
    this.getList()
    this.getTreeselect()
    this.handleFindCategorySelect()
    this.getDicts('sys_normal_disable').then((response) => {
      this.statusOptions = response.data
    })
    this.getDicts('sys_user_sex').then((response) => {
      this.sexOptions = response.data
      this.form.sex = this.sexOptions[0].dictValue
    })
  },
  // 动态获取高度
  mounted() {
    this.getOperateBoxHeight()
  },
  updated() {
    this.getOperateBoxHeight()
  },
  methods: {
    clearSelect() {
      this.$set(this.form, 'categoryIds', [])
      this.$refs.treeForm.setCheckedKeys([])
    },
    findGroupByClassId(data, one) {
      this.$set(this.form, 'categoryIds', one.checkedKeys)
    },
    filterNode(value, data) {
      if (!value) return true
      const stringTemp = data.label.toLowerCase()
      const stringTemp1 = value.toLowerCase()
      return stringTemp.indexOf(stringTemp1) !== -1
    },
    // 获取页面高度
    getOperateBoxHeight() {
      this.$nextTick(function () {
        this.OperateBoxHeight = document.getElementsByClassName('app-container')[0].offsetHeight - document.getElementsByClassName('operateBox')[0].offsetHeight - 10
      })
    },
    /** 查询用户列表 */
    getList() {
      this.loading = true
      listGroups(this.queryParams).then((response) => {
        const { success, totalCount, datas } = response
        if (success) {
          this.groupList = datas
          this.total = totalCount
          this.loading = false
        }
      })
    },
    /** 查询部门下拉树结构 */
    getTreeselect() {
      treeselect().then((response) => {
        this.deptOptions = response.data
      })
    },
    // 筛选节点
    // filterNode(value, data) {
    //   if (!value) return true
    //   const stringTemp = data.label.toLowerCase()
    //   const stringTemp1 = value.toLowerCase()
    //   return stringTemp.indexOf(stringTemp1) !== -1
    // },
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
      this.$confirm('确认要"' + text + '""' + row.userName + '"用户吗?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(function () {
          return changeUserStatus(row.userId, row.status)
        })
        .then(() => {
          this.msgSuccess(text + '成功')
        })
        .catch(function () {
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
        id: undefined,
        groupName: undefined,
        categoryIds: undefined,
        isEnabled: 'Y',
        modifyTime: undefined,
        remark: undefined
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
      this.reset()
      this.handleFindCategorySelect()
      this.open = true
      this.title = '新增'
    },
    handleFindCategorySelect() {
      findCategorySelect({ projectId: localStorage.getItem('project_id') }).then((response) => {
        const { success, totalCount, datas } = response
        if (success && totalCount > 0) {
          this.categories = datas
          // this.deviceCategories1 = JSON.parse(JSON.stringify(this.handleTree(response.datas, 'id')).replace(new RegExp('labelText', 'g'), 'label'))
          // this.deviceCategories1.forEach((item, index) => {
          //   if (item.children) {
          //     this.deviceCategories1[index].disabled = true
          //   }
          // })
        }
      })
      getPhysicalCategories({
        projectId: this.queryParams.projectId,
        engine: 'Freemarker',
        isEnable: 'Y'
      }).then((response) => {
        if (response.success) {
          // this.deviceCategories = response.datas
          this.deviceCategories1 = JSON.parse(JSON.stringify(this.handleTree(response.datas, 'id')).replace(new RegExp('labelText', 'g'), 'label'))
          this.deviceCategories1.forEach((item, index) => {
            if (item.children) {
              this.deviceCategories1[index].disabled = true
            }
          })
        }
      })
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      this.handleFindCategorySelect()
      const id = row.id
      const projectId = localStorage.getItem('project_id')
      getGroupById({ id, projectId }).then((response) => {
        const { mapData, success } = response
        if (success) {
          this.form = JSON.parse(JSON.stringify(mapData))
          getCategoryIds({ id, projectId }).then((response) => {
            const { listData } = response
            this.$set(this.form, 'categoryIds', listData)
            this.open = true
            this.title = '修改'
          })
        }
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
        .catch(() => { })
    },
    /** 提交按钮 */
    submitForm: function () {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          const projectId = localStorage.getItem('project_id')
          this.form.projectId = projectId
          if (this.form.id != undefined) {
            updateGroup(this.form).then((response) => {
              if (response.success) {
                deleteGroupCategory(this.form).then((response) => {
                  if (response.success) {
                    this.form.groupId = this.form.id
                    if (this.form.categoryIds.length > 0) {
                      addGroupCategory(this.form).then((response) => {
                        if (response.success) {
                          this.msgSuccess('修改成功')
                          this.open = false
                          this.getList()
                        } else {
                          this.msgError('修改失败')
                        }
                      })
                    } else {
                      this.msgSuccess('修改成功')
                      this.open = false
                      this.getList()
                    }
                  }
                })
              }
            })
          } else {
            addGroup(this.form).then((response) => {
              if (response.success) {
                const { categoryIds, groupName } = this.form
                if (categoryIds && categoryIds.length > 0) {
                  findGroupIdByName({ projectId, groupName }).then((response) => {
                    if (response.success) {
                      addGroupCategory({ projectId, groupId: response.strData, categoryIds }).then((response) => {
                        if (response.success) {
                          this.msgSuccess('新增成功')
                        }
                      })
                    }
                  })
                }
                this.open = false
                this.getList()
              } else {
                this.msgError('新增失败')
              }
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id
      this.$confirm('是否确认删除该项数据?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(function () {
          return deleteGroup({ id, projectId: localStorage.getItem('project_id') })
        })
        .then(() => {
          return deleteGroupCategory({ id, projectId: localStorage.getItem('project_id') })
        })
        .then(() => {
          this.getList()
          this.msgSuccess('删除成功')
        })
        .catch(function () {
          this.msgError('删除失败')
        })
    }
  }
}
</script>
