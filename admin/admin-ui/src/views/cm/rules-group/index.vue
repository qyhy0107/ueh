<template>
  <div class="app-container">
    <div ref="OperateBox" class="operateBox">
      <el-form :inline="true" :model="formInline" label-position="left" @submit.native.prevent>
        <el-form-item label="策略名称">
          <el-input v-model.trim="formInline.name" clearable size="small" placeholder="请输入名称"
            @keyup.enter.native="onSearch" />
        </el-form-item>
        <el-form-item>
          <!-- <el-button type="primary" size="mini" @click="onSearch">查询</el-button>
          <el-button class="addButton" size="mini" @click="onAdd">新增</el-button>
          <el-button size="mini" @click="onRest">重置</el-button> -->
          <el-button class="blueButton" size="mini" @click="onSearch">查询</el-button>
          <el-button class="grayButton" size="mini" @click="onRest">重置</el-button>
          <el-button class="grayButton" size="mini" @click="onAdd">新增</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="showTableBox" :style="{ height: OperateBoxHeight + 'px' }">
      <el-table :data="dataList" :style="{ height: OperateBoxHeight-60 + 'px' }" empty-text=" " element-loading-text="拼命加载中..." >
        <!-- <el-table-column prop="id" label="id" v-if="show" width="160" /> -->
        <template v-if="!loading" slot="empty" style="height:100%">
          <Deficiency width="30%" height="auto" />
        </template>
        <!-- <el-table-column prop="id" label="id" v-if="show" width="160" /> -->
        <el-table-column prop="name" label="策略名称" width="240" />
        <!-- <el-table-column prop="rule_type" label="策略类型" :formatter="getRuleType" width="160" align="center" /> -->
        <el-table-column prop="exec_type" label="时间规则" :formatter="getExecType" width="120" align="center" />
        <el-table-column prop="interval_type" label="重复类型" width="120" align="center" />
        <el-table-column prop="timeDescription" label="时间描述" show-overflow-tooltip :formatter="getTimeDescription" />
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

        <el-table-column prop="remark" label="备注" width="200" />
        <!-- <el-table-column label="查看策略" align="center" width="80" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleWatch(scope.row)">查看策略</el-button>
          </template>
        </el-table-column> -->
        <el-table-column label="操作" align="center" width="240" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-view" @click="handleWatch(scope.row)">查看</el-button>
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="totalCount > 0&&dataList.length" :page-sizes="[10, 20, 50, 200]" :total="totalCount"
        :page.sync="queryParams.start" :limit.sync="queryParams.limit" @pagination="search()" />
    </div>
    <!-- 添加或修改菜单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1000px" class="menuDialog">
      <el-menu v-model="activeIndex" mode="horizontal" :default-active="activeIndex" @select="IsActive">
        <el-menu-item index="2">
          <span slot="title">基本信息</span>
        </el-menu-item>
        <el-menu-item index="3">
          <span slot="title">规则</span>
        </el-menu-item>
      </el-menu>
      <el-row style="height:95%">
        <el-form ref="ruleForm" :model="ruleForm" :rules="rules" label-width="100px" class="demo-ruleForm">
          <!-- 基本信息 -->
          <el-col v-show="activeIndex == '2'" :span="24" style="padding:10px">
            <el-form-item label="策略名称" prop="name">
              <el-input v-model.trim="ruleForm.name" placeholder="请输入策略名称" :disabled="disable" />
            </el-form-item>
            <el-form-item label="生效规则" prop="exec_type">
              <el-radio-group v-model="ruleForm.exec_type" :disabled="disable" @change="changeExecType">
                <el-radio label="O" value="O">单次</el-radio>
                <el-radio label="R" value="R">重复</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-show="once" label="通知时段" prop="timeDescription">
              <el-date-picker v-model="ruleForm.timeDescription" value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange"
                :disabled="disable" size="small" style="width:100%" range-separator="至" start-placeholder="开始日期"
                end-placeholder="结束日期" />
            </el-form-item>
            <!--        day,week,month,year-->
            <el-form-item v-show="twice" label="" prop="interval_type">
              <el-radio-group v-model="ruleForm.interval_type" :disabled="disable" @change="changeIntervalType">
                <el-radio v-for="item in radios" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
            <!-- 选择日 -->
            <el-form-item v-show="day" label="">
              <el-time-picker v-model="ruleForm.execute_at" class="date-box" format="HH:mm:ss" size="small"
                :disabled="disable" placeholder="开始时间" value-format="HH:mm:ss" />
              <el-time-picker v-model="ruleForm.execute_util" class="date-box" size="small" placeholder="结束时间"
                format="HH:mm:ss" :disabled="disable" value-format="HH:mm:ss" />
            </el-form-item>
            <!-- 选择周 -->
            <el-form-item v-show="week" label="">
              <el-row style="height:40px">
                <el-col :span="6">
                  <el-select v-model="ruleForm.day_of_week_at" placeholder="请选择周" size="small"
                    class="el-form-item__label" :disabled="disable">
                    <el-option v-for="item in weeks" :key="item.id" :label="item.week" :value="item.id" />
                  </el-select>
                </el-col>
                <el-col :span="6">
                  <el-time-picker v-model="ruleForm.execute_at" class="date-box" size="small" format="HH:mm:ss"
                    :disabled="disable" placeholder="开始时间" value-format="HH:mm:ss" />
                </el-col>
                <el-col :span="6">
                  <el-select v-model="ruleForm.day_of_week_util" placeholder="请选择周" :disabled="disable" size="small"
                    class="el-form-item__label">
                    <el-option v-for="item in weeks" :key="item.id" :label="item.week" :value="item.id" />
                  </el-select>
                </el-col>
                <el-col :span="6">
                  <el-time-picker v-model="ruleForm.execute_util" class="date-box" size="small" format="HH:mm:ss"
                    :disabled="disable" placeholder="结束时间" value-format="HH:mm:ss" />
                </el-col>
              </el-row>
            </el-form-item>
            <!-- 选择月 -->
            <el-form-item v-show="month" label="">
              <el-row style="height:40px">
                <el-col :span="6">
                  <el-select v-model="ruleForm.day_start" placeholder="请选择天" size="small" class="el-form-item__label"
                    :disabled="disable">
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
            <!-- 选择年 -->
            <el-form-item v-show="year" label="">
              <el-row style="height:40px">
                <el-col>
                  <el-select v-model="ruleForm.month_start" placeholder="请选择月" size="small" class="el-form-item__label"
                    style="width:30%" :disabled="disable">
                    <el-option v-for="item in years" :key="item.id" :label="item.month" :value="item.id" />
                  </el-select>
                  <el-select v-model="ruleForm.day_start" placeholder="请选择天" size="small" class="el-form-item__label"
                    style="width:30%" :disabled="disable">
                    <el-option v-for="item in months" :key="item.id" :label="item.day" :value="item.id" />
                  </el-select>
                  <el-time-picker v-model="ruleForm.execute_at" class="date-box" format="HH:mm:ss" placeholder="开始时间"
                    size="small" value-format="HH:mm:ss" style="width:30%" :disabled="disable" />
                </el-col>
                <el-col>
                  <el-select v-model="ruleForm.month_end" placeholder="请选择月" size="small" class="el-form-item__label"
                    style="width:30%" :disabled="disable">
                    <el-option v-for="item in years" :key="item.id" :label="item.month" :value="item.id" />
                  </el-select>
                  <el-select v-model="ruleForm.day_end" placeholder="请选择天" size="small" class="el-form-item__label"
                    style="width:30%" :disabled="disable">
                    <el-option v-for="item in months" :key="item.id" :label="item.day" :value="item.id" />
                  </el-select>
                  <el-time-picker v-model="ruleForm.execute_util" style="width:30%" class="date-box" size="small"
                    placeholder="结束时间" format="HH:mm:ss" value-format="HH:mm:ss" :disabled="disable" />
                </el-col>
              </el-row>
            </el-form-item>
            <el-row>
              <el-col :span="12">
                <el-form-item label="过期保留" prop="on_completion">
                  <el-radio-group v-model="ruleForm.on_completion" :disabled="disable">
                    <el-radio label="Y">保留</el-radio>
                    <el-radio label="N">不保留</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="是否启用" prop="is_enable">
                  <el-switch v-model="ruleForm.is_enable" :disabled="disable" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="备注" prop="remark">
              <el-input v-model.trim="ruleForm.remark" type="textarea" :disabled="disable" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
          <!-- 规则 -->
          <el-col v-show="activeIndex == '3'" :span="24" style="padding: 10px;overflow: auto;height: 530px;">
            <el-form-item label="策略类型" prop="rule_type">
              <el-select v-model="ruleForm.rule_type" placeholder="请选择用途" style="width:30%" :disabled="disable"
                @change="changeRuleType">
                <el-option label="分组" value="G" />
              </el-select>
            </el-form-item>
            <el-form-item label="优先级" prop="display_order">
              <el-input v-model.trim="ruleForm.displayOrder" clearable placeholder="数字越小优先级高" style="width:30%"
                :disabled="disable" />
            </el-form-item>
            <div v-if="ruleForm.rule_type === 'G'">
              <!--<el-form-item label="事件源类型" prop="event_source_type">
                <el-select v-model="ruleForm.event_source_type" @change="getProbeSourceTypes()" :disabled='disable' placeholder="请选择事件源" style="width:50%">
                  <el-option v-for="item in sources" :label="item.id" :value="item.name" :key="item.name" />
                </el-select>
              </el-form-item>-->
              <el-form-item label="事件源" prop="event_source">
                <el-select v-model="ruleForm.event_source" :disabled="disable" placeholder="请选择事件源" style="width:50%">
                  <el-option v-for="item in eventSources" :key="item['probe_key']" :label="item.name"
                    :value="item['probe_key']" />
                </el-select>
              </el-form-item>
              <el-form-item label="运算公式" prop="formular">
                <el-input v-model.trim="ruleForm.formular" :disabled="true" clearable />
              </el-form-item>
              <el-form-item label="条件字段" prop="expression">
                <el-table :show-header="status" :data="ruleForm.expression" style="overflow-x:hidden">
                  <el-table-column type="expand">
                    <template slot-scope="props">
                      <el-form label-position="left" label-width="100px" inline class="demo-table-expand">
                        <div>
                          <el-form-item label="字段名">
                            <!-- <span>{{ props.row.conditionColumn }}</span> -->
                            <el-select v-model="props.row.conditionColumn" placeholder="请选择条件字段" clearable size="small"
                              :disabled="disable" @change="
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
                              v-model="props.row.operator" :disabled="disable" clearable size="small" placeholder="运算符">
                              <el-option v-for="operator in operatorsIntRecoveredStatus" :key="operator.opertorChar"
                                :label="operator.opertorName" :value="operator.opertorChar" />
                            </el-select>
                            <el-select
                              v-else-if="(props.row.conditionDataType == 'INT' || props.row.conditionDataType == 'FLOAT') && props.row.conditionColumn == 'isComponent'"
                              v-model="props.row.operator" :disabled="disable" clearable size="small" placeholder="运算符">
                              <el-option v-for="operator in operatorsIntIsComponent" :key="operator.opertorChar"
                                :label="operator.opertorName" :value="operator.opertorChar" />
                            </el-select>
                            <el-select
                              v-else-if="(props.row.conditionDataType == 'INT' || props.row.conditionDataType == 'FLOAT') && props.row.conditionColumn == 'Acknowledged'"
                              v-model="props.row.operator" :disabled="disable" clearable size="small" placeholder="运算符">
                              <el-option v-for="operator in operatorsIntAcknowledged" :key="operator.opertorChar"
                                :label="operator.opertorName" :value="operator.opertorChar" />
                            </el-select>
                            <el-select
                              v-else-if="props.row.conditionDataType == 'INT' || props.row.conditionDataType == 'FLOAT'"
                              v-model="props.row.operator" :disabled="disable" clearable size="small" placeholder="运算符">
                              <el-option v-for="operator in operatorsInt" :key="operator.opertorChar"
                                :label="operator.opertorName" :value="operator.opertorChar" />
                            </el-select>
                            <el-select v-else-if="props.row.conditionDataType == 'LONGTIMESTAMP'"
                              v-model="props.row.operator" :disabled="disable" clearable size="small" placeholder="运算符">
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
                              v-model="props.row.conditionValue[0]" :disabled="disable" size="small" placeholder="请输入内容"
                              clearable style="width:100%" controls-position="right" :min="1" />
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
                                <el-input-number v-model="props.row.conditionValue[0]" :disabled="disable" size="small"
                                  placeholder="最小值" clearable style="width:100%" controls-position="right" :min="1" />
                              </el-col>
                              <el-col :span="2" style="text-align: center; line-height: 2rem">
                                <span>-</span>
                              </el-col>
                              <el-col :span="11">
                                <el-input-number v-model="props.row.conditionValue[1]" :disabled="disable" size="small"
                                  placeholder="最大值" clearable style="width:100%" controls-position="right" :min="1" />
                              </el-col>
                            </el-row>
                            <el-input v-else v-model.trim="props.row.conditionValue[0]" type="textarea"
                              :disabled="disable" placeholder="默认值" :value="props.row.conditionValue[0]" />
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
                      <el-select v-model="scope.row.conditionColumn" clearable size="small" :disabled="disable"
                        placeholder="请选择条件字段" @change="
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
                      <el-select v-else-if="scope.row.conditionDataType == 'LONGTIMESTAMP'" v-model="scope.row.operator"
                        :disabled="disable" clearable size="small" placeholder="运算符">
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
                  <el-table-column label="值" width="240">
                    <template slot-scope="scope">
                      <el-date-picker v-if="scope.row.conditionDataType == 'LONGTIMESTAMP'"
                        v-model="scope.row.conditionValue[0]" :disabled="disable" value-format="timestamp" size="small"
                        type="datetimerange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
                        clearable class="specialDateClass" />
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
                        <el-option v-for="SItem in isComponentArr" :key="SItem.mappingValue" :value="SItem.mappingValue"
                          :label="SItem.name" />
                      </el-select>
                      <el-select v-else-if="scope.row.conditionColumn == 'Acknowledged'"
                        v-model="scope.row.conditionValue[0]" :disabled="disable" size="small" :multiple="false"
                        placeholder="请选择是否确认" clearable>
                        <el-option v-for="SItem in acknowledgedArr" :key="SItem.mappingValue"
                          :value="SItem.mappingValue" :label="SItem.name" />
                      </el-select>
                      <el-select v-else-if="scope.row.conditionColumn == 'Agent'" v-model="scope.row.conditionValue[0]"
                        :disabled="disable" size="small" :multiple="false" placeholder="请选择Probe" clearable>
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
                      <el-input v-else v-model.trim="scope.row.conditionValue[0]" :disabled="disable" placeholder="默认值"
                        :value="scope.row.conditionValue[0]" />
                    </template>
                  </el-table-column>
                  <el-table-column label="逻辑操作" width="80">
                    <template slot-scope="scope">
                      <el-select v-model="scope.row.operatorLogic" :disabled="disable" placeholder="" clearable
                        size="small">
                        <el-option v-for="item in operatorLogics" :key="item.opertorLogicChar"
                          :label="item.opertorLogicName" :value="item.opertorLogicChar" :disabled="disable" />
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="120">
                    <template slot-scope="scope">
                      <!-- <i v-if="ruleForm.expression.length != 1" class="el-icon-delete" :disabled="disable"
                        @click="fieldExpreDelete(scope)" />
                      <i v-if="scope.$index + 1 == ruleForm.expression.length || scope.$index == ruleForm.expression.length"
                        class="el-icon-circle-plus-outline" :disabled="disable" @click="addExpreClick()" /> -->
                      <el-button v-if="ruleForm.expression.length != 1" size="mini" type="text" :disabled="disable"
                        class="el-icon-delete" @click="fieldExpreDelete(scope)">删除</el-button>
                      <el-button
                        v-if="scope.$index + 1 == ruleForm.expression.length || scope.$index == ruleForm.expression.length"
                        size="mini" type="text" :disabled="disable" @click="addExpreClick()"
                        class="el-icon-circle-plus-outline">新增</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </el-form-item>
              <el-form-item label="分组">
                <el-select v-model="ruleForm.effect[0].effectValue" placeholder="请选择组别" clearable size="small"
                  style="width: 75%" :disabled="disable">
                  <el-option v-for="item in resource_types" :key="item.name" :label="item.label_text"
                    :value="item.name" />
                </el-select>
              </el-form-item>
            </div>
          </el-col>
        </el-form>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <!-- <el-button class="cancel_button" @click="resetForm('ruleForm')">取 消</el-button> -->
        <el-button v-if="!disable" class="cancel_button" @click="resetForm('ruleForm')">取 消</el-button>
        <el-button v-else class="cancel_button" @click="resetForm('ruleForm')">关 闭</el-button>
        <el-button v-show="!disable" type="primary" class="confirm_button" @click="submitForm()">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { addHandlerRulesDefine, updateHandlerRulesDefine, getHandlerRulesDefine, deleteHandlerRulesDefine, getEventStrategyById, getResByCategoryName, getPhysicalCategory1, getProbeSourceType, getManagerSelect } from '@/api/cm/eventStrategy'
