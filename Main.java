
import java.io.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.Properties;
import java.util.List;
import java.util.LinkedList;


public class Main {

    //Функция для корня
    public static String NodeSqrt(NodeList eqProps)
    {
        String str = "";
        String Recursion = eqProps.item(0).getAttributes().getNamedItem("val").getNodeValue(); //Берём поле для вложенности
        String Value = eqProps.item(1).getAttributes().getNamedItem("val").getNodeValue(); //Берём поле для конечных выражений
        if (Recursion.equals(Value)) { //Если поле факта Рекурсии совпадает с полем Значение, то это простое выражение
            str = "\\sqrt{" + Value + "}";
        }
        else { //Иначе это вложенное выражение
            str = "\\sqrt{" + TomitaRun(Recursion + ".") + "}";
        }
        return str;
    }



    public static String GlobalCase(Node eq) {
        NodeList eqProps = eq.getChildNodes(); //Подэлементы - параметры выражения
        //В зависимости от верхнего узла парсинга - корня грамматики
        //Проверяем какой факт пришёл и вызываем соответсвующую функцию
        switch(eq.getNodeName()) {
            case "Sqrt":
                return NodeSqrt(eqProps);
            default:
                return "notFound";
        }
    }

    //Функция для повторных вызовов Томиты
    public static String TomitaRun(String str) {
        TomitaParser tp = new TomitaParser();
        LinkedList<String> strtp = new LinkedList();
        strtp.add(str);
        String ret = "";
        try {
            //Вызываем Томиту посылая туда строку полученную из поля Recursion
            Document document = tp.run(strtp.toArray(new String[strtp.size()]));
            //Получаем корневой элемент из DOM
            Node root = document.getDocumentElement();
            //Просматриваем все подэлементы корневого
            //Структура выходного XML подразумевает что в ноде document находиться нода facts
            //И уже в ноде facts находятся ноды наших фактов
            NodeList docs = root.getChildNodes();
            for (int i = 0; i < docs.getLength(); i++) {
                Node doc = docs.item(i);
                // Если нода не текст - заходим внутрь
                if (doc.getNodeType() != Node.TEXT_NODE) {
                    NodeList facts = doc.getChildNodes();
                    for (int j = 0; j < facts.getLength(); j++) {
                        Node fact = facts.item(j);
                        // Если нода не текст - заходим внутрь
                        if (fact.getNodeType() != Node.TEXT_NODE && !fact.getNodeName().equals("Leads")) {
                            NodeList eqs = fact.getChildNodes();
                            Node eq = eqs.item(0); //Берём вершину парсинга
                            // Если нода не текст - вызываем
                            if (eq.getNodeType() != Node.TEXT_NODE) {
                                System.out.println(eq.getNodeName());
                                ret = GlobalCase(eq); //Вызываем функцию для обработки факта
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
        return ret;
    }

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");

        try {
            //Читаем входной файл
            TomitaParser tp = new TomitaParser();
            List<String> test = new LinkedList();
            BufferedReader br = new BufferedReader(new FileReader("test.txt"));
            try {
                String str = null;
                while ((str = br.readLine()) != null) {
                    test.add(str);
                }
            }
            finally {
                br.close();
            }
            //Посылаем входной файл Томите - Томита возвращает DOM
            Document document = tp.run(test.toArray(new String[test.size()]));

            //файл, который хранит свойства нашего проекта
            File file = new File("data.properties");

            //создаем объект Properties и загружаем в него данные из файла.
            Properties properties = new Properties();
            properties.load(new FileReader(file));

            //получаем значения свойств из объекта Properties
            String tex = properties.getProperty("texfile");

            //Выходной файл
            BufferedWriter writer = new BufferedWriter(new FileWriter(tex));
            //Начало тех документа
            writer.write("\\documentclass{article}\n");
            writer.write("\\begin{document}\n");
            //Получаем корневой элемент из DOM = fdo_objects
            Node root = document.getDocumentElement();
            System.out.println("Mathematical equasion:");
            System.out.println();
            //Просматриваем все подэлементы корневого элемента
            //Структура выходного XML подразумевает что в ноде document находиться нода facts
            //И уже в ноде facts находятся ноды наших фактов
            NodeList docs = root.getChildNodes();
            for (int i = 0; i < docs.getLength(); i++) {
                Node doc = docs.item(i);
                // Если нода не текст - заходим внутрь
                if (doc.getNodeType() != Node.TEXT_NODE) {
                    NodeList facts = doc.getChildNodes();
                    for (int j = 0; j < facts.getLength(); j++) {
                        Node fact = facts.item(j);
                        // Если нода не текст - заходим внутрь
                        if (fact.getNodeType() != Node.TEXT_NODE && !fact.getNodeName().equals("Leads")) {
                            NodeList eqs = fact.getChildNodes();
                            Node eq = eqs.item(0); //Берём вершину парсинга
                            // Если нода не текст - вызываем
                            if (eq.getNodeType() != Node.TEXT_NODE) {
                                System.out.println(eq.getNodeName());
                                writer.write("$$" + GlobalCase(eq) + "$$\n"); //Вызываем функцию для обработки факта
                            }
                        }
                    }
                }
            }
            writer.write("\\end{document}\n");
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
    }
}
