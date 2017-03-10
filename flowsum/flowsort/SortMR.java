package flowsort;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.StringUtils;

import flowsort.FlowBean;


public class SortMR {

	public static class SortMapper extends Mapper<LongWritable, Text, FlowBean, NullWritable>{

		//�õ�һ�����ݣ��зֳ����ֶΣ���װΪһ��flowbean����key���
		//map��������Ƚ��������ٴ��ݸ�reduce
		@Override
		protected void map(LongWritable key, Text value,Context context)
				throws IOException, InterruptedException {

			//��һ������
			String line = value.toString();
			//�зֳɸ����ֶ�
			String[] fields = StringUtils.split(line,'\t');
			
			String phoneNB = fields[0];
			//���ַ���ת��Ϊʮ���Ƶ�long
			long up_flow = Long.parseLong(fields[1]);
			long dw_flow = Long.parseLong(fields[2]);
			
			//����ʹ��new NullWritable()������,ֻ��NullWritable.get()����ȡʵ��
			context.write(new FlowBean(phoneNB, up_flow, dw_flow), NullWritable.get());
	
		}
	}
	
	public static class SortReducer extends Reducer<FlowBean, NullWritable, Text, FlowBean>{

		@Override
		protected void reduce(FlowBean key, Iterable<NullWritable> values,Context context)
				throws IOException, InterruptedException {
			
			String phoneNB = key.getPhoneNB();
			context.write(new Text(phoneNB),key);

		}
		
	}
	
	public static void main(String[] args) throws Exception{
		
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);
		job.setJarByClass(SortMR.class);
		job.setMapperClass(SortMapper.class);
		job.setReducerClass(SortReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(FlowBean.class);
		
		job.setMapOutputKeyClass(FlowBean.class);
		job.setMapOutputValueClass(NullWritable.class);
		
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		System.exit(job.waitForCompletion(true)?0:1);
	}
}
