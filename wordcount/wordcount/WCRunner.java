package wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.io.Text;

/**
 * ��������һ���ض�����ҵ
 * ���磬����ҵʹ���ĸ�����Ϊ�߼������е�map���ĸ���Ϊreduce
 * ������ָ������ҵҪ������������ڵ�·��
 * ������ָ������ҵ����Ľ�������ĸ�·��
 * ...
 * @author zhanglei
 *
 */
public class WCRunner {
	
	public static void main(String [] args) throws Exception{
		Configuration conf = new Configuration();
		//����Job����
		Job wcjob = Job.getInstance(conf);
		
		//��������job�����õ���Щ�����ĸ�jar��
		wcjob.setJarByClass(WCRunner.class);
		
		//��jobʹ�õ�mapper��reducer����
		wcjob.setMapperClass(WCMapper.class);
		wcjob.setReducerClass(WCReducer.class);
		
		/*
		 * ָ��reduce���������kv����
		 * ������reducer��ԭ���ǣ�û������reducer�������kv�����͵�api,
		 * setOutputKeyClass��setOutputValueClass���reducer��mapper��������,
		 * ���reducer��mapper���������һ�������������͹��ˣ�����setMapOutput... 
		 */
		wcjob.setOutputKeyClass(Text.class);
		wcjob.setOutputValueClass(LongWritable.class);
		//ָ��mapper���������kv����
		//wcjob.setMapOutputKeyClass(Text.class);
		//wcjob.setMapOutputValueClass(LongWritable.class);
		
		//ָ��Ҫ������������ݴ��������
		FileInputFormat.setInputPaths(wcjob, new Path("/wc/srcdata/"));
		//ָ����������������ݴ��·��
		FileOutputFormat.setOutputPath(wcjob, new Path("/wc/output/"));
		
		//��job�ύ����Ⱥ����,true����ӡ������־
		wcjob.waitForCompletion(true);
	}
}
