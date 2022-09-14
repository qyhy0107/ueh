<template>
  <div class="app-container">
    <div ref="OperateBox" class="operateBox">
      <el-form :inline="true" :model="formInline" label-position="left" @submit.native.prevent>
        <el-form-item label="配置名称" prop="name">
          <el-input v-model.trim="formInline.name" clearable placeholder="请输入配置名称" size="small"
            @keyup.enter.native="onSearch" />
        </el-form-item>
        <el-form-item>
          <el-button class="blueButton" size="mini" @click="onSearch">查询</el-button>
          <el-button class="grayButton" size="mini" @click="onRest">重置</el-button>
          <el-button class="grayButton" size="mini" @click="onAdd">新增</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="showTableBox" :style="{ height: OperateBoxHeight + 'px' }">
      <el-table :data="dataList" :style="{ height: OperateBoxHeight-60 + 'px' }" empty-text=" " element-loading-text="拼命加载中..." >
        <!-- <el-table-column prop="id" label="id" v-if="show" width="160" /> -->
        <template  slot="empty" style="height:100%">
          <Deficiency width="30%" height="auto" />
        </template>
        <el-table-column prop="name" label="配置名称" width="180" />
        <!-- <el-table-column prop="equipment_category" label="设备类别">
        </el-table-column> -->
        <el-table-column prop="exec_type" label="生效规则" width="100" :formatter="getExecType" />
        <el-table-column prop="interval_type" label="重复类型" width="100">
          <template slot-scope="scope">
            <span v-if="scope.row.interval_type == 'year'">年</span>
            <span v-else-if="scope.row.interval_type == 'day'">日</span>
            <span v-else-if="scope.row.interval_type == 'week'">周</span>
            <span v-else-if="scope.row.interval_type == 'month'">月</span>
            <span v-else>单次</span>
          </template>
        </el-table-column>
        <el-table-column prop="execute_at" label="通知开始时间" align="left" :formatter="getStartTime" />
        <el-table-column prop="execute_util" label="通知结束时间" align="left" :formatter="getEndTime" />

        <el-table-column prop="on_completion" label="过期保留" align="center" width="100">
          <template slot-scope="scope" width="100">
            <span v-if="scope.row.on_completion == 'Y'">保留</span>
            <span v-else class="brownColor">不保留</span>
          </template>
        </el-table-column>
        <el-table-column prop="is_enable" label="启用" align="center" width="100">
          <template slot-scope="scope">
            <span v-if="scope.row.isEnable == false" class="brownColor">不启用</span>
            <span v-else>启用</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" :show-overflow-tooltip="true" />
        <el-table-column label="操作" align="center" width="240" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-view" @click="handlelook(scope.row)">查看</el-button>
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <pagination v-show="totalCount > 0&&dataList.length" :page-sizes="[10, 20, 50, 200]" :total="totalCount"
        :page.sync="queryParams.start" :limit.sync="queryParams.limit"
        @pagination="getEventNotificationConfigurations()" />
    </div>
    <!-- 添加或修改菜单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1000px" :before-close="resetForm" class="menuDialog">
      <el-menu v-model="activeIndex" :default-active="activeIndex" class="el-menu-vertical-demo" mode="horizontal"
        @select="IsActive">
        <el-menu-item index="0">
          <span slot="title">基本信息</span>
        </el-menu-item>
        <el-menu-item index="1">
          <span slot="title">规则</span>
        </el-menu-item>
      </el-menu>
      <el-row style="height:95%">
        <el-col :span="24" style="padding:10px">
          <el-form ref="ruleForm" :model="ruleForm" :rules="rules" label-width="90px" class="demo-ruleForm">
            <el-row>
              <!-- 基本信息 -->
              <el-col v-if="activeIndex == '0'">
                <el-form-item label="配置名称" prop="name">
                  <el-input v-model.trim="ruleForm.name" placeholder="请输入配置名称" :disabled="disable" />
                </el-form-item>
                <el-form-item label="生效规则" prop="exec_type">
                  <el-radio-group v-model="ruleForm.exec_type" :disabled="disable" @change="changeExecType">
                    <el-radio label="O" value="O">单次</el-radio>
                    <el-radio label="R" value="R">重复</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item v-show="once" label="通知时段" prop="timeDescription">
                  <el-date-picker v-model="ruleForm.timeDescription" value-format="yyyy-MM-dd HH:mm:ss"
                    :disabled="disable" type="datetimerange" range-separator="至" start-placeholder="开始日期"
                    end-placeholder="结束日期" size="small" style="width:100%"/>
                </el-form-item>
                <el-form-item v-show="twice" label="" prop="interval_type">
                  <el-radio-group v-model="ruleForm.interval_type" :disabled="disable" @change="changeIntervalType">
                    <el-radio v-for="item in radios" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item v-show="day" label="" prop="executeDay">
                  <el-time-picker v-model="ruleForm.execute_at" class="date-box" format="HH:mm:ss" :disabled="disable"
                    placeholder="开始时间" value-format="HH:mm:ss" />
                  <el-time-picker v-model="ruleForm.execute_util" class="date-box" placeholder="结束时间" format="HH:mm:ss"
                    :disabled="disable" value-format="HH:mm:ss" />
                </el-form-item>
                <el-form-item v-show="week" label="">
                  <el-row style="height: 40px">
                    <el-col :span="6">
                      <el-select v-model="ruleForm.day_of_week_at" placeholder="请选择周" size="small" :disabled="disable"
                        class="el-form-item__label">
                        <el-option v-for="item in weeks" :key="item.id" :label="item.week" :value="item.id" />
                      </el-select>
                    </el-col>
                    <el-col :span="6">
                      <el-time-picker v-model="ruleForm.execute_at" format="HH:mm:ss" :disabled="disable"
                        placeholder="开始时间" value-format="HH:mm:ss" />
                    </el-col>
                  </el-row>
                  <el-row style="height: 40px">
                    <el-col :span="6">
                      <el-select v-model="ruleForm.day_of_week_util" placeholder="请选择周" :disabled="disable" size="small"
                        class="el-form-item__label">
                        <el-option v-for="item in weeks" :key="item.id" :label="item.week" :value="item.id" />
                      </el-select>
                    </el-col>
                    <el-col :span="6">
                      <el-time-picker v-model="ruleForm.execute_util" :disabled="disable" format="HH:mm:ss"
                        placeholder="结束时间" value-format="HH:mm:ss" />
                    </el-col>
                  </el-row>
                </el-form-item>
                <el-form-item v-show="month" label="" prop="executeWeek">
                  <el-row style="height: 40px">
                    <el-col :span="6">
                      <el-select v-model="ruleForm.day_start" placeholder="请选择天" size="small"
                        class="el-form-item__label" :disabled="disable">
                        <el-option v-for="item in months" :key="item.id" :label="item.day" :value="item.id" />
                      </el-select>
                    </el-col>
                    <el-col :span="6">
                      <el-time-picker v-model="ruleForm.execute_at" class="date-box" size="small" format="HH:mm:ss"
                        placeholder="结束时间" value-format="HH:mm:ss" :disabled="disable" />
                    </el-col>
                    <el-col :span="6">
                      <el-select v-model="ruleForm.day_end" placeholder="请选择天" size="small" class="el-form-item__label"
                        :disabled="disable">
                        <el-option v-for="item in months" :key="item.id" :label="item.day" :value="item.id" />
                      </el-select>
                    </el-col>
                    <el-col :span="6">
                      <el-time-picker v-model="ruleForm.execute_util" class="date-box" size="small" placeholder="结束时间"
                        format="HH:mm:ss" value-format="HH:mm:ss" :disabled="disable" />
                    </el-col>
                  </el-row>
                </el-form-item>
                <el-form-item v-show="year" label="" prop="executeWeek">
                  <el-row style="height: 40px">
                    <el-col>
                      <el-select v-model="ruleForm.month_start" :disabled="disable" placeholder="请选择月" size="small"
                        class="el-form-item__label" style="width: 30%">
                        <el-option v-for="item in years" :key="item.id" :label="item.month" :value="item.id" />
                      </el-select>

                      <el-select v-model="ruleForm.day_start" :disabled="disable" placeholder="请选择天" size="small"
                        class="el-form-item__label" style="width: 30%">
                        <el-option v-for="item in months" :key="item.id" :label="item.day" :value="item.id" />
                      </el-select>

                      <el-time-picker v-model="ruleForm.execute_at" :disabled="disable" class="date-box"
                        format="HH:mm:ss" size="small" placeholder="开始时间" style="width: 30%" value-format="HH:mm:ss" />
                    </el-col>

                    <el-col>
                      <el-select v-model="ruleForm.month_end" :disabled="disable" placeholder="请选择月" size="small"
                        class="el-form-item__label" style="width: 30%">
                        <el-option v-for="item in years" :key="item.id" :label="item.month" :value="item.id" />
                      </el-select>

                      <el-select v-model="ruleForm.day_end" :disabled="disable" placeholder="请选择天" size="small"
                        class="el-form-item__label" style="width: 30%">
                        <el-option v-for="item in months" :key="item.id" :label="item.day" :value="item.id" />
                      </el-select>

                      <el-time-picker v-model="ruleForm.execute_util" :disabled="disable" class="date-box" size="small"
                        placeholder="结束时间" format="HH:mm:ss" value-format="HH:mm:ss" style="width: 30%" />
                    </el-col>
                  </el-row>
                </el-form-item>
                <el-form-item label="过期保留" prop="on_completion">
                  <el-radio-group v-model="ruleForm.on_completion" :disabled="disable">
                    <el-radio label="Y">保留</el-radio>
                    <el-radio label="N">不保留</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="启用" prop="is_enable">
                  <!-- <el-switch :disabled="disable" v-model="ruleForm.is_enable"></el-switch> -->
                  <el-radio-group v-model="ruleForm.is_enable" :disabled="disable">
                    <el-radio :label="true">启用</el-radio>
                    <el-radio :label="false">不启用</el-radio>
                  </el-radio-group>
                </el-form-item>

                <el-form-item label="备注" prop="remark">
                  <el-input v-model.trim="ruleForm.remark" type="textarea" :disabled="disable" placeholder="请输入备注" />
                </el-form-item>
              </el-col>
              <!-- 规则 -->
              <el-col v-if="activeIndex == '1'">
                <el-form-item label="运算公式" prop="formular">
                  <el-input v-model.trim="ruleForm.formular" :disabled="true" clearable />
                </el-form-item>
                <el-form-item label="条件字段" prop="expression" style="overflow-x:hidden">
                  <el-table v-if="ruleForm.expression.length" :show-header="false" :data="ruleForm.expression">
                    <el-table-column type="expand">
                      <template slot-scope="props">
                        <el-form label-position="left" label-width="100px" inline class="demo-table-expand">
                          <div>
                            <el-form-item label="字段名">
                              <!-- <span>{{ props.row.conditionColumn }}</span> -->
                              <el-select v-model="props.row.conditionColumn" placeholder="请选择条件字段" clearable
                                size="small" :disabled="disable" @change="
                                  columnChange(props.row)
                                  setFormular(props.row.conditionColumn)
                                ">
                                <el-option v-for="item in effectExper" :key="item.columnInDB" :label="item.columnName"
                                  :value="item.columnInDB" />
                              </el-select>
                            </el-form-item>
                          </div>
                          <div>
                            <el-form-item label="操作">
                              <el-select
                                v-if="(props.row.conditionDataType == 'INT' || props.row.conditionDataType == 'FLOAT') && props.row.conditionColumn == 'RecoveredStatus'"
                                v-model="props.row.operator" :disabled="disable" clearable size="small"
                                placeholder="运算符">
                                <el-option v-for="operator in operatorsIntRecoveredStatus" :key="operator.opertorChar"
                                  :label="operator.opertorName" :value="operator.opertorChar" />
                              </el-select>
                              <el-select
                                v-else-if="(props.row.conditionDataType == 'INT' || props.row.conditionDataType == 'FLOAT') && props.row.conditionColumn == 'isComponent'"
                                v-model="props.row.operator" :disabled="disable" clearable size="small"
                                placeholder="运算符">
                                <el-option v-for="operator in operatorsIntIsComponent" :key="operator.opertorChar"
                                  :label="operator.opertorName" :value="operator.opertorChar" />
                              </el-select>
                              <el-select
                                v-else-if="(props.row.conditionDataType == 'INT' || props.row.conditionDataType == 'FLOAT') && props.row.conditionColumn == 'Acknowledged'"
                                v-model="props.row.operator" :disabled="disable" clearable size="small"
                                placeholder="运算符">
                                <el-option v-for="operator in operatorsIntAcknowledged" :key="operator.opertorChar"
                                  :label="operator.opertorName" :value="operator.opertorChar" />
                              </el-select>
                              <el-select
                                v-else-if="props.row.conditionDataType == 'INT' || props.row.conditionDataType == 'FLOAT'"
                                v-model="props.row.operator" :disabled="disable" clearable size="small"
                                placeholder="运算符">
                                <el-option v-for="operator in operatorsInt" :key="operator.opertorChar"
                                  :label="operator.opertorName" :value="operator.opertorChar" />
                              </el-select>
                              <el-select v-else-if="props.row.conditionDataType == 'LONGTIMESTAMP'"
                                v-model="props.row.operator" :disabled="disable" clearable size="small"
                                placeholder="运算符">
                                <el-option v-for="operator in operatorsTime" :key="operator.opertorChar"
                                  :label="operator.opertorName" :value="operator.opertorChar" />
                              </el-select>
                              <el-select
                                v-else-if="props.row.conditionDataType == 'STRING' && props.row.conditionColumn == 'Node'"
                                v-model="props.row.operator" clearable size="small" placeholder="运算符">
                                <el-option v-for="operator in operatorsStrNode" :key="operator.opertorChar"
                                  :label="operator.opertorName" :value="operator.opertorChar" />
                              </el-select>
                              <el-select
                                v-else-if="props.row.conditionDataType == 'STRING' && props.row.conditionColumn == 'EventCategory'"
                                v-model="props.row.operator" clearable size="small" placeholder="运算符">
                                <el-option v-for="operator in operatorsStrEventCategory" :key="operator.opertorChar"
                                  :label="operator.opertorName" :value="operator.opertorChar" />
                              </el-select>
                              <el-select
                                v-else-if="props.row.conditionDataType == 'STRING' && props.row.conditionColumn == 'Agent'"
                                v-model="props.row.operator" clearable size="small" placeholder="运算符">
                                <el-option v-for="operator in operatorsStrAgent" :key="operator.opertorChar"
                                  :label="operator.opertorName" :value="operator.opertorChar" />
                              </el-select>
                              <el-select
                                v-else-if="props.row.conditionDataType == 'STRING' && props.row.conditionColumn == 'Manager'"
                                v-model="props.row.operator" clearable size="small" placeholder="运算符">
                                <el-option v-for="operator in operatorsStrManager" :key="operator.opertorChar"
                                  :label="operator.opertorName" :value="operator.opertorChar" />
                              </el-select>
                              <el-select v-else v-model="props.row.operator" :disabled="disable" clearable size="small"
                                placeholder="运算符">
                                <el-option v-for="operator in operatorsStr" :key="operator.opertorChar"
                                  :label="operator.opertorName" :value="operator.opertorChar" />
                              </el-select>
                            </el-form-item>
                          </div>
                          <div>
                            <el-form-item label="值" class="labelSP">
                              <el-date-picker v-if="props.row.conditionDataType == 'LONGTIMESTAMP'"
                                v-model="props.row.conditionValue[0]" :disabled="disable" value-format="timestamp"
                                size="small" type="datetimerange" range-separator="-" start-placeholder="开始日期"
                                end-placeholder="结束日期" clearable class="specialDateClass" />
                              <el-select v-else-if="props.row.conditionColumn == 'EventCategory'"
                                v-model="props.row.conditionValue[0]" :disabled="disable" size="small" :multiple="false"
                                placeholder="请选择事件分组" clearable>
                                <el-option v-for="item in resource_types" :key="item.name" :label="item.label_text"
                                  :value="item.name" />
                              </el-select>
                              <el-select v-else-if="props.row.conditionColumn == 'RecoveredStatus'"
                                v-model="props.row.conditionValue[0]" :disabled="disable" size="small" :multiple="false"
                                placeholder="请选择恢复状态" clearable>
                                <el-option v-for="SItem in recoveredStatusArr" :key="SItem.mappingValue"
                                  :value="SItem.mappingValue" :label="SItem.name" />
                              </el-select>
                              <el-select v-else-if="props.row.conditionColumn == 'isComponent'"
                                v-model="props.row.conditionValue[0]" :disabled="disable" size="small" :multiple="false"
                                placeholder="请选择事件类型" clearable>
                                <el-option v-for="SItem in isComponentArr" :key="SItem.mappingValue"
                                  :value="SItem.mappingValue" :label="SItem.name" />
                              </el-select>
                              <el-select v-else-if="props.row.conditionColumn == 'Acknowledged'"
                                v-model="props.row.conditionValue[0]" :disabled="disable" size="small" :multiple="false"
                                placeholder="请选择是否确认" clearable>
                                <el-option v-for="SItem in acknowledgedArr" :key="SItem.mappingValue"
                                  :value="SItem.mappingValue" :label="SItem.name" />
                              </el-select>
                              <el-select v-else-if="props.row.conditionColumn == 'Agent'"
                                v-model="props.row.conditionValue[0]" :disabled="disable" size="small" :multiple="false"
                                placeholder="请选择Probe" clearable>
                                <el-option v-for="SItem in eventSources" :key="SItem.probe_key" :value="SItem.probe_key"
                                  :label="SItem.name" />
                              </el-select>
                              <el-select v-else-if="props.row.conditionColumn == 'Manager'"
                                v-model="props.row.conditionValue[0]" :disabled="disable" size="small" :multiple="false"
                                placeholder="请选择Probe" clearable>
                                <el-option v-for="SItem in manager_types" :key="SItem.Manager" :value="SItem.Manager"
                                  :label="SItem.Manager" />
                              </el-select>
                              <el-input-number
                                v-else-if="(props.row.conditionDataType == 'INT' || props.row.conditionDataType == 'FLOAT') && props.row.operator != 'between' && props.row.conditionColumn != 'Severity' && props.row.conditionColumn != 'OldSeverity'"
                                v-model="props.row.conditionValue[0]" :disabled="disable" size="small"
                                placeholder="请输入内容" clearable style="width:100%" controls-position="right" :min="1" />
                              <el-select
                                v-else-if="(props.row.conditionDataType == 'INT' || props.row.conditionDataType == 'FLOAT') && props.row.operator != 'between' && (props.row.conditionColumn == 'Severity' || props.row.conditionColumn == 'OldSeverity') && props.row.operator !== 'in'"
                                v-model="props.row.conditionValue[0]" :disabled="disable" size="small" :multiple="false"
                                placeholder="请选择级别内容" clearable>
                                <el-option v-for="SItem in SeverityArr" :key="SItem.mappingValue"
                                  :value="SItem.mappingValue" :label="SItem.name" />
                              </el-select>
                              <el-select
                                v-else-if="(props.row.conditionDataType == 'INT' || props.row.conditionDataType == 'FLOAT') && props.row.operator != 'between' && (props.row.conditionColumn == 'Severity' || props.row.conditionColumn == 'OldSeverity') && props.row.operator == 'in'"
                                v-model="props.row.conditionValue" :disabled="disable" size="small" :multiple="true"
                                placeholder="请选择级别内容" clearable>
                                <el-option v-for="SItem in SeverityArr" :key="SItem.mappingValue"
                                  :value="SItem.mappingValue" :label="SItem.name" />
                              </el-select>
                              <el-row
                                v-else-if="(props.row.conditionDataType == 'INT' || props.row.conditionDataType == 'FLOAT') && props.row.operator == 'between' && (props.row.conditionColumn == 'Severity' || props.row.conditionColumn == 'OldSeverity')"
                                :gutter="4">
                                <el-col :span="11">
                                  <el-select v-model="props.row.conditionValue[0]" :disabled="disable" size="small"
                                    placeholder="级别内容" clearable>
                                    <el-option v-for="SItem in SeverityArr" :key="SItem.mappingValue"
                                      :value="SItem.mappingValue" :label="SItem.name" />
                                  </el-select>
                                </el-col>
                                <el-col :span="2" style="text-align: center; line-height: 2rem">
                                  <span>-</span>
                                </el-col>
                                <el-col :span="11">
                                  <el-select v-model="props.row.conditionValue[1]" :disabled="disable" size="small"
                                    placeholder="级别内容" clearable>
                                    <el-option v-for="SItem in SeverityArr" :key="SItem.mappingValue"
                                      :value="SItem.mappingValue" :label="SItem.name" />
                                  </el-select>
                                </el-col>
                              </el-row>
                              <el-row
                                v-else-if="(props.row.conditionDataType == 'INT' || props.row.conditionDataType == 'FLOAT') && props.row.operator == 'between' && (props.row.conditionColumn != 'Severity' || props.row.conditionColumn != 'OldSeverity')"
                                :gutter="4">
                                <el-col :span="11">
                                  <el-input-number v-model="props.row.conditionValue[0]" :disabled="disable"
                                    size="small" placeholder="最小值" clearable style="width:100%"
                                    controls-position="right" :min="1" />
                                </el-col>
                                <el-col :span="2" style="text-align: center; line-height: 2rem">
                                  <span>-</span>
                                </el-col>
                                <el-col :span="11">
                                  <el-input-number v-model="props.row.conditionValue[1]" :disabled="disable"
                                    size="small" placeholder="最大值" clearable style="width:100%"
                                    controls-position="right" :min="1" />
                                </el-col>
                              </el-row>
                              <el-input v-else v-model.trim="props.row.conditionValue[0]" style="width:100%"
                                type="textarea" :disabled="disable" placeholder="默认值"
                                :value="props.row.conditionValue[0]" />
                            </el-form-item>
                          </div>
                          <div>
                            <el-form-item label="连接符">
                              <!-- <span>{{ props.row.name }}</span> -->
                              <el-select v-model="props.row.operatorLogic" placeholder="请继续" clearable size="small"
                                style="width: 150px" :disabled="disable">
                                <el-option v-for="item in operatorLogics" :key="item.opertorLogicChar"
                                  :label="item.opertorLogicName" :value="item.opertorLogicChar" />
                              </el-select>
                            </el-form-item>
                          </div>
                        </el-form>
                      </template>
                    </el-table-column>
                    <el-table-column label="值">
                      <template slot-scope="scope">
                        <el-select v-model="scope.row.conditionColumn" placeholder="请选择条件字段" clearable size="small"
                          :disabled="disable" @change="
                            columnChange(scope.row)
                            setFormular(scope.row.conditionColumn)
                          ">
                          <el-option v-for="item in effectExper" :key="item.columnInDB" :label="item.columnName"
                            :value="item.columnInDB" />
                        </el-select>
                      </template>
                    </el-table-column>
                    <el-table-column label="操作">
                      <template slot-scope="scope">
                        <el-select
                          v-if="(scope.row.conditionDataType == 'INT' || scope.row.conditionDataType == 'FLOAT') && scope.row.conditionColumn == 'RecoveredStatus'"
                          v-model="scope.row.operator" :disabled="disable" clearable size="small" placeholder="运算符">
                          <el-option v-for="operator in operatorsIntRecoveredStatus" :key="operator.opertorChar"
                            :label="operator.opertorName" :value="operator.opertorChar" />
                        </el-select>
                        <el-select
                          v-else-if="(scope.row.conditionDataType == 'INT' || scope.row.conditionDataType == 'FLOAT') && scope.row.conditionColumn == 'isComponent'"
                          v-model="scope.row.operator" :disabled="disable" clearable size="small" placeholder="运算符">
                          <el-option v-for="operator in operatorsIntIsComponent" :key="operator.opertorChar"
                            :label="operator.opertorName" :value="operator.opertorChar" />
                        </el-select>
                        <el-select
                          v-else-if="(scope.row.conditionDataType == 'INT' || scope.row.conditionDataType == 'FLOAT') && scope.row.conditionColumn == 'Acknowledged'"
                          v-model="scope.row.operator" :disabled="disable" clearable size="small" placeholder="运算符">
                          <el-option v-for="operator in operatorsIntAcknowledged" :key="operator.opertorChar"
                            :label="operator.opertorName" :value="operator.opertorChar" />
                        </el-select>
                        <el-select
                          v-else-if="scope.row.conditionDataType == 'INT' || scope.row.conditionDataType == 'FLOAT'"
                          v-model="scope.row.operator" :disabled="disable" clearable size="small" placeholder="运算符">
                          <el-option v-for="operator in operatorsInt" :key="operator.opertorChar"
                            :label="operator.opertorName" :value="operator.opertorChar" />
                        </el-select>
                        <el-select v-else-if="scope.row.conditionDataType == 'LONGTIMESTAMP'"
                          v-model="scope.row.operator" :disabled="disable" clearable size="small" placeholder="运算符">
                          <el-option v-for="operator in operatorsTime" :key="operator.opertorChar"
                            :label="operator.opertorName" :value="operator.opertorChar" />
                        </el-select>
                        <el-select
                          v-else-if="scope.row.conditionDataType == 'STRING' && scope.row.conditionColumn == 'Node'"
                          v-model="scope.row.operator" clearable size="small" placeholder="运算符">
                          <el-option v-for="operator in operatorsStrNode" :key="operator.opertorChar"
                            :label="operator.opertorName" :value="operator.opertorChar" />
                        </el-select>
                        <el-select
                          v-else-if="scope.row.conditionDataType == 'STRING' && scope.row.conditionColumn == 'EventCategory'"
                          v-model="scope.row.operator" clearable size="small" placeholder="运算符">
                          <el-option v-for="operator in operatorsStrEventCategory" :key="operator.opertorChar"
                            :label="operator.opertorName" :value="operator.opertorChar" />
                        </el-select>
                        <el-select
                          v-else-if="scope.row.conditionDataType == 'STRING' && scope.row.conditionColumn == 'Agent'"
                          v-model="scope.row.operator" clearable size="small" placeholder="运算符">
                          <el-option v-for="operator in operatorsStrAgent" :key="operator.opertorChar"
                            :label="operator.opertorName" :value="operator.opertorChar" />
                        </el-select>
                        <el-select
                          v-else-if="scope.row.conditionDataType == 'STRING' && scope.row.conditionColumn == 'Manager'"
                          v-model="scope.row.operator" clearable size="small" placeholder="运算符">
                          <el-option v-for="operator in operatorsStrManager" :key="operator.opertorChar"
                            :label="operator.opertorName" :value="operator.opertorChar" />
                        </el-select>
                        <el-select v-else v-model="scope.row.operator" :disabled="disable" clearable size="small"
                          placeholder="运算符">
                          <el-option v-for="operator in operatorsStr" :key="operator.opertorChar"
                            :label="operator.opertorName" :value="operator.opertorChar" />
                        </el-select>
                      </template>
                    </el-table-column>
                    <el-table-column label="值">
                      <template slot-scope="scope">
                        <el-date-picker v-if="scope.row.conditionDataType == 'LONGTIMESTAMP'"
                          v-model="scope.row.conditionValue[0]" :disabled="disable" value-format="timestamp"
                          size="small" type="datetimerange" range-separator="-" start-placeholder="开始日期"
                          end-placeholder="结束日期" clearable class="specialDateClass" />
                        <el-select v-else-if="scope.row.conditionColumn == 'EventCategory'"
                          v-model="scope.row.conditionValue[0]" :disabled="disable" size="small" :multiple="false"
                          placeholder="请选择事件分组" clearable>
                          <el-option v-for="item in resource_types" :key="item.name" :label="item.label_text"
                            :value="item.name" />
                        </el-select>
                        <el-select v-else-if="scope.row.conditionColumn == 'RecoveredStatus'"
                          v-model="scope.row.conditionValue[0]" :disabled="disable" size="small" :multiple="false"
                          placeholder="请选择恢复状态" clearable>
                          <el-option v-for="SItem in recoveredStatusArr" :key="SItem.mappingValue"
                            :value="SItem.mappingValue" :label="SItem.name" />
                        </el-select>
                        <el-select v-else-if="scope.row.conditionColumn == 'isComponent'"
                          v-model="scope.row.conditionValue[0]" :disabled="disable" size="small" :multiple="false"
                          placeholder="请选择事件类型" clearable>
                          <el-option v-for="SItem in isComponentArr" :key="SItem.mappingValue"
                            :value="SItem.mappingValue" :label="SItem.name" />
                        </el-select>
                        <el-select v-else-if="scope.row.conditionColumn == 'Acknowledged'"
                          v-model="scope.row.conditionValue[0]" :disabled="disable" size="small" :multiple="false"
                          placeholder="请选择是否确认" clearable>
                          <el-option v-for="SItem in acknowledgedArr" :key="SItem.mappingValue"
                            :value="SItem.mappingValue" :label="SItem.name" />
                        </el-select>
                        <el-select v-else-if="scope.row.conditionColumn == 'Agent'"
                          v-model="scope.row.conditionValue[0]" :disabled="disable" size="small" :multiple="false"
                          placeholder="请选择Probe" clearable>
                          <el-option v-for="SItem in eventSources" :key="SItem.probe_key" :value="SItem.probe_key"
                            :label="SItem.name" />
                        </el-select>
                        <el-select v-else-if="scope.row.conditionColumn == 'Manager'"
                          v-model="scope.row.conditionValue[0]" :disabled="disable" size="small" :multiple="false"
                          placeholder="请选择Probe" clearable>
                          <el-option v-for="SItem in manager_types" :key="SItem.Manager" :value="SItem.Manager"
                            :label="SItem.Manager" />
                        </el-select>
                        <el-input-number
                          v-else-if="(scope.row.conditionDataType == 'INT' || scope.row.conditionDataType == 'FLOAT') && scope.row.operator != 'between' && scope.row.conditionColumn != 'Severity' && scope.row.conditionColumn != 'OldSeverity'"
                          v-model="scope.row.conditionValue[0]" :disabled="disable" size="small" placeholder="请输入内容"
                          clearable style="width:100%" controls-position="right" :min="1" />
                        <el-select
                          v-else-if="(scope.row.conditionDataType == 'INT' || scope.row.conditionDataType == 'FLOAT') && scope.row.operator != 'between' && (scope.row.conditionColumn == 'Severity' || scope.row.conditionColumn == 'OldSeverity') && scope.row.operator !== 'in'"
                          v-model="scope.row.conditionValue[0]" :disabled="disable" size="small" :multiple="false"
                          placeholder="请选择级别内容" clearable>
                          <el-option v-for="SItem in SeverityArr" :key="SItem.mappingValue" :value="SItem.mappingValue"
                            :label="SItem.name" />
                        </el-select>
                        <el-select
                          v-else-if="(scope.row.conditionDataType == 'INT' || scope.row.conditionDataType == 'FLOAT') && scope.row.operator != 'between' && (scope.row.conditionColumn == 'Severity' || scope.row.conditionColumn == 'OldSeverity') && scope.row.operator == 'in'"
                          v-model="scope.row.conditionValue" :disabled="disable" size="small" :multiple="true"
                          placeholder="请选择级别内容" clearable>
                          <el-option v-for="SItem in SeverityArr" :key="SItem.mappingValue" :value="SItem.mappingValue"
                            :label="SItem.name" />
                        </el-select>
                        <el-row
                          v-else-if="(scope.row.conditionDataType == 'INT' || scope.row.conditionDataType == 'FLOAT') && scope.row.operator == 'between' && (scope.row.conditionColumn == 'Severity' || scope.row.conditionColumn == 'OldSeverity')"
                          :gutter="4">
                          <el-col :span="11">
                            <el-select v-model="scope.row.conditionValue[0]" :disabled="disable" size="small"
                              placeholder="级别内容" clearable>
                              <el-option v-for="SItem in SeverityArr" :key="SItem.mappingValue"
                                :value="SItem.mappingValue" :label="SItem.name" />
                            </el-select>
                          </el-col>
                          <el-col :span="2" style="text-align: center; line-height: 2rem">
                            <span>-</span>
                          </el-col>
                          <el-col :span="11">
                            <el-select v-model="scope.row.conditionValue[1]" :disabled="disable" size="small"
                              placeholder="级别内容" clearable>
                              <el-option v-for="SItem in SeverityArr" :key="SItem.mappingValue"
                                :value="SItem.mappingValue" :label="SItem.name" />
                            </el-select>
                          </el-col>
                        </el-row>
                        <el-row
                          v-else-if="(scope.row.conditionDataType == 'INT' || scope.row.conditionDataType == 'FLOAT') && scope.row.operator == 'between' && (scope.row.conditionColumn != 'Severity' || scope.row.conditionColumn != 'OldSeverity')"
                          :gutter="4">
                          <el-col :span="11">
                            <el-input-number v-model="scope.row.conditionValue[0]" :disabled="disable" size="small"
                              placeholder="最小值" clearable style="width:100%" controls-position="right" :min="1" />
                          </el-col>
                          <el-col :span="2" style="text-align: center; line-height: 2rem">
                            <span>-</span>
                          </el-col>
                          <el-col :span="11">
                            <el-input-number v-model="scope.row.conditionValue[1]" :disabled="disable" size="small"
                              placeholder="最大值" clearable style="width:100%" controls-position="right" :min="1" />
                          </el-col>
                        </el-row>
                        <el-input v-else v-model.trim="scope.row.conditionValue[0]" :disabled="disable"
                          placeholder="默认值" :value="scope.row.conditionValue[0]" />
                      </template>
                    </el-table-column>
                    <el-table-column label="逻辑操作">
                      <template slot-scope="scope">
                        <el-select v-model="scope.row.operatorLogic" placeholder="请继续" clearable size="small"
                          :disabled="disable">
                          <el-option v-for="item in operatorLogics" :key="item.opertorLogicChar"
                            :label="item.opertorLogicName" :value="item.opertorLogicChar" />
                        </el-select>
                      </template>
                    </el-table-column>
                    <el-table-column label="操作" prop="name" class-name="small-padding fixed-width">
                      <template slot-scope="scope">
                        <el-button v-if="ruleForm.expression.length != 1" size="mini" type="text" :disabled="disable" class="el-icon-delete" 
                          @click="fieldExpreDelete(scope)">删除</el-button>
                        <el-button
                          v-if="scope.$index + 1 == ruleForm.expression.length || scope.$index == ruleForm.expression.length" class="el-icon-circle-plus-outline"
                          size="mini" type="text" :disabled="disable" @click="addExpreClick()">新增</el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                </el-form-item>
                <el-form-item label="通知方式" prop="effect[0].effectType">
                  <!-- {{ radiosInforme }} -->
                  <el-checkbox-group v-model="ruleForm.effect[0].effectType">
                    <el-checkbox label="email" :disabled="disable">邮件</el-checkbox>
                    <!-- <el-checkbox label="flow" :disabled="disable">工单</el-checkbox>
                    <el-checkbox label="Wechat" :disabled="disable">融讯通</el-checkbox> -->
                    <!-- <el-checkbox v-for="item in radiosInforme" :key="item.value" :checked="item.isChecked" :disabled="disable" :label="item.value">{{ item.label }}</el-checkbox> -->
                  </el-checkbox-group>
                </el-form-item>
                <el-form-item label="通知对象" prop="effect[0].effectValue">
                  <el-input v-model.trim="ruleForm.effect[0].effectValue" :disabled="disable" clearable
                    type="textarea" />
                  <span style="font-size:12px">提示:通知对象请填写邮件地址，通知对象有多个时,用#分割</span>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <!-- <el-button class="cancel_button" @click="resetForm()">取 消</el-button> -->
        <el-button v-if="!disable" class="cancel_button" @click="resetForm('ruleForm')">取 消</el-button>
        <el-button v-else class="cancel_button" @click="resetForm('ruleForm')">关 闭</el-button>
        <el-button v-if="title != '查看'" v-show="showBu" type="primary" :disabled="disable" class="confirm_button"
          @click="submitForm()">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { addHandlerRulesDefine, updateHandlerRulesDefine, getHandlerRulesDefine, deleteHandlerRulesDefine, getEventStrategyById, getManagerSelect, getProbeSourceType, getPhysicalCategory1 } from '@/api/cm/eventStrategy'
