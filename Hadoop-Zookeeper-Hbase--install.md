# Hadoop安装教程：
#### 1. centos 6.4  (64位安装):  

- 使用 VMware 10 安装，先建立一个空白硬盘的虚拟机，然后修改该虚拟机设置，主要修改网络适配器为桥接方式，CD/DVD中添加iso镜像，勾选启动时连接。
- 安装系统（分配了20G的磁盘）时选择创建自定义布局，/home 2000M，/boot 200M，swap 1000M，其余空间分给/  。 选择安装 Basic Server (基本服务器）版本。
- 安装好系统后利用 setup 工具进行网络设置以及关闭防火墙。进行网络设置时要注意和真实机的网络在一个网段下，比如真实机的 ip 为192.168.1.100，则虚拟机的ip应设置为192.168.1.x，其他设置可保持与真实机的一致。
- 利用 VMware 再克隆两个虚拟机，注意克隆后的两个系统 ifconfig 不显示 ech0，解决方案：
	1. 进入 /etc/sysconfig/network-scripts 目录，发现有 ifcfg-eth0,即网卡（驱动）存在但未启用。
	2. 输入 ifconfig -a 命令，可显示 eth0 和 lo。
	3. 输入 ifconfig eth0 up，启用网卡。此时用 ifconfig，只能看到 inet6（ipv6?）的地址，没有 inet 的地址（即 Xshell 连接输入的 ip）。
	4. 修改 /etc/sysconfig/network-scripts/ifcfg-eth0 文件，把 ONBOOT=no 改  ONBOOT=yes，但 ifconfig 的结果没有任何改变。
	5. service network restart，重启。
	6. ifconfig 查看 ip 地址。
- service network restart 时报 eth0 设备无法找到，解决方案：
	1. 由于有两个虚拟机是利用 VMware 克隆来的，查看 /etc/udev/rules.d/70-persistent-net.rules 文件可发现三个虚拟机的 eth0 对应的三个 mac 地址相同，克隆来的两个虚拟机该文件中多出一个 eth1，删除这两个虚拟机对应的 eth0，同时将 eth1 改为 eht0。
	2. service network restart，重启。
	3. ifconfig 查看 ip 地址。
		
#### 2. hadoop 安装   

- 上传并解压 jdk-7u80-linux-i586.tar.gz 和 hadoop-2.6.5.tar.gz 包，并进行文件夹改名。（tar -zxvf xxx)
- 依次对应修改 /etc/sysconfig/network 文件，比如mater主机修改如下: HOSTNAME=master。修改后执行 hostname master。
- 在 /etc/hosts 文件中添加下列内容：  
	192.168.1.172  master  
	192.168.1.173  node1  
	192.168.1.174  node2   
- 实现SSH互通：    
	1. 三台虚拟机上分别实现ssh无密码登录本机：  
		cd ~/.ssh  #若没有该目录，请先执行一次ssh localhost  
		ssh-keygen -t rsa	# 会有提示，都按回车就可以	  
		cat id_rsa.pub >> authorized_keys  # 加入授权  
	2. 实现ssh互通：  
		将三台虚拟机的id_rsa.pub内容均加到authorized_keys文件中，即该文件中存在三台虚拟机的公钥。
	3. 三台虚拟机分别进行ssh测试：  
		ssh master  
		ssh node1  
		ssh node2  
- 修改~/.bashrc：  
	export JAVA_HOME=/root/jdk  
	export HADOOP_HOME=/root/hadoop  
	export HADOOP_INSTALL=$HADOOP_HOME  
	export HADOOP_MAPRED_HOME=$HADOOP_HOME  
	export HADOOP_COMMON_HOME=$HADOOP_HOME  
	export HADOOP_HDFS_HOME=$HADOOP_HOME  
	export YARN_HOME=$HADOOP_HOME  
	export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native  
	export HADOOP_OPTS="-Djava.library.path=$HADOOP_HOME/lib"  
	export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$JAVA_HOME/bin    	   


#### 3. 配置非HA机制的集群/分布式环境：    

