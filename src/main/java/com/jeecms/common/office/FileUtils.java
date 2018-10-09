package com.jeecms.common.office;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import com.jeecms.common.image.ImageUtils;

/**
 * @author Tom
 */
public class FileUtils {
	
	/**
	 * 
	 * @Title: createFile   
	 * @Description: 创建文件 （注：指定文件的上级目录不存在，会自动循环创建目录）
	 * @param: @param foldPath 文件目录
	 * @param: @param fileName 文件名（带后缀）
	 * @param: @param request
	 * @param: @return 创建结果  -1：文件已存在   0 :失败   1:成功
	 * @param: @throws IOException      
	 * @return: int
	 */
	public static int createFile(String foldPath,String fileName,HttpServletRequest request) throws IOException{
		String realFoldPath = getRealFold(foldPath,request); 
		File file = new File(realFoldPath+fileName);  
		if(file.exists()){
			//目标文件已存在
			return -1;
		}
		File folds = file.getParentFile();
		if(!folds.exists()){
			folds.mkdirs();
		}
		if(file.createNewFile()){
			//创建成功
			return 1;
		}else{
			//创建失败
			return 0;
		}
	}

	
	/**
	 * 获取文件真实路径
	 * @Title: getFileRealPaht   
	 * @Description: TODO
	 * @param: @param request
	 * @param: @return      
	 * @return: String
	 */
	public static String getFileRealPaht(String foldPath,String fileName,HttpServletRequest request) {
		String realpath = getRealFold(foldPath,request);
		realpath += fileName;
		return realpath;
	}
	
