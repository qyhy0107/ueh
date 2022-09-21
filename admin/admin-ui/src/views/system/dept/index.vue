<template>
  <div class="app-container">
    <div ref="OperateBox" class="operateBox">
      <el-form :inline="true" @submit.native.prevent>
        <el-form-item label="部门名称">
          <el-input v-model.trim="queryParams.deptName" placeholder="请输入部门名称" clearable size="small"
            @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button class="blueButton" type="primary" icon="el-icon-search" size="mini" @click="handleQuery">查询
          </el-button>
          <el-button class="grayButton" icon="el-icon-refresh" size="mini"
            @click="queryParams.deptName='';handleQuery()">重置</el-button>
          <el-button v-hasPermi="['system:dept:add']" class="grayButton" icon="el-icon-plus" size="mini"
            @click="handleAdd">新增</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="showTableBox" :style="{height:OperateBoxHeight+'px'}">
      <el-table  empty-text=" " element-loading-text="拼命加载中..."  v-loading="loading" :data="deptList" row-key="deptId" default-expand-all
        :tree-props="{children: 'children', hasChildren: 'hasChildren'}" :style="{height:OperateBoxHeight-60+'px'}">
        <template v-if="!loading" slot="empty" style="height:100%">
          <Deficiency width="30%" height="auto" />
        </template>
        <el-table-column prop="deptName" label="部门名称" width="260" />
        <el-table-column prop="orderNum" label="排序" width="200" />
        <!-- <el-table-column prop="status" label="状态" :formatter="statusFormat" width="100"></el-table-column> -->
        <el-table-column align="center" label="启用" prop="status" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span v-if="scope.row.status === '1'" class="brownColor">不启用</span>
            <span v-else>启用</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="200">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" width="200" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button v-hasPermi="['system:dept:edit']" size="mini" type="text" icon="el-icon-edit"
              @click="handleUpdate(scope.row)">修改</el-button>
            <el-button v-hasPermi="['system:dept:add']" size="mini" type="text" icon="el-icon-plus"
              @click="handleAdd(scope.row)">新增</el-button>
            <el-button v-hasPermi="['system:dept:remove']" size="mini" type="text" icon="el-icon-delete"
              @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    
    </div>
    <!-- 添加或修改部门对话框 -->
    <el-dialog :title="title" :visible.sync="open">
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col v-if="form.parentId !== 0" :span="24">
            <el-form-item label="上级部门" prop="parentId">
              <treeselect v-model="form.parentId" :options="deptOptions" :normalizer="normalizer"
                placeholder="选择上级部门" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门名称" prop="deptName">
              <el-input v-model.trim="form.deptName" placeholder="请输入部门名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示顺序" prop="orderNum">
              <el-input-number v-model="form.orderNum" controls-position="right" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人" prop="leader">
              <el-input v-model.trim="form.leader" placeholder="请输入负责人" maxlength="20" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model.trim="form.phone" placeholder="请输入联系电话" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model.trim="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="启用">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in statusOptions" :key="dict.dictValue" :label="dict.dictValue">{{ dict.dictLabel
                }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col>
            <el-form-item label="备注" prop="remark">
              <el-input v-model.trim="form.remark" type="textarea" clearable />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button class="cancel_button" @click="cancel">取 消</el-button>
        <el-button type="primary" class="confirm_button" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listDept,
  getDept,
  delDept,
  addDept,
  updateDept
} from '@/api/system/dept'
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'

import Deficiency from '@/components/Deficiency'

export default {
  name: 'Dept',
  components: { Treeselect,Deficiency },
  data() {
    return {
      // 页面高度
      OperateBoxHeight: 100,
      // 遮罩层
      loading: true,
      // 表格树数据
      deptList: [],
      // 部门树选项
      deptOptions: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 状态数据字典
      statusOptions: [],
      // 查询参数
      queryParams: {
        deptName: undefined,
        status: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        // parentId: [
        //   { required: true, message: "上级部门不能为空", trigger: "blur" },
        // ],
        deptName: [
          { required: true, message: '部门名称不能为空', trigger: 'blur' }
        ],
        orderNum: [
          { required: true, message: '菜单顺序不能为空', trigger: 'blur' }
        ],
        email: [
          {
            type: 'email',
            message: "'请输入正确的邮箱地址",
            trigger: ['blur', 'change']
          }
        ],
        phone: [
          {
            pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
            message: '请输入正确的手机号码',
            trigger: 'blur'
          }
        ]
      }
    }
  },
  created() {
    this.getList()
    this.getDicts('sys_normal_disable').then((response) => {
      this.statusOptions = response.data
    })
  },
  updated() {
    this.getOperateBoxHeight()
  },
  mounted() {
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
    /** 查询部门列表 */
    getList() {
      this.loading = true
      listDept(this.queryParams).then((response) => {
        this.deptList = this.handleTree(response.data, 'deptId')
        this.loading = false
      })
    },
    /** 转换部门数据结构 */
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children
      }
      return {
        id: node.deptId,
        label: node.deptName,
        children: node.children
      }
    },
    /** 查询部门下拉树结构 */
    getTreeselect() {
      listDept().then((response) => {
        this.deptOptions = this.handleTree(response.data, 'deptId')
      })
    },
    // 字典状态字典翻译
    statusFormat(row, column) {
      return this.selectDictLabel(this.statusOptions, row.status)
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        deptId: undefined,
        parentId: undefined,
        deptName: undefined,
        orderNum: 0,
        leader: undefined,
        phone: undefined,
        email: undefined,
        status: '0'
      }
      this.resetForm('form')
    },
    /** 查询按钮操作 */
    handleQuery() {
      this.getList()
    },
    /** 新增按钮操作 */
    handleAdd(row) {
      this.reset()
      this.getTreeselect()
      if (row != undefined) {
        this.form.parentId = row.deptId
      }
      this.open = true
      this.title = '新增'
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      this.getTreeselect()
      getDept(row.deptId).then((response) => {
        this.form = response.data
        this.open = true
        this.title = '修改'
      })
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          if (this.form.deptId != undefined) {
            updateDept(this.form).then((response) => {
              if (response.code === 200) {
                this.msgSuccess('修改成功')
                this.open = false
                this.getList()
              } else {
                this.msgError(response.msg)
              }
            })
          } else {
            addDept(this.form).then((response) => {
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
      this.$confirm(
        '是否确认删除名称为"' + row.deptName + '"的数据项?',
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
        .then(function() {
          return delDept(row.deptId)
        })
        .then((response) => {
          if (response.code == 200) {
            this.msgSuccess(response.msg)
          } else {
            this.msgError(response.msg)
          }
          this.getList()
        })
        .catch(function() {})
    }
  }
}
</script>
