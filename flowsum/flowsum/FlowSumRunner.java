package flowsum;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

//����job�������ύ��Ĺ淶д��
public class FlowSumRunner extends Configured implements Tool{

	@Override
	public int run(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		////����Job����
		Job job = Job.getInstance(conf);
		//��������job�����õ���Щ�����ĸ�jar��
		job.setJarByClass(FlowSumRunner.class);
		//��jobʹ�õ�mapper��reducer����
		job.setMapperClass(FlowSumMapper.class);
		job.setReducerClass(FlowSumReducer.class);
		
		//job.setMapOutputKeyClass(Text.class);
		//job.setMapOutputValueClass(FlowBean.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(FlowBean.class);
		
		//ָ��Ҫ������������ݴ��������
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		//ָ����������������ݴ��·��
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		//��job�ύ����Ⱥ����,true����ӡ������־
		return job.waitForCompletion(true)?0:1;
	}
	
	public static void main(String[] args) throws Exception{
		
		int res = ToolRunner.run(new Configuration(), new FlowSumRunner(), args);
		System.exit(res);
	}

}
