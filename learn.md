HDFS：海量数据的存储  
MapReduce：海量数据的分析  
YARN：资源管理调度  

## NN元数据管理机制：

![](https://raw.githubusercontent.com/zhanglei12345/learn_hadoop/master/img/nn2.png)

#### 上传文件：

![](https://raw.githubusercontent.com/zhanglei12345/learn_hadoop/master/img/nn1.png)

- 客服端负责切分文件。
- 客户端只负责写第一个block，写完则返回成功，副本由datanode负责完成；
- 如果block的第二个副本复制成功，但第三个复制失败，则第二个副本会收到错误信息并将错误信息返回给第一个副本，第一个副本向namenode汇报该bolck副本数不满足要求，namenode则重新分配一个datanode接收第三个副本，由第一个副本或第二个副本再复制一份给第三个副本。
- 存储大量的小文件会浪费元数据的存储空间，对datanode的空间并无影响。


![](https://raw.githubusercontent.com/zhanglei12345/learn_hadoop/master/img/nn3.png)

- 客户端上传文件时，NN首先往edits log文件中记录元数据操作日志（edits log不提供修改，只能追加）。
- 客户端开始上传文件（上传block的第一个副本即成功），完成后返回成功信息给NN，NN就在内存中写入这次上传操作的新产生的元数据信息。
- 每当edits log写满或者到达规定时间时，需要将这一段时间的新的元数据刷到fsimage文件中去。

![](https://raw.githubusercontent.com/zhanglei12345/learn_hadoop/master/img/nn4.png)

### NN职责：

1. 维护元数据信息；
2. 维护hdfs的目录树；
3. 响应客户端的请求。

### HA架构：
![](https://raw.githubusercontent.com/zhanglei12345/learn_hadoop/master/img/nn5.png)

- 应该让两个NN节点在某个时间只能有一个节点正常响应客户端请求，响应请求的必须为ACTIVE状态的那一台。
- standby状态的节点必须能够快速无缝地切换为active状态（意味着两个NN必须时刻保持元数据的一致）。
- zkfc：管理NN的运行状态，也要依赖于zk实现。
- 如何避免状态切换时发生brain split现象：（fencing机制）
	1. ssh发出kill指令；
	2. 执行自定义脚本。

## DN工作原理

1. 提供真实文件数据的存储服务；
2. 文件块（block）：最基本的存储单位。HDFS默认block大小是128M，如果一个文件不满128M，它所占的block空间不会到128M，但元数据还是一条，也叫一个block。
3. 不同于普通文件系统的是，HDFS中，如果一个文件小于一个数据块的大小，并不占用整个数据块存储空间。

## RPC框架

RPC：远程过程调用
![](https://raw.githubusercontent.com/zhanglei12345/learn_hadoop/master/img/RPC1.png)

![](https://raw.githubusercontent.com/zhanglei12345/learn_hadoop/master/img/RPC2.png)

## MapReduce

![](https://raw.githubusercontent.com/zhanglei12345/learn_hadoop/master/img/nn6.png)

## YARN
![](https://raw.githubusercontent.com/zhanglei12345/learn_hadoop/master/img/nn12.png)

## Shuffle机制
![](https://raw.githubusercontent.com/zhanglei12345/learn_hadoop/master/img/nn13.png)

一个切片(split)对应一个map进程。

![](https://raw.githubusercontent.com/zhanglei12345/learn_hadoop/master/img/nn8.png)

MapReduce全貌：

![](https://raw.githubusercontent.com/zhanglei12345/learn_hadoop/master/img/nn9.png)

## Zookeeper
Zookeeper是Hadoop的分布式协调服务。分布式应用程序可以基于它实现同步服务，配置维护和命名服务等。
![](https://raw.githubusercontent.com/zhanglei12345/learn_hadoop/master/img/nn10.png)

![](https://raw.githubusercontent.com/zhanglei12345/learn_hadoop/master/img/nn11.png)

- 奇数个节点，Zookeeper要想维持正常工作，存活的节点数量必须大于集群配置节点的一半。
- 数据的写要通过Leader来实现，进而通知其他节点更新，只要有超过半数的节点更新完成，Leader就认为这份数据更新成功。
- Leader和Follower不是在集群启动之前事先分配的，即不是通过配置文件分配的，Leader是在启动时通过"选举"产生的。
- Hadoop2.0使用Zookeeper的事件处理确保整个集群只有一个活跃的NameNode，存储配置信息等。
- HBase使用Zookeeper的事件处理确保整个集群只有一个HMaster，察觉HRegionServer联机和宕机，存储访问控制列表等。
- Zookeeper提供了一套很好的分布式集群管理的机制，就是它这种基于层次型的目录树的数据结构，并对树中的节点进行有效管理，从而可以设计出多种多样的分布式的数据管理模型。