import { getEventSeverityDefine, getEventSeverityDefineAll } from '@/api/publicResources/eventLevel'
import moment from 'moment'
import { getEventColumnDefine } from '@/api/publicResources/eventField'
// 省缺组件
import Deficiency from '@/components/Deficiency/index'
export default {
  components: { Deficiency },
  data() {
    return {
      resource_types: '',
      eventSources: '',
      isComponentArr: [{ mappingValue: 0, name: '组件告警' }, { mappingValue: 1, name: '设备告警' }],
      acknowledgedArr: [{ mappingValue: 0, name: '未确认' }, { mappingValue: 1, name: '已确认' }],
      recoveredStatusArr: [{ mappingValue: 1, name: '未恢复' }, { mappingValue: 2, name: '已恢复' }],
      operatorsIntRecoveredStatus: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }],
      operatorsIntIsComponent: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }],
      opsIntAcknowledged: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }],
      operatorsStrEventCategory: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }],
      operatorsStrAgent: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }],
      operatorsStrManager: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }],
      manager_types: [],
      // TIMESTAMP运算符
      operatorsTime: [{ opertorName: '在  和  之间', opertorChar: 'between' }],
      // 数字
      operatorsInt: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }, { opertorName: '小于', opertorChar: '<' }, { opertorName: '小于等于', opertorChar: '<=' }, { opertorName: '大于', opertorChar: '>' }, { opertorName: '大于等于', opertorChar: '>=' }, { opertorName: '包含', opertorChar: 'in' }, { opertorName: '在  和  之间', opertorChar: 'between' }, { opertorName: '正则', opertorChar: 'regex' }],
      // STRING运算符
      operatorsStr: [
        { opertorName: '等于', opertorChar: '=' },
        { opertorName: '不等于', opertorChar: '!=' },
        /* { opertorName: "包含", opertorChar: "in" },*/
        { opertorName: '相似', opertorChar: 'like' },
        { opertorName: '正则', opertorChar: 'regex' }
      ],
      // 带ip段的STRING运算符
      operatorsStrNode: [
        { opertorName: '等于', opertorChar: '=' },
        { opertorName: '不等于', opertorChar: '!=' },
        { opertorName: 'IP段', opertorChar: 'ip' },
        /* { opertorName: "包含", opertorChar: "in" },*/
        { opertorName: '相似', opertorChar: 'like' },
        { opertorName: '正则', opertorChar: 'regex' }
      ],
      // 获取表格页面高度
      OperateBoxHeight: 400,
      // 弹出弹窗title显示
      title: '新增',
      content: '',
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
      // 弹框切换下标
      activeIndex: '0',
      totalCount: 0,
      queryParams: {
        start: 1,
        limit: 20
      },
      formInline: {
        name: ''
      },
      // 新增、修改弹框
      open: false,
      show: false,
      dataList: [],
      expression: [
        {
          conditionColumn: '',
          conditionDataType: '',
          operator: '',
          conditionValue: ''
        }
      ],
      eventField: [
        {
          eventFields: '',
          operators: '',
          resultValues: '',
          logicalOperations: ''
        }
      ],
      saveOrUpdate: 'add',
      serverityNames: '',
      resourceNames: '',
      roleDatas: '',
      userDatas: '',
      disable: false,
      showBu: true,
      once: true,
      twice: false,
      day: false,
      week: false,
      month: false,
      year: false,

      operators: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }, { opertorName: '小于', opertorChar: '<' }, { opertorName: '小于等于', opertorChar: '<=' }, { opertorName: '大于', opertorChar: '>' }, { opertorName: '大于等于', opertorChar: '>=' }, { opertorName: '包含', opertorChar: 'in' }, { opertorName: '相似', opertorChar: 'like' }, { opertorName: '在  和  之间', opertorChar: 'between' }, { opertorName: '正则', opertorChar: 'regex' }],
      operatorLogics: [{ opertorLogicName: '与', opertorLogicChar: '&&' }, { opertorLogicName: '或', opertorLogicChar: '||' }],
      radiosInforme: [
        {
          label: '邮箱',
          value: 'email',
          isChecked: true
        },
        {
          label: '工单',
          value: 'flow',
          isChecked: false
        },
        {
          label: '融讯通',
          value: 'Wechat',
          isChecked: false
        }
      ],
      radios: [
        {
          label: '日',
          value: 'day',
          isChecked: true
        },
        {
          label: '周',
          value: 'week',
          isChecked: false
        },
        {
          label: '月',
          value: 'month',
          isChecked: false
        },
        {
          label: '年',
          value: 'year',
          isChecked: false
        }
      ],
      years: [{ month: '1 月', id: '01' }, { month: '2 月', id: '02' }, { month: '3 月', id: '03' }, { month: '4 月', id: '04' }, { month: '5 月', id: '05' }, { month: '6 月', id: '06' }, { month: '7 月', id: '07' }, { month: '8 月', id: '08' }, { month: '9 月', id: '09' }, { month: '10 月', id: '10' }, { month: '11 月', id: '11' }, { month: '12 月', id: '12' }],
      months: [
        { day: '1 日', id: '01' },
        { day: '2 日', id: '02' },
        { day: '3 日', id: '03' },
        { day: '4 日', id: '04' },
        { day: '5 日', id: '05' },
        { day: '6 日', id: '06' },
        { day: '7 日', id: '07' },
        { day: '8 日', id: '08' },
        { day: '9 日', id: '09' },
        { day: '10 日', id: '10' },
        { day: '11 日', id: '11' },
        { day: '12 日', id: '12' },
        { day: '13 日', id: '13' },
        { day: '14 日', id: '14' },
        { day: '15 日', id: '15' },
        { day: '16 日', id: '16' },
        { day: '17 日', id: '17' },
        { day: '18 日', id: '18' },
        { day: '19 日', id: '19' },
        { day: '20 日', id: '20' },
        { day: '21 日', id: '21' },
        { day: '22 日', id: '22' },
        { day: '23 日', id: '23' },
        { day: '24 日', id: '24' },
        { day: '25 日', id: '25' },
        { day: '26 日', id: '26' },
        { day: '27 日', id: '27' },
        { day: '28 日', id: '28' },
        { day: '29 日', id: '29' },
        { day: '30 日', id: '30' }
      ],
      weeks: [{ week: '周一', id: '1' }, { week: '周二', id: '2' }, { week: '周三', id: '3' }, { week: '周四', id: '4' }, { week: '周五', id: '5' }, { week: '周六', id: '6' }, { week: '周日', id: '7' }],
      effectExper: [],
      formular: '',
      ruleForm: {
        name: '',
        exec_type: 'O',
        timeDescription: [moment().format('YYYY-MM-DD 00:00:00'), moment().format('YYYY-MM-DD 23:59:59')],
        execute_util: '',
        execute_at: '',
        is_enable: true,
        // event_source_type: "INTERNAL",
        rule_type: 'Z',
        event_source: '',
        on_completion: 'Y',
        day_of_week_at: '',
        day_of_week_util: '',
        interval_type: 'day',
        day_start: '',
        day_end: '',
        month_start: '',
        month_end: '',
        formular: '',
        effect: [
          {
            effectColumn: '',
            effectDataType: '',
            effectType: ['email'],
            effectValue: ''
          }
        ],
        expression: [
          {
            conditionColumn: '',
            conditionDataType: '',
            operator: '',
            conditionValue: ''
          }
        ],
        remark: ''
      },
      rules: {
        name: [{ required: true, message: '请先填写名称', trigger: 'blur' }],
        exec_type: [{ required: true, message: '请先选择生效规则', trigger: 'blur' }],
        // 'effect[0].effectType': [
        //   { required: true, message: '请选择通知方式', trigger: 'blur' }
        // ],
        is_enable: [{ required: true, message: '请选择', trigger: 'blur' }],
        on_completion: [{ required: true, message: '请选择', trigger: 'blur' }],
        formular: [{ required: true, message: '请输入运算公式', trigger: 'blur' }],
        expression: [{ required: true, message: '请输入运算公式', trigger: 'blur' }],
        method_informe: [{ required: true, message: '请选择通知方式', trigger: 'blur' }]
      }
    }
  },
  watch: {
    'ruleForm.formular': function(newvalue, oldvalue) {
      // console.log(oldvalue,newvalue)
      const arr = newvalue.split(' and ')
      // console.log(arr)
      const indexArr = []
      arr.forEach((item) => {
        this.effectExper.forEach((itemc, index) => {
          itemc.isEnable = true
          if (itemc.columnInDB === item) {
            indexArr.push(index)
          }
        })
      })
      indexArr.forEach((item) => {
        // if (this.effectExper[item].isEnable) {
        this.effectExper[item].isEnable = false
        // }
      })
    }
  },
  created() {
    if (this.$route.params.name) {
      this.formInline.name = this.$route.params.name
    }
    this.getEventSeverityDefinesAll() // 获取级别数据
    this.getProbeSourceTypes()
    this.getPhysicalCategorys()
    // this.getPermiRole(); //获取数据权限
    this.getEventColumnDefine()
    this.getEventNotificationConfigurations()
    this.querySeverity()
    this.getManagerSelect()
  },
  updated() {
    this.getOperateBoxHeight()
  },
  mounted() {
    this.getOperateBoxHeight()
  },
  methods: {
    getPhysicalCategorys() {
      getPhysicalCategory1({ projectId: 10 }).then((response) => {
        this.resource_types = response.datas
      })
    },
    getProbeSourceTypes() {
      this.ruleForm.event_source = ''
      getProbeSourceType({
        projectId: 10,
        engine: 'Freemarker'
        // source_type: this.ruleForm.event_source_type,
      }).then((response) => {
        this.eventSources = response.datas
      })
    },
    getManagerSelect() {
      getManagerSelect({ projectId: 10 }).then((response) => {
        this.manager_types = response.datas
      })
    },
    querySeverity() {
      getEventSeverityDefine({
        name: '',
        projectId: 10,
        start: 1,
        limit: 10
      }).then((response) => {
        // 赋值SeverityArr
        if (response.success) {
          this.SeverityArr = response.datas
          this.SeverityArr = this.SeverityArr.filter((item) => item.isEnable == true)
        }
      })
    },
    // 查询类型设置
    columnChange(row) {
      const { conditionColumn } = row
      row.conditionDataType = ''
      row.operator = ''
      row.conditionValue = []
      if (!row.hasOwnProperty('operatorLogic')) {
        this.$set(row, 'operatorLogic', '')
      }
      this.effectExper.map((currentValue) => {
        const { columnInDB, dataType } = currentValue
        if (conditionColumn == columnInDB) {
          row.conditionDataType = dataType
        }
      })
    },
    changeIntervalTypeBy() {
      if (this.ruleForm.interval_type === 'day') {
        this.day = true
        this.week = false
        this.month = false
        this.year = false
      } else if (this.ruleForm.interval_type === 'week') {
        this.day = false
        this.week = true
        this.month = false
        this.year = false
      } else if (this.ruleForm.interval_type === 'year') {
        this.day = false
        this.week = false
        this.month = false
        this.year = true
      } else if (this.ruleForm.interval_type === 'month') {
        this.day = false
        this.week = false
        this.month = true
        this.year = false
      }
    },
    changeIntervalType() {
      if (this.ruleForm.interval_type === 'day') {
        this.ruleForm.day_of_week_at = ''
        this.ruleForm.day_of_week_util = ''
        this.ruleForm.month_start = ''
        this.ruleForm.month_end = ''
        this.ruleForm.day_start = ''
        this.ruleForm.day_end = ''
        this.ruleForm.execute_at = moment().format('00:00:00')
        this.ruleForm.execute_util = moment().format('23:59:59')
        this.day = true
        this.week = false
        this.month = false
        this.year = false
      } else if (this.ruleForm.interval_type === 'week') {
        this.ruleForm.month_start = ''
        this.ruleForm.month_end = ''
        this.ruleForm.day_start = ''
        this.ruleForm.day_end = ''
        var weekOfday = moment().format('E')
        this.ruleForm.day_of_week_at = weekOfday
        this.ruleForm.day_of_week_util = weekOfday
        this.ruleForm.execute_at = moment().format('00:00:00')
        this.ruleForm.execute_util = moment().format('23:59:59')
        this.day = false
        this.week = true
        this.month = false
        this.year = false
      } else if (this.ruleForm.interval_type === 'year') {
        this.ruleForm.day_of_week_at = ''
        this.ruleForm.day_of_week_util = ''
        this.ruleForm.day_start = moment().format('DD')
        this.ruleForm.day_end = moment().format('DD')
        this.ruleForm.month_start = moment().format('MM')
        this.ruleForm.month_end = moment().format('MM')
        this.day = false
        this.week = false
        this.month = false
        this.year = true
      } else if (this.ruleForm.interval_type === 'month') {
        this.ruleForm.day_of_week_at = weekOfday = ''
        this.ruleForm.day_of_week_util = weekOfday = ''
        this.ruleForm.day_start = moment().format('DD')
        this.ruleForm.day_end = moment().format('DD')
        this.day = false
        this.week = false
        this.month = true
        this.year = false
      }
    },
    changeExecType() {
      if (this.ruleForm.exec_type === 'O') {
        this.ruleForm.timeDescription = [moment().format('YYYY-MM-DD 00:00:00'), moment().format('YYYY-MM-DD 23:59:59')]
        this.once = true
        this.twice = false
        this.month = false
        this.year = false
        this.week = false
        this.day = false
      } else if (this.ruleForm.exec_type === 'R') {
        // 第一步
        this.ruleForm.timeDescription = []
        this.ruleForm.execute_at = '00:00:00'
        this.ruleForm.execute_util = '23:59:59'
        this.once = false
        this.twice = true
        this.month = false
        this.year = false
        this.week = false
        this.day = true
        this.ruleForm.interval_type = 'day'
      }
    },
    changeSelect() {
      this.ruleForm.serverity_name = this.ruleForm.serverity_name1.toString()
    },
    getEventNotificationConfigurations() {
      // 查询列表
      getHandlerRulesDefine({
        engine: 'Freemarker',
        name: this.formInline.name,
        ruleType: 'N',
        projectId: 10,
        excludeRuleType: '',
        start: this.queryParams.start,
        limit: this.queryParams.limit
      }).then((response) => {
        if (response.success) {
          this.dataList = response.datas
          this.totalCount = response.totalCount
        }
      })
    },
    // setFormular(row) {
    //   this.ruleForm.formular = ''
    //   for (var i = 0; i < this.ruleForm.expression.length; i++) {
    //     if (this.ruleForm.formular === '') {
    //       this.ruleForm.formular = this.ruleForm.expression[i].conditionColumn
    //     } else {
    //       this.ruleForm.formular = this.ruleForm.formular + ' and ' + this.ruleForm.expression[i].conditionColumn
    //     }
    //   }
    // },
    setFormular(row) {
      // console.log(row, this.ruleForm.expression, this.ruleForm.expression[0].operatorLogic)
      this.ruleForm.formular = ''
      // 根据条件字段拼接运算公式
      for (var i = 0; i < this.ruleForm.expression.length; i++) {
        if (this.ruleForm.formular === '') {
          this.ruleForm.formular = this.ruleForm.expression[i].conditionColumn
        } else {
          const temp = this.ruleForm.expression[i - 1].operatorLogic === '&&' ? ' and ' : ' or '
          this.ruleForm.formular = this.ruleForm.formular + temp + this.ruleForm.expression[i].conditionColumn
        }
      }
    },
    // 删除条件字段
    fieldExpreDelete(scope) {
      this.ruleForm.expression.splice(scope.$index, 1)
      this.setFormular()
    },
    addExpreClick() {
      this.ruleForm.expression.push({
        conditionColumn: '',
        conditionDataType: '',
        operator: '',
        conditionValue: ''
      })
    },
    // 新增字段
    addField() {
      this.eventField.push({
        eventFields: '',
        operators: '',
        resultValues: '',
        logicalOperations: ''
      })
    },
    // 获取页面高度
    getOperateBoxHeight() {
      this.$nextTick(function() {
        this.OperateBoxHeight = document.getElementsByClassName('app-container')[0].offsetHeight - document.getElementsByClassName('operateBox')[0].offsetHeight - 10
      })
    },
    // 获取级别数据
    getEventSeverityDefinesAll() {
      getEventSeverityDefineAll({ projectId: 10, name: '' }).then((response) => {
        this.serverityNames = response.datas
        this.getEventNotificationConfigurations()
      })
    },
    // 获取权限数据
    // getPermiRole() {
    //   getPermiRole({ projectId: 10 }).then((response) => {
    //     this.roleDatas = response.datas
    //   })
    // },
    /* getUserByRoleIds() {
      // getUserByRoleId
      getUserByRoleId({ projectId: 10, roleId: this.ruleForm.data_range }).then(
        (response) => {
          this.userDatas = response.datas;
          this.ruleForm.notification_user = this.userDatas[0].user_id;
        }
      );
    },*/
    onRest() {
      this.queryParams.start = 1
      this.formInline = {
        name: ''
      }
      this.getEventNotificationConfigurations()
    },
    // 查询方法
    onSearch() {
      this.getEventNotificationConfigurations()
    },
    search() {},
    onServiceNotifi() {
      // \views\configurationManagement\notificationService
      this.$router.push({ path: '/notificationService' })
    },
    // 添加方法
    onAdd() {
      this.title = '新增'

      this.saveOrUpdate = 'add'
      this.open = true
      this.rest()
      this.changeExecType()
      this.changeIntervalTypeBy()
      this.disable = false
      // this.day = false;
    },
    rest() {
      this.ruleForm = this.ruleForm = {
        name: '',
        exec_type: 'O',
        timeDescription: [moment().format('YYYY-MM-DD 00:00:00'), moment().format('YYYY-MM-DD 23:59:59')],
        event_source_type: 'INTERNAL',
        execute_util: '',
        execute_at: '',
        is_enable: true,
        rule_type: 'Z',
        event_source: '',
        on_completion: 'Y',
        day_of_week_at: '',
        day_of_week_util: '',
        interval_type: '',
        day_start: '',
        day_end: '',
        month_start: '',
        month_end: '',
        formular: '',
        effect: [
          {
            effectColumn: '',
            effectDataType: '',
            effectType: ['email'],
            effectValue: ''
          }
        ],
        expression: [
          {
            conditionColumn: '',
            conditionDataType: '',
            operator: '',
            conditionValue: ''
          }
        ],
        remark: ''
      }
    },

    // 点击同步按钮触发
    // synchronization(row) {
    //   this.$confirm('是否同步到事件平台', '警告', {
    //     confirmButtonText: '确定',
    //     cancelButtonText: '取消',
    //     type: 'warning'
    //   })
    //     .then(() => {
    //       updateSyncEventLevel({ projectId: 10 }).then((response) => {
    //         if (response.success) {
    //           this.msgSuccess('同步成功')
    //         }
    //       })
    //     })
    //     .catch(function() {})
    // },
    submitForm() {
      // this.ruleForm
      this.$refs['ruleForm'].validate((valid) => {
        if (valid) {
          if (this.saveOrUpdate == 'add') {
            this.ruleForm.project_id = '10'
            // this.ruleForm.event_source_type = "INTERNAL";
            this.ruleForm.rule_type = 'N'
            if (this.ruleForm.is_enable == true) {
              this.ruleForm.is_enable = 'Y'
            } else {
              this.ruleForm.is_enable = 'N'
            }
            if (this.ruleForm.effect !== '') {
              this.ruleForm.effect = JSON.stringify(this.ruleForm.effect)
            }
            this.ruleForm.expression = JSON.stringify(this.ruleForm.expression)
            if (this.ruleForm.exec_type === 'O') {
              this.ruleForm.execute_at = this.ruleForm.timeDescription[0]
              this.ruleForm.execute_util = this.ruleForm.timeDescription[1]
            } else {
              if (this.ruleForm.interval_type === 'month') {
                this.ruleForm.execute_at = this.ruleForm.day_start + ' ' + this.ruleForm.execute_at
                this.ruleForm.execute_util = this.ruleForm.day_end + ' ' + this.ruleForm.execute_util
              } else if (this.ruleForm.interval_type === 'year') {
                this.ruleForm.execute_at = this.ruleForm.month_start + '-' + this.ruleForm.day_start + ' ' + this.ruleForm.execute_at
                this.ruleForm.execute_util = this.ruleForm.month_end + '-' + this.ruleForm.day_end + ' ' + this.ruleForm.execute_util
              }
            }
            if (this.ruleForm.timeDescription.length > 0) {
              this.ruleForm.execute_at = moment(this.ruleForm.timeDescription[0]).format('YYYY-MM-DD HH:mm:ss')
              this.ruleForm.execute_util = moment(this.ruleForm.timeDescription[1]).format('YYYY-MM-DD HH:mm:ss')
            }
            addHandlerRulesDefine(this.ruleForm).then((response) => {
              if (response.success) {
                this.open = false
                this.msgSuccess('保存成功')
                this.getEventNotificationConfigurations()
                this.resetForm()
              } else {
                this.msgError('保存失败,已经有相同条件的策略')
                this.resetForm('ruleForm')
              }
            })
          } else {
            this.ruleForm.projectId = 10
            if (this.ruleForm.is_enable == true) {
              this.ruleForm.is_enable = 'Y'
            } else {
              this.ruleForm.is_enable = 'N'
            }
            if (this.ruleForm.effect !== '') {
              this.ruleForm.effect = JSON.stringify(this.ruleForm.effect)
            }
            this.ruleForm.expression = JSON.stringify(this.ruleForm.expression)
            if (this.ruleForm.exec_type === 'O') {
              this.ruleForm.execute_at = this.ruleForm.timeDescription[0]
              this.ruleForm.execute_util = this.ruleForm.timeDescription[1]
            } else {
              if (this.ruleForm.interval_type === 'month') {
                this.ruleForm.execute_at = this.ruleForm.day_start + ' ' + this.ruleForm.execute_at
                this.ruleForm.execute_util = this.ruleForm.day_end + ' ' + this.ruleForm.execute_util
              } else if (this.ruleForm.interval_type === 'year') {
                this.ruleForm.execute_at = this.ruleForm.month_start + '-' + this.ruleForm.day_start + ' ' + this.ruleForm.execute_at
                this.ruleForm.execute_util = this.ruleForm.month_end + '-' + this.ruleForm.day_end + ' ' + this.ruleForm.execute_util
              }
            }
            updateHandlerRulesDefine(this.ruleForm).then((response) => {
              if (response.success) {
                this.open = false
                this.msgSuccess('修改成功')
                this.getEventNotificationConfigurations()
                this.resetForm()
              } else {
                this.msgError('保存失败,已经有相同条件的策略')
                this.resetForm('ruleForm')
              }
            })
          }
        }
      })
    },
    resetForm(formName) {
      this.rest()
      this.changeExecType()
      this.changeIntervalTypeBy()
      this.activeIndex = '0'
      this.disable = false
      this.day = false
      this.$refs['ruleForm'].resetFields()
      this.open = false
    },
    // 删除方法
    handleDelete(row) {
      this.$confirm('是否确认删除该项数据', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          // delNotificationConfiguration({ id: row.id, projectId: 10 }).then(
          //   (response) => {
          //     this.queryParams.start = 1;
          //     this.msgSuccess("删除成功");
          //     this.getEventNotificationConfigurations();
          //   }
          // );
          deleteHandlerRulesDefine({ id: row.id, projectId: 10 }).then((response) => {
            this.queryParams.start = 1
            this.msgSuccess('删除成功')
            this.getEventNotificationConfigurations()
          })
        })
        .catch(function() {})
    },
    getStartTime(row) {
      if (row.exec_type === 'R') {
        if (row.interval_type === 'week') {
          return '周' + row.day_of_week_at + ' ' + row.execute_at
        } else {
          return row.execute_at
        }
      } else {
        if (row.exec_type === 'O') {
          return moment(row.execute_at).format('YYYY-MM-DD HH:mm:ss')
        } else {
          return row.execute_at
        }
      }
    },
    getEndTime(row) {
      if (row.exec_type === 'R') {
        if (row.interval_type === 'week') {
          // return   row.day_of_week_util+"周" +row.day_of_week_util;
          return '周' + row.day_of_week_util + ' ' + row.execute_util
        } else {
          return row.execute_util
        }
      } else {
        if (row.exec_type === 'O') {
          return moment(row.execute_util).format('YYYY-MM-DD HH:mm:ss')
        } else {
          return row.execute_util
        }
      }
    },
    getExecType(row) {
      if (row.exec_type === 'R') {
        return '重复'
      } else {
        return '单次'
      }
    },
    getServerity(row) {
      var serveritys = row.serverity_name.split(',')
      var serverityToString = ''

      for (var i = 0; i < serveritys.length; i++) {
        for (var j = 0; j < this.serverityNames.length; j++) {
          if (serveritys[i].toString() == this.serverityNames[j].id.toString()) {
            if (i == 0) {
              serverityToString = serverityToString + this.serverityNames[j].name + ','
            } else {
              serverityToString = serverityToString + this.serverityNames[j].name
            }
          }
        }
      }
      return serverityToString
    },
    // 修改方法
    handlelook(row) {
      this.title = '查看'
      this.saveOrUpdate = 'update'
      this.open = true
      this.disable = true
      // this.disabled=false;
      this.updateMethod(row)
    },
    // 修改方法
    handleUpdate(row) {
      this.title = '修改'
      this.saveOrUpdate = 'update'
      this.open = true
      this.showBu = true
      this.disable = false
      this.updateMethod(row)
    },
    updateMethod(row) {
      getEventStrategyById({ id: row.id, projectId: 10 }).then((response) => {
        this.open = true
        this.activeIndex = '0'
        this.ruleForm = response.datas[0]

        this.ruleForm.is_enable = this.ruleForm.is_enable.trim()
        // this.ruleForm.timeDescription[0]=
        if (this.ruleForm.is_enable.trim() === 'Y') {
          this.ruleForm.is_enable = true
        } else {
          this.ruleForm.is_enable = false
        }
        this.ruleForm.exec_type = this.ruleForm.exec_type.trim()
        this.ruleForm.on_completion = this.ruleForm.on_completion.trim()
        this.ruleForm.expression = JSON.parse(this.ruleForm.expression)
        this.ruleForm.effect = JSON.parse(this.ruleForm.effect)
        // console.log(this.ruleForm)
        if (this.ruleForm.exec_type === 'O') {
          this.once = true
          this.twice = false
          this.$set(this.ruleForm, 'timeDescription', [moment(this.ruleForm.execute_at).format('YYYY-MM-DD HH:mm:ss'), moment(this.ruleForm.execute_util).format('YYYY-MM-DD HH:mm:ss')])
        } else {
          this.once = false
          this.twice = true
          this.changeIntervalTypeBy()
          if (this.ruleForm.interval_type === 'month') {
            var start = this.ruleForm.execute_at.split(/[\s\n]/)
            var end = this.ruleForm.execute_util.split(/[\s\n]/)
            this.ruleForm.day_start = start[0]
            this.ruleForm.day_end = start[0]
            this.ruleForm.execute_at = start[1]
            this.ruleForm.execute_util = end[1]
          } else if (this.ruleForm.interval_type === 'year') {
            var start = this.ruleForm.execute_at.split(/[\s\n]/)
            var end = this.ruleForm.execute_util.split(/[\s\n]/)
            var start_mon = start[0].split('-')
            var end_mon = end[0].split('-')
            this.ruleForm.month_start = start_mon[0]
            this.ruleForm.month_end = end_mon[0]
            this.ruleForm.day_start = start_mon[1]
            this.ruleForm.day_end = end_mon[1]
            this.ruleForm.execute_at = start[1]
            this.ruleForm.execute_util = end[1]
          }
        }
      })
    },
    // 弹框切换方法
    IsActive(value) {
      this.activeIndex = value
    },
    getEventColumnDefine() {
      getEventColumnDefine({
        columnName: '',
        projectId: 10,
        engine: 'Freemarker',
        columnTypeModify: true
      }).then((response) => {
        this.effectExper = response.datas
      })
    }
  }
}
</script>
<style lang="scss" scoped>
@import '../../../assets/styles/index.scss';

