package com.capitalbio.pdf;

import com.capitalbio.pdf.model.DocModel;

public class PdfWindowsTest {
	public static void main(String[] args) throws Exception{
		XmlReader reader = new XmlReader();
		String srcFile = "E:\\develop\\asp_2i\\src\\com\\bio\\asp\\pdf\\example.xml";
		DocModel docModel = reader.parse(srcFile);
		PdfGenerator gen = new PdfGenerator();
		gen.setChartDirName("E:\\develop\\asp_2i\\WebRoot\\pdf\\");
		gen.setFileName("E:\\develop\\asp_2i\\WebRoot\\pdf\\example.pdf");
		gen.setTempFileName("E:\\develop\\asp_2i\\WebRoot\\pdf\\temp.pdf");
		gen.generatePdf(docModel,"");

	}
}
