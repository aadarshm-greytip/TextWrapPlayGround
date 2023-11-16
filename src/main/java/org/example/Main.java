package org.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Main {
    private static final int THOUSANDTHS_OF_FONT_UNIT = 1000;
    private static final int MIN_DEFAULT_FONT_SIZE_6 = 6;
    private static final int MAX_TEXT_WIDTH = 150;
    private static final String ELLIPSES = "...";
    private static final String SPACE = " ";


    /*
     "value": "#262 5th main bikasipura #262 5th main bikasipura ",
                "prefilledMapReferenceId": 76,
                "qbReference": "permanentaddr_cat.empPermanentAddr.address1",
                "length": 20,
                "width": 195,
                "xcordPdf": 86.5,
                "ycordPdf": 729.32,
                "page": 5
     */
    public void wrappingTextInRectangle(PDPageContentStream contentStream, PDType1Font font, float fontSize,boolean isRotation) throws IOException {
      //  String value = "#262 5th main bikasipura #262 5th main bikasipura #262 5th main bikasipura #262 5th main bikasipura #262 5th main bikasipura #262 5th main bikasipura #262 5th main bikasipura #262 5th main bikasipura";
       String value= "aadarshmishra46@gmail.com";
            float width = 76;
        float height = 66;
        PDRectangle rect = new PDRectangle(129,221, width, height); // rectangle to fit the text
        String text = value;
        if (text.length() > 150) {
            text= text.substring(0, 150 - 3) + "...";
        }
        contentStream.beginText();
        contentStream.setFont(font, fontSize);

        // calculate the width of the text based on the font and size
        float textWidth = font.getStringWidth(text) / 1000 * fontSize;

        // calculate the width and height of the rectangle
        float rectWidth = rect.getWidth();

        // if the width of the text is greater than the width of the rectangle,
        // then we need to shrink the font size to fit the text inside the rectangle
        if (textWidth > rectWidth) {
            fontSize = (fontSize * rectWidth) / textWidth;
            contentStream.setFont(font, fontSize);
        }


        // calculate the number of lines needed to fit the text inside the rectangle
        int numLines = (int) Math.ceil(textWidth / rectWidth);

        // calculate the height of each line
        float lineHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;

        // calculate the starting position of the text
//        float startX = 595f - 223;   // 592 ---
//        float startY = 68;
        float startX =  595 - 223;   // 592 ---
        float startY = 68;


        /*

]

Type: text
Value: aadarshmishra46@gmail.com
Width: 76
Height: 66
x---->68
y---->223
startX---->68.0
startY---->223.0

        Value: aadarshmishra46@gmail.com Accepted
Width: 76
Height: 66
x---->68
y---->223
-->
-->
Value: Manoj Mishra
Width: 195
Height: 20
x---->241
y---->464  (Accepted)


Value: ETTPM0048E
Width: 195
Height: 20
x---->239
y---->435
startX---->239.0
startY---->435.0

        x---->551
y---->204

x---->312
y---->214

x---->129
y---->221


Type: text
Value: 123412341234
Width: 195
Height: 20
x---->432
y---->213


         */
        // loop through each line of the text and draw it on the page
        for (int i = 0; i < numLines; i++) {
            int startIndex = i * (int) (text.length() / numLines);
            int endIndex = (i == numLines - 1) ? text.length() : (i + 1) * (int) (text.length() / numLines);

            String line = text.substring(startIndex, endIndex);

            if (isRotation) {
                float rotatedX = startX + (i * lineHeight); // Adjust X
                float rotatedY = startY; // Adjust Y

                contentStream.setTextMatrix(new Matrix(0, 1, -1, 0, rotatedX, rotatedY));


//                float lineX = startX;
//                float lineY = startY - (i * lineHeight);
//                contentStream.setTextMatrix(new Matrix(1, 0, 0, 1, lineX, lineY));
            } else {
                float lineX = startX;
                float lineY = startY - (i * lineHeight);
                contentStream.setTextMatrix(new Matrix(1, 0, 0, 1, lineX, lineY));
            }

            contentStream.showText(line);
        }
        System.out.println(fontSize);
        contentStream.endText();
    }

    public static void main(String[] args) {
        Main textWrapper = new Main();
        String downloadsPath = System.getProperty("user.home") + "/Documents/";
        String filePath = downloadsPath + "vertical.pdf"; // Replace "file.pdf" with the name of your downloaded file
        String outputFileName = "output.pdf";
        String outputPath = downloadsPath + outputFileName;
        try (PDDocument document = PDDocument.load(new File(filePath))) { // rotate
            PDPage page = document.getPage(0); // Get the first page of the document

            System.out.println(page.getRotation());
            PDPageContentStream contentStream = new PDPageContentStream(document, page, true, true);
            PDType1Font font = PDType1Font.HELVETICA;
            float fontSize = 12f;
            boolean isRotation = false;
            if (page.getRotation()==90 || page.getRotation()==270){ // rotate
                System.out.println("rotatation");
               // page.setRotation(0);
                isRotation =true;
           //     page.setRotation(0);
            }
            else{
                System.out.println("not rotated");
            }
            try {
                textWrapper.wrappingTextInRectangle(contentStream, font, fontSize,isRotation);


            } catch (IOException e) {
                e.printStackTrace();
            }

            contentStream.close();
            document.save(outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
