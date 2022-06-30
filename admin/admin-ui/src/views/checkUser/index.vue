<template>
  <div class="errPage-container">
    <el-card class="box-card">
      <div>
        <div slot="header" class="clearfix" style="margin-bottom:20px">
          <i class="el-icon-warning" style="color:#E6A23C"></i>
          <span>许可证过期，请更新许可</span>
        </div>
        <el-form ref="form" :model="form"  label-width="80px" label-position="left">
          <el-form-item label="认证密钥" prop="dictValue">
            <el-input
              v-model="form.dictValue"
              type="text"
              placeholder="请输入认证密钥"
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitForm" size="small">确 定</el-button>
          </el-form-item>
        </el-form>

      </div>
    </el-card>
  </div>
</template>

<script>
  import {updateDataJudge} from "@/api/system/dict/data";

  export default {
    data() {
      return {
        form: {

        },
      }
    },
    methods: {
      submitForm() {
        this.$refs["form"].validate(valid => {
          updateDataJudge(this.form).then(response => {
            if (response.code === 200) {
              this.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            } else {
              this.msgError(response.msg);
            }
          });
        });
      }
    }
  }
</script>

<style lang="scss" scoped>
  // .errPage-container {
  //   width: 800px;
  //   max-width: 100%;
  //   margin: 100px auto;
  //   .pan-back-btn {
  //     background: #008489;
  //     color: #fff;
  //     border: none!important;
  //   }
  //   .pan-gif {
  //     margin: 0 auto;
  //     display: block;
  //   }
  //   .pan-img {
  //     display: block;
  //     margin: 0 auto;
  //     width: 100%;
  //   }
  //   .text-jumbo {
  //     font-size: 60px;
  //     font-weight: 700;
  //     color: #484848;
  //   }
  //   .list-unstyled {
  //     font-size: 14px;
  //     li {
  //       padding-bottom: 5px;
  //     }
  //     a {
  //       color: #008489;
  //       text-decoration: none;
  //       &:hover {
  //         text-decoration: underline;
  //       }
  //     }
  //   }
  // }
  .box-card{
    color: #18191a;
    padding: 20px;
    // padding-top: 0;
    width: 600px;
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%,-50%);
  }
  /deep/.el-form-item__label {
    color: #606266 !important;
  }
</style>
