<template>
  <div>
    <el-form ref="formArm" :inline="true" class="mt10 alarmConmpentBox" style="padding-left:10px">
      <!-- <el-row>
        <el-col :span="13"> -->
      <!-- <el-form-item label="告警状态">
        <el-select v-model="formArm.Acknowledged" style="width:100px" placeholder="请选择" filterable clearable size="small" @change="getAlarmList">
          <el-option label="未恢复" :value="0" />
          <el-option label="已恢复" :value="1" />
        </el-select>
      </el-form-item> -->
      <!-- <el-form-item label="告警类型">
        <el-select v-model="formArmisComponent" clearable size="small" style="width:120px" @change="getAlarmList">
          <el-option label="设备告警" :value="1" />
          <el-option label="组件告警" :value="0" />
        </el-select>
      </el-form-item> -->

      <el-form-item label="事件类型:">
        <el-select v-model="formArm.isEventSeverityType" size="mini" clearable style="width:90px" @change="getAlarmList">
          <el-option value="2" label="恢复事件" />
          <el-option value="1" label="告警事件" />
        </el-select>
      </el-form-item>

      <el-form-item label="确认:">
        <el-select v-model="formArm.isAcknowledged" size="mini" style="width:100px" clearable @change="getAlarmList">
          <el-option label="确认" :value="1" />
          <el-option label="未确认" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="恢复:">
        <el-select v-model="formArm.isRecoveredStatus" size="mini" style="width:100px" clearable @change="getAlarmList">
          <el-option label="已恢复" :value="2" />
          <el-option label="未恢复" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="级别">
        <el-select v-model="formArm.level" size="small" clearable collapse-tags multiple style="width:130px" @change="getAlarmList">
          <el-option v-for="item in SeverityArr" :key="item.name" :label="item.name" :value="item.name" />
        </el-select>
      </el-form-item>
      <!-- </el-col>
        <el-col :span="11"> -->
      <el-form-item label="时间范围">
        <el-date-picker v-model="dateSelected" size="small" class="dateClass" type="datetimerange" :default-time="['24:00:00']" unlink-panels range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" />
        <!-- </el-form-item>
      <el-form-item> -->
        <!-- <el-form-item style="float:right"> -->
        <el-button type="primary" size="mini" icon="el-icon-search" @click="getAlarmList()">查询</el-button>
        <el-button size="mini" icon="el-icon-refresh" @click="reset">重置</el-button>
        <!-- <el-button type="primary" size="mini" icon="el-icon-search" @click="handleBatchAcknoledge()">批量确认</el-button> -->
        <el-button class="addButton" :disabled="disabled" size="mini" @click="handleBatchAcknoledge">批量确认</el-button>
      </el-form-item>
      <!-- </el-col>
      </el-row> -->
    </el-form>
    <!-- 表格区域 -->
    <div class="tableBox">
      <div class="tableBox-right">
        <el-table v-loading="loading" empty-text=" " :data="dataListAlarm" min-height="45vh" height="50vh" element-loading-text="拼命加载中..." @row-dblclick="openDialog" @selection-change="handleSelectionChange">
          <template v-if="!loading" slot="empty" style="height:100%">
            <Deficiency height="40%" width="auto" />
          </template>
          <el-table-column type="selection" :selectable="checkSelectable" width="45" align="center" />
          <el-table-column align="center" label="级别" prop="SeverityName" :show-overflow-tooltip="true" width="80">
            <template slot-scope="scope">
              <span :style="{ color: scope.row.SeverityName == '未知' ? '#0587ff' : scope.row.SeverityName == '通知' || scope.row.SeverityName == '警告' ? '#67c23a' : scope.row.SeverityName == '问题' || scope.row.SeverityName == '严重' ? '#e6a23c' : '#f56c6c' }">{{ scope.row.SeverityName }}</span>
            </template>
          </el-table-column>
          <!-- 传入类型值 -->
          <!-- <el-table-column align="left" label="资源类别" :show-overflow-tooltip="true" width="100" >
            <template>{{ pLabelText }}</template>
          </el-table-column>
          <el-table-column align="left" label="资产名" prop="NodeAlias" :show-overflow-tooltip="true" /> -->
          <el-table-column align="left" label="发生时间" prop="FirstOccurrence" :show-overflow-tooltip="true" width="200" />
          <!-- <el-table-column align="left" label="结束时间" prop="LastOccurrence" :show-overflow-tooltip="true" width="200" /> -->

          <el-table-column align="left" label="持续时长" prop="sec" :show-overflow-tooltip="true" width="150">
            <template slot-scope="scope">
              <!-- {{ scope.row | getDateTimeLong }} -->
              <span v-if="scope.row.RecoveredStatus === '未恢复'"> {{ (momentDate - scope.row.time1) | getdiffLongTime1 }}</span>
              <!-- 否则 是不是已恢复事件  是↓ 末次-首次 -->
              <span v-else-if="scope.row.RecoveredStatus === '已恢复'">{{ (scope.row.time2 - scope.row.time1) | getdiffLongTime1 }}</span>
            </template>
          </el-table-column>
          <el-table-column align="left" prop="EventSeverityType" label="事件类型" width="110">
            <template slot-scope="scope">
              <span v-if="scope.row.EventSeverityType == 1">
                告警事件
              </span>
              <span v-if="scope.row.EventSeverityType == 2">
                恢复事件
              </span>
            </template>
          </el-table-column>
          <!-- <el-table-column align="left" label="事件类型" prop="isComponent" :show-overflow-tooltip="true" width="90" /> -->
          <el-table-column align="left" label="恢复" prop="RecoveredStatus" :show-overflow-tooltip="true" width="90" />
          <el-table-column align="left" label="确认" prop="Acknowledged" :show-overflow-tooltip="true" width="90" />
          <el-table-column align="left" label="描述" prop="Summary" :show-overflow-tooltip="true" />
          <el-table-column align="center" label="操作" prop="concern" :show-overflow-tooltip="true" :width="100">
            <template slot-scope="scope">
              <el-button v-if="scope.row.Acknowledged === '未确认'" type="text" size="mini" icon="el-icon-edit" style="background:none;border:1px solid none;color:#5588ed" @click="handleUpdate(scope.row)">确认</el-button>
              <!-- <el-button v-else type="text" size="mini" icon="el-icon-view" style="background:none;border:1px solid none;color:#5588ed" @click="handleUpdate(scope.row)">查看</el-button> -->
              <el-button v-else size="mini" type="text" icon="el-icon-view" @click="handleUpdate1(scope.row)">确认记录</el-button>
            </template>
          </el-table-column>
        </el-table>
        <!-- <Deficiency v-else height="auto" width="25%" :style="{ height: OperateBoxHeight - 180 + 'px' }"></Deficiency> -->
        <pagination v-show="totalAlarm > 0" :total="totalAlarm" :page.sync="KeyParams.start" :limit.sync="KeyParams.limit" :page-sizes="[10, 20, 50, 200]" @pagination="getAlarmList()" />
      </div>
    </div>
    <!-- 表单区域 -->
    <el-dialog :title="formData.row.Acknowledged !== '确认' ? '告警确认' : '查看'" :visible.sync="formShow" width="950px">
      <el-form ref="formData" :model="formData" label-width="120px" class="demo-ruleForm">
        <!-- <el-form ref="formData" :model="formData" :rules="rules" label-width="110px" class="demo-ruleForm"> -->
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
                  提示：<br />
                  1.通知对象不填写时，符合规则的事件将通过事件分类发送对应角色的对象<br />
                  2.点击右侧填充按钮，将填充事件分类发送对应角色的对象<br />
                  3.通知对象请填写华融域账号，通知对象有多个时,用#分割
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
                  <el-input v-if="formData.row.Acknowledged" value="未确认" :readonly="true" />
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
          <div v-if="showCheck" style="padding:15px;">
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
                <el-timeline-item v-for="(activity, index) in activities" :key="index" color="#5588ed" icon="el-icon-warning-outline" size="large" placement="top">
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
  </div>