	/**
	 *  获取指定目录的真实物理路径
	 * @Title: getRealDir   
	 * @Description: TODO
	 * @param: @param fileDir  目录,以网站根目录开始
	 * @param: @param request
	 * @param: @return      
	 * @return: String
	 */
	public static String getRealFold(String foldPath,HttpServletRequest request){
		String realpath = request.getServletContext().getRealPath(foldPath);
		// tomcat8.0获取不到真实路径，通过/获取路径
		if (StringUtils.isBlank(realpath)) {
			realpath = request.getServletContext().getRealPath("/");
		}
		return realpath;
	}

	
	/**
	 * 写入内容
	 * @Title: writeFile   
	 * @Description: TODO
	 * @param: @param realpath  文件真实路径
	 * @param: @param content 写入内容
	 * @param: @throws IOException      
	 * @return: void
	 */
	public static void writeFile(String realpath, String content)
			throws IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(realpath), "UTF-8"));
		out.write(content);
		out.flush();
		out.close();
	}

	/**
	 * 读取html文件并指定替换元素替换内容
	 * @Title: dealHTMLContent   
	 * @Description: TODO
	 * @param: @param path  替换目标文件
	 * @param: @param selector  需要替换元素选择器，适用js及juqery选择器
	 * @param: @param content  替换内容
	 * @param: @return
	 * @param: @throws IOException      
	 * @return: String
	 */
	public static String dealHTMLContent(String path,String selcetor, String content)
			throws IOException {
		File input = new File(path);
		Document doc = Jsoup.parse(input, "UTF-8", "");
		Elements el = doc.select(selcetor);
		el.html(content);
		return doc.html();

	}
	
	public static String dealHTMLContent(String path)
			throws IOException {
		File input = new File(path);
		Document doc = Jsoup.parse(input, "UTF-8", "");
		return doc.html();

	}

	public static String getFilePrefix(String fileName) {
		int splitIndex = fileName.lastIndexOf(".");
		return fileName.substring(0, splitIndex);
	}

	public static String getFileSufix(String fileName) {
		int splitIndex = fileName.lastIndexOf(".");
		return fileName.substring(splitIndex + 1);
	}
	
	public static String getFileName(String path) {
		int lastIndex = path.lastIndexOf(".");
		int firstIndex = path.lastIndexOf("/");
		return path.substring(firstIndex+1, lastIndex);
	}
	
	public static String getFilePath(String fileName) {
		int splitIndex = fileName.lastIndexOf("/");
		return fileName.substring(0,splitIndex+1);
	}
	
	public static Set<String> listFiles(File directory,String prefixFileName,String suffix) {
		Set<String>filenames=new HashSet<String>();
		if(directory!=null&&directory.isDirectory()){
			File[]files = directory.listFiles();
			for(File f:files){
				String fname=f.getName();
				if(fname.endsWith(suffix)&&fname.startsWith(prefixFileName)){
					filenames.add(fname);
				}
			}
		}
		return filenames;
	}

	public static void copyFile(String inputFile, String outputFile)
			throws FileNotFoundException {
		File sFile = new File(inputFile);
		File tFile = new File(outputFile);
		FileInputStream fis = new FileInputStream(sFile);
		FileOutputStream fos = new FileOutputStream(tFile);
		int temp = 0;
		try {
			while ((temp = fis.read()) != -1) {
				fos.write(temp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String toHtmlString(File file, String filepath) {
		// 获取HTML文件流
		StringBuffer htmlSb = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			while (br.ready()) {
				htmlSb.append(br.readLine());
			}
			br.close();
			// 删除临时文件
			file.delete();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// HTML文件字符串
		String htmlStr = htmlSb.toString();
		htmlStr=replaceImg(htmlStr,filepath);
		// 返回经过清洁的html文本
		return htmlStr;
	//	return clearFormat(htmlStr, filepath);
	}
	
	private static String replaceImg(String txt,String uploadPath){
		//替换图片地址
		List<String>imgUrls=ImageUtils.getImageSrc(txt);
		for(String img:imgUrls){
			String imgRealUrl=img;
			imgRealUrl= uploadPath+"/"+img;
			txt=txt.replace(img, imgRealUrl);
		}
		return txt;
	}
	
	
	public static String subString(String html,String prefix,String subfix) {
		if(StringUtils.isNotBlank(html)){
			html=html.toLowerCase();
			prefix=prefix.toLowerCase();
			subfix=subfix.toLowerCase();
			return html.substring(html.indexOf(prefix)+prefix.length(), html.indexOf(subfix));
		}else{
			return "";
		}
	}

	/**
	 * 清除一些不需要的html标记
	 * 
	 * @param htmlStr
	 *            带有复杂html标记的html语句
	 * @return 去除了不需要html标记的语句
	 */
	public static String clearFormat(String htmlStr, String docImgPath) {
		// 获取body内容的正则
		String bodyReg = "<BODY .*</BODY>";
		Pattern bodyPattern = Pattern.compile(bodyReg);
		Matcher bodyMatcher = bodyPattern.matcher(htmlStr);
		if (bodyMatcher.find()) {
			// 获取BODY内容，并转化BODY标签为DIV
			htmlStr = bodyMatcher.group().replaceFirst("<BODY", "<DIV")
					.replaceAll("</BODY>", "</DIV>");
		}
		// 调整图片地址
		htmlStr = htmlStr.replaceAll("<IMG SRC=\"", "<IMG SRC=\"" + docImgPath
				+ "/");
		// 把<P></P>转换成</div></div>保留样式
		// content = content.replaceAll("(<P)([^>]*>.*?)(<\\/P>)",
		// "<div$2</div>");
		// 把<P></P>转换成</div></div>并删除样式
		htmlStr = htmlStr.replaceAll("(<P)([^>]*)(>.*?)(<\\/P>)", "<p$3</p>");
		/*
		// 删除不需要的标签
		htmlStr = htmlStr
				.replaceAll(
						"<[/]?(font|FONT|span|SPAN|xml|XML|del|DEL|ins|INS|meta|META|[ovwxpOVWXP]:\\w+)[^>]*?>",
						"");
		// 删除不需要的属性
		htmlStr = htmlStr
				.replaceAll(
						"<([^>]*)(?:lang|LANG|class|CLASS|style|STYLE|size|SIZE|face|FACE|[ovwxpOVWXP]:\\w+)=(?:'[^']*'|\"\"[^\"\"]*\"\"|[^>]+)([^>]*)>",
						"<$1$2>");
		*/
		htmlStr=htmlStr.replaceAll("\t", "");
		return htmlStr;
	}
}