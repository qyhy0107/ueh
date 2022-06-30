# 事件宝

#### 介绍
事件宝是竭峙信息自主设计、研发的IT数据中心事件管理平台，提供事件接入、处置策略配置、通知策略配置等全流程事件处理。核心功能包括：
1、	事件管理：可对事件进行统一的查看，搜索。
2、	配置管理：可新增事件接入、处置策略、通知策略，处置策略包括事件压缩、事件恢复、事件分组、事件升降级、事件屏蔽等。
事件宝功能由前端配置操作、后端事件处理两部分组成。前端配置操作负责平台运行所需的参数配置，参数包括事件接入、处理策略、事件通知分享策略、事件人工操作等。后端事件处理程序读取前端配置操作配置的参数进行事件处理。告警处理后端从事件接入、事件处理、事件转发分阶段对事件进行处理。

#### 安装
安装四步走：

##### 1.预安装准备
1.	防火墙、SELinux关闭
使用命令
<table>
<tr>
	<td>顺序</td>
	<td>命令</td>
	<td>说明</td>
	<td></td>
</tr>
<tr>
	<td>1</td>
	<td>setenforce 0</td>
	<td>临时关闭SELinux</td>
	<td></td>
</tr>
1	setenforce 0	临时关闭SELinux
2	sed -i 's#SELINUX=enforcing#SELINUX=disabled#' /etc/selinux/config	永久关闭SELinux
3	systemctl disable firewalld.service	禁用防火墙服务
4	systemctl stop firewalld.service	关闭防火墙服务
2.	部署软件
安装准备前，一台系统为 CentOS7.X 的虚拟机或物理机，并安装完成 PostgreSQL、Tomcat、JDK软件，简单 yum 安装或源码编译安装即可。
软件版本如下：
	PostgreSQL 13.7
	Tomcat 9.0.64
	JDK 1.8

#####介质下载
1.	下载编译完成的包至/tmp/plugin目录
使用命令
顺序	命令	说明
1	mkdir -p /tmp/plugin	创建介质存放目录
2	cd /tmp/plugin	进入介质存放目录
3	wget http://101.133.166.29/download/sjb.tar.gz	下载事件宝介质
4	tar zxvf sjb.tar.gz	解压事件宝介质


#####初始化数据库
	初始化PostgreSQL数据库
使用命令
顺序	命令	说明
1	su - postgres	切换至PostgreSQL的postgres用户
2	cd /tmp/plugin/sql	进入事件宝数据库存放目录
3	psql <ueh_all.sql	导入事件宝数据库


#####告警平台部署
	告警平台是使用微服务方式部署，主要是通过jar包方式运行。
1.	介质说明
类型	文件名称	说明
dispatcher	ueh-dispatcher	事件分发
transmit	ueh-transmit-default	通知处理
zabbix	ueh-probe-api-server	ZABBIX事件接入
	ueh-handler-default	ZABBIX事件处理
2.	目录说明
以ueh-handler为例,说明文件如下
顺序	路径	说明
1	config/application.yml	Spring Boot配置文件
2	config/log4j2.xml	日志配置文件
3	lib/	依赖jar包目录
4	ueh-probe-api-server.jar	主程序jar包
在config/log4j2.xml日志配置文件，日志默认存放路径为/var/log，可以通过以下参数修改日志路径及名称
<property name="LOG_HOME">/var/log </property>
<RollingFile name="rollingFile" fileName="${LOG_HOME}/ueh-probe-api-server.log" filePattern="${LOG_HOME}/$${date:yyyy}/ueh-probe-api-server-%d{yyyy-MM-dd}-%i.log">

3.	告警平台部署，以将zabbix接收事件模块、公共消息分发模块、zabbix事件处理模块、公共消息发送模块安装，安装前需要创建程序安装目录/app/ueh目录
使用命令
顺序	命令	说明
1	mkdir -p /app/ueh	创建事件宝运行目录

需要修改ueh-handler-default、ueh-transmit-default文件名称下面config目录的application.yml文件连接PostgreSQL数据库的地址、用户名、密码
	ueh-handler-default
使用命令
顺序	命令	说明
1	vim /app/ueh/ueh-handler-default/config/application.yml	打开ueh-handler-default的配置文件
2	jdbc:postgresql://localhost:5432/ueh?currentSchema=ueh_admin&TimeZone=PRC
username: root
password: 123456	修改handler连接PostgreSQL数据库的地址、用户名、密码
3	cd /app/ueh/ueh-handler-default/	切换至程序根目录
4	nohup java -jar ueh-handler-default.jar 2>&1 &	启动模块

	ueh-transmit-default
使用命令
顺序	命令	说明
1	mv /tmp/plugin/ueh/ueh-transmit-default /app/ueh	复制安装文件至程序安装目录
2	vim /app/ueh/ueh-transmit-default/config/application.yml	打开ueh-transmit-default的配置文件
3	jdbc:postgresql://localhost:5432/ueh?currentSchema=ueh_admin&TimeZone=PRC
username: root
password: 123456	修改transmit连接PostgreSQL数据库的地址、用户名、密码
4	cd /app/ueh/ueh-transmit-default	切换至程序根目录
5	nohup java -jar ueh-transmit-default.jar 2>&1 &	启动模块

	ueh-dispatcher
