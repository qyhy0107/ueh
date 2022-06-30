<template>
  <div class="app-container">
    <div ref="OperateBox" class="operateBox" style="position: relative;height:0;padding:0" />
    <div class="showTableBox" :style="{ height: OperateBoxHeight + 'px' }">
      <el-tabs v-model="active.tab" type="border-card" :style="{ height: OperateBoxHeight + 'px' }">
        <el-tab-pane label="24小时" name="first" :style="{ height: OperateBoxHeight + 'px' }">
          <template v-if="active.tab === 'first'">
            <el-form :inline="true" label-position="left" style="margin-top:10px;margin-left:10px">
              <el-form-item label="恢复">
                <el-select v-model="easySearch.RecoveredStatus" size="small" clearable placeholder="请选择" style="width:100px" @change="onSearch">
                  <el-option value="2" label="已恢复" />
                  <el-option value="1" label="未恢复" />
                </el-select>
              </el-form-item>

              <el-form-item label="确认">
                <el-select v-model="easySearch.Acknowledged" size="small" clearable placeholder="请选择" style="width:100px" @change="onSearch">
                  <el-option value="1" label="已确认" />
                  <el-option value="0" label="未确认" />
                </el-select>
              </el-form-item>
              <el-form-item label="事件类型">
                <el-select v-model="easySearch.EventSeverityType" :disabled="easySearch.RecoveredStatus === '1'" size="small" clearable placeholder="请选择" style="width:100px" @change="onSearch">
                  <el-option value="2" label="恢复事件" />
                  <el-option value="1" label="告警事件" />
                </el-select>
              </el-form-item>
              <el-form-item label="级别">
                <el-select v-model="easySearch.Severy" collapse-tags multiple size="small" clearable placeholder="请选择" style="width:140px" @change="onSearch">
                  <el-option v-for="SItem in SeverityArr" :key="SItem.mappingValue + '级别'" :value="SItem.mappingValue" :label="SItem.name" />
                </el-select>
              </el-form-item>
              <el-form-item label="分组" prop="EventCategoryLabelText">
                <el-select ref="selectUpResId" v-model="easySearch.EventCategoryLabelText" style="width:150px" clearable size="small" placeholder="请选择" @focus="filterText = ''">
                  <el-option hidden :value="easySearch.EventCategoryLabelText" :label="classIdName" />
                  <el-input v-model="filterText" placeholder="输入关键字进行过滤" size="mini" style="margin:5px;margin-bottom:0;width:95%" />
                  <el-tree ref="tree" :filter-node-method="filterNode" :data="deviceCategories1" default-expand-all clearable @node-click="handleNodeClick">
                    <div slot-scope="{ node, data }" class="custom-tree-node" style="width:100%">
                      <!-- <span v-if="data.disabled" style="color:rgb(132 130 130);cursor:no-drop;display:inline-block;width:100%">{{ node.label }}</span> -->
                      <span>{{ node.label }}</span>
                    </div>
                  </el-tree>
                </el-select>
              </el-form-item>
              <el-form-item label="来源">
                <el-select v-model="easySearch.Manager" size="small" clearable placeholder="请选择" style="width:100px" @change="onSearch">
                  <el-option v-for="item in managerList" :key="item.Manager" :value="item.Manager" :label="item.Manager" />
                  <!-- <el-option value="Scom邮件告警" label="Scom邮件告警" />
                  <el-option value="科来" label="科来" />
                  <el-option value="Tingyun 3.0" label="Tingyun 3.0" />
                  <el-option value="天旦" label="天旦" /> -->
                </el-select>
              </el-form-item>
              <el-form-item label="设备标识">
                <el-input v-model="easySearch.Node" style="width:250px" size="small" clearable placeholder="请输入设备标识/显示名称" @keyup.enter.native="onSearch" />
              </el-form-item>
              <el-form-item label="描述" min-width="15%">
                <el-input v-model="easySearch.Summary" style="width:250px" size="small" clearable placeholder="请输入描述" @keyup.enter.native="onSearch" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" size="mini" @click="onSearch">查询</el-button>
                <el-button class="grayButton" size="mini" @click="resetCondition1">重置</el-button>
                <el-button class="grayButton" size="mini" @click="resetCondition2">清空条件</el-button>
                <el-button class="addButton" size="mini" :disabled="!idSelections.length" @click="handleBatchAcknoledge">批量确认</el-button>
                <el-button class="addButton" size="mini" :disabled="!idSelections.length" @click="handleBatchRecoveredStatus">批量恢复</el-button>
                <el-button class="addButton" size="mini" @click="exportSelected">导出</el-button>
              </el-form-item>
            </el-form>
            <el-table v-loading="loading" :height="'70%'" :data="dataList" border @sort-change="getOrder" @selection-change="handleSelectionChange" @row-dblclick="openDialog">
              <template v-if="!loading" slot="empty" style="height:100%">
                <Deficiency width="45%" height="auto" />
              </template>
              <el-table-column type="selection" min-width="2%" />
              <el-table-column sortable="custom" align="left" prop="Severity" label="级别" min-width="5%" show-overflow-tooltip :formatter="tableColumnsFormat">
                <template slot-scope="scope" width="100">
                  <span v-for="item in SeverityArr" :key="item.mappingValue + '级别1'" :style="{ color: scope.row.Severity == '0' ? '#0587ff' : scope.row.Severity == '1' || scope.row.Severity == '2' ? '#67c23a' : scope.row.Severity == '3' || scope.row.Severity == '4' ? '#e6a23c' : '#f44336' }">
                    <span v-if="item.mappingValue === scope.row.Severity">{{ item.name }}</span>
                  </span>
                </template>
              </el-table-column>
              <el-table-column sortable="custom" prop="EventCategoryLabelText" label="分组" show-overflow-tooltip min-width="6%" align="left" :formatter="tableColumnsFormat" />
              <!-- <el-table-column sortable="custom" prop="Node" label="设备标识" show-overflow-tooltip min-width="6%" align="left" /> -->
              <el-table-column sortable="custom" label="设备标识" prop="Node" :show-overflow-tooltip="true" min-width="9%" align="left">
                <template slot-scope="scope">
                  <el-tooltip class="item" effect="dark" content="点击跳转到设备详情页" placement="top">
                    <span class="tagLink" @click="turnPage(scope.row)">
                      <i class="el-icon-paperclip" />
                      {{ scope.row.Node }}
                    </span>
                  </el-tooltip>
                </template>
              </el-table-column>
              <el-table-column sortable="custom" prop="NodeAlias" label="显示名称" show-overflow-tooltip min-width="9%" align="left" />
              <el-table-column sortable="custom" prop="FirstOccurrence" label="开始时间" show-overflow-tooltip min-width="10%" align="left" :formatter="tableColumnsFormat" />
              <el-table-column sortable="custom" prop="FirstOccurrence" label="持续时长" show-overflow-tooltip min-width="7%" align="left" :formatter="tableColumnsFormat">
                <template slot-scope="scope">
                  <!-- 是不是可恢复事件 不是↓-->
                  <!-- <span v-if="scope.row.EventType === 'P'">-</span> -->
                  <!-- 否则 是不是已恢复事件  不是↓ 当前-首次 -->
                  <span v-if="scope.row.RecoveredStatus == 1"> {{ (momentDate - scope.row.FirstOccurrence) | getdiffLongTime1 }}</span>
                  <!-- 否则 是不是已恢复事件  是↓ 末次-首次 -->
                  <span v-else-if="scope.row.RecoveredStatus == 2">{{ (scope.row.LastOccurrence - scope.row.FirstOccurrence) | getdiffLongTime1 }}</span>
                </template>
              </el-table-column>
              <el-table-column sortable="custom" prop="Manager" label="来源" show-overflow-tooltip align="left" :formatter="tableColumnsFormat" min-width="6%" />
              <el-table-column sortable="custom" prop="Summary" label="描述" min-width="15%" show-overflow-tooltip />
              <el-table-column sortable="custom" prop="EventSeverityType" label="事件类型" align="left" min-width="7%">
                <template slot-scope="scope">
                  <span v-if="scope.row.EventSeverityType == 1">
                    <el-tag type="danger">告警事件</el-tag>
                  </span>
                  <span v-if="scope.row.EventSeverityType == 2">
                    <el-tag type="success">恢复事件</el-tag>
                  </span>
                </template>
              </el-table-column>
              <el-table-column sortable="custom" prop="RecoveredStatus" label="恢复" align="left" min-width="6%">
                <template slot-scope="scope">
                  <span v-if="scope.row.RecoveredStatus == 1">
                    <el-tag type="danger">未恢复</el-tag>
                  </span>
                  <span v-if="scope.row.RecoveredStatus == 2">
                    <el-tag type="success">已恢复</el-tag>
                  </span>
                </template>
              </el-table-column>
              <el-table-column sortable="custom" prop="Acknowledged" label="确认" align="left" min-width="6%">
                <template slot-scope="scope">
                  <span v-if="scope.row.Acknowledged == 0">
                    <el-tag type="danger">未确认</el-tag>
                  </span>
                  <span v-else>
                    <el-tag type="success">已确认</el-tag>
                  </span>
                </template>
              </el-table-column>
              <!-- <el-table-column sortable="custom" prop="EventCategory" label="确认人" show-overflow-tooltip min-width="5%" align="left" :formatter="tableColumnsFormat" />
              <el-table-column sortable="custom" prop="EventCategory" label="确认日期" show-overflow-tooltip min-width="6%" align="left" :formatter="tableColumnsFormat" /> -->
              <!-- <el-table-column sortable="custom" prop="Tally" label="发生次数" min-width="6%" show-overflow-tooltip /> -->
              <el-table-column label="操作" align="center" min-width="6.5%" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                  <el-button v-if="scope.row.Acknowledged == 0" size="mini" type="text" :icon="scope.row.Acknowledged == 0 ? 'el-icon-edit' : 'el-icon-view'" @click="handleUpdate(scope.row)">{{ scope.row.Acknowledged == 0 ? '确认' : '查看' }}</el-button>
                  <el-button v-else size="mini" type="text" icon="el-icon-view" @click="handleUpdate1(scope.row)">确认记录</el-button>
                </template>
              </el-table-column>
            </el-table>
            <!-- <Deficiency v-else width="25%" height="auto" /> -->
            <pagination v-show="dataList.length > 0" :page-sizes="[10, 20, 50, 200]" :total="totalCount" :page.sync="queryParams.start" :limit.sync="queryParams.limit" @pagination="getEventList()" />
          </template>
        </el-tab-pane>
        <el-tab-pane label="24小时前" name="second" :style="{ height: OperateBoxHeight + 'px' }">
          <template v-if="active.tab === 'second'">
            <el-form :inline="true" label-position="left" style="margin-top:10px;margin-left:10px">
              <el-form-item label="确认">
                <el-select v-model="easySearch.Acknowledged" size="small" clearable placeholder="请选择" style="width:100px" @change="onSearch">
                  <el-option value="1" label="已确认" />
                  <el-option value="0" label="未确认" />
                </el-select>
              </el-form-item>
              <!-- <el-form-item label="事件类型">
                <el-select v-model="easySearch.EventSeverityType" size="small" clearable placeholder="请选择" style="width:100px" @change="onSearch">
                  <el-option value="2" label="恢复事件" />
                  <el-option value="1" label="告警事件" />
                </el-select>
              </el-form-item> -->
              <el-form-item label="级别">
                <el-select v-model="easySearch.Severy" collapse-tags multiple size="small" clearable placeholder="请选择" style="width:140px" @change="onSearch">
                  <el-option v-for="SItem in SeverityArr" :key="SItem.mappingValue + '级别2'" :value="SItem.mappingValue" :label="SItem.name" />
                </el-select>
              </el-form-item>
              <el-form-item label="分组" prop="EventCategoryLabelText">
                <el-select ref="selectUpResId" v-model="easySearch.EventCategoryLabelText" style="width:150px" clearable size="small" placeholder="请选择" @focus="filterText = ''">
                  <el-option hidden :value="easySearch.EventCategoryLabelText" :label="classIdName" />
                  <el-input v-model="filterText" placeholder="输入关键字进行过滤" size="mini" style="margin:5px;margin-bottom:0;width:95%" />
                  <el-tree ref="tree" :filter-node-method="filterNode" :data="deviceCategories1" default-expand-all clearable @node-click="handleNodeClick">
                    <div slot-scope="{ node, data }" class="custom-tree-node" style="width:100%">
                      <!-- <span v-if="data.disabled" style="color:rgb(132 130 130);cursor:no-drop;display:inline-block;width:100%">{{ node.label }}</span> -->
                      <span>{{ node.label }}</span>
                    </div>
                  </el-tree>
                </el-select>
              </el-form-item>
              <el-form-item label="来源">
                <el-select v-model="easySearch.Manager" size="small" clearable placeholder="请选择" style="width:100px" @change="onSearch">
                  <el-option v-for="item in managerList" :key="item.Manager" :value="item.Manager" :label="item.Manager" />
                  <!-- <el-option value="Scom邮件告警" label="Scom邮件告警" />
                  <el-option value="科来" label="科来" />
                  <el-option value="Tingyun 3.0" label="Tingyun 3.0" />
                  <el-option value="天旦" label="天旦" /> -->
                </el-select>
              </el-form-item>
              <el-form-item v-if="active.tab === 'second'" label="开始时间">
                <div @click.once="changeTime">
                  <el-date-picker v-model="easySearch.FirstOccurrence" style="width:507px" :picker-options="pickerOptions" size="small" type="datetimerange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="timestamp" clearable @change="onSearch" />
                </div>
              </el-form-item>
              <el-form-item label="设备标识">
                <el-input v-model="easySearch.Node" style="width:250px" size="small" clearable placeholder="请输入设备标识/显示名称" @keyup.enter.native="onSearch" />
              </el-form-item>
              <el-form-item label="描述" min-width="15%">
                <el-input v-model="easySearch.Summary" style="width:250px" size="small" clearable placeholder="请输入描述" @keyup.enter.native="onSearch" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" size="mini" @click="onSearch">查询</el-button>
                <el-button class="grayButton" size="mini" @click="resetCondition">重置</el-button>
                <el-button class="addButton" size="mini" :disabled="!idSelections.length" @click="handleBatchAcknoledge">批量确认</el-button>
                <el-button class="addButton" size="mini" :disabled="!idSelections.length" @click="handleBatchRecoveredStatus">批量恢复</el-button>
                <el-button class="addButton" size="mini" @click="exportSelected">导出</el-button>
              </el-form-item>
            </el-form>
            <el-table v-loading="loading" :height="'70%'" :data="dataList" border @sort-change="getOrder" @selection-change="handleSelectionChange" @row-dblclick="openDialog">
              <template v-if="!loading" slot="empty" style="height:100%">
                <Deficiency width="45%" height="auto" />
              </template>
              <el-table-column type="selection" min-width="2%" />
              <el-table-column sortable="custom" prop="Severity" label="级别" align="left" show-overflow-tooltip min-width="5%" :formatter="tableColumnsFormat">
                <template slot-scope="scope" width="100">
                  <span v-for="item in SeverityArr" :key="item.mappingValue + '级别3'" :style="{ color: scope.row.Severity == '0' ? '#0587ff' : scope.row.Severity == '1' || scope.row.Severity == '2' ? '#67c23a' : scope.row.Severity == '3' || scope.row.Severity == '4' ? '#e6a23c' : '#f44336' }">
                    <span v-if="item.mappingValue === scope.row.Severity">{{ item.name }}</span>
                  </span>
                </template>
              </el-table-column>
              <el-table-column sortable="custom" prop="EventCategoryLabelText" label="分组" show-overflow-tooltip min-width="6%" align="left" :formatter="tableColumnsFormat" />
              <el-table-column sortable="custom" label="设备标识" prop="Node" :show-overflow-tooltip="true" min-width="9%" align="left">
                <template slot-scope="scope">
                  <el-tooltip class="item" effect="dark" content="点击跳转到设备详情页" placement="top">
                    <span class="tagLink" @click="turnPage(scope.row)">
                      <i class="el-icon-paperclip" />
                      {{ scope.row.Node }}
                    </span>
                  </el-tooltip>
                </template>
              </el-table-column>
              <el-table-column sortable="custom" prop="NodeAlias" label="显示名称" show-overflow-tooltip min-width="9%" align="left" />
              <el-table-column sortable="custom" prop="FirstOccurrence" label="开始时间" show-overflow-tooltip min-width="10%" align="left" :formatter="tableColumnsFormat" />
              <el-table-column sortable="custom" prop="FirstOccurrence" label="持续时长" show-overflow-tooltip min-width="7%" align="left" :formatter="tableColumnsFormat">
                <template slot-scope="scope">
                  <!-- 是不是可恢复事件 不是↓-->
                  <!-- <span v-if="scope.row.EventType === 'P'">-</span> -->
                  <!-- 否则 是不是已恢复事件  不是↓ 当前-首次 -->
                  <span v-if="scope.row.RecoveredStatus == 1"> {{ (momentDate - scope.row.FirstOccurrence) | getdiffLongTime1 }}</span>
                  <!-- 否则 是不是已恢复事件  是↓ 末次-首次 -->
                  <span v-else-if="scope.row.RecoveredStatus == 2">{{ (scope.row.LastOccurrence - scope.row.FirstOccurrence) | getdiffLongTime1 }}</span>
                </template>
              </el-table-column>
              <el-table-column sortable="custom" prop="Manager" label="来源" show-overflow-tooltip align="left" :formatter="tableColumnsFormat" min-width="6%" />
              <el-table-column sortable="custom" prop="Summary" label="描述" min-width="15%" show-overflow-tooltip />
              <el-table-column sortable="custom" prop="EventSeverityType" label="事件类型" align="left" min-width="7%">
                <template slot-scope="scope">
                  <span v-if="scope.row.EventSeverityType == 1">
                    <el-tag type="danger">告警事件</el-tag>
                  </span>
                  <span v-if="scope.row.EventSeverityType == 2">
                    <el-tag type="success">恢复事件</el-tag>
                  </span>
                </template>
              </el-table-column>
              <el-table-column sortable="custom" prop="RecoveredStatus" label="恢复" align="left" min-width="6%">
                <template slot-scope="scope">
                  <span v-if="scope.row.RecoveredStatus == 1">
                    <el-tag type="danger">未恢复</el-tag>
                  </span>
                  <span v-if="scope.row.RecoveredStatus == 2">
                    <el-tag type="success">已恢复</el-tag>
                  </span>
                </template>
              </el-table-column>
              <el-table-column sortable="custom" prop="Acknowledged" label="确认" align="left" min-width="6%">
                <template slot-scope="scope">
                  <span v-if="scope.row.Acknowledged == 0">
                    <el-tag type="danger">未确认</el-tag>
                  </span>
                  <span v-else>
                    <el-tag type="success">已确认</el-tag>
                  </span>
                </template>
              </el-table-column>
              <!-- <el-table-column sortable="custom" prop="EventCategory" label="确认人" show-overflow-tooltip min-width="5%" align="left" :formatter="tableColumnsFormat" />
              <el-table-column sortable="custom" prop="EventCategory" label="确认日期" show-overflow-tooltip min-width="6%" align="left" :formatter="tableColumnsFormat" /> -->
              <!-- <el-table-column sortable="custom" prop="Tally" label="发生次数" min-width="6%" show-overflow-tooltip /> -->
              <el-table-column label="操作" align="center" min-width="6.5%" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                  <el-button v-if="scope.row.Acknowledged == 0" size="mini" type="text" :icon="scope.row.Acknowledged == 0 ? 'el-icon-edit' : 'el-icon-view'" @click="handleUpdate(scope.row)">{{ scope.row.Acknowledged == 0 ? '确认' : '查看' }}</el-button>
                  <el-button v-else size="mini" type="text" icon="el-icon-view" @click="handleUpdate1(scope.row)">确认记录</el-button>
                </template>
              </el-table-column>
            </el-table>
            <!-- <Deficiency v-else width="25%" height="auto" /> -->
            <pagination v-show="dataList.length > 0" :page-sizes="[10, 20, 50, 200]" :total="totalCount" :page.sync="queryParams.start" :limit.sync="queryParams.limit" @pagination="getEventList()" />
          </template>
        </el-tab-pane>
      </el-tabs>
    </div>
    <!-- 发生记录 -->
    <el-dialog title="确认记录" :visible.sync="formShow1" width="950px" min-height="60vh" class="tabsDialog">
      <el-tabs v-model="active1.tab" type="border-card">
        <el-tab-pane label="事件" name="one">
          <el-form ref="formData" :model="formData" :rules="rules" label-width="110px" class="demo-ruleForm">
            <el-row>
              <el-col :span="12">
                <el-form-item label="开始时间">
                  <el-date-picker :value="formData.row.FirstOccurrence" type="datetime" :readonly="true" style="width:100%" />
                </el-form-item>
              </el-col>
              <el-col v-for="item in SeverityArr" :key="item.id + '级别4'" :span="12">
                <el-form-item v-if="item.mappingValue === formData.row.Severity" label="级别">
                  <el-input :value="item.name" :readonly="true" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="持续时长">
                  <!-- <el-input v-if="formData.row.EventType === 'P'" value="-" /> -->
                  <el-input v-if="formData.row.RecoveredStatus == 1" :value="(momentDate - formData.row.FirstOccurrence) | getdiffLongTime1" />
                  <el-input v-else-if="formData.row.RecoveredStatus == 2" :value="(formData.row.LastOccurrence - formData.row.FirstOccurrence) | getdiffLongTime1" />
                </el-form-item>
              </el-col>
              <!-- <el-col v-else :span="12">
                <el-form-item label="结束时间">
                  <el-date-picker :value="formData.row.LastOccurrence" type="datetime" :readonly="true" style="width:100%" />
                </el-form-item>
              </el-col> -->
              <el-col v-for="item in SeverityArr" :key="item.id + '原始'" :span="12">
                <el-form-item v-if="item.mappingValue === formData.row.OldSeverity" label="原始级别">
                  <el-input :value="item.name" :readonly="true" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="设备标识">
                  <el-input :value="formData.row.Node" :readonly="true" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="显示名称">
                  <el-input :value="formData.row.NodeAlias" :readonly="true" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="指标组">
                  <el-input :value="formData.row.AlertGroup" :readonly="true" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="来源">
                  <el-input :value="formData.row.Manager" :readonly="true" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="指标">
                  <!-- <el-input :value="formData.row.AlertKey" :readonly="true" /> -->
                  <el-tooltip :content="formData.row.AlertKey">
                    <el-input :value="formData.row.AlertKey" :readonly="true" />
                  </el-tooltip>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="发生次数">
                  <el-input :value="formData.row.Tally" :readonly="true" />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item label="描述" min-width="15%">
                  <el-input type="textarea" :value="formData.row.Summary" :rows="4" :readonly="true" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="分组">
                  <el-input :value="formData.row.EventCategoryLabelText" placeholder="暂无数据" :readonly="true" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="事件类型">
                  <el-input v-if="formData.row.EventSeverityType == 1" value="告警事件" :readonly="true" />
                  <el-input v-if="formData.row.EventSeverityType == 2" value="恢复事件" :readonly="true" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="确认">
                  <el-input v-if="formData.row.Acknowledged == 0" value="未确认" :readonly="true" />
                  <el-input v-else value="已确认" :readonly="true" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item v-if="formData.row.EventType === 'P'" label="恢复">
                  <el-input v-if="formData.row.RecoveredStatus == 1" :value="'未恢复(不可恢复事件,24h后自动恢复)'" :readonly="true" />
                  <el-input v-if="formData.row.RecoveredStatus == 2" :value="'已恢复'" :readonly="true" />
                </el-form-item>
                <el-form-item v-else label="恢复">
                  <el-input v-if="formData.row.RecoveredStatus == 1" :value="'未恢复'" :readonly="true" />
                  <el-input v-if="formData.row.RecoveredStatus == 2" :value="'已恢复'" :readonly="true" />
                </el-form-item>
              </el-col>
              <el-col>
                <!-- <el-form-item label="通知类型" prop="notifyType">
                  <el-checkbox-group v-model="formData.notifyType" :disabled="true">
                    <el-checkbox label="flow">开单</el-checkbox>
                    <el-checkbox label="email">邮件</el-checkbox>
                    <el-checkbox label="wechat">融讯通</el-checkbox>
                  </el-checkbox-group>
                </el-form-item>
                <el-form-item label="通知对象" prop="person" >
                  <el-input v-model="formData.person" :readonly="true" clearable placeholder="暂无数据" style="vertical-align: top;" />
                </el-form-item> -->
                <el-form-item label="备注" prop="remark">
                  <el-input v-model="formData.remark" :readonly="true" type="textarea" placeholder="暂无数据" clearable />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="确认记录" name="two">
          <div v-if="showCheck">
            <div style="line-height:30px;margin-bottom:10px">
              <div>
                <span>确认人</span>：<span>{{ confirmObj.acknoledger || '-' }}</span>
              </div>
              <div>
                <span>确认时间</span>：<span>{{ confirmObj.transmit_date | getDate }}</span>
              </div>
              <div v-if="labelPosition === 'right' && confirmObj.url">
                <span>工单链接</span>：<span style="color: #1890ff;cursor: pointer;">
                  <i class="el-icon-paperclip" /><span @click="openWindows(confirmObj.url)">{{ confirmObj.url || '-' }}</span></span
                >
              </div>
              <div>
                <span>备注</span>：<span>{{ confirmObj.remark || '-' }}</span>
              </div>
            </div>
            <el-radio-group v-if="confirmObj.work_order_id" v-model="labelPosition" size="small">
              <el-radio-button label="left" size="mini">确认方式</el-radio-button>
              <el-radio-button label="right" size="mini">工单信息</el-radio-button>
            </el-radio-group>
            <div v-if="labelPosition === 'left'" style="padding-top:10px;height:37vh;overflow:auto;">
              <el-table :data="tableData" border :height="310" style="width: 100%;">
                <template v-if="!loading" slot="empty" style="height:100%">
                  <Deficiency style="padding-top:240px" height="100px" width="auto" />
                </template>
                <el-table-column prop="candidate" label="被通知人" />
                <el-table-column prop="notification_types" label="通知介质" :formatter="getStr" />
              </el-table>
            </div>
            <div v-if="labelPosition === 'right'" style="height:37vh;overflow:auto;padding-top:10px" class="rightBox">
              <el-timeline :reverse="true">
                <el-timeline-item v-for="activity in activities" :key="activity.id + 'jz'" color="#5588ed" icon="el-icon-warning-outline" size="large" placement="top">
                  <div class="timer">{{ activity.completeTime || activity.createTime }}</div>
                  <div class="content ">
                    <span>{{ activity.taskName }}</span>
                    <!-- <span>{{ activity.completeTime||activity.createTime }}</span> -->
                    <div>{{ activity.auditorName }}</div>
                  </div>
                </el-timeline-item>
              </el-timeline>
            </div>
          </div>
          <div v-else>
            <Deficiency style="padding-top:50%" height="100px" width="auto" />
          </div>
        </el-tab-pane>
        <!-- <div slot="footer" class="dialog-footer">
          <el-button class="cancel_button" @click="resetForm1()">取 消</el-button>
        </div> -->
      </el-tabs>
    </el-dialog>
    <!-- 添加或修改菜单对话框 -->
    <el-dialog :title="formTitle" :visible.sync="formShow" width="950px">
      <el-form ref="formData" :model="formData" :rules="rules" label-width="110px" class="demo-ruleForm">
        <el-row>
          <el-col :span="12">
            <el-form-item label="开始时间">
              <el-date-picker :value="formData.row.FirstOccurrence" type="datetime" :readonly="true" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col v-for="item in SeverityArr" :key="item.id + 'jb'" :span="12">
            <el-form-item v-if="item.mappingValue === formData.row.Severity" label="级别">
              <el-input :value="item.name" :readonly="true" />
            </el-form-item>
          </el-col>
          <!-- <el-col :span="12">
            <el-form-item label="持续时长">
              <el-input v-if="formData.row.EventType === 'P'" value="-" />
              <el-input v-else-if="formData.row.EventType === 'S' && formData.row.LastOccurrence === formData.row.FirstOccurrence" :value="(momentDate - formData.row.FirstOccurrence) | getdiffLongTime1" />
              <el-input v-else-if="formData.row.EventType === 'S'" :value="(formData.row.LastOccurrence - formData.row.FirstOccurrence) | getdiffLongTime1" />
            </el-form-item>
          </el-col> -->
          <el-col :span="12">
            <el-form-item label="持续时长">
              <!-- <el-input v-if="formData.row.EventType === 'P'" value="-" /> -->
              <!-- <el-input v-if=" formData.row.LastOccurrence === formData.row.FirstOccurrence" :value="(momentDate - formData.row.FirstOccurrence) | getdiffLongTime1" />
              <el-input v-else-if="formData.row.EventType === 'S'" :value="(formData.row.LastOccurrence - formData.row.FirstOccurrence) | getdiffLongTime1" /> -->
              <el-input v-if="formData.row.RecoveredStatus == 1" :value="(momentDate - formData.row.FirstOccurrence) | getdiffLongTime1" />
              <el-input v-else-if="formData.row.RecoveredStatus == 2" :value="(formData.row.LastOccurrence - formData.row.FirstOccurrence) | getdiffLongTime1" />
            </el-form-item>
          </el-col>
          <!-- v-if="formData.row.RecoveredStatus == 1" -->
          <!-- <el-col v-else :span="12">
            <el-form-item label="结束时间">
              <el-date-picker :value="formData.row.LastOccurrence" type="datetime" :readonly="true" style="width:100%" />
            </el-form-item>
          </el-col> -->
          <el-col v-for="item in SeverityArr" :key="item.id + 'olds'" :span="12">
            <el-form-item v-if="item.mappingValue === formData.row.OldSeverity" label="原始级别">
              <el-input :value="item.name" :readonly="true" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备标识">
              <el-input :value="formData.row.Node" :readonly="true" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示名称">
              <el-input :value="formData.row.NodeAlias" :readonly="true" />
            </el-form-item>
          </el-col>
          <!-- <el-col>
            <el-form-item label="最后开始时间">
              <el-date-picker :value="formData.row.LastOccurrence" type="datetime" :readonly="true" style="width:100%" />
            </el-form-item>
          </el-col> -->
          <el-col :span="12">
            <el-form-item label="指标组">
              <el-input :value="formData.row.AlertGroup" :readonly="true" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来源">
              <el-input :value="formData.row.Manager" :readonly="true" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="指标">
              <!-- <el-input :value="formData.row.AlertKey" :readonly="true" /> -->
              <el-tooltip :content="formData.row.AlertKey">
                <el-input :value="formData.row.AlertKey" :readonly="true" />
              </el-tooltip>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发生次数">
              <el-input :value="formData.row.Tally" :readonly="true" />
            </el-form-item>
          </el-col>

          <el-col>
            <el-form-item label="描述" min-width="15%">
              <el-input type="textarea" :value="formData.row.Summary" :rows="4" :readonly="true" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分组">
              <el-input :value="formData.row.EventCategoryLabelText" placeholder="暂无数据" :readonly="true" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="事件类型">
              <el-input v-if="formData.row.EventSeverityType == 1" value="告警事件" :readonly="true" />
              <el-input v-if="formData.row.EventSeverityType == 2" value="恢复事件" :readonly="true" />
            </el-form-item>
          </el-col>
          <!-- <el-col>
            <el-form-item label="分组" style="width:50%">
              <el-input :value="formData.row.EventCategoryLabelText" :readonly="true" />
            </el-form-item>
          </el-col> -->
          <!-- <el-col>
            <el-form-item label="事件类型">
              <el-input v-if="formData.row.isComponent == 0" value="组件告警" :readonly="true" />
              <el-input v-else value="设备告警" :readonly="true" />
            </el-form-item>
          </el-col> -->
          <el-col :span="12">
            <el-form-item label="确认">
              <el-input v-if="formData.row.Acknowledged == 0" value="未确认" :readonly="true" />
              <el-input v-else value="已确认" :readonly="true" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="formData.row.EventType === 'P'" label="恢复">
              <el-input v-if="formData.row.RecoveredStatus == 1" :value="'未恢复(不可恢复事件,24h后自动恢复)'" :readonly="true" />
              <el-input v-if="formData.row.RecoveredStatus == 2" :value="'已恢复'" :readonly="true" />
            </el-form-item>
            <el-form-item v-else label="恢复">
              <el-input v-if="formData.row.RecoveredStatus == 1" :value="'未恢复'" :readonly="true" />
              <el-input v-if="formData.row.RecoveredStatus == 2" :value="'已恢复'" :readonly="true" />
            </el-form-item>
          </el-col>
          <el-col>
            <el-form-item label="通知类型" prop="notifyType">
              <el-checkbox-group v-model="formData.notifyType" :disabled="formTitle !== '确认'">
                <!-- <el-checkbox label="flow">开单</el-checkbox> -->
                <el-checkbox label="email">邮件</el-checkbox>
                <!-- <el-checkbox label="wechat">融讯通</el-checkbox> -->
              </el-checkbox-group>
            </el-form-item>
            <el-form-item label="通知对象" prop="person">
              <el-input v-model="formData.person" type="textarea" :readonly="formTitle !== '确认'" clearable :placeholder="formTitle === '确认' ? '请输入通知对象' : '暂无数据'" style="vertical-align: top;" />
              <div v-if="formTitle === '确认'" style="display:inline-block;width:56%;font-size: 10px;line-height: initial;padding-left:10px;vertical-align: top;">
                <!-- <span style="vertical-align: top;">提示：</span> -->
                <div v-if="formTitle === '确认'" style="display:inline-block;width:100%">
                  提示：<br />
                  1.通知对象不填写时，符合规则的事件将通过事件分类发送对应角色的对象<br />
                  2.点击右侧填充按钮，将填充事件分类发送对应角色的对象<br />
                  3.通知对象请填写华融域账号，通知对象有多个时,用#分割
                </div>
              </div>
              <div v-if="formTitle === '确认'" style="display:inline-block;float: right;margin-top:10px">
                <el-button size="mini" round @click="getPerson">填充</el-button>
              </div>
            </el-form-item>
            <el-form-item label="备注" prop="remark">
              <el-input v-model="formData.remark" type="textarea" :placeholder="formTitle === '确认' ? '请输入备注' : '暂无数据'" clearable :readonly="formTitle !== '确认'" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <div v-if="formTitle === '确认'" style="font-size: 10px;line-height: initial;padding:5px;padding-top:5px;vertical-align: top;">说明：确认操作为对事件确认字段状态的修改</div>
        <el-button class="cancel_button" @click="resetForm()">取 消</el-button>
        <el-button v-if="formTitle === '确认'" type="primary" class="confirm_button" @click="submitForm()">确 定</el-button>
      </div>
    </el-dialog>
    <el-dialog :title="batchFormTitle" :visible.sync="batchFormShow" width="950px">
      <el-form ref="batchFormData" :model="batchFormData" :rules="rules" label-width="110px" class="demo-ruleForm">
        <el-row>
          <el-col>
            <el-form-item label="通知类型" prop="notifyType">
              <el-checkbox-group v-model="batchFormData.notifyType">
                <!-- <el-checkbox label="flow">开单</el-checkbox> -->
                <el-checkbox label="email">邮件</el-checkbox>
                <!-- <el-checkbox label="wechat">融讯通</el-checkbox> -->
              </el-checkbox-group>
            </el-form-item>

            <el-form-item label="备注" prop="remark">
              <el-input v-model="batchFormData.remark" type="textarea" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <div style="font-size: 10px;line-height: initial;padding:5px;padding-top:5px;vertical-align: top;">说明：确认操作为对事件确认字段状态的修改</div>
        <el-button class="cancel_button" @click="resetBatchForm()">取 消</el-button>
        <el-button type="primary" class="confirm_button" @click="submitBatchForm()">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import moment from 'moment'
