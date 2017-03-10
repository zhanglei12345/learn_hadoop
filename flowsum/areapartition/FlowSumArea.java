package areapartition;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.StringUtils;

import flowsum.FlowBean;

/**
 * 
 * ������ԭʼ��־��������ͳ�ƣ�����ͬʡ�ݵ��û�ͳ�ƽ���������ͬ�ļ�
 * ��Ҫ�Զ�������������ƣ�
 * 1.����������߼����Զ���һ��partitioner
 * 2.�Զ���reducer task�Ĳ�����������Ĭ��1����
 * 
 * @author zhanglei
 *
 */
public class FlowSumArea {
	
	public static class FlowSumAreaMapper extends Mapper<LongWritable,Text,Text,FlowBean>{
		
		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
		
			//��һ������
			String line = value.toString();
			//�зֳɸ����ֶ�
			String[] fileds = StringUtils.split(line,' ');
			//�õ�������Ҫ���ֶ�
			String phoneNB = fileds[0];
			//���ַ���ת��Ϊʮ���Ƶ�long
			long up_flow = Long.parseLong(fileds[3]);
			long dw_flow = Long.parseLong(fileds[4]);
			//��װ����Ϊkv�����
			context.write(new Text(phoneNB), new FlowBean(phoneNB, up_flow, dw_flow));
			
		}
	}
	
	public static class FlowSumAreaReducer extends Reducer<Text,FlowBean,Text,FlowBean>{

		@Override
		protected void reduce(Text key, Iterable<FlowBean> values, Context context)
				throws IOException, InterruptedException {
			
			long up_flow_counter = 0;
			long dw_flow_counter = 0;
			
			for(FlowBean value:values){
				up_flow_counter += value.getUp_flow();
				dw_flow_counter += value.getDw_flow();
			}
			
			context.write(key , new FlowBean(key.toString(),up_flow_counter,dw_flow_counter));

		}
	}
		
	public static void main(String[] args) throws Exception{
		
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);
		
		job.setJarByClass(FlowSumArea.class);
		
		job.setMapperClass(FlowSumAreaMapper.class);
		job.setReducerClass(FlowSumAreaReducer.class);
		
		//���������Զ���ķ����߼�����
		job.setPartitionerClass(AreaPartitioner.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(FlowBean.class);
		
		//����reduce�����񲢷�����Ӧ�ø��������������һ��
		job.setNumReduceTasks(6);
		
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		System.exit(job.waitForCompletion(true)?0:1);
	}
	
}