- 修改core-site.xml：    

   ```
	<configuration>
		<property>
			<name>fs.defaultFS</name>
			<value>hdfs://master:9000</value>
		</property>
		<property>
			<name>hadoop.tmp.dir</name>
			<value>/root/hdpdir/tmp</value>
		</property>
	</configuration>
	```
- 修改hdfs-site.xml：  

  	```
	<configuration>
		<property>
			<name>dfs.replication</name>
			<value>2</value>
		</property>
        <property>
            <name>dfs.namenode.secondary.http-address</name>
            <value>node1:50090</value>
        </property>
        <property>
            <name>dfs.namenode.name.dir</name>
            <value>file:/root/hdpdir/tmp/dfs/name</value>
        </property>
        <property>
            <name>dfs.datanode.data.dir</name>
            <value>file:/root/hdpdir/tmp/dfs/data</value>
        </property>
	</configuration>
	```
- 修改slaves：    
	node1  
	node2
- 修改mapred-site.xml（需要先重命名，默认文件名为mapred-site.xml.template）：  

	```
	<configuration>
		<property>
			<name>mapreduce.framework.name</name>
			<value>yarn</value>
		</property>
        <property>
            <name>mapreduce.jobhistory.address</name>
            <value>master:10020</value>
        </property>
        <property>
            <name>mapreduce.jobhistory.webapp.address</name>
            <value>master:19888</value>
        </property>
	</configuration>
	```
- 修改yarn-site.xml：  

	```
	<configuration>
        <property>
            <name>yarn.resourcemanager.hostname</name>
            <value>master</value>
        </property>
        <property>
            <name>yarn.nodemanager.aux-services</name>
            <value>mapreduce_shuffle</value>
        </property>
	</configuration>
	```

#### 4. 启动非HA机制的hadoop：  

- 启动hadoop出现警告WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable，解决方法：    
	在/root/hadoop/etc/hadoop/log4j.properties文件中添加
	log4j.logger.org.apache.hadoop.util.NativeCodeLoader=ERROR
