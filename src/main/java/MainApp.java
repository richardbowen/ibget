import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class MainApp {

    // Yes this is hack that will break one day
    static private String getTagValueFromXML(String xml, String tag){
        int loc = xml.indexOf(tag);
        int eloc = xml.indexOf(tag.replace("<","</"));
        return xml.substring(loc+tag.length(),eloc);
    }


    public static void main(String[] args){


        if (args.length != 3 ){
            System.out.println("ERROR: Three cmd line parameters required:");
            System.out.println("Token QueryId, outdir");

            System.exit(-1);
        }


        String outdir = args[2];

        String tok = args[0];
        int positionQueryId =  Integer.parseInt(args[1]);  //217489;

        String createurl ="https://gdcdyn.interactivebrokers.com/Universal/servlet/FlexStatementService.SendRequest?t=TOKEN&q=QUERY_ID&v=3";
        createurl = createurl.replace("TOKEN", tok);
        createurl = createurl.replace("QUERY_ID", Integer.toString(positionQueryId));

        try {
            Response response = Request.Get(createurl).execute();

//            HttpResponse hr = response.returnResponse();
            String createBody = response.returnContent().asString();


            System.out.println(createBody);


            String status = getTagValueFromXML(createBody,"<Status>");
            String ref = getTagValueFromXML(createBody,"<ReferenceCode>");

            if (!status.equals("Success"))
                throw new Exception("Couldn't create report, giving up. Status =" + status);

            //String referenceCode = "9146826535";
            String retrieveUrl ="https://gdcdyn.interactivebrokers.com/Universal/servlet/FlexStatement" +
                    "Service.GetStatement?q=REFERENCE_CODE&t=TOKEN&v=3";
            retrieveUrl = retrieveUrl.replace("TOKEN", tok);
            retrieveUrl = retrieveUrl.replace("REFERENCE_CODE", ref);

            for(int i=0;i<120;i++) {
                Thread.sleep(1000);

                response = Request.Get(retrieveUrl).execute();
                String body = response.returnContent().asString();

                if (body.contains("<ErrorCode>")){
                    System.out.println("waiting");
                    continue;
                }

                String filename = "ibextract." + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv";
                String joinedPath = new File(outdir, filename).toString();
                PrintWriter out = new PrintWriter(joinedPath);
                out.write(body);
                out.close();

                System.out.println(body);
                System.out.println("-------------------------------------------------");
                return;
            }

        }
        catch(Exception ex) {
            System.out.println(ex.getMessage());
            System.exit(-1);
        }

    }

}
