<template>
  <div class="app-container">
    <div class="operateBox">
      <el-form ref="queryForm" :model="queryParams" :inline="true" label-position="left">
        <el-form-item label="设备类别" prop="name">
          <el-input v-model.trim="queryParams.name" placeholder="请输入指标设备类别或显示名称" clearable size="small" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="启用" prop="isEnable">
          <el-select v-model="queryParams.isEnable" placeholder="请选择" clearable size="small" @change="handleQuery">
            <el-option v-for="dict in typeOptions" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          <el-button v-hasPermi="['system:config:add']" class="addButton" icon="el-icon-plus" size="mini" @click="handleAdd">新增</el-button>
          <!-- <el-button class="redButton" icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:group:remove']">删除</el-button> -->
        </el-form-item>
      </el-form>
    </div>
    <div class="showTableBox" :style="{height:OperateBoxHeight+'px'}">
      <el-table v-if="configList.length" v-loading="loading" :data="configList" row-key="id" :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
        <!-- <el-table-column type="selection" width="55" align="center" /> -->
        <el-table-column :show-overflow-tooltip="true" prop="name" header-align="left" align="left" label="设备类别">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isEnable=='N'" size="small" type="warning">{{ scope.row.name }}</el-tag>
            <span v-if="scope.row.isEnable=='Y'">{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column :show-overflow-tooltip="true" prop="labelText" header-align="left" align="left" label="显示名称" />
        <el-table-column :show-overflow-tooltip="true" prop="parentName" header-align="left" align="left" label="上级类别" />
        <el-table-column :show-overflow-tooltip="true" prop="shortName" header-align="left" align="left" label="短名称" />
        <el-table-column :show-overflow-tooltip="true" prop="displayOrder" header-align="left" align="left" label="显示顺序" />
        <el-table-column prop="isEnable" header-align="center" align="center" label="启用">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isEnable=='N'" size="small" type="danger">不启用</el-tag>
            <el-tag v-else size="small">启用</el-tag>
          </template>
        </el-table-column>
        <el-table-column :show-overflow-tooltip="true" prop="remark" header-align="left" align="left" label="备注" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button v-hasPermi="['system:group:edit']" size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button v-hasPermi="['system:group:remove']" size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <Deficiency v-else height="auto" width="25%" />
      <pagination v-show="total>0" :page-sizes="[10, 20, 50, 200]" :total="total" :page.sync="queryParams.start" :limit.sync="queryParams.limit" @pagination="getList" />
    </div>

    <!-- 添加或修改参数配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="50%">
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="上级类别" prop="parentId">
          <el-select v-model="form.parentId" placeholder="请选择上级类别" clearable filterable>
            <el-option v-for="item in physicalCategoryOptions" :key="item.id" :label="item.labelText" :value="item.id" :style="{display:item.parentId===0?'block':'none'}" />
          </el-select>
          <!-- <treeselect v-model="form.parentId" :options="physicalCategoryOptions" :flat="true" :normalizer="normalizer" :default-expand-level="1" :multiple="false" placeholder="选择上级类别" /> -->
        </el-form-item>
        <el-form-item label="设备类别" prop="name">
          <el-input v-model.trim="form.name" placeholder="设备类别" clearable />
        </el-form-item>
        <el-form-item label="显示名称" prop="labelText">
          <el-input v-model.trim="form.labelText" type="labelText" clearable placeholder="显示名称" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="displayOrder">
          <el-input-number v-model="form.displayOrder" type="displayOrder" :min="1000" :step="10" placeholder="显示顺序" controls-position="right" />
        </el-form-item>
        <el-form-item label="短名称" prop="shortName">
          <el-input v-model.trim="form.shortName" type="shortName" clearable placeholder="短名称" />
        </el-form-item>
        <el-form-item label="启用" prop="isEnable">
          <el-radio-group v-model="form.isEnable">
            <el-radio v-for="dict in typeOptions" :key="dict.dictValue" :label="dict.dictValue">{{ dict.dictLabel }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="纳管类别" prop="is_monitored">
          <el-radio-group v-model="form.is_monitored">
            <el-radio label="Y">纳管</el-radio>
            <el-radio label="N">非纳管</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model.trim="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
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
  /*  listPhysicalCategory, */
  getPhysicalCategory,
  delPhysicalCategory,
  addPhysicalCategory,
  updatePhysicalCategory,
  getPhysicalCategories
} from '@/api/cm/physicalCategory'
import { getProjectId } from '@/api/utils'
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import Deficiency from '@/components/Deficiency/index'
export default {
  components: { Treeselect, Deficiency },
  data() {
    return {
      OperateBoxHeight: 100,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      projectId: '',
      names: [],
      // 总条数
      total: 0,
      // 参数表格数据
      configList: [],
      configListId: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 类型数据字典
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
      physicalCategoryOptions: [],
      physicalId: [],
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        // start: 1,
        // limit: 10,
        name: undefined,
        isEnable: 'Y',
        projectId: 10,
        engine: 'Freemarker'
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: '类别名称不能为空', trigger: 'blur' }
        ],
        /* parentId: [
          { required: true, message: "上级类别不能为空", trigger: "blur" },
        ], */
        displayOrder: [
          { required: true, message: '显示顺序不能为空', trigger: 'blur' }
        ],
        isEnable: [
          { required: true, message: '是否可用不能为空', trigger: 'blur' }
        ],
        is_monitored: [
          { required: true, message: '请选择设备描述', trigger: 'blur' }
        ],
        shortName: [
          { required: true, message: '短名称不能为空', trigger: 'blur' },
          { pattern: /^([A-Za-z][A-Za-z0-9_]*)*$/, message: '指标名称要字母开头，且只能输入字母数字或者下划线' }
        ]
      }
    }
  },
  created() {
    this.queryParams.projectId = localStorage.getItem('project_id')
    this.getList()
    this.optionsParams()
    // this.getDicts("sys_yes_no").then(response => {
    //   this.typeOptions = response.data;
    // });
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
    /** 查询参数列表 */
    getList() {
      this.loading = true
      getPhysicalCategories(this.queryParams).then((response) => {
        if (response.success) {
          // this.configList = response.datas;
          // this.total = response.totalCount;
          if (response.datas.filter((item) => item.parentId == '0').length) {
            response.datas.forEach(item => {
              // console.log(item.parentId,item.id)
            })
            this.configList = []
            this.configListId = []
            this.changeDataFormat(
              response.datas,
              this.configList,
              this.configListId
            )
          } else {
            this.configList = response.datas
          }
        } else {
          this.configList = []
          // this.total = 0;
        }
        this.loading = false
      })
    },
    // 转换数据
    normalizer(node) {
      // console.log(node);
      if (node.children && !node.children.length) {
        delete node.children
      }
      return {
        id: node.id,
        label: node.name,
        isDisabled: node.isEnable,
        is_monitored: node.is_monitored,
        children: node.children
      }
    },
    // 参数系统内置字典翻译
    typeFormat(row, column) {
      return this.selectDictLabel(this.typeOptions, row.flag)
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        name: undefined,
        labelText: undefined,
        shortName: undefined,
        parentId: undefined,
        iconId: undefined,
        isEnable: 'Y',
        is_monitored: 'Y',
        projectId: 10,
        displayOrder: 1000,
        remark: undefined
      }
      this.resetForm('form')
    },
    /** 查询按钮操作 */
    handleQuery() {
      this.queryParams.start = 1
      // this.queryParams.isEnable = "Y";
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    optionsParams() {
      getPhysicalCategories({
        projectId: this.queryParams.projectId,
        engine: 'Freemarker'
      }).then((response) => {
        if (response.datas) {
          // 转换数据格式
          // response.datas.forEach((item) => {
          //   if (item.labelText != "") {
          //     if (item.parentId == "0") {
          //       item.children = [];
          //       this.physicalCategoryOptions.push(item);
          //       this.physicalId.push(item.id);
          //     } else {
          //       let itemIndex = this.physicalId.indexOf(item.parentId);
          //       if (itemIndex != -1) {
          //         this.physicalCategoryOptions[itemIndex].children.push(item);
          //       }
          //     }
          //   }
          // });
          response.datas.forEach((item) => {
            item.isEnable = true
          })
          this.physicalCategoryOptions = []
          this.physicalId = []
          this.changeDataFormat(
            response.datas,
            this.physicalCategoryOptions,
            this.physicalId
          )
          // console.log(this.physicalCategoryOptions);
          // 如果是顶级的话，处理成不能点击
          this.physicalCategoryOptions.forEach((item) => {
            // console.log(item.isEnable);
            // item.isEnable = true;
            if (item.parentId == '0') {
              item.isEnable = false
            }
          })
          // console.log(this.physicalCategoryOptions);
        }
      })
    },
    // 转换数据格式
    changeDataFormat(datas, list, listid) {
      // console.log(datas);
      datas.forEach((item) => {
        // 判断当前节点是否被使用
        // item.isUse = false;
        // 判断是否为顶级节点是的话
        if (item.parentId == '0') {
          item.children = []
          // item.isUse = true;
          list.push(item)
          listid.push(item.id)
        }
        // else {
        //   let itemIndex = listid.indexOf(item.parentId);
        //   if (itemIndex != -1) {
        //     item.isUse = true;
        //     list[itemIndex].children.push(item);
        //   }
        // }
      })
      datas.forEach((item) => {
        if (item.parentId != '0') {
          const itemIndex = listid.indexOf(item.parentId)
          if (itemIndex != -1) {
            // item.isUse = true;
            list[itemIndex].children.push(item)
          }
        }
      })
      // console.log(datas);
      // let tempList = datas.filter((item) => item.isUse == false);
      // list.forEach((item) => {
      //   if (item.children && item.children.length) {
      //     item.children.forEach((itemc) => {
      //       tempList.forEach((itemcm) => {
      //         if (itemcm.parentId == itemc.id) {
      //           if (!itemc.children) {
      //             itemc.children = [];
      //           }
      //           itemcm.isUse = true;
      //           itemc.children.push(itemcm);
      //         }
      //       });
      //     });
      //   }
      // });
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增'
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id)
      this.names = selection.map((item) => item.name)
      this.single = selection.length != 1
      this.multiple = !selection.length
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getPhysicalCategory({
        projectId: this.queryParams.projectId,
        engine: 'Freemarker',
        id: id
      }).then((response) => {
        this.form = response.mapData
        console.log(response)
        if (this.form.parentId != '0') {
          this.form.parentId = parseInt(response.mapData.parentId)
        } else {
          this.form.parentId = null
        }
        this.open = true
        this.title = '修改'
      })
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          if (this.form.labelText == undefined) {
            this.form.labelText = this.form.name
          }
          if (this.form.shortName) {
            this.form.shortName = this.form.shortName.toUpperCase()
          }
          this.form.projectId = this.queryParams.projectId
          this.form.engine = 'Freemarker'
          if (this.form.parentId == '' || this.form.parentId == undefined) {
            this.form.parentId = 0
          }
          // console.log(JSON.stringify(this.form));
          if (this.form.id != undefined) {
            updatePhysicalCategory(this.form).then((response) => {
              if (response.success) {
                this.msgSuccess('修改成功')
                this.open = false
                this.getList()
                this.reset()
              } else {
                this.msgError(response.errorMsg)
              }
            })
          } else {
            addPhysicalCategory(this.form).then((response) => {
              if (response.success) {
                this.msgSuccess('新增成功')
                this.open = false
                this.getList()
                this.reset()
              } else {
                this.msgError(response.errorMsg)
              }
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      const name = row.name || this.names
      this.$confirm('是否确认删除参数编号为"' + name + '"的数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          delPhysicalCategory({
            projectId: this.queryParams.projectId,
            engine: 'Freemarker',
            ids: row.id
          }).then((response) => {
            if (response.success) {
              this.getList()
            } else {
              this.msgError('删除失败：\n' + response.errorMsg)
            }
          })
        })
        .catch(function(e) {
          console.error(e.message)
        })
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams
      this.$confirm('是否确认导出所有参数数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(function() {
          return exportConfig(queryParams)
        })
        .then((response) => {
          this.download(response.msg)
        })
        .catch(function() {})
    }
  }
}
</script>