使用命令
顺序	命令	说明
1	mv /tmp/plugin/ueh/ueh-dispatcher /app/ueh	复制安装文件至程序安装目录
2	cd /app/ueh/ueh-dispatcher/	切换至程序根目录
3	nohup java -jar ueh-dispatcher.jar 2>&1 &	启动模块

	ueh-probe-api-server
使用命令
顺序	命令	说明
1	mv /tmp/plugin/ueh/ueh-probe-api-server /app/ueh	复制安装文件至程序安装目录
2	cd /app/ueh/ueh-probe-api-server/	切换至程序根目录
3	nohup java -jar ueh-probe-api-server.jar 2>&1 &	启动模块

#####Zabbix告警接入
前提条件，Zabbix监控系统已部署成功且正常使用。
1.	上传介质
介质名称：zabbix_socket.py
Zabbix告警集成的脚本需要上传zabbix_server.conf定义的alertscripts目录下。
Alertscripts目录截图：
 
${datadir}这个变量：<zabbix的安装目录>/share，例如：zabbix的编译安装目录是/app/zabbix/server,那么对应的AlertScriptPath=/app/zabbix/server/share/zabbix/alertscripts
使用命令
顺序	命令	说明
1	将/tmp/plugin/zabbix_socket.py上传至zabbix服务器	
2	cp /tmp/zabbix_socket.py /app/zabbix/server/share/zabbix/alertscripts	拷贝的目录参考Alertscripts目录截图
3	cd /app/zabbix/server/share/zabbix/alertscripts	进入alertscripts目录
4	vim zabbix_socket.py	修改zabbix集成文件
5	ProbeIP = "127.0.0.1"	修改ProbeIP的地址、参考ueh-probe-api-server的部署地址
6	修改完之后，先按ESC，使用:wq保存退出	

2.	配置媒介
此部分主要是在zabbix的前端web页面进行操作配置
登录zabbix, 选择管理=》示警媒介类型 =》创建媒体类型。如下图所示：
 
点击创建媒体类型界面填写信息：
名称：zabbix_to_socket，可自定义名称
类型：选择“脚本”
脚本名称：zabbix_socket.py,上传的脚本文件名称
脚本参数：{ALERT.MESSAGE}，必须填写为{ALERT.MESSAGE}，这个是zabbix触发器动作定义的告警内容
 
将创建的示警媒介类型配置到用户组 ，选择管理=》成员，点击“admin”用户
 
点击“报警媒介”，选择“添加”打开报警媒介配置界面
类型：选择配置的媒介名称
收件人：配置admin
其他项保持默认
配置完成之后，点击报警媒介配置界面里的“添加”，添加完成之后，点击“更新”完成配置。
 
3.	创建触发器动作
此部分主要是在zabbix的前端web页面进行操作配置
登录zabbix, 选择配置=》动作 =》事件源“触发器”，点击“创建动作”。如下图所示：
 
打开触发器动作的配置页面
名称：自定义触发器名称
条件：新的触发条件可以设置为空，则会将所有的告警推送至事件宝平台
 
动作页面里的操作部分，主要设置默认主题，消息内容，操作部分。
默认主题：{TRIGGER.STATUS}: {EVENT.NAME}
消息内容：
{HOST.HOST}#:#{HOST.NAME}#:#{TRIGGER.NAME}#:#{TRIGGER.TEMPLATE.NAME}#:#{TRIGGER.SEVERITY}#:#{TRIGGER.STATUS}#:#{TRIGGER.DESCRIPTION}#:#{ITEM.KEY1}#:#{ITEM.LASTVALUE}#:#{DATE} {TIME}#:#{TRIGGER.URL}#:#{TRIGGER.ID}#:#a834ad5e-0d70-86c3-8b4a-c0c3bfdbe5f5#:#{EVENT.TAGS}
特别注意：a834ad5e-0d70-86c3-8b4a-c0c3bfdbe5f5，这个probeID需要从ueh-zabbix-api-server的配置文件中的key获取
 
 
操作细节部分修改标红部分
 
恢复操作页面修改标红框部分
默认主题：{TRIGGER.STATUS}: {EVENT.NAME}
消息内容：
{HOST.HOST}#:#{HOST.NAME}#:#{TRIGGER.NAME}#:#{TRIGGER.TEMPLATE.NAME}#:#{TRIGGER.SEVERITY}#:#{TRIGGER.STATUS}#:#{TRIGGER.DESCRIPTION}#:#{ITEM.KEY1}#:#{ITEM.LASTVALUE}#:#{DATE} {TIME}#:#{TRIGGER.URL}#:#{TRIGGER.ID}#:#a834ad5e-0d70-86c3-8b4a-c0c3bfdbe5f5#:#{EVENT.TAGS}
特别注意：a834ad5e-0d70-86c3-8b4a-c0c3bfdbe5f5，这个probeID需要从ueh-zabbix-api-server的配置文件中的key获取
操作：选择通知所有参与者
 



#### 交流
扫描微信加群：

![二维码](http://www.china-alert.com/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20220630143007.jpg)