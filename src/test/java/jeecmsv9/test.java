package jeecmsv9;

public class test {

	public static void main(String[] args) {
		
		String str="/wenku/www/201803/300907321ryc.docx";
		
		String[] fileSuffixs=str.split("\\.");
		System.out.println("len->"+fileSuffixs.length);
		String fileSuffix=fileSuffixs[fileSuffixs.length-1];
	}

}
