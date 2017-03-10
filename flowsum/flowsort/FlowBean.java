package flowsort;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


import org.apache.hadoop.io.WritableComparable;

/**
 * ʵ���û��������Ӵ���С����
 */

public class FlowBean implements WritableComparable<FlowBean>{
	
	private String phoneNB;
	private long up_flow;
	private long dw_flow;
	private long sum_flow;

	//�ڷ����л�ʱ�����������Ҫ���ÿղι��캯����������ʾ����һ���ղι��캯��
	public FlowBean(){
		
	}
	
	//Ϊ�˶���ĳ�ʼ�����㣬����һ���������Ĺ��캯��
	public FlowBean(String phoneNB, long up_flow, long dw_flow) {
		super();
		this.phoneNB = phoneNB;
		this.up_flow = up_flow;
		this.dw_flow = dw_flow;
		this.sum_flow = up_flow + dw_flow;
	}

	//���������з����г����������
	//���������ж��������ֶ�ʱ����������л�ʱ��˳�򱣳�һ��
	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		phoneNB = in.readUTF();
		up_flow = in.readLong();
		dw_flow = in.readLong();
		sum_flow = in.readLong();
		
	}

	//�������������л�������
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

	@Override
	public String toString(){
		
		return "" + up_flow + "\t" + dw_flow + "\t" + sum_flow;
	}

	//conpareToĬ���ұ���󷵻���1���ұ���С����-1����ȷ���0
	@Override
	public int compareTo(FlowBean o) {
		
		return sum_flow > o.getSum_flow()?-1:1;
	}
}