.labelSP {
  width: 100%;

  /deep/.el-form-item__content {
    width: 80%;
  }
}

.dark,
.light {
  .menuDialog /deep/.el-dialog__body {
    height: 600px !important;
    padding: 0 !important;
  }

  .el-menu {
    border-right: solid 1px rgba(66, 170, 211, 0.1) !important;

    .el-menu-item:hover,
    .el-menu-item:focus {
      // background:rgb(85, 136, 237)!important;
      background: rgba(85, 136, 237, 0.1);
    }
  }
}

.radio-box {
  display: inline-block;
  position: relative;
  height: 25px;
  line-height: 25px;
  margin-right: 5px;
}

.radio {
  display: inline-block;
  width: 25px;
  height: 25px;
  vertical-align: middle;
  cursor: pointer;
  background-image: url(../../../assets/gaojingzonglanicon/zengjia.png);
  background-repeat: no-repeat;
  background-position: 0 0;
}

.input-radio {
  display: inline-block;
  position: absolute;
  opacity: 0;
  width: 25px;
  height: 25px;
  cursor: pointer;
  left: 0px;
  outline: none;
  -webkit-appearance: none;
}

.on {
  background-position: -25px 0;
}

.operateBox {
  padding: 15px;
}

.showTableBox {
  margin-top: 20px;
  padding: 0px;

  .titleBox {
    padding: 10px;
    font-size: 14px;
    line-height: 36px;
  }
}

/deep/.el-dialog__body {
  // 弹框按钮样式
  /deep/.el-button--mini {
    padding: 7px 10px;
  }
}

.el-date-editor.el-input,
.el-date-editor.el-input__inner {
  width: 170px;
  // height: 32px;
  // line-height: 32px;
}

/deep/.el-dialog__body {
  .el-table {
    border: 1px solid rgba(66, 170, 211, 0.2);
    border-bottom: 1px solid rgba(66, 170, 211, 0);
  }
}

// 特殊表单样式
.rowStyleForm {
  border: 1px solid rgba(66, 170, 211, 0.2);
  padding: 10px;
  margin-bottom: 22px;
  // .el-dialog__body /deep/.el-form-item{
  //   margin-bottom: 0px;
  // }
}

.light .el-dialog__body .rowStyleForm .el-form-item {
  margin-bottom: 0px !important;
}
</style>
