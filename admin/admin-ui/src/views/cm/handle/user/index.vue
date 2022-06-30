<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!--用户数据-->
      <el-col :span="24" :xs="24">
        <div ref="OperateBox" class="operateBox">
          <el-row>
            <el-col :lg="24" :xl="24">
              <el-form ref="queryForm" :model="queryParams" :inline="true" label-position="left" @submit.native.prevent>
                <el-form-item prop="condition">
                  <el-input v-model.trim="queryParams.condition" placeholder="请输入查询条件" clearable size="small" @keyup.enter.native="handleQuery" />
                </el-form-item>
                <el-form-item label="启用" prop="isEnable">
                  <el-select v-model="queryParams.isEnable" placeholder="请选择" clearable size="small" @change="handleQuery">
                    <el-option v-for="dict in typeOptions" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue" />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">查询</el-button>
                  <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
                </el-form-item>
                <el-form-item>
                  <el-button class="addButton" icon="el-icon-plus" size="mini" @click="handleAdd">新增</el-button>
                </el-form-item>
              </el-form>
            </el-col>
          </el-row>
        </div>
        <div class="showTableBox" :style="{ height: OperateBoxHeight + 'px' }">
          <el-table v-if="userList.length" v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
            <el-table-column v-if="show" align="left" width="140" label="uid" prop="uid" :show-overflow-tooltip="true" />
            <el-table-column label="被通知账号" prop="domainName" header-align="left" align="left" width="160" />
            <el-table-column label="被通知人" prop="userName" header-align="left" align="left" width="120" />
            <el-table-column label="被通知组" align="left" header-align="left" prop="groupNames" width="320" />
            <el-table-column label="关联设备" align="left" header-align="left" prop="resourceNames" />
            <el-table-column label="启用" align="center" prop="isEnable" width="100">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.isEnable == 'N'" size="small" type="danger">不启用</el-tag>
                <el-tag v-else size="small">启用</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="备注" align="left" header-align="left" prop="remark" width="140" />
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="140">
              <template slot-scope="scope">
                <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改</el-button>
                <el-button v-if="scope.row.userId !== 1" size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-else class="noneDateBox">
            <img src="../../../../assets/common/nonedata.png" width="25%" />
            <div>&nbsp;&nbsp;暂无数据~</div>
          </div>
          <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :page-sizes="[20, 50, 200]" :limit.sync="queryParams.pageSize" @pagination="getList" />
        </div>
      </el-col>
    </el-row>

    <!-- 添加或修改处理人对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="40%">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="被通知账号" prop="domainName">
          <el-input v-model.trim="form.domainName" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="被通知人" prop="userName">
          <el-input v-model.trim="form.userName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="被通知组" prop="groupNames">
          <el-select v-model="form.groupIds" placeholder="请选择通知组" filterable multiple clearable>
            <el-option v-for="group in groupSelect" :key="group.id" :label="group.groupName" :value="group.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联设备" prop="resourceNames">
          <!-- <el-select v-model="form.resourceIds" placeholder="请选择关注设备" filterable multiple clearable>
            <el-option v-for="resource in resourceSelect" :key="resource.id" :label="resource.labelText" :value="resource.id" />
          </el-select> -->
          <!-- <el-cascader
            v-model="id"
            :options="resourceCascader"
            :props="{ expandTrigger: 'hover' }"
            @change="handleChange"
          /> -->
          <el-cascader v-model="form.resourceIds" placeholder="请选择关注设备" :show-all-levels="false" :options="options" style="width:100%" :props="props" filterable>
            <!-- <el-cascader v-model="form.resourceIds" placeholder="请选择关注设备" :show-all-levels="false" :options="options" style="width:100%" :props="{ multiple: true, emitPath: false, lazy: true }" filterable> -->
            <!-- <template slot-scope="{ node, data }">
              <span>{{ data.labelText }}</span>
            </template> -->
          </el-cascader>
        </el-form-item>
        <el-form-item label="启用">
          <el-radio-group v-model="form.isEnable">
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
import { addHandleUser, addHandleUserAndGroup, addHandleUserAndResource, deleteUser, deleteUserGroup, deleteUserResource, findHandleUserByDomainName, findHandleUserById, findMonitoryUserList, findResourceSelect, findResourceparentIdSelect, getUserAndGroup, getUserAndResource, getUserAndResourceClass, updateHandleUser } from '@/api/cm/handle/user/user'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { findGroupSelect } from '@/api/cm/handle/group/group'

