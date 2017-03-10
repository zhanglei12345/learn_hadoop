package flowsum;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.StringUtils;

/**
 * FlowBean是我们自定义的一种数据类型，要在hadoop各个节点之间传输，应该遵循的序列化机制
 * 就必须实现hadoop相应的序列化接口
 * @author zhanglei
 *
 */
public class FlowSumMapper extends Mapper<LongWritable, Text, Text, FlowBean>{

	//拿到日志中的一行数据，切分各个字段，抽取出我们需要的字段：手机号，上行流量，下行流量，然后封装成kv发送出去
	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		//拿一行数据
		String line = value.toString();
		//切分成各个字段
		String[] fileds = StringUtils.split(line,' ');
		//拿到我们需要的字段
		String phoneNB = fileds[0];
		//将字符串转换为十进制的long
		long up_flow = Long.parseLong(fileds[3]);
		long dw_flow = Long.parseLong(fileds[4]);
		//封装数据为kv并输出
		context.write(new Text(phoneNB), new FlowBean(phoneNB, up_flow, dw_flow));
	}
	
	
}
