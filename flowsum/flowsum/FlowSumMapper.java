package flowsum;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.StringUtils;

/**
 * FlowBean�������Զ����һ���������ͣ�Ҫ��hadoop�����ڵ�֮�䴫�䣬Ӧ����ѭ�����л�����
 * �ͱ���ʵ��hadoop��Ӧ�����л��ӿ�
 * @author zhanglei
 *
 */
public class FlowSumMapper extends Mapper<LongWritable, Text, Text, FlowBean>{

	//�õ���־�е�һ�����ݣ��зָ����ֶΣ���ȡ��������Ҫ���ֶΣ��ֻ��ţ���������������������Ȼ���װ��kv���ͳ�ȥ
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
