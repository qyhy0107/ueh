<template>
  <div class="app-container">
    <div v-if="ww" class="specialBox specialBox1 overviewBox Synopsis">
      <!-- 设备状况 -->
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <span>设备状况</span>
        </div>
        <div class="topLeft">
          <div class="topLeft-item" style="cursor: pointer;" @click="turnPage2('自定义监控', { class_id: 'PING' })">
            <span>{{ totalAssets }}</span>
            <span>被检测设备总数</span>
          </div>
          <div class="topLeft-item" style="cursor: pointer;" @click="addScroll(1)">
            <span>{{ alarmServerCount }}</span>
            <span>被检测设备告警数</span>
          </div>
          <div class="topLeft-item">
            <span>{{ assetsNotAlerted }}</span>
            <span>被检测设备未告警数</span>
          </div>
        </div>
      </el-card>
      <!-- 告警分布 -->
      <el-card class="box-card alarmBox">
        <div slot="header" class="clearfix">
          <span>告警分布</span>
        </div>
        <el-row style="height: 100%">
          <el-col :span="12">
            <el-row class="lineBox">
              <el-col class="numCount numCount1" style="cursor: pointer;">
                <span @click="addScroll(0)">
                  <!-- <svg-icon icon-class="gaojing" style="margin-right: 4px" /> -->
                  <i class="el-icon-thumb" style="margin-right: 4px 2px;color:rgb(24, 144, 255)" />
                  告警总数
                </span>
                <span>{{ recoverAlarm + unrecoveredAlarm }}</span>
                <!-- <svg-icon icon-class="gaojing" style="margin-right: 4px" /> -->
              </el-col>
              <el-col class="numCount3" :span="24" style="cursor: pointer;">
                <span @click="addScroll(2)">
                  <el-row>
                    <el-col :span="12">
                      <span>已恢复</span>
                    </el-col>
                    <el-col :span="12">
                      <span style="float: right; color:#1890ff">{{ recoverAlarm }}</span>
                    </el-col>
                    <el-col>
                      <el-progress v-if="!isNaN(parseInt(recoverAlarmPercent))" :text-inside="true" :stroke-width="16" status="success" :percentage="recoverAlarmPercent" />
                      <el-progress v-else :text-inside="true" :stroke-width="16" status="success" :percentage="0" />
                    </el-col>
                  </el-row>
                </span>
              </el-col>
              <el-col class="numCount3" :span="24" style="cursor: pointer;">
                <span @click="addScroll(1)">
                  <el-row>
                    <el-col :span="12">
                      <span>未恢复</span>
                    </el-col>
                    <el-col :span="12">
                      <span style="float: right; color:#1890ff">{{ unrecoveredAlarm }}</span>
                    </el-col>
                    <el-col>
                      <el-progress v-if="!isNaN(parseInt(unrecoveredAlarmPercent))" status="warning" :text-inside="true" :stroke-width="16" :percentage="unrecoveredAlarmPercent || 0" />
                      <el-progress v-else :text-inside="true" :stroke-width="16" status="success" :percentage="0" />
                    </el-col>
                  </el-row>
                </span>
              </el-col>
            </el-row>
          </el-col>
          <el-col v-if="ww" :span="12" style="height: 100%">
            <common-chart :chart-data="radarData" chart-height="100%" />
          </el-col>
        </el-row>
      </el-card>
      <!-- 使用率top5 -->
      <el-card v-if="activeName" class="box-card topFiveBox">
        <el-tabs v-model="activeName" v-loading="top5loading" type="card">
          <el-tab-pane v-for="item in top_list" :key="item.topN_title" :label="item.topN_title" :name="item.topN_title">
            <div v-if="activeName == item.topN_title && linedata.options.length > 0" style="height: 100%">
              <common-chart :chart-data="linedata" :chart-height="ww > 1400 ? '100%' : '140px'" :is-router="true" :class_id="class_id" :class-id-list="kpiDatasetName.classIdList" />
            </div>
            <div v-else-if="!top5loading" style="height:130px">
              <Deficiency height="70px" width="auto" />
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-card>
      <el-card v-else class="box-card topFiveBox">
        <div style="height:100%" @click="turnPage2('TopN配置')">
          <Deficiency height="70px" width="auto" :icon-img="true" :lableshow="'请点击前往配置TopN'" />
        </div>
      </el-card>
      <!-- 设备列表 -->
      <el-card id="scrolldIV" class="box-card">
        <el-collapse v-model="activeCollapse">
          <el-collapse-item title="设备列表" name="1">
            <div class="collapseBox">
              <el-form :inline="true" label-position="left" style="height:30px!important" @submit.native.prevent>
                <el-form-item>
                  <el-tooltip class="item" effect="dark" :content="show1 ? '点击隐藏查询区域' : '点击显示查询区域'" placement="top">
                    <i :class="{ 'el-icon-s-unfold': !show1, 'el-icon-s-fold': show1 }" @click="show1 = !show1" />
                  </el-tooltip>
                </el-form-item>
                <transition name="el-zoom-in-center">
                  <el-form-item v-if="show1" label="名称/发起设备/被检测设备:">
                    <el-input v-model="resourceName" size="mini" clearable placeholder="请输入查询内容" @keyup.enter.native="findByresourceName" />
                  </el-form-item>
                </transition>
                <transition v-if="show1" name="el-zoom-in-center">
                  <el-form-item label="运行状态:">
                    <el-select v-model="operational_status" size="mini" clearable @change="findByresourceName">
                      <el-option label="良好" value="1" />
                      <el-option label="异常" value="0" />
                    </el-select>
                  </el-form-item>
                </transition>
                <transition v-if="show1" name="el-zoom-in-center">
                  <el-form-item>
                    <el-button size="mini" type="primary" plain @click="findByresourceName">查询</el-button>
                    <el-button
                      size="mini"
                      @click="
                        class_id_son = ''
                        resourceName = ''
                        operational_status = ''
                        findByresourceName()
                      "
                      >重置</el-button
                    >
                  </el-form-item>
                </transition>
              </el-form>
            </div>
            <div class="tableBox-right">
              <el-table v-loading="loading1" :data="dataList" empty-text=" " :height="ww > 1400 ? 410 : 265" element-loading-text="拼命加载中..." border @row-dblclick="turnPage" @sort-change="getOrder">
                <template v-if="!loading1" slot="empty" style="height:100%">
                  <Deficiency height="40%" width="auto" />
                </template>
                <el-table-column align="left" label="名称" prop="monitor_name" :show-overflow-tooltip="true" sortable="custom" min-width="15%">
                  <template slot-scope="scope">
                    <el-tooltip class="item" effect="dark" content="点击跳转" placement="top">
                      <span class="tagLink" @click="turnPage(scope.row)">
                        <i class="el-icon-paperclip" />
                        {{ scope.row.monitor_name }}
                      </span>
                    </el-tooltip>
                  </template>
                </el-table-column>
                <el-table-column align="left" label="运行状态" prop="PINGSTATUS" :show-overflow-tooltip="true" min-width="10%" sortable="custom">
                  <template slot-scope="scope">
                    <span v-if="scope.row.PINGSTATUS == 1" class="spanbg1">良 好</span>
                    <span v-else class="spanbg3">异 常</span>
                  </template>
                </el-table-column>
                <el-table-column align="left" label="被检测设备" prop="monitored_host" :show-overflow-tooltip="true" sortable="custom" min-width="10%" />
                <!-- <template slot-scope="scope">
                    <el-tooltip class="item" effect="dark" content="点击跳转" placement="top">
                      <span class="tagLink" @click="turnPage(scope.row)">
                        <i class="el-icon-paperclip" />
                        {{ scope.row.monitored_host }}
                      </span>
                    </el-tooltip>
                  </template>
                </el-table-column> -->
                <el-table-column align="left" label="发起设备" prop="monitored_proxy" :show-overflow-tooltip="true" sortable="custom" min-width="10%" />
                <el-table-column align="left" label="响应状态" prop="PING_INFO_RESPONSE_STATUS" :show-overflow-tooltip="true" sortable="custom" min-width="10%">
                  <template slot-scope="scope">
                    {{ scope.row.PING_INFO_RESPONSE_STATUS === 0 ? 'Down' : scope.row.PING_INFO_RESPONSE_STATUS === 1 ? 'Up' : '-' }}
                  </template>
                </el-table-column>
                <el-table-column align="left" label="丢包率(%)" prop="PING_RSP_LOSS" :show-overflow-tooltip="true" sortable="custom" min-width="10%">
                  <template slot-scope="scope">
                    {{ scope.row.PING_RSP_LOSS | toDecimal }}
                  </template>
                </el-table-column>
                <el-table-column align="left" label="响应时长(ms)" prop="PING_RSP_RESPONSE_TIME" :show-overflow-tooltip="true" sortable="custom" min-width="10%">
                  <template slot-scope="scope">
                    {{ scope.row.PING_RSP_RESPONSE_TIME | toDecimal }}
                  </template>
                </el-table-column>
                <el-table-column align="left" label="最后一次检测时间" prop="PING_INFO_LAST_CHECK_TIMESTAMP" :show-overflow-tooltip="true" sortable="custom" min-width="15%">
                  <template slot-scope="scope">
                    {{ scope.row.PING_INFO_LAST_CHECK_TIMESTAMP | getStartDate }}
                  </template>
                </el-table-column>
                <!-- 传入不同的指标插槽 -->
                <slot name="tableColumn" />
              </el-table>
              <pagination v-show="total > 0" :page-size="7" :pager-count="5" :total="total" :page-sizes="[10, 20, 50, 200]" :page.sync="queryParams.start" :limit.sync="queryParams.limit" @pagination="getResource()" />
            </div>
          </el-collapse-item>
          <el-collapse-item title name="2">
            <div slot="title">告警列表</div>
            <div class="collapseBox">
              <el-form :inline="true" label-position="left" style="height:30px!important" @submit.native.prevent>
                <el-form-item>
                  <el-tooltip class="item" effect="dark" :content="show1 ? '点击隐藏查询区域' : '点击显示查询区域'" placement="top">
                    <i :class="{ 'el-icon-s-unfold': !show2, 'el-icon-s-fold': show2 }" @click="show2 = !show2" />
                  </el-tooltip>
                </el-form-item>
                <transition name="el-zoom-in-center">
                  <el-form-item v-show="show2" label="设备标识/名称:">
                    <el-input v-model="name" size="mini" clearable placeholder="请输入查询内容" @keyup.enter.native="getAlarmList" />
                  </el-form-item>
                </transition>
                <transition name="el-zoom-in-center">
                  <el-form-item v-show="show2" label="级别:">
                    <el-select v-model="level" size="mini" collapse-tags multiple clearable style="width:130px" @change="getAlarmList">
                      <el-option v-for="item in alarmLevel" :key="item.text" :label="item.text" :value="item.text" />
                    </el-select>
                  </el-form-item>
                </transition>
                <transition name="el-zoom-in-center">
                  <el-form-item v-show="show2" label="事件类型:">
                    <el-select v-model="isEventSeverityType" size="mini" clearable style="width:90px" @change="getAlarmList">
                      <el-option value="2" label="恢复事件" />
                      <el-option value="1" label="告警事件" />
                    </el-select>
                  </el-form-item>
                </transition>
                <transition name="el-zoom-in-center">
                  <el-form-item v-show="show2" label="确认:">
                    <el-select v-model="isAcknowledged" size="mini" style="width:100px" clearable @change="getAlarmList">
                      <el-option label="已确认" :value="1" />
                      <el-option label="未确认" :value="0" />
                    </el-select>
                  </el-form-item>
                </transition>
                <transition name="el-zoom-in-center">
                  <el-form-item v-show="show2" label="恢复:">
                    <el-select v-model="isRecoveredStatus" size="mini" style="width:100px" clearable @change="getAlarmList">
                      <el-option label="已恢复" :value="2" />
                      <el-option label="未恢复" :value="1" />
                    </el-select>
                  </el-form-item>
                </transition>
                <transition name="el-zoom-in-center">
                  <el-form-item v-show="show2">
                    <el-button size="mini" type="primary" plain @click="getAlarmList">查询</el-button>
                    <el-button
                      size="mini"
                      @click="
                        name = ''
                        level = ''
                        isComponent = ''
                        isAcknowledged = ''
                        isEventSeverityType = ''
                        isRecoveredStatus = ''
                        queryParamsAlarm.start = 1
                        getAlarmList()
                      "
                      >重置</el-button
                    >
                    <el-button class="addButton" size="mini" @click="handleBatchAcknoledge">批量确认</el-button>
                  </el-form-item>
                </transition>
                <span type="text" size="mini" style="background:none;color:#5588ed;cursor: pointer;float:right;margin-top: 5px;" @click="turnPage3('实时告警', { class_id: class_id, name: name, level: level, isComponent: isComponent, isEventSeverityType: '' })">更多>></span>
              </el-form>
            </div>
            <div class="tableBox">
              <div class="tableBox-right">
                <el-table :key="Math.random()" v-loading="loading" border :height="ww > 1400 ? 380 : 250" :data="dataListAlarm" empty-text=" " element-loading-text="拼命加载中..." @row-dblclick="turnPage1" @selection-change="handleSelectionChange">
                  <template v-if="!loading" slot="empty" style="height:100%">
                    <Deficiency height="40%" width="auto" />
                  </template>
                  <!-- <template name="el-fade-in-linear"> -->
                  <el-table-column v-if="show2" :key="Math.random()" type="selection" align="left" :selectable="checkSelectable" :show-overflow-tooltip="true" min-width="2%" />
                  <el-table-column :key="Math.random()" align="left" label="级别" prop="SeverityName" :show-overflow-tooltip="true" min-width="5%">
                    <template slot-scope="scope">
                      <span :style="{ color: scope.row.SeverityName == '未知' ? '#0587ff' : scope.row.SeverityName == '通知' || scope.row.SeverityName == '警告' ? '#67c23a' : scope.row.SeverityName == '问题' || scope.row.SeverityName == '严重' ? '#e6a23c' : '#f44336' }">{{ scope.row.SeverityName }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column :key="Math.random()" align="left" label="分组" prop="EventCategoryLabelText" :show-overflow-tooltip="true" min-width="8%">
                    <!-- <template slot-scope="scope"></template> -->
                  </el-table-column>
                  <el-table-column :key="Math.random()" align="left" label="设备标识" prop="Node" :show-overflow-tooltip="true" min-width="10%">
                    <template slot-scope="scope">
                      <el-tooltip class="item" effect="dark" content="点击跳转" placement="top">
                        <span class="tagLink" @click="turnPage1(scope.row)">
                          <i class="el-icon-paperclip" />
                          {{ scope.row.Node }}
                        </span>
                      </el-tooltip>
                    </template>
                  </el-table-column>
                  <el-table-column :key="Math.random()" align="left" label="显示名称" prop="NodeAlias" :show-overflow-tooltip="true" min-width="13%" />
                  <el-table-column :key="Math.random()" align="left" label="发生时间" prop="FirstOccurrence" :how-overflow-tooltip="true" min-width="13%" />
                  <!-- <el-table-column :key="Math.random()" align="left" label="事件类型" prop="isComponent" :show-overflow-tooltip="true" width="90" /> -->
                  <el-table-column :key="Math.random()" align="left" prop="EventSeverityType" label="事件类型" :show-overflow-tooltip="true" min-width="7%">
                    <template slot-scope="scope">
                      <span v-if="scope.row.EventSeverityType == 1">
                        告警事件
                      </span>
                      <span v-if="scope.row.EventSeverityType == 2">
                        恢复事件
                      </span>
                    </template>
                  </el-table-column>
                  <el-table-column :key="Math.random()" align="left" label="恢复" prop="RecoveredStatus" :show-overflow-tooltip="true" min-width="6.5%" />
                  <el-table-column :key="Math.random()" align="left" label="确认" prop="Acknowledged" :show-overflow-tooltip="true" min-width="6.5%" />
                  <el-table-column :key="Math.random()" align="left" label="描述" prop="Summary" :show-overflow-tooltip="true" min-width="18%" />
                  <el-table-column v-if="true" :key="Math.random()" align="center" label="操作" prop="concern" :show-overflow-tooltip="true" min-width="11%">
                    <template slot-scope="scope">
                      <el-button v-if="scope.row.Acknowledged === '未确认'" type="text" size="mini" icon="el-icon-edit" style="background:none;border:1px solid none;color:#5588ed" @click="handleUpdate(scope.row)">确认</el-button>
                      <!-- <el-button v-else type="text" size="mini" icon="el-icon-view" style="background:none;border:1px solid none;color:#5588ed" @click="handleUpdate(scope.row)">查看</el-button> -->
                      <el-button v-else size="mini" type="text" style="background:none;border:1px solid none;color:#5588ed" icon="el-icon-view" @click="handleUpdate1(scope.row)">确认记录</el-button>
                    </template>
                  </el-table-column>
                  <!-- </template> -->
                </el-table>
                <pagination v-show="totalAlarm > 0 && dataListAlarm" :total="totalAlarm" :page-size="7" :pager-count="5" :page-sizes="[10, 20, 50, 200]" :page.sync="queryParamsAlarm.start" :limit.sync="queryParamsAlarm.limit" @pagination="getAlarmList()" />
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
      </el-card>
      <el-dialog :title="formData.row.Acknowledged !== '确认' ? '告警确认' : '查看'" :visible.sync="formShow" width="950px">
        <el-form ref="formData" :model="formData" label-width="120px" class="demo-ruleForm">
          <el-row>
            <el-col :span="12">
              <el-form-item label="开始时间">
                <el-date-picker :value="formData.row.FirstOccurrence" type="datetime" :readonly="true" style="width:100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="级别">
                <el-input :value="formData.row.SeverityName" :readonly="true" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="持续时长">
                <!-- <el-input v-if="formData.row.EventType === 'P'" value="-" /> -->
                <el-input v-if="formData.row.RecoveredStatus === '未恢复'" :value="(momentDate - formData.row.time1) | getdiffLongTime1" />
                <el-input v-else-if="formData.row.RecoveredStatus === '已恢复'" :value="(formData.row.time2 - formData.row.time1) | getdiffLongTime1" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="原始级别">
                <el-input :value="formData.row.OldSeverity" :readonly="true" />
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
              <el-form-item label="描述">
                <el-input type="textarea" :value="formData.row.Summary" :rows="4" :readonly="true" />
              </el-form-item>
            </el-col>
            <!-- <el-col>
              <el-form-item label="分组" style="width:50%">
                <el-input :value="formData.row.EventCategoryLabelText" :readonly="true" />
              </el-form-item>
            </el-col> -->
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
                <el-input :value="formData.row.Acknowledged" :readonly="true" />
                <!-- <el-input v-else value="已确认" :readonly="true" /> -->
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item v-if="formData.row.EventType === 'P'" label="恢复">
                <el-input v-if="formData.row.RecoveredStatus === '未恢复'" :value="formData.row.RecoveredStatus + '(不可恢复事件,24h后自动恢复)'" :readonly="true" />
                <el-input v-else :value="formData.row.RecoveredStatus" :readonly="true" />
              </el-form-item>
              <el-form-item v-else label="恢复">
                <el-input :value="formData.row.RecoveredStatus" :readonly="true" />
              </el-form-item>
            </el-col>
            <el-col>
              <el-form-item label="通知类型" prop="notifyType">
                <el-checkbox-group v-model="formData.notifyType" :disabled="formData.row.Acknowledged == '确认'">
                  <el-checkbox label="flow">开单</el-checkbox>
                  <el-checkbox label="email">邮件</el-checkbox>
                  <el-checkbox label="wechat">融讯通</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="通知对象" prop="person">
                <el-input v-model="formData.person" type="textarea" clearable style="vertical-align: top;" :placeholder="formData.row.Acknowledged !== '确认' ? '请输入通知对象' : '暂无数据'" :readonly="formData.row.Acknowledged == '确认'" />
                <div v-if="formData.row.Acknowledged !== '确认'" style="display:inline-block;width:56%;font-size: 10px;line-height: initial;padding-left:10px;vertical-align: top;">
                  <!-- <span style="vertical-align: top;">提示：</span> -->
                  <div style="display:inline-block;width:100%">
                    提示：
                    <br />1.通知对象不填写时，符合规则的事件将通过事件分类发送对应角色的对象 <br />2.点击右侧填充按钮，将填充事件分类发送对应角色的对象 <br />3.通知对象请填写华融域账号，通知对象有多个时,用#分割
                  </div>
                </div>
                <div v-if="formData.row.Acknowledged !== '确认'" style="display:inline-block;float: right;margin-top:10px">
                  <el-button size="mini" round @click="getPerson">填充</el-button>
                </div>
              </el-form-item>
              <el-form-item label="备注" prop="remark">
                <el-input v-model="formData.remark" type="textarea" :placeholder="formData.row.Acknowledged !== '确认' ? '请输入备注' : '暂无数据'" clearable :readonly="formData.row.Acknowledged == '确认'" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <div v-if="formData.row.Acknowledged !== '确认'" style="font-size: 10px;line-height: initial;padding:5px;padding-top:5px;vertical-align: top;">说明：确认操作为对事件确认字段状态的修改</div>
          <el-button class="cancel_button" @click="resetForm()">取 消</el-button>
          <el-button v-if="formData.row.Acknowledged !== '确认'" type="primary" class="confirm_button" @click="submitForm()">确 定</el-button>
        </div>
      </el-dialog>
      <el-dialog title="批量确认" :visible.sync="batchFormShow" width="950px">
        <el-form ref="batchFormData" :model="batchFormData" label-width="110px" class="demo-ruleForm">
          <el-row>
            <el-col>
              <el-form-item label="通知类型" prop="notifyType">
                <el-checkbox-group v-model="batchFormData.notifyType">
                  <el-checkbox label="flow">开单</el-checkbox>
                  <el-checkbox label="email">邮件</el-checkbox>
                  <el-checkbox label="wechat">融讯通</el-checkbox>
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
      <el-dialog title="确认记录" :visible.sync="formShow1" width="950px" min-height="60vh" class="tabsDialog">
        <el-tabs v-model="active1.tab" type="border-card">
          <el-tab-pane label="事件" name="one">
            <el-form ref="formData" :model="formData" label-width="110px" class="demo-ruleForm" style="padding:10px;padding-left:0">
              <el-row>
                <el-col :span="12">
                  <el-form-item label="开始时间">
                    <el-date-picker :value="formData.row.FirstOccurrence" type="datetime" :readonly="true" style="width:100%" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="级别">
                    <el-input :value="formData.row.SeverityName" :readonly="true" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="持续时长">
                    <!-- <el-input v-if="formData.row.EventType === 'P'" value="-" /> -->
                    <el-input v-if="formData.row.RecoveredStatus === '未恢复'" :value="(momentDate - formData.row.time1) | getdiffLongTime1" />
                    <el-input v-else-if="formData.row.RecoveredStatus === '已恢复'" :value="(formData.row.time2 - formData.row.time1) | getdiffLongTime1" />
                  </el-form-item>
                </el-col>
                <!-- <el-col v-else :span="12">
                  <el-form-item label="结束时间">
                    <el-date-picker :value="formData.row.LastOccurrence" type="datetime" :readonly="true" style="width:100%" />
                  </el-form-item>
                </el-col> -->
                <el-col :span="12">
                  <el-form-item label="原始级别">
                    <el-input :value="formData.row.OldSeverity" :readonly="true" />
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
                  <el-form-item label="描述">
                    <el-input type="textarea" :value="formData.row.Summary" :rows="4" :readonly="true" />
                  </el-form-item>
                </el-col>
                <!-- <el-col>
                  <el-form-item label="分组" style="width:50%">
                    <el-input :value="formData.row.EventCategoryLabelText" placeholder="暂无数据" :readonly="true" />
                  </el-form-item>
                </el-col> -->
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
                    <el-input v-if="formData.row.RecoveredStatus === '未恢复'" :value="formData.row.RecoveredStatus + '(不可恢复事件,24h后自动恢复)'" :readonly="true" />
                    <el-input v-else :value="formData.row.RecoveredStatus" :readonly="true" />
                  </el-form-item>
                  <el-form-item v-else label="恢复">
                    <el-input :value="formData.row.RecoveredStatus" :readonly="true" />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item label="备注" prop="remark">
                    <el-input v-model="formData.remark" :readonly="true" type="textarea" placeholder="暂无数据" clearable />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="确认记录" name="two">
            <div v-if="showCheck" style="padding:15px;padding-top:0px">
              <div style="line-height:30px;margin-bottom:10px">
                <div>
                  <span>确认人</span>：
                  <span>{{ confirmObj.acknoledger || '-' }}</span>
                </div>
                <div>
                  <span>确认时间</span>：
                  <span>{{ confirmObj.transmit_date | getDate }}</span>
                </div>
                <div v-if="labelPosition === 'right' && confirmObj.url">
                  <span>工单链接</span>：<span style="color: #1890ff;cursor: pointer;">
                    <i class="el-icon-paperclip" /><span @click="openWindows(confirmObj.url)">{{ confirmObj.url || '-' }}</span></span
                  >
                </div>
                <div>
                  <span>备注</span>：
                  <span>{{ confirmObj.remark || '-' }}</span>
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
                  <el-timeline-item v-for="activity in activities" :key="activity.id" color="#5588ed" icon="el-icon-warning-outline" size="large" placement="top">
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
        </el-tabs>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import CommonChart from '@/views/dashboard/commonCustomChart'
import moment from 'moment'
import axios from 'axios'
import Deficiency from '@/components/Deficiency/index'
import { batchCloseEvents, getPerson, pushEventToNotifyService, getPersonShow } from '@/api/event/event-manage'
import { getEventColumnDefine } from '@/api/publicResources/eventField'
// import { getWEBResource, getNumByAlarmLevel, getNumByAlarmState, getCustomTop5DataGroupByHost, getServerOverview, getResourceCount, getTotalAlarmAsset, getAlarmList, getKeys, getAlarmCategory, getResourceSonClassId } from '@/api/operation/serverOverview'
import { getCustomRouter, getWEBResource, getNumByAlarmLevel, getNumByAlarmState, getCustomTop5DataGroupByHost, getCustomOverview, getResourceCount, getTotalAlarmAsset, getCustomAlarmList, getCustomKeys, getResourceSonClassId } from '@/api/operation/serverOverview'
export default {
  name: 'Synopsis',
  components: { CommonChart, Deficiency },
  filters: {
    getColor(val, num) {
      return num === 100 ? '#30D87A' : '#60FFEF'
    }
  },
  // eslint-disable-next-line vue/prop-name-casing
  props: ['class_id', 'top_list', 'toRouter', 'kpiDatasetName', 'searchName'],
  data() {
    return {
      tableData: [],
      activities: [],
      showCheck: false,
      confirmObj: {
        work_order_id: ''
      },
      formShow1: false, // 弹框是否打开
      active1: {
        tab: 'one', // 关键指标 性能详情 事件tab
        menu: '' // 性能详情 指标组menu
      },
      labelPosition: 'left',
      nowDate1: moment()
        .subtract(24, 'hours')
        .format('x'),
      ww: null, // 屏幕宽度
      show1: true,
      show2: true,
      top5loading: false,
      disabled: true,
      // 表格选中记录
      idSelections: [],
      timer: '',
      // idSelections: [],
      tableSelections: [],
      batchFormShow: false, //  批量确认弹框是否打开
      batchFormData: {
        notifyType: [],
        remark: ''
      },
      alarmLevelCount: [], // 按告警级别计算数量
      alarmStateCount: [], // 按告警状态计算数量
      categoryList: [],
      totalAssets: '0', // 设备总数
      alarmServerCount: '0', // 告警设备数
      assetsNotAlerted: '0', // 未告警数
      alarmResourceLinux: '0', // 告警设备信息
      resourceName: '', // 设备信息名称
      operational_status: '',
      class_id_son: '', // 设备分组名称
      name: '', // 告警查询框
      level: '', // 告警等级
      compute: '', // top5计算规则
      isComponent: '', // 告警分类
      isAcknowledged: '',
      isEventSeverityType: '',
      isRecoveredStatus: '',
      alarmLevel: [],
      loading: false, // 告警列表加载状态
      loading1: false, // 设备类表加载状态
      // activeCollapse: ['1', '2'], //告警设备默认展开
      activeCollapse: ['1', '2'],
      radarData: {
        radar: [
          {
            indicator: [{ max: 100 }, { max: 100 }, { max: 100 }, { max: 100 }, { max: 100 }, { max: 100 }],
            center: this.ww > 1400 ? ['50%', '50%'] : ['50%', '65%'], // 位置
            radius: this.ww > 1400 ? 60 : 40, // 大小
            startAngle: 135,
            splitNumber: 4,
            shape: 'circle',
            name: {
              formatter: '{value}',
              textStyle: {
                color: this.$store.state.settings.themeClass === 'dark' ? 'rgba(192, 208, 208, 1)' : '#333',
                padding: this.ww > 1400 ? 20 : 10,
                fontSize: this.ww > 1400 ? 14 : 10
              }
            },
            splitArea: {
              areaStyle: {
                color: 'rgba(0, 240, 255, 0)', // 圆环颜色
                shadowColor: this.$store.state.settings.themeClass === 'dark' ? '#1890FF' : '#1890FF', // 圆颜色
                shadowBlur: 10
              }
            },
            axisLine: {
              lineStyle: {
                color: this.$store.state.settings.themeClass === 'dark' ? 'rgba(85, 136, 237, .25)' : '#E9E9E9' // 分割线
              }
            },
            splitLine: {
              lineStyle: {
                color: this.$store.state.settings.themeClass === 'dark' ? 'rgba(85, 136, 237, .25)' : '#E9E9E9' // 圆线
              }
            }
          }
        ],
        series: [
          {
            name: '雷达图',
            type: 'radar',
            color: this.$store.state.settings.themeClass === 'dark' ? 'rgba(85, 136, 237, 1)' : 'rgba(24, 144, 255, 1)',
            itemStyle: {
              emphasis: {
                lineStyle: { width: 4 }
              }
            },
            data: [
              {
                value: [],
                areaStyle: {
                  normal: {
                    color: this.$store.state.settings.themeClass === 'dark' ? 'rgba(85, 136, 237, 0.1)' : 'rgba(16, 142, 233, 0.1)' // 选择区域颜色
                  }
                },
                label: {
                  // color:'#fff',
                  show: true,
                  textStyle: {
                    color: this.$store.state.settings.themeClass === 'dark' ? '#fff' : 'rgba(51, 51, 51, 1)',
                    fontSize: this.ww > 1400 ? 14 : 10
                  },
                  position: 'top'
                }
              }
            ]
          }
        ]
      },
      // 通过名字判别是否需要处理字节数
      linedata: {
        timeline: {
          axisType: 'category',
          autoPlay: true,
          playInterval: 30000,
          inverse: false,
          currentIndex: 0,
          rewind: false,
          symbolSize: 10,
          height: 20,
          bottom: 10,
          lineStyle: {
            color: '#DAE1F5',
            width: 0.5,
            opacity: 0.1
          },
          itemStyle: {
            borderColor: '#DAE1F5',
            opacity: 0.1
          },
          checkpointStyle: {
            symbolSize: 10
          },
          controlStyle: {
            itemSize: this.ww > 1400 ? 16 : 13,
            color: '#1890ff'
          },
          // label: {
          //   formatter: function(s) {
          //     console.log(s)
          //     return s
          //   }
          // },
          tooltip: {
            show: false
          },
          data: []
        },
        color: this.$store.state.settings.themeClass === 'dark' ? 'rgba(85, 136, 237, .8)' : 'rgba(24, 144, 255, 1)',
        tooltip: {
          trigger: 'axis',
          textStyle: { color: '#ffffff' },
          // formatter: '{b}\n{c}',
          axisPointer: {
            // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
          },
          formatter: '{c} {a}'
        },
        grid: { top: '2%', left: '5%', right: '20%', bottom: '15%', containLabel: true },
        visualMap: {
          orient: 'horizontal',
          show: false,
          min: 10,
          max: 100,
          // Map the score column to color
          dimension: 0,
          inRange: { color: ['#65B581', '#FFCE34', '#FD665F'] }
        },
        xAxis: {
          // type: 'value',
          nameTextStyle: { color: this.$store.state.settings.themeClass === 'dark' ? 'rgba(192, 208, 208, .6)' : 'rgba(51, 51, 51, .45)' },
          splitLine: { show: false }, // 去除网格线
          splitArea: { show: false }, // 保留网格区域
          axisLine: {
            lineStyle: {
              type: 'solid',
              color: this.$store.state.settings.themeClass === 'dark' ? 'rgba(77, 77, 77, .4)' : 'rgba(51, 51, 51, .0)', // 左边线的颜色
              width: '1' // 坐标线的宽度
            }
          },
          axisLabel: {
            textStyle: {
              color: 'rgba(77, 77, 77, 0)' // 坐标值得具体的颜色
            }
          }
        },
        yAxis: {
          name: '(设备标识)',
          type: 'category',
          nameTextStyle: {
            color: this.$store.state.settings.themeClass === 'dark' ? 'rgba(192, 208, 208, .6)' : 'rgba(51, 51, 51, .45)'
          },
          splitLine: {
            show: true,
            lineStyle: {
              type: 'solid',
              color: this.$store.state.settings.themeClass === 'dark' ? 'rgba(77, 77, 77, .4)' : 'rgba(0, 0, 0, 0)', // 左边线的颜色
              width: '1' // 坐标线的宽度
            }
          }, // 去除网格线
          splitArea: { show: false }, // 保留网格区域
          axisLine: {
            lineStyle: {
              type: 'solid',
              color: this.$store.state.settings.themeClass === 'dark' ? 'rgba(77, 77, 77, .4)' : 'rgba(0, 0, 0, 0)', // 左边线的颜色
              width: '1' // 坐标线的宽度
            }
          },
          axisLabel: {
            clickable: true,
            formatter: function(value) {
              return value.split('_')[0]
            },
            textStyle: {
              color: this.$store.state.settings.themeClass === 'dark' ? 'rgba(192, 208, 208, .6)' : 'rgba(0, 0, 0, .45)' // 坐标值得具体的颜色
            }
          },
          data: []
        },
        series: [
          {
            type: 'bar',
            itemStyle: {
              normal: {
                label: {
                  show: true,
                  position: 'right',
                  formatter: '{c} {a}',
                  textStyle: {
                    fontWeight: 'bolder',
                    fontSize: '10',
                    color: this.$store.state.settings.themeClass === 'dark' ? '#fff' : '#333'
                  }
                }
              }
            },
            data: []
          }
        ],
        options: []
      },
      // top5活动标签名称
      activeName: this.top_list[0]['topN_title'],
      topscoll: this.limitNum,
      total: 0,
      totalAlarm: 0,
      queryParams: { start: 1, limit: 10, orderName: '', orderType: '' },
      queryParamsAlarm: { start: 1, limit: 10 },
      dataListAlarm: [],
      dataList: [],
      kpi_name: this.top_list[0]['kpi_name'],
      limitNum: 5,
      resourceKeys: [],
      momentDate: moment().format('x'),
      formData: {
        row: {},
        notifyType: [],
        remark: ''
      }, // 工单确认对象
      formShow: false, // 弹框是否打开
      tableColumns: [],
      // 级别数组
      SeverityArr: []
    }
  },
  computed: {
    recoverAlarm() {
      return this.alarmStateCount[1] || 0
    },
    unrecoveredAlarm() {
      return this.alarmStateCount[0] || 0
    },
    recoverAlarmPercent() {
      return Math.round((this.recoverAlarm / (this.recoverAlarm + this.unrecoveredAlarm)) * 100 * 100) / 100
    },
    unrecoveredAlarmPercent() {
      return Math.round((this.unrecoveredAlarm / (this.recoverAlarm + this.unrecoveredAlarm)) * 100 * 100) / 100
    }
  },
  watch: {
    $route: {
      handler(val, oldval) {
        const temp = {
          limit: this.queryParamsAlarm.limit,
          limit1: this.queryParams.limit,
          name: this.name,
          level: this.level,
          isComponent: this.isComponent,
          isAcknowledged: this.isAcknowledged,
          isEventSeverityType: this.isEventSeverityType,
          isRecoveredStatus: this.isRecoveredStatus,
          start: this.queryParamsAlarm.start,
          operational_status: this.operational_status,
          class_id_son: this.class_id_son,
          resourceName: this.resourceName
        }
        localStorage.setItem(this.searchName, JSON.stringify(temp))
      },
      // 深度观察监听
      deep: true
      // immediate: false
    },
    'active1.tab': function(newValue, oldValue) {
      this.getPersonShow()
    },
    labelPosition: function(newValue, oldValue) {
      this.getPersonShow()
    },
    top_list: {
      deep: true,
      immediate: true,
      handler(newValue, old) {
        if (!old) {
          this.linedata.series[0].name = this.top_list[0].unit
          this.kpi_name = newValue[0]['kpi_name']
          this.compute = newValue[0]['compute']
          // this.getCustomTop5DataGroupByHost()
          this.limitNum = newValue[0].topN_number
          this.linedata.timeline.data = []
          // 判断页数
          for (let i = 0; i < Math.ceil(this.limitNum / 5); i++) {
            this.linedata.timeline.data.push(i + 1)
          }
        }
      }
    },

    // top5标签变化时
    activeName(newActiveName) {
      // alert(newActiveName)
      this.top_list.map((value, index) => {
        const { topN_title, kpi_name, compute } = value
        if (topN_title === newActiveName) {
          this.linedata.series[0].name = this.top_list[index].unit
          // this.linedata.series[0].name = xAxisName
          this.kpi_name = kpi_name
          this.limitNum = value.topN_number
          this.compute = compute
          this.linedata.timeline.data = []
          // 判断页数
          for (let i = 0; i < Math.ceil(this.limitNum / 5); i++) {
            this.linedata.timeline.data.push(i + 1)
          }
        }
      })
      this.getCustomTop5DataGroupByHost()
    },
    // 获取到可访问的资源key时
    resourceKeys(newActiveName, old) {
      // 判断是不是第一次赋值
      if (old.length && old.length === newActiveName.length) {
        // 如果不是合并去重判断长度是否改变
        const concatArr = newActiveName.concat(old)
        const temp = [...new Set(concatArr)]
        if (temp.length === newActiveName.length) return
      }
      // 长度没有发生改变调用方法
      this.getCustomTop5DataGroupByHost() // 获取top5使用率主机以及值
      this.getAlarmList() // 获取告警列表
      this.getNumByAlarmLevel() // 根据告警级别分组获取资源告警数量
      this.getNumByAlarmState() // 根据告警状态分组获取资源告警数量
      this.getServerOverviews() // 设备总数
    }
  },
  beforeCreate() {
    this.ww = window.screen.width
  },
  created() {
    this.ww = window.screen.width

    // if (tempStr) {
    const tempObj = JSON.parse(localStorage.getItem(this.searchName))
    if (tempObj) {
      this.name = tempObj.name
      this.level = tempObj.level
      this.isComponent = tempObj.isComponent
      this.isAcknowledged = tempObj.isAcknowledged
      this.isRecoveredStatus = tempObj.isRecoveredStatus
      this.isEventSeverityType = tempObj.isEventSeverityType
      this.queryParamsAlarm.start = tempObj.start
      this.operational_status = tempObj.operational_status
      this.class_id_son = tempObj.class_id_son
      this.resourceName = tempObj.resourceName
      this.queryParamsAlarm.limit = tempObj.limit
      this.queryParams.limit = tempObj.limit1
    }
    // }
    this.getResourceSonClassId() // 获取设备分类
    this.getResource() // 获取设备列表
    this.getTableColumns() // 获取表单展示项
    this.getServerOverviews() // 设备总数
    this.getAlarmList() // 获取告警列表
  },
  mounted() {
    var tempStr = null
    window.onbeforeunload = function(e) {
      return '确定要离开吗?'
    }
    switch (this.$route.name) {
      case '操作系统':
        // console.log(this.$route.name)
        tempStr = 'ServerOverview'
        break
      case '服务器硬件':
        tempStr = 'HardwareOverview'
        break
      case '网络设备':
        tempStr = 'NetworkOverview'
        break
      case '中间件':
        tempStr = 'MiddlewareOverview'
        break
      case '数据库':
        tempStr = 'DatabaseOverview'
        break
      case '虚拟化':
        tempStr = 'VirtualizationOverview'
        break
      case '存储':
        tempStr = 'StorageOverview'
        break
      case '光纤交换机':
        tempStr = 'FSWOverview'
        break
      case 'WEB检测':
        tempStr = 'WEBOverview'
        break
      case 'PING检测':
        tempStr = 'PINGOverview'
        break
      case '进程检测':
        tempStr = 'ProcOverview'
        break
    }
    if (tempStr) {
      const tempObj = JSON.parse(localStorage.getItem(tempStr))
      if (tempObj) {
        // console.log(tempObj)
        this.name = tempObj.name
        this.level = tempObj.level
        this.isComponent = tempObj.isComponent
        this.isAcknowledged = tempObj.isAcknowledged
        this.isRecoveredStatus = tempObj.isRecoveredStatus
        this.isEventSeverityType = tempObj.isEventSeverityType
        this.queryParamsAlarm.start = tempObj.start
        this.operational_status = tempObj.operational_status
        this.class_id_son = tempObj.class_id_son
        this.resourceName = tempObj.resourceName
      }
    }
    this.timer = setInterval(this.getTimer, 300000)
  },
  beforeDestroy() {
    clearInterval(this.timer)
  },
  deactivated() {
    // console.log(1)
    clearInterval(this.timer)
  },
  methods: {
    openWindows(host) {
      window.open(host, '_blank')
    },
    addScroll(data) {
      // this.msgInfo('已为你定位到告警列表')
      var div = document.getElementById('scrolldIV')
      div.scrollTop = div.scrollHeight
      this.name = ''
      this.level = ''
      this.isEventSeverityType = ''
      this.isAcknowledged = ''
      this.queryParamsAlarm.start = 1
      if (data) {
        this.isRecoveredStatus = data
        this.msgInfo('已为你定位到告警列表,为你检索出' + (data === 2 ? '已恢复' : '未恢复') + '告警')
      } else {
        this.isRecoveredStatus = ''
        this.msgInfo('已为你定位到告警列表,为你检索出所有告警')
      }
      this.getAlarmList()
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
    // 获取流程
    getPersonShow() {
      // 获取确认记录
      this.confirmObj = {
        work_order_id: '',
        url: ''
      }
      this.tableData = []
      getPersonShow({ projectId: 10, EventID: this.formData.row.EventID }).then((res) => {
        if (res.success && res.datas.length) {
          // 确认人赋值
          this.confirmObj = res.datas[0]
          // 表格赋值
          if (this.confirmObj.candidate) {
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
            var url = 'http://itsm.chamc.com.cn/e3-bpm/openapi/instance/getCheckOpinion?token=iqJNiSVub6&workOrderId=' + this.confirmObj.work_order_id
            axios.post(url).then(function(res) {
              if (res.status === 200) {
                they_.activities = []
                const temp = res.data.data.rows.sort(they_.compare('completeTime'))
                they_.activities = [...temp]
              }
            })
          }
          this.showCheck = true
        } else {
          this.tableData = []
          this.showCheck = false
        }
      })
    },
    // 过滤方法
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
    },

    // 打开弹窗
    handleUpdate1(row) {
      this.formShow1 = true
      this.confirmObj = {
        work_order_id: ''
      }
      this.active1.tab = 'one'
      this.labelPosition = 'left'
      this.formData.row = row
      this.$set(this.formData, 'notifyType', ['email'])
      this.$set(this.formData, 'person', '')
      this.$set(this.formData, 'projectId', this.queryParams.projectId)
      this.getPersonShow()
    },

    // 定时器
    getTimer() {
      // console.log(1)
      this.getResourceSonClassId() // 获取设备分类
      this.getResource() // 获取设备列表
      this.getTableColumns() // 获取表单展示项
    },
    // 获取设备列表表格排序
    getOrder(params) {
      // 如果排序规则为空则按默认排序，清空两个值
      if (!params.order) {
        this.queryParams.orderName = ''
        this.queryParams.orderType = ''
      } else {
        this.queryParams.orderType = params.order === 'ascending' ? 'asc' : 'desc'
        this.queryParams.orderName = params.prop
      }
      this.getResource()
    },
    // 获取设备分类
    getResourceSonClassId() {
      getResourceSonClassId({
        projectId: 10,
        class_id: this.class_id,
        engine: 'Freemarker'
      }).then((response) => {
        // console.log(response)
        this.categoryList = response.datas
      })
    },
    getCustomTop5DataGroupByHost() {
      // 获取top5使用率主机以及值
      this.top5loading = true
      if (!this.kpi_name.length) return
      getCustomTop5DataGroupByHost({
        kpi_name: this.kpi_name,
        projectId: 10,
        isEnable: 'Y',
        nowDate: this.nowDate1,
        compute: this.compute,
        limitNum: this.limitNum,
        resourceKeys: this.resourceKeys,
        engine: 'Freemarker'
      }).then((response) => {
        // console.log(response)
        this.top5loading = false
        const temp = response.datas
        // 判断展示不展示切换条
        if (temp.length > 5) {
          this.linedata.timeline.show = true
        } else {
          this.linedata.timeline.show = false
        }
        this.linedata.options = []
        // console.log(response)
        if (!!temp && temp.length > 0) {
          temp.forEach((item, i) => {
            const page = Math.ceil((i + 1) / 5)
            if (item.value) {
              if (this.linedata.options[page - 1]) {
                this.linedata.options[page - 1].series[0].data.unshift(item.value)
                this.linedata.options[page - 1].yAxis[0].data.unshift(item.host + '_' + item.label_text)
              } else {
                this.linedata.options[page - 1] = { series: [{ data: [] }], yAxis: [{ data: [] }] }
                this.linedata.options[page - 1].series[0].data.unshift(item.value)
                this.linedata.options[page - 1].yAxis[0].data.unshift(item.host + '_' + item.label_text)
              }
              // 设置最大值和最小值
              if (i === temp.length - 1) {
                this.linedata.visualMap.max = this.linedata.options[0].series[0].data[this.linedata.options[0].series[0].data.length - 1]
                this.linedata.visualMap.min = this.linedata.options[this.linedata.options.length - 1].series[0].data[0]
              }
            }
          })
        }
      })
    },
    // 数值单位转换
    numTypeChange(limit) {
      var size = ''
      if (limit < 0.1 * 1024) {
        // 如果小于0.1KB转化成B
        size = limit.toFixed(2) + 'B'
      } else if (limit < 0.1 * 1024 * 1024) {
        // 如果小于0.1MB转化成KB
        size = (limit / 1024).toFixed(2) + 'KB'
      } else if (limit < 0.1 * 1024 * 1024 * 1024) {
        // 如果小于0.1GB转化成MB
        size = (limit / (1024 * 1024)).toFixed(2) + 'MB'
      } else {
        // 其他转化成GB
        size = (limit / (1024 * 1024 * 1024)).toFixed(2) + 'GB'
      }
      var sizestr = size + ''
      var len = sizestr.indexOf('.')
      var dec = sizestr.substr(len + 1, 2)
      if (dec === '00') {
        // 当小数点后为00时 去掉小数部分
        return sizestr.substring(0, len) + sizestr.substr(len + 3, 2)
      }
      return sizestr
    },
    getNumByAlarmLevel() {
      // 根据告警级别分组获取资源告警数量
      getNumByAlarmLevel({
        resourceKeys: this.resourceKeys,
        projectId: 10,
        // nowDate: moment()
        //   .startOf('day')
        //   .format('x'),
        nowDate: this.nowDate1,
        type: 'PING检测',
        engine: 'Freemarker'
      }).then((response) => {
        // console.log(response)
        const nums = response.datas
        this.alarmLevel = response.datas
        if (!!nums && nums.length > 0) {
          const indicator = nums.map((currentValue) => {
            const { text, dataValue } = currentValue
            return dataValue ? { max: dataValue, text, dataValue } : { max: 1, text, dataValue }
          })
          this.radarData['radar'][0]['indicator'] = []
          this.radarData['radar'][0]['indicator'] = indicator
          // 2雷达图属性赋值
          this.radarData['series'][0]['data'][0]['value'] = indicator.map((currentValue) => {
            return currentValue['dataValue']
          })
        }
      })
    },
    getNumByAlarmState() {
      // 根据告警状态分组获取资源告警数量
      // console.log(this.resourceKeys)
      getNumByAlarmState({
        // resourceKeys1: this.resourceKeys,
        projectId: 10,
        clabel_text: ['PING检测'],
        // nowDate: moment()
        //   .startOf('day')
        //   .format('x'),
        nowDate: this.nowDate1,
        engine: 'Freemarker'
      }).then((response) => {
        // console.log(response)
        const nums = response.datas
        // 重置值重新插入
        this.alarmStateCount = []
        // if (!!nums && nums.length > 0) {
        //   nums.forEach((currentValue) => {
        //     // 遍历状态值插入到页面中
        //     this.alarmStateCount.splice(currentValue.alarm_state, 1, currentValue.alarm_count)
        //   })
        // }
        if (!!nums && nums.length > 0) {
          nums.forEach((currentValue) => {
            // 遍历状态值插入到页面中
            if (currentValue.alarm_state === 2) {
              // this.alarmLevelCount[1] = currentValue.alarm_count
              this.$set(this.alarmStateCount, 1, currentValue.alarm_count)
            } else {
              this.$set(this.alarmStateCount, 0, currentValue.alarm_count)
            }

            // this.alarmStateCount.splice(currentValue.alarm_state, 1, currentValue.alarm_count)
          })
        }
      })
    },
    turnPage(row) {
      // console.log(row)
      const query = {
        name: {
          id: row.id,
          clabel_text: 'PING检测',
          classId: row.res_class_id,
          host: row.monitored_host,
          label_text: row.monitor_name,
          res_ip_address: row.monitored_proxy
        }
      }
      this.$router.push({ name: '检测通用详情页', params: query })
    },
    // 告警列表跳转
    turnPage1(row) {
      // console.log(row)
      // const ip = row.Summary.split(':')[0].replace('发起设备', '') || null
      getCustomRouter({
        monitored_proxy: row.HostOrProxy,
        monitored_host: row.Node,
        projectId: 10,
        classIdList: this.kpiDatasetName.classIdList, // classIdList
        engine: 'Freemarker'
      }).then((response) => {
        // console.log(response)
        if (response.success && response.datas.length) {
          const query = {
            name: {
              id: response.datas[0].id,
              clabel_text: response.datas[0].label_text,
              classId: this.kpiDatasetName.classIdList,
              host: row.Node,
              label_text: response.datas[0].monitor_name,
              res_ip_address: row.HostOrProxy
            }
          }
          this.$router.push({ name: '检测通用详情页', params: query })
        } else {
          this.msgError('没有找到对应页面，请联系管理员')
        }
      })
    },
    // 更多跳转
    turnPage2(router, param) {
      this.$router.push({ name: router, params: param || null })
    },
    turnPage3(router, param) {
      this.$router.push({ name: router, params: param || null })
    },
    /** 设备总数 */
    getServerOverviews() {
      const userStr = localStorage.getItem('user')
      const userObj = JSON.parse(userStr)
      const isAdmin = userObj['admin']
      const roleId = userObj['roleId']
      if (isAdmin) {
        getCustomOverview({
          class_id: this.class_id,
          projectId: 10,
          engine: 'Freemarker'
        }).then((response) => {
          this.totalAssets = response.datas[0].totalNumberAssets
          this.getTotalAlarmAssets() // 设备告警数
        })
      } else {
        getCustomOverview({
          class_id: this.class_id,
          projectId: 10,
          roleId,
          engine: 'Freemarker'
        }).then((response) => {
          this.totalAssets = response.datas[0].totalNumberAssets
          this.getTotalAlarmAssets() // 设备告警数
        })
      }
    },
    /** 告警设备数 */
    getTotalAlarmAssets() {
      getTotalAlarmAsset({
        resourceKeys1: this.resourceKeys,
        projectId: 10,
        // nowDate: moment()
        //   .startOf('day')
        //   .format('x'),
        nowDate: this.nowDate1,
        engine: 'Freemarker'
      }).then((response) => {
        if (!!response.datas && response.datas.length > 0) {
          this.alarmServerCount = response.datas[0].alarmServerCount
          this.assetsNotAlerted = this.totalAssets - this.alarmServerCount
        }
      })
    },
    /** 获取设备信息 */
    getResource() {
      this.loading1 = true
      const userStr = localStorage.getItem('user')
      const userObj = JSON.parse(userStr)
      const userId = userObj['userId']
      const roleId = userObj['roleId']
      // 1资源列表
      getWEBResource({
        start: this.queryParams.start,
        limit: this.queryParams.limit,
        resourceName: this.resourceName,
        nowDate: this.nowDate1,
        class_id_son: this.class_id_son,
        class_id: this.class_id,
        userId,
        PINGSTATUS: this.operational_status,
        type: 'PING',
        classIdList: this.kpiDatasetName.classIdList,
        roleId, // 传的话按角色，不传就是管理员
        orderName: this.queryParams.orderName, // 排序名称
        orderType: this.queryParams.orderType, // 排序类型
        projectId: 10,
        engine: 'Freemarker'
      }).then((response) => {
        this.loading1 = false
        this.total = response.totalCount
        this.dataList = response.datas
      })
      // 2资源keys
      getCustomKeys({
        type: 'PING',
        projectId: 10,
        engine: 'Freemarker'
      }).then((response) => {
        if (!!response.datas && response.datas.length > 0) {
          this.resourceKeys = response.datas.map((currentValue) => {
            return currentValue['resourceKeys']
          })
        }
      })
    },
    /** 获取告警信息 */
    getAlarmList() {
      this.loading = true
      getCustomAlarmList({
        start: this.queryParamsAlarm.start,
        limit: this.queryParamsAlarm.limit,
        // 设备分类名称
        EventCategoryLabelText: 'PING检测',
        isComponent: this.isComponent,
        isRecoveredStatus: this.isRecoveredStatus,
        isEventSeverityType: this.isEventSeverityType,
        isAcknowledged: this.isAcknowledged,
        nowDate: this.nowDate1,
        name: this.name,
        level: this.level,
        projectId: 10,
        engine: 'Freemarker'
      }).then((res) => {
        this.totalAlarm = res.totalCount
        if (res.datas.length && res.success) {
          this.dataListAlarm = res.datas
          this.loading = false
        } else {
          this.loading = false
          this.dataListAlarm = []
        }
      })
    },
    findByresourceName() {
      this.queryParams.start = 1
      this.loading1 = true
      this.getResource()
    },
    // 打开弹框
    handleUpdate(row) {
      this.momentDate = moment().format('x')
      this.formShow = true
      this.formData.row = row
      this.$set(this.formData, 'notifyType', ['email'])
      this.$set(this.formData, 'projectId', localStorage.getItem('project_id'))
      /* this.batchFormShow = true;
      this.batchFormData.events=this.tableSelections;
      this.$set(this.batchFormData,"notifyType",['ON']);
      this.$set(this.batchFormData,"projectId",this.queryParams.projectId);*/
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
        // console.log(response)
        this.tableColumns = response.datas
        // 给下拉项增加一个属性isSelect判断当前项是否已经被选择过，默认赋值false
        // this.tableColumns.forEach((item) => (item.isSelect = false))
        this.loading = false
      })
    },
    // 弹框取消方法
    resetForm(formName) {
      this.formShow = false
      // 重置备注和通知方式
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
    getPerson() {
      getPerson({ projectId: 10, deviceKey: this.formData.row.Node, ifGroup: true }).then((res) => {
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
          getPerson({ projectId: 10, deviceKey: this.formData.row.Node }).then((res1) => {
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
    // 弹框确认方法
    submitForm() {
      this.$refs['formData'].validate((valid) => {
        if (valid) {
          this.formData.events = [this.formData.row]
          this.formData.notificationType = {
            effectColumn: '',
            effectDataType: '',
            effectType: this.formData.notifyType,
            effectValue: this.formData.person
          }
          this.formData.acknoledger = JSON.parse(localStorage.getItem('roles'))[0]
          return pushEventToNotifyService(this.formData).then((response3) => {
            if (response3.success) {
              batchCloseEvents({ projectId: this.formData.projectId, ids: this.formData.row.EventID }).then((response) => {
                if (response.success) {
                  this.formShow = false
                  this.getAlarmList()
                } else {
                  this.msgError('删除失败：\n' + response.errorMsg)
                }
              })
            } else {
              this.msgError('获取事件推送地址失败：\n' + response3.errorMsg)
            }
          })
        }
      })
    },
    // 多选方法
    handleSelectionChange(selection) {
      // console.log(selection)
      // this.idSelections = selection.map((item) => item.EventID)
      this.idSelections = selection.map((item) => item.EventID)
      this.tableSelections = selection
      // this.idSelections = this.tableSelections.map((item) => item.EventID)
      // if (this.tableSelections.length) {
      //   this.disabled = false
      // } else {
      //   this.disabled = true
      // }
    },
    // 批量确认方法
    handleBatchAcknoledge() {
      this.batchFormShow = true
      this.batchFormData.events = this.tableSelections
      this.$set(this.batchFormData, 'notifyType', ['email'])
      this.$set(this.batchFormData, 'projectId', localStorage.getItem('project_id'))
    },
    // 禁止点击
    checkSelectable(row) {
      // console.log(row)
      return row.Acknowledged === '未确认'
    },
    // 批量确认提交
    submitBatchForm() {
      this.$refs['batchFormData'].validate((valid) => {
        if (valid) {
          this.formData.notificationType = null
          this.formData.acknoledger = JSON.parse(localStorage.getItem('roles'))[0]
          return pushEventToNotifyService(this.batchFormData).then((response) => {
            if (response.success) {
              batchCloseEvents({ projectId: this.batchFormData.projectId, ids: this.idSelections }).then((response2) => {
                if (response2.success) {
                  this.batchFormShow = false
                  this.getAlarmList()
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
    },
    // 批量确认提交
    resetBatchForm(formName) {
      this.batchFormShow = false
      // 重置备注和通知方式
      this.formData.notifyType = []
      this.formData.remark = ''
      this.$refs['batchFormData'].resetFields()
    }
    // 查询级别
    // querySeverity() {
    //   getEventSeverityDefine({
    //     name: '',
    //     projectId: 10,
    //     start: 1,
    //     limit: 10
    //   }).then((response) => {
    //     // 赋值SeverityArr
    //     this.SeverityArr = response.datas
    //   })
    // }
  }
}
</script>

<style lang="scss" scoped>
@import '../../assets/styles/index.scss';

.app-container {
  overflow: auto;
  height: calc(100vh - 80px);
}

.dark .overviewBox,
.light .overviewBox {
  // 跳转链接样式
  font-size: 12px;
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

  // 取消按钮样式
  /deep/.dangerButton.el-button--text {
    // color: #fff;
    background: #e63a3a;
    // padding: 4px 5px;
  }
}
.light .overviewBox {
  font-size: 12px;
  .tagLink {
    background-color: transparent;
    border-color: transparent;
    height: 20px;
    // padding: 0 6px;
    line-height: 20px;
    font-size: 12px;
    cursor: pointer;
    color: #333;
    i {
      color: rgba(24, 144, 255, 1);
    }
  }
}

.light {
  .overviewBox {
    // 告警分布
    .centerBox {
      .centerLeft {
        .centerLeftContent {
          .centerLeftContent-item {
            position: relative;
          }

          .centerLeftContent-item:last-child:before {
            display: none;
          }

          .centerLeftContent-item::before {
            content: '';
            width: 70%;
            height: 1px;
            position: absolute;
            bottom: -10%;
            left: 50%;
            transform: translateX(-50%);
            background-color: rgba(24, 144, 255, 1);
          }
        }
      }
    }
  }

  /deep/button.el-button.el-button--text.el-button--mini {
    background: rgba(61, 110, 192, 0.15);
    color: rgba(61, 110, 192, 1);

    .lineBox {
      position: relative;
    }

    .lineBox::before {
      position: absolute;
      top: 37%;
      right: 0;
      content: '';
      height: 50%;
      width: 1px;
      background-color: rgba(223, 223, 223, 1);
    }

    .numCount {
      // text-align: center;
      padding: 3%;
      font-size: 14px;
      line-height: 30px;

      span:nth-child(2) {
        // color:rgba(88, 194, 193, 1);
        font-size: 18px;
        font-weight: 700;
        vertical-align: bottom;
      }
    }

    .numCount {
      text-align: center;
      padding: 2% 5%;
      line-height: 30px;

      span:nth-child(2) {
        // color:rgba(88, 194, 193, 1);
        font-size: 24px;
        vertical-align: bottom;
      }
    }

    .numCount1 {
      text-align: left;
      padding: 6% !important;
    }

    .numCount3 {
      font-size: 12px;
      text-align: left;
      color: rgba(0, 0, 0, 0.65);
      padding: 5% 20% 5% !important;

      .el-row {
        // margin-bottom: 5%!important;
        .el-col:nth-child(2) {
          span {
            font-size: 18px;
            font-weight: 700;
          }
        }
      }
    }
  }
}

.dark {
  .overviewBox {
    // 告警分布
    .centerBox {
      .centerLeft {
        .centerLeftContent {
          .centerLeftContent-item {
            position: relative;
          }

          .centerLeftContent-item:last-child:before {
            display: none;
          }

          .centerLeftContent-item::before {
            content: '';
            width: 70%;
            height: 1px;
            position: absolute;
            bottom: -10%;
            left: 50%;
            transform: translateX(-50%);
            background-color: rgba(24, 144, 255, 1);
          }
        }
      }
    }
  }

  /deep/button.el-button.el-button--text.el-button--mini {
    background: rgba(61, 110, 192, 0.15);
    color: rgba(61, 110, 192, 1);
  }

  .numCount {
    // text-align: center;
    padding: 3%;
    font-size: 14px;
    line-height: 30px;

    span:nth-child(2) {
      // color:rgba(88, 194, 193, 1);
      font-size: 18px;
      font-weight: 700;
      vertical-align: bottom;
    }
  }

  .numCount {
    text-align: center;
    padding: 2% 5%;
    line-height: 30px;

    span:nth-child(2) {
      // color:rgba(88, 194, 193, 1);
      font-size: 24px;
      vertical-align: bottom;
    }
  }

  .numCount1 {
    text-align: left;
    padding: 6% !important;
  }

  .numCount3 {
    font-size: 12px;
    text-align: left;
    color: rgba(215, 215, 215, 1);
    padding: 5% 20% 5% !important;

    .el-row {
      .el-col:nth-child(2) {
        span {
          font-size: 18px;
          font-weight: 700;
        }
      }
    }
  }
}
// 弹框样式修改
.dark,
.light {
  .el-dialog__body {
    .el-form-item {
      margin-bottom: 12px !important;
    }
  }
}
/deep/.el-table__empty-text {
  height: 100%;
}
.collapseBox {
  padding: 5px 16px 5px 10px;
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
</style>
