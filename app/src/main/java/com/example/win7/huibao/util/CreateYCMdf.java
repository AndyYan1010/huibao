package com.example.win7.huibao.util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

 
public class CreateYCMdf{ 
    Document document = new Document();// 建立一个Document对象
     
    private static Font headfont ;// 设置字体大小 
    private static Font keyfont;// 设置字体大小 
    private static Font  headfont1;
    private static Font keyfont11;
    private static Font keyfonts;

     
    public CreateYCMdf(File file) {
         try {
            document.setPageSize(new Rectangle(595.0F,281.0F));
            PdfWriter.getInstance(document,new FileOutputStream(file)); 
            document.open();  
        } catch (Exception e) { 
            e.printStackTrace(); 
        }  
         
         
    } 
    int maxWidth = 520; 

     public PdfPTable createTable(int colNumber){ 
        PdfPTable table = new PdfPTable(colNumber); 
        try{ 
            table.setTotalWidth(maxWidth); 
            table.setLockedWidth(true); 
            table.setHorizontalAlignment(Element.ALIGN_CENTER);      
            table.getDefaultCell().setBorder(1); 
        }catch(Exception e){ 
            e.printStackTrace(); 
        } 
        return table; 
    } 

      
     public void generateYCMPDF(Map<String,String> map) throws Exception{
         BaseFont bfChinese = //BaseFont.createFont("C:/WINDOWS/Fonts/SIMSUN.TTC,1",BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
         BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
//         BaseFont bfHei = BaseFont.createFont("c:/WINDOWS/fonts/SIMHEI.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
         headfont = new Font(bfChinese, 20, Font.BOLD);
         keyfont = new Font(bfChinese, 8, Font.NORMAL);// 设置字体大小
         headfont1 = new Font(bfChinese, 8, Font.NORMAL);
         keyfont11 = new Font(bfChinese, 8, Font.BOLD);
         keyfont11.setColor(BaseColor.RED);
         keyfonts = new Font(bfChinese, 8, Font.BOLD);

         //加入空行
         Paragraph blankRow2 = new Paragraph(8f, " ", keyfont);

         //单据号
         PdfPTable table0 = createTable(6);
         table0.setTotalWidth(500);
         int width0[] = {8,20,8,20,8,20};
         table0.setWidths(width0);
         PdfPCell cell380 = new PdfPCell(new Paragraph("No:",keyfont));
         PdfPCell cell380s = new PdfPCell(new Paragraph(map.get("fbillno"),keyfont11));
         PdfPCell cell390 = new PdfPCell(new Paragraph("币别",keyfont));
         PdfPCell cell390s = new PdfPCell(new Paragraph("人民币",keyfonts));
         PdfPCell cell400 = new PdfPCell(new Paragraph("汇率",keyfont));
         PdfPCell cell400s = new PdfPCell(new Paragraph("1.0",keyfonts));
         cell380.setBorder(0);
         cell380s.setBorder(0);
         cell380s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell380s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell390.setBorder(0);
         cell390s.setBorder(0);
         cell390s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell390s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell400.setBorder(0);
         cell400s.setBorder(0);
         cell400s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell400s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         table0.addCell(cell380);
         table0.addCell(cell380s);
         table0.addCell(cell390);
         table0.addCell(cell390s);
         table0.addCell(cell400);
         table0.addCell(cell400s);
         document.add(table0);
         
         //表头 第二行
         PdfPTable table5 = createTable(6);
         table5.setTotalWidth(500);
         int width5[] = {16,20,16,20,16,20};
         table5.setWidths(width5);
         PdfPCell cell41 = new PdfPCell(new Paragraph("组织机构",keyfont));
         PdfPCell cell41s = new PdfPCell(new Paragraph(map.get("zuzhi"),keyfonts));
         PdfPCell cell42 = new PdfPCell(new Paragraph("申请部门",keyfont));
         PdfPCell cell42s = new PdfPCell(new Paragraph(map.get("shenqing"),keyfonts));
         PdfPCell cell43 = new PdfPCell(new Paragraph("责任部门",keyfont));
         PdfPCell cell43s = new PdfPCell(new Paragraph(map.get("zeren"),keyfonts));
         cell41.setBorder(0);
         cell41s.setBorder(2);
         cell41s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell41s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell42.setBorder(0);
         cell42s.setBorder(2);
         cell42s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell42s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell43.setBorder(0);
         cell43s.setBorder(2);
         cell43s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell43s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         table5.addCell(cell41);
         table5.addCell(cell41s);
         table5.addCell(cell42);
         table5.addCell(cell42s);
         table5.addCell(cell43);
         table5.addCell(cell43s);
         document.add(table5);

         //表头 第三行
         PdfPTable table6 = createTable(6);
         table6.setTotalWidth(500);
         int width6[] = {16,20,16,20,16,20};
         table6.setWidths(width6);
         PdfPCell cell44 = new PdfPCell(new Paragraph("责任人",keyfont));
         PdfPCell cell44s = new PdfPCell(new Paragraph(map.get("respon"),keyfonts));
         PdfPCell cell45 = new PdfPCell(new Paragraph("制单人",keyfont));
         PdfPCell cell45s = new PdfPCell(new Paragraph(map.get("zhidan"),keyfonts));
         PdfPCell cell4s = new PdfPCell(new Paragraph("往来",keyfont));
         PdfPCell cell4sp = new PdfPCell(new Paragraph(map.get("contacts"),keyfonts));
         cell44.setBorder(0);
         cell44s.setBorder(2);
         cell44s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell44s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell45.setBorder(0);
         cell45s.setBorder(2);
         cell45s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell45s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell4s.setBorder(0);
         cell4sp.setBorder(2);
         table6.addCell(cell44);
         table6.addCell(cell44s);
         table6.addCell(cell45);
         table6.addCell(cell45s);
         table6.addCell(cell4s);
         table6.addCell(cell4sp);
         document.add(table6);

         //表头 第四行
         PdfPTable table60 = createTable(6);
         table60.setTotalWidth(500);
         int width60[] = {16,20,16,20,16,20};
         table60.setWidths(width60);
         SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
         PdfPCell cell440 = new PdfPCell(new Paragraph("内容",keyfont));
         PdfPCell cell440s = new PdfPCell(new Paragraph(map.get("neirong"),keyfonts));
         PdfPCell cell450 = new PdfPCell(new Paragraph("计量",keyfont));
         PdfPCell cell450s = new PdfPCell(new Paragraph(map.get("jiliang"),keyfonts));
         PdfPCell cell40s = new PdfPCell(new Paragraph("",keyfont));
         PdfPCell cell40sp = new PdfPCell(new Paragraph("",keyfonts));
         cell440.setBorder(0);
         cell440s.setBorder(2);
         cell440s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell440s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell450.setBorder(0);
         cell450s.setBorder(2);
         cell450s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell450s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell40s.setBorder(0);
         cell40sp.setBorder(0);
         table60.addCell(cell440);
         table60.addCell(cell440s);
         table60.addCell(cell450);
         table60.addCell(cell450s);
         table60.addCell(cell40s);
         table60.addCell(cell40sp);
         document.add(table60);

         document.add(blankRow2);

         //表体 第一行
         PdfPTable table8 = createTable(2);
         table8.setTotalWidth(500);
         int width8[] = {16,92};
         table8.setWidths(width8);
         PdfPCell cell8 = new PdfPCell(new Paragraph("备注",keyfont));
         PdfPCell cell8s = new PdfPCell(new Paragraph(map.get("note"),keyfonts));
         cell8s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell8s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         table8.addCell(cell8);
         table8.addCell(cell8s);
         document.add(table8);

         //表体 第二行
         PdfPTable table7 = createTable(6);
         table7.setTotalWidth(500);
         int width7[] = {16,20,16,20,16,20};
         table7.setWidths(width7);
         PdfPCell cell7 = new PdfPCell(new Paragraph("数量",keyfont));
         PdfPCell cell7s = new PdfPCell(new Paragraph(map.get("shuliang"),keyfonts));
         PdfPCell cell71 = new PdfPCell(new Paragraph("单价",keyfont));
         PdfPCell cell71s = new PdfPCell(new Paragraph(map.get("danjia"),keyfonts));
         PdfPCell cell72 = new PdfPCell(new Paragraph("金额含税",keyfont));
         PdfPCell cell72s = new PdfPCell(new Paragraph(map.get("hanshui"),keyfonts));
         cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell7s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell7s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell71s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell71.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell71s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell72s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell72s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell72.setVerticalAlignment(Element.ALIGN_MIDDLE);
         table7.addCell(cell7);
         table7.addCell(cell7s);
         table7.addCell(cell71);
         table7.addCell(cell71s);
         table7.addCell(cell72);
         table7.addCell(cell72s);
         document.add(table7);



         //表体 第三行
         PdfPTable table10 = createTable(4);
         table10.setTotalWidth(500);
         int width10[] = {16,38,16,38};
         table10.setWidths(width10);
         PdfPCell cell11 = new PdfPCell(new Paragraph("启日期",keyfont));
         PdfPCell cell11s = new PdfPCell(new Paragraph(map.get("qi"),keyfonts));
         PdfPCell cell12 = new PdfPCell(new Paragraph("止日期",keyfont));
         PdfPCell cell12s = new PdfPCell(new Paragraph(map.get("zhi"),keyfonts));
         cell11s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell11.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell11s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell12s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell12.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell12s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         table10.addCell(cell11);
         table10.addCell(cell11s);
         table10.addCell(cell12);
         table10.addCell(cell12s);
         document.add(table10);

         //表体 第四行
         PdfPTable table9 = createTable(4);
         table9.setTotalWidth(500);
         int width9[] = {16,38,16,38};
         table9.setWidths(width9);
         PdfPCell cell9 = new PdfPCell(new Paragraph("计划预算进度",headfont1));
         PdfPCell cell9s = new PdfPCell(new Paragraph(map.get("progress"),keyfonts));
         PdfPCell cell10 = new PdfPCell(new Paragraph("计划",keyfont));
         PdfPCell cell10s = new PdfPCell(new Paragraph(map.get("plan"),keyfonts));
         cell9s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell9.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell9s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell10s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell10.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell10s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         table9.addCell(cell9);
         table9.addCell(cell9s);
         table9.addCell(cell10);
         table9.addCell(cell10s);
         document.add(table9);

         //表体 第五行
         PdfPTable table11 = createTable(6);
         table11.setTotalWidth(500);
         int width11[] = {16,20,16,20,16,20};
         table11.setWidths(width11);
         PdfPCell cell13 = new PdfPCell(new Paragraph("预算",keyfont));
         PdfPCell cell13s = new PdfPCell(new Paragraph(map.get("budget"),keyfonts));
         PdfPCell cell14 = new PdfPCell(new Paragraph("计划预算额",keyfont));
         PdfPCell cell14s = new PdfPCell(new Paragraph(map.get("pbudget"),keyfonts));
         PdfPCell cell15 = new PdfPCell(new Paragraph("人民币不含税额",headfont1));
         PdfPCell cell15s = new PdfPCell(new Paragraph(map.get("hanshui"),keyfonts));
         cell13s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell13.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell13s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell14s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell14.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell14s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell15s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell15.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell15s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         table11.addCell(cell13);
         table11.addCell(cell13s);
         table11.addCell(cell14);
         table11.addCell(cell14s);
         table11.addCell(cell15);
         table11.addCell(cell15s);
         document.add(table11);

         //表体 第六行
         PdfPTable table12 = createTable(6);
         table12.setTotalWidth(500);
         int width12[] = {16,20,16,20,16,20};
         table12.setWidths(width12);
         PdfPCell cell16 = new PdfPCell(new Paragraph("辅助",keyfont));
         PdfPCell cell16s = new PdfPCell(new Paragraph(map.get("fuzhu"),keyfonts));
         PdfPCell cell17 = new PdfPCell(new Paragraph("辅量",keyfont));
         PdfPCell cell17s = new PdfPCell(new Paragraph(map.get("fuliang"),keyfonts));
         PdfPCell cell18 = new PdfPCell(new Paragraph("评分",keyfont));
         PdfPCell cell18s = new PdfPCell(new Paragraph(map.get("pingfen"),keyfonts));
         cell16s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell16.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell16s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell17s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell17.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell17s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell18s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell18.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell18s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         table12.addCell(cell16);
         table12.addCell(cell16s);
         table12.addCell(cell17);
         table12.addCell(cell17s);
         table12.addCell(cell18);
         table12.addCell(cell18s);
         document.add(table12);

         //表体 第七行
         PdfPTable table13 = createTable(2);
         table13.setTotalWidth(500);
         int width13[] = {16,92};
         table13.setWidths(width13);
         PdfPCell cell19 = new PdfPCell(new Paragraph("发送消息",keyfont));
         PdfPCell cell19s = new PdfPCell(new Paragraph(map.get("fasong"),keyfonts));
         cell19s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell19.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell19s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         table13.addCell(cell19);
         table13.addCell(cell19s);
         document.add(table13);

         //表体 第八行
         PdfPTable table14 = createTable(2);
         table14.setTotalWidth(500);
         int width14[] = {16,92};
         table14.setWidths(width14);
         PdfPCell cell20 = new PdfPCell(new Paragraph("回馈消息",keyfont));
         PdfPCell cell20s = new PdfPCell(new Paragraph(map.get("huikui"),keyfonts));
         cell20s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell20.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell20s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         table14.addCell(cell20);
         table14.addCell(cell20s);
         document.add(table14);

         //表体 第九行
         PdfPTable table15 = createTable(8);
         table15.setTotalWidth(500);
         int width15[] = {8,20,10,20,8,20,10,20};
         table15.setWidths(width15);
         PdfPCell cell21 = new PdfPCell(new Paragraph("一审",keyfont));
         PdfPCell cell21s;
         if(map.get("qr1").equals("True")){
            cell21s = new PdfPCell(new Paragraph(map.get("a")+"(已确认)",keyfonts));
         }else{
             cell21s = new PdfPCell(new Paragraph(map.get("a"),keyfonts));
         }
         PdfPCell cel21 = new PdfPCell(new Paragraph("确认时间",keyfont));
         PdfPCell cel21s = new PdfPCell(new Paragraph(map.get("ftime2"),keyfonts));
         PdfPCell cell22 = new PdfPCell(new Paragraph("二审",keyfont));
         PdfPCell cell22s;
         if(map.get("qr2").equals("True")){
             cell22s = new PdfPCell(new Paragraph(map.get("b")+"(已确认)",keyfonts));
         }else{
             cell22s = new PdfPCell(new Paragraph(map.get("b"),keyfonts));
         }
         PdfPCell cel22 = new PdfPCell(new Paragraph("确认时间",keyfont));
         PdfPCell cel22s = new PdfPCell(new Paragraph(map.get("ftime3"),keyfonts));
         cell21s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell21.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell21s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cel21s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cel21.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cel21s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell22s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell22.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell22s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cel22s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cel22.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cel22s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         table15.addCell(cell21);
         table15.addCell(cell21s);
         table15.addCell(cel21);
         table15.addCell(cel21s);
         table15.addCell(cell22);
         table15.addCell(cell22s);
         table15.addCell(cel22);
         table15.addCell(cel22s);
         document.add(table15);

         //表体 第十行
         PdfPTable table16 = createTable(8);
         table16.setTotalWidth(500);
         int width16[] = {8,20,10,20,8,20,10,20};
         table16.setWidths(width16);
         PdfPCell cell23 = new PdfPCell(new Paragraph("三审",keyfont));
         PdfPCell cell23s;
         if(map.get("qr3").equals("True")){
             cell23s = new PdfPCell(new Paragraph(map.get("c")+"(已确认)",keyfonts));
         }else{
             cell23s = new PdfPCell(new Paragraph(map.get("c"),keyfonts));
         }
         PdfPCell cel23 = new PdfPCell(new Paragraph("确认时间",keyfont));
         PdfPCell cel23s = new PdfPCell(new Paragraph(map.get("ftime4"),keyfonts));
         PdfPCell cell24 = new PdfPCell(new Paragraph("四审",keyfont));
         PdfPCell cell24s;
         if(map.get("qr4").equals("True")){
             cell24s = new PdfPCell(new Paragraph(map.get("d")+"(已确认)",keyfonts));
         }else{
             cell24s = new PdfPCell(new Paragraph(map.get("d"),keyfonts));
         }
         PdfPCell cel24 = new PdfPCell(new Paragraph("确认时间",keyfont));
         PdfPCell cel24s = new PdfPCell(new Paragraph(map.get("ftime5"),keyfonts));
         cell23s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell23.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell23s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cel23s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cel23.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cel23s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell24s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell24.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell24s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cel24s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cel24.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cel24s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         table16.addCell(cell23);
         table16.addCell(cell23s);
         table16.addCell(cel23);
         table16.addCell(cel23s);
         table16.addCell(cell24);
         table16.addCell(cell24s);
         table16.addCell(cel24);
         table16.addCell(cel24s);
         document.add(table16);

         //表体 第九行
         PdfPTable table17 = createTable(6);
         table17.setTotalWidth(500);
         int width17[] = {8,20,10,20,20,38};
         table17.setWidths(width17);
         PdfPCell cell25 = new PdfPCell(new Paragraph("五审",keyfont));
         PdfPCell cell25s;
         if(map.get("qr5").equals("True")){
             cell25s = new PdfPCell(new Paragraph(map.get("e")+"(已确认)",keyfonts));
         }else{
             cell25s = new PdfPCell(new Paragraph(map.get("e"),keyfonts));
         }
         PdfPCell cel25 = new PdfPCell(new Paragraph("确认时间",keyfont));
         PdfPCell cel25s = new PdfPCell(new Paragraph(map.get("ftime6"),keyfonts));
         PdfPCell cell26 = new PdfPCell(new Paragraph("",keyfont));
         PdfPCell cell26s = new PdfPCell(new Paragraph("",keyfonts));
         cell25s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cell25.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell25s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cel25s.setHorizontalAlignment(Element.ALIGN_CENTER);
         cel25.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cel25s.setVerticalAlignment(Element.ALIGN_MIDDLE);
         cell26.setBorder(0);
         cell26s.setBorder(0);
         table17.addCell(cell25);
         table17.addCell(cell25s);
         table17.addCell(cel25);
         table17.addCell(cel25s);
         table17.addCell(cell26);
         table17.addCell(cell26s);
         document.add(table17);

         PdfPTable table18 = createTable(5);
         table18.setTotalWidth(500);
         int width18[] = {80,15,15,15,15};
         table18.setWidths(width18);
         PdfPCell cell47 = new PdfPCell(new Paragraph("",keyfont));
         PdfPCell cell48 = new PdfPCell(new Paragraph("打印人:",keyfont));
         PdfPCell cell49 = new PdfPCell(new Paragraph(map.get("username"),keyfonts));
         PdfPCell cell50 = new PdfPCell(new Paragraph("打印日期:",keyfont));
         PdfPCell cell51 = new PdfPCell(new Paragraph(new SimpleDateFormat("yyyy-MM-dd").format(new Date()),keyfonts));
         cell47.setBorder(0);
         cell48.setBorder(0);
         cell49.setBorder(0);
         cell50.setBorder(0);
         cell51.setBorder(0);
         table18.addCell(cell47);
         table18.addCell(cell48);
         table18.addCell(cell49);
         table18.addCell(cell50);
         table18.addCell(cell51);
         document.add(table18);

         document.close();
     }


//     public static void main_older(String[] args) throws Exception {
//         System.out.println("begin");
//         File file = new File("d:\\text1111.pdf");
//         file.createNewFile();
//         new CreateYCMdf(file).generateYCMPDF();
//         System.out.println("end");
//    }
} 