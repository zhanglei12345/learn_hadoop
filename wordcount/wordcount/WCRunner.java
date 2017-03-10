package wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.io.Text;

/**
 * 用来描述一个特定的作业
 * 比如，该作业使用哪个类作为逻辑处理中的map，哪个作为reduce
 * 还可以指定该作业要处理的数据所在的路径
 * 还可以指定该作业输出的结果放在哪个路径
 * ...
 * @author zhanglei
 *
 */
public class WCRunner {
	
	public static void main(String [] args) throws Exception{
		Configuration conf = new Configuration();
		//创建Job对象
		Job wcjob = Job.getInstance(conf);
		
		//设置整个job的所用的那些类在哪个jar包
		wcjob.setJarByClass(WCRunner.class);
		
		//本job使用的mapper和reducer的类
		wcjob.setMapperClass(WCMapper.class);
		wcjob.setReducerClass(WCReducer.class);
		
		/*
		 * 指定reduce的输出数据kv类型
		 * 先设置reducer的原因是：没有设置reducer输出数据kv的类型的api,
		 * setOutputKeyClass和setOutputValueClass会把reducer和mapper都设置了,
		 * 如果reducer和mapper的输出类型一样，就用这条就够了，无需setMapOutput... 
		 */
		wcjob.setOutputKeyClass(Text.class);
		wcjob.setOutputValueClass(LongWritable.class);
		//指定mapper的输出数据kv类型
		//wcjob.setMapOutputKeyClass(Text.class);
		//wcjob.setMapOutputValueClass(LongWritable.class);
		
		//指定要处理的输入数据存放在哪里
		FileInputFormat.setInputPaths(wcjob, new Path("/wc/srcdata/"));
		//指定处理结果的输出数据存放路径
		FileOutputFormat.setOutputPath(wcjob, new Path("/wc/output/"));
		
		//将job提交给集群运行,true：打印运行日志
		wcjob.waitForCompletion(true);
	}
}