export default {
  data() {
    // const that = this
    return {
      props: {
        lazy: true,
        multiple: true,
        emitPath: false,
        expandTrigger: 'hover',
        lazyLoad(node, resolve) {
          const projectId = localStorage.getItem('project_id')
          if (node.level === 0) {
            findResourceparentIdSelect({ projectId }).then((response) => {
              const { success, datas } = response
              if (success) {
                // that.options = datas
                resolve(datas)
              }
            })
          } else {
            findResourceSelect({ projectId, id: node.value }).then((response) => {
              const { success, datas } = response
              if (success) {
                // that.$set(node, 'children', datas)
                // node.children = datas
                const datasArr = datas.map((e) => ({ value: e.value, label: e.label, leaf: true }))
                resolve(datasArr)
              }
            })
          }
        }
      },
      options: [],
      groupSelect: [],
      resourceSelect: [],
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
      // 表单参数
      form: {
        isEnable: 'Y'
      },
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        condition: '',
        projectId: localStorage.getItem('project_id')
      },
      // 表单校验
      rules: {
        domainName: [{ required: true, message: '账号不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
    this.handleFindGroupSelect()
    // this.handleFindResourceSelect()
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
        this.OperateBoxHeight = document.getElementsByClassName('app-container')[0].offsetHeight - document.getElementsByClassName('operateBox')[0].offsetHeight - 10
      })
    },
    /** 查询用户列表 */
    getList() {
      this.loading = true
      findMonitoryUserList(this.queryParams).then((response) => {
        if (response.success) {
          this.userList = response.datas
          this.total = response.totalCount
          this.loading = false
        }
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
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        uid: undefined,
        domainName: undefined,
        userName: undefined,
        groupIds: undefined,
        resourceIds: undefined,
        isEnable: 'Y'
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
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.handleFindGroupSelect()
      // this.handleFindResourceSelect()
      this.open = true
      this.title = '新增'
    },
    // 通知组下拉框
    handleFindGroupSelect() {
      const projectId = localStorage.getItem('project_id')
      findGroupSelect({ projectId }).then((response) => {
        const { success, datas } = response
        if (success) {
          this.groupSelect = datas
        }
      })
    },
    handleFindResourceSelect() {
      const projectId = localStorage.getItem('project_id')
      findResourceparentIdSelect({ projectId }).then((response) => {
        const { success, datas } = response
        if (success) {
          this.options = datas
        }
      })
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.update = false
      this.reset()
      this.handleFindGroupSelect()
      this.handleFindResourceSelect()
      const userId = row.uid
      const projectId = localStorage.getItem('project_id')
      findHandleUserById({ userId, projectId }).then((response) => {
        const { mapData, success } = response
        if (success) {
          this.form = JSON.parse(JSON.stringify(mapData))
          getUserAndGroup({ userId, projectId }).then((response) => {
            // console.log(response)
            const { listData, success } = response
            if (success) {
              this.$set(this.form, 'groupIds', listData)
            }
          })
          getUserAndResource({ userId, projectId }).then((response) => {
            const { listData, success } = response
            if (success) {
              this.$set(this.form, 'resourceIds', listData)
            }
          })
          getUserAndResourceClass({ userId, projectId }).then((response) => {
            const { listData, success } = response
            if (success && listData.length) {
              listData.forEach((item) => {
                findResourceSelect({ projectId, id: item }).then((response) => {
                  const { success, datas } = response
                  if (success) {
                    // console.log(datas)
                    this.options.forEach((option, index) => {
                      if (option.value === item) {
                        // this.options[index].children = datas
                        const datasArr = datas.map((e) => ({ value: e.value, label: e.label, leaf: true }))
                        this.$set(this.options[index], 'children', datasArr)
                        // this.options = []
                        return
                      }
                    })
                  }
                })
              })
            }
          })
          this.open = true
          this.title = '修改'
        }
      })
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          const projectId = localStorage.getItem('project_id')
          this.form.projectId = projectId
          if (this.form.id !== undefined) {
            updateHandleUser(this.form).then((response) => {
              if (response.success) {
                deleteUserGroup(this.form).then((response) => {
                  if (response.success) {
                    deleteUserResource(this.form).then((response) => {
                      if (response.success) {
                        if ((this.form.groupIds && this.form.groupIds.length > 0) || (this.form.resourceIds && this.form.resourceIds.length > 0)) {
                          if (this.form.groupIds && this.form.groupIds.length > 0 && !(this.form.resourceIds && this.form.resourceIds.length > 0)) {
                            addHandleUserAndGroup({
                              projectId,
                              userId: this.form.id,
                              groupIds: this.form.groupIds
                            }).then((response) => {
                              this.msgSuccess('修改成功')
                              this.open = false
                              this.getList()
                            })
                          }
                          if (this.form.resourceIds && this.form.resourceIds.length > 0 && !(this.form.groupIds && this.form.groupIds.length > 0)) {
                            addHandleUserAndResource({
                              projectId,
                              userId: this.form.id,
                              resourceIds: this.form.resourceIds
                            }).then((response) => {
                              this.msgSuccess('修改成功')
                              this.open = false
                              this.getList()
                            })
                          }
                          if (this.form.groupIds && this.form.groupIds.length > 0 && this.form.resourceIds && this.form.resourceIds.length > 0) {
                            addHandleUserAndGroup({
                              projectId,
                              userId: this.form.id,
                              groupIds: this.form.groupIds
                            }).then((response) => {
                              addHandleUserAndResource({
                                projectId,
                                userId: this.form.id,
                                resourceIds: this.form.resourceIds
                              }).then((response) => {
                                this.msgSuccess('修改成功')
                                this.open = false
                                this.getList()
                              })
                            })
                          }
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
                this.msgError('修改失败')
              }
            })
          } else {
            addHandleUser(this.form).then((response) => {
              if (response.success) {
                const { resourceIds, groupIds, domainName } = this.form
                if ((resourceIds && resourceIds.length > 0) || (groupIds && groupIds.length > 0)) {
                  findHandleUserByDomainName({ projectId, domainName }).then((response) => {
                    if (response.success) {
                      if (groupIds && groupIds.length > 0) {
                        addHandleUserAndGroup({ projectId, userId: response.strData, groupIds }).then((res) => {
                          this.getList()
                        })
                      }
                      if (resourceIds && resourceIds.length > 0) {
                        addHandleUserAndResource({ projectId, userId: response.strData, resourceIds })((res) => {
                          this.getList()
                        })
                      }
                    }
                  })
                }
                this.msgSuccess('新增成功')
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
      const uid = row.uid
      const projectId = localStorage.getItem('project_id')
      this.$confirm('是否确认删除该项数据?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(function() {
          deleteUser({ id: uid, projectId })
        })
        .then(() => {
          deleteUserResource({ id: uid, projectId })
        })
        .then(() => {
          deleteUserGroup({ id: uid, projectId })
        })
        .then(() => {
          this.getList()
          this.msgSuccess('删除成功')
        })
        .catch(function() {})
    }
  }
}
</script>
<style lang="scss">
.dark {
  .el-cascader-node {
    color: #fff;
  }
  .el-cascader__search-input {
    background: transparent;
    font-weight: 500;
  }
  .el-cascader-node:not(.is-disabled):hover,
  .el-cascader-node:not(.is-disabled):focus {
    background: rgba(39, 102, 197, 0.2);
    color: #fff;
  }
  .el-cascader__tags .el-tag .el-icon-close {
    flex: none;
    background-color: rgba(192, 196, 204, 0%);
    color: #fff;
  }
  .el-cascader__tags .el-tag > span {
    color: #fff;
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .el-cascader__tags .el-tag {
    background-color: #4c608a;
    // border-color: #1890ff;
    // color: #1890ff;
  }
  .el-cascader__search-input {
    color: #ffffff;
    background: #474f5f;
  }
  .el-cascader__suggestion-list {
    max-height: 204px;
    margin: 0;
    padding: 6px 0;
    font-size: 14px;
    color: #e3e6eb;
    text-align: center;
  }
  .el-cascader__suggestion-item:hover,
  .el-cascader__suggestion-item:focus {
    background: #4c608a;
  }
}
</style>