- hdfs namenode -format       # 首次运行需要执行初始化，之后不需要
- 在master节点上执行：  
start-dfs.sh
start-yarn.sh
- 通过命令 jps 可以查看各个节点所启动的进程：  
- 成功启动后，可以访问 Web 界面 http://master:50070 查看 NameNode 和 Datanode 信息，还可以在线查看 HDFS 中的文件。利用命令hdfs dfs -ls /   查看hdfs上内容。   
(  
注意：要在本地机windows 7下C:\Windows\System32\drivers\etc\hosts文件中添加  
192.168.8.172 master  
192.168.8.173 node1  
192.168.8.174 node2  
）

#### 5. 执行分布式实例：

- 实例一（自带）
	1. hdfs dfs -mkdir /input
	2. hdfs dfs -put /root/hadoop/etc/hadoop/*.xml /input
	3. 运行 MapReduce 作业:  
		hadoop jar /root/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-examples-*.jar grep /input /output 'dfs[a-z.]+'  
	4. 可以通过 Web 界面查看任务进度 http://master:8088/cluster
	5. 运行完成之后执行命令hdfs dfs -cat /output/*
- 实例二（自定义）
	1. hadoop fs -mkdir -p /wordcount/input
	2. hadoop fs -put test.txt /wordcount/input
	3. hadoop jar hadoop-mapreduce-examples-2.6.5.jar wordcount /wordcount/input   /wordcount/output
	4. hadoop fs -cat /wordcount/output/part-r-00000
- 备注：
	1. master上edits和fsimage存放路径（需要看配置文件中hadoop的工作目录）：
		/root/hdpdir/tmp/dfs/name/current
	2. datanode上block的存放路径（需要看配置文件中hadoop的工作目录）：
		/root/hdpdir/tmp/dfs/data/current/BP-1490608859-192.168.1.172-1487361971380/current/finalized/subdir0/subdir0

#### 6. 关闭 Hadoop 集群：

- stop-yarn.sh
- stop-dfs.sh


# Zookeeper安装：

#### 1. 在master上传解压zookeeper安装包：    
	tar -zxvf zookeeper-3.4.9.tar.gz
#### 2. 在master上修改配置文件：
	1. cd zookeeper-3.4.9/conf
	2. mv zoo_sample.cfg zoo.cfg
	3. vi zoo.cfg
		- 修改dataDir路径 ：  dataDir=/root/zoodir/data
		- 在文本最后添加：  
			server.1=master:2888:3888  
			server.2=node1:2888:3888  
			server.3=node2:2888:3888
	4. 在dataDir路径下添加myid文件:
		- cd /root/zoodir/data	（注意先建立该目录）
		- echo 1 > myid		(myid文件中的内容跟zoo.cfy中的server后的名字对应)
#### 3. 将mater节点上的安装包zookeeper-3.4.9 scp到其他两个节点并修改相应的myid文件
#### 4. 分别进入三台机子zookeeper的bin目录下：（三台机子均要执行启动服务）
	./zkServer.sh start		（启动zookeeper服务）
	./zkServer.sh status	（查看zookeeper服务状态）
	netstat -nltp | grep 2181	（查看对应的监听端口）
#### 5. 命令行客户端zkCli.sh（在zookeeper的bin目录下）,可用来连接zookeeper集群。
	./zkCli.sh	(连接到本节点的2181端口）
#### 6. 启动HA机制的hadoop：
	1. 修改core-site.xml：
```
	<configuration>
	
		<!-- 指定hdfs的nameservice为ns1 -->
		<property>
			<name>fs.defaultFS</name>
			<value>hdfs://ns1</value>
		</property>
		
		<property>
			<name>hadoop.tmp.dir</name>
			<value>/root/hdpdir/tmp</value>
		</property>
		
		<property>
          <name>dfs.journalnode.edits.dir</name>
          <value>/root/hdpdir/journaldata</value>
          <desrciption>指定journalnode在本地磁盘存放数据的位置</desrciption>
       </property>
       
		<!-- 指定zookeeper地址 -->
		<property>
			<name>ha.zookeeper.quorum</name>
			<value>ha1:2181,ha2:2181,ha3:2181</value>
		</property>
		
	</configuration>
```	

	2. 修改hdfs-site.xml：

```		
<configuration>

	<property>
		<name>dfs.replication</name>
		<value>1</value>
	</property>

	<property>
		<name>dfs.nameservices</name>
		<value>ns1</value>
		<description> 命名空间的逻辑名称，如果使用 HDFS Federation, 可以配置多个命名空间的名称，使用逗号隔开 </description>
	</property>

	<property>
		<name>dfs.ha.namenodes.ns1</name>
		<value>nn1,nn2</value>
		<description> 所有 namenode 的唯一标示名称，可以配置多个 </description>
	</property>

	<property>
		<name>dfs.namenode.rpc-address.ns1.nn1</name>
		<value>ha1:9000</value>
		<description>namenode 监听的 RPC 地址 </description>
	</property>

	<property>
		<name>dfs.namenode.http-address.ns1.nn1</name>
		<value>ha1:50070</value>
		<desrciption>namenode 监听的 http 地址 </desrciption>
	</property>

	<property>
		<name>dfs.namenode.rpc-address.ns1.nn2</name>
		<value>ha2:9000</value>
		<description>namenode 监听的 RPC 地址 </description>
	</property>


	<property>
		<name>dfs.namenode.http-address.ns1.nn2</name>
		<value>ha2:50070</value>
		<desrciption>namenode 监听的 http 地址 </desrciption>
	</property>

	<property>
		<name>dfs.namenode.shared.edits.dir</name>
		<value>qjournal://ha1:8485;ha2:8485;ha3:8485/ns1</value>
		<description>NameNode 读写 JNs 组的 uri ，必须是奇数个 .指定namenode的元数据在journalnode上的存放位置</description>
	</property>

	<property>
		<name>dfs.ha.automatic-failover.enabled</name>
		<value>true</value>
		<desrciption>开启namenode失败自动切换</desrciption>
	</property>

	<property>
		<name>dfs.client.failover.proxy.provider.ns1</name>
		<value>org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider</value>
		<desrciption>配置失败自动切换实现方式</desrciption>
	</property>

	<property>
		<name>dfs.ha.fencing.methods</name>
		<value>
			sshfence
			shell(/bin/true)
		</value>
		<desrciption>配置隔离机制方法，多个机制用换行分割，即每个机制占用一行</desrciption>
	</property>

	<property>
		<name>dfs.ha.fencing.ssh.private-key-files</name>
		<value>/root/.ssh/id_rsa</value>
		<desrciption>使用sshfence隔离机制时需要ssh免登陆</desrciption>
	</property>

	<property>
		<name>dfs.ha.fencing.ssh.connect-timeout</name>
		<value>30000</value>
		<desrciption>配置sshfence隔离机制超时时间(单位：毫秒)</desrciption>
	</property>

</configuration>
```
	3. 修改mapred-site.xml：
```		
<configuration>

	<!-- 指定mr框架为yarn方式. -->
	<property>
		<name>mapreduce.framework.name</name>
		<value>yarn</value>
	</property>

</configuration>
```
	4. 修改yarn-site.xml：
```
<configuration>
<!-- Site specific YARN configuration properties -->

	<property>
		<name>yarn.nodemanager.aux-services</name>
		<value>mapreduce_shuffle</value>
	</property>
	
	<property>
		<name>yarn.nodemanager.aux-services.mapreduce.shuffle.class</name>
		<value>org.apache.hadoop.mapred.ShuffleHandler</value>
	</property>

</configuration>
```
	5. 修改slaves：
```
	ha1
	ha2
	ha3
```
	6. 启动流程：
		1. 启动zookeeper集群（分别在ha1、ha2、ha3上启动zk）
			zkServer.sh start
			zkServer.sh status	（查看状态：其中有两台是 follower ，一台是 leader）
		2. 启动journalnode（分别在ha1、ha2、ha3上启动zk）
			hadoop-daemon.sh start journalnode	（jps命令检查多了JournalNode进程）
		3. 格式化HDFS（在ha1上执行命令）  --首次启动格式化
			hdfs namenode -format
			#格式化后会根据core-site.xml中的hadoop.tmp.dir配置生成个文件，这里我配置的是/root/hdpdir/tmp，然后scp -r /root/hdpdir/tmp ha2:/root/hdpdir  (确保两个namenode的初始数据一样)
		4. 格式化ZKFC（在ha1上执行）
			hdfs zkfc -formatZK
			( 如果集群同步出现问题，或者更改了 zookeeper ，需要重新格式化 zkfc，然后再 ha1,ha2 上都执行 hadoop-daemon.sh start zkfc)
		5. 启动HDFS（在ha1上执行）
			start-dfs.sh
		6. 启动YARN（在ha1上执行）
			start-yarn.sh
		注意：以后启动只需要
			zkServer.sh start（每个节点都执行）
			start-dfs.sh
			start-yarn.sh

# Hbase安装：

#### 1. 修改hbase-env.sh
		//告诉hbase使用外部的zk
		export HBASE_MANAGES_ZK=false
		
#### 2. 修改core-site.xml：
```
	<configuration>
	
        <property>
                <name>fs.defaultFS</name>
                <value>hdfs://ns1</value>
        </property>
        
        <property>
                <name>hadoop.tmp.dir</name>
                <value>/root/hdpdir/tmp</value>
        </property>
        
        <property>
                <name>dfs.journalnode.edits.dir</name>
                <value>/root/hdpdir/journaldata</value>
                <desrciption>指定journalnode在本地磁盘存放数据的位置</desrciption>
        </property>
        
        <property>
                <name>ha.zookeeper.quorum</name>
                <value>ha1:2181,ha2:2181,ha3:2181</value>
        </property>
        
	</configuration>
```
#### 3. 将hadoop下的core-site.xml hdfs-site.xml复制粘贴到hbase的conf目录下。
#### 4. 复制hbase到其他节点
#### 5. 启动hbase：start-hbase.sh
#### 6. 查看Hbase的web页面：http://192.168.8.180:16010
#### 7. 启动hbase命令行客户端:
		hbase shell