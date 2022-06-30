<template>
  <div>
    <!-- 查询区域 -->
    <el-form ref="itemParams" :inline="true" class="mt10" @submit.native.prevent>
      <el-form-item label="指标名称/key/值" label-width="140px">
        <el-input v-model="KeyParams.name" placeholder="请输入指标名称/key/值" clearable size="small" @keyup.enter.native="resourceKeys" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="mini" icon="el-icon-search" @click="resourceKeys()">查询</el-button>
        <el-button size="mini" icon="el-icon-refresh" @click="reset">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 表格区域 -->
    <el-table v-model="itemList" v-loading="loading" :data="itemList" height="50vh" element-loading-text="拼命加载中..." empty-text=" " border>
      <template v-if="!loading" slot="empty" style="height:100%">
        <Deficiency v-if="!loading" height="40%" width="auto" />
      </template>
      <el-table-column align="left" label="指标名称" prop="name" :show-overflow-tooltip="true" />
      <el-table-column align="left" label="指标key" prop="key_" :show-overflow-tooltip="true" />
      <el-table-column align="left" label="当前值" prop="value" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          {{ scope.row.value | isNaN }}
        </template>
      </el-table-column>
      <el-table-column align="left" label="单位" prop="units" :show-overflow-tooltip="true" width="80">
        <template slot-scope="scope">
          {{ scope.row.units | isNaN }}
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="KeyParams.total > 0" :page-sizes="[10, 20, 50, 200]" :total="KeyParams.total" :page.sync="KeyParams.start" :limit.sync="KeyParams.limit" @pagination="resourceKeys" />
  </div>
</template>

<script>
import Deficiency from '@/components/Deficiency/index'
import { getNamesByHost1 } from '@/api/operation/server'
export default {
  components: {
    Deficiency
  },
  filters: {},
  props: {
    tag: {
      type: String,
      default: null
    },
    classId: {
      type: Number,
      default: null
    },
    // host值
    host: {
      type: String,
      default: null
    },
    label: {
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
      loading: false,
      itemList: [], // 更多指标表格数据
      KeyParams: {
        projectId: 10,
        host: '',
        engine: 'Freemarker',
        start: 1,
        limit: 50,
        total: 0,
        name: ''
      } // 更多指标查询条件对象
    }
  },

  watch: {
    ifChange: {
      handler(val, oldval) {
        this.KeyParams.start = 1
        this.resourceKeys()
      }
    },
    itemList: {
      handler(val, oldval) {
        this.itemList = val
      },
      deep: true,
      immediate: false
    }
  },
  created() {
    // 获取表格数据
    this.resourceKeys()
  },
  activated() {
    this.resourceKeys()
  },
  methods: {
    // 更多
    resourceKeys() {
      this.KeyParams.host = this.host
      this.KeyParams.label = this.label
      this.KeyParams.classId = this.classId
      this.KeyParams.tag = this.tag
      this.loading = true
      //   判断host是否存在
      if (this.host) {
        getNamesByHost1(this.KeyParams).then((response) => {
          this.itemList = response.datas
          this.KeyParams.total = response.totalCount
          this.loading = false
        })
      } else {
        this.itemList = []
        this.KeyParams.total = 0
        this.loading = false
      }
    },
    // 更多参数重置
    reset() {
      this.KeyParams.name = ''
      this.resourceKeys()
    }
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
/deep/.el-form-item__label {
  width: 120px;
}
@media screen and (max-width: 1400px) {
  .dark,
  .light {
    // .tableBox .el-input__inner {
    //   height: 20px!important;
    //   line-height: 20px!important;
    //   font-size: 10px!important;
    //   padding: 0px 10px !important;
    // }
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
  }
  .mt10 {
    margin-top: 0;
  }
  .el-form-item {
    margin-bottom: 0;
    // margin-top: 6px;
  }
  /deep/.el-form-item__label {
    width: 100px;
  }
  // .pagination-container {
  //   margin-top: 0;
  //   font-size: 10px;
  //   padding: 0;
  //   .el-pagination__total {
  //     font-size: 10px;
  //   }
  //   .el-pagination button {
  //     font-size: 10px;
  //     height: 20px;
  //     line-height: 20px;
  //   }
  //   .el-pager li {
  //     font-size: 10px;
  //     height: 24px;
  //     line-height: 24px;
  //   }
  //    .el-pagination__jump{
  //     font-size: 10px;
  //     height: 24px;
  //     line-height: 24px;
  // }
  // }
}
</style>
