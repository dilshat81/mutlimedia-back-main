package com.doranco.multimedia.serviceImpl;

import com.doranco.multimedia.constents.MultimediaConstants;
import com.doranco.multimedia.jwt.JwtFilter;
import com.doranco.multimedia.models.Bill;
import com.doranco.multimedia.repositories.BillDao;
import com.doranco.multimedia.service.BillService;
import com.doranco.multimedia.utils.MultimediaUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class BillServiceImpl implements BillService {
    
    @Autowired
    private BillDao billDao;
    @Autowired
    private JwtFilter jwtFilter;
    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        log.info("inside generateReport");
        try {
            String fileName;
            if(validateRequestMap(requestMap)){
               if(requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate")){
                   fileName = (String) requestMap.get("uuid");
               }else{
                   fileName = MultimediaUtils.getUUID();
                   requestMap.put("uuid", fileName);
               }
                insertBillWithPdf(requestMap);

                return  new ResponseEntity<>("{\"uuid\":\""+fileName+"\"}",HttpStatus.OK );

            }
            MultimediaUtils.getResponseEntity("Required data not found", HttpStatus.BAD_REQUEST);



        }catch(Exception ex){
            ex.printStackTrace();
        }
        return MultimediaUtils.getResponseEntity(MultimediaConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    private void insertBillWithPdf(Map<String, Object> requestMap)  throws DocumentException, JSONException {
        Bill bill = createBill(requestMap);
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, pdfOutputStream);
        document.open();
        setRectangleInPdf(document);

        String data = "name: " + requestMap.get("name") + "\n" +"Contact Number:" +requestMap.get("contactNumber")
                + "\n"+"Email:" +requestMap.get("email") + "\n"+ "Payment method:" +requestMap.get("paymentMethod")
                + "\n" + "Created date :  " + bill.getCreatedAt().toLocalDate() + "\n\n";



        Paragraph chunk = new Paragraph("Magasin Multimédia Geek Galaxy store", getFont("Header"));
        chunk.setAlignment(Element.ALIGN_CENTER);
        document.add(chunk);

        Paragraph paragraph = new Paragraph(data+ "\n\n", getFont("data"));
        document.add(paragraph);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        addTableHeader(table);

        JSONArray jsonArray = MultimediaUtils.getJsonArrayFromString((String) requestMap.get("productDetails"));
        for(int i=0; i<jsonArray.length(); i++){
            addRows(table, MultimediaUtils.getMapFromJson(jsonArray.getString(i)));
        }

        document.add(table);

        Paragraph footer = new Paragraph("Total: " +requestMap.get("totalAmount")+ "\n"
                + "Merci pour votre visite.À bientôt! ", getFont("data"));

        document.add(footer);
        document.close();
        bill.setPdfData(pdfOutputStream.toByteArray());
        billDao.save(bill);
    }

    private Bill createBill(Map<String, Object> requestMap) {

        Bill bill= new Bill();
        bill.setUuid((String) requestMap.get("uuid"));
        bill.setName((String) requestMap.get("name"));
        bill.setEmail((String) requestMap.get("email"));
        bill.setContactNumber((String) requestMap.get("contactNumber"));
        bill.setPaymentMethod((String) requestMap.get("paymentMethod"));
        bill.setTotal(Integer.parseInt((String) requestMap.get("totalAmount")));
        bill.setProductDetail((String) requestMap.get("productDetails"));
        bill.setCreatedAt(LocalDateTime.now());
        bill.setCreatedBy(jwtFilter.getCurrentUser());

        return bill;
    }

    private void addRows(PdfPTable table, Map<String, Object> data) {

        log.info("Inside addRows");
        table.addCell((String) data.get("name"));
        table.addCell((String) data.get("category"));
        table.addCell((String) data.get("quantity"));
        table.addCell(Double.toString((Double) data.get("price")));
        table.addCell(Double.toString((Double) data.get("total")));
    }

    private void addTableHeader(PdfPTable table) {

        log.info("Inside addTableHeader");
        Stream.of("Name", "Category", "Quantity", "Price", "Sub Total")
                .forEach(columnTitre -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitre));
                    header.setBackgroundColor(BaseColor.GREEN);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);

                });
    }

    private void setRectangleInPdf(Document document) throws DocumentException {

        log.info("Inside setRectangleInPdf");
        Rectangle rectangle = new Rectangle(577, 825,18,15);
        rectangle.enableBorderSide(1);
        rectangle.enableBorderSide(2);
        rectangle.enableBorderSide(4);
        rectangle.enableBorderSide(8);
        rectangle.setBorderColor(BaseColor.BLACK);
        rectangle.setBorderWidth(1);
        document.add(rectangle);

    }

    private Font getFont (String type){
        log.info("Inside getFont");
        switch (type){
            case "Header" :
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            case "Data" :
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return  dataFont;
            default:
                return new Font();

        }
    }



    private boolean validateRequestMap(Map<String, Object> requestMap) {

                return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("paymentMethod") &&
                requestMap.containsKey("productDetails") &&
                requestMap.containsKey("totalAmount");
    }

    @Override
    public ResponseEntity<List<Bill>> getBills() {

        List<Bill> list = new ArrayList<>();

            if(jwtFilter.isAdmin()){
                list = billDao.findAll();
            }else{
                list = billDao.findAllByCreatedBy(jwtFilter.getCurrentUser());
            }


        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        log.info("inside getPdf : requestMap {}", requestMap);
        try {
            if (!requestMap.containsKey("uuid")) {
                requestMap.put("isGenerate", true);
                ResponseEntity<String> reportResponse = generateReport(requestMap);
                if (reportResponse.getStatusCode() != HttpStatus.OK) {
                    return new ResponseEntity<>(new byte[0], reportResponse.getStatusCode());
                }
            }

            Optional<Bill> billOpt = billDao.findByUuid((String) requestMap.get("uuid"));

            if (billOpt.isPresent()) {
                return buildPdfResponse(billOpt.get());
            }
            return new ResponseEntity<>(new byte[0], HttpStatus.NOT_FOUND);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new byte[0], HttpStatus.INTERNAL_SERVER_ERROR);

    }

    private ResponseEntity<byte[]> buildPdfResponse(Bill bill) {
        byte[] pdfBytes = bill.getPdfData();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("inline")
                .filename(bill.getName() + ".pdf")
                .build());
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }




   @Override
    public ResponseEntity<String> deleteBill(String id) {
        try {
            Optional<Bill> optional = billDao.findById(id);
            if(optional.isPresent()) {
                billDao.deleteById(id);
                return MultimediaUtils.getResponseEntity("Facture supprimée avec succès", HttpStatus.OK);
            }else {
                return MultimediaUtils.getResponseEntity("Facture n'existe pas", HttpStatus.OK);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return MultimediaUtils.getResponseEntity(MultimediaConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
