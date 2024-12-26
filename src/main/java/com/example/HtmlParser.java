
package com.example;

import com.example.model.Module;
import com.example.model.ReflectorData;
import com.example.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HtmlParser {
    public static void main(String[] args) {
        try {
            String htmlFile = "src/main/resources/xlx303.html";
            File input = new File(htmlFile);
            Document doc = Jsoup.parse(input, "UTF-8");

            ReflectorData reflectorData = parseDocument(doc);

            // Pretty print JSON
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            String json = mapper.writeValueAsString(reflectorData);

            System.out.println(json);

            // Optionally save to file
            mapper.writeValue(new File("reflector_data.json"), reflectorData);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ReflectorData parseDocument(Document doc) {
        ReflectorData data = new ReflectorData();

        // Parse header information
        String headerText = doc.selectFirst("#top").text();
        String[] headerParts = headerText.split("-");
        data.setVersion(headerParts[0].trim().split("v")[1].trim());
        data.setDashboardVersion(headerParts[1].split("Dashboard v")[1].trim());
        data.setUptime(doc.selectFirst("#suptime").text());

        // Parse users
        data.setUsers(parseUsers(doc));

        // Parse modules
        data.setModules(parseModules(doc));

        return data;
    }

    private static List<User> parseUsers(Document doc) {
        List<User> users = new ArrayList<>();
        Elements userRows = doc.select("table.listingtable tr:has(td)");

        for (Element row : userRows) {
            Elements cells = row.select("td");
            if (cells.size() >= 8) {
                User user = new User();

                // Check if user is transmitting
                Element firstCell = cells.get(0);
                user.setTransmitting(firstCell.select("img[src*=tx]").size() > 0);
                user.setNumber(firstCell.text().trim());

                // Get country from flag image alt text
                user.setCountry(cells.get(1).select("img").attr("alt"));

                user.setCallSign(cells.get(2).text().trim());
                user.setSuffix(cells.get(3).text().trim());
                user.setDprsEnabled(cells.get(4).select("img").size() > 0);
                user.setViaPeer(cells.get(5).text().trim());
                user.setLastHeard(cells.get(6).text().trim());
                user.setListeningOn(cells.get(7).text().trim());

                users.add(user);
            }
        }

        return users;
    }

    private static List<Module> parseModules(Document doc) {
        List<Module> modules = new ArrayList<>();
        Elements moduleHeaders = doc.select("table.listingtable:last-of-type tr:first-of-type th");

        for (Element header : moduleHeaders) {
            Module module = new Module();
            String[] parts = header.html().split("<br>");

            module.setName(parts[0].trim());

            List<String> connections = new ArrayList<>();
            for (String part : parts) {
                if (part.contains("WIRES-X")) {
                    connections.add("WIRES-X: " + part.replaceAll(".*WIRES-X\\s+", "").trim());
                } else if (part.contains("AllStar")) {
                    connections.add("AllStar: " + part.replaceAll(".*AllStar\\s+", "").trim());
                } else if (part.contains("BM")) {
                    connections.add("BrandMeister: " + part.replaceAll(".*BM\\s+", "").trim());
                } else if (part.contains("DG-ID")) {
                    module.setDgId(part.replaceAll(".*DG-ID\\s+", "").trim());
                }
            }
            module.setConnections(connections);

            // Get the label (last character, usually A-M)
            String label = header.html().replaceAll(".*<br\\s*/>\\s*", "").trim();
            module.setLabel(label);

            // Get nodes for this module
            String moduleSelector = "table.listingtable:last-of-type tr:eq(1) td:eq(" + moduleHeaders.indexOf(header) + ") table tr td a";
            List<String> nodes = doc.select(moduleSelector).stream()
                    .map(Element::text)
                    .collect(Collectors.toList());
            module.setNodes(nodes);

            modules.add(module);
        }

        return modules;
    }
}
