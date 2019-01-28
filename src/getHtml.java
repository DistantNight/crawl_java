import java.io.File;
import java.io.FileWriter;
import java.lang.String;
import java.lang.InterruptedException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class getHtml {
    private static int start_index = 1;
    private static int end_index = 106;
    private static String file_path = "F:\\ideaProject\\SearchWeb\\data\\"; // the path to store the document
    public static void main(String[] args)
    {
        String url = "https://dxy.com/view/i/disease/list?section_group_name=buxian&page_index=";
        String disease_url = "https://dxy.com/disease/";
        try {
            for(int temp = start_index; temp <= end_index; temp++){
                Response res = Jsoup.connect(url + Integer.toString(temp)).ignoreContentType(true).execute();
                String content_body = res.body();
                JSONObject json_string = JSONObject.fromObject(content_body);
                System.out.println(json_string.toString());
                if(json_string.has("data")){
                    JSONObject data_string = JSONObject.fromObject(json_string.getString("data"));
                    if(data_string.has("items")){
                        JSONArray item_lists = data_string.getJSONArray("items");
                        for(int i = 0; i < item_lists.size(); i++){
                            JSONObject json_temp = JSONObject.fromObject(item_lists.getString(i));
                            if(json_temp.has("id")){
                                try {
                                    TimeUnit.MILLISECONDS.sleep(2000);
                                }catch(InterruptedException e){
                                    e.printStackTrace();
                                }
                                String disease_id = json_temp.getString("id");
                                Document doc = Jsoup.connect(disease_url + disease_id).get();
                                Elements disease_list = doc.getElementsByClass("disease-list");
                                Document disease_doc = Jsoup.parse(disease_list.toString());
                                try {
                                    setFile(disease_doc);
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }



        return;
    }
    private static void setFile(Document disease_doc) throws IOException
    {
        // use DiseaseName.txt to name the document to store the info got from web https://dxy.com/disease/id
        String file_name = disease_doc.getElementsByClass("disease-card-info-title").text();
        System.out.println(file_name);
        // create the file to store the info
        File store_file = new File(file_path + file_name);
        if(!store_file.exists()){
            store_file.createNewFile();
        }else{
            store_file.delete();
            store_file.createNewFile();
        }
        // to append the file
        FileWriter fw = new FileWriter(store_file, true);
        fw.write(disease_doc.getElementsByClass("disease-card-info-title").text()); // append the title to file
        fw.write(System.getProperty("line.separator"));
        fw.write(disease_doc.getElementsByClass("disease-card-info-content").text()); // append the content to file
        fw.write(System.getProperty("line.separator"));
        // use the element to traversal all the filed of disease
        Elements disease_detail_card = disease_doc.getElementsByClass("disease-detail-card");
        for(Element item : disease_detail_card){
            fw.write(item.text());
        }
        fw.close();
        return;
    }
}