</template>

<script>
import Deficiency from '@/components/Deficiency/index'
import moment from 'moment'
import { batchCloseEvents, pushEventToNotifyService, getPerson, getPersonShow } from '@/api/event/event-manage'
import { getEventSeverityDefine } from '@/api/publicResources/eventLevel'
import { getEventColumnDefine } from '@/api/publicResources/eventField'
import { getAlarmList } from '@/api/operation/serverOverview'
import axios from 'axios'
export default {
  components: {
    Deficiency
  },
  filters: {},
  props: {
    alarmtype: {
      type: String,
      default: null
    },
    // host值
    host: {
      type: String,
      default: null
    },
    flag: {
      type: String,
      default: null
    },
    // 设备分类
    pLabelText: {
      type: String,
      default: null
    },
    // 用于判断是否需要刷新数据
    // eslint-disable-next-line vue/require-prop-types
    ifChange: {
      default: null
    }
  },
  data() {
    return {
      tableData: [],
      activities: [],
      showCheck: false,
      confirmObj: {
        work_order_id: ''
      },
      momentDate: moment().format('x'),
      loading: false,
      SeverityArr: [], // 级别数组
      disabled: true,
      formArm: { Acknowledged: '', level: '', isComponent: '', isAcknowledged: '', isEventSeverityType: '', isRecoveredStatus: '', RecoveredStatus: '' }, // 事件查询区
      totalAlarm: 0,
      dataListAlarm: [], // 列表
      dateSelected: '', // input时间
      KeyParams: {
        projectId: 10,
        host: '',
        engine: 'Freemarker',
        start: 1,
        limit: 20,
        total: 0,
        name: ''
      },
      formData: {
        row: {},
        notifyType: [],
        remark: ''
      }, // 工单确认对象
      formShow: false, // 弹框是否打开
      formShow1: false, // 弹框是否打开
      active1: {
        tab: 'one', // 关键指标 性能详情 事件tab
        menu: '' // 性能详情 指标组menu
      },
      labelPosition: 'left',
      tableColumns: [], // 表单项
      // // 级别数组
      // SeverityArr: [],
      // 表格选中记录
      idSelections: [],
      // idSelections: [],
      tableSelections: [],
      batchFormShow: false, // 批量确认弹框是否打开
      batchFormData: {
        notifyType: [],
        remark: ''
      }
    }
  },

  watch: {
    'active1.tab': function(newValue, oldValue) {
      this.getPersonShow()
    },
    labelPosition: function(newValue, oldValue) {
      this.getPersonShow()
    },
    ifChange: {
      handler(val, oldval) {
        // this.getAlarmList()
        this.reset()
      }
    }
  },
  created() {
    // 获取表格数据
    this.getAlarmList()
    // 查询级别
    // this.querySeverity()
    // 查询表单内容
    this.getTableColumns()
    // 获取级别
    this.querySeverity()
  },
  // mounted() {
  //   this.getAlarmList()
  //   // 查询级别
  //   // this.querySeverity()
  //   // 查询表单内容
  //   this.getTableColumns()
  //   // 获取级别
  //   this.querySeverity()
  // },
  activated() {
    // this.resourceKeys()
  },
  methods: {
    openWindows(host) {
      window.open(host, '_blank')
    },
    // 禁止点击
    checkSelectable(row) {
      // console.log(row)
      return row.Acknowledged === '未确认'
    },
    getStr(val) {
      if (!val) {
        return
      }
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
    // 获取级别
    querySeverity() {
      getEventSeverityDefine({
        name: '',
        projectId: 10
      }).then((response) => {
        // 赋值SeverityArr
        // console.log(response)
        if (!response.datas) return
        this.SeverityArr = response.datas
        this.SeverityArr = this.SeverityArr.filter((item) => item.isEnable === true)
      })
    },
    // 获取时间戳
    getUnixTimeStamp(flag) {
      let { startTime, endTime } = this.getTimeStamp(flag)
      startTime = moment(startTime).valueOf() / 1000
      endTime = moment(endTime).valueOf() / 1000
      return { startTime, endTime }
    },
    // 获取告警列表
    getAlarmList(flag) {
      this.loading = true
      this.clickButtonAlarm = flag
      if (this.dateSelected) {
        // 为选择日期
        var startTime = this.dateSelected[0] / 1000
        var endTime = this.dateSelected[1] / 1000
      } else {
        // 传了flag,为固定日期
        // eslint-disable-next-line no-redeclare
        // var { startTime, endTime } = this.getUnixTimeStamp('1d')
        this.dateSelected = ''
      }
      // let { host } = this.routerInfo
      const { isEventSeverityType, Acknowledged, level, isComponent, isRecoveredStatus, isAcknowledged } = this.formArm
      // host = host.split(' ')
      let flagInfo = null
      if (this.flag) {
        flagInfo = '进程[' + this.flag + ']'
      }
      // console.log(this.alarmtype)
      if (this.host) {
        getAlarmList({
          Acknowledged,
          level,
          startTime,
          endTime,
          // nowDate: moment().startOf('day').format('x'),
          nowDate: moment()
            .subtract(24, 'hours')
            .format('x'),
          isComponent: isComponent,
          isEventSeverityType: isEventSeverityType,
          isRecoveredStatus: isRecoveredStatus,
          isAcknowledged: isAcknowledged,
          start: this.KeyParams.start,
          limit: this.KeyParams.limit,
          resourceKeys: [this.host],
          clabel_text: this.alarmtype ? [this.alarmtype] : null,
          flag: flagInfo,
          projectId: 10,
          engine: 'Freemarker'
        }).then((response) => {
          this.loading = false
          this.totalAlarm = response['totalCount']
          this.dataListAlarm = response['datas']
        })
      } else {
        this.loading = false
        this.totalAlarm = 0
        this.dataListAlarm = []
      }
    },
    // 时间戳处理
    getTimeStamp(flag) {
      let endTime = moment().format('YYYY-MM-DD HH:mm:ss')
      let startTime = moment()
        .subtract(1, 'h')
        .format('YYYY-MM-DD HH:mm:ss')
      if (flag === '1h') {
        startTime = moment()
          .subtract(1, 'h')
          .format('YYYY-MM-DD HH:mm:ss')
      } else if (flag === '1d') {
        startTime = moment().format('YYYY-MM-DD 00:00:00')
        endTime = moment().format('YYYY-MM-DD 23:59:59')
      } else if (flag === '1w') {
        startTime = moment()
          .subtract(6, 'd')
          .format('YYYY-MM-DD 00:00:00')
        endTime = moment().format('YYYY-MM-DD 23:59:59')
      } else if (flag === '1m') {
        startTime = moment()
          .subtract(1, 'M')
          .format('YYYY-MM-DD 00:00:00')
        endTime = moment().format('YYYY-MM-DD 23:59:59')
      }
      return { startTime, endTime }
    },
    // 重置
    reset() {
      this.KeyParams.start = 1
      // this.formArm.Acknowledged = ''
      // this.formArm.level = ''
      // this.formArm.isComponent = ''
      this.formArm = { Acknowledged: '', level: '', isComponent: '', isAcknowledged: '', isEventSeverityType: '', RecoveredStatus: '' }
      this.dateSelected = ''
      this.isComponent = ''
      // this.getAlarmList('1d')
      this.getAlarmList()
    },
    // 打开弹框
    handleUpdate(row) {
      this.formShow = true
      this.momentDate = moment().format('x')
      this.formData.row = row
      this.$set(this.formData, 'notifyType', ['email'])
      this.$set(this.formData, 'projectId', localStorage.getItem('project_id'))
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
      this.formData.row = JSON.parse(JSON.stringify(row))
      this.$set(this.formData, 'notifyType', ['email'])
      this.$set(this.formData, 'person', '')
      this.$set(this.formData, 'projectId', localStorage.getItem('project_id'))
      this.getPersonShow()
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
      // console.log(row)
      if (row.Acknowledged === '未确认') {
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
      this.idSelections = selection.map((item) => item.EventID)
      this.tableSelections = selection
      if (this.tableSelections.length) {
        this.disabled = false
      } else {
        this.disabled = true
      }
    },
    // 批量确认方法
    handleBatchAcknoledge() {
      this.batchFormShow = true
      this.batchFormData.events = this.tableSelections
      this.$set(this.batchFormData, 'notifyType', ['email'])
      this.$set(this.formData, 'person', '')
      this.$set(this.batchFormData, 'projectId', localStorage.getItem('project_id'))
    },
    // handleBatchAcknoledge() {
    //   this.batchFormShow = true
    //   this.batchFormData.events = this.tableSelections
    //   this.$set(this.formData, 'person', '')
    //   this.$set(this.batchFormData, 'notifyType', ['email'])
    //   this.$set(this.batchFormData, 'projectId', this.queryParams.projectId)
    //   // })
    // },
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
/deep/.el-table__empty-text {
  height: 100%;
}
/deep/.el-table--medium th,
/deep/.el-table--medium td {
  padding: 5px 0;
}
// 弹框样式修改
.dark,
.light {
  .el-dialog__body {
    .el-form-item {
      margin-bottom: 12px !important;
    }
  }
  .dateClass {
    width: 400px;
    margin-right: 20px;
  }
}

@media screen and (max-width: 1400px) {
  .dark,
  .light {
    .alarmConmpentBox {
      .el-select.el-select--small {
        width: 90px;
      }
      /deep/.el-form-item__label {
        padding-right: 5px;
      }
      /deep/.el-button--mini {
        margin-bottom: 2px;
        i {
          display: none;
        }
      }
    }
    .pagination-container {
      // height:30px;
      // line-height:30px;
      margin: 0;

      /deep/.el-pagination {
        padding: 0;
        padding-right: 15px;
        button {
          font-size: 10px;
          margin-top: 2px;
          height: 24px !important;
          line-height: 26px !important;
        }
        .el-pager li {
          margin-top: 2px;
          font-size: 10px;
          height: 24px;
          line-height: 24px;
        }
        .el-pagination__total,
        .el-pagination__jump {
          font-size: 10px !important;
          margin-left: 12px !important;
        }
      }
    }
    .dateClass {
      width: 352px;
      margin-right: 0;
    }
    .el-form-item {
      margin-bottom: 0px;
      padding-top: 2px;
      // margin-top: 6px;
    }
    .mt10 {
      margin-top: 0;
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
</style>
