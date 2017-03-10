package flowsum;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class FlowBean implements Writable{
	
	private String phoneNB;
	private long up_flow;
	private long dw_flow;
	private long sum_flow;

	//在反序列化时，反射机制需要调用空参构造函数，所以显示定义一个空参构造函数
	public FlowBean(){
		
	}
	
	//为了对象的初始化方便，加入一个带参数的构造函数
	public FlowBean(String phoneNB, long up_flow, long dw_flow) {
		super();
		this.phoneNB = phoneNB;
		this.up_flow = up_flow;
		this.dw_flow = dw_flow;
		this.sum_flow = up_flow + dw_flow;
	}

	//从数据流中反序列出对象的数据
	//从数据流中读出对象字段时，必须跟序列化时的顺序保持一致
	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		phoneNB = in.readUTF();
		up_flow = in.readLong();
		dw_flow = in.readLong();
		sum_flow = in.readLong();
		
	}

	//将对象数据序列化到流中
	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeUTF(phoneNB);
		out.writeLong(up_flow);
		out.writeLong(dw_flow);
		out.writeLong(sum_flow);
	}

	public String getPhoneNB() {
		return phoneNB;
	}

	public void setPhoneNB(String phoneNB) {
		this.phoneNB = phoneNB;
	}

	public long getUp_flow() {
		return up_flow;
	}

	public void setUp_flow(long up_flow) {
		this.up_flow = up_flow;
	}

	public long getDw_flow() {
		return dw_flow;
	}

	public void setDw_flow(long dw_flow) {
		this.dw_flow = dw_flow;
	}

	public long getSum_flow() {
		return sum_flow;
	}

	public void setSum_flow(long sum_flow) {
		this.sum_flow = sum_flow;
	}

	//reducer往文件中写入时不知道FlowBean对象如何写，需要复写toString方法，返回对象的字符串表示形式。
	@Override
	public String toString(){
		
		return "" + up_flow + "\t" + dw_flow + "\t" + sum_flow;
	}
}
