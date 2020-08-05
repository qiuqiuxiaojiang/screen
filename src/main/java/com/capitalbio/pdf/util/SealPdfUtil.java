package com.capitalbio.pdf.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDate;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignature;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.security.PdfPKCS7;

public class SealPdfUtil {
	//水印图片
		private final static String noneUrl = getProjectPath() + "/content/img" + File.separator+ "none.png";
		//签章文件
		private final static String pfxFile = getProjectPath() + "/content/img" + File.separator+"bjjy.pfx";
		//签章图片
		private final static String imgUrl = getProjectPath() + "/content/img" + File.separator+"bjbao.png";
		//签章密码
		private final static char[] password = "1234".toCharArray();
		
		
		/**
		 * 盖章
		 * @throws Exception 
		 *  
		 */
	public static void sealPdf(int pageSize,String inFile,String outFile,String reportType) throws  Exception{
		String fileName=getProjectPath()+"/properties/configure.xml";
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
		SAXReader reader = new SAXReader();
		Document document = reader.read(br); 
		
		List<Object> rowList = document.selectNodes("//param/path/coordinate"); 
		Element element =null;
		for(int i=0;i<rowList.size();i++){
			Element elementTmp = (Element)rowList.get(i);
			String reportTypeStr = elementTmp.attributeValue("reportType");
			String[] reportTypeArr = reportTypeStr.split(",");
			for(int k=0;k<reportTypeArr.length;k++){
				if(reportTypeArr[k].equals(reportType)){
					element=elementTmp;
					break;
				}
			}
		}
        //获得具体的row元素   
//        Element element = (Element)rowList.get(0);  
        //获得row元素的所有属性列表  
       //List elementList = element.attributes();  
        float lx=Float.parseFloat(element.attributeValue("lx"));
		float ly = Float.parseFloat(element.attributeValue("ly"));
		float rx =Float.parseFloat(element.attributeValue("rx"));
		float ry =Float.parseFloat(element.attributeValue("ry"));
		signPdfTwo(inFile, outFile, lx, ly, rx, ry, pageSize);
//		addImageToPdf(inFile, outFile, lx, ly, pageSize);
	}
	public static void main(String[] args) throws  Exception{
		sealPdf(2,"E://1.pdf","E://shi1.pdf","");
		
		
		
		
	}
	public static void addImageToPdf(String sourceFile, String silicFile, float lx, float ly, int pageSize)throws Exception{
		
//		PdfReader reader = new PdfReader(sourceFile);
//		
//		FileOutputStream fout = new FileOutputStream(silicFile);
//
//		PdfStamper stp = PdfStamper.createSignature(reader, fout, '\0', null, true);
//		//添加水印
//		Image image = Image.getInstance(noneUrl);
//		image.setAbsolutePosition(lx, ly);
//		PdfContentByte under = stp.getUnderContent(pageSize);
//
//		under.addImage(image);
		
		PdfReader reader = new PdfReader(sourceFile);
		PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(silicFile));
		Image image = Image.getInstance(imgUrl);
		image.setAlignment(0);
		image.scaleAbsolute(80,8);
		image.setAbsolutePosition(lx, ly);
//		PdfContentByte under = stamp.getOverContent(1);
		for(int i = 1; i <= reader.getNumberOfPages(); i++) {
			PdfContentByte under = stamp.getOverContent(i);
			under.addImage(image);
		}
		
		
	}

	/**
	 * 给pdf文件添加签章
	 * @param sourceFile 签章图片
	 * @param silicFile	输出文件
	 * @param lx x轴
	 * @param ly y轴
	 * @param rx 图片的宽加x轴
	 * @param ry 图片的高加y轴
	 * @param pageSize 第几页添加签章
	 * @throws Exception
	 */
	public static void signPdfTwo(String sourceFile, String silicFile, float lx, float ly, float rx, float ry, int pageSize) throws Exception{
		System.out.println("--- 添加签章 ---" + sourceFile);
		
		KeyStore ks = KeyStore.getInstance("pkcs12");
		System.out.println(pfxFile);
		ks.load(new FileInputStream(pfxFile), password);
		String alias = (String) ks.aliases().nextElement();
		PrivateKey pk = (PrivateKey) ks.getKey(alias, password);

		PdfReader reader = new PdfReader(sourceFile);
		
		FileOutputStream fout = new FileOutputStream(silicFile);

		PdfStamper stp = PdfStamper.createSignature(reader, fout, '\0', null, true);
		//添加水印
		Image image = Image.getInstance(noneUrl);
		image.setAbsolutePosition(lx, ly);
		image.scaleToFit(rx - lx, ry - ly);  
		PdfContentByte under = stp.getUnderContent(pageSize);
		under.addImage(image);
		PdfSignatureAppearance sap = stp.getSignatureAppearance();
		
		Certificate[] chain = ks.getCertificateChain(alias);
		sap.setCertificationLevel(PdfSignatureAppearance.NOT_CERTIFIED);
		sap.setVisibleSignature(new Rectangle(lx, ly, rx, ry), pageSize, "SignatureField2");
		
		sap.setSignatureGraphic(Image.getInstance(imgUrl));
		sap.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);

		PdfSignature dic = new PdfSignature(PdfName.ADOBE_PPKLITE, new PdfName("adbe.pkcs7.detached"));
		dic.setReason(sap.getReason());
		dic.setLocation(sap.getLocation());
		dic.setContact(sap.getContact());
		dic.setDate(new PdfDate(sap.getSignDate()));
		sap.setCryptoDictionary(dic);

		int contentEstimated = 7000;
		HashMap<PdfName, Integer> exc = new HashMap<PdfName, Integer>();
		exc.put(PdfName.CONTENTS, new Integer(contentEstimated * 2 + 2));

		sap.setReason("biodx");
		sap.setLocation("http://www.biodx.com/");
		sap.preClose(exc);

		InputStream data = sap.getRangeStream();

		MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
		byte buf[] = new byte[8192];
		int n;
		while ((n = data.read(buf)) > 0) {
			messageDigest.update(buf, 0, n);
		}
		byte hash[] = messageDigest.digest();

		Calendar cal = Calendar.getInstance();
		PdfPKCS7 pk7 = new PdfPKCS7(pk, chain, "SHA1", null, null, false);
		
		byte[] sh = pk7.getAuthenticatedAttributeBytes(hash, cal, null, null, null);
//		byte[] sh = pk7.getAuthenticatedAttributeBytes(hash, null, null, null);
		pk7.update(sh, 0, sh.length);

		byte[] sg = pk7.getEncodedPKCS7(hash, cal);
//		byte[] sg = pk7.getEncodedPKCS7(hash);

		if (contentEstimated + 2 < sg.length){
			throw new DocumentException("?????????");
		}
		byte[] paddedSig = new byte[contentEstimated];

		System.arraycopy(sg, 0, paddedSig, 0, sg.length);

		PdfDictionary dic2 = new PdfDictionary();

		dic2.put(PdfName.CONTENTS, new PdfString(paddedSig).setHexWriting(true));
		stp.close();
		sap.close(dic2);
		System.out.println("---结束签章 ---" + silicFile);
	}
	
	/**
	 * 返回项目路径 
	 * @return
	 */
	public static String getPath(){
		return SealPdfUtil.class.getResource("/").getPath();
	}
	/**
	 * 返回工程的路径 
	 * @return
	 */
	public static String getProjectPath(){
		String path = getPath();
		return path.replace("/WEB-INF/classes/", "");
	}
}
