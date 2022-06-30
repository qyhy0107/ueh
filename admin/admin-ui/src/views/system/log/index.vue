<template>
  <div class="app-container">
    <div class="btnBox">
      <el-form ref="queryForm" :model="queryParams" :inline="true" label-width="68px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="系统模块" prop="title">
              <el-select
                v-model="title"
                placeholder="请选择系统模块"
                clearable
                size="small"
                style="width: 240px"
              >
                <el-option
                  v-for="dict in typeSelects"
                  :key="dict.title"
                  :label="dict.title"
                  :value="dict.title"
                  @change.native="handleQuery"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="操作人员" prop="operName">
              <el-input
                v-model="operName"
                placeholder="请输入操作人员"
                clearable
                style="width: 240px;"
                size="small"
                @keyup.enter.native="handleQuery"
              />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select
                v-model="status"
                placeholder="操作状态"
                clearable
                size="small"
                style="width: 240px"
              >
                <el-option
                  v-for="dict in typeOptions"
                  :key="dict.dictValue"
                  :label="dict.dictLabel"
                  :value="dict.dictValue"
                  @change.native="handleQuery"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="操作时间">
              <el-date-picker
                v-model="dateRange"
                size="small"
                style="width: 240px"
                value-format="yyyy-MM-dd"
                type="daterange"
                range-separator="-"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">查询</el-button>
              <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div class="tableBox">
      <el-table v-loading="loading" :data="list" height="420">
        <el-table-column v-if="show" label="日志类型" align="center" prop="logtype" />
        <el-table-column label="模块标题" align="center" prop="title" />
        <el-table-column label="业务类型" align="center" prop="businessType" :formatter="typeFormat" />
        <el-table-column label="操作人员" align="left" header-align="center" prop="operName" />
        <el-table-column label="主机地址" align="left" header-align="center" prop="operIp" />
        <el-table-column label="操作状态" align="center" prop="status" :formatter="statusType" />
        <el-table-column label="操作日期" align="center" prop="operTime" width="180">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.operTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          align="center"
          class-name="small-padding fixed-width"
          width="200"
        >
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-view"
              @click="handleView(scope.row, scope.index004)"
            >详细</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total>0"
        :total="total"
        :page-sizes="[10, 20, 50, 200]"
        :page.sync="queryParams.start"
        :limit.sync="queryParams.limit"
        @pagination="getFindList"
      />
    </div>

    <!-- 操作日志详细 -->
    <el-dialog title="操作日志详细" :visible.sync="open" width="700px">
      <el-form ref="form" :model="form" label-width="100px" size="mini">
        <el-row>
          <el-col :span="12">
            <el-form-item label="操作模块：">{{ form.title }} / {{ form.businessType }}</el-form-item>
            <el-form-item label="登录信息：">{{ form.operName }} / {{ form.operIp }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="请求地址：">{{ form.operIp }}</el-form-item>
            <el-form-item label="请求方式：">{{ form.requestMethod }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="操作方法：">{{ form.method }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="请求参数：">
              {{
                form.operParam
              }}
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="返回参数：">
              {{
                form.jsonResult
              }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作状态：">
              <div v-if="form.status === 0">正常</div>
              <div v-else-if="form.status === 1">失败</div>
              <div v-else-if="form.status === '正常'">正常</div>
              <div v-else-if="form.status === '失败'">失败</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作时间：">
              {{
                parseTime(form.operTime)
              }}
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item v-if="form.status != 0" label="异常信息：">
              {{
                form.jsonResult
              }}
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="open = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  list,
  delOperlog,
  cleanOperlog,
  exportOperlog,
  findlistData,
  findlistTimeData,
  findlogfail,
  findlogfailtime,
  getSelect
} from '@/api/monitor/operlog'
import S from 'string'
import moment from 'moment'

export default {
  data() {
    return {
      show: false,
      title: '',
      operName: '',
      status: '',
      businessType: '',
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非多个禁用
      multiple: true,
      // 总条数
      total: 0,
      // 表格数据
      list: [],
      // 是否显示弹出层
      open: false,
      typeOptions: [
        { dictValue: '正常', dictLabel: '正常' },
        { dictValue: '失败', dictLabel: '失败' }
      ],

      // typeSelects: [
      //   { dictValue: "用户管理", dictLabel: "用户管理" },
      //   { dictValue: "配置参数管理", dictLabel: "配置参数管理" },
      //   { dictValue: "转发响应", dictLabel: "转发响应" },
      //   { dictValue: "登录", dictLabel: "登录" }
      // ],
      typeSelects: [],
      // 类型数据字典
      statusOptions: [],
      // 日期范围
      dateRange: [
        moment()
          .subtract(1, 'd')
          .toDate(),
        moment().toDate()
      ],
      // 表单参数
      form: {},
      // 查询参数
      queryParams: {
        start: 1,
        limit: 10,
        operName: undefined,
        status: undefined
      }
    }
  },
  created() {
    this.getFindList()
    this.getSelectData()
  },
  methods: {
    getSelectData() {
      getSelect().then(response => {
        this.typeSelects = response.list
      })
    },
    // 操作日志状态字典翻译
    statusFormat(row, column) {
      return this.selectDictLabel(this.statusOptions, row.status)
    },
    // 操作日志类型字典翻译
    typeFormat: function(row, column) {
      var a = row.businessType
      if (parseFloat(a).toString() == 'NaN') {
        return a
      } else {
        switch (row.businessType) {
          case '0':
            return '其它'
            break
          case '1':
            return '新增'
            break
          case '2':
            return '修改'
            break
          case '3':
            return '删除'
            break
          case '4':
            return '授权'
            break
          case '5':
            return '导出'
            break
          case '6':
            return '导入'
            break
          case '7':
            return '强退'
            break
          case '8':
            return '生成代码'
            break
          case '9':
            return '清空数据'
            break
        }
      }
    },

    ollType: function(row, column) {
      var a = row.logtype
      if (a == 'hl') {
        switch (row.operName) {
          case '1':
            return '接收查询请求'
          case '2':
            return '处理查询请求'
          case '3':
            return '转发查询请求'
          case '4':
            return '接收查询请求响应并转发监委'
          case '5':
            return '查询请求中断或失败异常'
          case '6':
            return '接收运营商推送查询结果'
          case '7':
            return '处理并转发查询结果'
          case '8':
            return '接收监委查询结果发送响应'
          case '9':
            return '转发查询结果发送响应到运营商'
          case '10':
            return '查询结果中断或失败异常'
          case '11':
            return 'IP认证标识'
          case '12':
            return '请求头校验标识'
          case 'admin':
            return 'admin'
        }
      } else if (a == 'ol') {
        return row.operName
      } else if (a == 'li') {
        return row.operName
      }
    },

    statusType: function(row, column) {
      return row.status == 0
        ? '正常'
        : row.status == 1
          ? '失败'
          : row.status == '正常'
            ? '正常'
            : row.status == '失败'
              ? '失败'
              : row
    },
    /** 查询按钮操作 */
    handleQuery() {
      this.queryParams.start = 1
      this.getFindList()
    },
    getFindList() {
      this.loading = true
      if (
        this.title == '' &&
        this.operName == '' &&
        this.status == '' &&
        this.dateRange
      ) {
        // 页面初始加载
        list({

          start: this.queryParams.start,
          end: this.queryParams.limit,
          startTime: moment(this.dateRange[0]).format('YYYY-MM-DD HH:mm:ss'),
          endTime: moment(this.dateRange[1]).format('YYYY-MM-DD HH:mm:ss')
        }).then(response => {
          // alert(JSON.stringify(response.rows));
          this.list = response.rows
          this.total = response.total
          this.loading = false
        })
      } else {
        this.loading = true
        var name
        if (this.operName == '监委') {
          name = '01'
        } else if (this.operName == '运营商') {
          name = '99'
        } else {
          name = this.operName
        }
        var status = this.status
        if (status != '') {
          if (status == '正常') {
            status = '0'
            if (!this.dateRange) {
              // 正常不带时间
              findlistData({
                title: this.title,
                name: name,
                status: status,
                start: this.queryParams.start,
                end: this.queryParams.limit
              }).then(response => {
                this.list = response.datas
                this.total = response.total
                this.loading = false
              })
            } else {
              // 正常带时间
              findlistTimeData({
                title: this.title,
                name: name,
                status: status,
                start: this.queryParams.start,
                end: this.queryParams.limit,
                startTime: moment(this.dateRange[0]).format(
                  'YYYY-MM-DD HH:mm:ss'
                ),
                endTime: moment(this.dateRange[1]).format('YYYY-MM-DD HH:mm:ss')
              }).then(response => {
                this.list = response.datas
                this.total = response.total
                this.loading = false
              })
            }
          } else if (status == '失败') {
            status = '1'
            if (!this.dateRange) {
              // 失败不带时间
              findlogfail({
                title: this.title,
                name: name,
                status: status,
                start: this.queryParams.start,
                end: this.queryParams.limit
              }).then(response => {
                this.list = response.datas
                this.total = response.total
                this.loading = false
              })
            } else {
              // 失败带时间
              findlogfailtime({
                title: this.title,
                name: name,
                status: status,
                start: this.queryParams.start,
                end: this.queryParams.limit,
                startTime: moment(this.dateRange[0]).format(
                  'YYYY-MM-DD HH:mm:ss'
                ),
                endTime: moment(this.dateRange[1]).format('YYYY-MM-DD HH:mm:ss')
              }).then(response => {
                this.list = response.datas
                this.total = response.total
                this.loading = false
              })
            }
          }
        } else if (status == '') {
          if (!this.dateRange) {
            // 空 不带时间
            findlistData({
              title: this.title,
              name: name,
              start: this.queryParams.start,
              end: this.queryParams.limit
            }).then(response => {
              this.list = response.datas
              this.total = response.total
              this.loading = false
            })
          } else {
            // 空 带时间
            findlistTimeData({
              title: this.title,
              name: name,
              start: this.queryParams.start,
              end: this.queryParams.limit,
              startTime: this.dateRange[0],
              endTime: this.dateRange[1]
            }).then(response => {
              this.list = response.datas
              this.total = response.total
              this.loading = false
            })
          }
        }
        this.loading = true
      }
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [
        moment()
          .subtract(1, 'd')
          .toDate(),
        moment().toDate()
      ]
      this.operName = ''
      this.title = ''
      this.status = ''
      this.queryParams.start = 1
      this.handleQuery()
    },
    /** 详细按钮操作 */
    handleView(row) {
      this.open = true

      this.form = row
    }
  }
}
</script>
<style lang="scss" scoped>
.btnBox {
  background-color: #093757;
  /*height: 110px;*/
  padding-top: 10px;
  padding-left: 10px;
  border-radius: 2px;
  margin-bottom: 5px;
  .el-form-item {
    margin-bottom: 5px;
  }
  /deep/.el-form-item__label {
  color: #A8B3BA;
  }
}
.tableBox {
  padding: 10px;
  background-color: #093757;
  border-radius: 2px;
}
.pagination-container{
    background: #093757 ;
    padding: 32px 16px;
    overflow:hidden;
    height: 50px;
}
</style>
