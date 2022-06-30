<template>
  <div class="app-container">
    <div ref="OperateBox" class="operateBox">
      <el-form ref="queryForm" :model="queryParams" :inline="true" label-width="5rem" label-position="left" @submit.native.prevent>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model.trim="queryParams.roleName" placeholder="请输入角色名称" clearable size="small" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
        <el-form-item>
          <el-button v-hasPermi="['system:role:add']" class="addButton" icon="el-icon-plus" size="mini" @click="handleAdd">新增 </el-button>
          <el-button v-hasPermi="['system:role:remove']" class="redButton" icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete">批量删除 </el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="showTableBox" :style="{ height: OperateBoxHeight + 'px' }">
      <el-table v-if="roleList.length" v-loading="loading" :data="roleList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" align="center" min-width="2%" />
        <!--      <el-table-column label="角色编号" prop="roleId" width="120" align="center"  />-->
        <el-table-column label="角色名称" prop="roleName" :show-overflow-tooltip="true" min-width="16%" />
        <el-table-column label="权限字符" prop="roleKey" :show-overflow-tooltip="true" min-width="16%" />
        <el-table-column label="显示顺序" prop="roleSort" :show-overflow-tooltip="true" min-width="16%" />
        <!-- <el-table-column label="状态" align="center" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <el-switch v-model="scope.row.status" active-value="0" inactive-value="1" @change="handleStatusChange(scope.row)" />
          </template>
        </el-table-column> -->
        <el-table-column align="center" label="启用" prop="status" :show-overflow-tooltip="true" min-width="8%">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === '1'" size="small" type="danger">不启用</el-tag>
            <el-tag v-else size="small">启用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" :show-overflow-tooltip="true" align="left" min-width="16%" />
        <!-- <el-table-column label="创建时间" align="center" prop="createTime" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column> -->
        <el-table-column label="操作" align="center" min-width="26%">
          <template slot-scope="scope">
            <el-button v-hasPermi="['system:role:edit']" size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">菜单权限 </el-button>
            <!--<el-button v-hasPermi="['system:role:edit']" size="mini" type="text" icon="el-icon-circle-check"
                       @click="handleDataTypeScope(scope.row)">数据分类权限
            </el-button>-->
            <el-button v-hasPermi="['system:role:edit']" size="mini" type="text" icon="el-icon-circle-check" @click="handleDataScope(scope.row)">数据权限 </el-button>
            <el-button v-hasPermi="['system:role:remove']" size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除 </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-else class="noneDateBox">
        <img src="../../../assets/common/nonedata.png" width="25%" />
        <div>&nbsp;&nbsp;暂无数据~</div>
      </div>
      <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" :page-sizes="[20, 50, 200]" @pagination="getList" />
    </div>
    <!-- 添加或修改角色配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px">
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model.trim="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="权限字符" prop="roleKey">
          <el-input v-model.trim="form.roleKey" placeholder="请输入权限字符" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="roleSort">
          <el-input-number v-model="form.roleSort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in statusOptions" :key="dict.dictValue" :label="dict.dictValue">{{ dict.dictLabel }} </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单">
          <el-tree ref="menu" :default-checked-keys="form.menuIds" :data="menuOptions" show-checkbox node-key="id" empty-text="加载中，请稍后" :props="defaultProps">
            <div slot-scope="{ node, data }" class="custom-tree-node" style="width:100%">
              <span v-if="data.visible === '0'">{{ node.label }}</span>
              <span v-else style="color:#5588ed;font-weight: 700;">{{ node.label }}</span>
            </div>
          </el-tree>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model.trim="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button class="cancel_button" @click="cancel">取 消</el-button>
        <el-button type="primary" class="confirm_button" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 分配角色数据权限对话框 -->
    <el-dialog :title="title" :visible.sync="openDataScope" width="500px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="角色名称">
          <el-input v-model.trim="form.roleName" :disabled="true" />
        </el-form-item>
        <el-form-item label="设备类别">
          <el-input v-model.trim="filterText" placeholder="输入关键字进行过滤" />
          <el-tree ref="dataMenu" :default-checked-keys="checkNodeArr" :data="dataOptions" show-checkbox node-key="id" empty-text="加载中，请稍后" :props="defaultProps" :filter-node-method="filterNode" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button class="cancel_button" @click="cancelDataScope">取 消</el-button>
        <el-button type="primary" class="confirm_button" @click="submitDataScope">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRole, getRole, delRole, addRole, updateRole, exportRole, dataScope, changeRoleStatus } from '@/api/system/role'
