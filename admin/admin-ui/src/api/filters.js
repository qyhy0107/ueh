import moment from 'moment'
const vFilters = {
  // 获取产品经理
  getProjectMem(val) {
    if (val && val !== '-') {
      // console.log(val)
      const temp = JSON.parse(val)
      // console.log(temp)
      return temp[0].name
    } else {
      return '-'
    }
  },
  getProjectMemD(val) {
    if (val && val !== '-') {
      const temp = JSON.parse('[' + val + ']')
      // console.log(temp)
      return temp[0].name
    } else {
      return '-'
    }
  },
  getProjectG(val) {
    if (val && val !== '-') {
      const temp = JSON.parse('[' + val + ']')
      // console.log(temp)
      return temp[0].description
    } else {
      return '-'
    }
  },
  // 时间戳转换为日期
  getDate(val) {
    if (val) {
      return moment(val).format('YYYY-MM-DD HH:mm:ss') || '-'
    } else {
      return '-'
    }
  },
  // 运行时长处理
  getFourL(val) {
    if (!val) {
      return '-'
    }
    if (val > 0 && val <= 0.0001) {
      return 0.0001 + '%'
    } else if (val > 0.0001) {
      return ((+val).toFixed(4)) + '%'
    } else {
      return '-'
    }
  },
  getTimeLong(val) {
    if (val === '-') {
      return '-'
    }
    if (val) {
      var theTime = parseInt(val) // 秒
      var theTime1 = 0 // 分
      var theTime2 = 0 // 小时
      var theTime3 = 0 // 天
      if (theTime > 60) {
        theTime1 = parseInt(theTime / 60)
        theTime = parseInt(theTime % 60)
        if (theTime1 > 60) {
          theTime2 = parseInt(theTime1 / 60)
          theTime1 = parseInt(theTime1 % 60)
          if (theTime2 > 60) {
            theTime3 = parseInt(theTime2 / 24)
            theTime2 = parseInt(theTime2 % 24)
          }
        }
      }
      var result = '' + parseInt(theTime) + '秒'
      if (theTime1 > 0) {
        result = '' + parseInt(theTime1) + '分' + result
      } else {
        result = '' + parseInt(theTime1) + '分' + result
      }
      if (theTime2 > 0) {
        result = '' + parseInt(theTime2) + '小时' + result
      }
      if (theTime3 > 0) {
        result = '' + parseInt(theTime3) + '天' + result
      }
      return result
    } else if (val === 0) {
      return '0秒'
    } else {
      return '-'
    }
  },
  getTimeTableLong(val) {
    if (val) {
      var theTime = parseInt(val) // 秒
      var theTime1 = 0 // 分
      var theTime2 = 0 // 小时
      var theTime3 = 0 // 天
      if (theTime > 60) {
        theTime1 = parseInt(theTime / 60)
        theTime = parseInt(theTime % 60)
        if (theTime1 > 60) {
          theTime2 = parseInt(theTime1 / 60)
          theTime1 = parseInt(theTime1 % 60)
          if (theTime2 > 60) {
            theTime3 = parseInt(theTime2 / 24)
            theTime2 = parseInt(theTime2 % 24)
          }
        }
      }
      var result = '' + parseInt(theTime) + '秒'
      if (theTime1 > 0) {
        result = '' + parseInt(theTime1) + '分' + result
      }
      if (theTime2 > 0) {
        result = '' + parseInt(theTime2) + '小时' + result
      }
      if (theTime3 > 0) {
        result = '' + parseInt(theTime3) + '天' + result
      }
      return result
    } else if (val === 0) {
      return '0秒'
    } else {
      return '-'
    }
  },
  getTimeTableLong1(val) {
    if (val) {
      var theTime = parseInt(val / 1000) // 秒
      var theTime1 = 0 // 分
      var theTime2 = 0 // 小时
      var theTime3 = 0 // 天
      if (theTime > 60) {
        theTime1 = parseInt(theTime / 60)
        theTime = parseInt(theTime % 60)
        if (theTime1 > 60) {
          theTime2 = parseInt(theTime1 / 60)
          theTime1 = parseInt(theTime1 % 60)
          if (theTime2 > 60) {
            theTime3 = parseInt(theTime2 / 24)
            theTime2 = parseInt(theTime2 % 24)
          }
        }
      }
      var result = '' + parseInt(theTime) + '秒'
      if (theTime1 > 0) {
        result = '' + parseInt(theTime1) + '分' + result
      }
      if (theTime2 > 0) {
        result = '' + parseInt(theTime2) + '小时' + result
      }
      if (theTime3 > 0) {
        result = '' + parseInt(theTime3) + '天' + result
      }
      return result
    } else if (val === 0) {
      return '0秒'
    } else {
      return '-'
    }
  },
  // 根据收尾时间获取当前时长，并处理时间
  getdiffLongTime(val) {
    const { LastOccurrence, FirstOccurrence } = val
    let tempTime = moment(LastOccurrence).valueOf() - moment(FirstOccurrence).valueOf()
    tempTime = tempTime / 1000
    if (tempTime) {
      var theTime = parseInt(tempTime) // 秒
      var theTime1 = 0 // 分
      var theTime2 = 0 // 小时
      var theTime3 = 0 // 天
      if (theTime > 60) {
        theTime1 = parseInt(theTime / 60)
        theTime = parseInt(theTime % 60)
        if (theTime1 > 60) {
          theTime2 = parseInt(theTime1 / 60)
          theTime1 = parseInt(theTime1 % 60)
          if (theTime2 > 60) {
            theTime3 = parseInt(theTime2 / 24)
            theTime2 = parseInt(theTime2 % 24)
          }
        }
      }
      var result = '' + parseInt(theTime) + '秒'
      if (theTime1 > 0) {
        result = '' + parseInt(theTime1) + '分' + result
      }
      if (theTime2 > 0) {
        result = '' + parseInt(theTime2) + '小时' + result
      }
      if (theTime3 > 0) {
        result = '' + parseInt(theTime3) + '天' + result
      }
      return result
    } else if (tempTime === 0) {
      return '0秒'
    } else {
      return '暂无数据'
    }
  },
  // 时间
  getdiffLongTime1(val) {
    // const { LastOccurrence, FirstOccurrence } = val
    // let tempTime = moment(LastOccurrence).valueOf() - moment(FirstOccurrence).valueOf()
    // if (val === 0) {
    //   return '0s'
    // }
    let tempTime = val
    tempTime = tempTime / 1000
    if (tempTime) {
      var theTime = parseInt(tempTime) // 秒
      var theTime1 = 0 // 分
      var theTime2 = 0 // 小时
      var theTime3 = 0 // 天
      if (theTime > 60) {
        theTime1 = parseInt(theTime / 60)
        theTime = parseInt(theTime % 60)
        if (theTime1 > 60) {
          theTime2 = parseInt(theTime1 / 60)
          theTime1 = parseInt(theTime1 % 60)
          if (theTime2 > 60) {
            theTime3 = parseInt(theTime2 / 24)
            theTime2 = parseInt(theTime2 % 24)
          }
        }
      }
      var result = '' + parseInt(theTime) + '秒'
      if (theTime1 > 0) {
        result = '' + parseInt(theTime1) + '分' + result
      }
      if (theTime2 > 0) {
        result = '' + parseInt(theTime2) + '小时' + result
      }
      if (theTime3 > 0) {
        result = '' + parseInt(theTime3) + '天' + result
      }
      return result
    } else if (tempTime === 0) {
      return '0秒'
    } else {
      return '-'
    }
  },
  // 自动更换单byte单位
  getNumUnit(val) {
    if (!val) return '-'
    if (+val === 0) return 0
    var size = ''
    // console.log(val)
    if (val < 0.1 * 1024) {
      // 如果小于0.1KB转化成B
      size = val.toFixed(2) + ' B'
    } else if (val < 0.1 * 1024 * 1024) {
      // 如果小于0.1MB转化成KB
      size = (val / 1024).toFixed(2) + ' KB'
    } else if (val < 0.1 * 1024 * 1024 * 1024) {
      // 如果小于0.1GB转化成MB
      size = (val / (1024 * 1024)).toFixed(2) + ' MB'
    } else {
      // 其他转化成GB
      size = (val / (1024 * 1024 * 1024)).toFixed(2) + ' GB '
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
  // 单位转换、s
  getNumUnitBPS(val) {
    if (!val) return '-'
    if (val === '0') return '0 Bps'
    var size = ''
    if (val < 0.1 * 1024) {
      // 如果小于0.1KB转化成B
      size = val.toFixed(2) + ' Bps'
    } else if (val < 0.1 * 1024 * 1024) {
      // 如果小于0.1MB转化成KB
      size = (val / 1024).toFixed(2) + ' KBps'
    } else if (val < 0.1 * 1024 * 1024 * 1024) {
      // 如果小于0.1GB转化成MB
      size = (val / (1024 * 1024)).toFixed(2) + ' MBps'
    } else {
      // 其他转化成GB
      size = (val / (1024 * 1024 * 1024)).toFixed(2) + ' GBps'
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
  getMMUnit3(val) {
    if (val === 0) {
      return 0
    }
    if (!val) {
      return '-'
    }
    if (!isNaN(val)) {
      var size = ''
      size = (val / 1024 / 1024).toFixed(2)
    }
    var sizestr = size + ''
    var len = sizestr.indexOf('.')
    var dec = sizestr.substr(len + 1, 2)
    if (dec === '00') {
      // 当小数点后为00时 去掉小数部分
      return sizestr.substring(0, len) + sizestr.substr(len + 3, 2)
    }

    return size + 'MB' || '-'
  },
  getMMUnit1(val) {
    if (val === 0) {
      return 0
    }
    if (!val) {
      return '-'
    }
    if (!isNaN(val)) {
      var size = ''
      size = (val / 1024 / 1024).toFixed(2)
    }
    var sizestr = size + ''
    var len = sizestr.indexOf('.')
    var dec = sizestr.substr(len + 1, 2)
    if (dec === '00') {
      // 当小数点后为00时 去掉小数部分
      return sizestr.substring(0, len) + sizestr.substr(len + 3, 2)
    }

    return size || '-'
  },
  getMMUnit2(val) {
    if (!isNaN(val)) {
      var size = ''
      size = (val / 1000 / 1000) + ' Mbps'
    }
    if (val === 0) {
      return '0'
    }
    return size || '-'
  },
  getMMUnit24(val) {
    if (!isNaN(val)) {
      var size = ''
      size = val + ' Mbps'
    }
    if (val === 0) {
      return '0'
    }
    return size || '-'
  },
  // 将单位转换为Gbps
  getMMUnit(val) {
    if (!isNaN(val)) {
      var size = ''
      size = (val / 1000 / 1000)
    }
    if (val === 0) {
      size.replace('00', '')
      return '-'
    }

    return size || '-'
  },
  NotNULL(val) {
    if (val && val !== 'null') {
      return val
    } else {
      return ''
    }
  },
  getKbpsUnit(val) {
    if (val == 0) {
      return 0
    }
    if (!isNaN(val)) {
      var size = ''
      size = val ? (val / 1000).toFixed(2) : '-'
    }
    return size || '-'
  },
  getKbpsUnit3(val) {
    if (val === 0) {
      return '0'
    }
    if (!isNaN(val)) {
      var size = ''
      size = val ? (val / 1024).toFixed(2) : '-'
    }
    return size || '-'
  },
  getKbpsUnit1(val) {
    if (!isNaN(val)) {
      if (val === 0) {
        return '0'
      }
      var size = ''
      size = val ? (val / 1000).toFixed(2) + ' Kbps' : '-'
    }
    return size || '-'
  },
  // 百分比保留两位数
  getTwoUnitPer(val) {
    const num = val * 100
    return num > 0.01 ? num.toFixed(2) : '-'
  },
  getTwo(val) {
    return val.toFixed(2) || '-'
  },
  // 浮點保留兩位數
  toDecimal(f) {
    if (isNaN(f)) {
      return '-'
      // return
    }
    f = Math.round(f * 100) / 100
    return f
  },
  // 浮點保留兩位數帶百分比
  toDecimalPer(f) {
    // console.log(f)
    if (isNaN(f) || f === null) {
      return '-'
    }
    f = Math.round(f * 100) / 100
    // return f + '%'
    return ((f + '').indexOf('.') !== -1) ? f + ' %' : +f.toFixed(2) + ' %'
  },
  // 返回百分比单位
  toPer(val) {
    if (val || val === 0 || val === '0') {
      return val + ' %'
    } else {
      return '-'
    }
  },
  isZero(val) {
    if (val > 0) {
      return val
    } else if (val === 0) {
      return 0
    } else {
      return '-'
    }
    // return val > 0 ? val : '-'
  },
  isNaN(val) {
    return val || '-'
  },
  getNumLength(val) {
    if (val !== '-') {
      if (val / 1000 < 1) {
        return val
      } else if (val / 1000 >= 1 && val / 1000000 < 1) {
        return (val / 1000).toFixed(2) + ' K'
      } else if (val / 1000000 >= 1 && val / 1000000000 < 1) {
        return (val / 1000 / 1000).toFixed(2) + ' M'
      } else {
        return (val / 1000 / 1000 / 1000).toFixed(2) + ' B'
      }
    } else {
      return val
    }
  },
  getStartDate(val) {
    if (!val) {
      return '-'
    }
    return moment(val * 1000).format('YYYY-MM-DD HH:mm:ss')
  },
  getStartDate1(val) {
    // console.log(val)
    if (!val) {
      return '-'
    }
    const time = new Date().getTime()
    // console.log(time)
    return moment(time - (val * 1000)).format('YYYY-MM-DD HH:mm:ss')
  },
  getDateTimeLong(val) {
    // getdiffLongTime(val) {
    const { LastOccurrence, FirstOccurrence, EventType, RecoveredStatus } = val
    const momentDate = moment().format('x')
    var tempTime = null
    if (EventType === 'P') {
      return '-'
    } else if (EventType === 'S' && RecoveredStatus === '未恢复') {
      tempTime = momentDate - moment(FirstOccurrence).valueOf()
    } else if (EventType === 'S' && RecoveredStatus === '已恢复') {
      tempTime = moment(LastOccurrence).valueOf() - moment(FirstOccurrence).valueOf()
    }
    tempTime = tempTime / 1000
    if (tempTime) {
      var theTime = parseInt(tempTime) // 秒
      var theTime1 = 0 // 分
      var theTime2 = 0 // 小时
      var theTime3 = 0 // 天
      if (theTime > 60) {
        theTime1 = parseInt(theTime / 60)
        theTime = parseInt(theTime % 60)
        if (theTime1 > 60) {
          theTime2 = parseInt(theTime1 / 60)
          theTime1 = parseInt(theTime1 % 60)
          if (theTime2 > 60) {
            theTime3 = parseInt(theTime2 / 24)
            theTime2 = parseInt(theTime2 % 24)
          }
        }
      }
      var result = '' + parseInt(theTime) + '秒'
      if (theTime1 > 0) {
        result = '' + parseInt(theTime1) + '分' + result
      }
      if (theTime2 > 0) {
        result = '' + parseInt(theTime2) + '小时' + result
      }
      if (theTime3 > 0) {
        result = '' + parseInt(theTime3) + '天' + result
      }
      return result
    } else if (tempTime === 0) {
      return '0秒'
    } else {
      return '暂无数据'
    }
    // }
  }
}

export default vFilters