import { getPhysicalCategories } from '@/api/cm/physicalCategory'
import { alarmTurnToHost, getEventsForRealtime, batchCloseEvents, getPerson, getPersonShow, pushEventToNotifyService, exportStatus, getManager, batchCloseEventsForWeb, getAlarmsCategory, getAlarmsCategory1, getAlarmKeys } from '@/api/event/event-manage'
import { getEventSeverityDefine } from '@/api/publicResources/eventLevel'
import { getEventColumnDefine } from '@/api/publicResources/eventField'
import { isTurnHost } from '@/api/operation/alarmOverview'
import axios from 'axios'
// 省缺组件
import Deficiency from '@/components/Deficiency'
export default {
  components: { Deficiency },
  data() {
    return {
      timer: '',
      alarmKeysArr: null,
      alarmsCategoryArr: null,
      alarmsCategoryArr1: null,
      deviceCategories: [],
      deviceCategories1: [],
      classIdName: '',
      filterText: '',
      confirmObj: {
        work_order_id: ''
      },
      tableData: [],
      showCheck: false,
      activities: [],
      pickerOptions: {
        disabledDate(time) {
          return (
            time.getTime() >
            moment()
              .subtract(1, 'days')
              .format('x')
          )
        }
      },
      // 按钮组
      labelPosition: 'left',
      options: [],
      momentDate: moment().format('x'),
      active: {
        tab: 'first', // 关键指标 性能详情 事件tab
        menu: '' // 性能详情 指标组menu
      },
      active1: {
        tab: 'one', // 关键指标 性能详情 事件tab
        menu: '' // 性能详情 指标组menu
      },
      easySearch: {
        EventCategoryLabelText: '',
        // Severy: '',
        RecoveredStatus: '1',
        Severy: [4, 5],
        Summary: '',
        FirstOccurrence: '',
        Manager: '',
        Node: ''
      },
      searchflag: 0,
      disabled: false,
      // TIMESTAMP运算符
      operatorsTime: [{ opertorName: '在  和  之间', opertorChar: 'between' }],
      // 数字
      operatorsInt: [{ opertorName: '等于', opertorChar: '=' }, { opertorName: '不等于', opertorChar: '!=' }, { opertorName: '小于', opertorChar: '<' }, { opertorName: '小于等于', opertorChar: '<=' }, { opertorName: '大于', opertorChar: '>' }, { opertorName: '大于等于', opertorChar: '>=' }, { opertorName: '包含', opertorChar: 'in' }, { opertorName: '在  和  之间', opertorChar: 'between' }],
      // STRING运算符
      operatorsStr: [
        { opertorName: '等于', opertorChar: '=' },
        { opertorName: '不等于', opertorChar: '!=' },
        /* { opertorName: "包含", opertorChar: "in" },*/
        { opertorName: '相似', opertorChar: 'like' }
      ],
      // 级别数组
      SeverityArr: [],
      // query
      // pickerOptions: {},
      sources: [{ id: 'ZABBIX', name: 'ZABBIX' }, { id: 'SYSLOG', name: 'SYSLOG' }, { id: 'SNMPTRAP', name: 'SNMPTRAP' }],
      queryParams: {
        projectId: 10,
        RecoveredStatus: '1',
        Severy: [4, 5],
        engine: 'Freemarker',
        start: 1,
        limit: 20,
        orderType: '',
        orderName: ''
      },
      //
      conditions: [{ column: null, operator: null, value: null, dataType: null }],
      // 获取表格页面高度
      OperateBoxHeight: 400,
      tableColumns: [],
      managerList: [],
      // 遮罩层
      loading: true,
      // 表格选中记录
      idSelections: [],
      tableSelections: [],
      // 总条数
      total: 0,
      totalCount: 0,
      formTitle: '确认',
      saveOrUpdate: 'save',
      dataList: [],
      formShow: false,
      formShow1: false,
      formData: {
        row: {},
        notifyType: [],
        remark: ''
      },
      rules: {
        // person: [
        //   //  { pattern: /([A-Za-z0-9_#_]*)*$/, message: '指标名称要字母开头，且只能输入字母数字或者下划线' },
        //   // { pattern: /([A-Za-z0-9_#_]*)*$/, message: '指标名称要字母开头，且只能输入字母数字或者下划线', trigger: 'blur' }
        // ]
      },
      batchFormTitle: '批量确认',
      batchFormShow: false,
      batchFormData: {
        notifyType: [],
        remark: ''
      }
    }
  },
  // 计算属性
  // computed: {
  //   momentDate: function() {
  //     return moment().format('x')
  //   }

  // },
  watch: {
    // alarmsCategoryArr() {
    //   // this.$refs.tree.filter(val)
    //   // this.$set(this.queryParams, 'alarmKeysArr', this.alarmKeysArr)
    //   this.$set(this.queryParams, 'alarmsCategoryArr', this.alarmsCategoryArr)
    //   this.onSearch()
    // },
    // alarmKeysArr() {
    //   this.$set(this.queryParams, 'alarmKeysArr', this.alarmKeysArr)
    //   this.onSearch()
    //   // this.$set(this.queryParams, 'alarmsCategoryArr', this.alarmsCategoryArr)
    // },
    filterText(val) {
      this.$refs.tree.filter(val)
    },
    'easySearch.EventCategoryLabelText': function(newValue, oldValue) {
      if (!this.easySearch.EventCategoryLabelText) {
        this.classIdName = ''
        this.queryParams.classIdList = ''
        this.onSearch()
      }
    },
    'active.tab': function(newValue, oldValue) {
      // console.log(newValue)
      if (newValue === 'first') {
        this.resetCondition1()
        this.onSearchTO24('>=')
      } else {
        this.resetCondition()
        this.onSearchTO24('<')
      }
    },
    'active1.tab': function(newValue, oldValue) {
      this.getPersonShow()
    },
    labelPosition: function(newValue, oldValue) {
      this.getPersonShow()
    },
    // 'easySearch.FirstOccurrence'
    'easySearch.FirstOccurrence': function(newValue, oldValue) {
      if (newValue) {
        this.queryParams.time1 = newValue[0] || ''
        this.queryParams.time2 = newValue[1] || ''
      } else {
        this.queryParams.time1 = ''
        this.queryParams.time2 = ''
      }

      this.onSearch()
    }
  },

  created() {
    if (this.$route.params.class_id) {
      this.$set(this.easySearch, 'Node', this.$route.params.name)
    }
    if (this.$route.params.name) {
      const temp = this.$route.params.name
      this.easySearch = {
        Manager: '',
        Severy: [],
        Summary: temp.Summary,
        EventCategoryLabelText: '',
        EventSeverityType: '',
        RecoveredStatus: temp.RecoveredStatus + '',
        Acknowledged: temp.Acknowledged + '',

        Node: temp.Summary
      }
    }

    const userStr = localStorage.getItem('user')
    const userObj = JSON.parse(userStr)

    const roleId = userObj['roleId']
    this.queryParams.projectId = localStorage.getItem('project_id')
    if (roleId) {
      this.getAlarmsCategory(roleId)
      this.querySeverity()
      this.active.tab === 'first'
      this.getPhysicalCategory()
      this.getTableColumns()
      // 获取事件来源
      this.getManager()
      // 获取权限设备
    } else {
      this.querySeverity()
      this.active.tab === 'first'
      this.getPhysicalCategory()
      this.getTableColumns()
      this.getEventList()
      // 获取事件来源
      this.getManager()
    }
  },
  beforeDestroy() {
    clearInterval(this.timer)
  },
  deactivated() {
    clearInterval(this.timer)
  },
  updated() {
    // this.momentDate = moment().format('x')
    this.getOperateBoxHeight()
  },
  mounted() {
    this.changeTime()
    // this.momentDate = moment().format('x')
    this.getOperateBoxHeight()
    this.timer = setInterval(this.getTimer, 60000)
  },
  methods: {
    // 定时调用方法
    getTimer() {
      // console.log('调用了定时器')
      this.momentDate = moment().format('x')
      this.getEventList()
    },
    openWindows(host) {
      window.open(host, '_blank')
    },
    // 跳转到设备详情页
    turnPage(row) {
      let proxy = null
      if (row.EventCategoryLabelText === 'WEB检测' || row.EventCategoryLabelText === 'PING检测') {
        // console.log(row)
        // proxy = row.Summary.split(':')[0].replace('发起设备', '') || null
        alarmTurnToHost({
          projectId: 10,
          proxy: row.HostOrProxy,
          Node: row.Node,
          className: row.EventCategoryLabelText,
          engine: 'Freemarker'
        }).then((response) => {
          const query = {
            name: {
              id: response.datas[0].id,
              clabel_text: response.datas[0].clabel_text,
              classId: response.datas[0].classId,
              host: row.Node,
              label_text: response.datas[0].label_text,
              res_ip_address: row.HostOrProxy
            }
          }
          this.$router.push({ name: '检测通用详情页', params: query })
        })
      } else if (row.EventCategoryLabelText === '进程检测') {
        proxy = row.Summary.split(':')[0].replace('发起设备', '') || null
        alarmTurnToHost({
          projectId: 10,
          proxy,
          Node: row.Node,
          className: row.EventCategoryLabelText,
          engine: 'Freemarker'
        }).then((response) => {
          const query = {
            name: {
              id: response.datas[0].id,
              clabel_text: response.datas[0].clabel_text,
              classId: response.datas[0].classId,
              host: row.Node,
              label_text: response.datas[0].label_text,
              res_ip_address: proxy
            }
          }
          this.$router.push({ name: '检测通用详情页', params: query })
        })
      } else if (row.EventCategoryLabelText === '虚拟机') {
        const query = {
          name: {
            classId: 265,
            host: row.Node,
            host_name: row.NodeAlias
          }
        }
        let tempPath = ''
        switch (row.AlertGroup) {
          case 'VMWare_VCenter_Vpoller_Hypervisor':
            tempPath = '虚拟化-宿主机'
            break
          case 'VMWare_VCenter_Vpoller_Virtual-Machine':
            tempPath = '虚拟化-VMware'
            break
          case 'VMWare_VCenter_Vpoller_Datastore':
            tempPath = '虚拟化-Datastore'
            break
        }
        if (tempPath) {
          this.$router.push({ name: tempPath, params: query || null })
        } else {
          this.msgInfo('设备未被纳管!')
        }
      } else {
        alarmTurnToHost({
          projectId: 10,
          proxy,
          Node: row.Node,
          className: row.EventCategoryLabelText,
          engine: 'Freemarker'
        }).then((response) => {
          // console.log(response)
          // return
          if (response.success) {
            if (response.datas.length) {
              const { host, res_vendor, label_text, classId, resourceId, res_ip_address, res_control_ip } = response.datas[0]
              const obj = {
                host,
                res_vendor,
                label_text,
                classId,
                clabel_text: row.EventCategoryLabelText,
                resourceId,
                res_ip_address,
                res_control_ip
              }
              const query = { name: obj }
              let tempPath = this.toRouter
              // 服务器
              switch (query.name.classId) {
                case 28:
                  tempPath = 'windows'
                  break
                case 29:
                  tempPath = 'Linux'
                  break
                case 253:
                  tempPath = '负载均衡'
                  break
                case 249:
                  tempPath = '路由器'
                  break
                case 55:
                  tempPath = '防火墙'
                  break
                case 252:
                  tempPath = '交换机'
                  break
                case 255:
                  tempPath = '光纤交换机'
                  break
                case 315:
                  tempPath = '博科光纤交换机'
                  break
                case 57:
                  tempPath = '数据库-MySQL'
                  break
                case 31:
                  tempPath = '数据库-Oracle'
                  break
                case 33:
                  tempPath = '数据库-SQL Server'
                  break
                case 265:
                  tempPath = '虚拟化-vc'
                  break
                case 259:
                  tempPath = '中间件-IIS'
                  break
                case 280:
                  tempPath = '华为硬件'
                  break
                case 281:
                  tempPath = '浪潮硬件'
                  break
                case 282:
                  tempPath = '惠普硬件'
                  break
                case 267:
                  tempPath = '存储-NetApp'
                  break
                case 269:
                  tempPath = '存储-华为'
                  break
                case 279:
                  tempPath = '存储-惠普'
                  break
                case 289:
                  tempPath = '上网行为管理'
                  break
                case 285:
                  tempPath = '无线控制器'
                  break
                case 36:
                  tempPath = '中间件-Tomcat'
                  break
                case 58:
                  tempPath = '中间件-Weblogic'
                  break
                case 63:
                  tempPath = '数据库-Redis'
                  break
                case 319:
                  tempPath = 'VPN'
                  break
                default:
                  tempPath = '通用设备详情页'
              }
              this.$router.push({ name: tempPath, params: query })
            } else {
              this.msgInfo('设备未被纳管!')
            }
          } else {
            this.msgError('获取设备信息出错!')
          }
        })
      }

      // 获取设备信息
    },
    getPhysicalCategory() {
      getPhysicalCategories({
        projectId: 10,
        engine: 'Freemarker',
        isEnable: 'Y'
      }).then((response) => {
        if (response.success) {
          this.deviceCategories = response.datas
          this.deviceCategories1 = JSON.parse(JSON.stringify(this.handleTree(response.datas, 'id')).replace(new RegExp('labelText', 'g'), 'label'))
          this.deviceCategories1.forEach((item, index) => {
            // console.log(item)
            this.deviceCategories1[index].id = item.label
            if (item.children) {
              this.deviceCategories1[index].disabled = true
              item.children.forEach((info, index1) => {
                this.deviceCategories1[index].children[index1].id = info.label
              })
            }
          })
        } else {
          this.msgError('查询失败设备类别：\n' + response.errorMsg)
        }
      })
    },
    handleNodeClick(data) {
      // console.log(data)
      const temp = []
      if (data.children) {
        if (data.children.length) {
          data.children.forEach((element) => {
            temp.push(element.id)
          })
        }
      }
      this.queryParams.classIdList = temp.length ? temp : ''
      // 这里主要配置树形组件点击节点后，设置选择器的值；自己配置的数据，仅供参考
      this.classIdName = data.label
      this.easySearch.EventCategoryLabelText = data.id
      // 选择器执行完成后，使其失去焦点隐藏下拉框的效果
      this.$refs.selectUpResId.blur()
      this.onSearch()
    },
    filterNode(value, data) {
      if (!value) return true
      const stringTemp = data.label.toLowerCase()
      const stringTemp1 = value.toLowerCase()
      return stringTemp.indexOf(stringTemp1) !== -1
    },
    getStr(val) {
      if (!val) {
        return
      }
      // console.log(val)
      if (val.length === 1) {
        return val.notification_types
          .replace('flow', '开单')
          .replace('email', '邮件')
          .replace('wechat', '融讯通')
          .replace('Wechat', '融讯通')
      } else {
        return val.notification_types
          .join(' 、')
          .replace('flow', '开单')
          .replace('email', '邮件')
          .replace('wechat', '融讯通')
          .replace('Wechat', '融讯通')
      }
      // val.notification_types = val.notification_types.join(',')
    },
    getManager() {
      getManager({
        projectId: 10
      }).then((response) => {
        // console.log(response)
        if (response.success && response.datas) {
          this.managerList = response.datas
        } else {
          this.msgInfo('当前没有事件来源，请联系管理员')
        }
      })
    },
    // 获取权限组
    getAlarmsCategory1(roleId) {
      this.loading = true
      getAlarmsCategory1({
        roleId,
        projectId: 10
      }).then((response) => {
        // console.log(response)
        if (response.success) {
          // this.alarmsCategoryArr1 = response.listData
          // this.onSearch()
          if (response.listData.length) {
            this.alarmsCategoryArr1 = response.listData
          } else {
            this.alarmsCategoryArr1 = null
          }
          this.getAlarmKeys(roleId)
        } else {
          this.alarmsCategoryArr1 = null
          this.getAlarmKeys(roleId)
          this.msgError('获取权限分组出错')
        }
      })
    },
    // 获取权限组
    getAlarmsCategory(roleId) {
      this.loading = true
      getAlarmsCategory({
        roleId,
        projectId: 10
      }).then((response) => {
        // console.log(response)
        if (response.success) {
          // this.alarmsCategoryArr = response.listData
          if (response.listData.length) {
            this.alarmsCategoryArr = response.listData
          } else {
            this.alarmsCategoryArr = null
          }
          // this.onSearch()
          this.getAlarmsCategory1(roleId)
        } else {
          this.getAlarmsCategory1(roleId)
          this.alarmsCategoryArr = null
          this.msgError('获取权限分组出错')
        }
      })
    },
    // 获取权限设备
    getAlarmKeys(roleId) {
      getAlarmKeys({
        roleId,
        projectId: 10
      }).then((response) => {
        // this.loading = false
        // console.log(response)
        if (response.success) {
          // this.managerList = response.datas
          // this.alarmKeysArr = response.listData
          if (response.listData.length) {
            this.alarmKeysArr = response.listData
          } else {
            this.alarmKeysArr = null
          }
          this.onSearch()
        } else {
          this.alarmKeysArr = null
          this.onSearch()
          this.msgError('获取权限设备出错')
        }
      })
    },
    getPerson() {
      getPerson({ projectId: this.queryParams.projectId, deviceKey: this.formData.row.Node, ifGroup: true }).then((res) => {
        // console.log(res)
        if (res.success && res.datas.length) {
          this.$set(this.formData, 'person', '')
          var temp = ''
          res.datas.forEach((item, index) => {
            if (index === 0) {
              temp += item.username
              this.$set(this.formData, 'person', temp)
            } else if (res.datas.length === index + 1) {
              temp += '#' + item.username
              this.$set(this.formData, 'person', temp)
            } else {
              temp += '#' + item.username
            }
          })
        } else {
          this.$set(this.formData, 'person', '')
          getPerson({ projectId: this.queryParams.projectId, deviceKey: this.formData.row.Node }).then((res1) => {
            if (res1.success && res1.datas.length) {
              this.$set(this.formData, 'person', '')
              var temp = ''
              res1.datas.forEach((item, index) => {
                if (index === 0) {
                  temp += item.username
                  this.$set(this.formData, 'person', temp)
                } else if (res1.datas.length === index + 1) {
                  temp += '#' + item.username
                  this.$set(this.formData, 'person', temp)
                } else {
                  temp += '#' + item.username
                }
              })
            } else {
              this.$set(this.formData, 'person', '')
              this.msgError('没有获取到对应对象')
            }
          })
        }
      })
    },
    changeTime() {
      const element = document.querySelector('button.el-picker-panel__icon-btn.el-icon-arrow-left')
      if (element) element.click()
    },
    exportSelected() {
      const queryParams = this.queryParams
      queryParams.eventIds = this.idSelections
      this.$confirm('是否确认导出所有数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(function() {
          return exportStatus(queryParams)
        })
        .then((response) => {
          const { code, msg } = response
          if (code !== 200) {
            this.msgInfo(msg)
          } else {
            this.download(msg)
          }
        })
        .catch(function() {})
    },
    getOrder(params) {
      // 如果排序规则为空则按默认排序，清空两个值
      if (!params.order) {
        this.queryParams.orderName = ''
        this.queryParams.orderType = ''
      } else {
        this.queryParams.orderType = params.order === 'ascending' ? 'asc' : 'desc'
        this.queryParams.orderName = params.prop
      }
      this.getEventList()
    },
    // 查询级别
    querySeverity() {
      getEventSeverityDefine({
        name: '',
        projectId: 10,
        start: 1,
        limit: 20
      }).then((response) => {
        // 赋值SeverityArr
        this.SeverityArr = response.datas
        this.SeverityArr = this.SeverityArr.filter((item) => item.isEnable === true)
      })
    },
    // 动态查询事件字段
    getTableColumns() {
      this.loading = true
      getEventColumnDefine({
        projectId: 10,
        engine: 'Freemarker',
        start: 1,
        limit: -1,
        columnTypes: ['S', 'C', 'U'],
        displayFlag: 'Y',
        // columnTypeModify: 'Y',
        isEnable: 'Y'
      }).then((response) => {
        this.tableColumns = response.datas
        // 给下拉项增加一个属性isSelect判断当前项是否已经被选择过，默认赋值false
        this.tableColumns.forEach((item) => (item.isSelect = false))
        // this.loading = false
      })
    },
    // 查询类型设置
    columnChange(index) {
      // 更换值的时候，先重置
      // console.log(JSON.stringify(this.conditions));
      this.conditions[index].value = null
      this.conditions[index].operator = null
      const arr = []
      this.conditions.forEach((item) => {
        arr.push(item.column)
      })
      this.tableColumns.forEach((item) => {
        item.isSelect = false
        // 判断当前下拉项要不要禁止点击
        if (arr.includes(item.columnInDB)) {
          item.isSelect = true
        }
        if (item.columnInDB === this.conditions[index].column) {
          // 判断当前dataType有没有值，没有的话直接赋值
          this.conditions[index].dataType = item.dataType
          // item.isSelect = !item.isSelect;
          if (item.dataType === 'INT' || item.dataType === 'FLOAT') {
            this.conditions[index].value = []
          }
        }
      })
    },
    // 新增查询条件
    addQureyCondition() {
      // 添加之前需要先判断前置项是否已经填写描述了，如果还是空白的，则不添加
      const index = this.conditions.length - 1
      if (this.conditions[index].column !== null && this.conditions[index].operator !== null && (this.conditions[index].value !== null || this.conditions[index].value === 0)) {
        this.conditions.push({ column: null, operator: null, value: null, dataType: null })
      } else {
        this.msgInfo('请先输入完成前置查询条件填写')
      }
    },
    // 删除查询条件
    deleteQureyCondition(index) {
      // 删除前先将下拉释放
      this.tableColumns.forEach((item) => {
        item.columnInDB === this.conditions[index].column ? (item.isSelect = false) : ''
      })
      if (this.conditions.length === 1) {
        this.conditions = [{ column: null, operator: null, value: null, dataType: null }]
      } else {
        this.conditions.splice(index, 1)
      }
      // 判断当前是不是只有一个查询条件
      // this.conditions.length === 1 ? "" : this.conditions.splice(index, 1);
    },
    // 表格格式处理
    tableColumnsFormat(row, column) {
      if (column.property === 'FirstOccurrence' || column.property === 'LastOccurrence') {
        // 处理日期格式
        var date = row[column.property]
        if (date === undefined) {
          return ''
        }
        return moment(date).format('YYYY-MM-DD HH:mm:ss')
      } else if (column.property === 'Severity') {
        // 处理级别格式
        const SeverityName = this.SeverityArr && this.SeverityArr.filter((item) => (item.mappingValue === row[column.property] ? item.name : ''))
        return SeverityName[0] && SeverityName[0].name
      } else if (column.property === 'FilterFlag') {
        const data = row[column.property]
        let returnText = ''
        try {
          const dataJson = JSON.parse(data)
          for (var i = 0; i < dataJson.length; i++) {
            if (dataJson[i] === 'NN') {
              returnText += '不通知~'
            } else if (dataJson[i] === 'NS') {
              returnText += '不分享~'
            } else if (dataJson[i] === 'NF') {
              returnText += '不开单~'
            }
          }
        } catch (e) {
          console.error(e.message)
        } finally {
          // eslint-disable-next-line no-unsafe-finally
          return returnText
        }
      } else {
        // 直接返回
        return row[column.property]
      }
    },
    // 24h
    resetCondition1() {
      // this.momentDate = moment().format('x')
      this.easySearch = {
        Manager: '',
        Severy: [4, 5],
        Summary: '',
        EventCategoryLabelText: '',
        EventSeverityType: '',
        RecoveredStatus: '1',
        Acknowledged: '',
        FirstOccurrence: '',
        Node: ''
      }
      this.queryParams = {
        projectId: 10,
        engine: 'Freemarker',
        start: 1,
        limit: 20,
        Node: '',
        EventSeverityType: '',
        RecoveredStatus: '1',
        time1: null,
        time2: null,
        Severy: [4, 5],
        Acknowledged: '',
        orderName: '',
        orderType: ''
      }
      this.conditions = [{ column: null, operator: null, value: null, dataType: null }]
      this.getTableColumns()
      this.getEventList()
    },
    resetCondition2() {
      // this.momentDate = moment().format('x')
      this.easySearch = {
        Manager: '',
        Severy: [],
        Summary: '',
        EventCategoryLabelText: '',
        EventSeverityType: '',
        RecoveredStatus: '',
        Acknowledged: '',
        FirstOccurrence: '',
        Node: ''
      }
      this.queryParams = {
        projectId: 10,
        engine: 'Freemarker',
        start: 1,
        limit: 20,
        Node: '',
        EventSeverityType: '',
        RecoveredStatus: '',
        time1: null,
        time2: null,
        Severy: [],
        Acknowledged: '',
        orderName: '',
        orderType: ''
      }
      this.conditions = [{ column: null, operator: null, value: null, dataType: null }]
      this.getTableColumns()
      this.getEventList()
    },
    resetCondition() {
      // this.momentDate = moment().format('x')
      this.easySearch = {
        Manager: '',
        Severy: '',
        Summary: '',
        EventCategoryLabelText: '',
        EventSeverityType: '',
        RecoveredStatus: '',
        Acknowledged: '',
        FirstOccurrence: '',
        Node: ''
      }
      this.queryParams = {
        projectId: 10,
        engine: 'Freemarker',
        start: 1,
        limit: 20,
        Node: '',
        EventSeverityType: '',
        RecoveredStatus: '',
        time1: null,
        time2: null,
        Severy: '',
        Acknowledged: '',
        orderName: '',
        orderType: ''
      }
      this.conditions = [{ column: null, operator: null, value: null, dataType: null }]
      this.getTableColumns()
      this.getEventList()
    },
    getEventList() {
      this.loading = true
      let isValid = true
      if (this.conditions.length > 0) {
        if (this.conditions[0].column) {
          this.conditions.map((condition) => {
            if (condition.operator === 'between' && (condition.dataType === 'INT' || condition.dataType === 'FLOAT')) {
              if (condition.value0 > condition.value1 || condition.value1 === '' || condition.value1 === undefined || condition.value1 == null) {
                this.dataList = []
                this.msgError('查询失败：\n条件设置错误，结束值小于开始值！')
                this.loading = false
                isValid = false
                return
              }
              condition.value = [condition.value0, condition.value1]
            }
          })
          this.queryParams.conditions = this.conditions
        }
      } else {
        this.queryParams.conditions = []
      }

      if (this.active.tab === 'first') {
        // this.onSearchTO24('>=')
        this.easySearch.operatorTime = '>='
      } else {
        // this.onSearchTO24('<')
        this.easySearch.operatorTime = '<'
      }
      const userStr = localStorage.getItem('user')
      const userObj = JSON.parse(userStr)
      // const userId = userObj['userId']
      const roleId = userObj['roleId']
      if (roleId) {
        this.$set(this.queryParams, 'alarmKeysArr', this.alarmKeysArr)
        this.$set(this.queryParams, 'alarmsCategoryArr', this.alarmsCategoryArr)
        this.$set(this.queryParams, 'alarmsCategoryArr1', this.alarmsCategoryArr1)
      }
      this.queryParams.eventIds = null
      this.queryParams.EventCategoryLabelText = this.easySearch.EventCategoryLabelText
      this.queryParams.Severy = this.easySearch.Severy
      this.queryParams.Acknowledged = this.easySearch.Acknowledged
      this.queryParams.RecoveredStatus = this.easySearch.RecoveredStatus
      this.queryParams.EventSeverityType = this.easySearch.EventSeverityType
      this.queryParams.Manager = this.easySearch.Manager
      this.queryParams.Summary = this.easySearch.Summary
      this.queryParams.time1 = this.easySearch.FirstOccurrence[0] || ''
      this.queryParams.time2 = this.easySearch.FirstOccurrence[1] || ''
      this.queryParams.Node = this.easySearch.Node
      this.queryParams.nowDate = moment()
        .subtract(1, 'days')
        .format('x')
      // 判断是否有角色

      // console.log(this.queryParams)
      this.queryParams.operatorTime = this.easySearch.operatorTime
      if (isValid) {
        getEventsForRealtime({ ...this.queryParams }).then((response) => {
          // console.log(this.queryParams, 1)
          this.loading = false
          if (response.success) {
            // console.log(response)
            this.dataList = response.datas
            this.totalCount = response.totalCount
          } else {
            this.dataList = []
            this.msgError('查询失败：\n' + response.errorMsg)
          }
        })
      }
    },
    // 获取页面高度
    getOperateBoxHeight() {
      this.$nextTick(function() {
        this.OperateBoxHeight = document.getElementsByClassName('app-container')[0].offsetHeight - document.getElementsByClassName('operateBox')[0].offsetHeight - 10
      })
    },
    // 查询方法
    onSearchTO24(val) {
      this.easySearch.FirstOccurrence = []
      this.easySearch.operatorTime = val || null
      this.easySearch.nowDate = moment()
        .subtract(1, 'days')
        .format('x')
      // this.easySearch.FirstOccurrence[1] = moment().valueOf()
      this.onSearch()
    },
    onSearch() {
      this.momentDate = moment().format('x')
      if (this.conditions.length) {
        const index = this.conditions.length - 1
        const lastItem = this.conditions[index]
        if (!(lastItem.column && lastItem.operator && (lastItem.value || lastItem.value === 0) && lastItem.dataType) && this.searchflag) {
          this.msgError('请先将查询条件填充完整')
          return
        }
      }
      this.getEventList()
    },
    handleSelectionChange(selection) {
      this.idSelections = selection.map((item) => item.EventID)
      this.tableSelections = selection
    },
    // 删除方法
    batchDelete(row) {
      this.$confirm('是否关闭选中的事件', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'info' }).then(() => {
        batchCloseEvents({
          projectId: this.queryParams.projectId,
          ids: this.idSelections
        }).then((response) => {
          if (response.success) {
            this.getEventList()
          } else {
            this.msgError('删除失败：\n' + response.errorMsg)
          }
        })
      })
    },
    /* batchAcknoledge() {
      this.$confirm("是否确认选中的事件", "警告", {confirmButtonText: "确定",cancelButtonText: "取消",type: "warning"}).then(() => {
        acknoledgeEvents({projectId: 10,engine: "Freemarker",condition: { EventID: this.tableSelections },value: { Acknoledged: 1 }}).then((response) => {
          this.getEventList();
          console.log(JSON.stringify(response));
        });
      });
    },*/
    resetForm(formName) {
      this.formShow = false
      // 重置备注和通知方式
      this.formData.notifyType = []
      this.formData.remark = ''
      this.$refs['formData'].resetFields()
    },
    resetForm1(formName) {
      // console.log(1)
      this.formShow1 = false
      // 重置备注和通知方式
      this.confirmObj = {
        work_order_id: ''
      }
      this.formData.notifyType = []
      this.formData.remark = ''
      this.$refs['formData'].resetFields()
    },
    openDialog(row) {
      if (row.Acknowledged === 0) {
        this.handleUpdate(row)
      } else {
        this.handleUpdate1(row)
      }
    },
    // 修改方法
    handleUpdate(row) {
      this.formTitle = row.Acknowledged === 0 ? '确认' : '查看'
      this.formShow = true
      this.formData.row = row
      this.$set(this.formData, 'notifyType', ['email'])
      this.$set(this.formData, 'person', '')
      this.$set(this.formData, 'projectId', this.queryParams.projectId)
      /* this.batchFormShow = true;
      this.batchFormData.events=this.tableSelections;
      this.$set(this.batchFormData,"notifyType",['ON']);
      this.$set(this.batchFormData,"projectId",this.queryParams.projectId);*/
    },
    handleUpdate1(row) {
      this.formShow1 = true
      this.confirmObj = {
        work_order_id: ''
      }
      this.labelPosition = 'left'
      this.active1.tab = 'one'
      this.formData.row = row

      this.$set(this.formData, 'notifyType', ['email'])
      this.$set(this.formData, 'person', '')
      this.$set(this.formData, 'projectId', this.queryParams.projectId)
      this.getPersonShow()
    },
    compare: function(prop) {
      return function(obj1, obj2) {
        var val1 = obj1[prop]
        var val2 = obj2[prop]
        if (val1 < val2) {
          return -1
        } else if (val1 > val2) {
          return 1
        } else {
          return 0
        }
      }
    },

    getPersonShow() {
      // 获取确认记录
      this.confirmObj = {
        work_order_id: '',
        url: ''
      }
      this.tableData = []
      getPersonShow({ projectId: this.queryParams.projectId, EventID: this.formData.row.EventID }).then((res) => {
        // console.log(res)
        if (res.success && res.datas.length) {
          // 确认人赋值
          this.confirmObj = res.datas[0]
          // 表格赋值
          if (this.confirmObj.candidate) {
            // console.log(this.confirmObj.candidate)
            this.tableData = JSON.parse(this.confirmObj.candidate)
            this.tableData = this.tableData.filter((item) => {
              // console.log(item)
              if (item.notification_types.length > 0) {
                return item
              }
            })
          }

          // 判断是否有流程id
          if (this.confirmObj.work_order_id) {
            // 请求工单
            const they_ = this
            // http://itsm.chamc.com.cn/e3-bpm
            var url = 'http://itsm.chamc.com.cn/e3-bpm/openapi/instance/getCheckOpinion?token=iqJNiSVub6&workOrderId=' + this.confirmObj.work_order_id
            axios.post(url).then(function(res) {
              // console.log(res)
              if (res.status === 200) {
                they_.activities = []
                // arr.sort(compare("age"))
                const temp = res.data.data.rows.sort(they_.compare('completeTime'))
                they_.activities = [...temp]
                // console.log()
              }
            })
          }
          this.showCheck = true
        } else {
          this.tableData = []
          this.showCheck = false
        }
        // else {
        //   // this.msgError('没有获取到对应对象')
        // }
      })
    },
    submitForm() {
      this.$refs['formData'].validate((valid) => {
        if (valid) {
          this.formData.events = [this.formData.row]
          // const temp = this.formData.person
          // this.formData.candidate = {}
          // if (temp.indexOf('#') === -1) {
          //   this.$set(this.formData.candidate, 'candidate', temp)
          // } else {
          // this.$set(this.formData.candidate, 'candidates', temp)
          // }
          this.formData.notificationType = {
            effectColumn: '',
            effectDataType: '',
            effectType: this.formData.notifyType,
            effectValue: this.formData.person
          }
          this.formData.acknoledger = JSON.parse(localStorage.getItem('roles'))[0]
          pushEventToNotifyService(this.formData).then((response3) => {
            if (response3.success) {
              batchCloseEvents({
                projectId: this.queryParams.projectId,
                ids: this.formData.row.EventID
              }).then((response) => {
                if (response.success) {
                  this.formShow = false
                  this.getEventList()
                } else {
                  this.msgError('删除失败：\n' + response.errorMsg)
                }
              })
            } else {
              this.msgError('获取事件推送地址失败：\n' + response3.errorMsg)
            }
          })
          /* }else{
                    this.msgError("获取事件推送地址失败：\n配置[notify_share]中[notify_share_receive_address]配置项不存在！");
                  }
                }else{
                  this.msgError("获取事件推送地址失败：\n"+response2.errorMsg);
                }
              });*/
        }
      })
    },
    resetBatchForm(formName) {
      this.batchFormShow = false
      // this.batchFormData={};
      // 重置备注和通知方式
      this.formData.notifyType = []
      this.formData.remark = ''
      this.$refs['batchFormData'].resetFields()
    },
    handleBatchRecoveredStatus() {
      // this.$confirm('是否删除选中的事件', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'info' }).then(() => {
      for (var i = 0; i < this.tableSelections.length; i++) {
        if (this.tableSelections[i].Acknowledged === 0) {
          // this.msgWarning('选中的事件包括确认告警事件，这些未确认的告警事件将不会被删除')
          this.$message({
            message: '选中的事件包括未确认事件，请重新选择',
            type: 'warning'
          })
          return
        }
        if (this.tableSelections[i].RecoveredStatus === 2) {
          // this.msgWarning('选中的事件包括确认告警事件，这些未确认的告警事件将不会被删除')
          this.$message({
            message: '选中的事件包括已恢复事件，请重新选择',
            type: 'warning'
          })
          return
        }
      }
      // batchCloseEventsForWeb
      this.$confirm('是否手动恢复选中的事件', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'info' }).then(() => {
        batchCloseEventsForWeb({
          projectId: this.queryParams.projectId,
          ids: this.idSelections
        }).then((response) => {
          if (response.success) {
            this.getEventList()
          } else {
            this.msgError('恢复操作失败：\n' + response.errorMsg)
          }
        })
      })
      // this.tableSelections
    },
    // 修改方法
    handleBatchAcknoledge() {
      // this.$confirm('是否删除选中的事件', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'info' }).then(() => {
      for (var i = 0; i < this.tableSelections.length; i++) {
        if (this.tableSelections[i].Acknowledged === 1) {
          // this.msgWarning('选中的事件包括确认告警事件，这些未确认的告警事件将不会被删除')
          this.$message({
            message: '选中的事件包括已确认事件，请重新选择',
            type: 'warning'
          })
          return
        }
      }
      this.batchFormShow = true
      this.batchFormData.events = this.tableSelections
      this.$set(this.formData, 'person', '')
      this.$set(this.batchFormData, 'notifyType', ['email'])
      this.$set(this.batchFormData, 'projectId', this.queryParams.projectId)
      // })
    },
    submitBatchForm() {
      this.$refs['batchFormData'].validate((valid) => {
        if (valid) {
          this.batchFormData.acknoledger = JSON.parse(localStorage.getItem('roles'))[0]
          // const temp = this.formData.person
          // this.formData.candidate = {}
          // if (temp.indexOf('#') === -1) {
          //   this.$set(this.formData.candidate, 'candidate', temp)
          // } else {
          // this.$set(this.formData.candidate, 'candidates', temp)
          // }
          this.formData.notificationType = {
            effectColumn: '',
            effectDataType: '',
            effectType: this.formData.notifyType,
            effectValue: this.formData.person
          }
          return pushEventToNotifyService(this.batchFormData).then((response) => {
            if (response.success) {
              batchCloseEvents({
                projectId: this.queryParams.projectId,
                ids: this.idSelections
              }).then((response2) => {
                if (response2.success) {
                  this.batchFormShow = false
                  this.getEventList()
                } else {
                  this.msgError('删除失败：\n' + response2.errorMsg)
                }
              })
            } else {
              this.msgError('获取事件推送地址失败：\n' + response.errorMsg)
            }
          })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import 'src/assets/styles/index';
// 表格样式修改
// .el-table__body{
//   height: calc(100%-50px)!important;
//   overflow: auto!important;
// }
.light .operateBox .operateBoxForm2 .el-form-item {
  margin-bottom: 6px;
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
.operateBox {
  .itemQueryInfo {
    margin: 0px 0px 5px 0;
    .el-form-item {
      width: 100% !important;
      /deep/.el-form-item__content {
        width: 100% !important;
      }
    }
  }
  .marginTen {
    margin-bottom: 10px;
    i {
      cursor: pointer;
      span {
        font-size: 10px;
        color: #1890ff;
        vertical-align: bottom;
      }
    }
  }
  i {
    line-height: 2rem;
  }
  .el-icon-circle-plus-outline {
    color: #1890ff;
  }
  .el-icon-delete {
    color: #7f819d;
  }
}
.dark .el-dialog,
.light .el-dialog {
  /deep/.el-dialog__body {
    max-height: 64vh;
  }
}
@media screen and (max-width: 1919px) {
  .itemQueryInfo {
    width: 80%;
    .el-form-item__content {
      width: 100% !important;
    }
  }
}
.dark,
.light {
  .el-dialog__body {
    max-height: 64vh !important;
    .el-form-item {
      margin-bottom: 12px !important;
    }
  }
}
.dark,
.light {
  .tabsDialog {
    /deep/.el-dialog__body {
      padding: 5px;
      min-height: 60vh !important;
      .rightBox {
        padding: 20px;
        // height: 95%;
        overflow: auto;

        .el-timeline {
          padding-left: 130px;

          .el-timeline-item__content {
            display: inline-block;
            color: #fff;
            margin-bottom: 30px;
            line-height: 20px;
            // height: 90px!important;
          }
          .el-timeline-item:last-child {
            // height:70px!important
            .el-timeline-item__content {
              display: inline-block;
              margin-bottom: 0px;
            }
          }

          /deep/.el-timeline-item__tail {
            border-left: 1px dotted #10aaf4;
          }
          /deep/.el-timeline-item__icon {
            // color: #03a9f4;
            font-size: 18px;
          }
          .timer {
            position: absolute;
            left: -145px;
            top: 1px;
            color: #5588ed;
            font-family: 'number';
            font-size: 16px;
          }
          .content {
            width: 98%;
            position: absolute;
            top: 1px;
            font-weight: 300;
            // height: max-content;
            // height: 100%;
            > span:first-child {
              //  color: #5588ed;

              color: #5588ed;
              font-weight: 700;
              font-size: 14px;
            }
            > span:nth-child(2) {
              font-family: 'number';
              font-size: 16px;
            }

            .contentBox {
              display: -webkit-box;
              overflow: hidden;
              text-overflow: ellipsis;
              word-wrap: break-word;
              white-space: normal !important;
              -webkit-line-clamp: 2;
              -webkit-box-orient: vertical;
            }
          }
        }
      }
    }
    /deep/.el-radio-button__inner {
      color: #5588ed;
      background: transparent;
      padding: 6px 10px;
      border: 1px solid #5588ed;
    }
    /deep/.el-radio-button__orig-radio:checked + .el-radio-button__inner {
      color: #fff;
      background: #5588ed;
      border: 1px solid #5588ed;
    }
    /deep/.el-radio-button:first-child .el-radio-button__inner {
      border: 1px solid #5588ed;
    }
  }
}

.dark {
  .tabsDialog {
    .content {
      color: #dfe4ed;
    }
  }
}
.light {
  .tabsDialog {
    .content {
      color: #333;
    }
  }
}
.tagLink {
  background-color: transparent;
  border-color: transparent;
  height: 20px;
  // padding: 0 6px;
  line-height: 20px;
  font-size: 12px;
  cursor: pointer;
  color: #c0d0d0;
  i {
    color: rgba(24, 144, 255, 1);
  }
}
</style>