import { treeselect as menuTreeselect, roleMenuTreeselect } from '@/api/system/menu'
import { treeselect as deptTreeselect, roleDeptTreeselect } from '@/api/system/dept'
import { getDataTypeOptions, getDataOptions, deleteRoleData, insertRoleData, getRoleData, getRoleDataType, deleteRoleDataTypes, insertRoleDataTypes, getParentDataTypeOptions/* , deleteRoleDataCategory  , insertRoleDataCategory, getRoleDataCategory */} from '@/api/cm/physicalCategory'

export default {
  name: 'Role',
  data() {
    return {
      checkNodeArr: [],
      // 权限树过滤字段
      filterText: '',
      filterText1: '',
      // 页面高度
      OperateBoxHeight: 100,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 总条数
      total: 0,
      // 角色表格数据
      roleList: [],
      // 弹出层标题
      title: '',
      titleType: '',
      // 是否显示弹出层
      open: false,
      // 是否显示弹出层（数据权限）
      openDataScope: false,
      openDataTypeScope: false,
      // 日期范围
      dateRange: [],
      // 状态数据字典
      statusOptions: [],
      // 数据范围选项
      dataScopeOptions: [
        {
          value: '1',
          label: '全部数据权限'
        },
        {
          value: '2',
          label: '自定数据权限'
        },
        {
          value: '3',
          label: '本部门数据权限'
        },
        {
          value: '4',
          label: '本部门及以下数据权限'
        },
        {
          value: '5',
          label: '仅本人数据权限'
        }
      ],
      // 菜单列表
      menuOptions: [],
      // 设备类别列表
      dataOptions: [],
      resource_types: [],
      // 部门列表
      deptOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        roleName: undefined,
        roleKey: undefined,
        status: undefined
      },
      // 表单参数
      form: {},
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      // 表单校验
      rules: {
        roleName: [{ required: true, message: '角色名称不能为空', trigger: 'blur' }],
        roleKey: [{ required: true, message: '权限字符不能为空', trigger: 'blur' }],
        roleSort: [{ required: true, message: '角色顺序不能为空', trigger: 'blur' }]
      }
    }
  },
  watch: {
    filterText(val) {
      this.$refs.dataMenu.filter(val)
    },
    filterText1(val) {
      this.$refs.dataMenu1.filter(val)
    }
  },
  created() {
    this.getList()
    this.getDicts('sys_normal_disable').then((response) => {
      this.statusOptions = response.data
    })
  },
  mounted() {
    this.getOperateBoxHeight()
  },
  updated() {
    this.getOperateBoxHeight()
  },
  methods: {
    // 权限树条件查询
    filterNode(value, data) {
      if (!value) return true
      const stringTemp = data.label.toLowerCase()
      const stringTemp1 = value.toLowerCase()
      return stringTemp.indexOf(stringTemp1) !== -1
    },
    // 获取页面高度
    getOperateBoxHeight() {
      this.$nextTick(function() {
        this.OperateBoxHeight = document.getElementsByClassName('app-container')[0].offsetHeight - document.getElementsByClassName('operateBox')[0].offsetHeight - 10
      })
    },
    getPhysicalCategorys() {
      getPhysicalCategory({ projectId: 10 }).then((response) => {
        this.resource_types = response.datas
      })
    },
    /** 查询角色列表 */
    getList() {
      this.loading = true
      listRole(this.addDateRange(this.queryParams, this.dateRange)).then((response) => {
        this.roleList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    /** 查询菜单树结构 */
    getMenuTreeselect() {
      menuTreeselect().then((response) => {
        this.menuOptions = response.data
        console.log(this.menuOptions)
      })
    },
    /** 查询部门树结构 */
    getDeptTreeselect() {
      deptTreeselect().then((response) => {
        this.deptOptions = response.data
      })
    },
    // 所有菜单节点数据
    getMenuAllCheckedKeys() {
      // 目前被选中的菜单节点
      const checkedKeys = this.$refs.menu.getHalfCheckedKeys()
      // 半选中的菜单节点
      const halfCheckedKeys = this.$refs.menu.getCheckedKeys()
      checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys)
      return checkedKeys
    },
    // 所有部门节点数据
    getDeptAllCheckedKeys() {
      // 目前被选中的部门节点
      const checkedKeys = this.$refs.dept.getHalfCheckedKeys()
      // 半选中的部门节点
      const halfCheckedKeys = this.$refs.dept.getCheckedKeys()
      checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys)
      return checkedKeys
    },
    /** 根据角色ID查询菜单树结构 */
    getRoleMenuTreeselect(roleId) {
      roleMenuTreeselect(roleId).then((response) => {
        this.menuOptions = response.menus
        this.$refs.menu.setCheckedKeys(response.checkedKeys)
      })
    },
    /** 根据角色ID查询部门树结构 */
    getRoleDeptTreeselect(roleId) {
      roleDeptTreeselect(roleId).then((response) => {
        this.deptOptions = response.depts
        this.$refs.dept.setCheckedKeys(response.checkedKeys)
      })
    },
    // 角色状态修改
    handleStatusChange(row) {
      const text = row.status === '0' ? '启用' : '停用'
      this.$confirm('确认要"' + text + '""' + row.roleName + '"角色吗?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(function() {
          return changeRoleStatus(row.roleId, row.status)
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
    // 取消按钮（数据权限）
    cancelDataScope() {
      this.openDataScope = false
      this.reset()
    },
    cancelDataTypeScope() {
      this.openDataTypeScope = false
      this.reset()
    },
    // 表单重置
    reset() {
      if (this.$refs.menu != undefined) {
        this.$refs.menu.setCheckedKeys([])
      }
      this.form = {
        roleId: undefined,
        roleName: undefined,
        roleKey: undefined,
        roleSort: 1000,
        status: '0',
        menuIds: [],
        deptIds: [],
        remark: undefined
      }
      this.resetForm('form')
    },
    /** 查询按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
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
      this.ids = selection.map((item) => item.roleId)
      this.single = selection.length != 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.getMenuTreeselect()
      this.open = true
      this.title = '新增'
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const roleId = row.roleId || this.ids
      this.$nextTick(() => {
        this.getRoleMenuTreeselect(roleId)
      })
      getRole(roleId).then((response) => {
        this.form = response.data
        this.open = true
        this.title = '修改菜单权限'
      })
    },
    /** 分配数据权限操作 */
    handleDataScope(row) {
      this.reset()
      this.checkNodeArr = []
      const roleId = row.roleId || this.ids
      const roleName = row.roleName
      getParentDataTypeOptions({
        projectId: 10,
        roleId: roleId,
        isEnable: 'Y',
        engine: 'Freemarker'
      }).then((response) => {
        const parentdataTypeOptions = response.datas
        getDataTypeOptions({ projectId: 10, roleId: roleId, isEnable: 'Y', engine: 'Freemarker' }).then((response) => {
          const dataTypeOptions = response.datas
          dataTypeOptions.map((valueData) => {
            parentdataTypeOptions.map((valueDataType) => {
              if (!valueDataType['children']) {
                valueDataType['children'] = new Array()
              }
              const valueDataTypeId = valueDataType['id']
              const valueDataPid = valueData['pid']
              const split = valueDataTypeId.split('_')
              const valueDataTypeIdNew = split[split.length - 1]
              if (valueDataPid == valueDataTypeIdNew) {
                valueDataType['children'].push(valueData)
                return valueDataType
              }
            })
          })
          getDataOptions({ projectId: 10, isEnable: 'Y', engine: 'Freemarker' }).then((response) => {
            response.datas.map((valueData) => {
              dataTypeOptions.map((valueDataType) => {
                if (!valueDataType['children']) {
                  valueDataType['children'] = new Array()
                }
                const valueDataTypeId = valueDataType['id']
                const valueDataPid = valueData['pid']
                const split = valueDataTypeId.split('_')
                const valueDataTypeIdNew = split[split.length - 1]
                if (valueDataPid == valueDataTypeIdNew) {
                  valueDataType['children'].push(valueData)
                  return valueDataType
                }
              })
            })
            getRoleData({ projectId: 10, isEnable: 'Y', engine: 'Freemarker', role_id: roleId }).then((response) => {
              /* let tmpDatas=new Array;
              if(response.datas&&response.datas.length>0){
                  tmpDatas=response.datas.map((value)=>{
                  return value['data_id'];
                })
              }*/
              const roleData = response.datas
              getRoleDataType({
                projectId: 10,
                isEnable: 'Y',
                engine: 'Freemarker',
                role_id: roleId
              }).then((response) => {
                const roleDataType = response.datas
                if (roleData.length > 0) {
                  roleDataType.unshift.apply(roleDataType, roleData)
                }
                this.checkNodeArr = roleDataType.map((currentValue) => {
                  return currentValue['id']
                })
              })
            })
            this.dataOptions = parentdataTypeOptions
            this.form.roleName = roleName
            this.form.roleId = roleId
            this.openDataScope = true
            this.title = '分配数据权限'
          })
        })
      })
    },
    removeScopeEl(scopeDatas) {
      const arr = scopeDatas.trim().split(',')
      const newArr = []
      this.resource_types.forEach((el) => {
        if (arr.indexOf(el.name) == -1) {
          newArr.push(el)
        }
      })
      this.resource_types = newArr
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          if (this.form.roleId != undefined) {
            this.form.menuIds = this.getMenuAllCheckedKeys()
            updateRole(this.form).then((response) => {
              if (response.code === 200) {
                this.msgSuccess('修改成功')
                this.open = false
                this.getList()
              } else {
                this.msgError(response.msg)
              }
            })
          } else {
            this.form.menuIds = this.getMenuAllCheckedKeys()
            addRole(this.form).then((response) => {
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
    /** 提交按钮（数据权限） */
    submitDataScope: function(rows) {
      const roleId = this.form.roleId
      if (roleId != undefined) {
        // 1获取选中的树节点
        /* let checkedNodes = this.$refs.dataMenu.getCheckedNodes();
        let checkedRootNodes=checkedNodes.filter((currentValue)=>{
          return !!currentValue.children;
        })
        let checkedLeafNodes=checkedNodes.filter((currentValue)=>{
          return !currentValue.children;
        })
        let checkedRootKeys=checkedRootNodes.map((currentValue)=>{
          let split = currentValue['id'].split('_');
          let splitElement = split[split.length-1];
          return splitElement;
        })
        let checkedLeafKeys=checkedLeafNodes.map((currentValue)=>{
          let split = currentValue['id'].split('_');
          let splitElement = split[split.length-1];
          return splitElement;
        })
        //2删除角色与数据关联后添加角色与数据关联
        let roleDatas=checkedLeafKeys.map((currentValue)=>{
          return {role_id:roleId,data_id:currentValue}
        })
        let roleDataTypes=checkedRootKeys.map((currentValue)=>{
          return {role_id:roleId,data_id:currentValue}
        })*/
        const checkedNodes = this.$refs.dataMenu.getCheckedNodes(false, false)
        const checkedCategoryNodes = checkedNodes.filter((currentValue) => {
          const split = currentValue['id'].split('_')
          // 后端返回的node key中包含class的为上级分类
          const flag = split.indexOf('class')
          return flag != -1
        })
        const checkedResourceNodes = checkedNodes.filter((currentValue) => {
          const split = currentValue['id'].split('_')
          // 后端返回的node key中包含class的为上级分类
          const flag = split.indexOf('class')
          return flag == -1
        })
        const checkedCategoryKeys = checkedCategoryNodes.map((currentValue) => {
          const split = currentValue['id'].split('_')
          const splitElement = split[split.length - 1]
          return splitElement
        })
        const checkedResourceKeys = checkedResourceNodes.map((currentValue) => {
          const split = currentValue['id'].split('_')
          const splitElement = split[split.length - 1]
          return splitElement
        })
        // 2删除角色与数据关联后添加角色与数据关联
        const roleDatas = checkedResourceKeys.map((currentValue) => {
          return { role_id: roleId, data_id: currentValue }
        })
        const roleDataTypes = checkedCategoryKeys.map((currentValue) => {
          return { role_id: roleId, data_id: currentValue }
        })

        deleteRoleData({ role_id: roleId, projectId: 10 }).then((response) => {
          insertRoleData({ roleDatas: roleDatas, projectId: 10, engine: 'Freemarker' }).then((response) => {
            deleteRoleDataTypes({ role_id: roleId, projectId: 10 }).then((response) => {
              insertRoleDataTypes({
                roleDataTypes: roleDataTypes,
                projectId: 10,
                engine: 'Freemarker'
              }).then((response) => {
                this.msgSuccess('修改成功')
                this.openDataScope = false
                this.getList()
              })
            })
          })
        })
        // 3
        /* let checkedKeys = this.$refs.dataMenu.getCheckedKeys();
        let roleDatas=checkedKeys.map((value,index,array)=>{
          return {role_id:roleId,data_id:value}
        })
        deleteRoleData({role_id:roleId,projectId: 10}).then((response)=>{
          insertRoleData({roleDatas:roleDatas,projectId: 10,engine: 'Freemarker'}).then((response)=>{
            this.msgSuccess("修改成功");
            this.openDataScope = false;
            this.getList();
          })
        })*/
        /* this.form.data_scope =
          this.form.dataScope + "," + this.form.data_scope.join(",");
        this.form.projectId = 10;
        setRoleDataScope(this.form).then((response) => {
          this.msgSuccess("修改成功");
          this.openDataScope = false;
          this.getList();
        });*/
      }
    },
    /** 提交按钮（数据分类权限） */
    /* submitDataTypeScope: function(rows) {
      const roleId = this.form.roleId
      if (roleId != undefined) {
        const checkedNodes = this.$refs.dataMenu1.getCheckedNodes(false, true)
        const checkedCategoryNodes = checkedNodes.filter((currentValue) => {
          const split = currentValue['id'].split('_')
          // 后端返回的node key中包含class的为上级分类
          if (currentValue['children']) {
            return false
          }
          const flag = split.indexOf('class')
          return flag != -1
        })
        const checkedCategoryKeys = checkedCategoryNodes.map((currentValue) => {
          const split = currentValue['id'].split('_')
          const splitElement = split[split.length - 1]
          return splitElement
        })
        // 2删除角色与数据关联后添加角色与数据关联
        const roleDataTypes = checkedCategoryKeys.map((currentValue) => {
          return { role_id: roleId, data_id: currentValue }
        })
        deleteRoleDataCategory({ role_id: roleId, projectId: 10 }).then((response) => {
          insertRoleDataCategory({ roleDataTypes: roleDataTypes, projectId: 10, engine: 'Freemarker' }).then((response) => {
            this.msgSuccess('修改成功')
            this.openDataTypeScope = false
            this.getList()
          })
        })
      }
    }, */
    /** 删除按钮操作 */
    handleDelete(row) {
      const roleIds = row.roleId || this.ids
      this.$confirm('是否确认删除该项数据?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(function() {
          return delRole(roleIds)
        })
        .then((res) => {
          if (res['code'] !== 500) {
            this.getList()
            this.msgSuccess('删除成功')
          } else {
            this.msgInfo(res['msg'])
          }
        })
        .catch(function() {})
    }
  }
}
</script>
