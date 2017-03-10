package master;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.junit.Before;
import org.junit.Test;

public class HdfsUtil {
	
	FileSystem fs = null;
	@Before
	public void init() throws Exception{
		//��ȡclasspath�µ�xxx-site.xml�����ļ��������������ݣ���װ��conf������
		Configuration conf = new Configuration();
		//Ҳ�����ڴ����ж�conf�е�������Ϣ�����ֶ����ã��Ḳ�ǵ������ļ��еĶ�ȡ��Ϣ
		conf.set("fs.defaultFS", "hdfs://master:9000");
		//����������Ϣ��ȥ��ȡһ�������ļ�ϵͳ�Ŀͻ��˲���ʵ������
		fs = FileSystem.get(new URI("hdfs://master:9000"), conf, "root");
	}
	
	/**
	 * �ϴ��ļ����Ƚϵײ��д��
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	public void upload() throws Exception {
		
		/*
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", "hdfs://master:9000");
		FileSystem fs = FileSystem.get(conf);
		*/
		Path dst = new Path("hdfs://master:9000/test/hello.txt");
		FSDataOutputStream os = fs.create(dst);
		FileInputStream is = new FileInputStream("F:/hello.txt");
		IOUtils.copy(is,os);
	}
	
	/**
	 * �ϴ��ļ�����װ�õ�д��
	 * @throws Exception
	 * @throws IOException
	 */
	@Test
	public void upload2() throws Exception, IOException{
		fs.copyFromLocalFile(new Path("F:/hello.txt"), new Path("hdfs://master:9000/test/hello2.txt"));
	}

	/**
	 * �����ļ�
	 * @throws Exception 
	 * @throws IllegalArgumentException 
	 */
	@Test
	public void download() throws Exception {
		fs.copyToLocalFile(false, new Path("hdfs://master:9000/test/hello2.txt"), new Path("F:/hello2.txt"), true);
	}

	/**
	 * �鿴�ļ���Ϣ
	 * @throws Exception 
	 * @throws IllegalArgumentException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void listFiles() throws FileNotFoundException, IllegalArgumentException, Exception {
		
		//listFiles�г������ļ���Ϣ�������ṩ�ݹ����
		RemoteIterator<LocatedFileStatus> files = fs.listFiles(new Path("/"), true);
		while(files.hasNext()){
			LocatedFileStatus file = files.next();
			Path filePath = file.getPath();
			String fileName = filePath.getName();
			System.out.println(fileName);
		}
		System.out.println("-----------------------------------");
		
		//listStatus �����г��ļ����ļ��е���Ϣ�����ǲ��ṩ�Դ��ĵݹ����
		FileStatus[] listStatus =  fs.listStatus(new Path("/"));
		for(FileStatus status:listStatus){
			String name = status.getPath().getName();
			System.out.println(name + (status.isDirectory()?" is dir":" is file"));
		}	
	}

	/**
	 * �����ļ���
	 * @throws Exception 
	 * @throws IllegalArgumentException 
	 */
	@Test
	public void mkdir() throws IllegalArgumentException, Exception {
		fs.mkdirs(new Path("/test/test_mkdir1/test_mkdir2"));
	}

	/**
	 * ɾ���ļ����ļ���
	 * @throws Exception 
	 * @throws IllegalArgumentException 
	 */
	@Test
	public void rm() throws IllegalArgumentException, Exception {
		fs.delete(new Path("/test/test_mkdir1"), true);
	}

}
