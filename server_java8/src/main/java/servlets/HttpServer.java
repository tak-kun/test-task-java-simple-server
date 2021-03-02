package servlets;

import com.google.gson.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
//import groovy.json.JsonSlurper;

class JsonServer {
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 8080;
    private static final int BACKLOG = 1;

    private static final String HEADER_ALLOW = "Allow";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final int STATUS_OK = 200;
    private static final int STATUS_LOGIN_NOT_ALLOWED = 404;
    private static final int STATUS_METHOD_ERROR = 400;

    private static final int NO_RESPONSE_LENGTH = -1;

    private static final String METHOD_GET = "GET";
    private static final String METHOD_OPTIONS = "OPTIONS";

    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_POST = "POST";

    private static final String DEFAULT_LINE = "\"{'hello world!'}\";";

    private static final String ALLOWED_METHODS = METHOD_GET + "," + METHOD_OPTIONS;
    private static Object InputStream;

    // TEST //
    public static class SAVER {
        public static String stroke ="Null";
    }
    // TEST //

    public static void main(final String... args) throws IOException {
        final HttpServer server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), BACKLOG);


        final boolean[] isThisJSONError_test = {true};
        final boolean[] isAllLoginsisStringsError_test = {false};
        final boolean[] isAllLoginsNormalSizeError_test = {false};
        final boolean[] isArraysNormalSizeError_test = {false};
        final boolean[] isInArraysDublicateError_test = {false};


        server.createContext("/logins", he -> {

            try {
                final Headers headers = he.getResponseHeaders();
                final String requestMethod = he.getRequestMethod().toUpperCase();
                //System.out.print(requestMethod);
                final InputStream body = he.getRequestBody();
                Scanner reader = new Scanner(body).useDelimiter("\r\n");;
                final String line = reader.next();

                isThisJSONError_test[0] = true;
                isAllLoginsisStringsError_test[0] = false;
                isAllLoginsNormalSizeError_test[0] = false;
                isArraysNormalSizeError_test[0] = false;
                isInArraysDublicateError_test[0] = false;

                // проверим глобальную строку для сохранения строк успешно полученных массивов:
                System.out.println("SAVED STRING are: "+ SAVER.stroke);


                // теперь распарсим строку с массивом json-ов в массив собственно json-ов

                try {
                    JsonArray myJsonObjectsArray = JsonParser.parseString(line).getAsJsonArray();
                    isThisJSONError_test[0] = false;
                } finally {
                    System.out.println("indeed..");
                }

                JsonArray myJsonObjectsArray = JsonParser.parseString(line).getAsJsonArray();
                System.out.println(myJsonObjectsArray.getClass());

//                isThisJSONError_test[0] = false;

                // ПРОВЕРЯЕМ КОЛ-ВО ЭЛЕМЕНТОВ
                if (myJsonObjectsArray.size() > 1000) {
                    isArraysNormalSizeError_test[0] = true;
                }
                // make for - for check String type:
                for (int i = 0; i < myJsonObjectsArray.size(); i++) {
                    JsonObject currentJson = (JsonObject) myJsonObjectsArray.get(i);
                    // тут нужно првоерить является ли логин строкой:
                    String currentLogin_string = currentJson.get("login").getAsString(); // получить поле "message" как строку
                    JsonPrimitive currentLogin = currentJson.getAsJsonPrimitive("login"); // получить поле "message" как строку
                    //System.out.println("current login is " + currentLogin.getClass());
                    // CHECK CURRENT LOGIN:
                    if (!currentLogin.isString()) {
                        isAllLoginsisStringsError_test[0] = true;
                    }
                    if (currentLogin_string.length() > 20) {
                        isAllLoginsNormalSizeError_test[0] = true;
                    }
                    System.out.println(currentLogin); //System.out.println("current login is string? " + currentLogin.isString());
                }

                // CHECK FOR DUBLICATES:
                Gson googleJson = new Gson();
                ArrayList jsonObjList = googleJson.fromJson(myJsonObjectsArray, ArrayList.class); // СПИСОК Жсонов
                //System.out.println("List Elements are  : "+jsonObjList.toString());
                HashSet hashSet = new HashSet(jsonObjList);
                ArrayList arrayList2 = new ArrayList(hashSet) ;
                if(arrayList2.size()<jsonObjList.size()){
                    System.out.println("Duplicate!");
                    isInArraysDublicateError_test[0] = true;
                }

                // MY SWTICH VARIANT:
                switch (requestMethod) {
//                    case METHOD_GET:
//                        break;
//                    case METHOD_OPTIONS:
//                        break;
                    case METHOD_PUT:
                        // CHECKS BLOCK
                        boolean checkError_1 = isThisJSONError_test[0];
                        boolean checkError_2 = isAllLoginsisStringsError_test[0];
                        boolean checkError_3 = isAllLoginsNormalSizeError_test[0];
                        boolean checkError_4 = isArraysNormalSizeError_test[0];
                        boolean checkError_5 = isInArraysDublicateError_test[0];
                        System.out.println("checkError_1: " + checkError_1);
                        System.out.println("checkError_2: " + checkError_2);
                        System.out.println("checkError_3: " + checkError_3);
                        System.out.println("checkError_4: " + checkError_4);
                        System.out.println("checkError_5: " + checkError_5);
                        if (checkError_1==true|checkError_2==true|checkError_3==true|checkError_4==true|checkError_5==true ) {
                            he.sendResponseHeaders(STATUS_METHOD_ERROR, NO_RESPONSE_LENGTH);
                        } else {
                            he.sendResponseHeaders(STATUS_OK, NO_RESPONSE_LENGTH);

                            //lets save array to special place:
//                            SAVER.stroke = "I was here";
                            SAVER.stroke = jsonObjList.toString();
                        }
                        // CHECKS BLOCK
                        break;
                    case METHOD_POST:
                        System.out.println("POST");
                        String weGot = jsonObjList.toString();
                        String weHave = SAVER.stroke;
                        System.out.println("weGot: "+weGot);
                        System.out.println("weHave: "+weHave);
                        if (weGot.equals(weHave)) {
                            System.out.println("ОДИНАКОВЫЕ");
                            he.sendResponseHeaders(STATUS_OK, NO_RESPONSE_LENGTH);

                        } else {
                            System.out.println("РАЗНЫЕ");
                            he.sendResponseHeaders(STATUS_LOGIN_NOT_ALLOWED, NO_RESPONSE_LENGTH);
                        }
                        break;
                    default:
                        break;
                } // switch
            } catch (IOException e) {
                System.out.println(e.getMessage());

            } finally {
                boolean checkError_1 = isThisJSONError_test[0];
                boolean checkError_2 = isAllLoginsisStringsError_test[0];
                boolean checkError_3 = isAllLoginsNormalSizeError_test[0];
                boolean checkError_4 = isArraysNormalSizeError_test[0];
                boolean checkError_5 = isInArraysDublicateError_test[0];
                System.out.println("checkError_1: " + checkError_1);
                System.out.println("checkError_2: " + checkError_2);
                System.out.println("checkError_3: " + checkError_3);
                System.out.println("checkError_4: " + checkError_4);
                System.out.println("checkError_5: " + checkError_5);
                if (checkError_1==true|checkError_2==true|checkError_3==true|checkError_4==true|checkError_5==true ) {
                    he.sendResponseHeaders(STATUS_METHOD_ERROR, NO_RESPONSE_LENGTH);
                } else {
                    he.sendResponseHeaders(STATUS_OK, NO_RESPONSE_LENGTH);
                }
                he.close();
            } // try-catch-finally
        }); // server.createContext
        server.start();
    }

    private static Map<String, List<String>> getRequestParameters(final URI requestUri) {
        final Map<String, List<String>> requestParameters = new LinkedHashMap<>();
        final String requestQuery = requestUri.getRawQuery();
        if (requestQuery != null) {
            final String[] rawRequestParameters = requestQuery.split("[&;]", -1);
            for (final String rawRequestParameter : rawRequestParameters) {
                final String[] requestParameter = rawRequestParameter.split("=", 2);
                final String requestParameterName = decodeUrlComponent(requestParameter[0]);
                requestParameters.putIfAbsent(requestParameterName, new ArrayList<>());
                final String requestParameterValue = requestParameter.length > 1 ? decodeUrlComponent(requestParameter[1]) : null;
                requestParameters.get(requestParameterName).add(requestParameterValue);
            }
        }
        return requestParameters;
    }

    private static String decodeUrlComponent(final String urlComponent) {
        try {
            return URLDecoder.decode(urlComponent, CHARSET.name());
        } catch (final UnsupportedEncodingException ex) {
            throw new InternalError(ex);
        }
    }
}
