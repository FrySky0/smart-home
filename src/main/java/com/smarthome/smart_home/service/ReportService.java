package com.smarthome.smart_home.service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Stream;

import org.openpdf.text.Chunk;
import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.BaseFont;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smarthome.smart_home.enums.automation.TriggerEvent;
import com.smarthome.smart_home.model.ActivityLog;
import com.smarthome.smart_home.model.AutomationRule;
import com.smarthome.smart_home.model.Device;
import com.smarthome.smart_home.model.Room;
import com.smarthome.smart_home.model.Sensor;
import com.smarthome.smart_home.repository.ActivityLogRepository;
import com.smarthome.smart_home.repository.RoomRepository;


import java.awt.Color;

import org.openpdf.text.pdf.BaseField;

import com.smarthome.smart_home.repository.AutomationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ActivityLogRepository logRepository;
    private final RoomRepository roomRepository;
    private final AutomationRepository automationRepository;
    @Transactional(readOnly=true)
    public byte[] generateActivityReportPDF() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();
            
            String fontPath = new ClassPathResource("fonts/arialmt.ttf").getURL().toString();
            BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            Font headFont = new Font(bf, 22, Font.BOLD, Color.DARK_GRAY);
            Font sectionFont = new Font(bf, 16, Font.BOLD, Color.BLUE);
            Font subSectionFont = new Font(bf, 12, Font.BOLD);
            Font normalFont = new Font(bf, 10, Font.NORMAL);

            Paragraph mainTitle = new Paragraph("SMART HOME FULL SYSTEM REPORT", headFont);
            mainTitle.setAlignment(Element.ALIGN_CENTER);
            mainTitle.setSpacingAfter(30);
            document.add(mainTitle);

            document.add(new Paragraph("1. SYSTEM STATUS REPORT", sectionFont));
            document.add(new Paragraph("Current state of rooms, devices and sensors", normalFont));
            document.add(new Chunk("\n"));

            List<Room> rooms = roomRepository.findAll();
            for (Room room : rooms) {
                Paragraph roomHeader = new Paragraph("Room: " + room.getName() + " (Floor " + room.getFloor() + ")", subSectionFont);
                roomHeader.setSpacingBefore(10);
                document.add(roomHeader);

                PdfPTable roomTable = new PdfPTable(4);
                roomTable.setWidthPercentage(100);
                roomTable.setSpacingBefore(5);
                roomTable.setWidths(new float[]{3, 2, 2, 2});

                addTableHeader(roomTable, new String[]{"Entity Name", "Type", "Status/Value", "UUID (Last 4)"}, Color.LIGHT_GRAY);

                for (Device d : room.getDevices()) {
                    roomTable.addCell(new Phrase(d.getName() + " (Device)", normalFont));
                    roomTable.addCell(new Phrase(d.getType().name(), normalFont));
                    String statusText = d.getStatus().name() + (d.getValue() != null ? " (" + d.getValue() + ")" : "");
                    roomTable.addCell(new Phrase(statusText, normalFont));
                    roomTable.addCell(new Phrase("..." + d.getUuid().toString().substring(32), normalFont));
                }

                for (Sensor s : room.getSensors()) {
                    roomTable.addCell(new Phrase(s.getName() + " (Sensor)", normalFont));
                    roomTable.addCell(new Phrase(s.getType().name(), normalFont));
                    roomTable.addCell(new Phrase(s.getValue().toString(), normalFont));
                    roomTable.addCell(new Phrase("..." + s.getUuid().toString().substring(32), normalFont));
                }

                if (room.getDevices().isEmpty() && room.getSensors().isEmpty()) {
                    PdfPCell emptyCell = new PdfPCell(new Phrase("No devices or sensors in this room"));
                    emptyCell.setColspan(4);
                    emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    roomTable.addCell(emptyCell);
                }

                document.add(roomTable);
            }

            document.newPage();

            document.add(new Paragraph("2. SYSTEM AUTOMATION RULES", sectionFont));
            document.add(new Paragraph("Правила автоматизации",normalFont));
            document.add(new Chunk("\n"));

            PdfPTable ruleTable = new PdfPTable(4);
            ruleTable.setWidthPercentage(100);
            ruleTable.setWidths(new float[]{3, 4, 4, 2});
            addTableHeader(ruleTable, new String[]{"Rule Name", "Trigger Condition", "Action", "State"}, new Color(204, 255, 204));

            List<AutomationRule> rules = automationRepository.findAll();
            for (AutomationRule rule : rules) {
                ruleTable.addCell(new Phrase(rule.getName(), normalFont));

                String triggerDesc = (rule.getTriggerEvent() == TriggerEvent.TIME) 
                    ? "At " + rule.getTriggerTime()
                    : "If " + (rule.getSensor() != null ? rule.getSensor().getName() : "Unknown") 
                        + " " + rule.getTriggerEvent() + " " + rule.getTriggerValue();
                ruleTable.addCell(new Phrase(triggerDesc, normalFont));

                String actionDesc = rule.getAction() + " on " + rule.getDevice().getName() 
                    + (rule.getActionValue() != null ? " to " + rule.getActionValue() : "");
                ruleTable.addCell(new Phrase(actionDesc, normalFont));

                PdfPCell stateCell = new PdfPCell(new Phrase(rule.isEnabled() ? "ACTIVE" : "DISABLED", normalFont));
                stateCell.setBackgroundColor(rule.isEnabled() ? new Color(220, 255, 220) : new Color(255, 220, 220));
                ruleTable.addCell(stateCell);
            }
            document.add(ruleTable);

            
            document.add(new Paragraph("3. SYSTEM ACTIVITY LOG", sectionFont));
            document.add(new Paragraph("Недавние события", normalFont));
            document.add(new Chunk("\n"));

            PdfPTable logTable = new PdfPTable(4);
            logTable.setWidthPercentage(100);
            logTable.setWidths(new float[]{3, 2, 2, 5});

            addTableHeader(logTable, new String[]{"Timestamp", "Component", "Action", "Details"}, new Color(173, 216, 230));

            List<ActivityLog> logs = logRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
            for (ActivityLog log : logs) {
                logTable.addCell(new Phrase(log.getTimestamp().toString(), normalFont));
                logTable.addCell(new Phrase(log.getComponentName().name(), normalFont));
                logTable.addCell(new Phrase(log.getAction().name(), normalFont));
                logTable.addCell(new Phrase(log.getDetails(), normalFont));
            }

            document.add(logTable);
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e);
        }
    }
    private void addTableHeader(PdfPTable table, String[] headers, Color color) {
        for (String headerTitle : headers) {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(color);
            header.setPhrase(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            header.setPadding(5);
            table.addCell(header);
        }
    }
}