import moment from 'moment'
import { getEventColumnDefine } from '@/api/publicResources/eventField'
// 省缺组件
import Deficiency from '@/components/Deficiency/index'
import { getEventSeverityDefine } from '@/api/publicResources/eventLevel'
export default {
  components: { Deficiency },
  data() {
    return {
      // 级别数组
      SeverityArr: [],
      isComponentArr: [{ mappingValue: 0, name: '组件告警' }, { mappingValue: 1, name: '设备告警' }],
      acknowledgedArr: [{ mappingValue: 0, name: '未确认' }, { mappingValue: 1, name: '已确认' }],
      recoveredStatusArr: [{ mappingValue: 1, name: '未恢复' }, { mappingValue: 2, name: '已恢复' }],
      conditions: [{ column: null, operator: null, value: null, dataType: null }],
      // TIMESTAMP运算符
      operatorsTime: [{ opertorName: '在  和  之间', opertorChar: 'between' }],
      opsTime: [{ opertorName: '在  和  之间', opertorChar: 'between' }],
      // 数字
      operatorsInt: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }, { opertorName: '小于', opertorChar: '<' }, { opertorName: '小于等于', opertorChar: '<=' }, { opertorName: '大于', opertorChar: '>' }, { opertorName: '大于等于', opertorChar: '>=' }, { opertorName: '包含', opertorChar: 'in' }, { opertorName: '在  和  之间', opertorChar: 'between' }, { opertorName: '正则', opertorChar: 'regex' }],
      operatorsIntRecoveredStatus: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }],
      operatorsIntIsComponent: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }],
      operatorsIntAcknowledged: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }],
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
      operatorsStrEventCategory: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }],
      operatorsStrAgent: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }],
      operatorsStrManager: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }],
      formData3: {
        powerAttrList: [
          {
            conditionColumn: '',
            conditionDataType: '',
            operator: '',
            conditionValue: '',
            mapping: [
              {
                sourceColumn: '',
                sourceValue: ''
              }
            ],
            resource_type: '',
            resource_name: ''
          }
        ]
      },
      effect: [
        {
          effectColumn: '',
          effectDataType: '',
          effectType: '',
          effectValue: ''
        }
      ],
      status: false,
      // 运算公式
      formular: '',
      // 表达式
      expression: [
        {
          conditionColumn: '',
          conditionDataType: '',
          operator: '',
          conditionValue: ''
        }
      ],
      // 字段映射
      eventField: [
        {
          eventFields: '',
          operators: '',
          resultValues: '',
          logicalOperations: ''
        }
      ],
      eventLevel: [{ name: '严重', characte: '=', IsRequired: true, zabbixName: 0 }, { name: '一般', characte: '=', IsRequired: true, zabbixName: 0 }, { name: '中等', characte: '=', IsRequired: true, zabbixName: 0 }, { name: '发生时间2', characte: '=', IsRequired: true, zabbixName: 0 }],
      activeIndex: '2',
      // 获取表格页面高度
      OperateBoxHeight: 400,
      // 弹出弹窗title显示
      title: '新增',
      content: '',
      // 遮罩层
      loading: true,
      disable: false,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 总条数
      total: 0,
      totalCount: 1,
      queryParams: {
        start: 1,
        limit: 20
      },
      formInline: {
        name: ''
      },
      // 新增、修改弹框
      open: false,
      dataList: [],
      show: false,
      radios: [{ label: '日', value: 'day', isChecked: true }, { label: '周', value: 'week', isChecked: false }, { label: '月', value: 'month', isChecked: false }, { label: '年', value: 'year', isChecked: false }],
      sources: [{ id: 'ZABBIX', name: 'ZABBIX' }, { id: 'SYSLOG', name: 'SYSLOG' }, { id: 'SNMPTRAP', name: 'SNMPTRAP' }],
      operators: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }, { opertorName: '小于', opertorChar: '<' }, { opertorName: '小于等于', opertorChar: '<=' }, { opertorName: '大于', opertorChar: '>' }, { opertorName: '大于等于', opertorChar: '>=' }, { opertorName: '包含', opertorChar: 'in' }, { opertorName: '相似', opertorChar: 'like' }, { opertorName: '在  和  之间', opertorChar: 'between' }],
      mappingAndJoin: [{ label: '映射', value: 'mapping' }],
      operatorLogics: [
        { opertorLogicName: '与', opertorLogicChar: '&&' },
        { opertorLogicName: '或', opertorLogicChar: '||' }
        /* { opertorLogicName: "非", opertorLogicChar: "!" },*/
        /* { opertorLogicName: "异或", opertorLogicChar: "XOR" },*/
      ],
      effectExper: [],
      effectDataTypes: [{ key: 'counter', value: '累计' }, { key: 'newest', value: '最新' }],
      resource_types: '',
      manager_types: [],
      resource_names: '',
      eventSources: '',
      event_source: [],
      ruleForm: {
        name: '',
        exec_type: 'O',
        timeDescription: [moment().format('YYYY-MM-DD 00:00:00'), moment().format('YYYY-MM-DD 23:59:59')],
        execute_util: '',
        execute_at: '',
        is_enable: true,
        event_source_type: 'RESTAPI',
        rule_type: 'G',
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
            effectType: '',
            effectValue: ''
          }
        ],
        expression: [
          {
            conditionColumn: '',
            conditionDataType: '',
            operator: '',
            conditionValue: []
          }
        ],
        remark: '',
        displayOrder: 0
      },
      saveOrUpdate: 'save',
      once: true,
      twice: false,
      day: false,
      week: false,
      month: false,
      year: false,
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
      rules: {
        event_source_type: [{ required: true, message: '请选择数据源', trigger: 'blur' }],
        name: [{ required: true, message: '请填写策略名称', trigger: 'blur' }],
        /* formular: [
            { required: true, message: '请输入公式', trigger: 'blur' }
          ],*/
        rule_type: [{ required: true, message: '请选择策略类型', trigger: 'blur' }]
      }
    }
  },
  created() {
    if (this.$route.params.name) {
      this.formInline.name = this.$route.params.name
    }
    this.getHandlerRulesDefines()
    this.getPhysicalCategorys()
    this.getProbeSourceTypes()
    this.getEventColumnDefine()
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
    querySeverity() {
      getEventSeverityDefine({
        name: '',
        projectId: 10,
        start: 1,
        limit: 20
      }).then((response) => {
        // 赋值SeverityArr

        this.SeverityArr = response.datas
        this.SeverityArr = this.SeverityArr.filter((item) => item.isEnable == true)
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
      // 更换值的时候，先重置
      // console.log(JSON.stringify(this.conditions));
      // this.conditions[index].value = null;
      // this.conditions[index].operator = null;
      // let arr = [];
      // this.conditions.forEach((item) => {
      //   arr.push(item.column);
      // });
      // this.tableColumns.forEach((item) => {
      //   item.isSelect = false;
      //   // 判断当前下拉项要不要禁止点击
      //   if (arr.includes(item.columnInDB)) {
      //     item.isSelect = true;
      //   }
      //   if (item.columnInDB == this.conditions[index].column) {
      //     // 判断当前dataType有没有值，没有的话直接赋值
      //     this.conditions[index].dataType = item.dataType;
      //     // item.isSelect = !item.isSelect;
      //     if (item.dataType == "INT" || item.dataType == "FLOAT") {
      //       this.conditions[index].value = [];
      //     }
      //   }
      // });
    },
    addWork() {
      this.formData3.powerAttrList.push({
        conditionColumn: '',
        conditionDataType: '',
        operator: '',
        conditionValue: '',
        mapping: [
          {
            sourceColumn: '',
            sourceValue: ''
          }
        ],
        resource_type: '',
        resource_name: ''
      })
    },
    changeRuleType(ruleType) {
      this.ruleForm.formular = ''
      this.ruleForm.displayOrder = 0
      this.ruleForm.expression = [
        {
          conditionColumn: '',
          conditionDataType: '',
          operator: '',
          conditionValue: []
        }
      ]
      if (ruleType === 'F') {
        this.ruleForm.effect = [
          {
            effectColumn: '',
            effectDataType: '',
            effectType: '',
            effectValue: []
          }
        ]
      } else {
        this.ruleForm.effect = [
          {
            effectColumn: '',
            effectDataType: '',
            effectType: '',
            effectValue: ''
          }
        ]
      }
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
    getProbeSourceTypesRadio() {
      this.ruleForm.event_source = ''
      getProbeSourceType({
        projectId: 10,
        source_type: this.event_source[0]
      }).then((response) => {
        this.eventSources = response.datas
      })
    },
    getPhysicalCategorys() {
      getPhysicalCategory1({ projectId: 10 }).then((response) => {
        this.resource_types = response.datas
      })
    },
    getManagerSelect() {
      getManagerSelect({ projectId: 10 }).then((response) => {
        this.manager_types = response.datas
      })
    },
    // 删除属性列
    removeRow(index) {
      this.formData3.powerAttrList.splice(index, 1)
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
    getResByCategoryNames(index) {
      getResByCategoryName({
        name: this.formData3.powerAttrList[index].resource_type,
        projectId: 10
      }).then((response) => {
        this.resource_names = response.datas
      })
    },
    groupClick() {
      getResByCategoryName({
        name: this.ruleForm.effect[0].effectColumn,
        projectId: 10
      }).then((response) => {
        this.resourceNames = response.datas
        this.$forceUpdate()
      })
    },
    // 数据初始化
    getHandlerRulesDefines() {
      this.loading = true
      getHandlerRulesDefine({
        engine: 'Freemarker',
        name: this.formInline.name,
        ruleType: 'G',
        projectId: 10,
        excludeRuleType: 'N',
        start: this.queryParams.start,
        limit: this.queryParams.limit
      }).then((response) => {
        this.loading = false
        this.dataList = response.datas
        this.totalCount = response.totalCount
      })
    },
    getTimeDescription(row) {
      var start
      var end
      if (row.exec_type == 'R') {
        if (row.interval_type == 'week') {
          start = '周' + row.day_of_week_at + ' ' + row.execute_at
          end = '周' + row.day_of_week_util + ' ' + row.execute_util
        } else {
          start = row.execute_at
          end = row.execute_util
        }
      } else {
        start = row.execute_at
        end = row.execute_util
      }
      return start + '~' + end
    },
    getCompletion(row) {
      // 过期保留，Y：保留，N：不保留
      if (row.on_completion.trim() === 'Y') {
        return '保留'
      }
      if (row.on_completion.trim() === 'N') {
        return '不保留'
      }
    },
    getExecType(row) {
      // 生效类型：O单次、R重复
      if (row.exec_type.trim() === 'O') {
        return '单次'
      }
      if (row.exec_type.trim() === 'R') {
        return '重复'
      }
    },
    getRuleType(row) {
      // N通用，C合并，R丰富，U升级，D降级，Z:压缩,F过滤，G分组
      if (row.rule_type === 'N') {
        return '通用'
      } else if (row.rule_type === 'C') {
        return '合并'
      } else if (row.rule_type === 'R') {
        return '丰富'
      } else if (row.rule_type === 'U') {
        return '升级'
      } else if (row.rule_type === 'D') {
        return '降级'
      } else if (row.rule_type === 'Z') {
        return '压缩'
      } else if (row.rule_type === 'F') {
        return '过滤'
      } else if (row.rule_type === 'G') {
        return '分组'
      }
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
    getEventColumnDefine() {
      getEventColumnDefine({
        columnName: '',
        projectId: 10,
        engine: 'Freemarker',
        columnTypeModify: true
      }).then((response) => {
        this.effectExper = response.datas
      })
    },
    // 获取页面高度
    getOperateBoxHeight() {
      this.$nextTick(function() {
        this.OperateBoxHeight = document.getElementsByClassName('app-container')[0].offsetHeight - document.getElementsByClassName('operateBox')[0].offsetHeight - 10
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
    // 删除条件字段
    fieldExpreDelete(scope) {
      this.ruleForm.expression.splice(scope.$index, 1)
      this.setFormular()
    },
    // 删除丰富字段
    fielFengfudExpreDelete(index, scope) {
      this.formData3.powerAttrList[index].mapping.splice(scope.$index, 1)
      this.setFormular()
    },
    // 删除结果字段
    fieldEffectDelete(scope) {
      this.ruleForm.effect.splice(scope.$index, 1)
    },
    // 条件字段添加行数
    addFengfuExpreClick(index) {
      this.formData3.powerAttrList[index].mapping.push({
        sourceColumn: '',
        sourceValue: ''
      })
    },
    addExpreClick() {
      this.ruleForm.expression.push({
        conditionColumn: '',
        conditionDataType: '',
        operator: '',
        conditionValue: []
      })
    },
    // 结果字段添加行数
    addEffectClick() {
      this.ruleForm.effect.push({
        effectColumn: '',
        effectDataType: '',
        effectType: '',
        effectValue: ''
      })
    },
    IsActive(value) {
      this.activeIndex = value
    },
    // 查询方法
    onSearch() {
      this.getHandlerRulesDefines()
    },
    onRest() {
      this.formInline.name = ''
      this.getHandlerRulesDefines()
    },
    search() {
      this.getHandlerRulesDefines()
    },
    // 添加方法
    onAdd() {
      this.resetForm('ruleForm')
      this.activeIndex = '2'
      this.title = '新增'
      this.saveOrUpdate = 'save'
      this.disable = false
      // console.log('submit!');
      this.open = true
      // this.ruleForm.usageType='N'
    },
    submitForm() {
      this.$refs['ruleForm'].validate((valid) => {
        if (valid) {
          const effectValue = this.ruleForm.effect[0].effectValue
          this.resource_types.filter((item) => {
            if (item.name == effectValue) {
              this.ruleForm.effect[0].effectType = item.label_text
            }
          })
          const ruleFormTempObj = JSON.parse(JSON.stringify(this.ruleForm))
          if (this.saveOrUpdate === 'save') {
            ruleFormTempObj.project_id = '10'
            if (ruleFormTempObj.is_enable == true) {
              ruleFormTempObj.is_enable = 'Y'
            } else {
              ruleFormTempObj.is_enable = 'N'
            }
            if (ruleFormTempObj.rule_type === 'C') {
              ruleFormTempObj.event_source = this.event_source
            }
            if (ruleFormTempObj.effect !== '') {
              const severityMapping = ruleFormTempObj.effect
              ruleFormTempObj.effect = JSON.stringify(severityMapping)
            }
            if (ruleFormTempObj.rule_type === 'R') {
              ruleFormTempObj.expression = JSON.stringify(this.formData3.powerAttrList)
            }
            ruleFormTempObj.expression = JSON.stringify(ruleFormTempObj.expression)
            if (ruleFormTempObj.exec_type === 'O') {
              ruleFormTempObj.execute_at = ruleFormTempObj.timeDescription[0]
              ruleFormTempObj.execute_util = ruleFormTempObj.timeDescription[1]
            } else {
              if (ruleFormTempObj.interval_type === 'month') {
                ruleFormTempObj.execute_at = ruleFormTempObj.day_start + ' ' + ruleFormTempObj.execute_at
                ruleFormTempObj.execute_util = ruleFormTempObj.day_end + ' ' + ruleFormTempObj.execute_util
              } else if (ruleFormTempObj.interval_type === 'year') {
                ruleFormTempObj.execute_at = ruleFormTempObj.month_start + '-' + ruleFormTempObj.day_start + ' ' + ruleFormTempObj.execute_at
                ruleFormTempObj.execute_util = ruleFormTempObj.month_end + '-' + ruleFormTempObj.day_end + ' ' + ruleFormTempObj.execute_util
              }
            }
            if (ruleFormTempObj.timeDescription.length > 0) {
              ruleFormTempObj.execute_at = moment(ruleFormTempObj.timeDescription[0]).format('YYYY-MM-DD HH:mm:ss')
              ruleFormTempObj.execute_util = moment(ruleFormTempObj.timeDescription[1]).format('YYYY-MM-DD HH:mm:ss')
            }
            addHandlerRulesDefine(ruleFormTempObj).then((response) => {
              if (response.success) {
                this.open = false
                this.msgSuccess('保存成功')
                this.getHandlerRulesDefines()
                this.resetForm()
              } else {
                this.msgError('保存失败,已经有相同条件的策略')
                this.resetForm('ruleForm')
              }
            })
          } else {
            ruleFormTempObj.projectId = '10'
            if (ruleFormTempObj.is_enable == true) {
              ruleFormTempObj.is_enable = 'Y'
            } else {
              ruleFormTempObj.is_enable = 'N'
            }
            if (ruleFormTempObj.rule_type === 'C') {
              ruleFormTempObj.event_source = this.event_source
            }
            if (ruleFormTempObj.effect !== '') {
              const severityMapping = ruleFormTempObj.effect
              ruleFormTempObj.effect = JSON.stringify(severityMapping)
            }
            ruleFormTempObj.expression = JSON.stringify(ruleFormTempObj.expression)
            if (ruleFormTempObj.exec_type === 'O') {
              ruleFormTempObj.execute_at = ruleFormTempObj.timeDescription[0]
              ruleFormTempObj.execute_util = ruleFormTempObj.timeDescription[1]
            } else {
              if (ruleFormTempObj.interval_type === 'month') {
                ruleFormTempObj.execute_at = ruleFormTempObj.day_start + ' ' + ruleFormTempObj.execute_at
                ruleFormTempObj.execute_util = ruleFormTempObj.day_end + ' ' + ruleFormTempObj.execute_util
              } else if (ruleFormTempObj.interval_type === 'year') {
                ruleFormTempObj.execute_at = ruleFormTempObj.month_start + '-' + ruleFormTempObj.day_start + ' ' + ruleFormTempObj.execute_at
                ruleFormTempObj.execute_util = ruleFormTempObj.month_end + '-' + ruleFormTempObj.day_end + ' ' + ruleFormTempObj.execute_util
              }
            }
            updateHandlerRulesDefine(ruleFormTempObj).then((response) => {
              if (response.success) {
                this.open = false
                this.msgSuccess('修改成功')
                this.getHandlerRulesDefines()
                this.resetForm('ruleForm')
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
      this.activeIndex = '2'
      this.ruleForm = {
        name: '',
        exec_type: 'O',
        timeDescription: [moment().format('YYYY-MM-DD 00:00:00'), moment().format('YYYY-MM-DD 23:59:59')],
        event_source_type: 'RESTAPI',
        execute_util: '',
        execute_at: '',
        is_enable: true,
        rule_type: 'G',
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
            effectType: '',
            effectValue: ''
          }
        ],
        expression: [
          {
            conditionColumn: '',
            conditionDataType: '',
            operator: '',
            conditionValue: []
          }
        ],
        remark: '',
        displayOrder: 0
      }
      this.formData3 = {
        powerAttrList: [
          {
            conditionColumn: '',
            conditionDataType: '',
            operator: '',
            conditionValue: '',
            mapping: [
              {
                sourceColumn: '',
                sourceValue: ''
              }
            ],
            resource_type: '',
            resource_name: ''
          }
        ]
      }
      this.open = false
      this.twice = false
      this.day = false
      this.week = false
      this.month = false
      this.year = false
      this.once = true
    },
    // 删除方法
    handleDelete(row) {
      this.$confirm('是否确认删除该项数据', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          const a = row.id
          deleteHandlerRulesDefine({ id: row.id, projectId: 10 }).then((response) => {
            // this.queryParams.start = 1;
            this.msgSuccess('删除成功')
            this.getHandlerRulesDefines()
          })
        })
        .catch(function(e) {
          console.error(e)
        })
    },
    // 查看方法
    handleWatch(row) {
      this.resetForm('ruleForm')
      this.disable = true
      this.title = '查看'
      getEventStrategyById({ id: row.id, projectId: 10 }).then((response) => {
        this.open = true
        this.activeIndex = '2'
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
        if (this.ruleForm.rule_type === 'R') {
          this.formData3.powerAttrList = JSON.parse(this.ruleForm.expression)
          this.formData3.powerAttrList = JSON.parse(this.formData3.powerAttrList)
        } else {
          this.ruleForm.expression = JSON.parse(this.ruleForm.expression)
        }
        this.ruleForm.effect = JSON.parse(this.ruleForm.effect)
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
    // 修改方法
    handleUpdate(row) {
      this.resetForm('ruleForm')
      this.activeIndex = '2'
      this.disable = false
      this.title = '修改'
      this.saveOrUpdate = 'update'
      getEventStrategyById({ id: row.id, projectId: 10 }).then((response) => {
        this.open = true
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

        if (this.ruleForm.rule_type === 'R') {
          this.formData3.powerAttrList = JSON.parse(this.ruleForm.expression)
          this.formData3.powerAttrList = JSON.parse(this.formData3.powerAttrList)
        } else {
          this.ruleForm.expression = JSON.parse(this.ruleForm.expression)
        }
        this.ruleForm.effect = JSON.parse(this.ruleForm.effect)
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

// 特殊日期框样式
.dark .specialDateClass,
.light .specialDateClass {
  /deep/.el-range-input {
    width: 60%;
    font-size: 10px;
  }

  /deep/.el-range__icon {
    font-size: 10px;
    margin-left: -22px;
    color: transparent;
  }

  /deep/.el-range-separator {
    padding: 2px;
    line-height: 10px;
    padding: 0 auto;
    width: 3%;
    font-weight: 700;
    line-height: 18px;
  }

  /deep/.el-range__close-icon {
    font-size: 10px;
    color: #c0c4cc;
    width: 0px;
    margin-right: 4px;
    // display: inline-block;
    float: right;
    line-height: 26px;
  }
}

/deep/.el-table--scrollable-x .el-table__body-wrapper {
  overflow-x: hidden;
}
</style>
